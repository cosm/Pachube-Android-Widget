// This code is forked from "Pachube for Android" by Daniele Altomare 'Fasteque' available at http://code.google.com/p/pachube-for-android/

package com.pachube.pachubewidget;

import java.util.ArrayList;

public class ParsedFeed
{
	private int feedID = -1;
	private String feedTitle = "";
	private String feedDescription = "";
	private String feedStatus = "";
	ArrayList<ParsedFeedData> feedData = new ArrayList<ParsedFeedData>();
	
	
	public int getFeedID()
	{
		return feedID;
	}


	public void setFeedID(int feedID)
	{
		this.feedID = feedID;
	}


	public String getFeedTitle()
	{
		return feedTitle;
	}
	
	
	public void setFeedTitle(String feedTitle)
	{
		this.feedTitle = shorten(feedTitle, 40);
	}
	
	
	public String getFeedDescription()
	{
		return feedDescription;
	}
	
	
	public void setFeedDescription(String feedDescription)
	{
		this.feedDescription = feedDescription;
	}


	public String getFeedStatus() {
		return feedStatus;
	}


	public void setFeedStatus(String feedStatus) {
		this.feedStatus = feedStatus;
	}
	
	private String shorten(String t, int l){
		if (t.length() > l){
			t = t.substring(0, l)+"...";
		}
		return t;
	}
}
