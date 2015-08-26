package model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class MyCheckInternetConnection {
	
	public static boolean isAvailable(Context ctx)
	{
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	      
	      NetworkInfo wifi =  cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	      NetworkInfo mobileDataPackage =  cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	      
	      //returns null if the type is not supported by the device. 
	      if(wifi != null && wifi.isConnected())
	      {
	    	  //Toast.makeText(ctx, "You are connected to Internet via WIFI", Toast.LENGTH_LONG).show();
	    	  return true;
	      }
	      else if(mobileDataPackage != null && mobileDataPackage.isConnected())
	      {
	    	  
	    	  //Toast.makeText(ctx, "You are connected to Internet via Mobile Data Package", Toast.LENGTH_LONG).show();
	    	  return true;
	      }
	      else
	      {
	    	  Toast.makeText(ctx, "Sorry, Some form of Internet Connection is required!", Toast.LENGTH_LONG).show();
	    	  return false;
	      }
	}

}
