package com.example.Customersloginauthenticationfromserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import model.IPAddressHolder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import com.example.Customerloginauthenticationfromserver.EditCustomerActivity;
import com.example.Customerloginauthenticationfromserver.LoginSuccessActivity;
import com.example.studentloginauthenticationfromserver.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CheckBalance extends Activity {
	//String accountbalance;
	ProgressDialog pd;
	TextView textViewCheckBalance;
	//TextView textViewName;
	String userid;
	TextToSpeech t1;
	String fullname;
	String jj;
	String i;
	String speech="Your account balance is Rupees";
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_balance);
		
		 t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
	         @Override
	         public void onInit(int status) {
	            if(status != TextToSpeech.ERROR) {
	               t1.setLanguage(Locale.UK);
	            }
	         }
	      });
		
		Intent ins=getIntent();
		
	    //accountbalance=ins.getStringExtra("accountbalance");
		//fullname=ins.getStringExtra("fullname");
		userid =  ins.getStringExtra("userid");
		//textViewName=(TextView)findViewById(R.id.textViewfullNamE);
		textViewCheckBalance=(TextView)findViewById(R.id.textViewCheckBalance);
		
		//textViewName.setText("Your name"+fullname);
		//textViewCheckBalance.setText(jj);
		textViewCheckBalance.setText(jj);
	     textViewCheckBalance.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String toSpeak = speech+jj;
	            Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
	            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
				}
		});	
		
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
	
		MyHttpAsycTaskGetDetails obj2 = new MyHttpAsycTaskGetDetails();
		obj2.execute("http://"+ IPAddressHolder.IPAddress +":38611/forAndroidClientServerStudentAuthentication/rest/Customer/myGetBalance/"+userid);
	
	}
	class MyHttpAsycTaskGetDetails extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(CheckBalance.this);
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
						//Converting JSON to Customer Object
				
						JSONObject json = new JSONObject(result);
						//Toast.makeText(, "Json is available", Toast.LENGTH_LONG);
						Toast.makeText(getApplicationContext(), json.toString(), Toast.LENGTH_LONG).show();
				jj=	json.getString("accountbalance");
					
						//jj=String.valueOf(json);
											
						textViewCheckBalance.setText("Your Current Balance is"+jj);
						Toast.makeText(getApplicationContext(), jj, Toast.LENGTH_LONG).show();
						
//						
		
						
									
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

	}	}
	
