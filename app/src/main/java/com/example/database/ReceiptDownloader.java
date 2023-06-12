package com.example.database;

import android.os.Build;
import android.os.StrictMode;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ReceiptDownloader {

    MyDBHandler dbHandler;

    MainActivity mainActivity;

    /**
     * Constructor
     *
     * @param dbHandler    the current database
     * @param mainActivity the current mainActivity
     */
    ReceiptDownloader(MyDBHandler dbHandler, MainActivity mainActivity) {
        this.dbHandler = dbHandler;
        this.mainActivity = mainActivity;
    }

    /**
     * Scrapes the information and creates the object of the receipt
     *
     * @param input String of the link of the QR code
     * @return a string with the ID of the receipt
     */
    public String downloadReceipt(String input) {
        String companyName = "";
        String receiptCost = "";
        String receiptDate = "";

        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Scrape Process

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
                    receiptDate = findWord(
                            info,
                            "Ημερομηνία, ώρα",
                            "Ημερομηνία, ώρα",
                            "Ημερομηνία, ώρα".length() + 1,
                            "Ημερομηνία, ώρα".length() + 11
                    );
                    receiptDate = Receipt.convertDateToDDMMYYY(receiptDate);

                    receiptCost = findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );

                    companyName = findWord(
                            info,
                            "Επωνυμία",
                            "Διεύθυνση",
                            9,
                            -1
                    );

                } else if (!doc.getElementsByClass("box-error").text().isEmpty()) {
                    //Receipt from closed company
                    receiptDate = "Unknown";

                    receiptCost = findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );

                    companyName = "Unknown";

                } else if (!doc.getElementsByClass("box-warning").text().isEmpty()) {
                    //Not Verified from gov receipt
                    receiptDate = findWord(
                            info,
                            "Διεύθυνση όπου λειτουργεί ο ΦΗΜ σήμερα ",
                            "Διεύθυνση όπου λειτουργεί ο ΦΗΜ σήμερα ",
                            39,
                            49
                    );

                    receiptCost = findWord(
                            receipt,
                            "Συνολικού ποσού",
                            "ευρώ",
                            16,
                            -1
                    );

                    companyName = findWord(
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

                receiptDate = findWord(
                        info,
                        "Ημερομηνία Έκδοσης",
                        "Ημερομηνία Έκδοσης",
                        "Ημερομηνία Έκδοσης".length() + 1,
                        "Ημερομηνία Έκδοσης".length() + 11
                );
                receiptDate = receiptDate.replace("/", "-");

                receiptCost = findWord(
                        info,
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)",
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)",
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)".length() + 1,
                        "Σύνολο για πληρωμή EUR (συμπεριλαμβανομένου ΦΠΑ)".length() + 1 + 12
                );
                receiptCost = receiptCost.trim().replace(",", ".");
                String[] xd = receiptCost.split("\\.");
                receiptCost = xd[0] + "." + xd[1].charAt(0) + xd[1].charAt(1);

                companyName = findWord(
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

                receiptDate = findWord(
                        info,
                        "ΗΜΕΡΟΜΗΝΙΑ",
                        "ΗΜΕΡΟΜΗΝΙΑ",
                        "ΗΜΕΡΟΜΗΝΙΑ".length() + 1,
                        "ΗΜΕΡΟΜΗΝΙΑ".length() + 11
                );
                receiptDate = receiptDate.replace("/", "-");

                receiptCost = findWord(
                        info,
                        "ΣΥΝΟΛΙΚΗ ΑΞΙΑ",
                        "Μ.Αρ.Κ.",
                        "ΣΥΝΟΛΙΚΗ ΑΞΙΑ".length() + 1,
                        -1
                );
                receiptCost = receiptCost.trim().replace(",", ".");

                companyName = findWord(
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
            Toast.makeText(mainActivity, "Δεν υποστηρίζεται αυτός ο τύπος απόδειξης", Toast.LENGTH_SHORT).show();
            return null;
        }

        Receipt newReceipt = new Receipt(companyName, Float.parseFloat(receiptCost), receiptDate); // create the object
        dbHandler.addProduct(newReceipt);   // add it to the database
        return String.valueOf(newReceipt.get_ID()); // return the id
    }

    /**
     * Data cleaning
     * @param paragraph The text we want to clean
     * @param startString start index
     * @param endString end index
     * @param start offset for start index
     * @param end offset for end index
     * @return the text we want
     */
    static String findWord(String paragraph, String startString, String endString, int start, int end) {
        int startDate = paragraph.indexOf(startString) + start;
        int endDate = paragraph.indexOf(endString) + end;
        return paragraph.substring(startDate, endDate);
    }
}
