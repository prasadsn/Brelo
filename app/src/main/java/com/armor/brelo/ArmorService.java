package com.armor.brelo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.armor.brelo.utils.SOSIntentService;

public class ArmorService extends Service {

	private BluetoothAdapter mBluetoothAdapter;
	public static final int REQUEST_CODE_SOS_DISMISS = 1;

	public static boolean alarmActive;

	private static final int txPower = 0;
	private BluetoothLeService mBluetoothLeService;
	private static final String TAG = ArmorService.class.getName();
	private String mDeviceAddress = null;
	private double distance = 0;
	private boolean auto_unlock_enabled;
	private ArmorServiceConnection armorServiceConnection = null;
	private static final int DISTANCE_THRESHOLD = 7000;
	private String deviceName = null;
	private String deviceAddress = null;
	public static final int NOTIFICATION_ID = 32123;
	public static final int NOTIFICATION_ID_SOS = 32124;
	public static boolean isNotificationActive = false;
	private boolean isDeviceReady = true;
	private File logFile;
	private boolean isDeviceOutOfRange = false;
	//private boolean auto_unlock_opd = false;


	private class ArmorServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			if (mBluetoothLeService.connect(mDeviceAddress)) {
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
			//onCreate();
			//auto_unlock_opd = false;
			//new Thread(r).start();
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		//isDeviceReady = true;
		//isDeviceOutOfRange = true;
		logFile = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath(), "logs.txt");
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		new Thread(r).start();
	}

	Runnable r = new Runnable() {

		@Override
		public void run() {
			while (true){//!auto_unlock_opd) {
				if (!isForeground(DeviceScanActivity.class) && isDeviceReady )//&& !auto_unlock_opd)
					mBluetoothAdapter.startLeScan(mLeScanCallback);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	/*
	public void onDestroy(){
		super.onDestroy();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		onCreate();
	}
	*/

	public boolean isForeground(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);

		ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
		if (componentInfo.getClassName().equals(serviceClass.getName()))
			return true;
		return false;
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			Log.e(TAG, "Found a device on Scan " + device.getName());
			deviceName = device.getName();
			deviceAddress = device.getAddress();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			distance = getDistance(rssi);
			/*
			 * FileOutputStream stream;
			 * try {
			 * stream = new FileOutputStream(logFile, true);
			 * stream.write(new String(deviceName + "\t" + distance + "\t" +
			 * device.getBondState() + "\n\r").getBytes());
			 * stream.flush();
			 * stream.close();
			 * } catch (FileNotFoundException e1) {
			 * // TODO Auto-generated catch block
			 * e1.printStackTrace();
			 * } catch (IOException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 */
			boolean sos_enabled = preferences.getBoolean("sos", false);
			auto_unlock_enabled = preferences.getBoolean("auto_unlock", false);
 			if (deviceName!=null && deviceName.contains("SOS") && sos_enabled) {
//				if (!alarmActive) {
					/*Intent intent = new Intent(ArmorService.this, AlarmActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplication().startActivity(intent);*/
					 showSOSNotification(deviceAddress, deviceName);
//				showManualNotification();
					alarmActive = !alarmActive;
//				}
			} else if (distance < DISTANCE_THRESHOLD && auto_unlock_enabled && device.getBondState() == BluetoothDevice.BOND_NONE
					&& isDeviceOutOfRange){
				try {
					isDeviceOutOfRange = false;
					//auto_unlock_opd = true;
					mBluetoothAdapter.stopLeScan(this);
					isDeviceReady = false;
					mDeviceAddress = device.getAddress();

					Intent gattServiceIntent = new Intent(ArmorService.this, BluetoothLeService.class);
					armorServiceConnection = new ArmorServiceConnection();
					IntentFilter filter = new IntentFilter(BluetoothLeService.ACTION_GATT_CONNECTED);
					registerReceiver(mGattUpdateReceiver, filter);
					//IntentFilter dfilter = new IntentFilter(BluetoothLeService.ACTION_GATT_DISCONNECTED);
					//registerReceiver(disReceiver, dfilter);
					bindService(gattServiceIntent, armorServiceConnection, BIND_AUTO_CREATE);
					// new Thread(sleepR).start();
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (distance < DISTANCE_THRESHOLD && !auto_unlock_enabled && !isNotificationActive) {
				showManualNotification();
			}
			if (distance > DISTANCE_THRESHOLD) {
				//new Thread(r).stop();
				isDeviceOutOfRange = true;
				if(!auto_unlock_enabled){
					((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
					isNotificationActive = false;
				}
			}
			mBluetoothAdapter.stopLeScan(this);
		}
	};
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
					Log.e(TAG, "Connection success: Sending command");

					byte[] op= new byte[1];
					op[0]=0x41;

					if (mBluetoothLeService != null)
						mBluetoothLeService.sendCommand(op);
					// else
					// mBluetoothLeService.sendCommand(MessageUtil.getEncryptedMessage("0x12",
					// null).getBytes());

				//Log.e(TAG, "disconnecting service");
				//mBluetoothLeService.disconnect();
				//isDeviceReady = true;

				new Thread(sleepR).start();
				// mBluetoothLeService.disconnect();
			}
		}
	};
	Runnable sleepR = new Runnable() {

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e(TAG, "disconnecting service");
			mBluetoothLeService.disconnect();

			isDeviceReady = true;



			//onDestroy();
			//final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			//mBluetoothAdapter = bluetoothManager.getAdapter();
			//new Thread(r).start();
		}
	};

	/*
	private final BroadcastReceiver disReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Log.e(TAG, "Connection disconnected");
				//auto_unlock_opd = false;
				new Thread(r).start();
				// mBluetoothLeService.disconnect();
			}
		}
	};
	*/

	double getDistance(int rssi) {
		/*
		 * RSSI = TxPower - 10 * n * lg(d)
		 * n = 2 (in free space)
		 * 
		 * d = 10 ^ ((TxPower - RSSI) / (10 * n))
		 */

		return Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void showManualNotification() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Armor").setContentText("Tap to unlock");
		// Creates an explicit intent for an Activity in your app
		isNotificationActive = true;
		Intent resultIntent = new Intent(getApplicationContext(), DeviceControlActivity.class);
		resultIntent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, deviceName);
		resultIntent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, deviceAddress);
		resultIntent.putExtra("NOTIFICATION", true);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
		stackBuilder.addParentStack(DeviceControlActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT
				| PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = mBuilder.build();
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}

	private void showSOSNotification(String deviceAddress, String deviceName) {
		RemoteViews expandedView = new RemoteViews("com.armor.brelo",
					R.layout.notification_sos);
		expandedView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
		expandedView.setTextViewText(R.id.title, "Armor");
		expandedView.setTextViewText(R.id.text, "You are being intruded");
		expandedView.setTextColor(R.id.title, Color.BLACK);
		expandedView.setTextColor(R.id.text, Color.BLACK);
		// Creates an explicit intent for an Activity in your app
		isNotificationActive = true;
		Intent resultIntent = new Intent(getApplicationContext(), DeviceControlActivity.class);
		resultIntent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, deviceName);
		resultIntent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, deviceAddress);
		resultIntent.putExtra("NOTIFICATION", true);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
		stackBuilder.addParentStack(DeviceControlActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT
				| PendingIntent.FLAG_ONE_SHOT);
		Intent sosIntent = new Intent(this, SOSIntentService.class);
		sosIntent.putExtra(SOSIntentService.EXTRA_DEVICE_ADDRESS, deviceAddress);
		PendingIntent sosPendingIntent = PendingIntent.getService(this, REQUEST_CODE_SOS_DISMISS, sosIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(deviceName).setContentText("You are being intruded").setPriority(Notification.PRIORITY_HIGH)
				.addAction(android.R.drawable.ic_menu_close_clear_cancel,"Dismiss", sosPendingIntent)
				.setDeleteIntent(sosPendingIntent).setAutoCancel(true);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = mBuilder.build();
		notification.contentView = expandedView;
		mNotificationManager.notify(0, notification);
	}
}

