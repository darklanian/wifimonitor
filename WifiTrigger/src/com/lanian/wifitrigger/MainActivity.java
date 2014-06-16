package com.lanian.wifitrigger;

import com.lanian.wifitrigger.EditTriggerFragment.OnEditTriggerEventListener;
import com.lanian.wifitrigger.TriggerListFragment.OnTriggerListEventListener;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity implements OnTriggerListEventListener, OnEditTriggerEventListener {

	final String TAG = "WifiTrigger";
		
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	/*public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_edittrigger, container,
					false);
			return rootView;
		}
	}*/
	
	
	
	
}
