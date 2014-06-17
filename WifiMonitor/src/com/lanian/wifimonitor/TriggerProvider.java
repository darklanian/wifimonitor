package com.lanian.wifimonitor;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class TriggerProvider extends ContentProvider {
	final String TAG = "TriggerProvider";
	
	public static final String AUTHORITY = "TriggerProviderAuthorities";
	public static final String WATCHING_SSIDS = "watching_ssids";
	public static final String TRIGGERS = "triggers";
	public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY);
	public static final Uri WATCHING_SSIDS_URI = Uri.withAppendedPath(Uri.parse("content://"+AUTHORITY), WATCHING_SSIDS);
	
	TriggerDBHelper dbHelper;
	
	@Override
	public boolean onCreate() {
		dbHelper = new TriggerDBHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.d(TAG, "query "+uri.getPath());
		if (uri.getPath().replace("/", "").equals(WATCHING_SSIDS)) {
			return dbHelper.getReadableDatabase().query(true, TriggerDBHelper.TriggerEntry.TABLE_NAME, new String[] {TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_SSID}, null, null, null, null, null, null);
		} else {
			return dbHelper.getReadableDatabase().query(TriggerDBHelper.TriggerEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		}
		
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long id = dbHelper.getWritableDatabase().insert(TriggerDBHelper.TriggerEntry.TABLE_NAME, null, values);
		if (id != -1) {
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.withAppendedPath(uri, String.valueOf(id));
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		int rows = dbHelper.getWritableDatabase().delete(TriggerDBHelper.TriggerEntry.TABLE_NAME, selection, selectionArgs);
		return rows;
		
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		
		int rows = dbHelper.getWritableDatabase().update(TriggerDBHelper.TriggerEntry.TABLE_NAME, values, selection, selectionArgs);
		return rows;
	}

}
