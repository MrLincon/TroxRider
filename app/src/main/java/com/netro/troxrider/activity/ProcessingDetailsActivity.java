package com.netro.troxrider.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netro.troxrider.R;
import com.netro.troxrider.util.Tools;

import java.util.HashMap;
import java.util.Map;

public class ProcessingDetailsActivity extends AppCompatActivity {

    ImageView back;

    CoordinatorLayout main;

    CardView btnStartPickup;

    TextView senderName, senderContact, senderAddress, receiverName, receiverContact, receiverAddress, parcelWeight, parcelPrice, orderID, deliveryFee;

    String data, order_id;

    Tools tools;


    private static final int REQUEST_CODE = 1011;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_details);

        back = findViewById(R.id.back);
        main = findViewById(R.id.main);
        senderName = findViewById(R.id.sender_name);
        senderContact = findViewById(R.id.sender_contact);
        senderAddress = findViewById(R.id.sender_address);
        receiverName = findViewById(R.id.receiver_name);
        receiverContact = findViewById(R.id.receiver_contact);
        receiverAddress = findViewById(R.id.receiver_address);
        parcelWeight = findViewById(R.id.parcel_weight);
        parcelPrice = findViewById(R.id.parcel_price);
        orderID = findViewById(R.id.order_id_text);
        btnStartPickup = findViewById(R.id.btn_start_pickup);
        deliveryFee = findViewById(R.id.delivery_fee);

        tools = new Tools();

        tools.setLightStatusBar(main, this);


        data = getIntent().getStringExtra("data");
        order_id = getIntent().getStringExtra("order_id");


        FirebaseFirestore.getInstance().collection("orders").document(order_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String SenderName = documentSnapshot.getString("sender_name");
                            String SenderContact = documentSnapshot.getString("sender_contact");
                            String SenderAddress = documentSnapshot.getString("sender_address");
                            String ReceiverName = documentSnapshot.getString("receiver_name");
                            String ReceiverContact = documentSnapshot.getString("receiver_contact");
                            String ReceiverAddress = documentSnapshot.getString("receiver_address");
                            long ParcelWeight = documentSnapshot.getLong("parcel_weight");
                            Long Price = documentSnapshot.getLong("price");
                            Long TotalPrice = documentSnapshot.getLong("total_price");
                            String OrdeID = documentSnapshot.getString("order_id");

                            Long DeliveryFee = TotalPrice-Price;

                            senderName.setText(SenderName);
                            senderContact.setText(SenderContact);
                            senderAddress.setText(SenderAddress);
                            receiverName.setText(ReceiverName);
                            receiverContact.setText(ReceiverContact);
                            receiverAddress.setText(ReceiverAddress);
                            parcelWeight.setText(ParcelWeight+" KG");
                            parcelPrice.setText("$"+Price);
                            orderID.setText(OrdeID);

                            deliveryFee.setText("$"+DeliveryFee);
                        }
                    }
                });

        btnStartPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ProcessingDetailsActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;


                    Dialog popup = new Dialog(ProcessingDetailsActivity.this);
                    popup.setContentView(R.layout.popup_confirm);
                    popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView message = popup.findViewById(R.id.message);
                    TextView actionText = popup.findViewById(R.id.action_text);
                    CardView btnContinue = popup.findViewById(R.id.btn_continue);
                    popup.show();
                    popup.setCancelable(false);

                    message.setText("You are about to start pickup process");
                    actionText.setText(getResources().getString(R.string.continue_));

                    btnContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popup.dismiss();
                            Intent intent = new Intent(ProcessingDetailsActivity.this, MapActivityPickedUp.class);
                            intent.putExtra("order_id",order_id);
                            startActivity(intent);
                        }
                    });

                } else {
                    ActivityCompat.requestPermissions(ProcessingDetailsActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}