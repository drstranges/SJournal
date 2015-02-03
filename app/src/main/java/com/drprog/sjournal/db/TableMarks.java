package com.drprog.sjournal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drprog.sjournal.db.entity.StudyMark;
import com.drprog.sjournal.db.utils.DataCheck;
import com.drprog.sjournal.db.utils.SQLBaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 01.02.14.
 */
public class TableMarks extends SQLBaseTable<StudyMark> {
    public static final String TABLE_NAME = "Marks";

    public static final String KEY_STUDENT_ID = "StudentId";
    public static final String KEY_CLASS_ID = "ClassId";
    public static final String KEY_TYPE = "Type";
    public static final String KEY_MARK = "Mark";
    public static final String KEY_SYMBOL = "Symbol";
    public static final String KEY_NOTE = "Note";
    protected static final String SCRIPT_CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_STUDENT_ID + " integer not null, " +
            KEY_CLASS_ID + " integer not null, " +
            KEY_TYPE + " integer not null default 0, " +
            KEY_MARK + " integer, " +
            KEY_SYMBOL + " text, " +
            KEY_NOTE + " text, " +
            //"primary key (" + KEY_STUDENT_ID + ", " + KEY_CLASS_ID + "), " +
            "foreign key (" + KEY_STUDENT_ID + ") references " + TableStudents.TABLE_NAME +
            "(" + TableStudents.KEY_ID + ") on delete cascade on update cascade, " +
            "foreign key (" + KEY_CLASS_ID + ") references " + TableClasses.TABLE_NAME + "(" +
            TableClasses.KEY_ID + ") on delete cascade on update cascade );";
    //TODO: Add triggers!

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
    protected StudyMark loadDbItem(Cursor cur) {
        StudyMark item = new StudyMark();
        item.setId(cur.getLong(cur.getColumnIndex(KEY_ID)));
        item.setStudentId(cur.getLong(cur.getColumnIndex(KEY_STUDENT_ID)));
        item.setClassId(cur.getLong(cur.getColumnIndex(KEY_CLASS_ID)));
        item.setType(cur.getInt(cur.getColumnIndex(KEY_TYPE)));
        item.setMark(cur.getInt(cur.getColumnIndex(KEY_MARK)));
        item.setSymbol(cur.getString(cur.getColumnIndex(KEY_SYMBOL)));
        item.setNote(cur.getString(cur.getColumnIndex(KEY_NOTE)));
        return item;
    }

    @Override
    protected ContentValues convertToCV(StudyMark item) {
        ContentValues cv = new ContentValues();
        if (item.getId() != null) { cv.put(KEY_ID, item.getId()); }
        cv.put(KEY_STUDENT_ID, item.getStudentId());
        cv.put(KEY_CLASS_ID, item.getClassId());
        cv.put(KEY_TYPE, item.getType());
        if (item.getMark() != null) { cv.put(KEY_MARK, item.getMark()); }
        if (item.getSymbol() != null) { cv.put(KEY_SYMBOL, item.getSymbol()); }
        if (item.getNote() != null) { cv.put(KEY_NOTE, item.getNote()); }
        return cv;
    }

    public StudyMark get(long studentId, long classId) {
        return super.get(KEY_STUDENT_ID + " = ? and " + KEY_CLASS_ID + " = ?",
                         new String[]{String.valueOf(studentId), String.valueOf(classId)});
    }

    public List<StudyMark> getMarks(long groupId, long subjectId, long classTypeId, int semester) {
        List<StudyMark> itemList = new ArrayList<StudyMark>();
        String sqlQ = "select table1.*"
                + " from " + TABLE_NAME + " as table1 inner join " + TableGroupManagement.TABLE_NAME
                + " as table2 on table1." + KEY_STUDENT_ID + " = table2." +
                TableGroupManagement.KEY_STUDENT_ID + " inner join " + TableClasses.TABLE_NAME +
                " as table3 on table1." + KEY_CLASS_ID + " = table3." + TableClasses.KEY_ID
                + " where table2." + TableGroupManagement.KEY_GROUP_ID + " = ? and table3."
                + TableClasses.KEY_SUBJECT_ID + " = ? and table3."
                + TableClasses.KEY_CLASS_TYPE_ID + " = ? and table3." + TableClasses.KEY_SEMESTER
                + " = ? ;";
        Cursor cur = sqlDatabase.rawQuery(sqlQ, new String[]{String.valueOf(groupId),
                String.valueOf(subjectId), String.valueOf(classTypeId), String.valueOf(semester)});
        if (DataCheck.checkCursor(cur)) {
            do {
                StudyMark item = loadDbItem(cur);
                itemList.add(item);
            }
            while (cur.moveToNext());
            cur.close();
        }
        return itemList;
    }

    public List<StudyMark> getMarks(long groupId, long classId) {
        List<StudyMark> itemList = new ArrayList<StudyMark>();
        String sqlQ = "select table1.*"
                + " from " + TABLE_NAME + " as table1 inner join " + TableGroupManagement.TABLE_NAME
                + " as table2 on table1." + KEY_STUDENT_ID + " = table2." +
                TableGroupManagement.KEY_STUDENT_ID
                + " where table2." + TableGroupManagement.KEY_GROUP_ID + " = ? and table1."
                + KEY_CLASS_ID + " = ? ;";
        Cursor cur = sqlDatabase.rawQuery(sqlQ, new String[]{String.valueOf(groupId),
                String.valueOf(classId)});
        if (DataCheck.checkCursor(cur)) {
            do {
                StudyMark item = loadDbItem(cur);
                itemList.add(item);
            }
            while (cur.moveToNext());
            cur.close();
        }
        return itemList;
    }

    public int getAbsentNumInClass(long groupId,long classId) {
//        return getCount(KEY_CLASS_ID + " = ? and " + KEY_TYPE + " = ?",
//                        new String[]{String.valueOf(classId),
//                                String.valueOf(StudyMark.TYPE_ABSENT)}
//        );
       int res = 0;
//        String sqlQ = "select count(*) from " + TABLE_NAME
//                + " as table1 inner join " + TableClasses.TABLE_NAME
//                + " as table2 on table1." + KEY_CLASS_ID + " = table2."
//                + TableClasses.KEY_ID + " inner join " + TableGroupManagement.TABLE_NAME +
//                " as table3 on table1." + KEY_STUDENT_ID + " = table3." +
//                TableGroupManagement.KEY_STUDENT_ID +
//                " where table1." + KEY_TYPE + " = ?"
//                + " and table1." + KEY_CLASS_ID + " = ?"
//                + " and table2." + TableClasses.KEY_GROUP_ID + " = ?"
//                + " and table3." + TableGroupManagement.KEY_GROUP_ID + " = ?;";

        String sqlQ = "select count(*) from " +
                "(select * from " + TABLE_NAME + " where " + KEY_TYPE + " = ? and " + KEY_CLASS_ID + " = ? )"
                + " as table1 inner join " +
                "(select * from " + TableGroupManagement.TABLE_NAME + " where " + TableGroupManagement.KEY_GROUP_ID + " = ? )"
                + " as table2 on table1." + KEY_STUDENT_ID + " = table2." + TableGroupManagement.KEY_STUDENT_ID +
                " where table1." + KEY_STUDENT_ID + " is not null;";


        String[] args = new String[]{
                String.valueOf(StudyMark.TYPE_ABSENT),
                String.valueOf(classId),
                String.valueOf(groupId)};
        Cursor cur = sqlDatabase.rawQuery(sqlQ, args);
        if (DataCheck.checkCursor(cur)) {
            res = cur.getInt(0);
            cur.close();
        }
        return res;
    }

    public int getAbsentNumInSemester(long groupId, long studentId, Long subjectId, Long classTypeId,
            int semester) {
        int res = 0;
        String sqlQ = "select count(*) from " + TABLE_NAME
                + " as table1 inner join " + TableClasses.TABLE_NAME
                + " as table2 on table1." + KEY_CLASS_ID + " = table2."
                + TableClasses.KEY_ID + " where table1." + KEY_TYPE + " = ?"
                + " and table2." + TableClasses.KEY_GROUP_ID + " = ?"
                +
                ((classTypeId != null) ? " and table2." + TableClasses.KEY_CLASS_TYPE_ID + " = ?" :
                        "")
                + " and table1." + KEY_STUDENT_ID + " = ?"
                + " and table2." + TableClasses.KEY_SUBJECT_ID + " = ? and table2."
                + TableClasses.KEY_SEMESTER + " = ? ;";
        String[] args = classTypeId != null ? new String[]{
                String.valueOf(StudyMark.TYPE_ABSENT),
                String.valueOf(groupId),
                String.valueOf(classTypeId),
                String.valueOf(studentId),
                String.valueOf(subjectId),
                String.valueOf(semester)}
                :
                new String[]{
                        String.valueOf(StudyMark.TYPE_ABSENT),
                        String.valueOf(groupId),
                        String.valueOf(studentId),
                        String.valueOf(subjectId),
                        String.valueOf(semester)};
        Cursor cur = sqlDatabase.rawQuery(sqlQ, args);
        if (DataCheck.checkCursor(cur)) {
            res = cur.getInt(0);
            cur.close();
        }
        return res;
    }

//    public static void deleteMarks(SQLiteDatabase db, long studentId, long groupId){
//        String sqlQ = "delete from " + TABLE_NAME
//                + " as table1 inner join " + TableClasses.TABLE_NAME
//                + " as table2 on table1." + KEY_CLASS_ID + " = table2."
//                + TableClasses.KEY_ID + " where table1." + KEY_STUDENT_ID + " = ?"
//                + " and table2." + TableClasses.KEY_GROUP_ID + " = ?;";
//        String[] args = new String[]{
//                String.valueOf(studentId),
//                String.valueOf(groupId)};
//        db.rawQuery(sqlQ, args);
//    }
}
