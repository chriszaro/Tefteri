package com.example.database;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class receiptScreen extends AppCompatActivity {
    EditText nameBox;
    EditText costBox;
    EditText dateBox;

    KeyListener defaultKeyListenerForNameBox;

    KeyListener defaultKeyListenerForCostBox;

    KeyListener defaultKeyListenerForDateBox;

    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipt);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        dbHandler = new MyDBHandler(findViewById(android.R.id.content).getRootView().getContext(), null, null, 1);

        //Get references to view objects
        nameBox = findViewById(R.id.companyName);
        defaultKeyListenerForNameBox = nameBox.getKeyListener();
        nameBox.setKeyListener(null);

        costBox = findViewById(R.id.receiptCost);
        defaultKeyListenerForCostBox = costBox.getKeyListener();
        costBox.setKeyListener(null);

        dateBox = findViewById(R.id.receiptDate);
        defaultKeyListenerForDateBox = dateBox.getKeyListener();
        dateBox.setKeyListener(null);

        //Code for ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Get from intent if the receiptScreen is for view or edit
        Intent intent = getIntent();
        boolean editBoolean = intent.getBooleanExtra("editBoolean", false);
        boolean newReceipt = intent.getBooleanExtra("newReceipt", false);

        String id = intent.getStringExtra("id");
        setValues(id);

        //if true then edit, else just view
        if (editBoolean) {
            editReceipt(id, newReceipt);
        } else {
            viewReceipt(id);
        }
    }

    /**
     * This method sets the receiptScreen to view mode.
     *
     * @param id
     */
    public void viewReceipt(String id) {
        //Set name and function to left button
        Button leftButton = findViewById(R.id.leftButton);
        leftButton.setText("ΕΠΕΞΕΡΓΑΣΙΑ");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editReceipt(id, false);
            }
        });

        //Set name and function to right button
        Button rightButton = findViewById(R.id.rightButton);
        rightButton.setText("ΔΙΑΓΡΑΦΗ");
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.deleteReceipt(id);
                endActivity();
            }
        });
    }

    /**
     * This method sets the receiptScreen to edit mode.
     *
     * @param id
     */
    public void editReceipt(String id, Boolean newReceipt) {
        resetFieldsListeners();

        //Set name and function to left button
        Button leftButton = findViewById(R.id.leftButton);
        leftButton.setText("ΑΠΟΘΗΚΕΥΣΗ");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endActivity();
            }
        });

        //Set name and function to right button
        Button rightButton = findViewById(R.id.rightButton);
        rightButton.setText("ΑΚΥΡΩΣΗ");
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newReceipt) {
                    dbHandler.deleteReceipt(id);
                }
                endActivity();
            }
        });
    }

    public void endActivity() {
        this.finish();
    }

    public static String findWord(String paragraph, String startString, String endString, int start, int end) {
        int startDate = paragraph.indexOf(startString) + start;
        int endDate = paragraph.indexOf(endString) + end;
        return paragraph.substring(startDate, endDate);
    }

    /**
     * This method sets values to the fields
     * @param id
     */
    public void setValues(String id) {
        //search tin apodeiksi me to id
        Receipt receipt = dbHandler.findProduct(id);

        //set values sta boxes
        costBox.setText(String.valueOf(receipt.get_cost()));
        nameBox.setText(String.valueOf(receipt.get_companyName()));
        dateBox.setText(String.valueOf(receipt.get_date()));
    }

    /**
     * This method resets the default key listeners for the text fields
     */
    public void resetFieldsListeners(){
        nameBox.setKeyListener(defaultKeyListenerForNameBox);
        costBox.setKeyListener(defaultKeyListenerForCostBox);
        dateBox.setKeyListener(defaultKeyListenerForDateBox);
    }
}