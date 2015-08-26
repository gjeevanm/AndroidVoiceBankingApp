package com.example.Customerloginauthenticationfromserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;


import model.IPAddressHolder;
import model.MyCheckInternetConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.studentloginauthenticationfromserver.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class MainActivity extends Activity implements OnClickListener
{
	Button loginButton, registerButton;
	TextView forgotPasswordTextView;
	
	
	EditText etLoginUserID, etLoginPwd;
	ProgressDialog pd;
	
	String userid;
	String pwd;
	
	String fullname;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	 loginButton = (Button) findViewById(R.id.button1);
	 forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordtextView1);
	 registerButton = (Button) findViewById(R.id.button3);
	 
	 loginButton.setOnClickListener(this);
	 forgotPasswordTextView.setOnClickListener(this);
	 registerButton.setOnClickListener(this);
	 
	 etLoginUserID = (EditText) findViewById(R.id.editTextLoginUserID);
	 etLoginPwd = (EditText) findViewById(R.id.editTextLoginPwd);
		
	}

	@Override
	public void onClick(View v) {

//		boolean available =  MyCheckInternetConnection.isAvailable(this);
//		
//		if(available == false )
//			return;
		
		switch(v.getId())
		{
			case R.id.button1: //login
				
				userid = etLoginUserID.getText().toString();
				pwd =  etLoginPwd.getText().toString();
				
				if(userid.length() == 0 || pwd.length() == 0)
				{
					if( userid.length() == 0)
						Toast.makeText(getApplicationContext(), "Plz enter userid", Toast.LENGTH_LONG).show();
					else
						Toast.makeText(getApplicationContext(), "Plz enter password", Toast.LENGTH_LONG).show();
					
					return;
				}
				
				
				MyHttpAsycTaskLogin obj = new MyHttpAsycTaskLogin();
				obj.execute("http://"+ IPAddressHolder.IPAddress +":12113/forAndroidClientServerStudentAuthentication/rest/Customer/"+userid+"/"+pwd);
				
				etLoginUserID.setText("");
				etLoginPwd.setText("");
				
				break;
			case R.id.forgotPasswordtextView1: //forgot password
				Intent in1 = new Intent(this, ForgotPasswordActivity.class);
				startActivity(in1);
				break;
				
			case R.id.button3: //registration
				
				Intent in2 = new Intent(this, NewUserRegistrationActivity.class);
				startActivity(in2);
				break;
		}
		
	}	
	
	class MyHttpAsycTaskLogin extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Authenticating from server, plz wait...");
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
					result = "Problem in receiving the acknowledgement message";
			
			}
			catch(Exception exp)
			{
				result =  "Problem in sending Login details to the Server" + exp.getMessage();
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(final String result) {
				super.onPostExecute(result);
				
				if(pd.isShowing())
					pd.dismiss();
				
				if(! result.contains("no scope"))
				{
					
					try
					{
						//Converting JSON to Customer Object
						JSONObject json = new JSONObject(result);
						
						fullname = (String)json.get("fullname");
					
						
						//Now retrieving the profile pic separately
						MyHttpAsycTaskGetPic obj = new MyHttpAsycTaskGetPic();
						obj.execute("http://"+ IPAddressHolder.IPAddress +":12113/forAndroidClientServerStudentAuthentication/rest/Customer/readImage/"+userid);
						
					}
					catch (final Exception e)
					{
						runOnUiThread( new Runnable() {
							@Override
							public void run() {						
								Toast.makeText(getApplicationContext(), "Android JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
							}
						});
					}

				}
				else
				{
					runOnUiThread( new Runnable() {
						@Override
						public void run() {						
							Toast.makeText(getApplicationContext(), result.substring(9), Toast.LENGTH_LONG).show();
						}
					});
				}
				
				
		}
		
	}
	
	class MyHttpAsycTaskGetPic extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Getting Pic from server, plz wait...");
            pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			
			String result = myPostDataToServer(params[0]);
			
			return result;
		}
		
		private String myPostDataToServer(String url) {
			
			String picresult = null;
			
			try
			{				
			   HttpClient httpClient =  new DefaultHttpClient();
			   
			   //to avoid timeout 
			   BasicHttpParams params = new BasicHttpParams();
			   HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
			   
		       HttpGet httpGet = new HttpGet(url);
		       HttpContext localContext =  new BasicHttpContext();
		       
		       HttpResponse response =  httpClient.execute(httpGet, localContext);
		       
		       runOnUiThread( new Runnable() {
					@Override
					public void run() {						
						Toast.makeText(getApplicationContext(), "Pic Request sent to server", Toast.LENGTH_LONG).show();
					}
				});
		       
		       HttpEntity entity =  response.getEntity();
		       InputStream ins =  entity.getContent();
		       
		       runOnUiThread( new Runnable() {
					@Override
					public void run() {						
						Toast.makeText(getApplicationContext(), "Picture Response Received from server", Toast.LENGTH_LONG).show();
					}
				});
		       
		       
		     //convert input to string type
		       if(ins != null)
		       {
					BufferedReader br =new BufferedReader(new InputStreamReader(ins));
					
					String line = "";
					while( (line =  br.readLine()) != null)
						picresult += line;
					
					ins.close();
		       }
		       else
					picresult = "Problem in receiving the image data";
		       
			}
			catch(final Exception exp)
			{
				runOnUiThread( new Runnable() {
					@Override
					public void run() {						
						Toast.makeText(getApplicationContext(), "Runtime error: " + exp.getMessage(), Toast.LENGTH_LONG).show();
					}
				});
				
				return exp.getMessage();
			}
			
			return picresult;
			
		}

		@Override
		protected void onPostExecute(final String picresult) {
				super.onPostExecute(picresult);
				
				if(pd.isShowing())
					pd.dismiss();
				
				String result2 = picresult.substring(4); //in the string received, it starts with null
				
				//contains only "na" if image not available (after removing null from nullna)
				
				Intent successIn = new Intent(MainActivity.this, LoginSuccessActivity.class);
				
				successIn.putExtra("userid", userid);
				successIn.putExtra("fullname", fullname);
			//	successIn.putExtra("accountbalance", accountbalance);
	
				
				if(result2.equals("na"))
					successIn.putExtra("profilepic", new byte[]{});
				else
				{
					byte[] bytes =  Base64.decode(result2, Base64.DEFAULT);
					Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
					
					ByteArrayOutputStream baos =new ByteArrayOutputStream();
					bm.compress(Bitmap.CompressFormat.JPEG, 10, baos);
					byte[] bytes2 = baos.toByteArray();
					
					successIn.putExtra("profilepic", bytes2);
				}
				
				startActivity(successIn);
		}
		
	}

}
