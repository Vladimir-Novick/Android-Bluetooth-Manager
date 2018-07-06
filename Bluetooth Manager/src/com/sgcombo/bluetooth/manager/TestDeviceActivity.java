package com.sgcombo.bluetooth.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
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
public class TestDeviceActivity extends Activity {
    
  Button btnOn;
  TextView txtArduino, txtString, txtStringLength;
  Handler bluetoothIn;

  final int handlerState = 0;        				 //used to identify handler message
  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  private StringBuilder recDataString = new StringBuilder();
   
  private ConnectedThread mConnectedThread;
    
  // SPP UUID service - this should work for most devices
  private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

  
  // String for MAC address
  private static String address;

@Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  
    setContentView(R.layout.activity_main);
    
    
  
    //Link the buttons and textViews to respective views 
    btnOn = (Button) findViewById(R.id.buttonOn);                
           
    txtString = (TextView) findViewById(R.id.txtString); 
    txtStringLength = (TextView) findViewById(R.id.testView1);   

    bluetoothIn = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == handlerState) {										//if message is what we want
            	String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                recDataString.append(readMessage);      								//keep appending to string until ~
                int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                    txtString.setText("Data Received = " + dataInPrint);           		
                    int dataLength = dataInPrint.length();							//get length of data received
                    txtStringLength.setText("String Length = " + String.valueOf(dataLength));
                    recDataString.delete(0, recDataString.length()); 					//clear all string data 
                    dataInPrint = " ";
                }            
            }
        }
    };
      
    btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
    checkBTState();	
 
    btnOn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) { 
        Toast.makeText(getBaseContext(), "Turn on Remote Shutter", Toast.LENGTH_SHORT).show();
      }
    });
  }



@Override
protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onRestoreInstanceState(savedInstanceState);

}



@Override
protected void onSaveInstanceState(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onSaveInstanceState(savedInstanceState);

}






   
  private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
      
      return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
      //creates secure outgoing connecetion with BT device using UUID
  }
    
  @Override
  public void onResume() {
    super.onResume();
    
    //Get MAC address from DeviceListActivity via intent
    Intent intent = getIntent();
    
    //Get the MAC address from the DeviceListActivty via EXTRA
    address = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

    //create device and set the MAC address
    BluetoothDevice device = btAdapter.getRemoteDevice(address);
    
     
     
    try {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        btSocket = device.createRfcommSocketToServiceRecord(uuid);   
    } catch (IOException e) {
    	Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
    }  
    // Establish the Bluetooth socket connection.
/*    try 
    {
      btSocket.connect();
    } catch (IOException e) {
      try 
      {
        btSocket.close();
    	Toast.makeText(getBaseContext(), "Socket connect failed", Toast.LENGTH_LONG).show();
      } catch (IOException e2) 
      {
    	//insert code to deal with this 
      } 
    } */ 
    mConnectedThread = new ConnectedThread(btSocket);
    mConnectedThread.start();
    
    //I send a character when resuming.beginning transmission to check device is connected
    //If it is not an exception will be thrown in the write method and finish() will be called
  //  mConnectedThread.write("x");
  }
  
  @Override
  public void onPause() 
  {
    super.onPause();
    try
    {
    //Don't leave Bluetooth sockets open when leaving activity
      btSocket.close();
    } catch (IOException e2) {
    	//insert code to deal with this 
    }
  }

 //Checks that the Android device Bluetooth is available and prompts to be turned on if off 
  private void checkBTState() {
 
    if(btAdapter==null) { 
    	Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
    } else {
      if (btAdapter.isEnabled()) {
      } else {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);
      }
    }
  }
  
  //create new class for connect thread
  private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
      
        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
            	//Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
      
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        
      
        public void run() {
            byte[] buffer = new byte[256];  
            int bytes; 
 
            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget(); 
                } catch (IOException e) {
                    break;
                }
            }
        }

    	}
}
    
