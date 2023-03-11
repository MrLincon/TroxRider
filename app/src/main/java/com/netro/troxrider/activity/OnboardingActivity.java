package com.netro.troxrider.activity;

import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.netro.troxrider.R;
import com.netro.troxrider.adapter.SliderAdapterOnboarding;


public class OnboardingActivity extends AppCompatActivity {

    private ViewPager onboarding;
    private LinearLayout dots;
    private TextView[] mdots;

    private int currentPage;

    private SliderAdapterOnboarding sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        onboarding = findViewById(R.id.viewPagerOnboarding);
        dots = findViewById(R.id.dots);

        sliderAdapter = new SliderAdapterOnboarding(this, onboarding);

        onboarding.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        onboarding.addOnPageChangeListener(viweListener);

    }

    private void addDotsIndicator(int position) {
        mdots = new TextView[3];
        dots.removeAllViews();
        for (int i = 0; i < mdots.length; i++) {
            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226;"));
            mdots[i].setTextSize(35);
            mdots[i].setTextColor(getResources().getColor(R.color.colorWhiteHighEmp));

            dots.addView(mdots[i]);
        }

        if (mdots.length > 0) {
            mdots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    ViewPager.OnPageChangeListener viweListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}