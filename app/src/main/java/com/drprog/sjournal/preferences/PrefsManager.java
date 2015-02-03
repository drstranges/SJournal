package com.drprog.sjournal.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Romka on 15.07.2014.
 */
public class PrefsManager {


    //private static final String PREFERENCES_NAME = "AppPreferences";
    //==============================================================================================
    //Background Prefs
    public static final String PREFS_LAST_SELECTED_GROUP_ID = "PREFS_LAST_SELECTED_GROUP_ID";
    public static final String PREFS_LAST_SELECTED_SUBJECT_ID = "PREFS_LAST_SELECTED_SUBJECT_ID";
    public static final String PREFS_LAST_SELECTED_CLASS_TYPE_ID =
            "PREFS_LAST_SELECTED_CLASS_TYPE_ID";
    public static final String PREFS_TOP_CHOICE_PANEL_VISIBILITY =
            "PREFS_TOP_CHOICE_PANEL_VISIBILITY";
//    public static final String PREFS_MARK_SCALE_SET = "PREFS_MARK_SCALE_SET";

    //==============================================================================================
    //Global Preferences
//    public static final String PREFS_MARK_SCALE_USER = "PREFS_MARK_SCALE_USER";
//    // true: user
//    // false: predefined
//    public static final String PREFS_MARK_SCALE = "PREFS_MARK_SCALE";
//    // 0: 5 {2-5};
//    // 1: 12 {1-12}
//    // 2: dinamic scale
//    // 3: user scale

    public static final String PREFS_MANUAL_STUDENT_ID = "PREFS_MANUAL_STUDENT_ID";
    // true: Manual
    // false: Auto

    public static final String PREFS_CALENDAR_SYNC = "PREFS_CALENDAR_SYNC";
    // true: Sync with Calendar
    // false: No Sync
    public static final String PREFS_CALENDAR_SYNC_WINDOW = "PREFS_CALENDAR_SYNC_WINDOW";
    // true: Look for 5 minutes after and before the current time.
    // false: No window


    //==============================================================================================
    // Blank Preferences
    public static final String PREFS_MARK_SCALE_USER_SET = "PREFS_MARK_SCALE_USER_SET";
    // Marks in gridView in MarkAUDDialog
    public static final String PREFS_SYMBOL_USER_SET = "PREFS_SYMBOL_USER_SET";
    // Symbols in gridView in MarkAUDDialog

    public static final String PREFS_BLANK_CLASS_STYLE = "PREFS_BLANK_CLASS_STYLE";
    // 0: Abbr & date
    // 1: Abbr
    // 2: Date
    public static final String PREFS_BLANK_NAME_FORMAT = "PREFS_BLANK_NAME_FORMAT";
    // 0: LastName F. M.
    // 1: LastName FirstName MiddleName
    // 2: LastName FirstName M.
    // 3: LastName FirstName
    // 4: LastName F.
    public static final String PREFS_BLANK_ABSENT_SYMBOL = "PREFS_BLANK_ABSENT_SYMBOL";
    //Char
    public static final String PREFS_BLANK_SYMBOL_FORMAT = "PREFS_BLANK_SYMBOL_FORMAT";
    // 0: Ellipsize
    // 1: Full Text (Decrease Font Size)
    // 2: Full Text (Increase Column Width)

    //Edit Class by Click
    public static final String PREFS_BLANK_EDIT_CLASS_ON_CLICK = "PREFS_BLANK_EDIT_CLASS_ON_CLICK";
    //true/false

    //==============================================================================================
    // Blank of Results Preferences
    public static final String PREFS_BLANK_SUMMARY_SHOW_COLS_AVG = "PREFS_BLANK_SUMMARY_SHOW_COLS_AVG";
    //true
    //false





    private static PrefsManager ourInstance = null;
    private final SharedPreferences sPrefs;

    private PrefsManager(Context context) {
        //sPrefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized PrefsManager getInstance(Context context) {
        if (ourInstance == null) {
            assert context != null;
            ourInstance = new PrefsManager(context);
        }
        return ourInstance;
    }

    public static void clearBackgroundPrefs(Context context) {
        SharedPreferences.Editor editor = getInstance(context).getPrefs().edit();
        editor.remove(PREFS_LAST_SELECTED_GROUP_ID);
        editor.remove(PREFS_LAST_SELECTED_SUBJECT_ID);
        editor.remove(PREFS_LAST_SELECTED_CLASS_TYPE_ID);
        editor.apply();
    }

    public SharedPreferences getPrefs() {
        return sPrefs;
    }


}
