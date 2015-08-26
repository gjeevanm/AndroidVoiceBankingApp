package com.example.Customersloginauthenticationfromserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.studentloginauthenticationfromserver.R;

import model.IPAddressHolder;
import model.Transactions;
import model.TransactionsToJSON;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Transfers extends Activity {
	String userid;
	ProgressDialog pd;
	String jj;
	String ss;
	Button transfer;
	//String accountbalance;
	float  Updateamount;
	public EditText useridOfAccount2;
	public EditText amountOFAccount2;
	public EditText yourCurrentBalance;

	int convertionSuccessfull = 0;
	String jsonFormOfTransactions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfers);
		useridOfAccount2=(EditText)findViewById(R.id.editTextadduserid2);
		amountOFAccount2=(EditText)findViewById(R.id.editTextaddAmount);
		yourCurrentBalance=(EditText)findViewById(R.id.editTextCurrentBalance);
		transfer=(Button)findViewById(R.id.buttonTransfer);
		//yourCurrentBalance.setText(jj);
		//yourCurrentBalance.setEnabled(false);
		Intent tran=getIntent();
		userid=tran.getStringExtra("userid");
		
		MyHttpAsycTaskGetDetails1 obj2 = new MyHttpAsycTaskGetDetails1();
		obj2.execute("http://"+ IPAddressHolder.IPAddress +":12113/forAndroidClientServerStudentAuthentication/rest/Customer/myGetBalance/"+userid);

		
		
	transfer.setOnClickListener(new View.OnClickListener()
	{
		
		@Override
		public void onClick(View v) 
		{
			// TODO Auto-generated method stub
			String s1=amountOFAccount2.getText().toString();
			String s2=yourCurrentBalance.getText().toString();
			//Toast.makeText(getApplicationContext(), "Value received from edit text", Toast.LENGTH_LONG).show();
		  String userid2=useridOfAccount2.getText().toString();
		  MyHttpAsycTaskGetDetails2 obj2 = new MyHttpAsycTaskGetDetails2();
			obj2.execute("http://"+ IPAddressHolder.IPAddress +":12113/forAndroidClientServerStudentAuthentication/rest/Customer/myGetBalance/"+userid2);

			Transactions trans=new Transactions(userid, s1, userid2);
		  try
			 {
				jsonFormOfTransactions = TransactionsToJSON.convert1(trans);
				convertionSuccessfull = 1;
			 }
			 catch (JSONException e)
			 {
				Toast.makeText(getApplicationContext(), "JSON Convertion Problem: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			 }
		  if(convertionSuccessfull == 1)
			 {
				 //Toast.makeText(this, jsonFormOfCustomer, Toast.LENGTH_LONG).show();
				 
			  MyHttpAsyncTransactions obj9 = new   MyHttpAsyncTransactions();
				 obj9.execute("http://"+ IPAddressHolder.IPAddress +":12113/forAndroidClientServerStudentAuthentication/rest/Customer/registerTransaction/"+userid);
				 
			 }
		
      		float moneytobetransfered=Float.parseFloat(s1);
      		float balanceofuser1=Float.parseFloat(s2);
      		//Toast.makeText(getApplicationContext(), "Balance is parsed", Toast.LENGTH_LONG).show();
      		if(balanceofuser1>moneytobetransfered)
      		{
      			float deductedammount=balanceofuser1-moneytobetransfered;
      			String accountbalance = Float.toString(deductedammount);
          		//Toast.makeText(getApplicationContext(), "Balance is parsed", Toast.LENGTH_LONG).show();

      			MyHttpAsyncTaskTransfer obj3 = new MyHttpAsyncTaskTransfer();
      			obj3.execute("http://"+ IPAddressHolder.IPAddress +":"+"/forAndroidClientServerStudentAuthentication/rest/Customer/updateMyTransfer/" + userid + "/" + accountbalance );
                 accountbalance=s1+ss;
                 obj3.execute("http://"+ IPAddressHolder.IPAddress +":"+"/forAndroidClientServerStudentAuthentication/rest/Customer/updateMyTransfer/" + userid2 + "/" + accountbalance );
                  Toast.makeText(getApplicationContext(), v.getId()+"Your funds has been transfered", Toast.LENGTH_LONG).show();
      		
      		}
      		else
      		{
      			Toast.makeText(getApplicationContext(), "Insufficent Funds in your account" , Toast.LENGTH_LONG).show();
      		}
      		}
		});
	}


	class MyHttpAsycTaskGetDetails1 extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(Transfers.this);
            pd.setMessage("Retrieving Details from server, plz wait...");
            pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			
			String result = myPostDataToServer(params[0]);
			
			return result;
		}
		
		private String myPostDataToServer(String url) {
			
			String result = "";
			
			try
			{				
			   HttpClient httpClient =  new DefaultHttpClient();
			   
			   //to avoid timeout 
			   BasicHttpParams params = new BasicHttpParams();
			   HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
			   
		       HttpGet httpGet = new HttpGet(url);
		       HttpContext localContext =  new BasicHttpContext();
		       
		       HttpResponse response =  httpClient.execute(httpGet, localContext);
		       HttpEntity entity =  response.getEntity();
		       InputStream ins =  entity.getContent();
		       
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
					result = "Details could not be retrieved";
			
			}
			catch(Exception exp)
			{
				result =  "Problem in connecting to the Server" + exp.getMessage();
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(final String result) {
				super.onPostExecute(result);
				
				if(pd.isShowing())
					pd.dismiss();
				
									
					try
					{
						//Converting JSON to CustomerObject
						JSONObject json = new JSONObject(result);
					//	Toast.makeText(getApplicationContext(), json.toString(), Toast.LENGTH_LONG).show();
						jj=	json.getString("accountbalance");
						yourCurrentBalance.setText(jj);						
						}
					catch (final Exception e)
					{
						runOnUiThread( new Runnable() {
							@Override
							public void run() {						
								Toast.makeText(getApplicationContext(), "Runtime Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
							}
						});
					}
		}
	}
	class MyHttpAsycTaskGetDetails2 extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(Transfers.this);
            pd.setMessage("Retrieving Details from server, plz wait...");
            pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			
			String result = myPostDataToServer(params[0]);
			
			return result;
		}
		
		private String myPostDataToServer(String url) {
			
			String result = "";
			
			try
			{				
			   HttpClient httpClient =  new DefaultHttpClient();
			   
			   //to avoid timeout 
			   BasicHttpParams params = new BasicHttpParams();
			   HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
			   
		       HttpGet httpGet = new HttpGet(url);
		       HttpContext localContext =  new BasicHttpContext();
		       
		       HttpResponse response =  httpClient.execute(httpGet, localContext);
		       HttpEntity entity =  response.getEntity();
		       InputStream ins =  entity.getContent();
		       
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
					result = "Details could not be retrieved";
			
			}
			catch(Exception exp)
			{
				result =  "Problem in connecting to the Server" + exp.getMessage();
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(final String result) {
				super.onPostExecute(result);
				
				if(pd.isShowing())
					pd.dismiss();
				
									
					try
					{
						//Converting JSON to CustomerObject
						JSONObject json = new JSONObject(result);
					//	Toast.makeText(getApplicationContext(), json.toString(), Toast.LENGTH_LONG).show();
						ss=	json.getString("accountbalance");
										
						}
					catch (final Exception e)
					{
						runOnUiThread( new Runnable() {
							@Override
							public void run() {						
								Toast.makeText(getApplicationContext(), "Runtime Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
							}
						});
					}
		}
	}

	class MyHttpAsyncTransactions extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(Transfers.this);
            pd.setMessage("Sending to server, plz wait...");
            pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			
			String result = myPostDataToServer(params[0], jsonFormOfTransactions);
			
			return result;
		}
		
		private String myPostDataToServer(String url, String jsonFormOfTransactions) {
			
			String result = "";
			
			try
			{
				//Create HttpClient
				HttpClient httpClient = new DefaultHttpClient();
				
				//make post request to the url
				HttpPost httpPost = new HttpPost(url);
				
				//set json to StringEntity
				StringEntity se = new StringEntity(jsonFormOfTransactions);
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
		}
	
	}

		class MyHttpAsyncTaskTransfer extends AsyncTask<String, Void, String>
		{	
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				
				pd = new ProgressDialog(Transfers.this);
	            pd.setMessage("Sending details to server, plz wait...");
	            pd.show();
			}

			@Override
			protected String doInBackground(String... params) {
				
				String result = myPostDataToServer(params[0]);
				
				return result;
			}
			
			private String myPostDataToServer(String url) {
				
				String result = "";
				
				try
				{				
				   HttpClient httpClient =  new DefaultHttpClient();
			       HttpGet httpGet = new HttpGet(url);
			       HttpContext localContext =  new BasicHttpContext();
			       
			       HttpResponse response =  httpClient.execute(httpGet, localContext);
			       HttpEntity entity =  response.getEntity();
			       InputStream ins =  entity.getContent();
					
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
					result =  "Problem in sending details to the Server" + exp.getMessage();
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
					
					if(! result.contains("Database"))
						finish();
			}
		}
}