package com.netro.troxrider.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.netro.troxrider.R;
import com.netro.troxrider.adapter.ViewpagerAdapterAssigned;
import com.netro.troxrider.adapter.ViewpagerAdapterHistory;
import com.netro.troxrider.util.Tools;

public class AssignedActivity extends AppCompatActivity {

    Tools tools;
    CoordinatorLayout main;
    ImageView back;

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned);

        back = findViewById(R.id.back);
        main = findViewById(R.id.main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        tools = new Tools();
        tools.setLightStatusBar(main,AssignedActivity.this);

        tabLayout.setupWithViewPager(viewPager);
        ViewpagerAdapterAssigned viewPagerAdapter = new ViewpagerAdapterAssigned(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0, true);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    }
