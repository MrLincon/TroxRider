package com.netro.troxrider.authentication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netro.troxrider.R;
import com.netro.troxrider.util.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    ImageView back;
    TextView logIn;
    CoordinatorLayout main;
    CardView cv_continue;

    TextInputLayout layout_email, layout_password;
    TextInputEditText et_email, et_password;

    String selected;

    Dialog popup;
    Tools tools;

    //Firebase
    private FirebaseAuth mAuth;
    private String userID;

    private FirebaseFirestore db;
    private DocumentReference document_ref;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        back = findViewById(R.id.back);
        logIn = findViewById(R.id.login);
        cv_continue = findViewById(R.id.cv_continue);
        main = findViewById(R.id.main);

        layout_email = findViewById(R.id.layout_email);
        layout_password = findViewById(R.id.layout_password);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        popup = new Dialog(this);

        tools = new Tools();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        cv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void getData() {

        final String email = et_email.getText().toString().trim();
        final String password = et_password.getText().toString().trim();

        if (email.isEmpty()) {
            layout_email.setError("");
            tools.makeSnack(main, "E-mail is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layout_email.setError("");
            tools.makeSnack(main, "Please enter a valid email");
            return;
        }

        if (password.isEmpty()) {
            layout_password.setError("");
            tools.makeSnack(main, "Password is required");
            return;
        }

        if (password.length() < 6) {
            layout_password.setError("");
            tools.makeSnack(main, "Minimum length of password should be 6");
            return;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            layout_password.setError("");
            Toast.makeText(SignUpActivity.this,getResources().getString( R.string.pass_check), Toast.LENGTH_LONG).show();
            return;
        } else {
            tools.loading(popup, true);
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    boolean check = !task.getResult().getSignInMethods().isEmpty();

                    if (!check) {

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            tools.logMessage("signup", "completed");
                                            tools.loading(popup, false);

                                            FirebaseAuth.getInstance().getCurrentUser()
                                                    .sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            Dialog popup2 = new Dialog(SignUpActivity.this);
                                                            popup2.setContentView(R.layout.popup_successful);
                                                            popup2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                            TextView message = popup2.findViewById(R.id.message);
                                                            TextView actionText = popup2.findViewById(R.id.action_text);
                                                            CardView btnContinue = popup2.findViewById(R.id.btn_continue);
                                                            popup2.show();
                                                            popup2.setCancelable(false);

                                                            message.setText("A verification e-mail has been sent to your mail address. Verify the e-mail address and log in");
                                                            actionText.setText(getResources().getString(R.string.continue_));


                                                            btnContinue.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    userID = mAuth.getUid();

                                                                    document_ref = db.collection("riderDetails").document(userID);

                                                                    Map<String, String> userMap = new HashMap<>();

                                                                    userMap.put("rider_email", email);
                                                                    userMap.put("rider_id", userID);
                                                                    userMap.put("user_type", "");

                                                                    document_ref.set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            popup2.dismiss();
                                                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                            finish();

                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            tools.makeSnack(main, "Please try again");
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    }
                                });

                    }
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();

    }
}

