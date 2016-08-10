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
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.armor.brelo.ui.AddLockActivity;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;
	private int selectedIndex = -1;
//	private MenuItem register = null;
	private Button register;

	private static final String PREF_NAME_REG_DEVICES = "registered_devices";

	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().setTitle(R.string.title_devices);
		mHandler = new Handler();
		// Use this check to determine whether BLE is supported on the device.
		// Then you can
		// selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}

		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		register = new Button(this);
		register.setText("Register");
//		getListView().addHeaderView(register);
		if (isMyServiceRunning(ArmorService.class))
			return;
		Intent serviceIntent = new Intent(DeviceScanActivity.this, ArmorService.class);
		startService(serviceIntent);

		//registerReceiver(serviceReceiver, serviceIntentFilter());
	}

	/*
	private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

				Intent rserviceIntent = new Intent(DeviceScanActivity.this, ArmorService.class);
				stopService(rserviceIntent);
				startService(rserviceIntent);

			}
		}
	};

	private static IntentFilter serviceIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		return intentFilter;
	}
	*/

	protected  void onDestroy(){
		super.onDestroy();
		/*Intent serviceIntent = new Intent(SettingsActivity.this, ArmorService.class);
		// Start service
		startService(serviceIntent);*/

	}

	public static void serviceRestart(){
		/*
		Intent rserviceIntent = new Intent(DeviceScanActivity.this, ArmorService.class);
		stopService(rserviceIntent);
		startService(rserviceIntent);
		*/
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
//		register = menu.getItem(0);
		/*
		 * if (!mScanning) {
		 * menu.findItem(R.id.menu_stop).setVisible(false);
		 * menu.findItem(R.id.menu_scan).setVisible(true);
		 * menu.findItem(R.id.menu_refresh).setActionView(null);
		 * } else {
		 * menu.findItem(R.id.menu_stop).setVisible(true);
		 * menu.findItem(R.id.menu_scan).setVisible(false);
		 * menu.findItem(R.id.menu_refresh).setActionView(
		 * R.layout.actionbar_indeterminate_progress);
		 * }
		 */
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_register:
			/*
			 * mLeDeviceListAdapter.clear();
			 * scanLeDevice(true);
			 */
			final BluetoothDevice device = mLeDeviceListAdapter.getDevice(selectedIndex);
			SharedPreferences pref = getSharedPreferences(PREF_NAME_REG_DEVICES, MODE_PRIVATE);
			String deviceName = device.getName();
			String deviceAddr = device.getAddress();
			SharedPreferences.Editor editor = pref.edit();
			editor.putString(deviceAddr, deviceName);
			editor.commit();

			final Intent intent = new Intent(this, SettingsActivity.class);
			if (mScanning) {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				mScanning = false;
			}
			startActivity(intent);
			finish();
			break;
		/*
		 * case R.id.menu_stop:
		 * scanLeDevice(false);
		 * break;
		 */
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		setListAdapter(mLeDeviceListAdapter);
		scanLeDevice(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		selectedIndex = position;
		BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
		String deviceName = device.getName();
		String deviceAddr = device.getAddress();
		Intent intent = new Intent(DeviceScanActivity.this, AddLockActivity.class);
		intent.putExtra("deviceName", deviceName);
		intent.putExtra("deviceAddr", deviceAddr);
		startActivity(intent);
		finish();
		/*SharedPreferences pref = getSharedPreferences(PREF_NAME_REG_DEVICES, MODE_PRIVATE);
		String temp = pref.getString(deviceAddr, null);
		if (temp == null) {
			register.setEnabled(true);
			return;
		}
		pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean autoUnlock = pref.getBoolean("auto_unlock", false);
		if (autoUnlock) {
			Intent intent = new Intent(DeviceScanActivity.this, SettingsActivity.class);
			startActivity(intent);
			finish();
		} else {
			Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
			intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
			intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
			startActivity(intent);
			finish();
		}*/
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
//		register = menu.findItem(R.id.menu_register);
		return super.onPrepareOptionsMenu(menu);
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = DeviceScanActivity.this.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			String deviceName = device.getName();
			if (deviceName != null && deviceName.contains("SOS")) {
				SharedPreferences pref = getSharedPreferences(PREF_NAME_REG_DEVICES, MODE_PRIVATE);
				String temp = pref.getString(device.getAddress(), null);
				if (temp != null)
					deviceName = temp;
			}
			if (deviceName != null && deviceName.length() > 0 && (deviceName.contains("BRELO") || deviceName.contains("SOS")))
				viewHolder.deviceName.setText(deviceName);
			else
				return view;
			viewHolder.deviceAddress.setText(device.getAddress());

			if (i == selectedIndex)
				view.setBackgroundColor(Color.GRAY);
			return view;
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					String deviceName = device.getName();
					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();
				}
			});
		}
	};

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}


}