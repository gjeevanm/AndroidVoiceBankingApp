package com.example.Customerloginauthenticationfromserver;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import model.IPAddressHolder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import com.example.Customerloginauthenticationfromserver.MainActivity.MyHttpAsycTaskGetPic;
import com.example.Customerloginauthenticationfromserver.MainActivity.MyHttpAsycTaskLogin;
import com.example.Customersloginauthenticationfromserver.*;
import com.example.studentloginauthenticationfromserver.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginSuccessActivity extends Activity implements OnClickListener
{
	private TextView txtSpeechInput;
	private ImageButton btnSpeak;
	private final int REQUEST_SPEECH = 100;
    TextView tvWelcome, tvDetails, tvEdit;
	ImageView ivprofilePic;
	Button buttonSavePic;
	TextView tvPicStatus;
	TextToSpeech t1;
	String userid, fullname,accountbalance;
	String speech="Welcome";
	ProgressDialog pd;
	Bitmap bm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_success);
		
		Intent in =  getIntent();
		userid=  in.getStringExtra("userid");
	//	accountbalance=in.getStringExtra("accountbalance");
		fullname =  in.getStringExtra("fullname");
		byte[] profilepic =  in.getByteArrayExtra("profilepic");
		
		
		tvWelcome = (TextView) findViewById(R.id.textViewWelcome);
		tvDetails = (TextView) findViewById(R.id.textViewDetails);
		tvEdit = (TextView) findViewById(R.id.textViewEdit);
		
		tvWelcome.setText("Welcome " + userid );
		
		tvDetails.setText("Name: " + fullname);
		
            tvWelcome.addTextChangedListener(new TextWatcher() {
			
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
				String toSpeak = speech+userid;
	            Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
	            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
				}
		});	
		
	
		
		
		ivprofilePic = (ImageView) findViewById(R.id.imageViewProfilePic);
		
		tvPicStatus = (TextView) findViewById(R.id.textViewPicStatus);
		
		if(profilepic.length != 0)
		{
			Bitmap retrievedPic =  BitmapFactory.decodeByteArray(profilepic, 0, profilepic.length);
			ivprofilePic.setImageBitmap(retrievedPic);
			
			tvPicStatus.setText("Tap to Update Pic");
		}
		//otherwise leave the default image	
		
		buttonSavePic = (Button) findViewById(R.id.buttonSavePic);
		
		buttonSavePic.setOnClickListener(this);
		
		ivprofilePic.setOnClickListener(this);
		tvEdit.setOnClickListener(this);
		
		txtSpeechInput = (TextView) findViewById(R.id.micTextField);
		btnSpeak = (ImageButton) findViewById(R.id.micButton);
 
		btnSpeak.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		
		String url = "";
		
		switch(v.getId())
		{
			
			case R.id.imageViewProfilePic:   //Get Pic from gallary
				Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(galleryIntent, 9999);  //9999: unique request code
				break;
			//------------------------------------------
				
			case R.id.buttonSavePic:
				
				//update the Student table in External Database by adding the pic field
				//sending image data to RESTFul Web Service
				
				MyHttpAsycTaskSavePic obj = new MyHttpAsycTaskSavePic();
				obj.execute("http://"+ IPAddressHolder.IPAddress +":12113/forAndroidClientServerStudentAuthentication/rest/Customer/savepic");
				
				break;
				
			case R.id.textViewEdit:
				
				MyHttpAsycTaskGetDetails obj2 = new MyHttpAsycTaskGetDetails();
				obj2.execute("http://"+ IPAddressHolder.IPAddress +":12113/forAndroidClientServerStudentAuthentication/rest/Customer/myGetDetails/"+userid);
				break;
				
			case R.id.micButton:
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello Customer,Say Balance or Transfers");
				startActivityForResult(intent, REQUEST_SPEECH);
				break;
				
		}
		
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 9999 && resultCode == RESULT_OK && data != null)
		{
			Uri selectedImage =  data.getData();
			String[] filePathColumn = {MediaStore.Images.Media.DATA};
			
			Cursor cursor =  getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			
			cursor.moveToFirst();
			
			int columnIndex =  cursor.getColumnIndex(filePathColumn[0]);
			String picturePath =  cursor.getString(columnIndex);
			cursor.close();
			
			bm =  BitmapFactory.decodeFile(picturePath);
			
			ivprofilePic.setImageBitmap(bm);
			
			tvPicStatus.setText("Pic Selected");
			buttonSavePic.setEnabled(true);  //enabling "Save" button
			
		}
		else if( (requestCode == REQUEST_SPEECH))
				{
	            if (resultCode == RESULT_OK){
	                ArrayList<String> matches = data
	                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

	                if (matches.size() == 0) {
	                    // didn't hear anything
	                } else {
	                    String mostLikelyThingHeard = matches.get(0);
	                    // toUpperCase() used to make string comparison equal
	                    if(mostLikelyThingHeard.toUpperCase().equals("TRANSFERS")){
	                    	Intent i=new Intent(LoginSuccessActivity.this,Transfers.class);
	                    	i.putExtra("userid", userid);
	                    	//i.putExtra("accountbalance", accountbalance);
	                    	startActivity(i);
	                        //startActivity(new Intent(LoginSuccessActivity.this, Transfers.class));
	                        
	                        
	                    }
	                    else
	                    	 mostLikelyThingHeard=matches.get(0);
	                    	if(mostLikelyThingHeard.toUpperCase().equals("BALANCE"))
	                    {
	                    		
	                    		Intent i1=new Intent(LoginSuccessActivity.this,CheckBalance.class);
		                    	//i1.putExtra("fullname", fullname);
		                    //	i1.putExtra("accountbalance", accountbalance);
	                    		i1.putExtra("userid", userid);
		                    	startActivity(i1);
	                    	//startActivity(new Intent(LoginSuccessActivity.this,CheckBalance.class));
	                    }
	                    	else
		                    	 mostLikelyThingHeard=matches.get(0);
		                    	if(mostLikelyThingHeard.toUpperCase().equals("TRANSACTIONS"))
		                    {
		                    		
		                    		Intent i8=new Intent(LoginSuccessActivity.this,TransactionHistory.class);
			                    	//i1.putExtra("fullname", fullname);
			                    //	i1.putExtra("accountbalance", accountbalance);
		                    		i8.putExtra("userid", userid);
			                    	startActivity(i8);
		                    	//startActivity(new Intent(LoginSuccessActivity.this,CheckBalance.class));
		                    }
	                }
	            }
	        }


	}
	
	
	class MyHttpAsycTaskSavePic extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(LoginSuccessActivity.this);
            pd.setMessage("Sending Pic to server, plz wait...");
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
				HttpClient httpClient = new DefaultHttpClient();
				
				BasicHttpParams params = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(params, 2 * 60 * 1000);
				   
				HttpPost httpPost = new HttpPost(url);
				
				//-------------------------------------------
				ByteArrayOutputStream bos =new ByteArrayOutputStream();
				bm.compress(CompressFormat.JPEG, 100, bos);
				byte[] imageBytes =  bos.toByteArray();
				String encodedImage =  Base64.encodeToString(imageBytes, Base64.DEFAULT);
				
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("myimage", encodedImage));
				pairs.add(new BasicNameValuePair("userid", userid));
				//--------------------------------------
				
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
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
				result =  "Problem in sending Profile Pic to the Server" + exp.getMessage();
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(final String result) {
				super.onPostExecute(result);
				
				if(pd.isShowing())
					pd.dismiss();
				
				tvPicStatus.setText("Pic Saved");
				
				runOnUiThread( new Runnable() {
					@Override
					public void run() {						
						Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
					}
				});
				
		}
		
	}
	//-----------------------------------------------
	
	class MyHttpAsycTaskGetDetails extends AsyncTask<String, Void, String>
	{	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pd = new ProgressDialog(LoginSuccessActivity.this);
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
						//Converting JSON to Student Object
						JSONObject json = new JSONObject(result);
						
						fullname = (String)json.get("fullname");
						userid=(String)json.getString("userid");
						//accountbalance=(String)json.get("accountbalance");
						Intent tran=new Intent(LoginSuccessActivity.this,Transfers.class);
						tran.putExtra("userid", userid);
						startActivity(tran);
		
						
						
						Intent editIn = new Intent(LoginSuccessActivity.this, EditCustomerActivity.class);
							editIn.putExtra("userid",userid);
							editIn.putExtra("fullname", fullname);
		
						startActivity(editIn);
						
						
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

}
