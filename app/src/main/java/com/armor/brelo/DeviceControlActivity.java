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
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
public class DeviceControlActivity extends Activity implements OnClickListener {
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
						updateLockUI(currentLockIndex);
					}
				});
/*
				LinearLayout layout = (LinearLayout) findViewById(R.id.lock_pointer_layout);
				updateUi(data);
				for (int i = 0; i < layout.getChildCount(); i++)
					layout.getChildAt(i).setOnClickListener(DeviceControlActivity.this);
				layout = (LinearLayout) findViewById(R.id.lock_layout);
				for (int i = 0; i < layout.getChildCount(); i++)
					layout.getChildAt(i).setOnClickListener(DeviceControlActivity.this);
*/

				// drawLockGrids();
			}
		}
	};

	private void updateLockUI(int lockStatus) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.btn_lock);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getChildAt(0).getLayoutParams();
		((LinearLayout) findViewById(R.id.btn_lock)).removeAllViews();
		View view = null;
		switch (lockStatus) {
			case 1:
				view = getLayoutInflater().inflate(R.layout.lock_open, null);
				//view.setBackground((Drawable) getResources().getDrawable(R.drawable.lock_locked_button));
				((LinearLayout) findViewById(R.id.btn_lock)).addView(view, params);
//				currentLockIndex = 1;
				break;
			case 2:
				view = getLayoutInflater().inflate(R.layout.lock_closed, null);
				view.setBackground((Drawable) getResources().getDrawable(R.drawable.lock_locked_button));
				((LinearLayout) findViewById(R.id.btn_lock)).addView(view, params);
//				currentLockIndex = 2;
				break;
			case 3:
				view = getLayoutInflater().inflate(R.layout.lock_locked, null);
				view.setBackground((Drawable) getResources().getDrawable(R.drawable.lock_locked_button));
				((LinearLayout) findViewById(R.id.btn_lock)).addView(view, params);
//				currentLockIndex = 3;
				break;
		}

	}

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

		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ArmorService.NOTIFICATION_ID);
		if (getIntent().getBooleanExtra("NOTIFICATION", false))
			ArmorService.isNotificationActive = false;
//		getActionBar().setTitle(mDeviceName);
//		getActionBar().setDisplayHomeAsUpEnabled(true);
		updateLockUI(currentLockIndex);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		View btnLock = findViewById(R.id.btn_lock);
		btnLock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				switch (currentLockIndex) {
					case 1:
						updateLockUI(2);
						sendUnlockCommand(2);
//						currentLockIndex = 2;
						break;
					case 2:
						updateLockUI(3);
						sendUnlockCommand(3);
//						currentLockIndex = 3;
						break;
					case 3:
//						updateLockUI(3);
//						sendUnlockCommand(3);
//						currentLockIndex = 3;
						break;
			}

			}

		});
		btnLock.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				switch (currentLockIndex) {
					case 3:
						updateLockUI(1);
						sendUnlockCommand(1);
//						currentLockIndex = 1;
						break;
					default:
						break;
				}
				return true;
			}
		});
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

	private void hideAllPointers() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.lock_pointer_layout);
		for (int i = 0; i < layout.getChildCount(); i++)
			layout.getChildAt(i).setVisibility(View.INVISIBLE);
	}


	public void updateUi(int pos){
		hideAllPointers();
		switch (pos) {

			case 1:
				findViewById(R.id.lock_pointer_0).setVisibility(View.VISIBLE);

				break;

			case 0:
				findViewById(R.id.lock_pointer_00).setVisibility(View.VISIBLE);

				break;

			case 2:
				findViewById(R.id.lock_pointer_1).setVisibility(View.VISIBLE);

				break;

			case 3:
				findViewById(R.id.lock_pointer_2).setVisibility(View.VISIBLE);

				break;

			case 4:
				findViewById(R.id.lock_pointer_3).setVisibility(View.VISIBLE);

				break;

			default:
				break;
		}
	}


	private void sendUnlockCommand(int index) {
		try {
			//String op_data = MessageUtil.getOpData(currentLockIndex, index) + "";
			//byte[] op_data1 = op_data.getBytes();
			//System.out.println("op_dataop_dataop_dataop_dataop_dataop_data:: "+op_data+"get bytes "+op_data.getBytes());
			//Log. ("op_dataop_dataop_dataop_dataop_dataop_data:: "+op_data);
			//mBluetoothLeService.sendCommand(op_data.getBytes());
			//byte[] bytes = ByteBuffer.allocate(4).putInt(1695609641).array();

			byte[] command = MessageUtil.getOpData(currentLockIndex, index);
			mBluetoothLeService.sendCommand(command);
			currentLockIndex = index;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	@Override
	public void onClick(View v) {
		hideAllPointers();

		final Handler handler = new Handler();

		switch (v.getId()) {
		case R.id.lock_0:
		case R.id.lock_pointer_0:
			findViewById(R.id.lock_pointer_0).setVisibility(View.VISIBLE);

		//	updateUi(1);
			//try {
		//		Thread.sleep(2000);                 //2000 milliseconds is one second.
		//	} catch(InterruptedException ex) {
		//		Thread.currentThread().interrupt();
		//	}
			sendUnlockCommand(1);
			currentLockIndex = 2;
			/*handler.postDelayed(new Runnable(){
				public void run(){
					updateUi(2);
				}
			}, DELAYui);*/
		//	try {
		//		Thread.sleep(2000);                 //2000 milliseconds is one second.
		//	} catch(InterruptedException ex) {
		//		Thread.currentThread().interrupt();
		//	}
		//	updateUi(2);
			break;
		case R.id.lock_00:
		case R.id.lock_pointer_00:
			findViewById(R.id.lock_pointer_00).setVisibility(View.VISIBLE);
			sendUnlockCommand(-1);
			currentLockIndex = 1;
			break;
		case R.id.lock_1:
		case R.id.lock_pointer_1:
			findViewById(R.id.lock_pointer_1).setVisibility(View.VISIBLE);
			sendUnlockCommand(2);
			currentLockIndex = 2;
			break;
		case R.id.lock_2:
		case R.id.lock_pointer_2:
			findViewById(R.id.lock_pointer_2).setVisibility(View.VISIBLE);
			sendUnlockCommand(3);
			currentLockIndex = 3;
			break;
		case R.id.lock_3:
		case R.id.lock_pointer_3:
			findViewById(R.id.lock_pointer_3).setVisibility(View.VISIBLE);
			sendUnlockCommand(4);
			currentLockIndex = 4;
			break;

		default:
			break;
		}
	}
}
