package com.drprog.sjournal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.db.entity.StudyMark;
import com.drprog.sjournal.db.utils.DataCheck;
import com.drprog.sjournal.db.utils.SQLBaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 01.02.14.
 */
public class TableStudents extends SQLBaseTable<Student> {
    public static final String TABLE_NAME = "Students";

    public static final String KEY_NAME_LAST = "LastName";
    public static final String KEY_NAME_FIRST = "FirstName";
    public static final String KEY_NAME_MIDDLE = "MiddleName";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_PHONE_MOB = "MobPhone";
    public static final String KEY_PHONE = "Phone";
    public static final String KEY_NOTE = "Note";
    private static final String SCRIPT_CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_NAME_LAST + " text not null, " +
            KEY_NAME_FIRST + " text not null, " +
            KEY_NAME_MIDDLE + " text, " +
            KEY_EMAIL + " text, " +
            KEY_PHONE_MOB + " text, " +
            KEY_PHONE + " text, " +
            KEY_NOTE + " text);";

    protected static void onCreateDb(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_TABLE);
    }

    protected static void onUpgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Student loadDbItem(Cursor cur) {
        Student item = new Student();
        item.setId(cur.getLong(cur.getColumnIndex(KEY_ID)));
        item.setLastName(cur.getString(cur.getColumnIndex(KEY_NAME_LAST)));
        item.setFirstName(cur.getString(cur.getColumnIndex(KEY_NAME_FIRST)));
        item.setMiddleName(cur.getString(cur.getColumnIndex(KEY_NAME_MIDDLE)));
        item.setEmail(cur.getString(cur.getColumnIndex(KEY_EMAIL)));
        item.setMobilePhone(cur.getString(cur.getColumnIndex(KEY_PHONE_MOB)));
        item.setPhone(cur.getString(cur.getColumnIndex(KEY_PHONE)));
        item.setNote(cur.getString(cur.getColumnIndex(KEY_NOTE)));
        return item;
    }

    @Override
    protected ContentValues convertToCV(Student item) {
        ContentValues cv = new ContentValues();
        if (item.getId() != null && item.getId() >= 0) { cv.put(KEY_ID, item.getId()); }
        cv.put(KEY_NAME_LAST, item.getLastName());
        cv.put(KEY_NAME_FIRST, item.getFirstName());
        cv.put(KEY_NAME_MIDDLE, item.getMiddleName());
        if (item.getEmail() != null) { cv.put(KEY_EMAIL, item.getEmail()); }
        if (item.getMobilePhone() != null) { cv.put(KEY_PHONE_MOB, item.getMobilePhone()); }
        if (item.getPhone() != null) { cv.put(KEY_PHONE, item.getPhone()); }
        if (item.getNote() != null) { cv.put(KEY_NOTE, item.getNote()); }
        return cv;
    }

    public List<Student> getStudentsByGroupId(long groupId) {
        List<Student> itemList = new ArrayList<Student>();
        Cursor cur = TableGroupManagement.getStudents(sqlDatabase, groupId);
        if (DataCheck.checkCursor(cur)) {
            do {
                Student item = loadDbItem(cur);
                itemList.add(item);
            }
            while (cur.moveToNext());
            cur.close();
        }
        return itemList;
    }

    public long insertStudentInGroup(long studentId, long groupId) {
        return TableGroupManagement.insert(sqlDatabase, groupId, studentId);
    }

    public long deleteStudentFromGroup(long studentId, long groupId) {
        return TableGroupManagement.delete(sqlDatabase, groupId, studentId);
    }

    public long deleteStudentFromAllGroup(long studentId) {
        return TableGroupManagement.deleteStudentFromAllGroups(sqlDatabase, studentId);
    }

    public List<Student> getAbsentList(Long classId) {
        List<Student> itemList = new ArrayList<Student>();
        String sqlQ = "select table1.*"
                + " from " + TABLE_NAME + " as table1 inner join " + TableMarks.TABLE_NAME
                + " as table2 on table1." + KEY_ID + " = table2." +
                TableMarks.KEY_STUDENT_ID
                + " where table2." + TableMarks.KEY_CLASS_ID + " = ? and table2."
                + TableMarks.KEY_TYPE + " = ? ;";
        Cursor cur = sqlDatabase.rawQuery(sqlQ, new String[]{String.valueOf(classId),
                String.valueOf(StudyMark.TYPE_ABSENT)});
        if (DataCheck.checkCursor(cur)) {
            do {
                Student item = loadDbItem(cur);
                itemList.add(item);
            }
            while (cur.moveToNext());
            cur.close();
        }
        return itemList;

    }
}
