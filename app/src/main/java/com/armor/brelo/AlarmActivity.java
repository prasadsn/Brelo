package com.armor.brelo;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AlarmActivity extends Activity {

	MediaPlayer m = new MediaPlayer();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_layout);
		playBeep();
		findViewById(R.id.stop_alarm_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArmorService.alarmActive = !ArmorService.alarmActive;
				m.stop();
				finish();
			}
		});
	}

	protected  void onDestroy(){
		super.onDestroy();
		/*Intent serviceIntent = new Intent(SettingsActivity.this, ArmorService.class);
		// Start service
		startService(serviceIntent);*/

	}

	public void playBeep() {
	    try {
	        if (m.isPlaying()) {
	            m.stop();
	            m.release();
	            m = new MediaPlayer();
	        }

	        AssetFileDescriptor descriptor = getAssets().openFd("bomb_siren.mp3");
	        m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
	        descriptor.close();

	        m.prepare();
	        m.setVolume(1f, 1f);
	        m.setLooping(true);
	        m.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		ArmorService.alarmActive = !ArmorService.alarmActive;
		m.stop();
		finish();
	}
}
