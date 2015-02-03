package com.drprog.sjournal.db.prefs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drprog.sjournal.db.utils.DataCheck;
import com.drprog.sjournal.db.utils.SQLBaseTable;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Romka on 02.02.14.
 */
public class TableDimensions extends SQLBaseTable<Dimensions> {
    //public static final int PROFILE_DEFAULT = 0;
    public static final String STR_DEFAULT = "Default";
    public static final String STR_USER_PROFILE = "UserProf_";

    public static final String TABLE_NAME = "Dimensions";
    public static final String TABLE_NAME_UTILS = "Utils";
    //public static final String KEY_ID = "_id";
    public static final String KEY_PROFILE_NAME = "ProfileName";
    public static final String KEY_ENTRY_KEY = "EntryKey";
    public static final String KEY_TEXT_SIZE = "TextSize";
    public static final String KEY_TEXT_STYLE = "TextStyle";
    public static final String KEY_TEXT_COLOR = "TextColor";
    public static final String KEY_TEXT_GRAVITY = "TextGravity";
    //public static final String KEY_TEXT_SINGLE_LINE = "TextSingleLine";
    public static final String KEY_TEXT_MAX_LINES = "TextMaxLines";
    public static final String KEY_TEXT_PADDING_LEFT = "TextPaddingLeft";
    public static final String KEY_TEXT_PADDING_TOP = "TextPaddingTop";
    public static final String KEY_TEXT_PADDING_RIGHT = "TextPaddingRight";
    public static final String KEY_TEXT_PADDING_BOTTOM = "TextPaddingBottom";
    public static final String KEY_MARGINS_LEFT = "MarginsLeft";
    public static final String KEY_MARGINS_TOP = "MarginsTop";
    public static final String KEY_MARGINS_RIGHT = "MarginsRight";
    public static final String KEY_MARGINS_BOTTOM = "MarginsBottom";
    public static final String KEY_BACKGROUND_COLOR = "BackgroundColor";
    public static final String KEY_WIDTH = "Width";
    public static final String KEY_LAYOUT_HEIGHT = "LayoutHeight";
    public static final String KEY_LAYOUT_WIDTH = "LayoutWidth";

    protected static final String SCRIPT_CREATE_MAIN_TABLE = "create table " + TABLE_NAME + " ( " +
            //KEY_ID + " integer primary key autoincrement, " +
            KEY_PROFILE_NAME + " text not null, " +
            KEY_ENTRY_KEY + " integer not null, " +
            KEY_BACKGROUND_COLOR + " integer, " +
            KEY_WIDTH + " integer, " +
            KEY_LAYOUT_WIDTH + " integer, " +
            KEY_LAYOUT_HEIGHT + " integer, " +
            KEY_TEXT_SIZE + " integer, " +
            KEY_TEXT_STYLE + " integer, " +
            KEY_TEXT_COLOR + " integer, " +
            KEY_TEXT_GRAVITY + " integer, " +
            //KEY_TEXT_SINGLE_LINE + " integer not null default 0, " +
            KEY_TEXT_MAX_LINES + " integer, " +
            KEY_TEXT_PADDING_LEFT + " integer not null default 0, " +
            KEY_TEXT_PADDING_TOP + " integer not null default 0, " +
            KEY_TEXT_PADDING_RIGHT + " integer not null default 0, " +
            KEY_TEXT_PADDING_BOTTOM + " integer not null default 0, " +
            KEY_MARGINS_LEFT + " integer not null default 0, " +
            KEY_MARGINS_TOP + " integer not null default 0, " +
            KEY_MARGINS_RIGHT + " integer not null default 0, " +
            KEY_MARGINS_BOTTOM + " integer not null default 0, " +
            "primary key (" + KEY_ENTRY_KEY + ", " + KEY_PROFILE_NAME + "));";


    public static final String ENTRY_CURRENT_PROFILE = "CurrentProfile";
    public static final String KEY_VALUE = "Value";
    protected static final String SCRIPT_CREATE_UTILS_TABLE =
            "create table " + TABLE_NAME_UTILS + " ( " +
                    KEY_ID + " integer primary key autoincrement, " +
                    KEY_ENTRY_KEY + " text not null, " +
                    KEY_VALUE + " blob not null);";


    private Context mainContext;

    //TODO: Add Groups of Dimensions
//    private Dimensions dimPrototype;
//
//    public void setDimPrototype(Dimensions dimPrototype) {
//        this.dimPrototype = dimPrototype;
//    }

    public TableDimensions(Context mainContext) {
        this.mainContext = mainContext;
    }

    protected static void onCreateDb(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_MAIN_TABLE);
        db.execSQL(SCRIPT_CREATE_UTILS_TABLE);
    }

    private String getNewProfileName() {
        List<String> nameList = getStringList(KEY_PROFILE_NAME, null, null);
        int i = 0;
        StringBuffer sb = new StringBuffer(STR_USER_PROFILE);
        int initLength = sb.length();
        do {
            i++;
            sb.setLength(initLength);
            sb.append(String.valueOf(i));
        } while (nameList.contains(sb.toString()));
        return sb.toString();
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Dimensions loadDbItem(Cursor cur) {
        //assert (dimPrototype != null);
        Dimensions item = new Dimensions(mainContext);
        int columnIndex;

        columnIndex = cur.getColumnIndex(KEY_PROFILE_NAME);
        item.setProfileName(cur.getString(columnIndex));

        columnIndex = cur.getColumnIndex(KEY_ENTRY_KEY);
        item.setKey(cur.getInt(columnIndex));

        columnIndex = cur.getColumnIndex(KEY_TEXT_SIZE);
        if (cur.isNull(columnIndex)) { item.setTextSize(null); } else {
            item.setTextSize(cur.getInt(columnIndex));
        }

        columnIndex = cur.getColumnIndex(KEY_TEXT_STYLE);
        if (cur.isNull(columnIndex)) { item.setTextStyle(null); } else {
            item.setTextStyle(cur.getInt(columnIndex));
        }

        columnIndex = cur.getColumnIndex(KEY_TEXT_COLOR);
        if (cur.isNull(columnIndex)) { item.setTextColor(null); } else {
            item.setTextColor(cur.getInt(columnIndex));
        }

        columnIndex = cur.getColumnIndex(KEY_TEXT_GRAVITY);
        if (cur.isNull(columnIndex)) { item.setGravity(null); } else {
            item.setGravity(cur.getInt(columnIndex));
        }

//        columnIndex = cur.getColumnIndex(KEY_TEXT_SINGLE_LINE);
//        if (cur.getInt(columnIndex) == 0) { item.setSingleTextLine(false); } else {
//            item.setSingleTextLine(true);
//        }

        columnIndex = cur.getColumnIndex(KEY_TEXT_MAX_LINES);
        if (cur.isNull(columnIndex)) { item.setMaxLines(null); } else {
            item.setMaxLines(cur.getInt(columnIndex));
        }


        columnIndex = cur.getColumnIndex(KEY_BACKGROUND_COLOR);
        if (cur.isNull(columnIndex)) { item.setBgColor(null); } else {
            item.setBgColor(cur.getInt(columnIndex));
        }

        columnIndex = cur.getColumnIndex(KEY_WIDTH);
        if (cur.isNull(columnIndex)) { item.setViewWidth(null); } else {
            item.setViewWidth(cur.getInt(columnIndex));
        }

        if (!(cur.isNull(cur.getColumnIndex(KEY_LAYOUT_WIDTH)) || cur.isNull(cur
                                                                                     .getColumnIndex(
                                                                                             KEY_LAYOUT_HEIGHT)))) {
            item.setLayout(cur.getInt(cur.getColumnIndex(KEY_LAYOUT_WIDTH)),
                           cur.getInt(cur.getColumnIndex(KEY_LAYOUT_HEIGHT)));
        }

        int m_left = cur.getInt(cur.getColumnIndex(KEY_MARGINS_LEFT));
        int m_top = cur.getInt(cur.getColumnIndex(KEY_MARGINS_TOP));
        int m_right = cur.getInt(cur.getColumnIndex(KEY_MARGINS_RIGHT));
        int m_bottom = cur.getInt(cur.getColumnIndex(KEY_MARGINS_BOTTOM));
        item.setMargins(m_left, m_top, m_right, m_bottom);

        int p_left = cur.getInt(cur.getColumnIndex(KEY_TEXT_PADDING_LEFT));
        int p_top = cur.getInt(cur.getColumnIndex(KEY_TEXT_PADDING_TOP));
        int p_right = cur.getInt(cur.getColumnIndex(KEY_TEXT_PADDING_RIGHT));
        int p_bottom = cur.getInt(cur.getColumnIndex(KEY_TEXT_PADDING_BOTTOM));
        item.setPadding(p_left, p_top, p_right, p_bottom);

        return item;
    }

    @Override
    protected ContentValues convertToCV(Dimensions dims) {
        ContentValues cv = new ContentValues();

        cv.put(KEY_PROFILE_NAME, dims.getProfileName());
        cv.put(KEY_ENTRY_KEY, dims.getKey());
        cv.put(KEY_TEXT_SIZE, dims.getTextSize());
        cv.put(KEY_TEXT_STYLE, dims.getTextStyle());
        cv.put(KEY_TEXT_COLOR, dims.getTextColor());
        cv.put(KEY_TEXT_GRAVITY, dims.getGravity());
        //cv.put(KEY_TEXT_SINGLE_LINE, dims.isSingleTextLine());
        cv.put(KEY_TEXT_MAX_LINES, dims.getMaxLines());
        cv.put(KEY_MARGINS_LEFT, dims.getMarginsLeft());
        cv.put(KEY_MARGINS_TOP, dims.getMarginsTop());
        cv.put(KEY_MARGINS_RIGHT, dims.getMarginsRight());
        cv.put(KEY_MARGINS_BOTTOM, dims.getMarginsBottom());
        cv.put(KEY_BACKGROUND_COLOR, dims.getBgColor());
        cv.put(KEY_WIDTH, dims.getViewWidth());
        cv.put(KEY_LAYOUT_WIDTH, dims.getLayoutWidth());
        cv.put(KEY_LAYOUT_HEIGHT, dims.getLayoutHeight());
        cv.put(KEY_TEXT_PADDING_LEFT, dims.getPaddingLeft());
        cv.put(KEY_TEXT_PADDING_TOP, dims.getPaddingTop());
        cv.put(KEY_TEXT_PADDING_RIGHT, dims.getPaddingRight());
        cv.put(KEY_TEXT_PADDING_BOTTOM, dims.getPaddingBottom());
        return cv;
    }

    public List<String> getProfileNames() {
        return super.getStringList(KEY_PROFILE_NAME, null, null, KEY_PROFILE_NAME);
    }

    public String getCurrentProfile() {
        byte[] blob = new byte[0];
        String currentProfile = null;
        Cursor cur = sqlDatabase.query(
                TABLE_NAME_UTILS,
                null,
                KEY_ENTRY_KEY + " = ?",
                new String[]{ENTRY_CURRENT_PROFILE},
                null,
                null,
                null);
        if (DataCheck.checkCursor(cur)) {
            blob = cur.getBlob(cur.getColumnIndex(KEY_VALUE));
            cur.close();
        }

        if ((blob != null) && (blob.length > 0)) {
            try {
                currentProfile = new String(blob, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return currentProfile;
    }

    public long setCurrentProfile(String profileName) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ENTRY_KEY, ENTRY_CURRENT_PROFILE);
        cv.put(KEY_VALUE, profileName.getBytes());
        if (getCurrentProfile() == null) {
            return sqlDatabase.insert(TABLE_NAME_UTILS, null, cv);
        } else {
            return sqlDatabase.update(TABLE_NAME_UTILS, cv,
                                      KEY_ENTRY_KEY + " = ?",
                                      new String[]{ENTRY_CURRENT_PROFILE});
        }
    }

    public List<Dimensions> getProfile(String profileName) {
        return super.getList(KEY_PROFILE_NAME + " = ?", new String[]{profileName}, null);
    }

    public long delete(String profileName, int key) {
        return super.delete(KEY_PROFILE_NAME + " = ? and " + KEY_ENTRY_KEY + " = ?",
                            new String[]{profileName, String.valueOf(key)});
    }

    public long delete(String profileName) {
        return super.delete(KEY_PROFILE_NAME + " = ? ",
                            new String[]{profileName});
    }

    public int update(Dimensions item) {
        String selection = KEY_PROFILE_NAME + " = ? and " + KEY_ENTRY_KEY + " = ?";
        String[] selectionArgs = new String[]{item.getProfileName(), String.valueOf(item.getKey())};
        return super.update(selection, selectionArgs, item);
    }


}
