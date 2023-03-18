package com.netro.troxrider.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netro.troxrider.R;
import com.netro.troxrider.util.Tools;

import java.util.HashMap;
import java.util.Map;

public class LongIntDeliveryDetailsActivity extends AppCompatActivity {

    ImageView back;

    CoordinatorLayout main;

    CardView btnAccept;

    TextView  senderName, senderContact, senderAddress, receiverName, receiverContact, receiverAddress, parcelWeight, parcelPrice, orderID;

    String data, order_id;

    Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_int_delivery_details);

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
        btnAccept = findViewById(R.id.btn_accept);

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
                            String OrdeID = documentSnapshot.getString("order_id");


                            senderName.setText(SenderName);
                            senderContact.setText(SenderContact);
                            senderAddress.setText(SenderAddress);
                            receiverName.setText(ReceiverName);
                            receiverContact.setText(ReceiverContact);
                            receiverAddress.setText(ReceiverAddress);
                            parcelWeight.setText(ParcelWeight+" KG");
                            parcelPrice.setText("$"+Price);
                            orderID.setText(OrdeID);
                        }
                    }
                });


        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog popup = new Dialog(LongIntDeliveryDetailsActivity.this);
                popup.setContentView(R.layout.popup_action);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView message = popup.findViewById(R.id.message);
                CardView btnAccept = popup.findViewById(R.id.btn_accept);
                LinearLayout btnCancel = popup.findViewById(R.id.btn_cancel);
                popup.show();
                popup.setCancelable(false);

                message.setText("You are about to accept the delivery order. Make sure you have read the description.");

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });

                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        btnAccept.setFocusable(false);

                        Map<String, Object> userMap = new HashMap<>();

                        userMap.put("assigned_rider_id", FirebaseAuth.getInstance().getUid());
                        userMap.put("delivery_status","Picking Up");
                        userMap.put("order_status","Processing");

                        FirebaseFirestore.getInstance().collection("orders").document(order_id)
                                .update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        popup.dismiss();
                                        Toast.makeText(LongIntDeliveryDetailsActivity.this, "Check your history to see the details", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LongIntDeliveryDetailsActivity.this,MainActivity.class));
                                        finish();
                                    }
                                });
                    }
                });

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