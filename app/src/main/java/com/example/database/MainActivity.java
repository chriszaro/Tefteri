package com.example.database;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder> adapter;
    static Menu activityBarMenu;

    Context mainContext;

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
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);

        //Code For Ads
        MobileAds.initialize(this, initializationStatus -> {});
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        activityBarMenu = menu;
        MenuItem myMenuItem = menu.findItem(R.id.action_delete);
        myMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                MyDBHandler dbHandler = new MyDBHandler(mainContext, null, null, 1);
                Toast.makeText(mainContext, "delete", Toast.LENGTH_SHORT).show();
                dbHandler.deleteReceipts(RecyclerAdapter.findIDsOfItemsForDeletion());
                return false;
            }
        });
        myMenuItem.setVisible(false);
        return true;
    }

    public static Menu getActivityBarMenu() {
        return activityBarMenu;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

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
                return true;
            case R.id.add_and_edit_option:
                viewReceiptScreen(findViewById(android.R.id.content).getRootView());
                return true;
            default:
                return false;
        }
    }

    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRScannerActivity.class);
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");

            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });



    public void viewReceiptScreen(View view) {
        //Create the Intent to start the AddProductScreen Activity
        Intent i = new Intent(this, ViewReceiptScreen.class);

        //Pass data to the AddProductScreen Activity through the Intent

        //Ask Android to start the new Activity
        startActivity(i);
    }
}