/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.armor.brelo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.armor.brelo.ui.LockFragment;
import com.armor.brelo.utils.MessageUtil;

/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data,
 * and display GATT services and characteristics supported by the device. The
 * Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with
 * the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends FragmentActivity implements LockFragment.OnLockChangeListener{
	private final static String TAG = DeviceControlActivity.class.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private String mDeviceName;
	private String mDeviceAddress;
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mNotifyCharacteristic;

	private int currentLockIndex = 3;
	private static int DELAYui = 5000;					// 2000 milliseconds

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	private LockFragment mLockFragment;

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
			mBluetoothLeService.connect(mDeviceAddress);
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
				mConnected = true;
				updateConnectionState(R.string.connected);
				invalidateOptionsMenu();
				mBluetoothLeService.getLockStatus();

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				updateConnectionState(R.string.disconnected);
				invalidateOptionsMenu();
				clearUI();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				mBluetoothLeService.getLockStatus();
				// displayGattServices(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				byte data = intent.getByteExtra(BluetoothLeService.EXTRA_DATA, (byte) 0);
				Log.e(TAG, "Read characteristing data  " + data);
				currentLockIndex = data;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mLockFragment.updateLockUI(currentLockIndex);
					}
				});
			}
		}
	};


	protected void onStop() {
		super.onStop();
		mBluetoothLeService.disconnect();
	};

	private void clearUI() {
		// mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
		// mDataField.setText(R.string.no_data);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock_handler);

		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

		mLockFragment = (LockFragment) getSupportFragmentManager().findFragmentById(R.id.btn_lock);
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ArmorService.NOTIFICATION_ID);
		if (getIntent().getBooleanExtra("NOTIFICATION", false))
			ArmorService.isNotificationActive = false;
//		getActionBar().setTitle(mDeviceName);
//		getActionBar().setDisplayHomeAsUpEnabled(true);
		mLockFragment.updateLockUI(currentLockIndex);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
		/*Intent serviceIntent = new Intent(SettingsActivity.this, ArmorService.class);
		// Start service
		startService(serviceIntent);*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gatt_services, menu);
		/*
		 * if (mConnected) {
		 * menu.findItem(R.id.menu_connect).setVisible(false);
		 * menu.findItem(R.id.menu_disconnect).setVisible(true);
		 * } else {
		 * menu.findItem(R.id.menu_connect).setVisible(true);
		 * menu.findItem(R.id.menu_disconnect).setVisible(false);
		 * }
		 */
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*
		 * case R.id.menu_connect:
		 * mBluetoothLeService.connect(mDeviceAddress);
		 * return true;
		 */
		case R.id.menu_settings:
			// mBluetoothLeService.disconnect();
			Intent intent = new Intent(DeviceControlActivity.this, SettingsActivity.class);
			startActivity(intent);

			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// mConnectionState.setText(resourceId);
			}
		});
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	private void sendUnlockCommand(int index) {
		try {
			byte[] command = MessageUtil.getOpData(currentLockIndex, index);
			mBluetoothLeService.sendCommand(command);
			currentLockIndex = index;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLockChanged(int position) {

	}
}
