package com.drprog.sjournal.syncscroll;


import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class ScrollManager implements ScrollListener {

    public static final int SCROLL_HORIZONTAL = 1;
    public static final int SCROLL_VERTICAL = 2;
    public static final int SCROLL_BOTH = 3;
    private int scrollDirection = SCROLL_BOTH;
    private final List<ScrollNotifier> clients = new ArrayList<ScrollNotifier>();
    private volatile boolean isSyncing = false;

    public static void disallowParentScroll(View childScroll, int rightMargin) {
        final int _rightMargin = rightMargin;
        childScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getX() > v.getWidth() - _rightMargin) return false;
                    //requestDisallowParentInterceptTouchEvent(childScroll, true);
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //requestDisallowParentInterceptTouchEvent(childScroll, false);
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
    }

    public void setScrollDirection(int scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    public void addScrollClient(ScrollNotifier client) {
        clients.add(client);
        client.setScrollListener(this);
    }

    public void clear() {
        clients.clear();
    }

    @Override
    public void onScrollChanged(View sender, int l, int t, int oldl, int oldt) {
        // avoid notifications while scroll bars are being synchronized
        if (isSyncing) return;
        isSyncing = true;

        if (l != oldl) {
            if (scrollDirection == SCROLL_VERTICAL) {
                isSyncing = false;
                return;
            }
        } else if (t != oldt) {
            if (scrollDirection == SCROLL_HORIZONTAL) {
                isSyncing = false;
                return;
            }
        } else {
            isSyncing = false;
            return;
        }


        // update clients
        for (ScrollNotifier client : clients) {
            View view = (View) client;
            // don't update sender
            if (view == sender) { continue; }

            // scroll relevant views
            if (((l != oldl) && (view instanceof HorizontalScrollView))
                    || ((t != oldt) && (view instanceof ScrollView))
                    || ((t != oldt) && (view instanceof ListView))) { view.scrollTo(l, t); }
        }

        isSyncing = false;
    }

//    public static void disallowChildScroll(final View childScroll, View parentScroll, int bottomMargin) {
//        final int _bottomMargin = bottomMargin;
//        //final int _childScrollId = childScrollId;
//        parentScroll.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (event.getY() > v.getHeight() - _bottomMargin) return false;
//                    //requestDisallowParentInterceptTouchEvent(parentScroll, true);
//                    childScroll.getParent().requestDisallowInterceptTouchEvent(true);
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    //requestDisallowParentInterceptTouchEvent(parentScroll, false);
//                    childScroll.getParent().requestDisallowInterceptTouchEvent(false);
//                }
//                return false;
//            }
//        });
//    }

//    private static void requestDisallowParentInterceptTouchEvent(View v, boolean disallow) {
//        while (v.getParent() != null && v.getParent() instanceof View) {
//            if (v.getParent() instanceof ScrollView) {
//                v.getParent().requestDisallowInterceptTouchEvent(disallow);
//            }
//            v = (View) v.getParent();
//        }
//    }
}
