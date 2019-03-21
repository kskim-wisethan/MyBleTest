package com.example.mybletest.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.mybletest.adapter.BleItemRecyclerViewAdapter;
import com.example.mybletest.model.BleModel;
import com.example.mybletest.util.BleManager;
import com.example.mybletest.util.CardsDecoration;
import com.example.mybletest.util.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.NavUtils;
import androidx.appcompat.app.ActionBar;

import android.view.MenuItem;

import com.example.mybletest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BleItemListActivity extends AppCompatActivity {
    private final static String TAG = BleItemListActivity.class.getSimpleName();

    public static final String EXTRAS_SCAN_FILTER = "SCAN_FILTER";

    private boolean mTwoPane = false;
    private ArrayList<BleModel> mDevices = new ArrayList<BleModel>();

    private BleManager mBleManager;
    private RecyclerView mRecyclerView;
    private BleItemRecyclerViewAdapter mBleItemRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleitem_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.bleitem_detail_container) != null) {
            mTwoPane = true;
        }

        mBleManager = BleManager.getInstance(this);

        mBleItemRecyclerViewAdapter = new BleItemRecyclerViewAdapter(this, mDevices, mTwoPane);
        mRecyclerView = (RecyclerView) findViewById(R.id.ble_item_list);
        assert mRecyclerView != null;
        setupRecyclerView();

        scanBleDevices();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        mRecyclerView.addItemDecoration(new CardsDecoration((int) getResources().getDimension(R.dimen.header_gap), (int) getResources().getDimension(R.dimen.row_gap)));
        mRecyclerView.setAdapter(mBleItemRecyclerViewAdapter);
    }

    private void scanBleDevices() {
        mBleManager.scanBleDevice(new BleManager.BleDeviceCallback() {
            @Override
            public void onResponse(BleModel model) {
                if (Constants.SCAN_FILTER.isEmpty()) {
                    addDevice(model);

                } else {
                    String id = model.getUuid();
                    if (id.compareTo(Constants.SCAN_FILTER) == 0) {
                        mBleManager.stopScan();
                        addDevice(model);
                    }
                }
            }
        });
    }

    private void addDevice(BleModel model) {
        String id = model.getUuid();
        int index = findDeviceId(id);
        if (index >= 0) {
            mDevices.set(index, model);
            mBleItemRecyclerViewAdapter.notifyItemChanged(index);
        } else {
            mDevices.add(model);
            mBleItemRecyclerViewAdapter.notifyItemChanged(mDevices.size() - 1);
        }
    }

    private int findDeviceId(String id) {
        int ret = -1;
        for (int i = 0; i < mDevices.size(); i++) {
            if (mDevices.get(i).getUuid().compareTo(id) == 0) {
                ret = i;
            }
        }
        return ret;
    }
}
