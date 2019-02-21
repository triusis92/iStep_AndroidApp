package com.example.espana83.istep;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.ArrayList;
import java.util.Date;

public class GoalFragment extends Fragment {

    View view, view2;
    private double steps;
    private Button createGoalBtn;
    private PopupWindow popUpWindow;
    private ArrayList<Goal> goals = new ArrayList<>();
    private DynamoDBMapper mapper = LoginActivity.getMapper();
    private ListView goalListView;
    GoalAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_goal, container, false);
        inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);//needed for popup
        view2 = inflater.inflate(R.layout.pop_up, null);//view for popup
        createGoalBtn = (Button) view.findViewById(R.id.button2);
        goalListView = (ListView)view.findViewById(R.id.goalListViewID);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            steps = extras.getDouble("count");
        }

        try{
            BackgroundService.updateGoals(getContext());
            Person p = mapper.load(Person.class, LoginActivity.getUser().getUserId());
            goals = p.getGoals();

        }catch(Exception e){
        }


        adapter = new GoalAdapter(view.getContext(), R.layout.row_view, goals);//populate adapter listview with goals
        goalListView.setAdapter(adapter);//display the listview
        popUpWindow = new PopupWindow(view2);
        popUpWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));//set popup background as transparent
        popUpWindow.setContentView(view2);


        for(int i = 0; i < BackgroundService.goals.size(); i++)
        {
            if(!BackgroundService.goals.get(i).getActive())
            {
                if(BackgroundService.goals.get(i).getCompleted())
                {
                    // successes in here
                    Toast.makeText(getContext(),BackgroundService.goals.get(i).getName() + " is complete!", Toast.LENGTH_SHORT).show();
                    goals.remove(goals.get(i));
                }
                else
                {
                    // failures in here
                    Toast.makeText(getContext(),BackgroundService.goals.get(i).getName() + " was not completed! Better luck next time!", Toast.LENGTH_SHORT).show();
                    goals.remove(goals.get(i));
                }
                BackgroundService.goals.remove(BackgroundService.goals.get(i));
                BackgroundService.updateGoals(getContext());
                adapter.notifyDataSetChanged();
            }
        }
        //goalProgressChange();
        buttonClick();

        return view;
    }


    private void buttonClick()//once the button is clicked, pop up window pops out over the original layout, and original shit set invisible
    {
        createGoalBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int limit = 2;
                if(LoginActivity.items.get(0).getPurchased())
                    limit++;
                if(goals.size() == limit)
                    Toast.makeText(getContext(),"Goal limit reached, complete current goals to add more", Toast.LENGTH_SHORT).show();
                else {
                    popUpWindow.showAtLocation(view.findViewById(R.id.activity_goal), Gravity.CENTER, 10, 10);
                    popUpWindow.setFocusable(true);//needed for spinner to work
                    if (android.os.Build.VERSION.SDK_INT >= 24) {
                        popUpWindow.update(50, 300, 1000, 1500);
                    } else if (android.os.Build.VERSION.SDK_INT >= 23) {
                        popUpWindow.update(0, 300, 1000, 1500);
                    } else if (android.os.Build.VERSION.SDK_INT <= 22) {
                        popUpWindow.update(0, 0, 575, 1300);
                    }

                    closePop();
                }
            }

        });
    }
    private void closePop()//generic method that closes popup window and sets everything to visible again
    {
        Button createGoalBtn2 = (Button)view2.findViewById(R.id.cglCreate);
        createGoalBtn2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                EditText name = (EditText)view2.findViewById(R.id.cglName);
                EditText target = (EditText)view2.findViewById(R.id.cglTarget);
                EditText days = (EditText)view2.findViewById(R.id.cglDays);
                RadioGroup type = (RadioGroup) view2.findViewById(R.id.cglRadioGroup);
                RadioButton checked = (RadioButton) view2.findViewById(type.getCheckedRadioButtonId());
                if(name.getText()!=null && days.getText().toString().matches("\\d+(?:\\.\\d+)?") && target.getText().toString().matches("\\d+(?:\\.\\d+)?")) {

                    try {
                        Goal g = new Goal();
                        g.setName(name.getText().toString());
                        g.setTarget(Double.parseDouble(target.getText().toString()));
                        g.setDays(Integer.parseInt(days.getText().toString()));
                        g.setType(checked.getText().toString());
                        g.setCreated(new Date());
                        g.setProgress(0);
                        g.setActive(true);
                        g.setCompleted(false);
                        goals.add(g);
                        Person p = new Person();
                        p.setUserID(LoginActivity.getUser().getUserId());
                        p.setGoals(goals);
                        mapper.save(p);
                        BackgroundService.goals = goals;
                        adapter.notifyDataSetChanged();//update listview

                    } catch (Exception exception) {

                    }

                    popUpWindow.dismiss();//closes pop
                    name.getText().clear();
                    target.getText().clear();
                    days.getText().clear();
                    type.clearCheck();
                }
                else
                {
                    Toast.makeText(getContext(), "Must enter all fields",
                            Toast.LENGTH_LONG).show();
                }
                //createGoalBtn.setVisibility(View.VISIBLE);
            }

        });
    }

    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e("App Error",exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if(temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if(temp != null && temp.length() > 0) {
                return formattedString;
            }
        }
        return  formattedString;
    }
}