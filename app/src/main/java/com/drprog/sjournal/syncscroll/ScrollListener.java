package com.drprog.sjournal.syncscroll;

import android.view.View;

public interface ScrollListener {
    void onScrollChanged(View syncedScrollView, int l, int t, int oldl, int oldt);

}
