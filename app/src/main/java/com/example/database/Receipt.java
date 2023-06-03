package com.example.database;

import java.text.SimpleDateFormat;

public class Receipt {
    private int id;
    private String companyName;
    private float cost;
    private String date;

    public Receipt() {
    }

    public Receipt(String companyName, float cost, String date) {
        this.companyName = companyName;
        this.cost = cost;
        this.date = date;
        this.id = hashCode();
    }

    @Override
    public int hashCode() {
        int hash = Float.floatToIntBits(cost);
        hash = 31 * hash + companyName.hashCode();
        hash = 31 * hash + date.hashCode();
        return hash;
    }

    public int get_ID() {
        return id;
    }

    public void setID(int _id) {
        this.id = _id;
    }

    public String get_companyName() {return companyName;}

    public void set_companyName(String companyName) {this.companyName = companyName;}

    public float get_cost() {return cost;}

    public void set_cost(float cost) {this.cost = cost;}

    public String get_date() {return date;}

    // DD-MM-YYYY
    // YYYY-MM-DD
//    SimpleDateFormat

    /**
     * Convert a reference string from DD-MM-YYYY to YYYY-MM-DD format
     * @param ref
     * @return The converted date as a string
     */
    public static String convertDateToDatabaseCompatible(String ref){
        // defensive programming just to be sure
        if (ref.length() < 10) // if it ain't in date format, something went wrong
            return ref;
        if (ref.charAt(2) != '-') // if it's in format YYYY-MM-DD already, do nothing
            return ref;
        StringBuilder s = new StringBuilder();
        s.append(ref.substring(6));
        s.append('-');
        s.append(ref.substring(3, 5));
        s.append('-');
        s.append(ref.substring(0, 2));

        return s.toString();
    }

    /**
     * Convert a reference string from YYYY-MM-DD to DD-MM-YYYY format
     * @param ref
     * @return The converted date as a string
     */
    public static String convertDateToDDMMYYY(String ref){
        // defensive programming just to be sure
        if (ref.length() < 10) // if it ain't in date format, something went wrong
            return ref;
        if (ref.charAt(4) != '-') // if it's in format DD-MM-YYYY already, do nothing
            return ref;
        StringBuilder s = new StringBuilder();
        s.append(ref.substring(8));
        s.append('-');
        s.append(ref.substring(5, 7));
        s.append('-');
        s.append(ref.substring(0, 4));

        return s.toString();
    }

    public void set_date(String date) {this.date = date;}
}