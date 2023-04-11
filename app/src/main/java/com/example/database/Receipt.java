package com.example.database;

public class Receipt {
    private int _id;
    private String _companyName;
    private float _cost;
    private String _date;

    public Receipt() {
    }

    public Receipt(int id, String companyName, float cost, String date) {
        this.setID(id);
        this._companyName = companyName;
        this._cost = cost;
        this._date = date;
    }

    public Receipt(String companyName, float cost, String date) {
        this._companyName = companyName;
        this._cost = cost;
        this._date = date;
    }

    public int getID() {
        return _id;
    }

    public void setID(int _id) {
        this._id = _id;
    }

    public String get_companyName() {return _companyName;}

    public void set_companyName(String companyName) {this._companyName = companyName;}

    public float get_cost() {return _cost;}

    public void set_cost(float cost) {this._cost = cost;}

    public String get_date() {return _date;}

    public void set_date(String date) {this._date = date;}
}