package com.lanian.wifimonitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {

	final String TAG = "WifiTrigger";
	static final String SHARED_PREFERENCES_SSIDS = "ssids";
	static final String KEY_PREVIOUS_SSIDS = "previous";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "scan completed");
		Cursor cursor = context.getContentResolver().query(TriggerProvider.WATCHING_SSIDS_URI, null, null, null, null);
		if (cursor.getCount() == 0) {
			Log.d(TAG, "there is no watching SSID");
			return;
		}
			
		
		cursor.moveToFirst();
		ArrayList<String> ssidsWatching = new ArrayList<String>();
		while (!cursor.isAfterLast())
		{
			ssidsWatching.add(cursor.getString(0));
			cursor.moveToNext();
		}
		for (String ssid : ssidsWatching)
			Log.d(TAG, "watching: "+ssid);
		
		WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> results = wm.getScanResults();
		
		HashSet<String> ssids = new HashSet<String>();
		for (ScanResult sr : results) {
			if (!sr.SSID.isEmpty() && ssidsWatching.contains(sr.SSID)) {
				ssids.add(sr.SSID);
					
			}
		}
		
		for (String ssid : ssids)
			Log.d(TAG, "around here: "+ssid);
		
		SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES_SSIDS, Context.MODE_MULTI_PROCESS);
		Set<String> ssidsPrev = sp.getStringSet(KEY_PREVIOUS_SSIDS, new HashSet<String>());
		
		for (String ssid : ssidsPrev)
			Log.d(TAG, "previous: "+ssid);
		
		sp.edit().putStringSet(KEY_PREVIOUS_SSIDS, ssids).commit();
		
		ArrayList<String> disappeared = new ArrayList<String>();
		for (String ssid : ssidsPrev) {
			if (!ssids.contains(ssid))
				disappeared.add(ssid);
		}
		
		ArrayList<String> appeared = new ArrayList<String>();
		for (String ssid : ssids) {
			if (!ssidsPrev.contains(ssid))
				appeared.add(ssid);
		}
		
		
		/*Bundle data = new Bundle();
		data.putStringArrayList("disappeared", disappeared);
		data.putStringArrayList("appeared", appeared);*/
		for (String ssid : appeared) {
			Log.d(TAG, "appeared: "+ssid);
		}
		for (String ssid : disappeared) {
			Log.d(TAG, "disappeared: "+ssid);
			
		}
		
		if (!appeared.isEmpty() || !disappeared.isEmpty()) {
			((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
		}
	}

}
