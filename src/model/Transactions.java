package model;

public class Transactions {
	
	private String userid;
	private String amount;
	private String userid2;
	public Transactions(String userid, String amount, String userid2) {
		super();
		this.userid = userid;
		this.amount = amount;
		this.userid2 = userid2;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getUserid2() {
		return userid2;
	}
	public void setUserid2(String userid2) {
		this.userid2 = userid2;
	}
	
	

}
