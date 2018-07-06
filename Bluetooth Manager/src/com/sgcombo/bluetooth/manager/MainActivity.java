package com.sgcombo.bluetooth.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
/*
Copyright (C) 2012 by Vladimir Novick http://www.linkedin.com/in/vladimirnovick , 

    vlad.novick@gmail.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/
public class MainActivity extends Activity {
	private static final int EDIT_SETTING_COMPLETED = 22;
	private static final int CHECK_DISCOVERABLE = 9;
	private static final boolean D = true;
	private static final int DISCOVERABLE_FINISH = 4;
	// EXTRA string to send on to mainactivity
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	public static String EXTRA_DEVICE_NAME = "device_name";
	private static final int PICK_NEW_DEVICE_REQUEST = 5;
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 6;
	private static final int REQUEST_DISCOVERABLE_CODE = 1;
	private static final int REQUEST_ENABLE_BT = 30;

	private static final String SELECTED_DEVICES_KEY = "SELECTED_DEVICES_KEY";
	// Debugging for LOGCAT
	private static final String TAG = "DeviceListActivity";

	final Context context = this;

	boolean clickedDeleteDevicesInprocess = false;
	// we are going to use a handler to be able to run in our TimerTask
	final Handler discovarableHandlerTimer = new Handler() {
		private void discoverableCheckStatus() {
			if (mBtAdapter != null) {
			if (mBtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
				discovarableShowScanMode(false);
				discovarableStopTimerTest();
			}
			}

		}

		public void handleMessage(Message msg) {
			discoverableCheckStatus();

		}
	};

	Timer discovarableTimerTest;

	private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				final int state = intent
						.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
								BluetoothDevice.ERROR);
				final int prevState = intent.getIntExtra(
						BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
						BluetoothDevice.ERROR);

				// if (state == BluetoothDevice.BOND_BONDED && prevState ==
				// BluetoothDevice.BOND_BONDING) {
				// showToast("Paired");
				// } else if (state == BluetoothDevice.BOND_NONE && prevState ==
				// BluetoothDevice.BOND_BONDED){
				// showToast("Unpaired");
				// }

				reloadList();
			}
		}
	};

	TimerTask discovarableTimerTestTask;
	private ImageView discovarebleImage;

	// Member fields

	// Member fields
	private BluetoothAdapter mBtAdapter = null;

	private MainAdapter mMainAdapter;

	boolean reloadListInprocess = false;

	public List<String> selectedDevices = new ArrayList<String>();

	private Intent serverIntent;

	TextView textView1;

	// declare button for launching website and textview for connection status
	Button tlbutton;

	public BluetoothDevice getActiveDevice(String makAddress) {
		if (mBtAdapter == null)
			return null;
		BluetoothDevice device = mBtAdapter.getRemoteDevice(makAddress);
		return device;
	}

	

	
	 private Intent enableBtIntent = null;
	 
	private void checkBTState() {

		if (mBtAdapter == null) {
			Toast.makeText(getBaseContext(),
					"Device does not support Bluetooth", Toast.LENGTH_SHORT)
					.show();
		} else {
			if (mBtAdapter.isEnabled()) {
				Log.d(TAG, "...Bluetooth ON...");
			} else {
				// Prompt user to turn on Bluetooth
				 enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

			}
		}
	}

	private void checkOperationBarIsVisibled() {
		LinearLayout operation_line = (LinearLayout) findViewById(R.id.operation_line);
		LinearLayout user_detail_line = (LinearLayout) findViewById(R.id.user_detail_line);

		ListView listView = (ListView) findViewById(R.id.paired_devices);

		if (selectedDevices.size() > 0) {
			TextView selected_count = (TextView) findViewById(R.id.selected_count);
			int s = selectedDevices.size();
			selected_count.setText(Integer.toString(s));
			operation_line.setVisibility(View.VISIBLE);
			user_detail_line.setVisibility(View.GONE);
			listView.setClickable(true);
		} else {

			operation_line.setVisibility(View.GONE);
			user_detail_line.setVisibility(View.VISIBLE);
			listView.setClickable(false);
		}
	}

	List<BluetoothDevice> unpairedDeviceList = new ArrayList<BluetoothDevice>();

	public void clickedConfig(View view) {
		showPreferenceScreen();
	}

	private void showPreferenceScreen() {

		Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

		startActivityForResult(intent, EDIT_SETTING_COMPLETED);

	}

	public void clickedDeleteDevices(View view) {
		if (clickedDeleteDevicesInprocess)
			return;
		clickedDeleteDevicesInprocess = true;
		ImageView image = (ImageView) findViewById(R.id.delete_selected_item);
		Resources res = getResources();
		/** from an Activity */
		image.setImageDrawable(res.getDrawable(R.drawable.delete_item_proces));
		unpairedDeviceList.clear();
		for (String address : selectedDevices) {
			try {
				BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
				unpairedDeviceList.add(device);

			} catch (Exception ex) {

			}
		}

		for (BluetoothDevice device : unpairedDeviceList) {
			unpairDevice(device);
		}

		selectedDevices.clear();
		image.setImageDrawable(res.getDrawable(R.drawable.delete_item));
		clickedDeleteDevicesInprocess = false;
	}

	public void clickedDiscoverable(View v) {
		if ( mBtAdapter != null) {
		if (mBtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			String strTime = preferences.getString("pref_discoverable_time",
					"300");

			int time = Integer.parseInt(strTime);

			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
			startActivityForResult(discoverableIntent, DISCOVERABLE_FINISH);

		}
		}
	}

	public void clickedHelp(View view) {
		AboutDialog about = new AboutDialog(this);
		about.getWindow().setBackgroundDrawableResource(R.color.title_about);
		String txtAboutBox = getResources().getString(R.string.about_box_title);
		about.setTitle(txtAboutBox);
		about.show();
	}

	public void clickedNewDevice(View view) {
		// Launch the DeviceListActivity to see devices and do scan
		serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);

	}

	public void clickedReload(View view) {
		if (reloadListInprocess)
			return;
		reloadListInprocess = true;
		ImageView image = (ImageView) findViewById(R.id.ImageViewReload01);
		Resources res = getResources();
		/** from an Activity */
		image.setImageDrawable(res.getDrawable(R.drawable.reload_process));
		reloadList();
		image.setImageDrawable(res.getDrawable(R.drawable.reload));

	}

	public void clickedReload1(View view) {
		if (reloadListInprocess)
			return;
		reloadListInprocess = true;
		ImageView image = (ImageView) findViewById(R.id.reload_items);
		Resources res = getResources();
		/** from an Activity */
		image.setImageDrawable(res.getDrawable(R.drawable.reload_process));
		reloadList();
		image.setImageDrawable(res.getDrawable(R.drawable.reload));

	}

	private void clickedShowTestDeviceActivity(DeviceInfo deviceInfo) {
		String address = deviceInfo.getAddress();
		String name = deviceInfo.getName();

		// Make an intent to start next activity while taking an extra which is
		// the MAC address.
		// Intent i = new Intent(PairedListActivity.this,
		// TestDeviceActivity.class);
		// i.putExtra(EXTRA_DEVICE_ADDRESS, address);
		// i.putExtra(EXTRA_DEVICE_NAME, name);
		// startActivity(i);

	}

	public void clickedUnselectedItems(View view) {
		selectedDevices.clear();
		mMainAdapter.notifyDataSetChanged();
		checkOperationBarIsVisibled();
	}

	private void discovarableShowScanMode(boolean show) {

		if (show) {
			Animation myRotation = AnimationUtils.loadAnimation(
					getApplicationContext(), R.drawable.rotate);
			discovarebleImage.startAnimation(myRotation);

		} else {
			discovarebleImage.clearAnimation();
		}
	}

	public void discovarableStartTestTimer() {
		// set a new Timer
		discovarableTimerTest = new Timer();

		// initialize the TimerTask's job
		discovarableInitializeTimerTask();

		// schedule the timer
		discovarableTimerTest.schedule(discovarableTimerTestTask, 100, 2000); //
	}

	public synchronized void discovarableStopTimerTest() {
		// stop the timer, if it's not already null
		if (discovarableTimerTest != null) {
			discovarableTimerTest.cancel();
			discovarableTimerTest = null;
		}
	}

	public void discovarableInitializeTimerTask() {

		discovarableTimerTestTask = new TimerTask() {
			public void run() {

				// use a handler to run a toast that shows the current timestamp
				discovarableHandlerTimer.post(new Runnable() {
					public void run() {
						discovarableHandlerTimer
								.sendEmptyMessage(CHECK_DISCOVERABLE);

					}
				});
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode != Activity.RESULT_OK) {
				Toast.makeText(getBaseContext(),
						"Bluetooth is NOT Enabled!", Toast.LENGTH_SHORT)
						.show();
				finish();
			}		
			break;
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// connectDevice(data, true);
			}
			break;

		case DISCOVERABLE_FINISH:
			if ((resultCode == RESULT_OK)) {
				discovarableShowScanMode(true);
				discovarableStartTestTimer();
			}
			break;

		case EDIT_SETTING_COMPLETED: {
			// TODO EDIT_SETTING_COMPLETED
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			String prefDeviceName = preferences.getString("prefDeviceName", "");

			String prefDeviceNameCurrent = getLocalBluetoothName();

			if (!prefDeviceName.equals(prefDeviceNameCurrent)) {
				if (mBtAdapter != null ) {
				   mBtAdapter.setName(prefDeviceName);
			    }
			}

		}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public String getLocalBluetoothName() {
		String name = "";
        if (mBtAdapter != null) {
			name = mBtAdapter.getName();
		if (name == null) {
			name = mBtAdapter.getAddress();
		}
        }
		return name;
	}
	
    private boolean isBleSupportedOnDevice() {
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this, "BLUETOOTH is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isBleSupportedOnDevice()){
			finish();
		}
		setContentView(R.layout.main_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		 DisplayMetrics metrics = new DisplayMetrics();
		 getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 int widthPixels = metrics.widthPixels;
		 int heightPixels = metrics.heightPixels;
		 float xDpi = metrics.xdpi;
		 float yDpi = metrics.ydpi;
		 
		 Resources resources = getResources();
	    Configuration config = resources.getConfiguration();

		discovarebleImage = (ImageView) findViewById(R.id.discovarable_items);

		selectedDevices = new ArrayList<String>();

		ListView listView = (ListView) findViewById(R.id.paired_devices);
		listView.setLongClickable(true);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int pos, long id) {
				clickedToLine_LONG_SelectLine(parent, view, pos);
				return true;
			}

		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				DeviceInfo deviceInfo = (DeviceInfo) parent
						.getItemAtPosition(position);
				clickedShowTestDeviceActivity(deviceInfo);

				clickedEditDeviceInfo(deviceInfo);

				return;

			}

		});

		registerReceiver(mPairReceiver, new IntentFilter(
				BluetoothDevice.ACTION_BOND_STATE_CHANGED));
		restoreDevicesName();

	}

	private String result = "";
	private boolean canselDialog = false;

	private LinkedHashMap<String, String> customDevicesName = new LinkedHashMap<String, String>();

	public String getCustomDeviceName(String address) {
		if (customDevicesName.containsKey(address)) {
			return customDevicesName.get(address);
		}
		return "";
	}

	private void saveDevicesName() {
		String objectStr = JSONValue.toJSONString(customDevicesName);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("customDevicesName", objectStr);
		editor.apply();
		return;
	}

	private void restoreDevicesName() {
		customDevicesName.clear();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String jsonString = preferences.getString("customDevicesName", "");
		if (jsonString.length() > 0) {
			JSONParser parser = new JSONParser();

			ContainerFactory orderedKeyFactory = new ContainerFactory() {
				public List createArrayContainer() {
					return new LinkedList();
				}

				@Override
				public Map createObjectContainer() {
					return new LinkedHashMap();
				}

				@Override
				public List creatArrayContainer() {
					// TODO Auto-generated method stub
					return null;
				}

			};

			Object obj;
			try {
				obj = parser.parse(jsonString, orderedKeyFactory);
				customDevicesName = (LinkedHashMap) obj;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	protected void clickedEditDeviceInfo(DeviceInfo deviceInfo) {
		canselDialog = true;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.prompts_edit_device_name, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		alertDialogBuilder.setView(promptsView);

		TextView dialogTitle = (TextView) promptsView
				.findViewById(R.id.textOriginameDeviceName);
		dialogTitle.setText(deviceInfo.getName());

		TextView deviceAddress = (TextView) promptsView
				.findViewById(R.id.textDeviceAddress);
		deviceAddress.setText(deviceInfo.getAddress());

		final String keyName = deviceInfo.getAddress();

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogDeviceNameInput);

		if (customDevicesName.containsKey(keyName)) {
			result = customDevicesName.get(keyName);
		} else {
			result = deviceInfo.getName();
		}
		userInput.setText(result);

		// set dialog message
		alertDialogBuilder
				.setCancelable(true)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// get user input and set it to result
						// edit text
						result = userInput.getText().toString();

						if (customDevicesName.containsKey(keyName)) {
							customDevicesName.remove(keyName);
						}
						if (result.length() != 0) {
							customDevicesName.put(keyName, result);
						}

						saveDevicesName();
						canselDialog = false;
						mMainAdapter.notifyDataSetChanged();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	private void clickedToLine_LONG_SelectLine(AdapterView<?> parent,
			View view, int pos) {
		DeviceInfo deviceInfo = (DeviceInfo) parent.getItemAtPosition(pos);

		String address = deviceInfo.getAddress();
		selectLine(address, view);
		checkOperationBarIsVisibled();
	}

	@Override
	protected void onDestroy() {
		this.unregisterReceiver(mPairReceiver);
		super.onDestroy();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
		String[] array = savedInstanceState
				.getStringArray(SELECTED_DEVICES_KEY);

		selectedDevices.clear();

		for (int i = 0; i < array.length; i++) {
			String v = array[i];
			selectedDevices.add(v);
		}

		restoreDevicesName();
		checkOperationBarIsVisibled();
	}

	@Override
	protected void onPause() {
		this.discovarableStopTimerTest();

		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mBtAdapter = null;
		// Check device has Bluetooth and that it is turned on
		mBtAdapter = BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT
															// THAT IT WORKS!!!
		// ***************
		checkBTState();

		// Initialize array adapter for paired devices
		mMainAdapter = new MainAdapter(this, R.layout.device_name);
		mMainAdapter.selectedDevices = this.selectedDevices;
		mMainAdapter.mainActivity = this;

		// Find and set up the ListView for paired devices
		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mMainAdapter);

		// Get the local Bluetooth adapter
		if (mBtAdapter == null) {
			mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		}

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String prefDeviceName = preferences.getString("prefDeviceName", "");
		if (prefDeviceName.length() == 0) {
			SharedPreferences.Editor editor = preferences.edit();
			prefDeviceName = getLocalBluetoothName();
			editor.putString("prefDeviceName", prefDeviceName);
			editor.apply();
		}

		TextView main_title_id = (TextView) findViewById(R.id.main_title_id);
		main_title_id.setText(prefDeviceName);

		if (mBtAdapter != null) {
			// Get a set of currently paired devices and append to
			// 'pairedDevices'
			Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

			// Add previosuly paired devices to the array

			for (BluetoothDevice device : pairedDevices) {
				mMainAdapter.addDevice(device.getName(), device.getAddress());
			}
			checkOperationBarIsVisibled();

			if (mBtAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
				this.discovarableShowScanMode(true);
			}
		}

		// onResume we start our timer so it can start when the app comes from
		// the background
		discovarableStartTestTimer();

	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {

		super.onSaveInstanceState(savedInstanceState);
		String[] array = new String[selectedDevices.size()];

		int pos = 0;
		for (String v : selectedDevices) {
			array[pos] = v;
			pos++;
		}

		saveDevicesName();
		savedInstanceState.putStringArray(SELECTED_DEVICES_KEY, array);
	}

	private void pairDevice(BluetoothDevice device) {
		try {

			// waitingForBonding = true;

			Method m = device.getClass()
					.getMethod("createBond", (Class[]) null);
			m.invoke(device, (Object[]) null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void reloadList() {

		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		selectedDevices.clear();

		// Add previosuly paired devices to the array
		mMainAdapter.clear();
		for (BluetoothDevice device : pairedDevices) {
			mMainAdapter.addDevice(device.getName(), device.getAddress());
		}
		mMainAdapter.notifyDataSetChanged();
		checkOperationBarIsVisibled();
		reloadListInprocess = false;

	}

	private void selectLine(String address, View view) {
		if (selectedDevices.contains(address)) {
			selectedDevices.remove(address);
		} else {
			selectedDevices.add(address);
		}
		Object o = view.findViewById(R.id.message_sel_line);
		LinearLayout message_sel_line = (LinearLayout) o;

		MainAdapter.markSelectedLine(selectedDevices, message_sel_line,
				address, view);
	}

	private void unpairDevice(BluetoothDevice device) {
		try {
			Method m = device.getClass()
					.getMethod("removeBond", (Class[]) null);
			m.invoke(device, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}