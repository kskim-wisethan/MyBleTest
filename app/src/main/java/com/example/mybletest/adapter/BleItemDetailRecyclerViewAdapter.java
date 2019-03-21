package com.example.mybletest.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mybletest.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class BleItemDetailRecyclerViewAdapter extends RecyclerView.Adapter<BleItemDetailRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = BleItemDetailRecyclerViewAdapter.class.getSimpleName();

    private ArrayList<String> mList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDetailView;

        public ViewHolder(View itemView) {
            super(itemView);

            mDetailView = (TextView) itemView.findViewById(R.id.ble_content_detail);
        }
    }

    public BleItemDetailRecyclerViewAdapter(ArrayList<String> list) {
        mList = list;
    }

    @Override
    public BleItemDetailRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.bleitem_detail_line, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TextView textView = viewHolder.mDetailView;
        textView.setText(mList.get(position));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: item " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

