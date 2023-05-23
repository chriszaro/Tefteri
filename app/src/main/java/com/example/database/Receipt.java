package com.example.database;

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


    /**
     * Convert a reference string from DD-MM-YYYY to YYYY-MM-DD format
     * @param ref
     * @return
     */
    public static String convertDateToDatabaseCompatible(String ref){
        // defensive programming just to be sure
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

    public void set_date(String date) {this.date = date;}
}