package com.example.database;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
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

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    Menu activityBarMenu;

    private Context mainContext;
    private MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        //Set the layout of the items in the RecyclerView
        layoutManager = new LinearLayoutManager(this);
        mainContext = this;
        recyclerView.setLayoutManager(layoutManager);

        //Set my Adapter for the RecyclerView
        adapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(adapter);

        // as per the android documentation, the database should remain open for as long as possible
//        dbHandler = new MyDBHandler(this, null, null, 1);

        // Code For Ads
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /**
     * This method is for the trash button on activity bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        activityBarMenu = menu;
        MenuItem myMenuItem = menu.findItem(R.id.action_delete);
        MainActivity ma = this;
        myMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                MyDBHandler dbHandler = new MyDBHandler(mainContext, null, null, 1);
                Toast.makeText(mainContext, "delete", Toast.LENGTH_SHORT).show();
                dbHandler.deleteReceipt("1");
                return false;
            }
        });
        myMenuItem.setVisible(false);
        return true;
    }

    /**
     * Getter for ActivityBarMenu
     * @return
     */
    public Menu getActivityBarMenu() {
        return activityBarMenu;
    }

    /**
     * Method for popup menu from float button (add)
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
     * @param item the menu item that was clicked
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_option:
                Toast.makeText(this, "add clicked", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(mainContext, QRScannerActivity.class);
//                startActivity(intent);
                scanCode();
                return true;
            case R.id.multiple_add_option:
                Toast.makeText(this, "multiple_add_option clicked", Toast.LENGTH_SHORT).show();
                /*while (true){
                    scanCode();
                    if (scanCode() == null){
                        return true;
                    }
                }*/
            case R.id.add_and_edit_option:
                scanCode();
                editReceipt(recyclerView);
                return true;
            default:
                return false;
        }
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRScannerActivity.class);
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setBeepEnabled(false);
        barLauncher.launch(options);
    }

    String lastReceiptID;
    ActivityResultLauncher<ScanOptions> barLauncher =
            registerForActivityResult(
                    new ScanContract(), result -> {
                        String input = result.getContents();
                        lastReceiptID = downloadReceipt(input);
                    }
            );

    public String downloadReceipt(String input) {

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
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
        }
        if (input.contains("https://www1.aade.gr") || input.contains("https://www1.gsis.gr")) {
            try {
                Document doc = Jsoup.connect(input).get();
                String info = doc.getElementsByClass("info").text();
                String receipt = doc.getElementsByClass("receipt").text();

                //Verified
                if (!doc.getElementsByClass("success").text().isEmpty()) {
                    receiptDate = receiptScreen.findWord(
                            info,
                            "Ημερομηνία, ώρα",
                            "Ημερομηνία, ώρα",
                            "Ημερομηνία, ώρα".length() + 1,
                            "Ημερομηνία, ώρα".length() + 11
                    );

                    // 2022-04-15 to 15-04-2022
                    String formattedDate = "";
                    formattedDate = formattedDate + receiptDate.charAt(8);
                    formattedDate = formattedDate + receiptDate.charAt(9);
                    formattedDate = formattedDate + receiptDate.charAt(7);
                    formattedDate = formattedDate + receiptDate.charAt(5);
                    formattedDate = formattedDate + receiptDate.charAt(6);
                    formattedDate = formattedDate + receiptDate.charAt(4);
                    formattedDate = formattedDate + receiptDate.charAt(0);
                    formattedDate = formattedDate + receiptDate.charAt(1);
                    formattedDate = formattedDate + receiptDate.charAt(2);
                    formattedDate = formattedDate + receiptDate.charAt(3);
                    receiptDate = formattedDate;

                    receiptCost = receiptScreen.findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );
                    //receiptCost = receiptCost.replace(".", ",");

                    companyName = receiptScreen.findWord(
                            info,
                            "Επωνυμία",
                            "Διεύθυνση",
                            9,
                            -1
                    );

                } else if (!doc.getElementsByClass("box-error").text().isEmpty()) {
                    //closed company

                    receiptDate = "Unknown";

                    receiptCost = receiptScreen.findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );

                    companyName = "Unknown";

                } else if (!doc.getElementsByClass("box-warning").text().isEmpty()) {
                    //Not Verified
                    receiptDate = receiptScreen.findWord(
                            info,
                            "Διεύθυνση όπου λειτουργεί ο ΦΗΜ σήμερα ",
                            "Διεύθυνση όπου λειτουργεί ο ΦΗΜ σήμερα ",
                            39,
                            49
                    );

                    receiptCost = receiptScreen.findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );

                    companyName = receiptScreen.findWord(
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
            //einvoice
            try {
                Document doc = Jsoup.connect(input).get();
                String info = doc.getElementsByTag("body").text();

                receiptDate = receiptScreen.findWord(
                        info,
                        "Ημερομηνία Έκδοσης",
                        "Ημερομηνία Έκδοσης",
                        "Ημερομηνία Έκδοσης".length() + 1,
                        "Ημερομηνία Έκδοσης".length() + 11
                );
                receiptDate = receiptDate.replace("/", "-");

                receiptCost = receiptScreen.findWord(
                        info,
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)",
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)",
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)".length() + 1,
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)".length() + 1 + 12
                );
                receiptCost = receiptCost.trim().replace(",", ".");
                String[] xd = receiptCost.split("\\.");
                receiptCost = xd[0] + "." + xd[1].charAt(0) + xd[1].charAt(1);

                companyName = receiptScreen.findWord(
                        info,
                        "Εκδότης Επωνυμία επιχείρησης",
                        "Οδός",
                        "Εκδότης Επωνυμία επιχείρησης".length() + 1,
                        -1
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (input.contains("https://www.iview.gr")) {
            Toast.makeText(this, "Δεν υποστηρίζεται αυτός ο τύπος απόδειξης", Toast.LENGTH_SHORT).show();
        }
        Receipt newReceipt = new Receipt(companyName, Float.parseFloat(receiptCost), receiptDate);
        dbHandler.addProduct(newReceipt);
        return String.valueOf(newReceipt.get_ID());
    }

    public void editReceipt(View view) {
        //Create the Intent to start the AddProductScreen Activity
        Intent i = new Intent(this, receiptScreen.class);

        //Pass data to the AddProductScreen Activity through the Intent
        i.putExtra("editBoolean", true);
        i.putExtra("newReceipt", true);
        i.putExtra("id", lastReceiptID);

        //Ask Android to start the new Activity
        startActivity(i);
    }
}