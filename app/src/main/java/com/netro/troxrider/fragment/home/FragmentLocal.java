package com.netro.troxrider.fragment.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.netro.troxrider.R;
import com.netro.troxrider.activity.LocalDeliveryDetailsActivity;
import com.netro.troxrider.adapter.OrderDataAdapter;
import com.netro.troxrider.model.OrderData;
import com.netro.troxrider.util.LinearRecyclerDecoration;
import com.netro.troxrider.util.Tools;

public class FragmentLocal extends Fragment {

    Dialog popup;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    public String item_id;

    private String userID;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference localData;

    String Country, State, City, Status;

    OrderDataAdapter adapter;

    Tools tools;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_local, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        int topPadding = getResources().getDimensionPixelSize(R.dimen.topPadding);
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.bottomPadding);
        int sidePadding = getResources().getDimensionPixelSize(R.dimen.sidePadding);
        recyclerView.addItemDecoration(new LinearRecyclerDecoration(topPadding, bottomPadding, sidePadding));


        tools = new Tools();

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        db = FirebaseFirestore.getInstance();

        db.collection("riderDetails").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Status = documentSnapshot.getString("rider_status");
                    Country = documentSnapshot.getString("rider_country");
                    State = documentSnapshot.getString("rider_state");
                    City = documentSnapshot.getString("rider_work_location");

                    if (Status.equals("Approved")) {
                        loadData(Country, State, City);
                    }else {

                    }

                }
            }
        });


        return view;
    }

    private void loadData(String country, String state, String city) {

        localData = db.collection("orders");


        Query query = localData.whereEqualTo("order_status", "Approved").whereEqualTo("sender_city",city).orderBy("timestamp", Query.Direction.ASCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(15)
                .setPageSize(15)
                .build();

        FirestorePagingOptions<OrderData> options = new FirestorePagingOptions.Builder<OrderData>()
                .setQuery(query, config, OrderData.class)
                .build();

        adapter = new OrderDataAdapter(options, swipeRefreshLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });

        adapter.setOnItemClickListener(new OrderDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                item_id = documentSnapshot.getId();
                OrderData orderData = documentSnapshot.toObject(OrderData.class);
                assert orderData != null;
                String status = orderData.getOrder_status();

                Intent intent = new Intent(getContext(), LocalDeliveryDetailsActivity.class);
                intent.putExtra("status", status);
                intent.putExtra("order_id", item_id);
                startActivity(intent);

            }
        });

    }


}
