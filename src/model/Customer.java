package model;

public class Customer {
    
    private String userid;
    private String email;
    private String fullname;
    private String password;
    private String phone;
    private String accountbalance;
   

    public Customer() {
    }
    
    public Customer(String userid, String email, String fullname, String password, String phone,String accountbalance) {
        this.userid = userid;
        this.email = email;
        this.fullname = fullname;
        this.password = password;
        this.phone = phone;
        this.accountbalance=accountbalance;
        
    }
     
    /**
     * @return the userid
     */
    public String getuserid() {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setuserid(String userid) {
        this.userid = userid;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the fullname
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * @param fullname the fullname to set
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }


        /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

	public String getAccountbalance() {
		return accountbalance;
	}

	public void setAccountbalance(String accountbalance) {
		this.accountbalance = accountbalance;
	}
}


