package com.netro.troxrider.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.netro.troxrider.R;
import com.netro.troxrider.adapter.ViewpagerAdapterHome;
import com.netro.troxrider.util.Tools;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    ChipNavigationBar bottomNav;
    CircleImageView userImage;
    TextView userName;
    ImageView notification;

    TabLayout tabLayout;
    ViewPager viewPager;

    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;

    Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);
        userImage = findViewById(R.id.user_image);
        userName = findViewById(R.id.user_name);
        notification = findViewById(R.id.notification);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        tools = new Tools();

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getUid();


        tabLayout.setupWithViewPager(viewPager);
        ViewpagerAdapterHome viewPagerAdapter = new ViewpagerAdapterHome(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0, true);






        //load user data
        db.collection("riderDetails").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String user_image = documentSnapshot.getString("rider_image");
                    String user_name = documentSnapshot.getString("rider_name");

                    if (user_image!=null){
                        Glide.with(MainActivity.this).load(user_image).into(userImage);
                    }

                    userName.setText(user_name);


                }
            }
        });


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                finish();
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });


        bottomNav.setItemSelected(R.id.nav_home,true);
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i){
                    case R.id.nav_home:
                        break;
s
                    case R.id.nav_history:
                        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                        break;

                    case R.id.nav_assigned:
                        startActivity(new Intent(MainActivity.this, AssignedActivity.class));
                        break;

                    case R.id.nav_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setItemSelected(R.id.nav_home,true);
    }

}