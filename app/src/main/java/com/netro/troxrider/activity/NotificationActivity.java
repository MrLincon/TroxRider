package com.netro.troxrider.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;

import com.netro.troxrider.R;
import com.netro.troxrider.util.Tools;

public class NotificationActivity extends AppCompatActivity {

    Tools tools;
    CoordinatorLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        main = findViewById(R.id.main);

        tools = new Tools();
        tools.setLightStatusBar(main,NotificationActivity.this);
    }
}