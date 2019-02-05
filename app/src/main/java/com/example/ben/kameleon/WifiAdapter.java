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

    // Interface for listening for clicks in the recycler view which gets the position of the item clicked
    public  interface OnItemClickListener {
        void  onItemClick(int position);
    }

    // Sets the mListener variable to the position of the item pressed
    public  void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class WifiViewHolder extends RecyclerView.ViewHolder {
        // Variables that hold the icon of the network and the name of the SSID
        public ImageView mImageView;
        public TextView  mWifiName;

        public WifiViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            // Locates the text view by ID in the itemView (Wi-Fi card)
            mImageView = itemView.findViewById(R.id.wifi_image);
            mWifiName = itemView.findViewById(R.id.wifi_name);

            // When a card is pressed, do this:
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        // Returns the position of the item pressed in the RecyclerView
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    // Sets the wifiList passed into the adapter to the variable mWifiList
    public WifiAdapter(ArrayList<WifiItem> wifiList) {
        mWifiList = wifiList;
    }

    // Sets the Wi-Fi card (wifi_item) XML file as the WifiViewHolder
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

        // Sets the card SSID and image to the values configured in the mWifiList
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mWifiName.setText(currentItem.getWifiName());
    }

    // Returns the number of items in the mWifiList
    @Override
    public int getItemCount() {
        return (mWifiList == null) ? 0 : mWifiList.size();
    }
}
