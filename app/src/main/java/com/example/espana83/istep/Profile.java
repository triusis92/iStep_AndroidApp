package com.example.espana83.istep;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Lance on 22/04/2017.
 */

@DynamoDBTable(tableName = "profiles")
public class Profile {
    private String userID;
    private int level;
    private double height;
    private double weight;
    private int age;
    private boolean gender;
    private double bmi;
    private String country;

    public Profile(){

    }

    @DynamoDBHashKey(attributeName = "userID")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @DynamoDBAttribute(attributeName = "level")
    public int getLevel(){return level;}

    public void setLevel(int level){this.level = level;}

    @DynamoDBAttribute(attributeName = "height")
    public double getHeight(){return height;}

    public void setHeight(double height){this.height = height;}

    @DynamoDBAttribute(attributeName = "weight")
    public double getWeight(){return weight;}

    public void setWeight(double weight){this.weight = weight;}

    @DynamoDBAttribute(attributeName = "age")
    public int getAge(){return age;}

    public void setAge(int age){this.age = age;}

    @DynamoDBAttribute(attributeName = "gender")
    public boolean getGender(){return gender;}

    public void setGender(boolean gender){this.gender = gender;}

    @DynamoDBAttribute(attributeName = "BMI")
    public double getBmi(){return bmi;}

    public void setBmi(double bmi){this.bmi = bmi;}

    @DynamoDBAttribute(attributeName = "country")
    public String getCountry(){return country;}

    public void setCountry(String country){this.country = country;}
}
