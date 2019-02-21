package com.example.espana83.istep;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.content.BroadcastReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class StepsFragment extends Fragment {

    View view;
    private TextView stepsTotal,levelText,calText,distText,activityStatus;
    private ProgressBar progressBar;
    private int level=0;
    private double value=0;
    private double total=0;
    private double totalSteps=0;
    private double calories;
    private double distance;
    private Context context;
    private MyBroadcastReceiver myReceiver;
    private Profile p;
    private DynamoDBMapper mapper = LoginActivity.getMapper();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_steps, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        stepsTotal = (TextView) view.findViewById(R.id.totalSteps);
        activityStatus = (TextView) view.findViewById(R.id.textView19);
        levelText = (TextView) view.findViewById(R.id.levelText);
        calText = (TextView) view.findViewById(R.id.caloriesText);
        distText = (TextView) view.findViewById(R.id.distanceText);
        context = getActivity().getApplicationContext();
        myReceiver = new MyBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter("YourAction");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myReceiver, intentFilter);
        readValues();

        return view;
    }

    private void readValues() {
        {
            ObjectInputStream inputStream = null;
            try {
                levelText.setText("Level "+level);
                // Android get file input stream
                FileInputStream fis = getActivity().openFileInput("values");
                // Construct the ObjectInputStream object
                inputStream = new ObjectInputStream(fis);
                totalSteps = inputStream.readDouble();
                value = inputStream.readDouble();
                stepsTotal.setText("Today's\n\tSteps\n"+(int)value);
                int agg = (int)(totalSteps+value);
                level = agg/100;
                levelText.setText("Level "+level);
                progressBar.setProgress(agg%100);//reset progress bar
                caloriesBurnedCalculator();
            } catch (EOFException ex) {
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            } finally {
                // Close the ObjectInputStream
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
    }
    private void updateBar()//updates and animates progress bar
    {
        progressBar.setProgress((int)(totalSteps%100));//reset progress bar
    }
    private void caloriesBurnedCalculator()
    {
        double weight;
        double height;
        // Fill with your data
        try {
            p = mapper.load(Profile.class, LoginActivity.getUser().getUserId());
            weight = p.getWeight(); // kg
            height = p.getHeight(); // cm

        } catch (Exception e) {
            weight = 68.0;
            height=170;
        }

        double stepsCount = value;
        //Don't edit below this
        final  double walkingFactor = 0.57;
        double CaloriesBurnedPerMile;
        double strip;
        double stepCountMile; // step/mile
        double conversationFactor;
        double CaloriesBurned;
        NumberFormat formatter = new DecimalFormat("#0.00");
        //double distance;
        CaloriesBurnedPerMile = walkingFactor * (weight * 2.2);
        strip = height * 0.415;
        stepCountMile = 160934.4 / strip;
        conversationFactor = CaloriesBurnedPerMile / stepCountMile;
        calories = stepsCount * conversationFactor;
        distance = (stepsCount * strip) / 100000;
        calText.setText("Calories Burned\n\t\t\t\t\t"+formatter.format(calories));
        distText.setText("Distance Walked\n\t\t\t\t "+formatter.format(distance)+" km");
    }

    @Override
    public void onStart(){
        super.onStart();
        myReceiver = new MyBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter("YourAction");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myReceiver, intentFilter);
    }
    @Override
    public void onResume(){
        super.onResume();
        myReceiver = new MyBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter("YourAction");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myReceiver, intentFilter);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            Bundle b = arg1.getExtras();
            value = b.getDouble("currentSteps");
            total = b.getDouble("totalSteps");
            totalSteps = value+total;
            //level = (int)totalSteps/100;
            int agg = (int)(totalSteps+value);
            if(agg%100==0)
            {
                level++;
                levelText.setText("Level "+level);
            }

            if(value<2000)
            {
                activityStatus.setText("Activity Level | Sedentary");
            }
            else if(value>2000 && value<5000)
            {
                activityStatus.setText("Activity Level | Lightly Active");
            }
            else if(value>5000 && value<10000)
            {
                activityStatus.setText("Activity Level | Active");
            }
            else if(value>10000 && value<20000)
            {
                activityStatus.setText("Activity Level | Very Active");
            }
            else if(value>20000)
            {
                activityStatus.setText("Activity Level | Highly Active");
            }
            stepsTotal.setText("Today's\n\tSteps\n"+(int)value);
            updateBar();//updates progress bar when app is opened
            caloriesBurnedCalculator();
        }
    }
}