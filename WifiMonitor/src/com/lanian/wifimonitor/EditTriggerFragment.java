package com.lanian.wifimonitor;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Spinner;

public class EditTriggerFragment extends Fragment implements AdapterView.OnItemSelectedListener, LoaderCallbacks<Cursor> {
	
	public interface OnEditTriggerEventListener {
		public void onFinishEditTrigger();
	}
	
	final String TAG = "WifiTrigger";
	OnEditTriggerEventListener listener;
	BroadcastReceiver scanResultReceiver;
	int state;
	int triggerId = -1;
	
	public EditTriggerFragment setTriggerId(int triggerId) {
		this.triggerId = triggerId; 
		return this;
	}
	
	public void onTriggerLoaded(Cursor data) {
		data.moveToFirst();
		
		((EditText)getView().findViewById(R.id.editText_name)).setText(data.getString(1));
		((Spinner)getView().findViewById(R.id.spinner_ap_state)).setSelection(data.getInt(2));
		((AutoCompleteTextView)getView().findViewById(R.id.actv_ssid)).setText(data.getString(3));
		getView().findViewById(R.id.actv_ssid).setVisibility(View.VISIBLE);
	
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		listener = (OnEditTriggerEventListener)getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_edittrigger, container, false);
		
		((Spinner)rootView.findViewById(R.id.spinner_ap_state)).setOnItemSelectedListener(this);
		((AutoCompleteTextView)rootView.findViewById(R.id.actv_ssid)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((AutoCompleteTextView)v).showDropDown();
			}
		});
		
		return rootView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		
		scanResultReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				unregisterScanResultReceiver();
				updateSSIDs();
			}
			
		};
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (triggerId == -1)
			inflater.inflate(R.menu.newtrigger, menu);
		else 
			inflater.inflate(R.menu.edittrigger, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done_edit_trigger:
			saveTrigger();
			break;
		case R.id.action_delete_trigger:
			askDeleteTrigger();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void askDeleteTrigger() {
		if (triggerId == -1)
			return;
		
		new AlertDialog.Builder(getActivity())
		.setTitle(R.string.dialog_title_delete_trigger)
		.setMessage(R.string.dialog_message_delete_trigger)
		.setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteTrigger();
			}
		})
		.setNegativeButton(R.string.dialog_button_no, null)
		.create().show();
	}

	protected void deleteTrigger() {
		if (triggerId == -1)
			return;
		
		getActivity().getContentResolver().delete(TriggerProvider.CONTENT_URI, 
				TriggerDBHelper.TriggerEntry._ID+"=?", new String[] {String.valueOf(triggerId)});
		
		if (listener != null)
			listener.onFinishEditTrigger();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		loadTrigger();
		
		getActivity().registerReceiver(scanResultReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		WifiManager wm = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
		wm.startScan();
	}
	
	private void loadTrigger() {
		if (triggerId == -1)
			return;
		
		Bundle args = new Bundle();
		args.putInt("triggerId", triggerId);
		getLoaderManager().restartLoader(0, args, this);
	}

	@Override
	public void onPause() {
		unregisterScanResultReceiver();
		super.onPause();
	}
	
	private void unregisterScanResultReceiver() {
		if (scanResultReceiver != null) {
			getActivity().unregisterReceiver(scanResultReceiver);
			scanResultReceiver = null;
		}
	}
	
	private void updateSSIDs() {
		WifiManager wm = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> results = wm.getScanResults();
		ArrayList<String> ssids = new ArrayList<String>();
		for (ScanResult sr : results) {
			if (!sr.SSID.isEmpty()) {
				if (!ssids.contains(sr.SSID))
					ssids.add(sr.SSID);
			}
		}
		
		AutoCompleteTextView actvSsid = (AutoCompleteTextView)getView().findViewById(R.id.actv_ssid);
		actvSsid.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, ssids));
		actvSsid.setVisibility(View.VISIBLE);
		
		((ProgressBar)getView().findViewById(R.id.progressBar_scanning)).setVisibility(View.GONE);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.spinner_ap_state) {
			state = position;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG, "nothing selected");
	}
	
	private void saveTrigger() {
		EditText et = (EditText)getView().findViewById(R.id.editText_name);
		String name = et.getText().toString().trim();
		if (name.isEmpty()) {
			Toast.makeText(getActivity(), R.string.toast_error_trigger_name_is_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		AutoCompleteTextView tv = (AutoCompleteTextView)getView().findViewById(R.id.actv_ssid);
		String ssid = tv.getText().toString().trim();
		if (ssid.isEmpty()) {
			Toast.makeText(getActivity(), R.string.toast_error_ssid_is_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		
		ContentValues values = new ContentValues();
		values.put(TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_NAME, name);
		values.put(TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_TYPE, state);
		values.put(TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_SSID, ssid);
		if (triggerId == -1) {
			Uri uri = getActivity().getContentResolver().insert(TriggerProvider.CONTENT_URI, values);
			Log.d(TAG, "addNewTrigger "+uri.toString());
		} else {
			int row = getActivity().getContentResolver().update(TriggerProvider.CONTENT_URI, values,
					TriggerDBHelper.TriggerEntry._ID+"=?", new String[] {String.valueOf(triggerId)});
			Log.d(TAG, "update trigger "+row);
		}
		
		listener.onFinishEditTrigger();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), TriggerProvider.CONTENT_URI, 
				new String[] {TriggerDBHelper.TriggerEntry._ID, 
						TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_NAME, 
						TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_TYPE, 
						TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_SSID,
						TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_ACTION }, 
				TriggerDBHelper.TriggerEntry._ID+"=?", new String[] { String.valueOf(args.getInt("triggerId")) }, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		onTriggerLoaded(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		
	}
}
