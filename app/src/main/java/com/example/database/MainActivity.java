package com.example.database;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {
    TextView idView;
    EditText nameBox;
    EditText costBox;
    EditText dateBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get references to view objects
        idView = findViewById(R.id.receiptID);
        nameBox = findViewById(R.id.companyName);
        costBox = findViewById(R.id.receiptCost);
        dateBox = findViewById(R.id.receiptDate);
    }

    //OnClick method for ADD button
    public void newReceipt (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        String companyName = nameBox.getText().toString();
        String receiptCost = costBox.getText().toString();
        String receiptDate = dateBox.getText().toString();

        if (!companyName.equals("") &&  !receiptCost.equals("") &&  !receiptDate.equals("")){
            Receipt found = dbHandler.findProduct(companyName);
            if (found == null){
                Receipt receipt = new Receipt(companyName, Float.parseFloat(receiptCost), receiptDate);
                dbHandler.addProduct(receipt);
                nameBox.setText("");
                costBox.setText("");
                dateBox.setText("");
            }
        }

        String input = "https://www1.aade.gr/tameiakes/myweb/q1.php?SIG=CFA1800124300033535EBAA152B0CB943EDB30CF9135B5F8074ECB86E5622.99";

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        try {
            Document doc = Jsoup.connect(input).get();
            String info = doc.getElementsByClass("info").text();
            String receipt =  doc.getElementsByClass("receipt").text();

            if (doc.getElementsByClass("warning").text().isEmpty()) {
                System.out.print("Verified");

                receiptDate = MainActivity.findWord(
                        info,
                        "Ημερομηνία, ώρα",
                        "Ημερομηνία, ώρα",
                        16,
                        32
                );

                receiptCost = MainActivity.findWord(
                        receipt,
                        "Συνολικού ποσού",
                        "ευρώ",
                        16,
                        -1
                );

                companyName = MainActivity.findWord(
                        info,
                        "Επωνυμία",
                        "Διεύθυνση",
                        9,
                        -1
                );
                Log.d("myTag", companyName);
                nameBox.setText(companyName);
                costBox.setText(receiptCost);
                dateBox.setText(receiptDate);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String findWord(String paragraph, String startString, String endString, int start, int end){
        int startDate = paragraph.indexOf(startString) + start;
        int endDate = paragraph.indexOf(endString) + end;
        return paragraph.substring(startDate, endDate);
    }

    public void downloadReceipt() {
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