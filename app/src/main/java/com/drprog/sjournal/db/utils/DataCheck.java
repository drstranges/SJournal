package com.drprog.sjournal.db.utils;

import android.database.Cursor;

/**
 * Created by Romka on 31.01.14.
 */
public class DataCheck {
    /**
     * This method check cursor to Null or Empty.
     * If cursor is Empty then execute close.
     * If cursor not Null and not Empty then execute moveToFirst.
     *
     * @param cur opened cursor.
     * @return False if cursor is null or empty, True otherwise.
     */
    public static boolean checkCursor(Cursor cur) {
        if (cur == null) return false;
        if (!cur.moveToFirst()) {
            cur.close();
            return false;
        }
        return true;
    }

}
