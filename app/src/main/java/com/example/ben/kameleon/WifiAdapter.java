package com.example.ben.kameleon;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiViewHolder> {

    private ArrayList<WifiItem> mWifiList;
    private OnItemClickListener mListener;

    public  interface OnItemClickListener {
        void  onItemClick(int position);
    }

    public  void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class WifiViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView  mWifiName;

        public WifiViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.wifi_image);
            mWifiName = itemView.findViewById(R.id.wifi_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public WifiAdapter(ArrayList<WifiItem> wifiList) {
        mWifiList = wifiList;
    }

    @NonNull
    @Override
    public WifiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_item, parent, false);
        WifiViewHolder wvh = new WifiViewHolder(v, mListener);
        return wvh;
    }

    @Override
    public void onBindViewHolder(@NonNull WifiViewHolder holder, int position) {
        WifiItem currentItem = mWifiList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mWifiName.setText(currentItem.getWifiName());
    }

    @Override
    public int getItemCount() {
        return mWifiList.size();
    }
}
