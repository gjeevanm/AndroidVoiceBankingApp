package model;

import org.json.JSONException;
import org.json.JSONObject;
import model.*;

public class TransactionsToJSON {
	public static String convert1(Transactions t) throws JSONException
	{
		JSONObject json = new JSONObject();
		
				
	  // json.put("userid", t.getUserid());
		//json.put("amount", t.getAmount());
		//json.put("userid2", t.getUserid2());
	

		//json.put("accountbalance", true);
		
		String jsonResultFromTransactionsObjec =  json.toString();
		
		return jsonResultFromTransactionsObjec;
	}

}




