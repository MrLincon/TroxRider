package com.netro.troxrider.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netro.troxrider.R;
import com.netro.troxrider.model.OrderData;

public class OrderDataAdapter extends FirestorePagingAdapter<OrderData, OrderDataAdapter.OrderDataHolder> {

    private OnItemClickListener listener;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public OrderDataAdapter(@NonNull FirestorePagingOptions<OrderData> options, SwipeRefreshLayout swipeRefreshLayout) {
        super(options);
        this.mSwipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull final OrderDataHolder holder, int position, @NonNull OrderData model) {
        final String post_id = getItem(position).getId();
        final String user_id = firebaseAuth.getCurrentUser().getUid();

        holder.Name.setText(model.getSender_name());
        holder.Address.setText(model.getSender_address());
        holder.Contact.setText(model.getSender_contact());
        holder.OrderID.setText("Order ID#" + model.getOrder_id());
        holder.OrderStatus.setText(model.getOrder_status());


        if (model.getOrder_status().equals("Pending")) {
            holder.statusCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLighter));
            holder.OrderStatus.setTextColor(mContext.getResources().getColor(R.color.colorBlackHighEmp));
        } else if (model.getOrder_status().equals("Processing")) {
            holder.statusCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLightest));
        } else if (model.getOrder_status().equals("Picked Up")) {
            holder.statusCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorSecondaryLightest));
        } else if (model.getOrder_status().equals("Delivered")) {
            holder.statusCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorStatusSuccess));
            holder.OrderStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhiteHighEmp));
        } else if (model.getOrder_status().equals("Returned")) {
            holder.statusCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorStatusError));
            holder.OrderStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhiteHighEmp));
        }

    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state) {

            case LOADING_INITIAL:
                mSwipeRefreshLayout.setRefreshing(true);
                Log.d("Paging Log", "Loading Initial data");
                break;
            case LOADING_MORE:
                mSwipeRefreshLayout.setRefreshing(true);
                Log.d("Paging Log", "Loading next page");
                break;
            case FINISHED:
                mSwipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log", "All data loaded");
                break;
            case LOADED:
                mSwipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log", "Total data loaded " + getItemCount());
                break;
            case ERROR:
                mSwipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log", "Error loading data");
                break;
        }
    }


    @NonNull
    @Override
    public OrderDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,
                parent, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new OrderDataHolder(view);
    }

    class OrderDataHolder extends RecyclerView.ViewHolder {

        TextView Name, Address, Contact, OrderID, OrderStatus;
        CardView statusCard;

        public OrderDataHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Address = itemView.findViewById(R.id.address);
            Contact = itemView.findViewById(R.id.contact);
            OrderID = itemView.findViewById(R.id.order_id);
            OrderStatus = itemView.findViewById(R.id.status);
            statusCard = itemView.findViewById(R.id.status_card);

            mContext = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

