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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.netro.troxrider.R;
import com.netro.troxrider.util.Tools;

import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    ImageView back;
    TextInputLayout emailLayout, currentPasswordLayout, newPasswordLayout;
    TextInputEditText email, currentPassword, newPassword;
    CoordinatorLayout main;

    FirebaseUser user;
    AuthCredential credential;
    String Email, CurrentPassword, NewPassword;


    CardView changePassword;

    Dialog popup;

    Tools tools;

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
        setContentView(R.layout.activity_reset_password);

        main = findViewById(R.id.main);
        back = findViewById(R.id.back);
        emailLayout = findViewById(R.id.email_layout);
        currentPasswordLayout = findViewById(R.id.password_layout);
        newPasswordLayout = findViewById(R.id.password_layout_new);
        email = findViewById(R.id.et_email);
        currentPassword = findViewById(R.id.et_password);
        newPassword = findViewById(R.id.et_password_new);
        changePassword = findViewById(R.id.change_password);


        tools = new Tools();

        tools.setLightStatusBar(main, this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Email = email.getText().toString();
                CurrentPassword = currentPassword.getText().toString();
                NewPassword = newPassword.getText().toString();

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

                if (CurrentPassword.isEmpty()) {
                    currentPasswordLayout.setError("Current password is required");
                    tools.makeSnack(main, "Current password is required");
                    return;
                }

                if (CurrentPassword.length() < 6) {
                    currentPasswordLayout.setError("Minimum length of current password should be 6");
                    tools.makeSnack(main, "Minimum length of current password was  6");
                    return;
                }
                if (!PASSWORD_PATTERN.matcher(CurrentPassword).matches()) {
                    currentPasswordLayout.setError("");
                    Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.pass_check), Toast.LENGTH_LONG).show();
                    return;
                }
                if (NewPassword.isEmpty()) {
                    newPasswordLayout.setError("New password is required");
                    tools.makeSnack(main, "New password is required");
                    return;
                }

                if (NewPassword.length() < 6) {
                    newPasswordLayout.setError("Minimum length of new password should be 6");
                    tools.makeSnack(main, "Minimum length of new password should be 6");
                    return;
                }
                if (!PASSWORD_PATTERN.matcher(NewPassword).matches()) {
                    newPasswordLayout.setError("");
                    Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.pass_check), Toast.LENGTH_LONG).show();
                    return;
                } else {

                    user = FirebaseAuth.getInstance().getCurrentUser();

                    credential = EmailAuthProvider.getCredential(Email, CurrentPassword);


                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Update the user's password with the new password
                                        user.updatePassword(NewPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseAuth.getInstance().signOut();

                                                            Dialog popup = new Dialog(ResetPasswordActivity.this);
                                                            popup.setContentView(R.layout.popup_successful);
                                                            popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                            TextView message = popup.findViewById(R.id.message);
                                                            TextView actionText = popup.findViewById(R.id.action_text);
                                                            CardView btnContinue = popup.findViewById(R.id.btn_continue);
                                                            popup.setCancelable(false);
                                                            popup.show();

                                                            message.setText("Your password has been updated. Log in with your new password.");
                                                            actionText.setText("Log in");

                                                            btnContinue.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                                                    finish();
                                                                }
                                                            });

                                                        } else {
                                                            currentPasswordLayout.setErrorEnabled(true);
                                                            tools.makeSnack(main, "Your current password in not correct");
                                                        }
                                                    }
                                                });
                                    } else {
                                        tools.makeSnack(main, "Your current password in not correct");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    tools.makeSnack(main, "Your current password in not correct");
                                }
                            });
                }
            }
        });
    }
}