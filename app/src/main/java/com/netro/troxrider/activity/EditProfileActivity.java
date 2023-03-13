package com.netro.troxrider.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.netro.troxrider.R;
import com.netro.troxrider.util.Tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    CoordinatorLayout main;
    CardView btnUpdate;

    CircleImageView riderImage;
    ImageView back, selectImage;

    TextInputLayout nameLayout, emailLayout, dobLayout,
            contactLayout, genderLayout, countryLayout, addressLayout, workLocationLayout;

    TextInputEditText name, email, dob, contact, gender, country, address, workLocation;
    String user_type;

    Dialog popup;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userID, valueUserType;

    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;
    private String imageLink = "";
    byte[] Data;
    private final int PICK_IMAGE_REQUEST = 22;
    private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 101;

    Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        main = findViewById(R.id.main);
        back = findViewById(R.id.back);
        btnUpdate = findViewById(R.id.btn_update);
        riderImage = findViewById(R.id.user_image);
        selectImage = findViewById(R.id.select_image);
        nameLayout = findViewById(R.id.name_layout);
        emailLayout = findViewById(R.id.email_layout);
        dobLayout = findViewById(R.id.user_dob_layout);
        contactLayout = findViewById(R.id.contact_layout);
        genderLayout = findViewById(R.id.gender_layout);
        countryLayout = findViewById(R.id.country_layout);
        addressLayout = findViewById(R.id.address_layout);
        workLocationLayout = findViewById(R.id.work_location_layout);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.user_dob);
        contact = findViewById(R.id.contact);
        gender = findViewById(R.id.gender);
        country = findViewById(R.id.country);
        address = findViewById(R.id.address);
        workLocation = findViewById(R.id.work_location);


        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        popup = new Dialog(this);
        tools = new Tools();

        db.collection("riderDetails").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String rider_image = documentSnapshot.getString("rider_image");
                String user_name = documentSnapshot.getString("rider_name");
                String user_email = documentSnapshot.getString("rider_email");
                String user_dob = documentSnapshot.getString("rider_dob");
                String user_contact = documentSnapshot.getString("rider_contact");
                user_type = documentSnapshot.getString("user_type");
                String user_gender = documentSnapshot.getString("rider_gender");
                String user_country = documentSnapshot.getString("rider_country");
                String user_address = documentSnapshot.getString("rider_address");
                String user_work_location = documentSnapshot.getString("rider_work_location");

                Glide.with(EditProfileActivity.this).load(rider_image).into(riderImage);
                name.setText(user_name);
                email.setText(user_email);
                dob.setText(user_dob);
                contact.setText(user_contact);
                gender.setText(user_gender);
                country.setText(user_country);
                address.setText(user_address);
                workLocation.setText(user_work_location);

            }
        });


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Request the permission
                    ActivityCompat.requestPermissions(EditProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_READ_EXTERNAL_STORAGE);
                } else {
                    selectImage();
                }
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tools.makeSnack(main, "E-mail address can not be changed");
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.makeSnack(main, "Death of birth can not be changed");
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog genderPopup = new Dialog(EditProfileActivity.this);
                genderPopup.setContentView(R.layout.popup_gender);
                genderPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                RelativeLayout male = genderPopup.findViewById(R.id.male);
                RelativeLayout female = genderPopup.findViewById(R.id.female);
                RelativeLayout other = genderPopup.findViewById(R.id.other);
                genderPopup.show();

                male.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        male.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_bg));
                        gender.setText("Male");
                        genderPopup.dismiss();
                    }
                });

                female.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        female.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_bg));
                        gender.setText("Female");
                        genderPopup.dismiss();
                    }
                });

                other.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        other.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_bg));
                        gender.setText("Other");
                        genderPopup.dismiss();
                    }
                });
            }
        });

        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             tools.makeSnack(main, "Country can not be changed");

            }
        });

        workLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.makeSnack(main, "Work location can not be changed");
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString();
                String DOB = dob.getText().toString();
                String Contact = contact.getText().toString();
                String Gender = gender.getText().toString();
                String Country = country.getText().toString();
                String WorkLocation = workLocation.getText().toString();
                String Address = address.getText().toString();

                if (!Name.isEmpty() && !Address.isEmpty() && !DOB.isEmpty() && !Contact.isEmpty()
                        && !Gender.isEmpty()) {

                    if (filePath != null) {
                        tools.loading(popup, true);

                        Map<String, Object> userMap = new HashMap<>();

                        userMap.put("rider_name", Name);
                        userMap.put("rider_dob", DOB);
                        userMap.put("rider_contact", Contact);
                        userMap.put("rider_gender", Gender);
                        userMap.put("rider_address", Address);

                        db.collection("riderDetails").document(userID).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                uploadImage(userID);

                            }
                        });
                    } else {
                        tools.loading(popup, true);

                        Map<String, Object> userMap = new HashMap<>();

                        userMap.put("rider_name", Name);
                        userMap.put("rider_dob", DOB);
                        userMap.put("rider_contact", Contact);
                        userMap.put("rider_gender", Gender);
                        userMap.put("rider_address", Address);

                        db.collection("riderDetails").document(userID).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                popup.dismiss();
                                startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                                finish();
                            }
                        });
                    }
                } else {
                    tools.makeSnack(main, getString(R.string.fill_all_the_fields));

                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                finish();
            }
        });


    }


    // Select Image method
    private void selectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                Data = baos.toByteArray();
                Glide.with(getApplicationContext()).load(bitmap).centerCrop().into(riderImage);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    // UploadImage method
    private void uploadImage(String ID) {

        if (filePath != null) {
            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data = baos.toByteArray();
                //uploading the image
                UploadTask uploadTask = ref.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        tools.makeSnack(main, "Upload successful");

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageLink = uri.toString();
                                Log.d("imageLink", "onSuccess: " + imageLink);
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                DocumentReference docRef = db.collection("riderDetails").document(ID);

                                Map<String, Object> userMap = new HashMap<>();
                                final String id = docRef.getId();

                                userMap.put("rider_image", imageLink);
                                userMap.put("timestamp", FieldValue.serverTimestamp());

                                docRef.update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        popup.dismiss();
                                        startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        popup.dismiss();
                                    }
                                });
                            }
                        });

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        popup.dismiss();
                    }
                });
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
        finish();
    }
}