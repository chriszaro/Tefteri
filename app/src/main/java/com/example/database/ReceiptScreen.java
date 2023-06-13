package com.example.database;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class ReceiptScreen extends AppCompatActivity {
    EditText nameBox;
    EditText costBox;
    EditText dateBox;

    KeyListener defaultKeyListenerForNameBox;
    KeyListener defaultKeyListenerForCostBox;
    KeyListener defaultKeyListenerForDateBox;

    MyDBHandler dbHandler;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipt);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        context = this;
        dbHandler = new MyDBHandler(findViewById(android.R.id.content).getRootView().getContext(), null, null, 1);

        nullifyTextAreaListeners();

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

        if (intent.getStringExtra("id") != null) {
            String id = intent.getStringExtra("id");
            setValues(id);

            //if true then edit, else just view
            if (editBoolean) {
                editReceipt(id, newReceipt);
            } else {
                viewReceipt(id);
            }
        }
        else {
            editReceipt(null, newReceipt);
        }
    }

    /**
     * This method sets the receiptScreen to view mode.
     *
     * @param id of the receipt
     */
    public void viewReceipt(String id) {
        //Set name and function to left button
        Button leftButton = findViewById(R.id.leftButton);
        leftButton.setText(R.string.Edit_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editReceipt(id, false);
            }
        });

        //Set name and function to right button
        Button rightButton = findViewById(R.id.rightButton);
        rightButton.setText(R.string.Delete_button);
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
     * @param id         of the receipt
     * @param newReceipt true if new receipt, so delete if cancel
     */
    public void editReceipt(String id, Boolean newReceipt) {
        resetTextAreaListeners();

        //Set name and function to left button
        Button leftButton = findViewById(R.id.leftButton);
        leftButton.setText(R.string.Save_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id == null) createReceipt();
                else updateReceipt(id);
            }
        });

        //Set name and function to right button
        Button rightButton = findViewById(R.id.rightButton);
        rightButton.setText(R.string.Cancel_button);
        rightButton.setOnClickListener(new View.OnClickListener() {
            /*
             * If this is called by Scan and Edit, then we delete
             * else we just close the activity
             */
            @Override
            public void onClick(View v) {
                if (newReceipt) {
                    dbHandler.deleteReceipt(id);
                }
                endActivity();
            }
        });
    }

    /**
     * For manual addition
     */
    private void createReceipt() {
        String date = dateBox.getText().toString();
        String cost = costBox.getText().toString();
        String name = nameBox.getText().toString();
        if (date.length() == 0 || cost.length() == 0 || name.length() == 0) {
            Toast.makeText(context, R.string.emptyValues, Toast.LENGTH_SHORT).show();
        } else {
            if (date.length() != 10) {
                Toast.makeText(context, R.string.wrong_date_format, Toast.LENGTH_SHORT).show();
            } else {
                if (date.charAt(2) == '/' || date.charAt(5) == '/') {
                    date = date.replace('/', '-');
                }
                dbHandler.addProduct(new Receipt(name, Float.parseFloat(cost), date));
                endActivity();
            }
        }
    }

    /**
     * Method for updating receipts
     *
     * @param id the receipt
     */
    public void updateReceipt(String id) {
        String date = dateBox.getText().toString();
        String cost = costBox.getText().toString();
        String name = nameBox.getText().toString();
        if (date.length() == 0 || cost.length() == 0 || name.length() == 0) {
            Toast.makeText(context, R.string.emptyValues, Toast.LENGTH_SHORT).show();
        } else {
            if (date.length() != 10) {
                Toast.makeText(context, R.string.wrong_date_format, Toast.LENGTH_SHORT).show();
            } else {
                if (date.charAt(2) == '/' || date.charAt(5) == '/') {
                    date = date.replace('/', '-');
                }
                dbHandler.updateReceipt(id, name, cost, date);
                Toast.makeText(context, R.string.updated, Toast.LENGTH_SHORT).show();
                endActivity();
            }
        }
    }

    /**
     * Close the activity
     */
    public void endActivity() {
        this.finish();
    }

    /**
     * This method sets values to the fields
     *
     * @param id receipt
     */
    public void setValues(String id) {
        //search tin apodiksi me to id
        Receipt receipt = dbHandler.findProduct(id);

        //set values sta boxes
        costBox.setText(String.valueOf(receipt.get_cost()));
        nameBox.setText(receipt.get_companyName());
        dateBox.setText(receipt.get_date());
    }

    /**
     * This method removes the listeners of the text boxes
     */
    public void nullifyTextAreaListeners() {
        nameBox = findViewById(R.id.companyName);
        defaultKeyListenerForNameBox = nameBox.getKeyListener();
        nameBox.setKeyListener(null);

        costBox = findViewById(R.id.receiptCost);
        defaultKeyListenerForCostBox = costBox.getKeyListener();
        costBox.setKeyListener(null);

        dateBox = findViewById(R.id.receiptDate);
        defaultKeyListenerForDateBox = dateBox.getKeyListener();
        dateBox.setKeyListener(null);
    }

    /**
     * This method resets the default key listeners for the text fields
     */
    public void resetTextAreaListeners() {
        nameBox.setKeyListener(defaultKeyListenerForNameBox);
        costBox.setKeyListener(defaultKeyListenerForCostBox);
        dateBox.setKeyListener(defaultKeyListenerForDateBox);
    }
}