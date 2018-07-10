package com.sgcombo.bluetooth.manager;

import java.util.ArrayList;
import java.util.List;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

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
public class DeviceListAdapter extends BaseAdapter{
	private LayoutInflater mInflater;	
	private List<BluetoothDevice> mData;
	private OnPairButtonClickListener mListener;
	
	public DeviceListAdapter(Context context) { 
        mInflater = LayoutInflater.from(context); 
        mData = new ArrayList<BluetoothDevice>();
    }
	
	public void add(BluetoothDevice device) {
		mData.add(device);
		notifyDataSetChanged();
	
	}
	
	
	private boolean isDiscovered = true;
	
	public void setAsDiscover(boolean dis){
		isDiscovered = dis;
	}
	
	
	public void setListener(OnPairButtonClickListener listener) {
		mListener = listener;
	}
	
	public int getCount() {
		return (mData == null) ? 0 : mData.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {			
			convertView			=  mInflater.inflate(R.layout.list_item_device, null);
			
			holder 				= new ViewHolder();
			
			holder.nameTv		= (TextView) convertView.findViewById(R.id.tv_name);
			holder.addressTv 	= (TextView) convertView.findViewById(R.id.tv_address);
			holder.pairBtn		= (Button) convertView.findViewById(R.id.btn_pair);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		BluetoothDevice device	= mData.get(position);
		
		holder.nameTv.setText(device.getName());
		holder.addressTv.setText(device.getAddress());
		boolean b = (device.getBondState() == BluetoothDevice.BOND_BONDED);
		if (b) {
			holder.pairBtn.setText( "Unpair" );
			holder.pairBtn.setBackgroundResource(R.drawable.layers_line_button_paired_bg);
		} else {
			if (isDiscovered) {
			    holder.pairBtn.setText( "....");
			    holder.pairBtn.setBackgroundResource(R.drawable.layers_line_button_wait_bg);
			} else {
		       holder.pairBtn.setText( "Pair");
		       holder.pairBtn.setBackgroundResource(R.drawable.layers_line_button_nopair_bg);
			}
			 

		}
	
	
		holder.pairBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onPairButtonClick(position);
				}
			}
		});
		
        return convertView;
	}

	static class ViewHolder {
		TextView nameTv;
		TextView addressTv;
		TextView pairBtn;
	}
	
	public interface OnPairButtonClickListener {
		public abstract void onPairButtonClick(int position);
	}

	public BluetoothDevice get(int position) {
		BluetoothDevice device	= mData.get(position);
		return device;
	}

	public void clear() {
		mData.clear();
		notifyDataSetChanged();
		
	}
}