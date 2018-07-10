package com.sgcombo.bluetooth.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
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
public class MainAdapter extends ArrayAdapter<DeviceInfo> {
	
	public MainAdapter(Context context, int textViewResourceId) {

    super(context, textViewResourceId);
}


public MainActivity mainActivity ;	

private Context mContext;

public MainAdapter(Context context, int resource, List<DeviceInfo> contactItems) {
    super(context, resource, contactItems);
    mContext = context;
}


public List<String> selectedDevices ;

public static void markSelectedLine(List<String> selectedDevices,LinearLayout message_sel_line, String address, View v) {
	

   	LinearLayout message_line2 = (LinearLayout)  v.findViewById(R.id.message_line2);
	
	if (selectedDevices.contains(address)) {
		message_sel_line.setBackgroundResource(R.drawable.main_selected_background_line);
		

		
		
	} else {
		message_sel_line.setBackgroundResource(R.drawable.main_background_line);

	}
	
	

	
}

private int getImageResource( BluetoothDevice device){
	
	int ret = R.drawable.device_type_uncategorized;
	
	if (device == null) return ret;
	
	   BluetoothClass bluetoothClass = device.getBluetoothClass();

    if (bluetoothClass == null) {
    	return ret;
    }
    int majorDeviceClass = bluetoothClass.getMajorDeviceClass();

	
  
  //  int deviceClassType = bluetoothClass.getDeviceClass();
    
 //   deviceClassType like  BluetoothClass.Device.
    
    switch (majorDeviceClass) {
    case BluetoothClass.Device.Major.AUDIO_VIDEO: 
    	ret = R.drawable.device_type_audio_video;
        break;
    case BluetoothClass.Device.Major.COMPUTER:
    	ret = R.drawable.device_type_computer;
        break;      
    case BluetoothClass.Device.Major.HEALTH:
    	ret = R.drawable.device_type_audio_heath;
        break;  
    case BluetoothClass.Device.Major.IMAGING:
    	ret = R.drawable.device_type_imageing;
        break;  
    case BluetoothClass.Device.Major.NETWORKING:
    	ret = R.drawable.device_type_networking;
        break;  
    case BluetoothClass.Device.Major.PERIPHERAL:
    	ret = R.drawable.device_type_peripheral;
        break;  
    case BluetoothClass.Device.Major.PHONE:
    	ret = R.drawable.device_type_phone;
        break;  
    case BluetoothClass.Device.Major.TOY:
    	ret = R.drawable.device_type_toy;
        break;      
    case BluetoothClass.Device.Major.UNCATEGORIZED:
    	ret = R.drawable.device_type_uncategorized;
        break;   
    case BluetoothClass.Device.Major.WEARABLE:
    	ret = R.drawable.device_type_wearable;
        break;           
    }
    
    return ret;
}


@Override
public View getView(int position, View convertView, ViewGroup parent) {

    View v = convertView;

    if (v == null) {

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        v = vi.inflate(R.layout.main_activity_screen_line, null);
     
    }

    
    
    DeviceInfo p = getItem(position);
    

    
    
    int lineN = position%2;

    if (p != null) {

 
		Object o = v.findViewById(R.id.message_sel_line);
    	LinearLayout message_sel_line = (LinearLayout) o;
    	
      	

    	String address = p.getAddress();
    	MainAdapter.markSelectedLine(selectedDevices,message_sel_line, address,v);
   
    	
    	String textLine = p.getName();

       	
     	TextView message =  (TextView) v.findViewById(R.id.device_name);
     	
     	  SpannableString textSpan = new SpannableString(textLine);
     	  
    
   	 message.setText(textSpan, BufferType.SPANNABLE);



 	 textLine = p.getAddress();
 	 
 	textLine = mainActivity.getCustomDeviceName(textLine);

   	
 	 message =  (TextView) v.findViewById(R.id.device_address);
 	
 	  textSpan = new SpannableString(textLine);
 	  

	 message.setText(textSpan, BufferType.SPANNABLE);


    	
	 ImageView imageView = (ImageView)v.findViewById(R.id.imageView1);
	 
	 BluetoothDevice device = mainActivity.getActiveDevice(p.getAddress());
	
	 
	 int imageID = getImageResource(device);	 
     imageView.setImageResource(imageID);
      
    //	message_line

    }

   
    LinearLayout vt = (LinearLayout)v;
    vt.setTag(p);
    
    return v;
}



public void delete(DeviceInfo p) {
	this.remove(p);
	
}


public void addDevice(String name, String address) {
	DeviceInfo deviceInfo = new DeviceInfo();
	deviceInfo.setName(name);
	deviceInfo.setAddress(address);
	this.add(deviceInfo);
	
}
}