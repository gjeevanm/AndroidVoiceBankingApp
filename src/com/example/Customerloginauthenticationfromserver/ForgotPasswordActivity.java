package com.example.Customerloginauthenticationfromserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;

import model.IPAddressHolder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.example.Customerloginauthenticationfromserver.NewUserRegistrationActivity.MyHttpAsycTaskCheck;
import com.example.studentloginauthenticationfromserver.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends Activity implements OnClickListener
{
	ProgressDialog pd;
	
	EditText etPhone;
	String phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		
		Button b = (Button) findViewById(R.id.buttonRecover);
		
		etPhone = (EditText) findViewById(R.id.editTextPhoneForPwd);
		
		b.setOnClickListener(this);
	}

	//Recover button
	@Override
	public void onClick(View v) {
		
		phoneNumber =  etPhone.getText().toString();
		
		if(phoneNumber.length() != 10)
		{
			Toast.makeText(this, "Plz enter your registered 10 digit Phone number", Toast.LENGTH_LONG).show();
			return;
		}
		
		MyHttpAsycTaskRecoverPwd obj = new MyHttpAsycTaskRecoverPwd();
		obj.execute("http://"+ IPAddressHolder.IPAddress +":12113/forAndroidClientServerStudentAuthentication/rest/Customer/phoneVerify/"+phoneNumber);
		
	}

	class MyHttpAsycTaskRecoverPwd extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(ForgotPasswordActivity.this);
            pd.setMessage("Recovering from server, plz wait...");
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
				result =  "Problem in sending email ID to the Server" + exp.getMessage();
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(final String result) {
				super.onPostExecute(result);
				
				if(pd.isShowing())
					pd.dismiss();
				
				if(result.contains(","))
				{
					String userid = result.substring(0, result.indexOf(","));
					String pwd = result.substring(result.indexOf(",") + 1);
					
					mySendMessageToPhone(userid, pwd);
				}
				else
				{
					runOnUiThread( new Runnable() {
						@Override
						public void run() {						
							Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
						}
					});
				}
					
		}

		private void mySendMessageToPhone(String userid, String pwd)
		{	
			String message = "Standard Chartered Mobile App\nYour Account id: "  + userid + "\nPassword: " + pwd;
			
			//Now sending sms
			SmsManager sm = SmsManager.getDefault();		
			sm.sendTextMessage(phoneNumber, null, message, null, null);
			
			
			runOnUiThread( new Runnable() {
				@Override
				public void run() {						
					Toast.makeText(getApplicationContext(), "userid and password has been sent as SMS to your registered phone number", Toast.LENGTH_LONG).show();
				}
			});
			
			finish();
		}		
		
	}

}
