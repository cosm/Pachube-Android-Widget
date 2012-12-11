// This code is forked from "Pachube for Android" by Daniele Altomare 'Fasteque' available at http://code.google.com/p/pachube-for-android/

package com.pachube.pachubewidget;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FeedDataHandler extends DefaultHandler
{
	// Tags.
	private boolean id = false;
	private boolean title = false;
	private boolean description = false;
	private boolean status = false;
	private boolean data = false;
	private boolean tag = false;
	private boolean value = false;
	private boolean unit = false;

	private ParsedFeed feed = null;
	private ParsedFeedData feedData = null;
	
	// Getter.
	public ParsedFeed getParsedFeed()
	{
		return this.feed;
	}

	@Override
	public void startDocument() throws SAXException
	{
		feed = new ParsedFeed();
	}
	
	
	@Override
	public void endDocument() throws SAXException
	{
		// Nothing to do.
	}

	// <tag>.
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if(localName.equals("title"))
			this.title = true;
		if(localName.equals("description"))
			this.description = true;
		if(localName.equals("status"))
			this.status = true;
		if(localName.equals("data"))
		{
			this.data = true;
			feedData = new ParsedFeedData();
			feedData.setId(attributes.getValue("id"));
		}
		if(localName.equals("tag"))
			this.tag = true;
		if(localName.equals("current_value"))
			this.value = true;
		if(localName.equals("unit"))
			this.unit = true;		
	}
	
	
	// </tag>.
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if(localName.equals("title"))
			this.title = false;
		if(localName.equals("description"))
			this.description = false;
		if(localName.equals("status"))
			this.status = false;
		if(localName.equals("data"))
		{
			this.data = false;
			feed.feedData.add(feedData);
		}
		if(localName.equals("tag"))
			this.tag = false;
		if(localName.equals("current_value"))
			this.value = false;
		if(localName.equals("unit"))
			this.unit = false;
	}

	// <tag>characters</tag>.
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if(this.title)
			feed.setFeedTitle(new String(ch, start, length));
		if(this.description)
			feed.setFeedDescription(new String(ch, start, length));
		if(this.status)
			feed.setFeedStatus(new String(ch, start, length));
		if(this.data && this.tag)
			feedData.setTag(new String(ch, start, length));
		if(this.value)
			feedData.setValue(new String(ch, start, length));
		if(this.unit)
			feedData.setUnitName(new String(ch, start, length));
	}
}
