package com.myapp.mynewapp.Models;

public class users {
    String Userid,Username,Profilepic;

    public users(String userid, String username, String profilepic) {
        Userid = userid;
        Username = username;
        Profilepic = profilepic;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getUsername(String displayName) {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getProfilepic() {
        return Profilepic;
    }

    public void setProfilepic(String profilepic) {
        Profilepic = profilepic;
    }
    public users(){}
}
