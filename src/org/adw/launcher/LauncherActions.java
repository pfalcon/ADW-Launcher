package org.adw.launcher;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class LauncherActions {

	public interface Action {

		public String getName();

		public void setup();

		public void putIntentExtras(Intent intent);

		public boolean runIntent(Intent intent);
	}

	private static LauncherActions mInstance = null;
	private Launcher mLauncher;
	private final List<Action> mActions = new ArrayList<Action>();

	public static synchronized LauncherActions getInstance() {
		if (mInstance == null)
			mInstance = new LauncherActions();
		return mInstance;
	}

	private LauncherActions() {
		BroadcastReceiver reciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub

			}
		};
	}

	public void init(Launcher launcher) {
		Log.d("BOOMBULER", "init");
		mActions.clear();
		mLauncher = launcher;
		String[] menuBindingsNames = launcher.getResources().getStringArray(R.array.menu_binding_entries);
		String[] menuBindingsValues = launcher.getResources().getStringArray(R.array.menu_binding_values);

		for (int i = 0; i < menuBindingsValues.length; i++) {
			int value = Integer.parseInt(menuBindingsValues[i]);
			String name = menuBindingsNames[i];
			if (value != Launcher.BIND_NONE && value != Launcher.BIND_APP_LAUNCHER) {
				DefaultLauncherAction lact = new DefaultLauncherAction(value, name);
				mActions.add(lact);
			}
		}
	}

	public Intent getIntentForAction(Action action) {
		Intent result = new Intent(CustomShirtcutActivity.ACTION_LAUNCHERACTION);
		result.setClass(mLauncher, CustomShirtcutActivity.class);
		action.putIntentExtras(result);
		return result;
	}



	public ListAdapter getSelectActionAdapter() {
		return new ListAdapter() {

			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
			}

			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
			}

			@Override
			public boolean isEmpty() {
				return mActions.isEmpty();
			}

			@Override
			public boolean hasStableIds() {
				return false;
			}

			@Override
			public int getViewTypeCount() {
				return 1;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null)
				{
					LayoutInflater li = mLauncher.getLayoutInflater();
					convertView = li.inflate(R.layout.add_list_item, parent, false);
				}
				((TextView)convertView).setText(mActions.get(position).getName());
				return convertView;
			}

			@Override
			public int getItemViewType(int position) {
				return 0;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return mActions.get(position);
			}

			@Override
			public int getCount() {
				return mActions.size();
			}

			@Override
			public boolean isEnabled(int position) {
				return true;
			}

			@Override
			public boolean areAllItemsEnabled() {
				return true;
			}
		};
	}



	public void launch(Intent intent) {
		for(Action act : mActions) {
			if (act.runIntent(intent))
				break;
		}
	}




	private class DefaultLauncherAction implements Action {

		private static final String EXTRA_BINDINGVALUE = "DefaultLauncherAction.EXTRA_BINDINGVALUE";

		private final int mBindingValue;
		private final String mName;

		public DefaultLauncherAction(int bindingValue, String name) {
			mBindingValue = bindingValue;
			mName = name;
		}

		@Override
		public String getName() {
			return mName;
		}

		@Override
		public void setup() {
			// Nothing to do...
		}

		@Override
		public void putIntentExtras(Intent intent) {
			intent.putExtra(EXTRA_BINDINGVALUE, mBindingValue);
		}

		@Override
		public boolean runIntent(Intent intent) {
			if (intent.hasExtra(EXTRA_BINDINGVALUE))
			{
				int val = intent.getIntExtra(EXTRA_BINDINGVALUE, 0);
				if (val == mBindingValue) {
					mLauncher.fireHomeBinding(mBindingValue, 0);
					return true;
				}

			}

			return false;
		}

	}


}
