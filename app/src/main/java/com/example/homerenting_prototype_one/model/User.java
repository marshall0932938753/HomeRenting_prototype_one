package com.example.homerenting_prototype_one.model;

public class User {
    private String id;
    private String name;
    private String gender;
    private String phone;
    private String address;

//    public User(String name, String gender, String phone, String address){
//        setName(name);
//        setGender(gender);
//        setPhone(phone);
//        setAddress(address);
//    }

    public String getId(){ return id;}
    public void setId(String id){ this.id = id;}

    public String getName(){ return name;}
    public void setName(String name){ this.name = name;}

    public String getGender(){ return gender;}
    public void setGender(String gender){ this.gender = gender;}

    public String getPhone(){ return phone;}
    public void setPhone(String phone){ this.phone = phone;}

    public String getAddress(){ return address;}
    public void setAddress(String address){ this.address = address;}

}