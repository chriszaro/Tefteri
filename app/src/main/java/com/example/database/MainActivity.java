package com.example.database;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, CameraScanCallback {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    Menu activityBarMenu;
    MyDBHandler dbHandler;

    CameraScanner camera;

    ReceiptDownloader downloader;

    Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = this;
        dbHandler = new MyDBHandler(mainContext, null, null, 1);
        //dbHandler.loadDataFromFile("brands.sql");

        recyclerView = findViewById(R.id.recycler_view);

        //Set the layout of the items in the RecyclerView
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // as per the android documentation, the database should remain open for as long as possible

        camera = new CameraScanner(this);

        downloader = new ReceiptDownloader(dbHandler, this);

        dbHandler.table();
        //dbHandler.loadDataFromFile("brands.sql");

        // Massively add data to database for testing purposes
        boolean loadManyReceipts = false;
        String manyReceiptsSQLInsertsFileName = "receiptsDB_db-receipts.sql"; // located in /src/main/assets
        if (loadManyReceipts) {
            dbHandler.loadDataFromFile(manyReceiptsSQLInsertsFileName);
            //Log.d("MainActivityReceiptsLoa", "Loaded many receipts from MainActivity");
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
    }

    /**
     * This method resets the state of MainActivity after any action that changes the database
     */
    public void refreshAdapter() {
        adapter = new RecyclerAdapter(this, false, "", "");
        recyclerView.setAdapter(adapter);
        invalidateOptionsMenu();
    }

    /**
     * This method is for the menu of the action bar, it includes multiple deletion and about page
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // here goes options menu with question mark
        getMenuInflater().inflate(R.menu.main_menu, menu);
        activityBarMenu = menu;

        //Multiple deletion button
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

        //About page button
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
     */
    public Menu getActivityBarMenu() {
        return activityBarMenu;
    }

    /**
     * Method for creating popup menu for the add float button
     */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    /**
     * Click handler for popup menu from add float button
     *
     * @param item the menu item that was clicked
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (isNetworkAvailable()) {
            switch (item.getItemId()) {
                case R.id.add_option:
                    camera.scanCode(false);
                    return true;
                case R.id.multiple_add_option:
                    camera.setMultiScanMode(true);
                    camera.scanCode(false);
                    return true;
                case R.id.add_and_edit_option:
                    camera.scanCode(true);
                    return true;
                case R.id.manual_add_option:
                    manualAddition();
                    return true;
                default:
                    return false;
            }
        } else {
            if (item.getItemId() == R.id.manual_add_option) {
                manualAddition();
                return true;
            }// Network is not available
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Network check function
     *
     * @return true/false whether a Network if any network is available or not.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    String lastReceiptID;

    /**
     * Code to be executed after the camera scan is done
     * Synchronizes the execution
     */
    @Override
    public void onScanComplete() {

        //Create the Intent to start the AddProductScreen Activity
        Intent i = new Intent(this, ReceiptScreen.class);

        //Pass data to the AddProductScreen Activity through the Intent
        i.putExtra("editBoolean", true);
        i.putExtra("newReceipt", true);
        i.putExtra("id", lastReceiptID);

        //Ask Android to start the new Activity
        startActivity(i);
    }

    /**
     * Code to be executed after the camera scan is done
     */
    public void manualAddition() {

        //Create the Intent to start the AddProductScreen Activity
        Intent i = new Intent(this, ReceiptScreen.class);

        //Pass data to the AddProductScreen Activity through the Intent
        i.putExtra("editBoolean", true);
        i.putExtra("newReceipt", true);

        //Ask Android to start the new Activity
        startActivity(i);
    }

    // ActivityResultLauncher for handling normal and multiple scans.
    ActivityResultLauncher<ScanOptions> barLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                // Get scan result.
                String input = result.getContents();

                // Check if result is not null.
                if (input != null) {
                    // Download the receipt using the scanned input.
                    String temp = downloader.downloadReceipt(input);

                    // If the download was successful, save the last receipt ID.
                    if (temp != null) {
                        lastReceiptID = temp;
                    }

                    // Check if the camera is in multi-scan mode.
                    if (camera.getMultiScanMode()) {
                        // Initialize the Timer outside of the AlertDialog so it can be accessed in button callbacks.
                        final Timer t = new Timer();

                        // Create a dialog to ask the user if they want to continue scanning.
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle("Συνέχεια;") // "Continue?" in Greek
                                .setMessage("Θέλετε να ξανασκανάρετε;") // "Do you want to scan again?" in Greek
                                .setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // If user says "Yes", continue scanning.
                                        camera.scanCode(false);
                                    }
                                })
                                .setNegativeButton("Όχι", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // If user says "No", stop scanning.
                                        camera.setMultiScanMode(false);
                                        // Cancel the Timer when "No" is clicked to prevent scanner from reopening.
                                        t.cancel();
                                    }
                                })
                                .show();
                    }
                }
            });


    // ActivityResultLauncher for handling scan and edit mode.
    ActivityResultLauncher<ScanOptions> barLauncher2 =
            registerForActivityResult(
                    new ScanContract(), result -> {
                        // Get scan result.
                        String input = result.getContents();

                        // Check if result is not null.
                        if (input != null) {
                            // Download the receipt using the scanned input.
                            String temp = downloader.downloadReceipt(input);
                            if (temp != null) {
                                // Save last receipt ID if download was successful.
                                lastReceiptID = temp;

                                // Invoke the onScanComplete callback indicating that the scan is complete.
                                CameraScanCallback callback = (CameraScanCallback) mainContext;
                                callback.onScanComplete();
                            }
                        }
                    }
            );
}

