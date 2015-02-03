package com.drprog.sjournal.db.prefs;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Romka on 02.02.14.
 */
public class Dimensions implements Parcelable {
    public static final Parcelable.Creator<Dimensions> CREATOR =
            new Parcelable.Creator<Dimensions>() {
                public Dimensions createFromParcel(Parcel source) {return new Dimensions(source);}

                public Dimensions[] newArray(int size) {return new Dimensions[size];}
            };
    private Context mainContext;
    //StateListDrawable states;
    private DisplayMetrics displayMetrics;
    private String profileName;                                                 //0
    private Integer key;                                                        //1
    private Integer textSize;                                                   //2
    private Integer textColor;                                                  //3
    private Integer textStyle;                                                  //4
    private Integer bgColor;                                                    //5
    private Integer viewWidth;                                                  //6
    private Integer gravity = Gravity.CENTER;                                   //7
    private Integer layoutWidth = LinearLayout.LayoutParams.WRAP_CONTENT;       //8
    private Integer layoutHeight = LinearLayout.LayoutParams.WRAP_CONTENT;      //9
    private int marginsLeft = 0;                                                //10
    private int marginsTop = 0;                                                 //11
    private int marginsRight = 0;                                               //12
    private int marginsBottom = 0;                                              //13
    private int paddingLeft = 0;                                                //14
    private int paddingTop = 0;                                                 //15
    private int paddingRight = 0;                                               //16
    private int paddingBottom = 0;                                              //17
    //private boolean singleTextLine = false;                                   //18
    private Integer maxLines = null;                                            //18

    public Dimensions(Context mainContext) {
        this.mainContext = mainContext;
        displayMetrics = mainContext.getResources().getDisplayMetrics();
    }

    public Dimensions(Dimensions copyDims) {
        copy(copyDims);
    }

    private Dimensions(Parcel in) {
        if (in.readByte() != 0) { this.profileName = in.readString(); }
        if (in.readByte() != 0) {
            this.key = (Integer) in.readValue(Integer.class.getClassLoader());
        }
        if (in.readByte() != 0) {
            this.textSize = (Integer) in.readValue(Integer.class.getClassLoader());
        }
        if (in.readByte() != 0) {
            this.textColor = (Integer) in.readValue(Integer.class.getClassLoader());
        }
        if (in.readByte() != 0) {
            this.textStyle = (Integer) in.readValue(Integer.class.getClassLoader());
        }
        if (in.readByte() != 0) {
            this.bgColor = (Integer) in.readValue(Integer.class.getClassLoader());
        }
        if (in.readByte() != 0) {
            this.viewWidth = (Integer) in.readValue(Integer.class.getClassLoader());
        }
        if (in.readByte() != 0) {
            this.gravity = (Integer) in.readValue(Integer.class.getClassLoader());
        }
        if (in.readByte() != 0) {
            this.layoutWidth = (Integer) in.readValue(Integer.class.getClassLoader());
        }
        if (in.readByte() != 0) {
            this.layoutHeight = (Integer) in.readValue(Integer.class.getClassLoader());
        }
        if (in.readByte() != 0) {
            this.maxLines = (Integer) in.readValue(Integer.class.getClassLoader());
        }

        this.marginsLeft = in.readInt();
        this.marginsTop = in.readInt();
        this.marginsRight = in.readInt();
        this.marginsBottom = in.readInt();
        this.paddingLeft = in.readInt();
        this.paddingTop = in.readInt();
        this.paddingRight = in.readInt();
        this.paddingBottom = in.readInt();
        //this.singleTextLine = in.readByte() != 0;
    }

    public static int dipToPixels(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getTextSize() {
        return textSize;
    }

    public void setTextSize(Integer textSize) {
        this.textSize = textSize;
    }

    public Integer getTextColor() {
        return textColor;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
    }

    public Integer getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(Integer textStyle) {
        this.textStyle = textStyle;
    }

//    public boolean isSingleTextLine() {
//        return singleTextLine;
//    }
//
//    public void setSingleTextLine(boolean singleTextLine) {
//        this.singleTextLine = singleTextLine;
//    }


    public Integer getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(Integer maxLines) {
        this.maxLines = maxLines;
    }

    public Integer getBgColor() {
        return bgColor;
    }

    public void setBgColor(Integer bgColor) {
        this.bgColor = bgColor;
//        if (bgColor != null)
//            getStateListDrawable(bgColor);
    }

    public Integer getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(Integer viewWidth) {
        this.viewWidth = viewWidth;
    }

    public Integer getGravity() {
        return gravity;
    }

    public void setGravity(Integer gravity) {
        this.gravity = gravity;
    }

    public Integer getLayoutWidth() {
        return layoutWidth;
    }

    public Integer getLayoutHeight() {
        return layoutHeight;
    }

    public int getMarginsLeft() {
        return marginsLeft;
    }

    public int getMarginsTop() {
        return marginsTop;
    }

    public int getMarginsRight() {
        return marginsRight;
    }

    public int getMarginsBottom() {
        return marginsBottom;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

//    public Integer getProfileId() {
//        return profileId;
//    }
//
//    public void setProfileId(Integer profileId) {
//        this.profileId = profileId;
//    }

//    private void getStateListDrawable(int bgColor){
//        GradientDrawable gd_normal = new GradientDrawable();
//        gd_normal.setColor(bgColor);
//        states = new StateListDrawable();
//        states.addState(new int[]{android.R.attr.state_pressed},
//                        mainContext.getResources().getDrawable(R.drawable.btn_choice_bg_pressed));
//        states.addState(new int[]{},
//                        gd_normal);
//
//    }

    public Dimensions setLayout(Integer layoutWidth, Integer layoutHeight) {
        this.layoutWidth = layoutWidth;
        this.layoutHeight = layoutHeight;
        return this;
    }

    public Dimensions setMargins(int marginsLeft, int marginsTop, int marginsRight,
            int marginsBottom) {
        this.marginsLeft = marginsLeft;
        this.marginsTop = marginsTop;
        this.marginsRight = marginsRight;
        this.marginsBottom = marginsBottom;
        return this;
    }

    public Dimensions setPadding(int paddingLeft, int paddingTop, int paddingRight,
            int paddingBottom) {
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;
        return this;
    }

    public Dimensions copy(Dimensions copy) {
        if (copy != null) {
            if (copy.mainContext != null) {
                this.mainContext = copy.mainContext;
                this.displayMetrics = mainContext.getResources().getDisplayMetrics();
            }

            this.profileName = copy.profileName;                                //0
            this.key = copy.key;                                                //1
            this.textSize = copy.textSize;                                      //2
            this.textColor = copy.textColor;                                    //3
            this.textStyle = copy.textStyle;                                    //4
            this.bgColor = copy.bgColor;                                        //5
            this.viewWidth = copy.viewWidth;                                    //6
            this.gravity = copy.gravity;                                        //7
            this.layoutWidth = copy.layoutWidth;                                //8
            this.layoutHeight = copy.layoutHeight;                              //9
            this.marginsLeft = copy.marginsLeft;                                //10
            this.marginsTop = copy.marginsTop;                                  //11
            this.marginsRight = copy.marginsRight;                              //12
            this.marginsBottom = copy.marginsBottom;                            //13
            this.paddingLeft = copy.paddingLeft;                                //14
            this.paddingTop = copy.paddingTop;                                  //15
            this.paddingRight = copy.paddingRight;                              //16
            this.paddingBottom = copy.paddingBottom;                            //17
            //this.singleTextLine = copy.singleTextLine;                          //18
            this.maxLines = copy.maxLines;
//            if(bgColor != null)
//                getStateListDrawable(bgColor);
        }
        return this;
    }

    public int dipToPixels(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public Dimensions setDimensionInDip(Integer viewWidthDip,                   //6
            int marginsLeftDip,                     //10
            int marginsTopDip,                      //11
            int marginsRightDip,                    //12
            int marginsBottomDip) {                 //13

        if (viewWidthDip != null) {
            this.viewWidth = dipToPixels(viewWidthDip);
        } else {
            this.viewWidth = null;
        }
        this.marginsLeft = dipToPixels(marginsLeftDip);
        this.marginsTop = dipToPixels(marginsTopDip);
        this.marginsRight = dipToPixels(marginsRightDip);
        this.marginsBottom = dipToPixels(marginsBottomDip);
        return this;
    }

    public void setStyle(TextView v) {
        if (textSize != null) v.setTextSize(textSize);                                     //2
        if (textColor != null) v.setTextColor(textColor);                                  //3
        if (textStyle != null) v.setTypeface(null, textStyle);                             //4

//        if (bgColor != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                v.setBackground(states);
//            }else v.setBackgroundDrawable(states);
//            v.setClickable(true);
//        }

        if (bgColor != null) v.setBackgroundColor(bgColor);                                //5
        if (viewWidth != null) v.setWidth(viewWidth);                                      //6
        if (gravity != null) v.setGravity(gravity);                                        //7
        LinearLayout.LayoutParams layoutParams;                                            //8-13
        if ((layoutWidth != null) && (layoutHeight != null)) {
            layoutParams = new LinearLayout.LayoutParams(layoutWidth, layoutHeight);
        } else {
            layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
        }
        if (layoutParams != null) {
            if ((marginsLeft >= 0) && (marginsTop >= 0) && (marginsRight >= 0)
                    && (marginsBottom >= 0)) {
                layoutParams.setMargins(marginsLeft, marginsTop, marginsRight, marginsBottom);
            }
            v.setLayoutParams(layoutParams);
        }
        if ((paddingLeft >= 0) && (paddingTop >= 0) && (paddingRight >= 0)                  //14-17
                && (paddingBottom >= 0)) {
            v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        if (maxLines != null){
            v.setEllipsize(TextUtils.TruncateAt.END);
            v.setMaxLines(1);
            //v.setPadding(0,7,7,0);
            v.setIncludeFontPadding(false);

            //DebugUtils.testMaxLines(v);
            //v.setMaxLines(1);
            //v.setLineSpacing(v.getLineSpacingExtra(),v.getLineSpacingMultiplier());
            //v.getExtendedPaddingBottom()
            //v.setSingleLine(true);

            //v.setMaxHeight(RunUtils.measureTextHeight(v));

            //v.setSingleLine(true);
            //v.setHorizontallyScrolling(false);
            //v.setHorizontallyScrolling(true);
            //v.computeScroll();
           //v.setScrollX(v.getTotalPaddingLeft());
        }
        //v.setSingleLine(singleTextLine);                                                    //18
        v.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        Dimensions that = (Dimensions) o;

        return !(key != null ? !key.equals(that.key) : that.key != null);

    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (this.profileName != null) {
            dest.writeByte((byte) 1);
            dest.writeString(this.profileName);
        } else { dest.writeByte((byte) 0); }
        if (this.key != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.key);
        } else { dest.writeByte((byte) 0); }
        if (this.textSize != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.textSize);
        } else { dest.writeByte((byte) 0); }
        if (this.textColor != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.textColor);
        } else { dest.writeByte((byte) 0); }
        if (this.textStyle != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.textStyle);
        } else { dest.writeByte((byte) 0); }
        if (this.bgColor != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.bgColor);
        } else { dest.writeByte((byte) 0); }
        if (this.viewWidth != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.viewWidth);
        } else { dest.writeByte((byte) 0); }
        if (this.gravity != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.gravity);
        } else { dest.writeByte((byte) 0); }
        if (this.layoutWidth != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.layoutWidth);
        } else { dest.writeByte((byte) 0); }
        if (this.layoutHeight != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.layoutHeight);
        } else { dest.writeByte((byte) 0); }
        if (this.maxLines != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.maxLines);
        } else { dest.writeByte((byte) 0); }
        dest.writeInt(this.marginsLeft);
        dest.writeInt(this.marginsTop);
        dest.writeInt(this.marginsRight);
        dest.writeInt(this.marginsBottom);
        dest.writeInt(this.paddingLeft);
        dest.writeInt(this.paddingTop);
        dest.writeInt(this.paddingRight);
        dest.writeInt(this.paddingBottom);
        //dest.writeByte(singleTextLine ? (byte) 1 : (byte) 0);
    }

    public Context getMainContext() {
        return mainContext;
    }

    public void setMainContext(Context mainContext) {
        if (mainContext != null) {
            this.mainContext = mainContext;
            this.displayMetrics = mainContext.getResources().getDisplayMetrics();
        }
    }
}
