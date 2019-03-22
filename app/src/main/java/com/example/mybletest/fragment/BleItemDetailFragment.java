package com.example.mybletest.fragment;

import android.app.Activity;

import com.example.mybletest.adapter.BleItemDetailRecyclerViewAdapter;
import com.example.mybletest.adapter.BleItemRecyclerViewAdapter;
import com.example.mybletest.model.BleModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mybletest.R;

import java.util.ArrayList;


public class BleItemDetailFragment extends Fragment {
    private static final String TAG = BleItemDetailFragment.class.getSimpleName();

    public static final String BLE_ID = "BLE_ID";
    public static final String BLE_NAME = "BLE_NAME";
    public static final String BLE_ITEM = "BLE_ITEM";

    private String mId;
    private String mName;
    private BleModel mItem;
    ArrayList<String> mList = new ArrayList<String>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public BleItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(BLE_ID)) {
            mId = getArguments().getString(BLE_ID);
            mName = getArguments().getString(BLE_NAME);
            mItem = (BleModel) getArguments().getSerializable(BLE_ITEM);

            //
            mList.add("Device ID : " + mItem.getUuid());
            mList.add("Device Name : " + mItem.getName());
            mList.add("Rssi : " + mItem.getRssi());
            mList.add("Description : " + mItem.getDescription());
            mList.add("Scan Record : \n" + mItem.getScanRecord());

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getName());
                appBarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
                appBarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bleitem_detail, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.ble_item_detail_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new BleItemDetailRecyclerViewAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public void setList(ArrayList<String> list) {
        mList = list;
        //
    }

    public void addList(String data) {
        mList.add(data);
        mAdapter.notifyItemChanged(mList.size() - 1);
    }
}
