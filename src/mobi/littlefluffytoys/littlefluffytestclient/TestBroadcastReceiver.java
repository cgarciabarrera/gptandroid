package mobi.littlefluffytoys.littlefluffytestclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TestBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LocationBroadcastReceiver", "onReceive: received location update");
        
        final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
        
        // The broadcast has woken up your app, and so you could do anything now - 
        // perhaps send the location to a server, or refresh an on-screen widget.
        // We're gonna create a notification.
        
        // Construct the notification.
        
        TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
        Funciones.IMEI = mngr.getDeviceId().toString();
        
        Notification notification = new Notification(R.drawable.notification, "Locaton updated " + locationInfo.getTimestampAgeInSeconds() + " seconds ago", System.currentTimeMillis());

        Intent contentIntent = new Intent(context, TestActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        notification.setLatestEventInfo(context, "Location update broadcast received", "Timestamped " + LocationInfo.formatTimeAndDay(locationInfo.lastLocationUpdateTimestamp, true), contentPendingIntent);
        
        // Trigger the notification.
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1234, notification);
        new SaveImageTask(Float.toString(locationInfo.lastLat),Float.toString(locationInfo.lastLong), Integer.toString(locationInfo.lastAccuracy), (String)locationInfo.lastProvider, Long.toString(locationInfo.lastLocationUpdateTimestamp)).execute();
        Log.d("CGB", "CGB2");
    }
    
    
	public class SaveImageTask extends AsyncTask<String, String, String>
	{

		String Lat;
		String Lon;
		String Acc;
		String Orig;
		String Hora;
	    public SaveImageTask(String lat, String lon, String acc, String orig, String hora) {
	        this.Lat =lat;
	        this.Lon = lon;
	        this.Acc = acc;
	        this.Orig =orig;
	        this.Hora =hora;
	        		
	        
	    }

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://cgarciabarrera.no-ip.org:3000/gps_points/manual");

		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("latitude", Lat));
		        nameValuePairs.add(new BasicNameValuePair("longitude", Lon));
		        nameValuePairs.add(new BasicNameValuePair("imei", Funciones.IMEI));
		        nameValuePairs.add(new BasicNameValuePair("accuracy", Acc));
		        nameValuePairs.add(new BasicNameValuePair("provider", Orig));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        httpclient.execute(httppost);

		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    	e.printStackTrace();
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    	e.printStackTrace();
		    }
			return null;
		}



	}
}