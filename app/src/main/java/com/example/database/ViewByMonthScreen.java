package com.example.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

/**
 * Activity to see the receipts by month
 */
public class ViewByMonthScreen extends AppCompatActivity {

    private static final String[] MONTHS = new String[] { "Ιανουάριος", "Φεβρουάριος", "Μάρτιος", "Απρίλιος", "Μάιος", "Ιούνιος", "Ιούλιος", "Αύγουστος", "Σεπτέμβριος", "Οκτώβριος", "Νοέμβριος", "Δεκέμβριος" };
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    Menu activityBarMenu;
    MyDBHandler dbHandler;
    private Context mainContext;
    String selectedYear;
    String selectedMonth;

    TextView sumField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_by_month_screen);

        recyclerView = findViewById(R.id.recycler_view);

        //Set the layout of the items in the RecyclerView
        layoutManager = new LinearLayoutManager(this);
        mainContext = this;
        recyclerView.setLayoutManager(layoutManager);
        // as per the android documentation, the database should remain open for as long as possible
        dbHandler = new MyDBHandler(mainContext, null, null, 1);
        sumField = findViewById(R.id.sumField);

        // Month spinner
        Spinner monthSpinner = findViewById(R.id.spinner_month);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, MONTHS);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        int MIN_YEAR = 2019;
        Calendar actual_calendar = Calendar.getInstance();
        int MAX_YEAR = actual_calendar.get(Calendar.YEAR);

        // Year spinner
        Spinner yearSpinner = findViewById(R.id.spinner_year);
        List<String> years = new ArrayList<>();
        for (int i = MIN_YEAR; i <= MAX_YEAR; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Get current date
        selectedYear = String.valueOf(actual_calendar.get(Calendar.YEAR));
        selectedMonth = String.valueOf(actual_calendar.get(Calendar.MONTH));

        // Set the current year and month as the default values
        yearSpinner.setSelection(years.indexOf(selectedYear));
        monthSpinner.setSelection(Integer.parseInt(selectedMonth));

        // Callback function for when new item is selected from the month spinner
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedMonth = String.valueOf(position + 1);
                float cost = dbHandler.getTotalCostOfMonth(selectedMonth, selectedYear);
                // Setting the new value to the sumField and refreshing
                sumField.setText(String.valueOf(cost) + '€');
                refreshAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Callback function for when new item is selected from the year spinner
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = (String) parent.getItemAtPosition(position);
                float cost = dbHandler.getTotalCostOfMonth(selectedMonth, selectedYear);
                // Finally, set the value in sumField
                sumField.setText(String.valueOf(cost) + '€');
                refreshAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        float cost = dbHandler.getTotalCostOfMonth(selectedMonth, selectedYear);
        // Finally, set the value in sumField
        sumField.setText(String.valueOf(cost) + '€');
        refreshAdapter();
    }

    /**
     * This method is for the trash button on activity bar
     *
     * @param menu menu object
     * @return true/false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // here goes options menu with question mark
        getMenuInflater().inflate(R.menu.main_menu, menu);
        activityBarMenu = menu;
        MenuItem trashCanItem = menu.findItem(R.id.action_delete);
        trashCanItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                ArrayList<String> ids = adapter.findIDsOfItemsForDeletion();
                for (String id : ids){
                    dbHandler.deleteReceipt(id);
                }
                float cost = dbHandler.getTotalCostOfMonth(selectedMonth, selectedYear);
                // Finally, set the value in sumField
                sumField.setText(String.valueOf(cost) + '€');
                refreshAdapter();
                adapter.disableTrash();
                return false;
            }
        });

        MenuItem questionMark = menu.findItem(R.id.action_about);
        questionMark.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(mainContext, AboutActivity.class);
                startActivity(intent);
                return false;
            }
        });
        trashCanItem.setVisible(false);
        return true;
    }

    public void refreshAdapter() {
        adapter = new RecyclerAdapter( this, true, selectedMonth, selectedYear);
        recyclerView.setAdapter(adapter);
        invalidateOptionsMenu();
        float cost = dbHandler.getTotalCostOfMonth(selectedMonth, selectedYear);
        // Finally, set the value in sumField
        sumField.setText(String.valueOf(cost) + '€');
    }

    public Menu getActivityBarMenu() {
        return activityBarMenu;
    }
}