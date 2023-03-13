package com.netro.troxrider.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;

import com.netro.troxrider.R;
import com.netro.troxrider.util.Tools;

public class HistoryActivity extends AppCompatActivity {

    Tools tools;
    CoordinatorLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        main = findViewById(R.id.main);

        tools = new Tools();
        tools.setLightStatusBar(main,HistoryActivity.this);
    }
}