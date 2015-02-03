package com.drprog.sjournal.dialogs.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.drprog.sjournal.db.entity.StudySubject;
import com.drprog.sjournal.db.prefs.Dimensions;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.RunUtils;

import java.util.List;

/**
 * Created by Romka on 17.07.2014.
 */
public class SubjectChoiceAdapter extends BaseChoiceAdapter {

    public SubjectChoiceAdapter(Context context, List<StudySubject> itemList, boolean isMoreItem,
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
        int defTextSize = 0;

        @Override
        public void initFields(View v) {
            this.title = (TextView) v.findViewById(R.id.titleView);
            this.summary = (TextView) v.findViewById(R.id.summaryView);
            //this.summary.setMaxLines(2);
            if (defTextSize <= 0) defTextSize = (int) this.summary.getTextSize();

        }

        @Override
        public void setFields(int position) {
            this.title.setText(((StudySubject) itemList.get(position)).getAbbr());
            this.summary.setText(((StudySubject) itemList.get(position)).getTitle());

            int maxHeight = Dimensions.dipToPixels(mainContext, 25);
            int maxWidth = Dimensions.dipToPixels(mainContext, 182);
            this.summary.setTextSize(defTextSize);
            RunUtils.placeFullTextS(this.summary, null, maxWidth, maxHeight);

        }
    }


}
