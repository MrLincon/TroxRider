package com.netro.troxrider.authentication;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.netro.troxrider.R;
import com.netro.troxrider.util.Tools;

public class ForgotPasswordActivity extends AppCompatActivity {

    ImageView back;

    TextInputLayout emailLayout;
    TextInputEditText email;

    String selected = "";
    CardView btnContinue;
    CoordinatorLayout main;

    Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        back = findViewById(R.id.back);
        btnContinue = findViewById(R.id.btn_continue);
        emailLayout = findViewById(R.id.email_layout);
        email = findViewById(R.id.et_email);
        main = findViewById(R.id.main);

        tools = new Tools();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email = email.getText().toString();

                if (Email.isEmpty()) {
                    emailLayout.setError("E-mail is required");
                    tools.makeSnack(main, "Email is required");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    emailLayout.setError("Please enter a valid email");
                    tools.makeSnack(main, "Please enter a valid email");
                    return;
                }
                else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(Email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Dialog popup = new Dialog(ForgotPasswordActivity.this);
                                        popup.setContentView(R.layout.popup_successful);
                                        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        TextView message = popup.findViewById(R.id.message);
                                        TextView actionText = popup.findViewById(R.id.action_text);
                                        CardView btnContinue = popup.findViewById(R.id.btn_continue);
                                        popup.setCancelable(false);
                                        popup.show();

                                        message.setText(R.string.pasword_reset_link);
                                        actionText.setText(R.string.continue_);

                                        btnContinue.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                popup.dismiss();
                                                finish();
                                            }
                                        });
                                    }else {
                                        tools.makeSnack(main," Please enter a valid e-mail address");
                                    }
                                }
                            });
                }


            }
        });
    }
}