package com.eneskilic.aboneliklerim.Model;

public class User {
    public String UserName;
    public String UserLastName;
    public String ProfilePhoto;
    public String BirthDate;
    public String Gender;

    public User(String UserName,String UserLastName,String ProfilePhoto,String BirthDate,String Gender){
        this.UserName = UserName;
        this.UserLastName = UserLastName;
        this.ProfilePhoto = ProfilePhoto;
        this.BirthDate = BirthDate;
        this.Gender = Gender;
    }

}
