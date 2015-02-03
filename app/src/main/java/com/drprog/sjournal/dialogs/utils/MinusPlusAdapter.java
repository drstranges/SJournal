package com.drprog.sjournal.dialogs.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Romka on 17.07.2014.
 */
public class MinusPlusAdapter<T> extends BaseChoiceAdapter {

    public MinusPlusAdapter(Context context, int itemRes, List<T> itemList, Integer itemMoreRes,
            Integer itemAddRes) {
        super(context, itemRes, itemList, itemMoreRes, itemAddRes);
    }

    @Override
    protected ViewHolder createViewHolder() {
        return new ViewHolder();
    }

    private class ViewHolder implements BaseChoiceAdapter.ViewHolder {
        int listPosition = -1;
        TextView title;

        @Override
        public void initFields(View v) {
            this.title = (TextView) v.findViewById(android.R.id.text1);
            ImageButton minusBtn = (ImageButton) v.findViewById(android.R.id.button1);
            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemList != null && !itemList.isEmpty() && listPosition >= 0) {
                        itemList.remove(listPosition);
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void setFields(int position) {
            this.title.setText(itemList.get(position).toString());
            this.listPosition = position;
        }
    }




}
