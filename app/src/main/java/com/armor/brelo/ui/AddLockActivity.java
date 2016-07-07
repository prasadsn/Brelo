package com.armor.brelo.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.armor.brelo.R;
import com.armor.brelo.controller.LockManager;

public class AddLockActivity extends AppCompatActivity {

    private String mLockName;
    private String mLockMacAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lock);
        mLockName = getIntent().getStringExtra("deviceName");
        mLockMacAddress = getIntent().getStringExtra("deviceAddr");
        ((EditText) findViewById(R.id.edit_text_lock_name)).setText(mLockName);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText lockName = (EditText) findViewById(R.id.edit_text_lock_name);
                EditText fromTime = (EditText) findViewById(R.id.edit_text_from_time);
                EditText toTime = (EditText) findViewById(R.id.edit_text_to_time);
                Switch nightModeEnabled = (Switch) findViewById(R.id.switch_night_mode);
                Switch autoUnlockEnabled = (Switch) findViewById(R.id.switch_auto_lock);
                LockManager.addLock(mLockMacAddress, lockName.getText().toString(), fromTime.getText().toString(), toTime.getText().toString(), nightModeEnabled.isChecked(), autoUnlockEnabled.isChecked());
                finish();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

}
