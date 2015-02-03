package com.drprog.sjournal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Romka on 09.07.2014.
 */
public class TableGroupManagement {
    //private static TableGroupManagement ourInstance = null;
    public static final String KEY_ID = "_id";
    public static final String TABLE_NAME = "GroupManagement";
    public static final String KEY_GROUP_ID = "GroupId";
    public static final String KEY_STUDENT_ID = "StudentId";

    private static final String SCRIPT_CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_GROUP_ID + " integer not null, " +
            KEY_STUDENT_ID + " integer not null, " +
            //"primary key (" + KEY_GROUP_ID + ", " + KEY_STUDENT_ID + "), " +
            "foreign key (" + KEY_STUDENT_ID + ") references " +
            TableStudents.TABLE_NAME + "(" + TableStudents.KEY_ID +
            ") on delete cascade on update cascade, " +
            "foreign key (" + KEY_GROUP_ID + ") references " + TableGroups.TABLE_NAME +
            "(" + TableGroups.KEY_ID + ") on delete cascade on update cascade);";


    protected static void onCreateDb(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_TABLE);
    }

    protected static void onUpgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

//    public static synchronized TableGroupManagement getInstance(Context context){
//        if (ourInstance == null) {
//            assert context != null;
//            ourInstance = new TableGroupManagement();
//        }
//        return ourInstance;
//    }

    public static Cursor getStudents(SQLiteDatabase db, long groupId) {
        String sqlQ = "select table1.*"
                + " from " + TableStudents.TABLE_NAME + " as table1 inner join " +
                TableGroupManagement.TABLE_NAME
                + " as table2 on table1." + TableStudents.KEY_ID + " = table2." +
                TableGroupManagement.KEY_STUDENT_ID
                + " where table2." + TableGroupManagement.KEY_GROUP_ID + " = ? order by table1."
                + TableStudents.KEY_NAME_LAST;
        return db.rawQuery(sqlQ, new String[]{String.valueOf(groupId)});
    }

    public static Cursor getGroups(SQLiteDatabase db, long studentId) {
        String sqlQ = "select table1.*"
                + " from " + TableGroups.TABLE_NAME + " as table1 inner join " +
                TableGroupManagement.TABLE_NAME
                + " as table2 on table1." + TableGroups.KEY_ID + " = table2." +
                TableGroupManagement.KEY_GROUP_ID
                + " where table2." + TableGroupManagement.KEY_STUDENT_ID + " = ?";
        return db.rawQuery(sqlQ, new String[]{String.valueOf(studentId)});
    }

    public static int delete(SQLiteDatabase db, long groupId, long studentId) {
        //TableMarks.deleteMarks(db,studentId,groupId);
        return db.delete(TABLE_NAME, KEY_GROUP_ID + " = ? and " + KEY_STUDENT_ID + " = ?",
                         new String[]{String.valueOf(groupId), String.valueOf(studentId)});
    }

//    public static int deleteAllStudentsFromAllGroups(SQLiteDatabase db) {
//        return db.delete(TABLE_NAME, null, null);
//    }

    public static int deleteStudentFromAllGroups(SQLiteDatabase db, long studentId) {
        return db.delete(TABLE_NAME, KEY_STUDENT_ID + " = ?",
                         new String[]{String.valueOf(studentId)});
    }

    public static long insert(SQLiteDatabase db, long groupId, long studentId) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_STUDENT_ID, studentId);
        cv.put(KEY_GROUP_ID, groupId);
        return db.insert(TABLE_NAME, null, cv);
    }

//    public static int update(SQLiteDatabase db, long oldGroupId, long oldStudentId,
//            long newGroupId, long newStudentId) {
//        ContentValues cv = new ContentValues();
//        cv.put(KEY_STUDENT_ID, newStudentId);
//        cv.put(KEY_GROUP_ID, newGroupId);
//        return db.update(TABLE_NAME, cv,
//                         KEY_GROUP_ID + " = ? and " + KEY_STUDENT_ID + " = ?",
//                         new String[]{String.valueOf(oldGroupId), String.valueOf(oldStudentId)});
//    }

}
