package com.drprog.sjournal.dialogs.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.R;

import java.util.List;

/**
 * Created by Romka on 17.07.2014.
 */
public class GroupChoiceAdapter extends BaseChoiceAdapter {


    public GroupChoiceAdapter(Context context, List<StudyGroup> itemList, boolean isMoreItem,
            boolean isAddItem) {
        super(context, R.layout.item_choice, itemList,
              isMoreItem ? R.layout.item_choice_more : null,
              isAddItem ? R.layout.item_choice_add : null);
    }

    @Override
    protected ViewHolder createViewHolder() {
        return new ViewHolder();
    }

    private class ViewHolder implements BaseChoiceAdapter.ViewHolder {
        TextView title;

        @Override
        public void initFields(View v) {
            this.title = (TextView) v.findViewById(R.id.titleView);
            TextView summaryView = (TextView) v.findViewById(R.id.summaryView);
            summaryView.setVisibility(View.GONE);
        }

        @Override
        public void setFields(int position) {
            this.title.setText(((StudyGroup) itemList.get(position)).getCode());
        }
    }

}
