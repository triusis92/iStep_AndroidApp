package com.example.espana83.istep;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GroupFragment extends Fragment {

    private View view;
    private TextView title;
    private LeaderboardAdapter adapter;
    private ListView leaderboardListView;
    private ArrayList<AccountInfo> accounts = new ArrayList<>();
    private DynamoDBMapper mapper = LoginActivity.getMapper();
    private List<AccountInfo> result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_group, container, false);
        title = (TextView) view.findViewById(R.id.leaderboardTitle);
        leaderboardListView = (ListView)view.findViewById(R.id.leaderboardListView);
        try{
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            result = mapper.scan(AccountInfo.class, scanExpression);
            accounts.clear();
            for(int i = 0; i < result.size(); i++)
            {
                accounts.add(result.get(i));
            }
            Collections.sort(accounts);
        }catch(Exception e){

        }
        adapter = new LeaderboardAdapter(view.getContext(), R.layout.leaderboard_row, accounts);//populate adapter listview with shtuff
        leaderboardListView.setAdapter(adapter);//display the listview
        return view;
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