package com.example.homerenting_prototype_one.model;

public class DataModel {
    String date;
    String time;
    String name;
    String gender;
    String phone;
    String address;

    public DataModel(String date, String time, String name, String gender, String phone, String address){
        this.date = date;
        this.time = time;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
    }

    public String getDate(){
        return date;
    }
    public String getTime(){
        return time;
    }
    public String getName(){
        return name;
    }
    public String getGender(){
        return gender;
    }
    public String getPhone(){
        return phone;
    }
    public String getAddress(){
        return address;
    }
}
