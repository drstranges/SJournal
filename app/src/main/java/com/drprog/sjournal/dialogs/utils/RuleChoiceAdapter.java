package com.drprog.sjournal.dialogs.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.drprog.sjournal.db.entity.Rule;

import java.util.List;

/**
 * Created by Romka on 19.08.2014.
 */
public class RuleChoiceAdapter extends BaseChoiceAdapter {
    public RuleChoiceAdapter(Context context, int itemRes, List<Rule> itemList,
            Integer itemMoreRes, Integer itemAddRes) {
        super(context, itemRes, itemList, itemMoreRes, itemAddRes);
    }

    @Override
    protected ViewHolder createViewHolder() {
        return new RuleViewHolder();
    }

    class RuleViewHolder implements ViewHolder {
        TextView title;

        @Override
        public void initFields(View v) {
            title = (TextView) v.findViewById(android.R.id.text1);
        }

        @Override
        public void setFields(int position) {
            title.setText(itemList.get(position).toString());
        }
    }
}
