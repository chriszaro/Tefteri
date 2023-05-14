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
    TextView idView;
    EditText nameBox;
    EditText costBox;
    EditText dateBox;

    KeyListener defaultKeyListenerForNameBox;

    KeyListener defaultKeyListenerForCostBox;

    KeyListener defaultKeyListenerForDateBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipt);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //Get references to view objects
        idView = findViewById(R.id.receiptID);
        nameBox = findViewById(R.id.companyName);
        defaultKeyListenerForNameBox = nameBox.getKeyListener();
        nameBox.setKeyListener(null);
        costBox = findViewById(R.id.receiptCost);
        defaultKeyListenerForCostBox = costBox.getKeyListener();
        costBox.setKeyListener(null);
        dateBox = findViewById(R.id.receiptDate);
        defaultKeyListenerForDateBox = dateBox.getKeyListener();
        dateBox.setKeyListener(null);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        boolean editBoolean = intent.getBooleanExtra("editBoolean", false);

        // Use custom settings as needed
        if (editBoolean) {
            // Do something based on customSetting1 being true
            editReceipt(findViewById(android.R.id.content).getRootView());
        }
    }

    public void editReceipt(View view){
        nameBox.setKeyListener(defaultKeyListenerForNameBox);
        costBox.setKeyListener(defaultKeyListenerForCostBox);
        dateBox.setKeyListener(defaultKeyListenerForDateBox);

        Button editButton = findViewById(R.id.editButton);
        editButton.setText("ΑΠΟΘΗΚΕΥΣΗ");
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something different when the button is clicked
                newReceipt(view);
            }
        });

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setText("ΑΚΥΡΩΣΗ");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something different when the button is clicked
                newReceipt(view);
            }
        });

    }


    //OnClick method for ADD button
    public void newReceipt(View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        String companyName = nameBox.getText().toString();
        String receiptCost = costBox.getText().toString();
        String receiptDate = dateBox.getText().toString();

        /*if (!companyName.equals("") && !receiptCost.equals("") && !receiptDate.equals("")) {
            Receipt found = dbHandler.findProduct(companyName);
            if (found == null) {
                Receipt receipt = new Receipt(companyName, Float.parseFloat(receiptCost), receiptDate);
                dbHandler.addProduct(receipt);
                nameBox.setText("");
                costBox.setText("");
                dateBox.setText("");
            }
        }*/
        nameBox.setText("");
        costBox.setText("");
        dateBox.setText("");

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }
        //company closed test
        //String input = "https://www1.aade.gr/tameiakes/myweb/q1.php?SIG=DCM1600619200252556F2BD37B9CF29CEB8695F8179590360D578695A091.20";
        //Verified
        //String input = "https://www1.aade.gr/tameiakes/myweb/q1.php?SIG=CFA1800124300033535EBAA152B0CB943EDB30CF9135B5F8074ECB86E5622.99";
        //Not Verified
        //String input = "https://www1.aade.gr/tameiakes/myweb/q1.php?SIG=DCR1801381500016311AB2BFDBF46937691B63C936A85637F75B37C178B14.00";
        //String input = "https://einvoice.s1ecos.gr/v/EL094352564-42547400-E33B5614B29BBB8AC2F25B345F3B75B83EAB3A9A-B1A3458713FA42C881D20651BDB4CD6D";
        String input = "http://tam.gsis.gr/eafdss/myweb/q1.php?SIG=CFZ20000527005892836A762E8CAFEBEB510BCEF5692959BFD2F51E702200008.42";
        //not supported type
        //String input = "https://www.iview.gr/181951675073530293";
        if (input.contains("http://tam.gsis.gr")){
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
                            "Ημερομηνία, ώρα".length()+1,
                            "Ημερομηνία, ώρα".length()+11
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

                nameBox.setText(companyName);
                costBox.setText(receiptCost);
                dateBox.setText(receiptDate);
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
                        "Ημερομηνία Έκδοσης".length()+1,
                        "Ημερομηνία Έκδοσης".length()+11
                );
                receiptDate = receiptDate.replace("/","-");
                dateBox.setText(receiptDate);

                receiptCost = receiptScreen.findWord(
                        info,
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)",
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)",
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)".length()+1,
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)".length()+1+12
                );
                receiptCost = receiptCost.trim().replace(",", ".");
                String[] xd = receiptCost.split("\\.");
                receiptCost = xd[0] + "." + xd[1].charAt(0) + xd[1].charAt(1);

                costBox.setText(receiptCost);

                companyName = receiptScreen.findWord(
                        info,
                        "Εκδότης Επωνυμία επιχείρησης",
                        "Οδός",
                        "Εκδότης Επωνυμία επιχείρησης".length()+1,
                        -1
                );
                nameBox.setText(companyName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (input.contains("https://www.iview.gr")) {
            Toast.makeText(this, "Δεν υποστηρίζεται αυτός ο τύπος απόδειξης", Toast.LENGTH_SHORT).show();
        }

        dbHandler.addProduct(new Receipt(nameBox.getText().toString(), Float.parseFloat(costBox.getText().toString()), dateBox.getText().toString()));
    }

    public static String findWord(String paragraph, String startString, String endString, int start, int end) {
        int startDate = paragraph.indexOf(startString) + start;
        int endDate = paragraph.indexOf(endString) + end;
        return paragraph.substring(startDate, endDate);
    }

//    //OnClick method for FIND button
//    public void lookupReceipt (View view) {
//        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
//        Product product = dbHandler.findProduct(productBox.getText().toString());
//        if (product != null) {
//            idView.setText(String.valueOf(product.getID()));
//            quantityBox.setText(String.valueOf(product.getQuantity()));
//        } else {
//            idView.setText(getString(R.string.no_match_found));
//            quantityBox.setText("");
//        }
//    }

//    //OnClick method for DELETE button
//    public void removeReceipt (View view) {
//        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
//        boolean result = dbHandler.deleteProduct(productBox.getText().toString());
//        if (result)
//        {
//            idView.setText(getString(R.string.record_deleted));
//            productBox.setText("");
//            quantityBox.setText("");
//        }
//        else {
//            idView.setText(getString(R.string.no_match_found));
//            quantityBox.setText("");
//        }
//    }
}