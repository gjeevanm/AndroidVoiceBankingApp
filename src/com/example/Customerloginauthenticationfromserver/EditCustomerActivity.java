package com.example.Customerloginauthenticationfromserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import model.IPAddressHolder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.example.Customerloginauthenticationfromserver.ForgotPasswordActivity.MyHttpAsycTaskRecoverPwd;
import com.example.studentloginauthenticationfromserver.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditCustomerActivity extends Activity implements OnClickListener
{
	ProgressDialog pd;

	String userid, fullname,phone, email;
	
	EditText etuserid, etfullname, etphone, etemail;
	Button btnUpdate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_student);
		
		Intent in =  getIntent();
		
		userid = in.getStringExtra("userid");
		fullname = in.getStringExtra("fullname");
		
		phone = in.getStringExtra("phone");
		email = in.getStringExtra("email");
		
	
		etuserid = (EditText) findViewById(R.id.editTextUpdateUserID);
		etfullname = (EditText) findViewById(R.id.editTextUpdateName);
		
		etphone = (EditText) findViewById(R.id.editTextUpdatePhone);
		etemail = (EditText) findViewById(R.id.editTextUpdateEmail);
		
		btnUpdate = (Button) findViewById(R.id.buttonUpdateProfile);
		
		btnUpdate.setOnClickListener(this);
		
		etuserid.setText(userid);
		etfullname.setText(fullname);
		etphone.setText(phone);
		etemail.setText(email);
		
	}

	@Override
	public void onClick(View v) {
		
		String fullnameStr = etfullname.getText().toString();
		
		String phoneStr = etphone.getText().toString();
		String emailStr = etemail.getText().toString();
		
		String fullnameStr2 = "";
		
		try
		{
			fullnameStr2 =  URLEncoder.encode(fullnameStr, "UTF-8").replace("+", "%20");
		}
		catch (UnsupportedEncodingException e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			return;
		}

		MyHttpAsycTaskUpdateDetails obj = new MyHttpAsycTaskUpdateDetails();
		obj.execute("http://"+ IPAddressHolder.IPAddress +":38611/forAndroidClientServerStudentAuthentication/rest/Customer/updateMyDetails/" + userid + "/" + fullnameStr2 + "/"  + phoneStr + "/" + emailStr);		
	}

	class MyHttpAsycTaskUpdateDetails extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(EditCustomerActivity.this);
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
