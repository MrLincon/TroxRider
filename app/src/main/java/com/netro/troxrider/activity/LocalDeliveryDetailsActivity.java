package com.netro.troxrider.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netro.troxrider.R;
import com.netro.troxrider.util.Tools;

public class LocalDeliveryDetailsActivity extends AppCompatActivity {

    ImageView back;

    CoordinatorLayout main;

    CardView btnAccept;

    TextView  senderName, senderContact, senderAddress, receiverName, receiverContact, receiverAddress, parcelWeight, parcelPrice, orderID;

    String data, order_id;

    Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_delivery_details);

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