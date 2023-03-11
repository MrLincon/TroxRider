package com.netro.troxrider.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.netro.trox.R;
import com.netro.trox.adapter.ImageAdapter;
import com.netro.trox.util.Tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetupActivity extends AppCompatActivity {

    CoordinatorLayout main;
    CardView btnContinue;

    CircleImageView userImage;
    ImageView selectImage;
    RecyclerView recyclerView;

    TextInputLayout nameLayout, emailLayout,
            contactLayout, countryLayout, addressLayout;

    TextInputEditText name, email, contact, country, address;

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
        setContentView(R.layout.activity_account_setup_merchant);

        main = findViewById(R.id.main);
        btnContinue = findViewById(R.id.btn_continue);
        userImage = findViewById(R.id.user_image);
        selectImage = findViewById(R.id.select_image);
        nameLayout = findViewById(R.id.name_layout);
        emailLayout = findViewById(R.id.email_layout);
        contactLayout = findViewById(R.id.contact_layout);
        countryLayout = findViewById(R.id.country_layout);
        addressLayout = findViewById(R.id.address_layout);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        country = findViewById(R.id.country);
        address = findViewById(R.id.address);
        recyclerView = findViewById(R.id.recycler_view);
        upload = findViewById(R.id.upload);


        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        popup = new Dialog(this);
        tools = new Tools();

        db.collection("userDetails").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String data = documentSnapshot.getString("user_email");
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
                if (ContextCompat.checkSelfPermission(AccountSetupMerchantActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Request the permission
                    ActivityCompat.requestPermissions(AccountSetupMerchantActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_READ_EXTERNAL_STORAGE);
                }else {
                    selectImage();
                }
            }
        });


        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog countryPopup = new Dialog(AccountSetupMerchantActivity.this);
                countryPopup.setContentView(R.layout.popup_country);
                countryPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ListView countryList = countryPopup.findViewById(R.id.country_list);
                countryPopup.show();

                String[] countries = {"Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas",
                        "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia", "Cameroon", "Canada",
                        "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo, Democratic Republic of the", "Congo, Republic of the", "Costa Rica", "Cote d'Ivoire", "Croatia", "Cuba", "Cyprus",
                        "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kosovo", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius",
                        "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar (Burma)", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Korea", "North Macedonia", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru"};


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AccountSetupMerchantActivity.this,
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

            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString();
                String Contact = contact.getText().toString();
                String Country = country.getText().toString();
                String Address = address.getText().toString();

                if (!Name.isEmpty() && !Address.isEmpty() && !Contact.isEmpty()
                        && !Country.isEmpty() && filePath != null && imageList.size()!=0) {

                    tools.loading(popup, true);

                    Map<String, Object> userMap = new HashMap<>();

                    userMap.put("user_name", Name);
                    userMap.put("user_contact", Contact);
                    userMap.put("user_country", Country);
                    userMap.put("user_address", Address);
                    userMap.put("user_type", "Merchant");

                    db.collection("userDetails").document(userID).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            uploadImage(userID);

                        }
                    });


                } else {
                    tools.makeSnack(main, getString(R.string.fill_all_the_fields));

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

                                DocumentReference docRef = db.collection("userDetails").document(userID);

                                Map<String, Object> userMap = new HashMap<>();
                                final String id = docRef.getId();

                                userMap.put("user_image", imageLink);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(AccountSetupMerchantActivity.this,RecyclerView.HORIZONTAL, false ));
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


        FirebaseFirestore.getInstance().collection("userDetails").document(userID).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
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