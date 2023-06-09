package com.example.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {
    //Σταθερές για τη ΒΔ (όνομα ΒΔ, έκδοση, πίνακες κλπ)
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "receiptsDB.db";
    public static final String TABLE_RECEIPTS = "receipts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMPANYNAME = "companyname";
    public static final String COLUMN_COST = "cost";

    public static final String COLUMN_DATE = "date";
    private Context context;

    //Constructor
    public MyDBHandler(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    //Δημιουργία του σχήματος της ΒΔ (πίνακας products)
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = new StringBuilder().
                append("CREATE TABLE ").
                append(TABLE_RECEIPTS).
                append('(').
                append(COLUMN_ID).
                append(" INTEGER PRIMARY KEY,").
                append(COLUMN_COMPANYNAME).
                append(" TEXT,").
                append(COLUMN_COST).
                append(" INTEGER,").
                append(COLUMN_DATE).
                append(" TEXT").append(')').toString();
        db.execSQL(CREATE_PRODUCTS_TABLE);
//        db.close();
    }

    //Αναβάθμιση ΒΔ: εδώ τη διαγραφώ και τη ξαναδημιουργώ ίδια
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIPTS);
        onCreate(db);
    }

    //Μέθοδος για προσθήκη ενός προϊόντος στη ΒΔ
    public void addProduct(Receipt receipt) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPANYNAME, receipt.get_companyName());
        values.put(COLUMN_ID, receipt.get_ID());
        values.put(COLUMN_COST, receipt.get_cost());
        String date = Receipt.convertDateToDatabaseCompatible(receipt.get_date());
        values.put(COLUMN_DATE, date);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_RECEIPTS, null, values);
        db.close();
    }

    //Μέθοδος για εύρεση προϊόντος βάσει ονομασίας του
    public Receipt findProduct(String id) {
        String query = new StringBuilder().
                append("SELECT * FROM ").
                append(TABLE_RECEIPTS).
                append(" WHERE ").
                append(COLUMN_ID).
                append(" = '").
                append(id).
                append('\'').
                toString();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Receipt receipt;
        if (cursor.moveToFirst()) {
            receipt = this.createReceiptFromCursor(cursor);
        } else {
            receipt = null;
        }
        cursor.close();
        db.close();
        return receipt;
    }

    /**
     * Method that fetches all receipts, packs them in an ArrayList and returns them.
     * @return An ArrayList of all receipts the database contains
     */
    public ArrayList<Receipt> fetchAllReceipts() {
        String query = new StringBuilder().
                append("SELECT * FROM ").
                append(TABLE_RECEIPTS).
                append(" ORDER BY ").
                append(COLUMN_DATE).
                append(" DESC ").
                append(';').
                toString();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Receipt> receipts = new ArrayList<>();
        if (cursor.moveToFirst()) { // if the cursor is not empty
            do {
                Receipt receipt = this.createReceiptFromCursor(cursor);
                receipts.add(receipt);
            } while (cursor.moveToNext());
        } else // if the cursor is empty
            receipts = null;
        cursor.close();
        db.close();
        return receipts;
    }

    /**
     * Leave the parameter null if you don't want to update it
     *
     * @param ID          The ID of the receipt we want to update (NOT NULL)
     * @param companyName The new company name
     * @param cost        The new cost
     * @param date        The new date (make sure it's in a database compatible format
     */
    public boolean updateReceipt(String ID, String companyName, String cost, String date) {
        ContentValues pairs = new ContentValues();
        if (companyName != null)
            pairs.put(COLUMN_COMPANYNAME, companyName);
        if (cost != null)
            pairs.put(COLUMN_COST, cost);
        if (date != null) {
            date = Receipt.convertDateToDatabaseCompatible(date);
            pairs.put(COLUMN_DATE, date);
        }
        if (pairs.size() == 0) {
            return false;
        }
//        String updateString =
//                (companyName == null ? COLUMN_COMPANYNAME + " = '" + companyName + "', ": "") +
//                (cost == null ? COLUMN_COST + " = '" + cost + "', ": "") +
//                (date == null ? COLUMN_DATE + " = '" + date + "', ": "");
//        updateString = updateString.substring(0, updateString.length()-1); // cut the remaining comma

        /*String query = "UPDATE " + TABLE_RECEIPTS +
                " SET " +
                 updateString +
                " WHERE " + COLUMN_ID + " = '" + ID + "';"; // update only for the specific customer*/
        SQLiteDatabase db = this.getWritableDatabase();
        // found help at
        // https://stackoverflow.com/questions/9798473/sqlite-in-android-how-to-update-a-specific-row
        db.update(TABLE_RECEIPTS, pairs, COLUMN_ID + " = ?", new String[]{ID});
        db.close();
        return true;
    }

    /**
     * This method allows for receipt deletion from the database
     * @param ID the id of the receipt to be deleted
     */
    public void deleteReceipt(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEIPTS, COLUMN_ID + " = ?", new String[]{ID});
        db.close();
    }

    /**
     * Adds an additional 0 digit in front of 1 digit numbers
     *
     * @param month String number of the month
     * @return the corrected string number of the month
     */
    private String correctMonth(String month) {
        month = month.length() == 1 ? '0' + month : month; // if the month is a single digit, add 0
        // in front of it to make it compatible with an SQL query
        return month;
    }

    /**
     * This method parses a given month and year string, to make it usable for database operations
     * @param month the month, given in string format
     * @param year the year, given in string format
     * @return The first day of a month, given the month and year, in YYYY-MM-DD format
     */
    private String correctStartDate(String month, String year) {
        StringBuilder builder = new StringBuilder();
        builder.append(year);
        builder.append('-');
        builder.append(month);
        builder.append("-01");
        return builder.toString();
    }

    /**
     * This method parses a given month and year string, to make it usable for database operations
     * @param month the month, given in string format
     * @param year the year, given in string format
     * @return The last day of a month, given the month and year, in YYYY-MM-DD format
     */
    private String correctEndDate(String month, String year) {
        int m = Integer.parseInt(month);
        StringBuilder builder = new StringBuilder();
        builder.append(year);
        builder.append('-');
        builder.append(month);
        if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12)
            builder.append("-31");
        else if (m == 2) { // DETERMINE IF YEAR IS LEAP YEAR
            int y = Integer.parseInt(year);
            if (y % 4 == 0 && y % 100 != 0) // is leap year
                builder.append("-29");
            else if (y % 400 == 0) // is leap year
                builder.append("-29");
            else // is NOT leap year
                builder.append("-28");
        }
        else
            builder.append("-30");

        return builder.toString();
    }

    /**
     * Method to get the total cost of a specific month and year
     *
     * @param month String number of the month
     * @param year  String of the year
     * @return float number of the cost
     */
    public float getTotalCostOfMonth(String month, String year) {

        month = correctMonth(month); // make corrections to month string
        String startDate = correctStartDate(month, year);
        String endDate = correctEndDate(month, year);
        String query = new StringBuilder().
                append("SELECT SUM( ").
                append(COLUMN_COST).
                append(" )FROM ").
                append(TABLE_RECEIPTS).
                append(" WHERE ").
                append(COLUMN_DATE).
                append(" BETWEEN ").
                append("'").
                append(startDate).
                append("'").
                append(" AND ").
                append("'").
                append(endDate).
                append("'").
                append(" ORDER BY ").
                append(COLUMN_DATE).
                append(" DESC ").
                append(';').
                toString();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        float cost = 0f;
        if (cursor.moveToFirst()) {
            String costFloat = cursor.getString(0);
            if (costFloat != null)
                cost = Float.parseFloat(costFloat);
        }
        cursor.close();
        db.close();
        return cost;
    }

    /**
     * Method to get the Receipts of a specific month and year
     *
     * @param month String number of the month
     * @param year  String of the year
     * @return list of the receipts
     */
    public ArrayList<Receipt> fetchReceiptsBasedOnMonthAndYear(String month, String year) {

        month = correctMonth(month); // make corrections to month string
        String startDate = correctStartDate(month, year);
        String endDate = correctEndDate(month, year);
        String query = new StringBuilder().
                append("SELECT * FROM ").
                append(TABLE_RECEIPTS).
                append(" WHERE ").
                append(COLUMN_DATE).
                append(" BETWEEN ").
                append("'").
                append(startDate).
                append("'").
                append(" AND ").
                append("'").
                append(endDate).
                append("'").
                append(" ORDER BY ").
                append(COLUMN_DATE).
                append(" DESC ").
                append(';').
                toString();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Receipt> receipts = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Receipt r = createReceiptFromCursor(cursor);
                receipts.add(r);
            } while (cursor.moveToNext());
        } /* else
            receipts = null; */
        cursor.close();
        db.close();
        return receipts;
    }

    /**
     * This method creates a receipt object using a cursor
     *
     * @param cursor The cursor which contains data for the receipt, taken from a database
     * @return The receipt that is created from the cursor
     */
    private Receipt createReceiptFromCursor(Cursor cursor) {
        Receipt receipt = new Receipt();
        receipt.setID(Integer.parseInt(cursor.getString(0)));
        receipt.set_companyName(cursor.getString(1));
        receipt.set_cost(Float.parseFloat(cursor.getString(2)));
        String date = Receipt.convertDateToDDMMYYY(cursor.getString(3));
        receipt.set_date(date);
        return receipt;
    }

    /**
     * Adds data from an sql file that contains sql INSERT statements
     *
     * @param filename The path to the file, located in the assets folder
     */
    public void loadDataFromFile(String filename) {
        String TAG = "SQL massive Loader";
        InputStream inputStream = null;
        try {
            // Open the SQL file in the assets folder
            inputStream = this.context.getAssets().open(filename);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line;

            SQLiteDatabase db = this.getWritableDatabase();

            // Read the SQL file line by line
            while ((line = bufferedReader.readLine()) != null) {
//                stringBuilder.append(line);
//                stringBuilder.append('\n');
                db.execSQL(line);
            }
//            Log.d(TAG, stringBuilder.toString());
            // Execute the SQL statements that we fetched
//            db.execSQL(stringBuilder.toString());
            db.close();

            Log.d(TAG, "Loaded SQL file successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error while loading SQL data: " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing input stream: " + e.getMessage());
                }
            }
        }
    }
}