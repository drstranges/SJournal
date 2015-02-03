package com.drprog.sjournal.dialogs.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.drprog.sjournal.preferences.PrefsManager;
import com.drprog.sjournal.R;

import java.util.List;

/**
 * Created by Romka on 06.08.2014.
 */
public class MarkChoiceAdapter extends BaseChoiceAdapter {
    private String itemAddText;
    private String itemMoreText;


    public MarkChoiceAdapter(Context context, List itemList, boolean isMore) {
        super(context, R.layout.item_center, itemList, isMore ? R.layout.item_center_more : null,
              R.layout.item_center_add);
        if (isMore) {
            itemMoreText = PrefsManager.getInstance(mainContext).getPrefs()
                    .getString(PrefsManager.PREFS_BLANK_ABSENT_SYMBOL,
                               mainContext.getResources().getString(R.string.symbol_absent));
        }
    }

    @Override
    protected ViewHolder createViewHolder() {
        return new ViewHolder();
    }

    public String getItemAddText() {
        return itemAddText;
    }

    public void setItemAddText(String itemAddText) {
        this.itemAddText = itemAddText;
    }

    @Override
    protected void initAddView(View convertView) {
        if (itemAddText != null) {
            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(itemAddText);
        } else {
            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(R.string.three_dots);
        }
    }

    @Override
    protected void initMoreView(View convertView) {
        ((TextView) convertView.findViewById(android.R.id.text1))
                .setText(itemMoreText);
    }

    private class ViewHolder implements BaseChoiceAdapter.ViewHolder {
        TextView title;

        @Override
        public void initFields(View v) {
            this.title = (TextView) v.findViewById(android.R.id.text1);
        }

        @Override
        public void setFields(int position) {
            this.title.setText(itemList.get(position).toString());
        }
    }

}
