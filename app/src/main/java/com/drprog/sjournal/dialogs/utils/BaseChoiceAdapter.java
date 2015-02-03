package com.drprog.sjournal.dialogs.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.drprog.sjournal.db.utils.BaseJournalEntity;

import java.util.List;

/**
 * Created by Romka on 28.07.2014.
 */
public abstract class BaseChoiceAdapter extends BaseAdapter {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_MORE = -1;
    public static final int TYPE_ADD = -2;
    protected List itemList;
    protected final Context mainContext;
    protected final LayoutInflater layoutInflater;
    protected Integer checkedPosition;
    //protected boolean isMoreItem = false;
    //protected boolean isAddItem = true;
    protected final Integer itemMoreRes;
    protected final Integer itemAddRes;
    protected int itemRes = -1;


    public BaseChoiceAdapter(Context context, int itemRes, List itemList, Integer itemMoreRes,
            Integer itemAddRes)
    //boolean isMoreItem,
    //boolean isAddItem)
    {
        this.mainContext = context;
        this.itemList = itemList;
        this.itemMoreRes = itemMoreRes;
        this.itemAddRes = itemAddRes;
        this.itemRes = itemRes;
        layoutInflater = LayoutInflater.from(context);
    }

    public List getItemList() {
        return itemList;
    }

    public void setCheckedPosition(Integer checkedPosition) {
        this.checkedPosition = checkedPosition;
    }

    @Override
    public int getCount() {
        return itemList.size() + (itemMoreRes != null ? 1 : 0) + (itemAddRes != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < itemList.size()) {
            return TYPE_ITEM;
        } else if (position == itemList.size()) {
            return itemMoreRes != null ? TYPE_MORE : TYPE_ADD;
        } else { return TYPE_ADD; }
    }

    @Override
    public int getViewTypeCount() {
        return 1 + (itemMoreRes != null ? 1 : 0) + (itemAddRes != null ? 1 : 0);
    }

    @Override
    public Object getItem(int position) {
        if (position < itemList.size()) { return itemList.get(position); } else { return null; }
    }

    @Override
    public long getItemId(int position) {
        if (position < itemList.size()) {
            if (itemList.get(position) instanceof BaseJournalEntity) {
                return ((BaseJournalEntity) itemList.get(position)).getId();
            } else { return TYPE_ITEM; }
        } else if (
                position == itemList.size()) {
            return itemMoreRes != null ? TYPE_MORE : TYPE_ADD;
        } else { return TYPE_ADD; }

    }

    protected abstract ViewHolder createViewHolder();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int type = getItemViewType(position);
        if (type == TYPE_ITEM) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(itemRes, null);
                holder = createViewHolder();
                holder.initFields(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (holder != null) holder.setFields(position);
        } else if (type == TYPE_MORE) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(itemMoreRes, null);
                initMoreView(convertView);
            }


        } else if (type == TYPE_ADD) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(itemAddRes, null);
                initAddView(convertView);
            }

        }
        if (checkedPosition != null &&
                (checkedPosition == position || (type < 0 && type == checkedPosition))) {
            convertView.setBackgroundColor(Color.RED);
        }

//        // ------- Wrap in a new ItemContainer ----------------------
//        if (! (convertView instanceof ItemContainer)){
//            ViewGroup container = new ItemContainer(layoutInflater.getContext());
//            container.setTag(convertView.getTag());
//            convertView.setTag(null);
//            container.addView(convertView);
//            convertView = container;
//        }

        return convertView;
    }

    protected void initAddView(View convertView) {
        //
    }

    protected void initMoreView(View convertView) {
        //
    }

    public interface ViewHolder {
        public void initFields(View v);

        public void setFields(int position);
    }


//
//    private static class ItemContainer extends RelativeLayout {
//       MeasureListener measureListener;
//        public ItemContainer(Context context) {
//            super(context);
//        }
//
//        public ItemContainer(Context context, AttributeSet attrs) {
//            super(context, attrs);
//        }
//
//        public ItemContainer(Context context, AttributeSet attrs, int defStyle) {
//            super(context, attrs, defStyle);
//        }
//
//
//        @Override
//        protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
//            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        }
//
//        @Override
//        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            if (measureListener != null) {
//                measureListener.onClientMeasured(this, getMeasuredWidth(), getMeasuredHeight());
//            }
////            if (viewsInRow == null) return;
////
////            int measuredHeight = getMeasuredHeight();
////            int maxHeight = measuredHeight;
////            for(View v: viewsInRow){
////                if (v != null){
////                    maxHeight = Math.max(maxHeight, v.getMeasuredHeight());
////                }
////            }
////            if (maxHeight == measuredHeight) return;
////            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
////            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
////            switch (heightMode){
////                case MeasureSpec.AT_MOST:
////                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(maxHeight, heightSize), MeasureSpec.EXACTLY);
////                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
////                    break;
////                case MeasureSpec.EXACTLY:
////                    //
////                    break;
////                case MeasureSpec.UNSPECIFIED:
////                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
////                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
////                    break;
////            }
//        }
//
//        @Override
//        public void setMeasureListener(MeasureListener measureListener) {
//            this.measureListener = measureListener;
//        }
//
//        @Override
//        public MeasureListener getMeasureListener() {
//            return measureListener;
//        }
//    }


}
