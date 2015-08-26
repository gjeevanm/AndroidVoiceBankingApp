package model;

import org.json.JSONException;
import org.json.JSONObject;


public class CustomerToJSON {
	
	public static String convert(Customer s) throws JSONException
	{
		JSONObject json = new JSONObject();
		
				
		json.put("userid", s.getuserid());
		json.put("emailid", s.getEmail());
		json.put("fullname", s.getFullname());
		json.put("pwd", s.getPassword());
		json.put("phone", s.getPhone());
		json.put("accountbalance", s.getAccountbalance());

		//json.put("accountbalance", true);
		
		String jsonResultFromCustomerObjec =  json.toString();
		
		return jsonResultFromCustomerObjec;
	}

}

