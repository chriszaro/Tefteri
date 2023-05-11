package com.example.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    //Constructor
    public MyDBHandler(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Δημιουργία του σχήματος της ΒΔ (πίνακας products)
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_RECEIPTS + '(' +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_COMPANYNAME + " TEXT," +
                COLUMN_COST + " INTEGER," +
                COLUMN_DATE + " TEXT" + ')';
        db.execSQL(CREATE_PRODUCTS_TABLE);
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
        values.put(COLUMN_DATE, receipt.get_date());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_RECEIPTS, null, values);
        db.close();
    }

    //Μέθοδος για εύρεση προϊόντος βάσει ονομασίας του
    public Receipt findProduct(String companyName) {
        String query = "SELECT * FROM " + TABLE_RECEIPTS + " WHERE " +
                COLUMN_COMPANYNAME + " = '" + companyName + '\'';
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Receipt receipt = new Receipt();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            receipt.setID(Integer.parseInt(cursor.getString(0)));
            receipt.set_companyName(cursor.getString(1));
            receipt.set_cost(Integer.parseInt(cursor.getString(2)));
            receipt.set_date(cursor.getString(3));
            cursor.close();
        } else {
            receipt = null;
        }
        db.close();
        return receipt;
    }


    /**
     * This method fetches the first N results from the table
     * @param N the results we need
     * @param skip the number of results to be skipped
     * @return An array list of the receipts
     */
    public ArrayList<Receipt> fetchNReceipts(int N, int skip){
        String query = "SELECT * FROM " + TABLE_RECEIPTS +
                       " ORDER BY " + COLUMN_DATE +
                       " LIMIT " + N +
                       " OFFSET " + skip + ';';
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Receipt> receipts =new ArrayList<>(N);
        if (cursor.moveToFirst()){ // if the cursor is not empty
            do {
                Receipt receipt = this.createReceiptFromCursor(cursor);
                receipts.add(receipt);
            } while (cursor.moveToNext());
        } else { // if the cursor is empty
            receipts = null;
        }
        db.close();
        return receipts;

    }


    /**
     * Leave the parameter null if you don't want to update it
     * @param ID The ID of the receipt we want to update (NOT NULL)
     * @param companyName The new company name
     * @param cost The new cost
     * @param date The new date
     */
    public void updateReceipt(String ID, String companyName, String cost, String date){
        ContentValues pairs = new ContentValues();
        if (companyName != null)
            pairs.put(COLUMN_COMPANYNAME, companyName);
        if (cost != null)
            pairs.put(COLUMN_COST, cost);
        if (date != null)
            pairs.put(COLUMN_DATE, date);
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
    }

    public void deleteReceipt(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEIPTS, COLUMN_ID + " = ?", new String[]{ID});
        db.close();
    }

    public void deleteReceipts(ArrayList<String> ids) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEIPTS, COLUMN_ID + " = ?", (String[]) ids.toArray());
        db.close();
    }

    /**
     * This method creates a receipt object using a cursor
     * @param cursor The cursor which contains data for the receipt, taken from a database
     * @return The receipt that is created from the cursor
     */
    private Receipt createReceiptFromCursor(Cursor cursor){
        Receipt receipt = new Receipt();
        receipt.setID(Integer.parseInt(cursor.getString(0)));
        receipt.set_companyName(cursor.getString(1));
        receipt.set_cost(Integer.parseInt(cursor.getString(2)));
        receipt.set_date(cursor.getString(3));
        return receipt;
    }

//    //Μέθοδος για διαγραφή προϊόντος βάσει ονομασίας του
//    public boolean deleteProduct(String productname) {
//        boolean result = false;
//        Product product = findProduct(productname);
//        if (product != null){
//            SQLiteDatabase db = this.getWritableDatabase();
//            db.delete(TABLE_PRODUCTS, COLUMN_ID + " = ?",
//                    new String[] { String.valueOf(product.getID()) });
//            result = true;
//            db.close();
//        }
//        return result;
//    }
}