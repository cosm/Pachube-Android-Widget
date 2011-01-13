// This code is forked from "Pachube for Android" by Daniele Altomare 'Fasteque' available at http://code.google.com/p/pachube-for-android/


package com.pachube.pachubewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;

public class PachubeWidget extends AppWidgetProvider
{	
	//update rate in milliseconds
	public static final int UPDATE_RATE = 60000;


	@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{
	    for (int appWidgetId : appWidgetIds)
	    {      
	        setAlarm(context, appWidgetId, -1);
	    }
	    
		super.onDeleted(context, appWidgetIds);
	}


	@Override
	public void onDisabled(Context context)
	{
		// stop the update service 
		context.stopService(new Intent(context, PachubeWidgetService.class));
		
		super.onDisabled(context);
	}


	@Override
	public void onEnabled(Context context)
	{
		super.onEnabled(context);
	}


	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
	        for (int appWidgetId : appWidgetIds)
	        {
	        	if(PachubeWidgetConfig.loadConfigKeyPref(context, appWidgetId))
	        	{
	        		setAlarm(context, appWidgetId, PachubeWidgetConfig.loadUpdateRateKeyPref(context, appWidgetId));
	        	}
	        }
	}


    @Override
	public void onReceive(Context context, Intent intent)
    {
        // v1.5 fix that doesn't call onDelete Action
        final String action = intent.getAction();
        
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action))
        {
            final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
            {
                this.onDeleted(context, new int[] { appWidgetId });
            }
        }
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action))
        {
            final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
            {
                this.onUpdate(context, AppWidgetManager.getInstance(context), new int[] { appWidgetId });
            }
        }
        else
        {
            super.onReceive(context, intent);
        }
	}

    
	public static PendingIntent makeControlPendingIntent(Context context, String command, int appWidgetId)
    {
        Intent active = new Intent(context, PachubeWidgetService.class);
        active.setAction(command);
        active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        
        //this Uri data is to make the PendingIntent unique, so it wont be updated by FLAG_UPDATE_CURRENT
        //so if there are multiple widget instances they wont override each other
        Uri data = Uri.withAppendedPath(Uri.parse("pachubewidget://widget/id/#" + command + appWidgetId), String.valueOf(appWidgetId));
        active.setData(data);
        
        return(PendingIntent.getService(context, 0, active, PendingIntent.FLAG_UPDATE_CURRENT));
    }


    public static void setAlarm(Context context, int appWidgetId, int updateRate)
    {
        PendingIntent newPending = makeControlPendingIntent(context, PachubeWidgetService.UPDATE, appWidgetId);
        
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        if (updateRate >= 0)
        {
            alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), updateRate, newPending);
        }
        else
        {
            // on a negative updateRate stop the refreshing
            alarms.cancel(newPending);
        }
    }
}