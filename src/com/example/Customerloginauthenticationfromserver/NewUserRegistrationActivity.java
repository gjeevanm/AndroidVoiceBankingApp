package com.example.Customerloginauthenticationfromserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import model.Customer;
import model.CustomerToJSON;
import model.IPAddressHolder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;

import com.example.studentloginauthenticationfromserver.R;

//import com.example.studentloginauthenticationfromserver.MainActivity.MyHttpAsycTaskLogin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewUserRegistrationActivity extends Activity implements OnClickListener
{
	EditText userIDEditText, emailIDEditText, fullnameEditText, pwdEditText, confirmPwdEditText, mobileEditText;
	ProgressDialog pd;
	TextView tvCheck;
	
	private static String emailIDRegExp = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	//sachin.itp@gmail.com
	//dhoni_sca@yahoo.co.in

/*
^			#start of the line

[_A-Za-z0-9-]+	#  must start with string in the bracket [ ], must contains one or more (+)

(			#  start of group #1

\\.[_A-Za-z0-9-]+	#     follow by a dot "." and string in the bracket [ ], must contains one or more (+)

)*			#  end of group #1, this group is optional (*)

@			#     must contains a "@" symbol

[A-Za-z0-9]+       #        follow by string in the bracket [ ], must contains one or more (+)

(			#	   start of group #2 - first level TLD checking

\\.[A-Za-z0-9]+  #	     follow by a dot "." and string in the bracket [ ], must contains one or more (+)

)*		#	   end of group #2, this group is optional (*)

(			#	   start of group #3 - second level TLD checking

\\.[A-Za-z]{2,}  #	     follow by a dot "." and string in the bracket [ ], with minimum length of 2

)			#	   end of group #3

$		

*/
	private static String contactNumberRegExp = "^[\\d]{10}$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_user_registration);
		
		Button b = (Button) findViewById(R.id.buttonNextRegForm);
		
		userIDEditText = (EditText) findViewById(R.id.editTextUserID);
		emailIDEditText = (EditText) findViewById(R.id.editTextEmail);
		fullnameEditText = (EditText) findViewById(R.id.editTextFullname);
		pwdEditText = (EditText) findViewById(R.id.editTextPwd);
		confirmPwdEditText = (EditText) findViewById(R.id.editTextConfirmPwd);
		mobileEditText = (EditText) findViewById(R.id.editTextMobile);
		
		tvCheck = (TextView) findViewById(R.id.textViewCheckAvailability);
		
		b.setOnClickListener(this);
		tvCheck.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
			case R.id.buttonNextRegForm:
				String userid =  userIDEditText.getText().toString();
				String emailid =  emailIDEditText.getText().toString();
				String fullname =  fullnameEditText.getText().toString();
				String pwd =  pwdEditText.getText().toString();
				String confirmPwd =  confirmPwdEditText.getText().toString();
				String phone =  mobileEditText.getText().toString();
				
				if(userid.equals("") || emailid.equals("") || fullname.equals("") || pwd.equals("") || confirmPwd.equals("") || phone.equals(""))
				{
					Toast.makeText(this, "No fields should be left blank", Toast.LENGTH_LONG).show();
					
					return;
				}
				
				if(userid.startsWith(" ") || fullname.startsWith(" ") || pwd.startsWith(" ") )
				{
					Toast.makeText(this, "Plz don't start with spaces", Toast.LENGTH_LONG).show();
					
					return;
				}
				
				
				if(! pwd.equals(confirmPwd))
				{
					Toast.makeText(this, "Confirm Password do not match", Toast.LENGTH_LONG).show();
					return;
				}
				
				if(! emailid.matches(emailIDRegExp) || ! phone.matches(contactNumberRegExp))
				{
					if( ! emailid.matches(emailIDRegExp) )
						Toast.makeText(this, "Plz enter valid email id", Toast.LENGTH_LONG).show();
					else
						Toast.makeText(this, "Plz enter 10 digit phone number", Toast.LENGTH_LONG).show();
					
					return;
				}
				
				Intent  in = new Intent(this, Registration.class);
				
				in.putExtra("userid", userid);
				in.putExtra("emailid", emailid);
				in.putExtra("fullname", fullname);
				in.putExtra("pwd", pwd);
				in.putExtra("phone", phone);
				
				startActivity(in);
			break;
			
		case R.id.textViewCheckAvailability:
			
			String userid2 =  userIDEditText.getText().toString();
			
			MyHttpAsycTaskCheck obj = new MyHttpAsycTaskCheck();
			obj.execute("http://"+ IPAddressHolder.IPAddress +":12113/forAndroidClientServerStudentAuthentication/rest/Customer/check/"+userid2);
			break;
		
		}	
		
	}
	
	class MyHttpAsycTaskCheck extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(NewUserRegistrationActivity.this);
            pd.setMessage("Checking from server, plz wait...");
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
				result =  "Problem in sending userid to the Server" + exp.getMessage();
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

}
