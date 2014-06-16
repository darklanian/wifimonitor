package com.lanian.wifitrigger;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TriggerListFragment extends ListFragment implements LoaderCallbacks<Cursor>{
	final String TAG = "WifiTrigger";
	
	public interface OnTriggerListEventListener {
		public void onTriggerSelected(int triggerId);
		public void onActionNewTrigger();
	}
	
	OnTriggerListEventListener listener;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setListAdapter(new SimpleCursorAdapter(getActivity(), android.R.layout.simple_selectable_list_item, null, 
				new String[] {TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_NAME}, 
				new int[] {android.R.id.text1}, 
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		setListShown(false);
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (OnTriggerListEventListener)activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
	}
			
	@Override
	public void onResume() {
		super.onResume();
		
		reload();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.triggerlist, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_trigger:
			listener.onActionNewTrigger();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor item = (Cursor)getListAdapter().getItem(position);
		Log.d(TAG, String.format("onListItemClick: id=%d name=%s", item.getInt(0), item.getString(1)));
		if (listener != null)
			listener.onTriggerSelected(item.getInt(0));
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		return new CursorLoader(getActivity(), TriggerProvider.CONTENT_URI, 
				new String[] { TriggerDBHelper.TriggerEntry._ID, TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_NAME }, 
				null, null, TriggerDBHelper.TriggerEntry.COLUMN_NAME_TRIGGER_NAME+" ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		
		((SimpleCursorAdapter)getListAdapter()).swapCursor(arg1);
		if (isResumed())
			setListShown(true);
		else
			setListShownNoAnimation(true);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
		((SimpleCursorAdapter)getListAdapter()).swapCursor(null);
	}
	
	public void reload() {
		getLoaderManager().restartLoader(0, null, this);
	}
}
