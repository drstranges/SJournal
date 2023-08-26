package com.drprog.sjournal.dialogs.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.RunUtils;

/**
 * Created by Romka on 28.07.2014.
 */
public class ColorAdapter extends ArrayAdapter {
    protected String[] colorNameList;
    protected int[] colorList;
    protected Context mainContext;
    protected LayoutInflater layoutInflater;
    protected int resource;
    protected int textViewResourceId;
    private int dropDownViewResource;

    public ColorAdapter(Context context, int resource) {
        super(context, resource);
        init(context, resource, android.R.id.text1);
    }

    public ColorAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        init(context, resource, textViewResourceId);
    }

    protected void init(Context context, int resource, int textViewResourceId) {
        this.mainContext = context;
        layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
        this.dropDownViewResource = resource;
        this.textViewResourceId = textViewResourceId;
        colorList = context.getResources().getIntArray(R.array.colors_value);
        colorNameList = context.getResources().getStringArray(R.array.colors_name);
    }

    @Override
    public void setDropDownViewResource(int resourceDropDown) {
        super.setDropDownViewResource(resourceDropDown);
        this.dropDownViewResource = resourceDropDown;
    }

    @Override
    public int getCount() {
        return colorList.length;
    }

    @Override
    public Object getItem(int position) {
        return colorNameList[position];
    }

    @Override
    public long getItemId(int position) {
        return colorList[position];
    }

    public int getItemPos(int color) {
        for (int pos = 0; pos < colorList.length; pos++) {
            if (colorList[pos] == color) return pos;
        }
        return -1;
    }

    public int getBgColor(int position) {
        return colorList[position];
    }

    public int getTextColor(int position) {
        return RunUtils.getContrastColor(colorList[position]);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(resource,parent,false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(textViewResourceId);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(colorNameList[position]);
        holder.textView.setBackgroundColor(colorList[position]);
        holder.textView.setTextColor(RunUtils.getContrastColor(colorList[position]));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(dropDownViewResource, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(textViewResourceId);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(colorNameList[position]);
        holder.textView.setBackgroundColor(colorList[position]);
        holder.textView.setTextColor(RunUtils.getContrastColor(colorList[position]));
        return convertView;
    }

    protected static class ViewHolder {
        TextView textView;
    }
}
