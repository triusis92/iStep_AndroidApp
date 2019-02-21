package com.example.espana83.istep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class StoreFragment extends Fragment {
    private static View view,view2;
    private static double spentSteps = 0;
    private static ListView storeListView;
    private static StoreAdapter adapter;
    private static TextView stepsDisplay;
    private static final String STORE_FILENAME = "myItems";
    private MyBroadcastReceiver myReceiver;
    private static PopupWindow popUpWindowRecipe;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_store_fragment, container, false);
        myReceiver = new MyBroadcastReceiver();
        inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);//needed for popup
        view2 = inflater.inflate(R.layout.recipe_pop_up, null);//view for popup

        storeListView = (ListView)view.findViewById(R.id.storeListView);
        stepsDisplay = (TextView)view.findViewById(R.id.storeStepDisplay);
        double yoke = (BackgroundService.totalSteps + BackgroundService.steps) - BackgroundService.spentSteps;
        stepsDisplay.setText("Steps available to spend: " + yoke);
        adapter = new StoreAdapter(view.getContext(), R.layout.store_row, LoginActivity.items);//populate adapter listview with shtuff
        storeListView.setAdapter(adapter);//display the listview
        popUpWindowRecipe = new PopupWindow(view2);
        popUpWindowRecipe.setBackgroundDrawable(new ColorDrawable(Color.WHITE));//set popup background as transparent
        popUpWindowRecipe.setContentView(view2);
        return view;

    }

    public static void doSomething(Context context, int position, boolean isRecipe)
    {
        if(((BackgroundService.totalSteps + BackgroundService.steps) - BackgroundService.spentSteps) < LoginActivity.items.get(position).getPrice())
        {
            Toast.makeText(context, "Unfortunately you dont have enough steps for that", Toast.LENGTH_LONG).show();
        }
        else
        {
            if(isRecipe)
            {
                displayRecipe(LoginActivity.items.get(position).getRecipeText());
            }
            else
            {
                Toast.makeText(context, "Congrats, you have purchased " + LoginActivity.items.get(position).getName(), Toast.LENGTH_LONG).show();
            }
            BackgroundService.spentSteps = BackgroundService.spentSteps + LoginActivity.items.get(position).getPrice();
            LoginActivity.items.get(position).setPurchased(true);
            adapter = new StoreAdapter(view.getContext(), R.layout.store_row, LoginActivity.items);//populate adapter listview with shtuff
            storeListView.setAdapter(adapter);//display the listview
            writeStore();
            BackgroundService.updateAccount(context);
            BackgroundService.writeValues(context);
            double yoke = (BackgroundService.totalSteps + BackgroundService.steps) - BackgroundService.spentSteps;
            stepsDisplay.setText("Steps available to spend: " + yoke);
        }

    }

    private static void displayRecipe(String text)
    {
        popUpWindowRecipe.showAtLocation(view.findViewById(R.id.activity_store_fragment), Gravity.CENTER, 10, 10);
        popUpWindowRecipe.setFocusable(true);//needed for spinner to work

        TextView recipe = (TextView)view2.findViewById(R.id.recipeTextView);
        recipe.setText(text);
                if (android.os.Build.VERSION.SDK_INT >= 24) {
            popUpWindowRecipe.update(50, 300, 1000, 1500);
        } else if (android.os.Build.VERSION.SDK_INT >= 23) {
            popUpWindowRecipe.update(0, 300, 1000, 1500);
        } else if (android.os.Build.VERSION.SDK_INT <= 22) {
            popUpWindowRecipe.update(0, 0, 550, 1300);
        }
        Button closeRecipe = (Button)view2.findViewById(R.id.recipeCloseBtn);
        closeRecipe.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                popUpWindowRecipe.dismiss();//closes p
            }

        });

    }

    private static void writeStore() {

        ObjectOutputStream outputStream = null;
        try {
            // create Android FileOutputStream, private file for this app only
            FileOutputStream fos = view.getContext().openFileOutput(STORE_FILENAME,
                    Context.MODE_PRIVATE);
            // Construct the output stream
            outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(LoginActivity.items);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            // Close the ObjectOutputStream
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            Bundle b = arg1.getExtras();
            double current = b.getDouble("currentSteps");
            double total = b.getDouble("totalSteps");
            double yoke = (total + current) - BackgroundService.spentSteps;
            stepsDisplay.setText("Steps available to spend: " + yoke);

        }
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
}