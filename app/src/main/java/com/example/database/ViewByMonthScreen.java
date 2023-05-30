package com.example.database;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ViewByMonthScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_by_month_screen);

        BottomNavigationView nav = findViewById(R.id.nav_view);
        nav.getMenu().findItem(R.id.navigation_home).setChecked(false);
        nav.getMenu().findItem(R.id.navigation_calendar).setChecked(true);

        View calendar = nav.findViewById(R.id.navigation_home);
        Activity a = this;
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.finish();
            }
        });
    }
}