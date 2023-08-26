package com.drprog.sjournal.utils;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.drprog.sjournal.dialogs.VersionRestrictionDialog;
import com.drprog.sjournal.BuildConfig;

import java.util.concurrent.TimeUnit;

/**
 * Created by Romka on 20.07.2014.
 */
public class RunUtils {

    private static long nextDebouncedToastTime = 0;

    public static boolean checkIntegerMaxNum(Integer num){
        if (num == null) return true;

        return (num < Integer.MAX_VALUE && num > Integer.MIN_VALUE);
    }

    public static Integer tryParse(String text) {
        try {
            return new Integer(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static StaticLayout measure(TextPaint textPaint, String text, Integer wrapWidth) {
        int boundedWidth = Integer.MAX_VALUE;
        if (wrapWidth != null && wrapWidth > 0) {
            boundedWidth = wrapWidth;
        }
        return new StaticLayout(text, textPaint, boundedWidth, Layout.Alignment.ALIGN_CENTER, 1.0f,
                                0.0f,
                                false);
    }

    public static float getMaxLineWidth(StaticLayout layout) {
        float maxLine = 0.0f;
        int lineCount = layout.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            if (layout.getLineWidth(0) > maxLine) {
                maxLine = layout.getLineWidth(0);
            }
        }
        return maxLine;
    }

    public static boolean placeFullTextS(TextView view, String text, int maxWidth, int maxHeight) {
        int textSize = (int) view.getTextSize();
        TextPaint textPaint = view.getPaint();
        if (text == null) text = view.getText().toString();
        boolean isOk = false;
        StaticLayout sl;
        if (maxHeight == -1) maxHeight = measure(textPaint, "X", null).getHeight();
        do {
            sl = measure(textPaint, text, maxWidth);
            int height = sl.getHeight();
            if (height <= maxHeight) {
                isOk = true;
            } else {
                textSize = textSize - 1;
                if (textSize > 0) view.setTextSize(textSize);
            }
        } while ((textSize > 0) && (!isOk));
        return isOk;
    }

/*    public static boolean placeSingleLineTextS(TextView view, String text, int maxWidth) {
        int textSize = (int) view.getTextSize();
        TextPaint textPaint = view.getPaint();
        if (text == null) text = view.getText().toString();
        boolean isOk = false;
        StaticLayout sl;
        do {
            sl = measure(textPaint,text,maxWidth);
            if (sl.getWidth() < maxWidth) {
                isOk = true;
            } else {
                textSize = (int) (textSize * maxWidth / sl.getWidth() - 0.5);
                if (textSize > 0) view.setTextSize(textSize);
            }
        } while ((textSize > 0) && (!isOk));
        return isOk;
    }*/

//    public static String getSingleLineTextTrunk(TextView view, String text, Integer maxWidth) {
//        boolean isOk = false;
//        Rect bounds = new Rect();
//        Paint textPaint = view.getPaint();
//        if (text == null) text = view.getText().toString();
//        if (maxWidth == null) maxWidth = view.getWidth()+ view.getPaddingLeft()+ view.getPaddingRight();
//        do {
//            textPaint.getTextBounds(text, 0, text.length(), bounds);
//            if (bounds.width() <= maxWidth) {
//                isOk = true;
//            } else {
//                text = text.substring(0,text.length()-1);
//            }
//        } while ((text.length() > 0) && (!isOk));
//        return text;
//    }

    public static int measureTextHeight(TextView view) {

        int textSize = (int) view.getTextSize();
        Rect bounds = new Rect();
        Paint textPaint = view.getPaint();
        String text = "X";
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }
    public static int measureTextWidth(TextView view, String text) {

        int textSize = (int) view.getTextSize();
        if (text == null) text = view.getText().toString();
        Rect bounds = new Rect();
        Paint textPaint = view.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    public static boolean placeSingleLineText(TextView view, String text, int maxWidth,
            int minTextSize) {
        if (minTextSize < 0) minTextSize = 0;
        int textSize = (int) view.getTextSize();
        boolean isOk = false;
        Rect bounds = new Rect();
        Paint textPaint = view.getPaint();
        if (text == null) text = view.getText().toString();
        do {
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            if (bounds.width() < maxWidth) {
                isOk = true;
            } else {
                textSize = (int) (textSize * maxWidth / bounds.width() - 0.5);
                if (textSize < minTextSize) textSize = minTextSize;
                if (textSize > 0) view.setTextSize(textSize);
            }
        } while ((textSize > minTextSize) && (!isOk));
        return isOk;
    }

    public static void showToast(Context ctx, int resMessage, int duration) {
        Toast toast;
        toast = Toast.makeText(ctx, resMessage, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public static void showToast(Context ctx, int resMessage) {
        showToast(ctx,resMessage,Toast.LENGTH_LONG);
    }
    public static void showShortToastWithDebounce(Context ctx, int resMessage) {
        long now = System.currentTimeMillis();
        if (now > nextDebouncedToastTime) {
            if (nextDebouncedToastTime == 0) {
                // Show 2 times until debounce
                nextDebouncedToastTime++;
            } else {
                nextDebouncedToastTime = now + TimeUnit.HOURS.toMillis(1);
            }
            showToast(ctx,resMessage,Toast.LENGTH_SHORT);
        }
    }

    public static void showToast(Context ctx, String message, int duration) {
        Toast toast;
        toast = Toast.makeText(ctx, message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public static void showToast(Context ctx, String message) {
        showToast(ctx,message,Toast.LENGTH_LONG);
    }

    public static int getContrastColor(int color) {
        //Y = 0.2126 * (R/255)^2.2  +  0.7151 * (G/255)^2.2  +  0.0721 * (B/255)^2.2
        //If Y is less than or equal to 0.18, use white text. If it’s greater than 0.18, use black text.
        if (color == Color.TRANSPARENT) return Color.BLACK;
        double Y =
                0.2126 * Math.pow((Color.red(color) / 255.0), 2.2) +
                        0.7151 * Math.pow((Color.green(color) / 255.0), 2.2) +
                        0.0721 * Math.pow((Color.blue(color) / 255.0), 2.2);
        return Y <= 0.18 ? Color.WHITE : Color.BLACK;
    }

    public static AlertDialog getAlertDialog(Context context, Integer iconResId,
            Integer titleResId, Integer messageResId, boolean btnOk, boolean btnCancel,
            DialogInterface.OnClickListener okClickListener, ListAdapter listAdapter,
            DialogInterface.OnClickListener listClickListener, View view) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        if (iconResId != null) adb.setIcon(iconResId);
        if (titleResId != null) adb.setTitle(titleResId);
        if (messageResId != null) adb.setMessage(messageResId);
        if (btnOk) adb.setPositiveButton(android.R.string.ok, okClickListener);
        if (btnCancel) adb.setNegativeButton(android.R.string.cancel, null);
        if (listAdapter != null) {
            adb.setAdapter(listAdapter, listClickListener);
        }
        if (view != null) adb.setView(view);
        return adb.create();
    }

    public static boolean isNumeric(String str) {
        return str.matches("\\d+");  //match a number with optional '-' and decimal.
    }






    public static boolean showMessageIfFree(FragmentManager fm) {
            if (BuildConfig.IS_VERSION_FREE) {
                VersionRestrictionDialog.newInstance().show(fm, "dialog");
                return true;
            }

        return false;
    }

}
