package com.lanian.wifimonitor;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity implements TriggerListFragment.OnTriggerListEventListener, EditTriggerFragment.OnEditTriggerEventListener {

	final String TAG = "WifiMonitor";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new TriggerListFragment(), "TriggerList").commit();
		}
	}

	@Override
	public void onTriggerSelected(int triggerId) {
		getFragmentManager().beginTransaction().replace(R.id.container, new EditTriggerFragment().setTriggerId(triggerId)).addToBackStack("EditTrigger").commit();
	}

	@Override
	public void onActionNewTrigger() {
		getFragmentManager().beginTransaction().replace(R.id.container, new EditTriggerFragment()).addToBackStack("EditTrigger").commit();
	}

	@Override
	public void onFinishEditTrigger() {
		getFragmentManager().popBackStack();
	}

}
