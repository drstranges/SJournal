package com.drprog.sjournal.dialogs.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.drprog.sjournal.db.entity.StudyClassType;
import com.drprog.sjournal.R;

import java.util.List;

/**
 * Created by Romka on 28.07.2014.
 */
public class ClassTypeChoiceAdapter extends BaseChoiceAdapter {
    public ClassTypeChoiceAdapter(Context context, List<StudyClassType> itemList,
            boolean isMoreItem,
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
        TextView summary;

        @Override
        public void initFields(View v) {
            this.title = (TextView) v.findViewById(R.id.titleView);
            this.summary = (TextView) v.findViewById(R.id.summaryView);
            this.summary.setMaxLines(2);
        }

        @Override
        public void setFields(int position) {
            this.title.setText(((StudyClassType) itemList.get(position)).getAbbr());
            this.summary.setText(((StudyClassType) itemList.get(position)).getTitle());
        }
    }
}
