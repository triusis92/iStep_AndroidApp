package com.example.espana83.istep;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.Serializable;
import java.util.ArrayList;

@DynamoDBTable(tableName = "goals")
public class Person implements Serializable {
    private String userID;
    private ArrayList<Goal> goals;

    @DynamoDBHashKey(attributeName = "userID")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @DynamoDBAttribute(attributeName = "goals")
    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public void setGoals(ArrayList<Goal> goals) {
        this.goals = goals;
    }
}