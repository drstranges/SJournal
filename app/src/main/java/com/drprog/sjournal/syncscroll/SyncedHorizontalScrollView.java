package com.drprog.sjournal.syncscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;


/**
 * Created by Romka on 16.08.13.
 */
public class SyncedHorizontalScrollView extends HorizontalScrollView implements ScrollNotifier {


    private ScrollListener scrollListener = null;

    public SyncedHorizontalScrollView(Context context) {
        super(context);
    }

    public SyncedHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SyncedHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollListener != null) { scrollListener.onScrollChanged(this, l, t, oldl, oldt); }
    }

    @Override
    public ScrollListener getScrollListener() {
        return scrollListener;
    }

    @Override
    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

}
