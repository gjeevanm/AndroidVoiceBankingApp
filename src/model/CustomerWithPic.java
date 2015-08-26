package model;

public class CustomerWithPic {
    
    private String fullname;
    private byte[] profilepic;

    public CustomerWithPic() {
    }
    
    public CustomerWithPic(String fullname, byte[] profilepic) {
        this.fullname = fullname;
       
        this.profilepic = profilepic;
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
     * @return the profilepic
     */
    public byte[] getProfilepic() {
        return profilepic;
    }

    /**
     * @param profilepic the profilepic to set
     */
    public void setProfilepic(byte[] profilepic) {
        this.profilepic = profilepic;
    }
}
