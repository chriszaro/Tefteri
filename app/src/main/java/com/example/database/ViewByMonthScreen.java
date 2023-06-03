package com.example.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewByMonthScreen extends AppCompatActivity {

    private static final String[] MONTHS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2099;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RecyclerAdapter adapter;

    Menu activityBarMenu;
    MyDBHandler dbHandler;

    private Context mainContext;

    String selectedYear;
    String selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_by_month_screen);

        recyclerView = findViewById(R.id.recycler_view);

        //Set the layout of the items in the RecyclerView
        layoutManager = new LinearLayoutManager(this);
        mainContext = this;
        recyclerView.setLayoutManager(layoutManager);

        // Month spinner
        Spinner monthSpinner = findViewById(R.id.spinner_month);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MONTHS);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Year spinner
        Spinner yearSpinner = findViewById(R.id.spinner_year);
        List<String> years = new ArrayList<>();
        for (int i = MIN_YEAR; i <= MAX_YEAR; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Get current date
        Calendar actual_calendar = Calendar.getInstance();
        selectedYear = String.valueOf(actual_calendar.get(Calendar.YEAR));
        selectedMonth = String.valueOf(actual_calendar.get(Calendar.MONTH));

        refreshAdapter();

        // Set the current year and month as the default values
        yearSpinner.setSelection(years.indexOf(selectedYear));
        monthSpinner.setSelection(Integer.parseInt(selectedMonth));


        // You can use an OnItemSelectedListener to react to changes:
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = String.valueOf(Integer.parseInt((String) parent.getItemAtPosition(position))+ 1);
                refreshAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = (String) parent.getItemAtPosition(position);
                refreshAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        recyclerView = findViewById(R.id.recycler_view);

        //Set the layout of the items in the RecyclerView
        layoutManager = new LinearLayoutManager(this);
        mainContext = this;
        recyclerView.setLayoutManager(layoutManager);
        // as per the android documentation, the database should remain open for as long as possible
        dbHandler = new MyDBHandler(mainContext, null, null, 1);

        // Massively add data to database for testing purposes
        boolean loadManyReceipts = false;
        String manyReceiptsSQLInsertsFileName= "sql-queries/receiptsDB_db-receipts.sql"; // located in /src/main/assets
        if (loadManyReceipts) {
            dbHandler.loadDataFromFile(manyReceiptsSQLInsertsFileName);
            Log.d("MainActivityReceiptsLoa", "Loaded many receipts from MainActivity");
        }

        // Code For Ads
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        BottomNavigationView nav = findViewById(R.id.nav_view);
        nav.getMenu().findItem(R.id.navigation_recents).setChecked(false);
        nav.getMenu().findItem(R.id.navigation_monthly).setChecked(true);

        View calendar = nav.findViewById(R.id.navigation_recents);
        Activity a = this;
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.finish();
            }
        });
    }

    public void refreshAdapter() {
        adapter = new RecyclerAdapter( this, false, selectedMonth, selectedYear);
        recyclerView.setAdapter(adapter);
    }
}