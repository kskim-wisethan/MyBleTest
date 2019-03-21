package com.example.mybletest.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mybletest.R;
import com.example.mybletest.activity.BleItemDetailActivity;
import com.example.mybletest.activity.BleItemListActivity;
import com.example.mybletest.fragment.BleItemDetailFragment;
import com.example.mybletest.model.BleModel;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class BleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<BleItemRecyclerViewAdapter.ViewHolder> {

    private final BleItemListActivity mParentActivity;
    private List<BleModel> mItems;
    private boolean mTwoPane = false;

    private final View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            BleModel item = (BleModel) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(BleItemDetailFragment.BLE_ID, item.getUuid());
                arguments.putString(BleItemDetailFragment.BLE_NAME, item.getName());
                arguments.putSerializable(BleItemDetailFragment.BLE_ITEM, item);
                BleItemDetailFragment fragment = new BleItemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.bleitem_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, BleItemDetailActivity.class);
                intent.putExtra(BleItemDetailFragment.BLE_ID, item.getUuid());
                intent.putExtra(BleItemDetailFragment.BLE_NAME, item.getName());
                intent.putExtra(BleItemDetailFragment.BLE_ITEM, item);
                context.startActivity(intent);
            }
        }
    };

    public BleItemRecyclerViewAdapter(BleItemListActivity parent, List<BleModel> items, boolean twoPane) {
        mItems = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mAddress.setText(mItems.get(position).getUuid());
        holder.mRssi.setText(mItems.get(position).getRssi());
        holder.mName.setText(mItems.get(position).getName());
        holder.mIcon.setImageResource(R.mipmap.ic_launcher_round);

        holder.itemView.setTag(mItems.get(position));
        holder.itemView.setOnClickListener(mOnItemClickListener);
        holder.mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mParentActivity, BleItemDetailActivity.class);
                intent.putExtra(BleItemDetailFragment.BLE_ID, mItems.get(position).getUuid());
                intent.putExtra(BleItemDetailFragment.BLE_NAME, mItems.get(position).getName());
                intent.putExtra(BleItemDetailFragment.BLE_ITEM, mItems.get(position));
                mParentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mAddress;
        final TextView mRssi;
        final TextView mName;
        final ImageView mIcon;
        final Button mConnect;

        ViewHolder(View view) {
            super(view);

            mAddress = (TextView) view.findViewById(R.id.text_item_address);
            mRssi = (TextView) view.findViewById(R.id.text_item_rssi);
            mName = (TextView) view.findViewById(R.id.text_item_name);
            mIcon = (ImageView) view.findViewById(R.id.image_item_icon);
            mConnect = (Button) view.findViewById(R.id.button_connect);
        }
    }
}
