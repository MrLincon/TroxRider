package com.netro.troxrider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.netro.troxrider.R;
import com.netro.troxrider.model.LocationList;

import java.util.List;


public class LocationListAdpater extends RecyclerView.Adapter<LocationListAdpater.ViewHolder> {

    private List<LocationList> mData;
    private Context context;

    private OnItemClickListener listener;

    public LocationListAdpater(List<LocationList> data, Context context) {
        mData = data;
        context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Name;

        public ViewHolder(View itemView) {
            super(itemView);
            Name = (TextView) itemView.findViewById(R.id.name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationList item = mData.get(position);
        holder.Name.setText(item.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(item.getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String value);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}