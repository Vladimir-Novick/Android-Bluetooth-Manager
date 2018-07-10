package com.sgcombo.bluetooth.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/*
Copyright (C) 2012 by Vladimir Novick http://www.linkedin.com/in/vladimirnovick , 

     vlad.novick@gmail.com , http://www.sgcombo.com , https://github.com/Vladimir-Novick

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
public class NewDeviceListActivity extends Activity {

    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;
    private static final String SELECTED_DEVICES_KEY = "SELECTED_DEVICES_KEY";
    
  
    // declare button for launching website and textview for connection status
    Button tlbutton;
    TextView textView1;
    
    // EXTRA string to send on to mainactivity
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_DEVICE_NAME  = "device_name";

    // Member fields

    private MainAdapter pairedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_device_list);
        
        selectedDevices = new ArrayList<String>();

    	ListView listView = (ListView) findViewById(R.id.paired_devices);


    	listView.setOnItemClickListener(new OnItemClickListener() {

    		@Override
    		public void onItemClick(AdapterView<?> parent, View view,
    				int position, long id) {
    			
    			DeviceInfo deviceInfo = (DeviceInfo)parent.getItemAtPosition(position);
    			clickedPairDevice(deviceInfo);
   
    			return;

    		}

    	});
    	

        
    }
    
    private  void clickedPairDevice(DeviceInfo deviceInfo){
		String address = deviceInfo.getAddress();
		String name = deviceInfo.getName();
		
    }
    
	private void selectLine(String address, View view) {
		if (selectedDevices.contains(address)) {
			selectedDevices.remove(address);
		} else {
			selectedDevices.add(address);
		}
		Object o = view.findViewById(R.id.message_sel_line);
    	LinearLayout message_sel_line = (LinearLayout) o;
    	

    	MainAdapter.markSelectedLine(selectedDevices,message_sel_line, address,view);
	}
    
    public List<String> selectedDevices = new ArrayList<String>();  

 @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		String[] array = savedInstanceState.getStringArray(SELECTED_DEVICES_KEY);

		selectedDevices.clear();

		for (int i = 0; i < array.length; i++) {
			String v = array[i];
			selectedDevices.add(v);
		}

		checkOperationBarIsVisibled();
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

		savedInstanceState.putStringArray(SELECTED_DEVICES_KEY, array);
	}


	private void pairDevice(BluetoothDevice device) {
		try {

		 //   waitingForBonding = true;

		    Method m = device.getClass()
		        .getMethod("createBond", (Class[]) null);
		    m.invoke(device, (Object[]) null);


		} catch (Exception e) {
		    e.printStackTrace();
		}
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

 
 boolean clickedDeleteDevicesInprocess = false;
 
 public void clickedDeleteDevices(View view){
	 if (clickedDeleteDevicesInprocess) return;
	 clickedDeleteDevicesInprocess = true;
	 ImageView  image =  (ImageView) findViewById(R.id.delete_selected_item);
	 Resources res = getResources(); /** from an Activity */
	 image.setImageDrawable(res.getDrawable(R.drawable.delete_item_proces));
		for (String address : selectedDevices) {
			// TODO - unpair device
			
			try {
			BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
		
			unpairDevice(device);
			
			}  catch ( Exception ex){
				
			}
		}
		reloadList();
		 image.setImageDrawable(res.getDrawable(R.drawable.delete_item));
		 clickedDeleteDevicesInprocess = false;
 }
 
 public void clickedUnselectedItems(View view){
	 selectedDevices.clear();
	 mPairedDevicesArrayAdapter.notifyDataSetChanged();
	 checkOperationBarIsVisibled();
 }
 
 public void clickedNewDevice(View view){
	 // TODO - ADD new device and pair it
 }
 
 
 public void clickedReload(View view) {
	 if (reloadListInprocess) return;
	 reloadListInprocess = true;
	 ImageView  image =  (ImageView) findViewById(R.id.ImageViewReload01);
	 Resources res = getResources(); /** from an Activity */
	 image.setImageDrawable(res.getDrawable(R.drawable.reload_process));
 	reloadList();
 	 image.setImageDrawable(res.getDrawable(R.drawable.reload));
 	
 }
 
 public void clickedReload1(View view) {
	 if (reloadListInprocess) return;
	 reloadListInprocess = true;
	 ImageView  image =  (ImageView) findViewById(R.id.reload_items);
	 Resources res = getResources(); /** from an Activity */
	 image.setImageDrawable(res.getDrawable(R.drawable.reload_process));
 	reloadList();
 	 image.setImageDrawable(res.getDrawable(R.drawable.reload));
 	
 }

 boolean reloadListInprocess = false;
 
private void reloadList() {

	 
 	Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
 	selectedDevices.clear();

 	// Add previosuly paired devices to the array
 	    mPairedDevicesArrayAdapter.clear();
 		for (BluetoothDevice device : pairedDevices) {
 			mPairedDevicesArrayAdapter.addDevice(device.getName(),device.getAddress());
 		}
 		mPairedDevicesArrayAdapter.notifyDataSetChanged();
 		checkOperationBarIsVisibled();
 		reloadListInprocess = false;
 	
}
 
 // Member fields
 private BluetoothAdapter mBtAdapter;
 private MainAdapter mPairedDevicesArrayAdapter;
    
    @Override
    public void onResume() 
    {
    	super.onResume();
    	//*************** 
    	checkBTState();



     	// Initialize array adapter for paired devices
    	mPairedDevicesArrayAdapter = new MainAdapter(this, R.layout.device_name);
    	mPairedDevicesArrayAdapter.selectedDevices = this.selectedDevices;

    	// Find and set up the ListView for paired devices
    	ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
    	pairedListView.setAdapter(mPairedDevicesArrayAdapter);


    	// Get the local Bluetooth adapter
    	mBtAdapter = BluetoothAdapter.getDefaultAdapter();

    	// Get a set of currently paired devices and append to 'pairedDevices'
    	Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

    	// Add previosuly paired devices to the array

    		for (BluetoothDevice device : pairedDevices) {
    			mPairedDevicesArrayAdapter.addDevice(device.getName(),device.getAddress());
    		}
    		checkOperationBarIsVisibled();
  }

 
    private void checkBTState() {
        // Check device has Bluetooth and that it is turned on
    	 mBtAdapter=BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBtAdapter==null) { 
        	Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
          if (mBtAdapter.isEnabled()) {
            Log.d(TAG, "...Bluetooth ON...");
          } else {
            //Prompt user to turn on Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
 
            }
          }
        }
}