package com.netro.troxrider.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
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
import com.netro.troxrider.adapter.ImageAdapter;
import com.netro.troxrider.util.Tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetupActivity extends AppCompatActivity {

    CoordinatorLayout main;
    CardView btnContinue;

    CircleImageView userImage;
    ImageView selectImage;
    RecyclerView recyclerView;

    TextInputLayout nameLayout, emailLayout, dobLayout,
            contactLayout, genderLayout, countryLayout, stateLayout, addressLayout, workLocationLayout,
            identityLayout;

    TextInputEditText name, email, dob, contact, gender, country, state, workLocation, address, identityType;

    LinearLayout upload;

    Dialog popup;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userID;

    FirebaseStorage storage;
    StorageReference storageReference;
    private CollectionReference item;
    private DatabaseReference databaseReference;

    private static final int PICK_IMG = 1;
    private ArrayList<Uri> imageList = new ArrayList<Uri>();
    private int uploads = 0;
    int index = 0;

    ImageAdapter adapter;

    private Uri filePath;
    private String imageLink = "";
    byte[] Data;
    private final int PICK_IMAGE_REQUEST = 22;
    private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 101;

    Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);


        main = findViewById(R.id.main);
        btnContinue = findViewById(R.id.btn_continue);
        userImage = findViewById(R.id.user_image);
        selectImage = findViewById(R.id.select_image);

        nameLayout = findViewById(R.id.name_layout);
        emailLayout = findViewById(R.id.email_layout);
        dobLayout = findViewById(R.id.user_dob_layout);
        contactLayout = findViewById(R.id.contact_layout);
        genderLayout = findViewById(R.id.gender_layout);
        countryLayout = findViewById(R.id.country_layout);
        workLocationLayout = findViewById(R.id.city_layout);
        addressLayout = findViewById(R.id.address_layout);
        identityLayout = findViewById(R.id.identity_layout);
        stateLayout = findViewById(R.id.state_layout);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.user_dob);
        contact = findViewById(R.id.contact);
        gender = findViewById(R.id.gender);
        country = findViewById(R.id.country);
        state = findViewById(R.id.state);
        workLocation = findViewById(R.id.city);
        address = findViewById(R.id.address);
        identityType = findViewById(R.id.identity);
        recyclerView = findViewById(R.id.recycler_view);
        upload = findViewById(R.id.upload);


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
                String data = documentSnapshot.getString("rider_email");
                email.setText(data);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose();
            }
        });


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AccountSetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Request the permission
                    ActivityCompat.requestPermissions(AccountSetupActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_READ_EXTERNAL_STORAGE);
                }else {
                    selectImage();
                }
            }
        });


        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AccountSetupActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog genderPopup = new Dialog(AccountSetupActivity.this);
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

                state.setText("");
                workLocation.setText("");
                Dialog countryPopup = new Dialog(AccountSetupActivity.this);
                countryPopup.setContentView(R.layout.popup_country);
                countryPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ListView countryList = countryPopup.findViewById(R.id.country_list);
                countryPopup.show();



                db.collection("Countries").orderBy("name", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String[] countries;
                            List<String> locationData = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String data = document.getString("name");
                                locationData.add(data);
                            }
                            countries = locationData.toArray(new String[0]);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AccountSetupActivity.this,
                                    R.layout.country_list_layout,R.id.text, countries);
                            countryList.setAdapter(adapter);

                            countryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedCountry = parent.getItemAtPosition(position).toString();
                                    country.setText(selectedCountry);
                                    countryPopup.dismiss();
                                }
                            });

                        } else {
                            tools.logMessage("DeliveryAddressActivity", task.getException().toString());
                        }
                    }
                });


            }
        });

        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                workLocation.setText("");

                if (!country.getText().toString().equals("")){
                    Dialog statePopup = new Dialog(AccountSetupActivity.this);
                    statePopup.setContentView(R.layout.popup_state);
                    statePopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    ListView stateList = statePopup.findViewById(R.id.state_list);
                    statePopup.show();



                    db.collection("Countries").document(country.getText().toString()).collection("States").orderBy("name", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String[] countries;
                                List<String> locationData = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String data = document.getString("name");
                                    locationData.add(data);
                                }
                                countries = locationData.toArray(new String[0]);

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AccountSetupActivity.this,
                                        R.layout.country_list_layout,R.id.text, countries);
                                stateList.setAdapter(adapter);

                                stateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String selectedState = parent.getItemAtPosition(position).toString();
                                        state.setText(selectedState);
                                        statePopup.dismiss();
                                    }
                                });

                            } else {
                                tools.logMessage("DeliveryAddressActivity", task.getException().toString());
                            }
                        }
                    });

                }else {
                    tools.logMessage("DeliveryAddressActivity", "You need to select your country first");
                }

            }
        });

        workLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!country.getText().toString().equals("")){
                    Dialog cityPopup = new Dialog(AccountSetupActivity.this);
                    cityPopup.setContentView(R.layout.popup_city);
                    cityPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    ListView stateList = cityPopup.findViewById(R.id.city_list);
                    cityPopup.show();



                    db.collection("Countries").document(country.getText().toString())
                            .collection("States").document(state.getText().toString())
                            .collection("Cities").orderBy("name", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String[] countries;
                                List<String> locationData = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String data = document.getString("name");
                                    locationData.add(data);
                                }
                                countries = locationData.toArray(new String[0]);

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AccountSetupActivity.this,
                                        R.layout.country_list_layout,R.id.text, countries);
                                stateList.setAdapter(adapter);

                                stateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String selectedState = parent.getItemAtPosition(position).toString();
                                        workLocation.setText(selectedState);
                                        cityPopup.dismiss();
                                    }
                                });

                            } else {
                                tools.logMessage("DeliveryAddressActivity", task.getException().toString());
                            }
                        }
                    });

                }else {
                    tools.logMessage("DeliveryAddressActivity", "You need to select your work location first");
                }

            }
        });

        identityType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog identityPopup = new Dialog(AccountSetupActivity.this);
                identityPopup.setContentView(R.layout.popup_identity);
                identityPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                RelativeLayout nid = identityPopup.findViewById(R.id.nid);
                RelativeLayout passport = identityPopup.findViewById(R.id.passport);
                RelativeLayout other = identityPopup.findViewById(R.id.other);
                identityPopup.show();

                nid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nid.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_bg));
                        identityType.setText("National ID");
                        identityPopup.dismiss();
                    }
                });

                passport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passport.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_bg));
                        identityType.setText("Passport");
                        identityPopup.dismiss();
                    }
                });

                other.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        other.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_bg));
                        identityType.setText("Other");
                        identityPopup.dismiss();
                    }
                });
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name = name.getText().toString();
                String Email = email.getText().toString();
                String DOB = dob.getText().toString();
                String Contact = contact.getText().toString();
                String Gender = gender.getText().toString();
                String Country = country.getText().toString();
                String State = state.getText().toString();
                String WorkLocation = workLocation.getText().toString();
                String Address = address.getText().toString();
                String IdentityType = identityType.getText().toString();

                if (Name.isEmpty()) {
                    nameLayout.setError("");
                    tools.makeSnack(main, "Name is required");
                    return;
                }
                if (Email.isEmpty()) {
                    emailLayout.setError("");
                    tools.makeSnack(main, "Email is required");
                    return;
                }
                if (DOB.isEmpty()) {
                    dobLayout.setError("");
                    tools.makeSnack(main, "Death of birth is required");
                    return;
                }
                if (Contact.isEmpty()) {
                    contactLayout.setError("");
                    tools.makeSnack(main, "Contact is required");
                    return;
                } if (Gender.isEmpty()) {
                    genderLayout.setError("");
                    tools.makeSnack(main, "Gender is required");
                    return;
                } if (Country.isEmpty()) {
                    countryLayout.setError("");
                    tools.makeSnack(main, "Country is required");
                    return;
                } if (State.isEmpty()) {
                    stateLayout.setError("");
                    tools.makeSnack(main, "State is required");
                    return;
                } if (WorkLocation.isEmpty()) {
                    workLocationLayout.setError("");
                    tools.makeSnack(main, "Work location is required");
                    return;
                } if (Address.isEmpty()) {
                    addressLayout.setError("");
                    tools.makeSnack(main, "Address is required");
                    return;
                } if (IdentityType.isEmpty()) {
                    identityLayout.setError("");
                    tools.makeSnack(main, "Select an identity type");
                    return;
                } if (filePath == null) {
                    tools.makeSnack(main, "User image is required");
                    return;
                } if (imageList.size()==0) {
                    tools.makeSnack(main, "You must upload identity documents");
                    return;
                }else{

                    tools.loading(popup, true);

                    Map<String, Object> userMap = new HashMap<>();

                    userMap.put("rider_name", Name);
                    userMap.put("rider_dob", DOB);
                    userMap.put("rider_contact", Contact);
                    userMap.put("rider_gender", Gender);
                    userMap.put("rider_country", Country);
                    userMap.put("rider_state", State);
                    userMap.put("rider_address", Address);
                    userMap.put("rider_work_location", WorkLocation);
                    userMap.put("rider_identity_type", IdentityType);
                    userMap.put("user_type", "Rider");
                    userMap.put("rider_status", "Pending");

                    db.collection("riderDetails").document(userID).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            uploadImage(userID);

                        }
                    });


                }
            }
        });
    }


    // Override onActivityResult method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                Data = baos.toByteArray();
                Glide.with(getApplicationContext()).load(bitmap).centerCrop().into(userImage);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_IMG && resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();

                int CurrentImageSelect = 0;

                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                    imageList.add(imageUri);
                    CurrentImageSelect++;
                }
                loadSelectedImages();
            } else {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    imageList.add(imageUri);
                }
                loadSelectedImages();
            }
        }

    }


    // Upload Image method
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
                      tools.makeSnack(main,"Upload successful");

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageLink = uri.toString();
                                Log.d("imageLink", "onSuccess: " + imageLink);
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                DocumentReference docRef = db.collection("riderDetails").document(userID);

                                Map<String, Object> userMap = new HashMap<>();
                                final String id = docRef.getId();

                                userMap.put("rider_image", imageLink);
                                userMap.put("timestamp", FieldValue.serverTimestamp());

                                docRef.update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        upload();

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


    public void choose() {
        //we will pick images
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMG);

    }

    public void loadSelectedImages() {
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new ImageAdapter(imageList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AccountSetupActivity.this,RecyclerView.HORIZONTAL, false ));
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

    @SuppressLint("SetTextI18n")
    public void upload() {
        final StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
        for (uploads = 0; uploads < imageList.size(); uploads++) {

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageList.get(uploads));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            final StorageReference imageName = ImageFolder.child("image/" + System.currentTimeMillis());

            imageName.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            String url = String.valueOf(uri);
                            SendLink(url, index);

                        }
                    });

                }
            });


        }


    }

    private void SendLink(String url, int counter) {
        index++;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("imageLink", FieldValue.arrayUnion(url));


        FirebaseFirestore.getInstance().collection("riderDetails").document(userID).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });

        if (index == imageList.size()) {
            index=0;
            popup.dismiss();
            imageList.clear();
            adapter.notifyDataSetChanged();

            tools.makeSnack(main, getString(R.string.profile_updated));
            popup.setContentView(R.layout.popup_successful);
            popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView message = popup.findViewById(R.id.message);
            TextView actionText = popup.findViewById(R.id.action_text);
            CardView btnContinue = popup.findViewById(R.id.btn_continue);
            popup.show();
            popup.setCancelable(false);

            message.setText(getResources().getString(R.string.account_ready));
            actionText.setText(getResources().getString(R.string.go_to_home));

            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        }


    }

}