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

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import android.widget.Toast;

/**
 * The extension receiver receives the extension intents and starts the
 * extension service when it arrives.
 */
public class ExtensionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(SampleExtensionService.LOG_TAG + "Recv", "onExtReceive: " + intent.getAction() + " Data: " + intent.getExtras().getString(Control.Intents.EXTRA_DATA_URI));
        if(intent.getAction()==Control.Intents.CONTROL_RESUME_INTENT)
        {
			Intent i = new Intent();
			i.setClassName(context, MainActivity.class.getName());
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			context.getApplicationContext().sendBroadcast(new Intent("com.sonyericsson.extras.liveware.extension.sensorsample.startApp"));
        }
        if(intent.getAction()!="SDATA")
        {
        	intent.setClass(context, SampleExtensionService.class);
        	context.startService(intent);
        }
        String toastMessage = "Action Toast" + ", Event: " + intent.getIntExtra(Notification.Intents.EXTRA_EVENT_ID, -1)
                + ", Action: " + intent.getAction() + ", Message: " + intent.getDataString();
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
