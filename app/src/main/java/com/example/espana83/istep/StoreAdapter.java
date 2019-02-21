package com.example.espana83.istep;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance on 09/05/2017.
 */

public class StoreAdapter extends ArrayAdapter<StoreItem> {

    Context context;
    int layoutResourceId;
    ArrayList<StoreItem> data = null;
    private static List<String> usedPositions = new ArrayList<String>();
    StoreItem s;

    public StoreAdapter(Context context, int layoutResourceId, ArrayList<StoreItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        System.out.println("getView " + position + " " + convertView);
        View row = convertView;
        StoreAdapter.ItemHolder holder = null;
        s = getItem(position);
        String pos = Integer.toString(position);
        if (!usedPositions.contains(position))//works the same without thisw
        {
            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new StoreAdapter.ItemHolder();
                holder.name = (TextView) row.findViewById(R.id.storeRowName);
                holder.price = (TextView) row.findViewById(R.id.storeRowPrice);
                holder.description = (TextView) row.findViewById(R.id.storeRowDescription);
                holder.buy = (Button) row.findViewById(R.id.storeRowButton);
                if(s.getPurchased())
                {
                    holder.buy.setEnabled(false);
                    holder.buy.setBackgroundColor(Color.GRAY);
                }
                else
                {
                    holder.buy.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            StoreFragment.doSomething(getContext(), position, s.getRecipe());
                        }
                    });
                }

                row.setTag(holder);
            } else {
                holder = (StoreAdapter.ItemHolder) row.getTag();
            }
            holder.name.setText(s.getName());
            holder.price.setText(s.getPrice() + " Steps");
            holder.description.setText(s.getDescription());
            usedPositions.add(String.valueOf(position)); // holds the used position
        } else {
            usedPositions.remove(String.valueOf(position));
        }
        return row;
    }

    static class ItemHolder {
        TextView name;
        TextView price;
        TextView description;
        Button buy;
    }
}
