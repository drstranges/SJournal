package com.drprog.sjournal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drprog.sjournal.db.entity.StudyClass;
import com.drprog.sjournal.db.utils.SQLBaseTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Romka on 01.02.14.
 */
public class TableClasses extends SQLBaseTable<StudyClass> {
    public static final String TABLE_NAME = "Classes";

    public static final String KEY_SEMESTER = "Semester";
    public static final String KEY_GROUP_ID = "GroupId";
    public static final String KEY_SUBJECT_ID = "SubjectId";
    public static final String KEY_CLASS_TYPE_ID = "ClassTypeId";
    public static final String KEY_DATE = "Date";
    public static final String KEY_TITLE = "Title";
    public static final String KEY_ABBR = "Abbr"; //Abbreviation
    public static final String KEY_ROW_COLOR = "RowColor";
    public static final String KEY_ROW_TEXT_COLOR = "RowTextColor";
    public static final String KEY_NOTE = "Note";
    protected static final String SCRIPT_CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_SEMESTER + " integer not null default 1, " +
            KEY_GROUP_ID + " integer not null, " +
            KEY_SUBJECT_ID + " integer not null, " +
            KEY_CLASS_TYPE_ID + " integer not null, " +
            KEY_DATE + " text not null, " +
            KEY_TITLE + " text, " +
            KEY_ABBR + " text, " +
            KEY_ROW_COLOR + " integer, " +
            KEY_ROW_TEXT_COLOR + " integer, " +
            KEY_NOTE + " text, " +
            "foreign key (" + KEY_GROUP_ID + ") references " + TableGroups.TABLE_NAME +
            "(" + TableGroups.KEY_ID + ") on delete cascade on update cascade, " +
            "foreign key (" + KEY_SUBJECT_ID + ") references " + TableSubjects.TABLE_NAME +
            "(" + KEY_ID + ") on delete cascade on update cascade, " +
            "foreign key (" + KEY_CLASS_TYPE_ID + ") references " + TableClassTypes.TABLE_NAME +
            "(" + TableClassTypes.KEY_ID + ") on delete cascade on update cascade);";
    //not change dateFormat
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
    protected StudyClass loadDbItem(Cursor cur) {
        StudyClass item = new StudyClass();
        item.setId(cur.getLong(cur.getColumnIndex(KEY_ID)));
        item.setSemester(cur.getInt(cur.getColumnIndex(KEY_SEMESTER)));
        item.setGroupId(cur.getLong(cur.getColumnIndex(KEY_GROUP_ID)));
        item.setSubjectId(cur.getLong(cur.getColumnIndex(KEY_SUBJECT_ID)));
        item.setClassTypeId(cur.getLong(cur.getColumnIndex(KEY_CLASS_TYPE_ID)));
        item.setDate(cur.getString(cur.getColumnIndex(KEY_DATE)));
        item.setTheme(cur.getString(cur.getColumnIndex(KEY_TITLE)));
        item.setAbbr(cur.getString(cur.getColumnIndex(KEY_ABBR)));
        if (!cur.isNull(cur.getColumnIndex(KEY_ROW_COLOR))) {
            item.setRowColor(cur.getInt(cur.getColumnIndex(KEY_ROW_COLOR)));
        }
        //TODO: Add if (! cur.isNull) to All Nullable entries in any tables
        if (!cur.isNull(cur.getColumnIndex(KEY_ROW_TEXT_COLOR))) {
            item.setTextColor(cur.getInt(cur.getColumnIndex(KEY_ROW_TEXT_COLOR)));
        }
        item.setNote(cur.getString(cur.getColumnIndex(KEY_NOTE)));
        return item;
    }

    @Override
    protected ContentValues convertToCV(StudyClass item) {
        ContentValues cv = new ContentValues();
        if (item.getId() != null) { cv.put(KEY_ID, item.getId()); }
        cv.put(KEY_SEMESTER, item.getSemester());
        cv.put(KEY_GROUP_ID, item.getGroupId());
        cv.put(KEY_SUBJECT_ID, item.getSubjectId());
        cv.put(KEY_CLASS_TYPE_ID, item.getClassTypeId());
        cv.put(KEY_DATE, item.getDate());
        if (item.getTheme() != null) { cv.put(KEY_TITLE, item.getTheme()); }
        if (item.getAbbr() != null) { cv.put(KEY_ABBR, item.getAbbr()); }
        if (item.getRowColor() != null) { cv.put(KEY_ROW_COLOR, item.getRowColor()); }
        if (item.getTextColor() != null) { cv.put(KEY_ROW_TEXT_COLOR, item.getTextColor()); }
        if (item.getNote() != null) { cv.put(KEY_NOTE, item.getNote()); }
        return cv;
    }

    public List<StudyClass> getClasses(long groupId, long subjectId, long classTypeId,
            int semester) {
        return super.getList(KEY_GROUP_ID + " = ? and "
                                     + KEY_SUBJECT_ID + " = ? and "
                                     + KEY_CLASS_TYPE_ID + " = ? and "
                                     + KEY_SEMESTER + " = ?",
                             new String[]{String.valueOf(groupId),
                                     String.valueOf(subjectId),
                                     String.valueOf(classTypeId),
                                     String.valueOf(semester)},
                             "date(" + KEY_DATE + "), " + KEY_ABBR
        );
    }

    public boolean isClassContain(String key, long id) {
        return super.get(key + " = ?", new String[]{String.valueOf(id)}) != null;
    }

    public StudyClass getClassAtNow(long groupId, long subjectId, long classTypeId, int semester) {
        List<StudyClass> list = super.getList(KEY_GROUP_ID + " = ? and " +
                                                      KEY_SUBJECT_ID + " = ? and " +
                                                      KEY_CLASS_TYPE_ID + " = ? and " +
                                                      KEY_SEMESTER + " = ? and " +
                                                      KEY_DATE + " = ?",
                                              new String[]{String.valueOf(groupId),
                                                      String.valueOf(subjectId),
                                                      String.valueOf(classTypeId),
                                                      String.valueOf(semester),
                                                      dateFormat.format(Calendar.getInstance()
                                                                                .getTime())},
                                              KEY_ABBR
        );
        if (list == null || list.isEmpty()) return null;
        return list.get(list.size() - 1);
    }
}
