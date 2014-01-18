/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the Sony Ericsson Mobile Communications AB nor the names
  of its contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.sonyericsson.extras.liveware.extension.sensorsample;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The extension receiver receives the extension intents and starts the
 * extension service when it arrives.
 */
//public class HostReceiver extends BroadcastReceiver {
//
//	public float x;
//	public float y;
//	public float z;
//	
//	public TextView dataText;
//	public FrameLayout dataFrame;
//	public ImageView imgView;
//	
//	public Activity mActivity;
//	
//	public HostReceiver()
//	{
//		
//	}
//	
//	public HostReceiver(Activity activity) {
//		// TODO Auto-generated constructor stub
//		this.mActivity = activity;
//	}
//	
//	public HostReceiver(TextView text, FrameLayout frame, ImageView img)
//	{
//		this.dataText = text;
//		this.dataFrame = frame;
//		this.imgView = img;
//	}
//	
//	@SuppressLint("NewApi")
//	@Override
//    public void onReceive(final Context context, final Intent intent) {
//		try {
//        Log.d(SampleExtensionService.LOG_TAG + "Recv", "onHostReceive: " + intent.getAction() + " Data: " + intent.getFloatExtra("x",0) + " " + intent.getFloatExtra("y",0) + " " + intent.getFloatExtra("z", 0));
//        x = intent.getFloatExtra("x", 0);
//        y = intent.getFloatExtra("y", 0);
//        z = intent.getFloatExtra("z", 0);
//        
//        long eventtime = SystemClock.uptimeMillis(); 
//        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
//        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
//        KeyEvent downEvent = null;
//        KeyEvent upEvent = null;
//        
//        
//        if(z>8)
//        {
//            downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
//
//            upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
//        }
//        else if(z<-8)
//        {
//            downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE, 0);
//
//            upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE, 0);
//        }
//        
////        context.getApplicationContext().
////        TextView dataText = (TextView)((this.mActivity).findViewById(R.id.sensorDataView));
////        FrameLayout dataFrame = (FrameLayout)(this.mActivity).findViewById(R.id.sensorVDataView);
////        ImageView imgView = (ImageView)(this.mActivity).findViewById(R.id.imageView1);
//        context.getApplicationContext()
//        dataText.setText(String.format("x=%.1f y=%.1f z=%.1f",x,y,z));
////        imgView.setX(x*10);
////        imgView.setY(y*10);
//        
////        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
////        context.sendOrderedBroadcast(downIntent, null); 
////        
////        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent); 
////        context.sendOrderedBroadcast(upIntent, null); 
//		}
//		catch (Exception e)
//		{
//			Log.d(SampleExtensionService.LOG_TAG, e.toString());
//		}
//    }
//}
