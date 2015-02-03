package com.drprog.sjournal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.db.utils.DataCheck;
import com.drprog.sjournal.db.utils.SQLBaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 30.01.14.
 */
public class TableGroups extends SQLBaseTable<StudyGroup> {

    public static final String TABLE_NAME = "Groups";
    public static final String KEY_CODE = "Code";
    public static final String KEY_SEMESTER = "Semester";
    //public static final String KEY_SPECIALTY = "Specialty";
    //public static final String KEY_SPECIALTY_CODE = "SpecialtyCode";

    protected static final String SCRIPT_CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_CODE + " text not null unique, " +
            KEY_SEMESTER + " integer not null default 1 " +
            //", " +
            //KEY_SPECIALTY_CODE + " text, " +
            //KEY_SPECIALTY + " text" +
            ");";

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
    protected StudyGroup loadDbItem(Cursor cur) {
        StudyGroup item = new StudyGroup();
        item.setId(cur.getLong(cur.getColumnIndex(KEY_ID)));
        item.setCode(cur.getString(cur.getColumnIndex(KEY_CODE)));
        item.setSemester(cur.getInt(cur.getColumnIndex(KEY_SEMESTER)));
        //item.setSpecialtyCode(cur.getString(cur.getColumnIndex(KEY_SPECIALTY_CODE)));
        //item.setSpecialty(cur.getString(cur.getColumnIndex(KEY_SPECIALTY)));
        return item;
    }

    @Override
    protected ContentValues convertToCV(StudyGroup item) {
        ContentValues cv = new ContentValues();
        if (item.getId() != null) { cv.put(KEY_ID, item.getId()); }
        cv.put(KEY_CODE, item.getCode());
        cv.put(KEY_SEMESTER, item.getSemester());
//        if (item.getSpecialtyCode() != null)
//            cv.put(KEY_SPECIALTY_CODE, item.getSpecialtyCode());
//        if (item.getSpecialty() != null)
//            cv.put(KEY_SPECIALTY, item.getSpecialty());
        return cv;
    }

    public List<StudyGroup> getGroupsByStudentId(long studentId) {
        List<StudyGroup> itemList = new ArrayList<StudyGroup>();
        Cursor cur = TableGroupManagement.getGroups(sqlDatabase, studentId);
        if (DataCheck.checkCursor(cur)) {
            do {
                StudyGroup item = loadDbItem(cur);
                itemList.add(item);
            }
            while (cur.moveToNext());
            cur.close();
        }
        return itemList;
    }

    public List<StudyGroup> getAllByFilter(Long subjectId, Long classTypeId) {
        String sqlQ;
        String[] selectionArgs;
        if (subjectId == null && classTypeId == null) {
            return getAll(KEY_CODE);
        } else {

            sqlQ = "select distinct table1.*"
                    + " from " + TABLE_NAME + " as table1 inner join " + TableClasses.TABLE_NAME
                    + " as table2 on table1." + KEY_ID + " = table2." + TableClasses.KEY_GROUP_ID
                    + " where "
                    +
                    (subjectId != null ? "table2." + TableClasses.KEY_SUBJECT_ID + " = ? " : "")
                    +
                    ((classTypeId != null && subjectId != null) ? "and " : "")
                    +
                    (classTypeId != null ? "table2." + TableClasses.KEY_CLASS_TYPE_ID + " = ? " :
                            "")
                    + "order by table1." + KEY_CODE;
            if (subjectId != null && classTypeId != null) {
                selectionArgs = new String[]{
                        String.valueOf(subjectId),
                        String.valueOf(classTypeId)};
            } else {
                selectionArgs = new String[]{
                        String.valueOf(subjectId != null ? subjectId : classTypeId)
                };
            }

            Cursor cur = sqlDatabase.rawQuery(sqlQ, selectionArgs);
            return super.getList(cur);
            //TODO: Add Filter By Current Semester!
        }
    }

}

