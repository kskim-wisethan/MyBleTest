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

import com.example.mybletest.activity.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of BleItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BleItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BleItemListActivity extends AppCompatActivity {
    private final static String TAG = BleItemListActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private boolean mTwoPane = false;
    private int mScanCount = 0;
    private String mDeviceName = "";
    private String mDeviceAddress = "";

    private ArrayList<BleModel> mDevices = new ArrayList<BleModel>();
    private RecyclerView mRecyclerView;
    private BleItemRecyclerViewAdapter mBleItemRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleitem_list);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

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
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
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
        BleManager bm = BleManager.getInstance(this);
        bm.scanBleDevice(new BleManager.BleDeviceCallback() {
            @Override
            public void onResponse(BleModel model) {
                String id = model.getUuid();
                if (id.compareTo("C4:64:E3:F0:2E:65") == 0) {
                    ++mScanCount;
                }

                if (mScanCount > 1) {
                    bm.stopScan();
                    //mScanCount = 0;
                } else {
                    mDevices.add(model);
                    mBleItemRecyclerViewAdapter.notifyItemChanged(mDevices.size() - 1);
                }
            }
        });
    }

}
