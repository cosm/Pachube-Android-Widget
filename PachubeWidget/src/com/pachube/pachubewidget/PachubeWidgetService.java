// This code is forked from "Pachube for Android" by Daniele Altomare 'Fasteque' available at http://code.google.com/p/pachube-for-android/

package com.pachube.pachubewidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.widget.RemoteViews;

public class PachubeWidgetService extends Service
{
	public static final String UPDATE = "update";

	private NetworkInfo nwkInfo = null;
	
	@Override
	public void onStart(Intent intent, int startId)
	{
		String command = intent.getAction();
		
		int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		
		RemoteViews remoteView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.pachubewidget_layout);
		
		
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
		
		//SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
		
		
		
		if(command.equals(UPDATE))
		{
			if(isOnline())
			{
				ParsedFeed feed = RestClient.connect("https://api.pachube.com/v1/feeds/" +
													PachubeWidgetConfig.loadFeedIDKeyPref(getApplicationContext(), appWidgetId) + 
													".xml", 
													PachubeWidgetConfig.loadUsernamePref(getApplicationContext(), appWidgetId),PachubeWidgetConfig.loadPasswordPref(getApplicationContext(), appWidgetId));
				
				if(feed != null)
				{
					remoteView.setTextViewText(R.id.feed_title, feed.getFeedTitle());
					
					remoteView.setTextViewText(R.id.feed_description, feed.getFeedDescription());
					
					if(feed.getFeedStatus().equals("frozen"))
						remoteView.setTextColor(R.id.feed_status, 0xFF00a4cb);
					if(feed.getFeedStatus().equals("live"))
						remoteView.setTextColor(R.id.feed_status, 0xFF00cb03);
					remoteView.setTextViewText(R.id.feed_status, feed.getFeedStatus());
					
					// TODO : loop all over the data
					if(feed.feedData != null)
					{
						int thisDatastream = PachubeWidgetConfig.loadDsIDKeyPref(getApplicationContext(), appWidgetId);

						int thisDatastreamIndex;
						for (thisDatastreamIndex = 0; thisDatastreamIndex < feed.feedData.size(); thisDatastreamIndex++)
							if (feed.feedData.get(thisDatastreamIndex).getId() == thisDatastream)
								break;

						if (thisDatastreamIndex < feed.feedData.size()) {
							remoteView.setTextViewText(R.id.feed_data_tag, feed.feedData.get(thisDatastreamIndex).getTag());

							if(feed.feedData.get(thisDatastreamIndex).getValue().equals(""))
								remoteView.setTextViewText(R.id.feed_data_value, "-");
							else
								remoteView.setTextViewText(R.id.feed_data_value, feed.feedData.get(thisDatastreamIndex).getValue());

							remoteView.setTextViewText(R.id.feed_data_unit, feed.feedData.get(thisDatastreamIndex).getUnitName());
						} else {
							remoteView.setTextViewText(R.id.feed_title, "ID = " + Integer.toString(thisDatastream));

							remoteView.setTextViewText(R.id.feed_status, getString(R.string.no_datastream_id));
						}
					}
				}
			}
			else
			{
				remoteView.setTextViewText(R.id.feed_title, "Problem loading Pachube feed " + String.valueOf(PachubeWidgetConfig.loadFeedIDKeyPref(getApplicationContext(), appWidgetId)));
				
				remoteView.setTextViewText(R.id.feed_status, getString(R.string.no_connection));
			}
			
			// apply changes to widget
			appWidgetManager.updateAppWidget(appWidgetId, remoteView);
		} 
		
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
	
	private boolean isOnline()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		nwkInfo = cm.getActiveNetworkInfo();
		 
		if((nwkInfo == null) || !nwkInfo.isConnected())
			return false;
		 
		// check if roaming: to disable internet while roaming, just return false.
		if(nwkInfo.isRoaming())
			return true;
		
		return true; 
	}

}
