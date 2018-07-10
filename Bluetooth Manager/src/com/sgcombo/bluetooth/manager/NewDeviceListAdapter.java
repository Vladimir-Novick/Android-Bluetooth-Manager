package com.sgcombo.bluetooth.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
public class NewDeviceListAdapter extends ArrayAdapter<DeviceInfo> {
	
	public NewDeviceListAdapter(Context context, int textViewResourceId) {

    super(context, textViewResourceId);
}



private Context mContext;

public NewDeviceListAdapter(Context context, int resource, List<DeviceInfo> contactItems) {
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




@Override
public View getView(int position, View convertView, ViewGroup parent) {

    View v = convertView;

    if (v == null) {

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        v = vi.inflate(R.layout.new_device_list_line, null);
     
    }

    
    
    DeviceInfo p = getItem(position);
    

    
    int lineN = position%2;

    if (p != null) {

    	
   
    	
//       	TextView address_view =  (TextView) v.findViewById(R.id.sms_address);
 //      	address_view.setText(p.getAddress());
    	
    	
    	
    	

    
    	
    	
    	
    	
 
		Object o = v.findViewById(R.id.message_sel_line);
    	LinearLayout message_sel_line = (LinearLayout) o;
    	
      	

    	String address = p.getAddress();
    	NewDeviceListAdapter.markSelectedLine(selectedDevices,message_sel_line, address,v);
   
    	
    	String textLine = p.getName();

       	
     	TextView message =  (TextView) v.findViewById(R.id.device_name);
     	
     	  SpannableString textSpan = new SpannableString(textLine);
     	  
    
   	 message.setText(textSpan, BufferType.SPANNABLE);



 	 textLine = p.getAddress();

   	
 	 message =  (TextView) v.findViewById(R.id.device_address);
 	
 	  textSpan = new SpannableString(textLine);
 	  

	 message.setText(textSpan, BufferType.SPANNABLE);


    	
    	

      
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