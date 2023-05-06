package com.example.database;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ViewReceiptScreen extends AppCompatActivity {
    TextView idView;
    EditText nameBox;
    EditText costBox;
    EditText dateBox;

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //Get references to view objects
        idView = findViewById(R.id.receiptID);
        nameBox = findViewById(R.id.companyName);
        costBox = findViewById(R.id.receiptCost);
        dateBox = findViewById(R.id.receiptDate);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        mAdView=findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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
        String input = "https://einvoice.s1ecos.gr/v/EL094352564-42547400-E33B5614B29BBB8AC2F25B345F3B75B83EAB3A9A-B1A3458713FA42C881D20651BDB4CD6D";
        //not supported type
        //String input = "https://www.iview.gr/181951675073530293";
        if (input.contains("https://www1.aade.gr") || input.contains("https://www1.gsis.gr")) {
            try {
                Document doc = Jsoup.connect(input).get();
                String info = doc.getElementsByClass("info").text();
                String receipt = doc.getElementsByClass("receipt").text();

                //Verified
                if (!doc.getElementsByClass("success").text().isEmpty()) {
                    receiptDate = ViewReceiptScreen.findWord(
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

                    receiptCost = ViewReceiptScreen.findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );
                    //receiptCost = receiptCost.replace(".", ",");

                    companyName = ViewReceiptScreen.findWord(
                            info,
                            "Επωνυμία",
                            "Διεύθυνση",
                            9,
                            -1
                    );

                } else if (!doc.getElementsByClass("box-error").text().isEmpty()) {
                    //closed company

                    receiptDate = "Unknown";

                    receiptCost = ViewReceiptScreen.findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );
                    //receiptCost = receiptCost.replace(".", ",");

                    companyName = "Unknown";

                } else if (!doc.getElementsByClass("box-warning").text().isEmpty()) {
                    //Not Verified
                    receiptDate = ViewReceiptScreen.findWord(
                            info,
                            "Διεύθυνση όπου λειτουργεί ο ΦΗΜ σήμερα ",
                            "Διεύθυνση όπου λειτουργεί ο ΦΗΜ σήμερα ",
                            39,
                            49
                    );

                    receiptCost = ViewReceiptScreen.findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );
                    //receiptCost = receiptCost.replace(".", ",");

                    companyName = ViewReceiptScreen.findWord(
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

                receiptDate = ViewReceiptScreen.findWord(
                        info,
                        "Ημερομηνία Έκδοσης",
                        "Ημερομηνία Έκδοσης",
                        "Ημερομηνία Έκδοσης".length()+1,
                        "Ημερομηνία Έκδοσης".length()+11
                );
                receiptDate = receiptDate.replace("/","-");
                dateBox.setText(receiptDate);

                receiptCost = ViewReceiptScreen.findWord(
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

                companyName = ViewReceiptScreen.findWord(
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
            //TODO print not supported
        }

        dbHandler.addProduct(new Receipt(nameBox.getText().toString(), Float.parseFloat(costBox.getText().toString()), dateBox.getText().toString()));
    }

    public static String findWord(String paragraph, String startString, String endString, int start, int end) {
        int startDate = paragraph.indexOf(startString) + start;
        int endDate = paragraph.indexOf(endString) + end;
        return paragraph.substring(startDate, endDate);
    }

   /* public void downloadReceipt() {
//        String input = "https://www1.aade.gr/tameiakes/myweb/q1.php?SIG=DCM1600619200252556F2BD37B9CF29CEB8695F8179590360D578695A091.20";
        String input = "https://www1.aade.gr/tameiakes/myweb/q1.php?SIG=CFA1800124300033535EBAA152B0CB943EDB30CF9135B5F8074ECB86E5622.99";

        try {
            Document doc = Jsoup.connect(input).get();
            String info = doc.getElementsByClass("info").text();
            String receipt =  doc.getElementsByClass("receipt").text();

            if (doc.getElementsByClass("warning").text().isEmpty()) {
                System.out.print("Verified");

                String receiptDate = MainActivity.findWord(
                        info,
                        "Ημερομηνία, ώρα",
                        "Ημερομηνία, ώρα",
                        16,
                        32
                );

                String receiptCost = MainActivity.findWord(
                        receipt,
                        "Συνολικού ποσού",
                        "ευρώ",
                        16,
                        -1
                );

                String companyName = MainActivity.findWord(
                        info,
                        "Επωνυμία",
                        "Διεύθυνση",
                        9,
                        -1
                );

                nameBox.setText(companyName);
                costBox.setText(receiptCost);
                dateBox.setText(receiptDate);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

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