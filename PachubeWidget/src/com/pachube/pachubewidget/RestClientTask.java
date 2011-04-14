package com.pachube.pachubewidget;

import android.os.Handler;
import android.os.Message;

import android.util.Log;

public class RestClientTask implements Runnable {
	private Handler mHandler;
	private String url;
	private String username;
	private String password;

	public RestClientTask (Handler handler, String url, String username, String password) {
		mHandler = handler;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public void run() {
		ParsedFeed result = RestClient.connect(this.url, this.username, this.password);
		Message msg = mHandler.obtainMessage(0, result);
		mHandler.sendMessage(msg);
	}
}
