package com.example.espana83.istep;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance on 05/05/2017.
 */

public class LeaderboardAdapter extends ArrayAdapter<AccountInfo> {

    Context context;
    int layoutResourceId;
    ArrayList<AccountInfo> data = null;
    private static List<String> usedPositions = new ArrayList<String>();

    public LeaderboardAdapter(Context context, int layoutResourceId, ArrayList<AccountInfo> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getView " + position + " " + convertView);
        View row = convertView;
        AccountHolder holder = null;
        AccountInfo a = getItem(position);
        if (!usedPositions.contains(position))//works the same without thisw
        {
            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new AccountHolder();
                holder.rank = (TextView) row.findViewById(R.id.leaderboardRowRank);
                holder.username = (TextView) row.findViewById(R.id.leaderboardRowUsername);
                holder.steps = (TextView) row.findViewById(R.id.leaderboardRowSteps);

                row.setTag(holder);
            } else {
                holder = (AccountHolder) row.getTag();
            }
            holder.rank.setText("" + (position+1));
            holder.username.setText(a.getUserID());
            holder.steps.setText("" + a.getTodaysSteps());
            usedPositions.add(String.valueOf(position)); // holds the used position
        } else {
            usedPositions.remove(String.valueOf(position));
        }
        return row;
    }

    static class AccountHolder {
        TextView rank;
        TextView username;
        TextView steps;
    }
}