package com.nugdev.jobpostings;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class MainActivity extends Activity implements OnNavigationListener {

	public final String DEBUG_TAG = "JobPostings";
	public int regionCode = 6;
	public JobFragment jobFragment = new JobFragment();
	public static ArrayList<Posting> data = new ArrayList<Posting>();
	public Posting[] data2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ActionBar actionBar = getActionBar();
		if(actionBar == null)
			Log.d(DEBUG_TAG, "Da Fuck?");
		actionBar.setTitle("");
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
		        R.array.region_array, android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
		
		scanPage();
	}

	public boolean onNavigationItemSelected(int position, long id) {
		//String[] strings = getResources().getStringArray(R.array.region_array);
		regionCode = position + 1;
		Log.d(DEBUG_TAG, "Position: " + position);
		scanPage();
		//JobFragment.updateList(getLayoutInflater());
		//JobFragment fragment = new JobFragment();
		//Bundle args = new Bundle();
		//this.getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
		return true;
	  // Our logic
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void instantiateFragments(Bundle inState) {
		   FragmentManager manager = getFragmentManager();
		   FragmentTransaction transaction = manager.beginTransaction();
			      jobFragment = (JobFragment) manager.getFragment(inState, jobFragment.DEBUG_TAG);
			  
	}
				
	public void updateList() {
		JobFragment fragment = new JobFragment();
		//Bundle args = new Bundle();
		this.getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
	}
	
	// When user clicks button, calls AsyncTask.
    // Before attempting to fetch the URL, makes sure that there is a network connection.
    @SuppressWarnings("unchecked")
	public void myClickHandler(View view) {
    	Log.d(DEBUG_TAG, "click");
        // Gets the URL from the UI's text field.
        //String stringUrl = urlText.getText().toString();
    	scanPage();
    	/*
        ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.d(DEBUG_TAG, networkInfo.toString());
        //Log.d(DEBUG_TAG, stringUrl);
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageText().execute("");
            Log.d(DEBUG_TAG, "true");
        } else {
            textView.setText("No network connection available.");
        }
        */
    }
    
    public void parseXML(String result) throws XmlPullParserException, IOException {
    	data.clear();
    	XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        boolean titleTag = false;
        boolean itemTag = false;
        boolean descTag = false;
        xpp.setInput(new StringReader(result.substring(3)));
        int eventType = xpp.getEventType();
        Posting post = new Posting();
        String temp = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
         if(eventType == XmlPullParser.START_DOCUMENT) {
             System.out.println("Start document");
         } else if(eventType == XmlPullParser.START_TAG) {
        	 if(xpp.getName().equals("item"))
        		 itemTag = true;
        	 if(xpp.getName().equals("title"))
        		 titleTag = true;
        	 if(xpp.getName().equals("description"))
        		 descTag = true;
             System.out.println("Start tag "+xpp.getName());
         } else if(eventType == XmlPullParser.END_TAG) {
        	 if(xpp.getName().equals("title"))
        		 titleTag = false;
        	 if(xpp.getName().equals("description"))
        		 descTag = false;
        	 if(xpp.getName().equals("item")) {
        		 data.add(post);
        		 post = new Posting();
        	 }
             System.out.println("End tag "+xpp.getName());
         } else if(eventType == XmlPullParser.TEXT) {
        	 if(titleTag && itemTag) {
        		 temp = xpp.getText();
        		 post.setSchool(temp.substring(0, temp.indexOf("-")));
        		 post.setQuickDesc(temp.substring(temp.indexOf("-") + 1));
        		 //post = new Posting(temp.substring(0, temp.indexOf("-")),temp.substring(temp.indexOf("-") + 1),"","");
        		 //data.add(post);
        		 //data.add(xpp.getText());
        	 }
        	 if(descTag && itemTag) {
        		 post.setDesc(xpp.getText());
        	 }
             System.out.println("Text "+xpp.getText());
         }
         eventType = xpp.next();
        }
        System.out.println("End document");
    }
    
    public void scanPage() {
    	ConnectivityManager connMgr = (ConnectivityManager) 
	            getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        Log.d(DEBUG_TAG, networkInfo.toString());
	        //Log.d(DEBUG_TAG, stringUrl);
	        if (networkInfo != null && networkInfo.isConnected()) {
	            new DownloadWebpageText().execute("");
	            Log.d(DEBUG_TAG, "true");
	        } else {
	            //textView.setText("No network connection available.");
	        }
    }
    
 // Uses AsyncTask to create a task away from the main UI thread. This task takes a 
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageText extends AsyncTask<String, String, String> {
       protected String doInBackground(String... urls) {
           
       	Log.d(DEBUG_TAG, "dWT");
           // params comes from the execute() call: params[0] is the url.
           try {
           		downloadUrl(urls[0]);
           	//return db.getName();
           	return "Updating Database";
           } catch (IOException e) {
           	return "Cannot open Webpage";
           } catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           return "What?";
       }
       
       private ProgressDialog pdia;
       
       // onPreExecute() - displays during AsyncTask
       protected void onPreExecute()
       {
       	super.onPreExecute();
       	pdia = new ProgressDialog(MainActivity.this);
       	pdia.setMessage("Loading...");
       	pdia.setCanceledOnTouchOutside(false);
       	pdia.setCancelable(false);
       	pdia.show();
       	
       }
       
       // onPostExecute displays the results of the AsyncTask.
       @SuppressWarnings("unchecked")
		protected void onPostExecute(String result) {
       	pdia.dismiss();
       	updateList();
           //textView.setText(result);
           //new updateDatabase().execute(itemArray);
      }
       
    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private void downloadUrl(String myurl) throws IOException, XmlPullParserException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;
            
        try {
       	 /*
       	//Setup the parameters
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("Username", "pUser"));
			nameValuePairs.add(new BasicNameValuePair("Password", "pricelistapp"));
			//Add more parameters as necessary

			//Create the HTTP request
			HttpParams httpParameters = new BasicHttpParams();

			//Setup timeouts
			HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
			HttpConnectionParams.setSoTimeout(httpParameters, 15000);			

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpPost httppost = new HttpPost(priceListURL);
			//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));        
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
           */
       	 
			//URL url = new URL(priceListURL);
            //Log.d(DEBUG_TAG,"TRYING");
            //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setReadTimeout(10000 /* milliseconds */);
            //conn.setConnectTimeout(15000 /* milliseconds */);
            //conn.setRequestMethod("POST");
            //String credentials = "pUser" + ":" + "pricelistapp";
            //String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            //conn.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
            //conn.setDoInput(true); 
            // Starts the query
            //conn.connect();
            
       	 ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			 nameValuePairs.add(new BasicNameValuePair("txtKeyword", "&math"));
			 nameValuePairs.add(new BasicNameValuePair("ddlRegion", "&06"));
			 //Add more parameters as necessary

			 //Create the HTTP request
			 HttpParams httpParameters = new BasicHttpParams();

			 // a blank returns all values
			 String text = "";
			 String region = "0" + regionCode;
			 
			 //Setup timeouts
			 HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
			 HttpConnectionParams.setSoTimeout(httpParameters, 15000);			

			// String credentials = "pUser" + ":" + "pricelistapp";
	         //String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
	         //nameValuePairs.add(new BasicNameValuePair("Authorization", "Basic " + base64EncodedCredentials));
			 HttpClient httpclient = new DefaultHttpClient(httpParameters);
			 HttpPost httppost = new HttpPost("https://www.pnwboces.org/teacherapplication/rss.aspx?k=" + text + "&r=" + region);
			 httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			 httppost.setHeader("txtKeyword", "math");
			 HttpResponse response = httpclient.execute(httppost);
			 HttpEntity entity = response.getEntity();
			 
			 //Scanner in = new Scanner(EntityUtils.toString(entity));
			 String result = EntityUtils.toString(entity);
			 System.out.println(result);
			 parseXML(result);
			 // Create a JSON object from the request response
			 //JSONObject jsonObject = new JSONObject(result);
			 /*
			 JSONArray jArray = new JSONArray(result);
			 for(int i = 0; i < jArray.length(); i++) {
				JSONObject obj = jArray.getJSONObject(i);
				//System.out.println("What is the tool? " + obj.getString("itemClass"));
				itemArray.add(new Item(obj.getString("name"),obj.getString("defindex"), obj.getString("description"), obj.getString("uniquePrice"),obj.getString("uncraft"),obj.getString("vintage"),
						obj.getString("genuine"),obj.getString("strange"),obj.getString("haunted"),obj.getString("collector"),obj.getString("uniquePriceMon"),obj.getString("uncraftMon"),obj.getString("vintageMon"),
						obj.getString("genuineMon"),obj.getString("strangeMon"),obj.getString("hauntedMon"),obj.getString("collectorMon"),obj.getString("picUrl"),obj.getString("classEquip"),obj.getString("lastUpdate"),
						obj.getString("itemClass"), obj.getString("hasUnusual")));
		 	 }
            */
            
            //int response = entity..getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            //is = conn.getInputStream();

            // Convert the InputStream into a string
            //readIt(is, len);
            
        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        } finally {
            if (is != null) {
                is.close();
            } 
        }
    }
    }
}

