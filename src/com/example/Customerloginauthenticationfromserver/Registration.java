package com.example.Customerloginauthenticationfromserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import model.Customer;
import model.CustomerToJSON;
import model.IPAddressHolder;

import com.example.studentloginauthenticationfromserver.R;
import com.example.studentloginauthenticationfromserver.R.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Registration extends Activity implements OnClickListener{
	
	Button registerButton;
	String jsonFormOfCustomer;
	int convertionSuccessfull = 0;
	
	Customer cus;
	
    ProgressDialog pd;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		registerButton = (Button) findViewById(R.id.buttonRegister);
		registerButton.setOnClickListener(this);

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		 Intent in =  getIntent();
 		 
		 String userid = in.getStringExtra("userid");
		 String emailid = in.getStringExtra("emailid");
		 String fullname = in.getStringExtra("fullname");
		 String pwd = in.getStringExtra("pwd");
		 String phone = in.getStringExtra("phone");
		 String accountbalance=in.getStringExtra("accountbalance");
		 
		 
		 cus =new Customer(userid, emailid, fullname, pwd, phone,accountbalance);
		 
		 try
		 {
			jsonFormOfCustomer = CustomerToJSON.convert(cus);
			convertionSuccessfull = 1;
		 }
		 catch (JSONException e)
		 {
			Toast.makeText(this, "JSON Convertion Problem: " + e.getMessage(), Toast.LENGTH_LONG).show();
			return;
		 }
		//Now we are ready to send the JSON Data (which contains Student details) to the Server
		 if(convertionSuccessfull == 1)
		 {
			 //Toast.makeText(this, jsonFormOfStudent, Toast.LENGTH_LONG).show();
			 
			 MyHttpAsycTask obj = new MyHttpAsycTask();
			 obj.execute("http://"+ IPAddressHolder.IPAddress +":38611/forAndroidClientServerStudentAuthentication/rest/Customer/register");
			 
		 }
		
	}
	
	class MyHttpAsycTask extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(Registration.this);
            pd.setMessage("Sending to server, plz wait...");
            pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			
			String result = myPostDataToServer(params[0], jsonFormOfCustomer);
			
			return result;
		}
		
		private String myPostDataToServer(String url, String jsonFormOfCustomer) {
			
			String result = "";
			
			try
			{
				//Create HttpClient
				HttpClient httpClient = new DefaultHttpClient();
				
				//make post request to the url
				HttpPost httpPost = new HttpPost(url);
				
				//set json to StringEntity
				StringEntity se = new StringEntity(jsonFormOfCustomer);
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json") );
				
				//set httpPost entity
				httpPost.setEntity(se);
				
				//set header to inform server about the type of content
				httpPost.setHeader("Content-type", "application/json");
				
				//execute post request to the given url
				HttpResponse httpResponse =  httpClient.execute(httpPost);
				
				//receive response as inputstream
				InputStream ins =  httpResponse.getEntity().getContent();
				
				//convert input to string type
				if(ins != null)
				{
					BufferedReader br =new BufferedReader(new InputStreamReader(ins));
					
					String line = "";
					while( (line =  br.readLine()) != null)
						result += line;
					
					ins.close();
				}
				else
					result = "Problem in receiving the acknowledgement message";
			
			}
			catch(Exception exp)
			{
				result =  "Problem in sending customer details to the Server" + exp.getMessage();
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(final String result) {
				super.onPostExecute(result);
				
				if(pd.isShowing())
					pd.dismiss();
				
				runOnUiThread( new Runnable() {
					@Override
					public void run() {						
						Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
					}
				});
				
				//When the registration is successful, go back to the login screen
				if(result.contains("successfully"))
				{
					Intent in = new Intent(Registration.this, MainActivity.class);
					startActivity(in);
				}
				
		}
		
	}

}

		
		 
		
		
		
