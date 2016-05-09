package com.authorwjf.http_get;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Main extends Activity implements OnClickListener {

	// contacts JSONArray
	JSONArray contacts = null;

	// JSON Node names
	private static final String TAG_CONTACTS = "contacts";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_ADDRESS = "address";
	private static final String TAG_GENDER = "gender";
	private static final String TAG_PHONE = "phone";
	private static final String TAG_PHONE_MOBILE = "mobile";
	private static final String TAG_PHONE_HOME = "home";
	private static final String TAG_PHONE_OFFICE = "office";

	// Hashmap for ListView
	ArrayList<HashMap<String, String>> contactList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.my_button).setOnClickListener(this);
    }

	@Override
	public void onClick(View arg0) {
		Button b = (Button)findViewById(R.id.my_button);
		b.setClickable(false);
		new LongRunningGetIO().execute();
	}
	
	private class LongRunningGetIO extends AsyncTask <Void, Void, String> {
		
		protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
	       InputStream in = entity.getContent();
	         StringBuffer out = new StringBuffer();
	         int n = 1;
	         while (n>0) {
	             byte[] b = new byte[4096];
	             n =  in.read(b);
	             if (n>0) out.append(new String(b, 0, n));
	         }
	         return out.toString();
	    }
		
		@Override
		protected String doInBackground(Void... params) {
			 HttpClient httpClient = new DefaultHttpClient();
			 HttpContext localContext = new BasicHttpContext();
             //HttpGet httpGet = new HttpGet("http://www.cheesejedi.com/rest_services/get_big_cheese.php?puzzle=1");
			 //HttpGet httpGet = new HttpGet("http://localhost/testews.php");
			 //HttpGet httpGet = new HttpGet("http://192.168.96.9/testews.php");
			 HttpGet httpGet = new HttpGet("http://api.androidhive.info/contacts/");
			// [{"id":"59","level":"1","time_in_secs":"4","par":"0","initials":"TNF","quote":"Hey hey ole hickery", "time_stamp":"2012-03-03 04:36:15"}]
             String text = null;
             try {
                   HttpResponse response = httpClient.execute(httpGet, localContext);
                   HttpEntity entity = response.getEntity();
                   text = getASCIIContentFromEntity(entity);


				 if (text != null) {
					 try {
						 JSONObject jsonObj = new JSONObject(text);

						 // Getting JSON Array node
						 contacts = jsonObj.getJSONArray(TAG_CONTACTS);

						 // looping through All Contacts
						 for (int i = 0; i < contacts.length(); i++) {
							 JSONObject c = contacts.getJSONObject(i);

							 String id = c.getString(TAG_ID);
							 String name = c.getString(TAG_NAME);
							 String email = c.getString(TAG_EMAIL);
							 String address = c.getString(TAG_ADDRESS);
							 String gender = c.getString(TAG_GENDER);

							 // Phone node is JSON Object
							 JSONObject phone = c.getJSONObject(TAG_PHONE);
							 String mobile = phone.getString(TAG_PHONE_MOBILE);
							 String home = phone.getString(TAG_PHONE_HOME);
							 String office = phone.getString(TAG_PHONE_OFFICE);

							 // tmp hashmap for single contact
							 HashMap<String, String> contact = new HashMap<String, String>();

							 // adding each child node to HashMap key => value
							 contact.put(TAG_ID, id);
							 contact.put(TAG_NAME, name);
							 contact.put(TAG_EMAIL, email);
							 contact.put(TAG_PHONE_MOBILE, mobile);

							 // adding contact to contact list
							 contactList.add(contact);
						 }
					 } catch (JSONException e) {
						 e.printStackTrace();
					 }
				 } else {
					 Log.e("ServiceHandler", "Couldn't get any data from the url");
				 }

             } catch (Exception e) {
            	 return e.getLocalizedMessage();
             }
             return text;
		}	
		
		protected void onPostExecute(String results) {
			if (results!=null) {
				EditText et = (EditText)findViewById(R.id.my_edit);
				et.setText(results);
			}
			Button b = (Button)findViewById(R.id.my_button);
			b.setClickable(true);
		}
    }
}