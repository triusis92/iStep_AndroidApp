package com.example.espana83.istep;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

import java.util.Date;

@DynamoDBDocument
public class Goal {
    private String name;
    private String type;
    private double target;
    private int days;
    private Date created;
    private double progress;
    private boolean active;
    private boolean completed;

    public Goal()
    {

    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName(){return name;}

    public void setName(String name){this.name = name;}

    @DynamoDBAttribute(attributeName = "type")
    public String getType(){return type;}

    public void setType(String type){this.type = type;}

    @DynamoDBAttribute(attributeName = "target")
    public double getTarget(){return target;}

    public void setTarget(double target){this.target = target;}

    @DynamoDBAttribute(attributeName = "days")
    public int getDays(){return days;}

    public void setDays(int days){this.days = days;}

    @DynamoDBAttribute(attributeName = "created")
    public Date getCreated(){return created;}

    public void setCreated(Date created){this.created = created;}

    @DynamoDBAttribute(attributeName = "progress")
    public double getProgress(){return progress;}

    public void setProgress(double progress){this.progress = progress;}

    @DynamoDBAttribute(attributeName = "active")
    public boolean getActive(){return active;}

    public void setActive(boolean active){this.active = active;}

    @DynamoDBAttribute(attributeName = "completed")
    public boolean getCompleted(){return completed;}

    public void setCompleted(boolean completed){this.completed = completed;}
}