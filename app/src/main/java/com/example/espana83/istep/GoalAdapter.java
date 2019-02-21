package com.example.espana83.istep;

/**
 * Created by Lance on 26/04/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GoalAdapter extends ArrayAdapter<Goal>{

    Context context;
    int layoutResourceId;
    ArrayList<Goal> data = null;
    private static List<String> usedPositions  = new ArrayList<String>();

    public GoalAdapter(Context context, int layoutResourceId, ArrayList<Goal> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getView " + position + " " + convertView);
        View row = convertView;
        GoalHolder holder = null;
        Goal g = getItem(position);
        if (!usedPositions.contains(position))//works the same without thisw
        {
            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new GoalHolder();
                holder.goalName = (TextView)row.findViewById(R.id.name);
                holder.goalType = (TextView)row.findViewById(R.id.type);
                holder.goalTarget = (TextView)row.findViewById(R.id.target);
                holder.goalDays = (TextView)row.findViewById(R.id.days);
                holder.bar = (ProgressBar)row.findViewById(R.id.indGoalProgressBar);


                row.setTag(holder);
            }
            else
            {
                holder = (GoalHolder)row.getTag();
            }
            holder.goalName.setText(g.getName());
            holder.goalType.setText(g.getType());
            holder.goalTarget.setText("Target: " + g.getTarget());
            holder.goalDays.setText("Days: " + g.getDays());
            holder.bar.setMax((int)Math.round(g.getTarget()));
            holder.bar.setProgress((int)Math.round(g.getProgress()));
                usedPositions.add(String.valueOf(position)); // holds the used position
        }
        else
        {
            usedPositions.remove(String.valueOf(position));
        }
        return row;
    }

    static class GoalHolder
    {
        TextView goalName;
        TextView goalType;
        TextView goalTarget;
        TextView goalDays;
        ProgressBar bar;
    }
}