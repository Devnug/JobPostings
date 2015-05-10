package com.nugdev.jobpostings;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class JobFragment extends Fragment implements OnClickListener{

	public static final String ARG_SECTION_NUMBER = "section_number";
	
	public final String DEBUG_TAG = "JobFragment";
	
	//public ArrayList<String> data;
	public static ListView lv1;
	static ArrayAdapter<Posting> adapter;
	EditText edt;
	Spinner typeSpinner;
	Spinner classSpinner;
	static View v;
	
	@SuppressWarnings("unchecked")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        /** Creating an array adapter to store the list of countries **/
		//data = new ArrayList<String>();
		//refreshList();
		
        //adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,data);
 
        /** Setting the list adapter for the ListFragment **/
        //setListAdapter(adapter);
        //updateList(inflater);
        v = inflater.inflate(R.layout.datalist, null); 
        //adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, MainActivity.data);
        //adapter = new CustomAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1,data);
        adapter = new PostingAdapter<Posting>(v.getContext(), android.R.layout.simple_list_item_1,MainActivity.data);
        lv1 = (ListView)v.findViewById(R.id.ListView01);    
        lv1.setAdapter(adapter);
        //container.addView(v);
        //setListAdapter(adapter);
        lv1.setOnItemClickListener(new OnItemClickListener() {
			  public void onItemClick(AdapterView<?> parent, View view,
			      int position, long id) {
				  	
				  	// Refresh specific item in Listview to either show or hide description of the job
				  	((PostingAdapter<Posting>) parent.getAdapter()).changeState(view, position);		
			  	}
			}); 
        
        lv1.setTextFilterEnabled(true);
        
        edt = (EditText) v.findViewById(R.id.SearchText);
        //edt.setText(" ");
        
        edt.addTextChangedListener(new TextWatcher()
	    {


	        @Override
	        public void onTextChanged( CharSequence arg0, int arg1, int arg2, int arg3)
	        {
	        	// TODO Auto-generated method stub
	        	if(MainActivity.data.size() > 0)
	        		JobFragment.this.adapter.getFilter().filter(arg0);

	        }



	        @Override
	        public void beforeTextChanged( CharSequence arg0, int arg1, int arg2, int arg3)
	        {
	            // TODO Auto-generated method stub

	        }



	        @Override
	        public void afterTextChanged( Editable arg0)
	        {
	            // TODO Auto-generated method stub
	        	//if(MainActivity.data.size() > 0)
	        	//	JobFragment.this.adapter.getFilter().filter(arg0);

	        }
	    });
	    
        return v;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
	
	public static void updateList(LayoutInflater inflater) {
		v = inflater.inflate(R.layout.datalist, null); 
        adapter = new PostingAdapter<Posting>(v.getContext(), R.layout.item_list, MainActivity.data);
        //adapter = new CustomAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1,data);
        lv1 = (ListView)v.findViewById(R.id.ListView01);    
        lv1.setAdapter(adapter);
        //container.addView(v);
        //setListAdapter(adapter);
        
        lv1.setTextFilterEnabled(true);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
