package com.example.database;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, CameraScanCallback {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    Menu activityBarMenu;
    MyDBHandler dbHandler;
    // Add a flag indicating whether we are in multi-scan mode
    private boolean isMultiScanMode = false;

    private Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        //Set the layout of the items in the RecyclerView
        layoutManager = new LinearLayoutManager(this);
        mainContext = this;
        recyclerView.setLayoutManager(layoutManager);
        // as per the android documentation, the database should remain open for as long as possible
        dbHandler = new MyDBHandler(mainContext, null, null, 1);

        // Massively add data to database for testing purposes
        boolean loadManyReceipts = false;
        String manyReceiptsSQLInsertsFileName = "sql-queries/receiptsDB_db-receipts.sql"; // located in /src/main/assets
        if (loadManyReceipts) {
            dbHandler.loadDataFromFile(manyReceiptsSQLInsertsFileName);
            Log.d("MainActivityReceiptsLoa", "Loaded many receipts from MainActivity");
        }

        // Code For Ads
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //ClickListener for Calendar Icon
        View calendar = findViewById(R.id.nav_view).findViewById(R.id.navigation_monthly);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainContext, ViewByMonthScreen.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAdapter();
        invalidateOptionsMenu();
    }

    /**
     * This method refresh the data of main activity
     */
    public void refreshAdapter() {
        adapter = new RecyclerAdapter(this, false, "", "");
        recyclerView.setAdapter(adapter);
    }

    /**
     * This method is for the trash button on activity bar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // here goes options menu with question mark
        getMenuInflater().inflate(R.menu.main_menu, menu);
        activityBarMenu = menu;
        MenuItem trashCanItem = menu.findItem(R.id.action_delete);
        trashCanItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                ArrayList<String> ids = adapter.findIDsOfItemsForDeletion();
                for (String id : ids) {
                    dbHandler.deleteReceipt(id);
                }
                refreshAdapter();
                adapter.disableTrash();
                return false;
            }
        });

        MenuItem questionMark = menu.findItem(R.id.action_about);
        questionMark.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(mainContext, AboutActivity.class);
                startActivity(intent);
                return false;
            }
        });
        trashCanItem.setVisible(false);
        return true;
    }

    /**
     * Getter for ActivityBarMenu
     *
     * @return
     */
    public Menu getActivityBarMenu() {
        return activityBarMenu;
    }

    /**
     * Method for popup menu from float button (add)
     *
     * @param v
     */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    /**
     * Method for function of popup menu from float button (add)
     *
     * @param item the menu item that was clicked
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (isNetworkAvailable()) {
            switch (item.getItemId()) {
                case R.id.add_option:
                    scanCode(false);
                    return true;
                case R.id.multiple_add_option:
                    isMultiScanMode = true;
                    scanCode(false);
                    return true;
                case R.id.add_and_edit_option:
                    scanCode(true);
                    return true;
                default:
                    return false;
            }
        } else {
            // Network is not available
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    String lastReceiptID;

    @Override
    public void onScanComplete() {
        // Code to be executed after the camera scan is done

        //Create the Intent to start the AddProductScreen Activity
        Intent i = new Intent(this, ReceiptScreen.class);

        //Pass data to the AddProductScreen Activity through the Intent
        i.putExtra("editBoolean", true);
        i.putExtra("newReceipt", true);
        i.putExtra("id", lastReceiptID);

        //Ask Android to start the new Activity
        startActivity(i);
    }

    private void scanCode(Boolean edit) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRScannerActivity.class);
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setBeepEnabled(false);
        if (edit) {
            barLauncher2.launch(options);
        } else {
            barLauncher.launch(options);
        }
    }

    // For normal and multiple scan
    ActivityResultLauncher<ScanOptions> barLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                String input = result.getContents();
                if (input != null) {
                    String temp = downloadReceipt(input);
                    if (temp != null) {
                        lastReceiptID = temp;
                    }

                    if (isMultiScanMode) {
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle("Continue scanning?")
                                .setMessage("Do you want to continue scanning?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue scanning
                                        scanCode(false);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Stop scanning
                                        isMultiScanMode = false;
                                    }
                                })
                                .show();

                        // After 5 seconds, treat non-responses as "Yes"
                        final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                dialog.dismiss(); // close dialog
                                t.cancel(); // cancel also the Timer
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        scanCode(false); // Continue scanning
                                    }
                                });
                            }
                        }, 1); // 1 millisecond
                    }
                }
            });


    //For scan and edit
    ActivityResultLauncher<ScanOptions> barLauncher2 =
            registerForActivityResult(
                    new ScanContract(), result -> {
                        String input = result.getContents();
                        if (input != null) {
                            String temp = downloadReceipt(input);
                            if (temp != null) {
                                lastReceiptID = temp;
                                CameraScanCallback callback = (CameraScanCallback) mainContext;
                                callback.onScanComplete();
                            }
                        }
                    }
            );

    public String downloadReceipt(String input) {
        String companyName = "";
        String receiptCost = "";
        String receiptDate = "";

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (input.contains("http://tam.gsis.gr")) {
            input = "https://www1.aade.gr/tameiakes" + input.substring(25);
        } else if (input.contains("http://www1.gsis.gr")) {
            input = "https://www1.aade.gr/tameiakes" + input.substring(29);
        } else if (input.contains("https://www1.gsis.gr")) {
            input = "https://www1.aade.gr/tameiakes" + input.substring(30);
        } else if (input.contains("https://www1.aade.gr/tameiakes/myweb/q1.ph?")) {
            input = "https://www1.aade.gr/tameiakes/myweb/q1.php" + input.substring(42);
        }

        if (input.contains("https://www1.aade.gr")) {
            try {
                Document doc = Jsoup.connect(input).get();
                String info = doc.getElementsByClass("info").text();
                String receipt = doc.getElementsByClass("receipt").text();

                //Gov Verified Receipts
                if (!doc.getElementsByClass("success").text().isEmpty()) {
                    receiptDate = ReceiptScreen.findWord(
                            info,
                            "Ημερομηνία, ώρα",
                            "Ημερομηνία, ώρα",
                            "Ημερομηνία, ώρα".length() + 1,
                            "Ημερομηνία, ώρα".length() + 11
                    );
                    receiptDate = Receipt.convertDateToDDMMYYY(receiptDate);

                    receiptCost = ReceiptScreen.findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );

                    companyName = ReceiptScreen.findWord(
                            info,
                            "Επωνυμία",
                            "Διεύθυνση",
                            9,
                            -1
                    );

                } else if (!doc.getElementsByClass("box-error").text().isEmpty()) {
                    //Receipt from closed company
                    receiptDate = "Unknown";

                    receiptCost = ReceiptScreen.findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );

                    companyName = "Unknown";

                } else if (!doc.getElementsByClass("box-warning").text().isEmpty()) {
                    //Not Verified from gov receipt
                    receiptDate = ReceiptScreen.findWord(
                            info,
                            "Διεύθυνση όπου λειτουργεί ο ΦΗΜ σήμερα ",
                            "Διεύθυνση όπου λειτουργεί ο ΦΗΜ σήμερα ",
                            39,
                            49
                    );

                    receiptCost = ReceiptScreen.findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );

                    companyName = ReceiptScreen.findWord(
                            info,
                            "Επωνυμία",
                            "Διεύθυνση όπου λειτουργεί ο ΦΗΜ σήμερα ",
                            9,
                            -1
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (input.contains("https://einvoice.s1ecos.gr")) {
            //einvoice receipt type
            try {
                Document doc = Jsoup.connect(input).get();
                String info = doc.getElementsByTag("body").text();

                receiptDate = ReceiptScreen.findWord(
                        info,
                        "Ημερομηνία Έκδοσης",
                        "Ημερομηνία Έκδοσης",
                        "Ημερομηνία Έκδοσης".length() + 1,
                        "Ημερομηνία Έκδοσης".length() + 11
                );
                receiptDate = receiptDate.replace("/", "-");

                receiptCost = ReceiptScreen.findWord(
                        info,
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)",
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)",
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)".length() + 1,
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)".length() + 1 + 12
                );
                receiptCost = receiptCost.trim().replace(",", ".");
                String[] xd = receiptCost.split("\\.");
                receiptCost = xd[0] + "." + xd[1].charAt(0) + xd[1].charAt(1);

                companyName = ReceiptScreen.findWord(
                        info,
                        "Εκδότης Επωνυμία επιχείρησης",
                        "Οδός",
                        "Εκδότης Επωνυμία επιχείρησης".length() + 1,
                        -1
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (input.contains("https://einvoice-portal.s1ecos.gr/")) {
            //einvoice receipt type 2
            try {
                Document doc = Jsoup.connect(input).get();
                String info = doc.getElementsByTag("body").text();

                receiptDate = ReceiptScreen.findWord(
                        info,
                        "ΗΜΕΡΟΜΗΝΙΑ",
                        "ΗΜΕΡΟΜΗΝΙΑ",
                        "ΗΜΕΡΟΜΗΝΙΑ".length() + 1,
                        "ΗΜΕΡΟΜΗΝΙΑ".length() + 11
                );
                receiptDate = receiptDate.replace("/", "-");

                receiptCost = ReceiptScreen.findWord(
                        info,
                        "ΣΥΝΟΛΙΚΗ ΑΞΙΑ",
                        "Μ.Αρ.Κ.",
                        "ΣΥΝΟΛΙΚΗ ΑΞΙΑ".length() + 1,
                        -1
                );
                receiptCost = receiptCost.trim().replace(",", ".");

                companyName = ReceiptScreen.findWord(
                        info,
                        "Επωνυμία επιχείρησης",
                        "ΤΟΠΟΣ ΑΠΟΣΤΟΛΗΣ",
                        "Επωνυμία επιχείρησης".length() + 1,
                        -1
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Δεν υποστηρίζεται αυτός ο τύπος απόδειξης", Toast.LENGTH_SHORT).show();
            return null;
        }
        Receipt newReceipt = new Receipt(companyName, Float.parseFloat(receiptCost), receiptDate);
        dbHandler.addProduct(newReceipt);
        return String.valueOf(newReceipt.get_ID());
    }
}

