package com.example.mybletest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.mybletest.activity.BleItemListActivity;
import com.example.mybletest.model.BleModel;
import com.example.mybletest.service.BluetoothLeService;
import com.example.mybletest.util.BleManager;
import com.example.mybletest.util.PermissionManager;
import com.example.mybletest.util.StringUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;
    private static final long SCAN_PERIOD = 10000;

    private boolean mBleScanning = false;
    private boolean mConnected = false;
    private int mScanCount = 0;

    private Handler mBleHandler = new Handler();
    private String mOutput = "";

    BluetoothAdapter mBluetoothAdapter;
    BluetoothGatt mGatt;

    TextView mHelloText;

    public UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PermissionManager permissionManager = new PermissionManager(this);
        permissionManager.permissionCheck();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mHelloText = (TextView) findViewById(R.id.hello_text);

        //checkBleEnable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkBleEnable();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_bluetooth) {
            goBleScan();

        } else if (id == R.id.nav_scanble) {
            goBleListActivity();

        } else if (id == R.id.nav_information) {
            Toast.makeText(this, "Information", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_manage) {
            Toast.makeText(this, "Options", Toast.LENGTH_SHORT).show();
            scanBleDevice(false);

        } else if (id == R.id.nav_connections) {
            Toast.makeText(this, "Connections", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_data) {
            Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mBleDeviceListAdapter.addDevice(device);
                    //mBleDeviceListAdapter.notifyDataSetChanged();

                    // temp
                    //mOutput += String.format("name: %s, uuid: %s, addr: %s \n", device.getName(), device.getUuids().toString(), device.getAddress());
                    String address = device.getAddress();
                    if (address.compareTo("C4:64:E3:F0:2E:65") == 0) {
                        ++mScanCount;
                        String name = (device.getName() == null) ? getString(R.string.unknown_device) : device.getName();
                        String hexmessage = StringUtils.byteArrayInHexFormat(scanRecord);
                        mOutput = String.format("count: %d\nDevice name: %s\nDevice address: %s\nScan data: %s\n", mScanCount, name, address, hexmessage);
                        mHelloText.setText(mOutput);
                        scanBleDevice(false);
                    }
                }
            });
        }
    };

    private void scanBleDevice(final boolean enable) {
        checkBleEnable();

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mBleHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBleScanning = false;
                    mBluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            mBleScanning = true;
            mBluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mBleScanning = false;
            mBluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    private void checkBleEnable() {
        // BLE permissions
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        } else {
            // Set up BLE
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void connectDevice(BluetoothDevice device) {
        GattClientCallback gattClientCallback = new GattClientCallback();
        mGatt = device.connectGatt(this, false, gattClientCallback);
    }

    private class GattClientCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_FAILURE) {
                disconnectGattServer();
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                disconnectGattServer();
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnected = true;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                disconnectGattServer();
            }
        }
    }

    public void disconnectGattServer() {
        mConnected = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }

    private void goBleScan() {
        BleManager bm = BleManager.getInstance(this);
        bm.scanBleDevice(new BleManager.BleDeviceCallback() {
            @Override
            public void onResponse(BleModel model) {
                String id = model.getUuid();
                if (id.compareTo("C4:64:E3:F0:2E:65") == 0) {
                    ++mScanCount;
                    String name = (model.getName() == null) ? getString(R.string.unknown_device) : model.getName();
                    String record = model.getScanRecord();
                    mOutput = String.format("count: %d\nDevice name: %s\nDevice address: %s\nScan record: %s\n", mScanCount, name, id, record);
                    mHelloText.setText(mOutput);
                    bm.stopScan();
                }
            }
        });
    }

    private void goBleListActivity() {
        Intent intent = new Intent(this, BleItemListActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra("ParentClassSource", MainActivity.class.getName());
            startActivity(intent);
        }
    }
}
