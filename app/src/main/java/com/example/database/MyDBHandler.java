package com.example.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

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
                TABLE_RECEIPTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_COMPANYNAME + " TEXT," +
                COLUMN_COST + " INTEGER," +
                COLUMN_DATE + " TEXT" + ")";
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
        values.put(COLUMN_COST, receipt.get_cost());
        values.put(COLUMN_DATE, receipt.get_date());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_RECEIPTS, null, values);
        db.close();
    }

    //Μέθοδος για εύρεση προϊόντος βάσει ονομασίας του
    public Receipt findProduct(String companyName) {
        String query = "SELECT * FROM " + TABLE_RECEIPTS + " WHERE " +
                COLUMN_COMPANYNAME + " = '" + companyName + "'";
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