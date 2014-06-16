package com.lanian.wifitrigger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class TriggerDBHelper extends SQLiteOpenHelper {

	
	private static final String DB_NAME = "triggers.db";
	private static final int DB_VERSION = 1;
	
	public static abstract class TriggerEntry implements BaseColumns {
		public static final String TABLE_NAME = "triggers";
		public static final String COLUMN_NAME_TRIGGER_NAME = "name";
		public static final String COLUMN_NAME_TRIGGER_TYPE = "type";
		public static final String COLUMN_NAME_TRIGGER_SSID = "ssid";
		public static final String COLUMN_NAME_TRIGGER_ACTION = "action";
	}
	
	private static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE "+TriggerEntry.TABLE_NAME+" ("+
			TriggerEntry._ID+" INTEGER PRIMARY KEY,"+
			TriggerEntry.COLUMN_NAME_TRIGGER_NAME+" TEXT,"+
			TriggerEntry.COLUMN_NAME_TRIGGER_TYPE+" INTEGER,"+
			TriggerEntry.COLUMN_NAME_TRIGGER_SSID+" TEXT,"+
			TriggerEntry.COLUMN_NAME_TRIGGER_ACTION+" TEXT"+
			")";
	
	public TriggerDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
