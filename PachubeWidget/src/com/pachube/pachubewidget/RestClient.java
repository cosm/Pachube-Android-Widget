package com.pachube.pachubewidget;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.util.Log;

public class RestClient
{
	
    public static ParsedFeed connect(String url, String username, String password)
    {
        HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
            public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
                AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                        ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                
                if (authState.getAuthScheme() == null) {
                    AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
                    Credentials creds = credsProvider.getCredentials(authScope);
                    if (creds != null) {
                        authState.setAuthScheme(new BasicScheme());
                        authState.setCredentials(creds);
                    }
                }
            }    
        };

    	ParsedFeed feed = null;
    	
        DefaultHttpClient httpclient = new DefaultHttpClient();

        httpclient.getCredentialsProvider().setCredentials(new AuthScope("www.pachube.com", 443), new UsernamePasswordCredentials(username,password));
        BasicHttpContext localcontext = new BasicHttpContext();
        BasicScheme basicAuth = new BasicScheme();
        localcontext.setAttribute("preemptive-auth", basicAuth);
        
        httpclient.addRequestInterceptor(preemptiveAuth, 0);
        
        HttpHost targetHost = new HttpHost("www.pachube.com", 443, "https"); 
        
        
        // Prepare a request object
        HttpGet httpget = new HttpGet(url);
 
        // Execute the request
        HttpResponse response;
        
        try
        {
            response = httpclient.execute(targetHost, httpget, localcontext);
            int responseCode = response.getStatusLine().getStatusCode();
            // Examine the response status
            //Log.i("PW", response.getStatusLine().toString());
            
            HttpEntity entity = response.getEntity();
            
            if ((entity != null) && (responseCode == 200))
            {
            	
                 InputStream instream = entity.getContent();
                
                //String result = convertStreamToString(instream);
                //Log.i("PW", result);
                
	            SAXParserFactory spf = SAXParserFactory.newInstance();
	            SAXParser sp;
			
	            /* Get the XMLReader of the SAXParser we created. */
	            try
	            {
	            	sp = spf.newSAXParser();
					XMLReader xr = sp.getXMLReader();
					
					// Create a new ContentHandler and apply it to the XMLReader.
                    FeedDataHandler feedHandler = new FeedDataHandler();
                    xr.setContentHandler(feedHandler);
					
					InputSource is = new InputSource(instream);
					xr.parse(is);
					
					feed = feedHandler.getParsedFeed();
				}
	            catch (SAXException e)
	            {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ParserConfigurationException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
		return feed;
    }

/*
    private static String convertStreamToString(InputStream is) {
        
         // To convert the InputStream to String we use the BufferedReader.readLine() method.
         
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
*/
}
