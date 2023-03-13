package com.netro.troxrider.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netro.troxrider.R;
import com.netro.troxrider.authentication.LoginActivity;
import com.netro.troxrider.authentication.SignUpActivity;

public class SplashActivity extends AppCompatActivity {

    boolean firstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getApplication().getSharedPreferences("prefs", MODE_PRIVATE);
                firstStart = prefs.getBoolean("starting", true);

                if (firstStart==true) {

                    Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                    startActivity(intent);
                    finish();
                }else {

                    if (FirebaseAuth.getInstance().getCurrentUser()!=null){

                        FirebaseFirestore.getInstance().collection("riderDetails").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    String userType = documentSnapshot.getString("user_type");
                                    if (userType.equals("")){
                                        startActivity(new Intent(SplashActivity.this, AccountSetupActivity.class));
                                        finish();
                                    }else {
                                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            }
                        });

                    }else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }

                }
            }
        }, 2000);
    }
}