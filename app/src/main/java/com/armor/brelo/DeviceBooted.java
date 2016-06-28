package com.armor.brelo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceBooted extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent serviceIntent = new Intent(context, ArmorService.class);
        // Start service
        context.startService(serviceIntent);

	}

}
