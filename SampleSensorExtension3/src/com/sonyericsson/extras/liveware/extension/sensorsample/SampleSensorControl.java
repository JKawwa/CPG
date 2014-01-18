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

import java.net.URI;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.aef.registration.Registration.Extension;
import com.sonyericsson.extras.liveware.aef.registration.Registration.HostApp;
import com.sonyericsson.extras.liveware.aef.sensor.Sensor;
import com.sonyericsson.extras.liveware.aef.sensor.Sensor.SensorAccuracy;
import com.sonyericsson.extras.liveware.aef.sensor.Sensor.SensorInterruptMode;
import com.sonyericsson.extras.liveware.aef.sensor.Sensor.SensorRates;
import com.sonyericsson.extras.liveware.extension.util.AefTextView;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.SmartWatchConst;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensor;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorEvent;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorEventListener;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorException;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The sample sensor control handles the accelerometer sensor on an accessory.
 * This class exists in one instance for every supported host application that
 * we have registered to
 */
class SampleSensorControl extends ControlExtension {

    public static final int WIDTH = 128;

    public static final int HEIGHT = 128;
    
    public static final String sData = "com.sonyericsson.extras.liveware.extension.sensorsample.sData";

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.RGB_565;

    private AccessorySensor mSensor = null;
    
    private float timestamp = 0;
    

    private final AccessorySensorEventListener mListener = new AccessorySensorEventListener() {

        public void onSensorEvent(AccessorySensorEvent sensorEvent) {
            updateDisplay(sensorEvent);
        }
    };

    /**
     * Create sample sensor control.
     *
     * @param hostAppPackageName Package name of host application.
     * @param context The context.
     */
    SampleSensorControl(final String hostAppPackageName, final Context context) {
        super(context, hostAppPackageName);

        AccessorySensorManager manager = new AccessorySensorManager(context, hostAppPackageName);
        mSensor = manager.getSensor(Sensor.SENSOR_TYPE_ACCELEROMETER);
    }
    
    @Override
    public void onResume() {
        Log.d(SampleExtensionService.LOG_TAG, "Starting control");
        // Note: Setting the screen to be always on will drain the accessory
        // battery. It is done here solely for demonstration purposes
        setScreenState(Control.Intents.SCREEN_STATE_ON);

        // Start listening for sensor updates.
        if (mSensor != null) {
            try {
            	Log.d(SampleExtensionService.LOG_TAG, "Registering Listener");
                mSensor.registerInterruptListener(mListener);
            } catch (AccessorySensorException e) {
                Log.d(SampleExtensionService.LOG_TAG, "Failed to register listener");
            }
        }

        updateDisplay(null);

    }

    @Override
    public void onPause() {
        // Stop sensor
        if (mSensor != null) {
            mSensor.unregisterListener();
        }
    }

    @Override
    public void onDestroy() {
        // Stop sensor
        if (mSensor != null) {
            mSensor.unregisterListener();
            mSensor = null;
        }
    }
    
    

    /**
     * Update the display with new accelerometer data.
     *
     * @param sensorEvent The sensor event.
     */
    private void updateDisplay(AccessorySensorEvent sensorEvent) {
        // Create bitmap to draw in.
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, BITMAP_CONFIG);

        // Set default density to avoid scaling.
        bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        LinearLayout root = new LinearLayout(mContext);
        root.setLayoutParams(new LayoutParams(WIDTH, HEIGHT));

        LinearLayout sampleLayout = (LinearLayout)LinearLayout.inflate(mContext,
                R.layout.accelerometer_values, root);

        // Update the values.
        if (sensorEvent != null) {
            float[] values = sensorEvent.getSensorValues();

            if (values != null && values.length == 3) {
                TextView xView = (TextView)sampleLayout.findViewById(R.id.accelerometer_value_x);
                TextView yView = (TextView)sampleLayout.findViewById(R.id.accelerometer_value_y);
                TextView zView = (TextView)sampleLayout.findViewById(R.id.accelerometer_value_z);

                // Show values with one decimal.
                xView.setText(String.format("%.1f", values[0]));
                yView.setText(String.format("%.1f", values[1]));
                zView.setText(String.format("%.1f", values[2]));
                
//                if((sensorEvent.getTimestamp() - timestamp) > 200)
                {
                	sendData(values);
                }

            }
            //mContext.sendBroadcast(test);

            //sendToHostApp(new Intent(Control.Intents.CONTROL_CLEAR_DISPLAY_INTENT));

//            Toast bla = Toast.makeText(mContext, data.toString() , Toast.LENGTH_SHORT);
//            bla.show();

            // Show time stamp in milliseconds. (Reading is in nanoseconds.)
            TextView timeStampView = (TextView)sampleLayout
                    .findViewById(R.id.accelerometer_value_timestamp);
            timeStampView.setText(String.format("%d", (long)(sensorEvent.getTimestamp() / 1e6)));

            // Show sensor accuracy.
            TextView accuracyView = (TextView)sampleLayout
                    .findViewById(R.id.accelerometer_value_accuracy);
            accuracyView.setText(getAccuracyText(sensorEvent.getAccuracy()));
        }

        sampleLayout.measure(WIDTH, HEIGHT);
        sampleLayout
                .layout(0, 0, sampleLayout.getMeasuredWidth(), sampleLayout.getMeasuredHeight());

        Canvas canvas = new Canvas(bitmap);
        sampleLayout.draw(canvas);

        showBitmap(bitmap);
    }

    private void sendData(float[] values) {
		// TODO Auto-generated method stub
        //Send Values to Host
        Uri data = Uri.parse(String.format("x:%.1f y:%.1f z:%.1f",values[0],values[1],values[2]));
//        Log.d(SampleExtensionService.LOG_TAG,data.toString());
        Intent test = new Intent(sData);
        test.putExtra("x", values[0]);
        test.putExtra("y", values[1]);
        test.putExtra("z",values[2]);
//        test.setData(data);
//        test.setData(data);
        mContext.sendBroadcast(test);
//        mContext.sendBroadcast(test, Registration.HOSTAPP_PERMISSION);
	}

	/**
     * Convert an accuracy value to a text.
     *
     * @param accuracy The accuracy value.
     * @return The text.
     */
    private String getAccuracyText(int accuracy) {

        switch (accuracy) {
            case SensorAccuracy.SENSOR_STATUS_UNRELIABLE:
                return mContext.getString(R.string.accuracy_unreliable);
            case SensorAccuracy.SENSOR_STATUS_ACCURACY_LOW:
                return mContext.getString(R.string.accuracy_low);
            case SensorAccuracy.SENSOR_STATUS_ACCURACY_MEDIUM:
                return mContext.getString(R.string.accuracy_medium);
            case SensorAccuracy.SENSOR_STATUS_ACCURACY_HIGH:
                return mContext.getString(R.string.accuracy_high);
            default:
                return String.format("%d", accuracy);
        }
    }
    
    @Override
    public void onTouch(ControlTouchEvent event) {
    	// TODO Auto-generated method stub
    	super.onTouch(event);
    }
    
}
