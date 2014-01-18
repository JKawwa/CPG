package com.sonyericsson.extras.liveware.extension.sensorsample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.EventListener;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Contacts.Intents;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Media;
import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.aef.registration.Registration.HostApp;
import com.sonyericsson.extras.liveware.aef.sensor.Sensor;
import com.sonyericsson.extras.liveware.extension.util.registration.HostApplicationInfo;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensor;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorManager;
import com.sonyericsson.extras.liveware.extension.sensorsample.Client;

public class MainActivity extends Activity {

	public static Client conclient;
	public static MediaPlayer mp;
	public static String currentSongTitle;
	public static String playlist;
	
	TextView text = null;
	FrameLayout frame = null;
	ImageView img = null;
	public HostReceiver nHR;
	public IntentFilter nIF;
	Boolean searched = false;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (android.os.Build.VERSION.SDK_INT > 7) { StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy); }
		
		try
		{
    	String[] proj = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, Audio.Media.DATA};
//    	Uri playlistUri = Uri.parse("content://com.google.android.music.MusicContent/audio");
    	Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	Cursor cursor = getContentResolver().query(musicUri, proj, null, null, null);
	
			int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
        	int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        	int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        	int dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
//        	int sourceIdColumn = 3;
//        	int hasLocalColumn = 4;
//        	int dataColumn = 3;
        	long thisSource;
        	long thisId;
        	if (cursor == null) {
        	    // query failed, handle error.
        	} else if (!cursor.moveToFirst()) {
        	    // no media on the device
        	} else {
            do {
               thisId = cursor.getLong(idColumn);
               String thisTitle = cursor.getString(titleColumn);
               String thisArtist = cursor.getString(artistColumn);
//               thisSource = cursor.getLong(sourceIdColumn);
//               int thisLocal = cursor.getInt(hasLocalColumn);
               
               // ...process entry...
               Log.d("Music","Found: " + thisId + ":" + thisArtist + ":" + thisTitle/* + ":" + thisSource + ":" + thisLocal*/);
            } while (cursor.moveToNext());
            
            Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
            
//			String path = yourCursor.getString(getColumnIndex(Audio.Media.DATA));

			MainActivity.mp = new MediaPlayer();
			MainActivity.mp.setDataSource(getApplicationContext(),contentUri);
			MainActivity.mp.prepare();
//			mp.
        	}
//	        Intent intent2 = new Intent(Intent.ACTION_MAIN);
//	        intent2.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
//	        intent2.putExtra(SearchManager.QUERY,"yeah yeah");
//	        startActivity(intent2);
		}
		catch(Exception e)
		{
			{
				Toast test = Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
				test.show();
				e.printStackTrace();
			}
		}
		
//		mp = new MediaPlayer();
		Log.d("Test", "Created Media Player");
//		Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 24982);
//        
//		//	        					String path = yourCursor.getString(getColumnIndex(Audio.Media.DATA));
//		
//		try {
//			mp.setDataSource(getApplicationContext(),contentUri);
//			mp.prepare();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		startService(new Intent(this,MediaListener.class));
		text = (TextView)findViewById(R.id.sensorDataView);
		text.setText("Test");
		frame = (FrameLayout)findViewById(R.id.sensorVDataView);
		img = (ImageView)findViewById(R.id.imageView1);
//		nHR = new HostReceiver(text,frame,img);
		new ClientInit().execute("Awesome");
		while(conclient == null)
			;
		nHR = new HostReceiver();
		nIF = new IntentFilter(SampleSensorControl.sData);
		nIF.addAction("com.sonyericsson.extras.liveware.extension.sensorsample.startApp");
		registerReceiver(nHR, nIF);
		
//		drawData(nHR.x,nHR.y,nHR.z);

		
//		AccessorySensor mSensor = null;
//		 AccessorySensorManager manager = new AccessorySensorManager(getBaseContext(), );
//	        mSensor = manager.getSensor(Sensor.SENSOR_TYPE_ACCELEROMETER);
			
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		registerReceiver(nHR, nIF);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver(nHR);
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
//		BroadcastReceiver test = new BroadcastReceiver() {
//
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				// TODO Auto-generated method stub
//				//if(intent.getAction()=="ACC_VALUES")
//					Toast.makeText(context, "IT WORKS", Toast.LENGTH_LONG);
//					
//			}
//			
//		};
		return true;
	}
	
	class ClientInit extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			String host = "192.168.43.23"; // The host name can either be a machine name, such as "java.sun.com", or a textual representation of its IP address
			int port = 27021;
			BufferedReader reader = null;
			PrintWriter writer = null;
			boolean connected = false;
			Socket socket = null;
			while(!connected)
			{
				try {
				
					SocketAddress test = new InetSocketAddress(host,port);

					socket = new Socket();
					socket.connect(test,5000);
					Log.d("Test", "Created Socket");
					connected= true;
	//				Toast.makeText(getApplicationContext(), "Created Socket", Toast.LENGTH_SHORT).show();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.d("Test",socket.toString());
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				Log.d("Test","Retrieved Input Stream");
//				Toast.makeText(getApplicationContext(), "Retrieved Input Stream", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer = new PrintWriter(socket.getOutputStream(), true);
				Log.d("Test","Retrieved Output Stream");
//				Toast.makeText(getApplicationContext(), "Retrieved Output Stream", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // true for auto flush
			conclient = new Client(socket,reader,writer);
			////////////////////
			//Read and Write Testing
//			int i;
//			String Input = null;
//			for(i=0;i<10;i++)
//			{				
//				writer.println("play:Raise Your Weapon (Ft. Greta Svabo Bech):120");
////				writer.println("Hello " + i + " !");
//				try{
//				Input = reader.readLine();
//				Log.d("Test","TESTING: "+Input);
//				}catch (IOException e) {
////					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
////				try {
////	//				Input = null;
////					Input = reader.readLine();
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////				Log.d("Test","READ: " + Input);
//			}
			/////////////////////
//			Toast.makeText(getApplicationContext(), "READ" + Input, Toast.LENGTH_SHORT).show();
			return null;
		}
		
	}
	
	public class HostReceiver extends BroadcastReceiver {

		public float x;
		public float y;
		public float z;
		public int state = 0;
		public int device = 0;
//		public enum state {
//			NOT_ENGAGED, SELECTING, NEXT, PREVIOUS, SELECTED, PAUSE, SWITCH
//		}
		
//		public Activity mActivity;
//		
//		public HostReceiver()
//		{
//			
//		}
//		
//		public HostReceiver(Activity activity) {
//			// TODO Auto-generated constructor stub
//			this.mActivity = activity;
//		}
//		
//		public HostReceiver(TextView text, FrameLayout frame, ImageView img)
//		{
//			this.dataText = text;
//			this.dataFrame = frame;
//			this.imgView = img;
//		}
		
		@SuppressLint("NewApi")
		@Override
	    public void onReceive(final Context context, final Intent intent) {
			try {
			if(intent.getAction()=="com.sonyericsson.extras.liveware.extension.sensorsample.startApp")
			{
				Intent i = new Intent();
				i.setClassName("com.sonyericcson.extras.liveware.extension.sensorsample", "MainActivity");
			    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				context.startActivity(i);
			}
			else
			{
//	        Log.d(SampleExtensionService.LOG_TAG + "Recv", "onHostReceive: " + intent.getAction() + " Data: " + intent.getFloatExtra("x",0) + " " + intent.getFloatExtra("y",0) + " " + intent.getFloatExtra("z", 0));
	        x = intent.getFloatExtra("x", 0);
	        y = intent.getFloatExtra("y", 0);
	        z = intent.getFloatExtra("z", 0);
	        
	        long eventtime = SystemClock.uptimeMillis(); 
	        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
	        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
	        KeyEvent downEvent = null;
	        KeyEvent upEvent = null;
	        
	        if(x<(-8))
	        {
	        	state=0;
        		Log.d("Test","State: " + state);
	        }
	        else if(x>8)
	        {
	        	if(state==2||state==4||state==6)
	        	{
		        	state=5;
	        		Log.d("Test","State: " + state);
	        	}
	        }
	        else if(z>8)
	        {
	        	if(state==0||state==5||state==1||state==3)
	        	{
	        		state=2;
	        		Log.d("Test","State: " + state);
	        	}
	        		
//	            downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0);

//	            upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
	        }
	        else if(y>8)
	        {
	        	if(state==1||state==2)
	        	{
	        		state = 3;
	        		Log.d("Test","State: " + state);
	        		device = 1 - device;
	        		Log.d("Test","Device: " + device);
	        	}
	        	else if(state==4||state==5)
	        	{
	        		state = 6;
	        		Log.d("Test","State: " + state);
	        		if(device==0)
	        		{
	        			try
	        			{
		        			String songTitle = MainActivity.currentSongTitle;
		        			int songPosition = MainActivity.mp.getCurrentPosition();
		        			conclient.writer.println("play:"+songTitle+":"+songPosition);
	        				mp.pause();
	        				device=1;
	        			}
	        			catch (Exception e)
	        			{
	        				e.printStackTrace();
	        			}
	        		}
	        		else if(device==1)
	        		{
	        			try
	        			{
	        				conclient.writer.println("info");
	        				String readSongInfo = conclient.reader.readLine();
	        				String[] readSongArgs = readSongInfo.split(":");
	        				try
	        				{
		        		    	String[] proj = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, Audio.Media.DATA};
	//	        		    	Uri playlistUri = Uri.parse("content://com.google.android.music.MusicContent/audio");
		        		    	Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		        		    	Cursor cursor = getContentResolver().query(musicUri, proj, null, null, null);
	        			
	        					int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
	        		        	int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
	        		        	int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
	        		        	int dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
	        		        	
	        		        	long thisSource;
	        		        	long thisId;
	        		        	String thisTitle;
	        		        	String thisArtist;
	        		        	if (cursor == null) {
	        		        	    // query failed, handle error.
	        		        	} else if (!cursor.moveToFirst()) {
	        		        	    // no media on the device
	        		        	} else {
		        		            do {
		        		               thisId = cursor.getLong(idColumn);
		        		               thisTitle = cursor.getString(titleColumn);
		        		               thisArtist = cursor.getString(artistColumn);
		        		               
		        		               // ...process entry...
		        		               Log.d("Music","Found: " + thisId + ":" + thisArtist + ":" + thisTitle/* + ":" + thisSource + ":" + thisLocal*/);
		        		            } while (cursor.moveToNext() && !thisTitle.equals(readSongArgs[1]));
		        		            Log.d("Music",readSongArgs[1] + "^" + readSongArgs[2]);
		        		            if(thisTitle.equals(readSongArgs[1]))
		        		            {
			        		            Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
			        		            
		//	        					String path = yourCursor.getString(getColumnIndex(Audio.Media.DATA));
			        		            MainActivity.mp.reset();
			        					MainActivity.mp.setDataSource(getApplicationContext(),contentUri);
			        					MainActivity.mp.prepare();
			        					MainActivity.mp.start();
			        					MainActivity.mp.seekTo(Integer.parseInt(readSongArgs[2]));
			        					MainActivity.currentSongTitle = thisTitle;
			        					conclient.writer.flush();
			        					conclient.writer.println("pause");
			        					device = 0;
		        		            }
	        		        	}
	        				}
	        				catch(Exception e)
	        				{
	        					{
	        						Toast test = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
	        						test.show();
	        						e.printStackTrace();
	        					}
	        				}
	        			}
	        			catch (Exception e)
	        			{
	        				e.printStackTrace();
	        			}
	        		}
	        	}
	        }
	        else if(y<(-8))
	        {
	        	if(state==2||state==3)
	        	{
	        		state = 1;
	        		Log.d("Test","State: " + state);
	        		device = 1 - device;
	        		Log.d("Test","Device: " + device);
	        	}
	        	else if(state==5||state==6)
	        	{
	        		state = 4;
	        		Log.d("Test","State: " + state);
	        		Log.d("Test","State: " + state);
	        		if(device==0)
	        		{
//	        			downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
//	        			upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
//	        			
//	        	        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
//	        	        context.sendBroadcast(downIntent, null); 
//	        	        
//	        	        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent); 
//	        	        context.sendBroadcast(upIntent, null); 
	        			try
	        			{
	        				if(MainActivity.mp==null);
	        				{
	        					mp = new MediaPlayer();
	        				}
	        				if(MainActivity.mp.isPlaying())
	        				{
	        					MainActivity.mp.pause();
	        				}
	        				else
	        				{
	        					MainActivity.mp.start();
	        				}
	        			}
	        			catch (Exception e)
	        			{
	        				e.printStackTrace();
	        			}
	        		}
	        		else if(device==1)
	        		{
	        			conclient.writer.println("pause");
	        		}
	        	}
	        }
//	        else if(z<(-8))
//	        {
////	            downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE, 0);
////
////	            upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE, 0);
//	        }
	        
//	        context.getApplicationContext().
//	        TextView dataText = (TextView)((this.mActivity).findViewById(R.id.sensorDataView));
//	        FrameLayout dataFrame = (FrameLayout)(this.mActivity).findViewById(R.id.sensorVDataView);
//	        ImageView imgView = (ImageView)(this.mActivity).findViewById(R.id.imageView1);
	        
	        text.setText(String.format("x=%.1f y=%.1f z=%.1f",x,y,z));
	        img.setY(300+x*50);
	        img.setX(300+y*50);
	        
//	        sendBroadcast(new Intent("android.media.action.MEDIA_PLAY_FROM_SEARCH"));
	        
//	        if(!searched)
//	        {
//	        	String[] proj = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, "SourceId", "hasLocal"};
//	        	Uri playlistUri = Uri.parse("content://com.google.android.music.MusicContent/audio");
//	        	Cursor cursor = getContentResolver().query(playlistUri, proj, null, null, null);
//	        ContentResolver contentResolver = getContentResolver();
//	        Uri uri = Uri.parse("android.media.action.MEDIA_PLAY_FROM_SEARCH");
//	        Intent intent2 = new Intent(Intent.ACTION_MAIN);
//	        intent2.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
//	        intent2.putExtra(SearchManager.QUERY,"yeah yeah");
//	        startActivity(intent2);
//	        Intent intent3 = new Intent(Intent.ACTION_MAIN);
//	        intent3.setAction(MediaStore.)
//	        Log.d(SampleExtensionService.LOG_TAG,"Searching for Song...");
	        //Cursor cursor = contentResolver.query(uri, null, null, null, null);
//	        if (cursor == null) {
	            // query failed, handle error.
//	        	Log.d(SampleExtensionService.LOG_TAG,"Query Failed");
//	        } else if (!cursor.moveToFirst()) {
	            // no media on the device
//	        	Log.d(SampleExtensionService.LOG_TAG,"Nothing Found");
//	        } else {
//	            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
//	            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
//	            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//	            int sourceIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
//	        	int idColumn = 0;
//	        	int titleColumn = 1;
//	        	int artistColumn = 2;
//	        	int sourceIdColumn = 3;
//	        	int hasLocalColumn = 4;
//	        	long thisSource;
//	        	long thisId;
//	            do {
//	               thisId = cursor.getLong(idColumn);
//	               String thisTitle = cursor.getString(titleColumn);
//	               String thisArtist = cursor.getString(artistColumn);
//	               thisSource = cursor.getLong(sourceIdColumn);
//	               int thisLocal = cursor.getInt(hasLocalColumn);
//	               // ...process entry...
//	               Log.d(SampleExtensionService.LOG_TAG,"Found: " + thisId + ":" + thisArtist + ":" + thisTitle + ":" + thisSource + ":" + thisLocal);
//	            } while (cursor.moveToNext());
//	            MediaPlayer mpObject = new MediaPlayer();
//	            try {
//	                if (thisSource > 0) {
//	                    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI, Long.valueOf(thisSource));
//	                    mpObject.setAudioStreamType(AudioManager.STREAM_MUSIC);
//	                    mpObject.setDataSource(context, contentUri);
//	                    mpObject.prepare();
//	                    mpObject.start();
//
//	                }
//	            } catch (Exception e) {
//	                e.printStackTrace();
//	            }
//	            Log.d(SampleExtensionService.LOG_TAG, "Done");
//	            Intent i3 = Intent..
//	        }
//	        searched = true;
//	        }
	        
//	        String[] proj2 = { "SourceId", MediaStore.Audio.Playlists.Members.TITLE, MediaStore.Audio.Playlists.Members._ID };
//	        String playListRef = "content://com.google.android.music.MusicContent/playlists/" + playListId + "/members";
//	        Uri songUri = Uri.parse(playListRef);
//	        Cursor songCursor = getContentResolver().query(songUri, proj2, null, null, null);
//
//	        long audioId = -1;
//	        while (songCursor.moveToNext()) {
//	            audioId = songCursor.getLong(songCursor.getColumnIndex("SourceId"));
//	            String title = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));
//	            Log.v("", "audioId: " + audioId + ", title: " + title);
//	        }
//	        songCursor.close();
	        
//	        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
//	        context.sendBroadcast(downIntent, null); 
//	        
//	        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent); 
//	        context.sendBroadcast(upIntent, null); 
			}
			}
			catch (Exception e)
			{
				Log.d(SampleExtensionService.LOG_TAG, e.toString());
			}
		}
	}
}
