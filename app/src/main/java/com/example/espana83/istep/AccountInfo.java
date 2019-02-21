package com.example.espana83.istep;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Lance on 23/04/2017.
 */

@DynamoDBTable(tableName = "accounts")
public class AccountInfo implements Comparable<AccountInfo>{
    private String userID;
    private double totalSteps;
    private double spentSteps;
    private double todaysSteps;

    public AccountInfo() {

    }

    @DynamoDBHashKey(attributeName = "userID")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @DynamoDBAttribute(attributeName = "totalSteps")
    public double getTotalSteps(){return totalSteps;}

    public void setTotalSteps(double totalSteps){this.totalSteps = totalSteps;}

    @DynamoDBAttribute(attributeName = "spentSteps")
    public double getSpentSteps(){return spentSteps;}

    public void setSpentSteps(double spentSteps){this.spentSteps = spentSteps;}

    @DynamoDBAttribute(attributeName = "todaysSteps")
    public double getTodaysSteps(){return todaysSteps;}

    public void setTodaysSteps(double todaysSteps){this.todaysSteps = todaysSteps;}

    public int compareTo(AccountInfo that) {
        if(this.todaysSteps < that.getTodaysSteps())
            return 1;
        if(this.todaysSteps > that.getTodaysSteps())
            return -1;
        return 0;
    }
}
