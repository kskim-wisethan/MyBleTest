package com.example.mybletest.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mybletest.fragment.BleItemDetailFragment;
import com.example.mybletest.model.BleModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.view.View;
import android.view.MenuItem;

import com.example.mybletest.R;


public class BleItemDetailActivity extends AppCompatActivity {

    BleItemDetailFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleitem_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(BleItemDetailFragment.BLE_ID, getIntent().getStringExtra(BleItemDetailFragment.BLE_ID));
            arguments.putString(BleItemDetailFragment.BLE_NAME, getIntent().getStringExtra(BleItemDetailFragment.BLE_NAME));
            arguments.putSerializable(BleItemDetailFragment.BLE_ITEM, getIntent().getSerializableExtra(BleItemDetailFragment.BLE_ITEM));

            mFragment = new BleItemDetailFragment();
            mFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.bleitem_detail_container, mFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, BleItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
