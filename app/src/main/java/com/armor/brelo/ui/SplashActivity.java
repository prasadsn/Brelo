package com.armor.brelo.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.armor.brelo.BluetoothLeService;
import com.armor.brelo.BreloApplication;
import com.armor.brelo.R;
import com.armor.brelo.controller.LockManager;
import com.armor.brelo.db.model.Lock;
import com.armor.brelo.utils.ApplicationSettings;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SplashActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final long SCAN_PERIOD = 10000;
    private Handler mHandler;
    private BluetoothLeService mBluetoothLeService;
    private static final String TAG = "SplashActivity";
    private ArrayList<String> mAddressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Realm.getDefaultInstance().beginTransaction();
        List<Lock> locks = LockManager.getAllLocks();
        for (Lock lock : locks) {
            lock.setInProximity(false);
        }
        LockManager.updateLocks(locks);
        Realm.getDefaultInstance().commitTransaction();
        mHandler = new Handler();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        scanLeDevice();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
                if(mAddressList.size() == 0) {
                    Intent intent1 = new Intent(SplashActivity.this, SignUpActivity.class);
                    startActivity(intent1);
                    finish();
                } else {
                    Intent gattServiceIntent = new Intent(SplashActivity.this, BluetoothLeService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                }
            }
        }, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void scanLeDevice() {
        boolean flag = mBluetoothAdapter.startLeScan(mLeScanCallback);
        Log.d(TAG, "Scan successful status : " + flag);
    }

    private void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();
                    Lock lock = LockManager.getLock(deviceAddress);
                    if(lock != null && !mAddressList.contains(deviceAddress) && !deviceName.equals("SOS"))
                        mAddressList.add(deviceAddress);
                }
            });
        }
    };
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mAddressList.get(0));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read
    // or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                invalidateOptionsMenu();
                mBluetoothLeService.getLockStatus();

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                mBluetoothLeService.getLockStatus();
                // displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte data = intent.getByteExtra(BluetoothLeService.EXTRA_DATA, (byte) 0);
                Log.e(TAG, "Read characteristing data  " + data);
                Realm.getDefaultInstance().beginTransaction();
                Lock lock = LockManager.getLock(mAddressList.get(0));
                lock.setInProximity(true);
                lock.setLockStatus(data);
                LockManager.updateLock(lock);
                Realm.getDefaultInstance().commitTransaction();
                mAddressList.remove(0);
                mBluetoothLeService.disconnect();
                unbindService(mServiceConnection);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(mAddressList.size() == 0) {
                    Intent intent1 = new Intent(SplashActivity.this, SignUpActivity.class);
                    startActivity(intent1);
                    finish();
                } else {
                    Intent gattServiceIntent = new Intent(SplashActivity.this, BluetoothLeService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                }
//                ApplicationSettings.BreloSharedPreferences.getInstance().putInt(ApplicationSettings.PREF_KEY_LOCK_STATUS, data, SplashActivity.this);
            }
        }
    };

    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
