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
    public static final String RECEIPTS_COLUMN_ID = "_id";
    public static final String RECEIPTS_COLUMN_COMPANYNAME = "companyname";
    public static final String RECEIPTS_COLUMN_COST = "cost";

    public static final String RECEIPTS_COLUMN_DATE = "date";

    public static final String TABLE_BRANDS = "companies";
    public static final String BRANDS_COLUMN_COMPANY_NAME = "company_name";
    public static final String BRANDS_COLUMN_DISCRETE_TITLE = "discrete_title";

    public static final String BRANDS_COLUMN_CATEGORY = "category";
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
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_RECEIPTS + '(' +
                RECEIPTS_COLUMN_ID + " INTEGER PRIMARY KEY," +
                RECEIPTS_COLUMN_COMPANYNAME + " TEXT," +
                RECEIPTS_COLUMN_COST + " INTEGER," +
                RECEIPTS_COLUMN_DATE + " TEXT" + ')';
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
        values.put(RECEIPTS_COLUMN_COMPANYNAME, receipt.get_companyName());
        values.put(RECEIPTS_COLUMN_ID, receipt.get_ID());
        values.put(RECEIPTS_COLUMN_COST, receipt.get_cost());
        String date = Receipt.convertDateToDatabaseCompatible(receipt.get_date());
        values.put(RECEIPTS_COLUMN_DATE, date);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_RECEIPTS, null, values);
        db.close();
    }

    //Μέθοδος για εύρεση προϊόντος βάσει ονομασίας του
    public Receipt findProduct(String id) {
        String query = "SELECT * FROM " + TABLE_RECEIPTS + " WHERE " +
                RECEIPTS_COLUMN_ID + " = '" + id + '\'';
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
     * This method fetches the first N results from the table
     *
     * @param N    the results we need
     * @param skip the number of results to be skipped
     * @return An array list of the receipts
     */
    public ArrayList<Receipt> fetchNReceipts(int N, int skip) {
        String query = "SELECT * FROM " + TABLE_RECEIPTS +
                " ORDER BY " + RECEIPTS_COLUMN_DATE + " DESC " +
                " LIMIT " + N +
                " OFFSET " + skip + ';';
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Receipt> receipts = new ArrayList<>(N);
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

    public ArrayList<Receipt> fetchAllReceipts() {
        String query = "SELECT * FROM " + TABLE_RECEIPTS +
                " ORDER BY " + RECEIPTS_COLUMN_DATE + " DESC " + ';';
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
            pairs.put(RECEIPTS_COLUMN_COMPANYNAME, companyName);
        if (cost != null)
            pairs.put(RECEIPTS_COLUMN_COST, cost);
        if (date != null) {
            date = Receipt.convertDateToDatabaseCompatible(date);
            pairs.put(RECEIPTS_COLUMN_DATE, date);
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
        db.update(TABLE_RECEIPTS, pairs, RECEIPTS_COLUMN_ID + " = ?", new String[]{ID});
        db.close();
        return true;
    }

    public void deleteReceipt(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEIPTS, RECEIPTS_COLUMN_ID + " = ?", new String[]{ID});
        db.close();
    }

    public void deleteReceipts(ArrayList<String> ids) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEIPTS, RECEIPTS_COLUMN_ID + " = ?", (String[]) ids.toArray());
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

    private String correctStartDate(String month, String year) {
        StringBuilder builder = new StringBuilder();
        builder.append(year);
        builder.append('-');
        builder.append(month);
        builder.append("-01");
        return builder.toString();
    }

    private String correctEndDate(String month, String year) {
        int m = Integer.parseInt(month);
        StringBuilder builder = new StringBuilder();
        builder.append(year);
        builder.append('-');
        builder.append(month);
        if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12)
            builder.append("-31");
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
                append(RECEIPTS_COLUMN_COST).
                append(" )FROM ").
                append(TABLE_RECEIPTS).
                append(" WHERE ").
                append(RECEIPTS_COLUMN_DATE).
                append(" BETWEEN ").
                append("'").
                append(startDate).
                append("'").
                append(" AND ").
                append("'").
                append(endDate).
                append("'").
                append(" ORDER BY ").
                append(RECEIPTS_COLUMN_DATE).
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
        String query = "SELECT * FROM " + TABLE_RECEIPTS +
                " WHERE " + RECEIPTS_COLUMN_DATE + " BETWEEN " +
                "'" + startDate + "'" + " AND " + "'" + endDate + "'" +
                " ORDER BY " + RECEIPTS_COLUMN_DATE + " DESC " + ';';

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
     * Executes SQL statements from sql file
     *
     * @param filename The path to the file, located in the assets folder
     */
    public void runSQLFile(String filename) {
        String TAG = "SQLML";
        InputStream inputStream = null;
        try {
            // Open the SQL file in the assets folder
            inputStream = this.context.getAssets().open(filename);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            SQLiteDatabase db = this.getWritableDatabase();

            // Read the SQL file line by line
            while ((line = bufferedReader.readLine()) != null) {
                db.execSQL(line);
            }
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

    public String brand(String company_name) {
        String query = "SELECT discrete_title FROM companies WHERE company_name = '" + company_name + "' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String result;
        if (cursor.moveToFirst()) { // if the cursor is not empty
            result = cursor.getString(0);
        } else // if the cursor is empty
            result = null;
        cursor.close();
        db.close();
        return result;
    }

    public void companiesTableInit() {
        SQLiteDatabase db = this.getReadableDatabase();

        if (!tableExists(db, TABLE_BRANDS)) {
            db.execSQL("DROP TABLE IF EXISTS companies;");

            String CREATE_BRANDS_TABLE = "CREATE TABLE " +
                    TABLE_BRANDS + '(' +
                    BRANDS_COLUMN_COMPANY_NAME + " TEXT PRIMARY KEY," +
                    BRANDS_COLUMN_DISCRETE_TITLE + " TEXT," +
                    BRANDS_COLUMN_CATEGORY + " TEXT" + ')';
            db.execSQL(CREATE_BRANDS_TABLE);
            this.runSQLFile("brands.sql"); // located in /src/main/assets
        }
        db.close();
    }

    /**
     * Checks if a table exists in a database
     * https://gist.github.com/ruslan-hut/389134bc0bd3cbbbc783c41f430b00ff
     * @param sqLiteDatabase the database
     * @param table the name of the table
     * @return true/false
     */
    private boolean tableExists(SQLiteDatabase sqLiteDatabase, String table){
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen() || table == null){
            return false;
        }
        int count = 0;
        String[] args = {"table",table};
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type=? AND name=?",args,null);
        if (cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count > 0;
    }

}