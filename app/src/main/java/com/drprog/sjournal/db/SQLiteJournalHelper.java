package com.drprog.sjournal.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;

import com.drprog.sjournal.db.entity.StudyMark;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.IOFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Romka on 30.01.14.
 */

public class SQLiteJournalHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "SJournalDB.db";
    public static final int DB_VERSION = 3;

    //private static final String OLD_DB_NAME = "SJournalDB.db";
    //private static final String DB_TEST = "com.drprog.test.tdb";    // Test db-file

    private static final String OLD_DB_SAVED_FILE_NAME = "old_SJournalDB.db";
    private static SQLiteJournalHelper ourInstance = null;
    public final TableGroups groups = new TableGroups();
    public final TableSubjects subjects = new TableSubjects();
    public final TableClassTypes classTypes = new TableClassTypes();
    public final TableStudents students = new TableStudents();
    public final TableClasses classes = new TableClasses();
    public final TableMarks marks = new TableMarks();
    public final TableSummaryEntries summaryEntries = new TableSummaryEntries();

    //public TableMarkTypes markTypes;
    //public TableGroupManagement groupManagement;  -- > Only static.
    public final TableRules rules = new TableRules();
    private Context mainContext;
    private volatile boolean requestToClose = false;
    private List<String> frozeList = new ArrayList<String>();

    private SQLiteJournalHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mainContext = context;
    }

    public static synchronized SQLiteJournalHelper getInstance(Context context) {
        if (ourInstance == null) {
            assert context != null;
            ourInstance = new SQLiteJournalHelper(context.getApplicationContext());
        }
//        Log.d(TAG_DEBUG,
//              "-----SQLiteJournalHelper.getInstance (" + context.getApplicationContext() +
//                      ")-----");
        return ourInstance;
    }

    public static synchronized SQLiteJournalHelper getWritableInstance(Context context) {
        ourInstance = getInstance(context);
        ourInstance.getWritableDatabase();
        return ourInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableGroups.onCreateDb(db);
        TableSubjects.onCreateDb(db);
        TableClassTypes.onCreateDb(db);
        TableStudents.onCreateDb(db);
        TableClasses.onCreateDb(db);
        TableMarks.onCreateDb(db);
        TableGroupManagement.onCreateDb(db);
        TableSummaryEntries.onCreateDb(db);
        TableRules.onCreateDb(db);
        TableRuleManagement.onCreateDb(db);
        //TableMarkTypes.onCreate(db);
        //Log.d(TAG_DEBUG, "-----SQLiteJournalHelper.onCreate-----");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /* TableGroups.onUpgrade(db, oldVersion, newVersion);
        TableGroups.onUpgrade(db, oldVersion, newVersion);
        TableStudents.onUpgrade(db, oldVersion, newVersion);
        TableSubjects.onUpgrade(db, oldVersion, newVersion);
        TableClassTypes.onUpgrade(db, oldVersion, newVersion);
        TableMarkTypes.onUpgrade(db, oldVersion, newVersion);
        TableGroupManagement.onUpgrade(db, oldVersion, newVersion);
        TableClasses.onUpgrade(db, oldVersion, newVersion);
        TableMarks.onUpgrade(db, oldVersion, newVersion);*/
        migrateFromFirstBeta(db, oldVersion, newVersion);
        //Log.d(TAG_DEBUG, "-----SQLiteJournalHelper.onUpgrade-----");
    }

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConfigure(SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            } else {
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

        //TODO: Check working on API 14 and above!
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            } else {
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }
//        super.onOpen(db);
//        if (!db.isReadOnly()) {
//            db.execSQL("PRAGMA foreign_keys = ON;");
//        }
        groups.onOpenDb(db);
        subjects.onOpenDb(db);
        classTypes.onOpenDb(db);
        groups.onOpenDb(db);
        students.onOpenDb(db);
        subjects.onOpenDb(db);
        classes.onOpenDb(db);
        marks.onOpenDb(db);
        summaryEntries.onOpenDb(db);
        rules.onOpenDb(db);
        //markTypes = new TableMarkTypes(db);
        //groupManagement = new TableGroupManagement(db);

        requestToClose = false;
        //Log.d(TAG_DEBUG, "-----SQLiteJournalHelper.onOpen-----");
    }

    @Override
    public synchronized void close() {
        if (isFrozen()) {
            requestToClose = true;
        } else {
            super.close();
        }
    }

    public boolean isFrozen() {
        return !frozeList.isEmpty();
    }

    public void setFrozenOn(String senderKey) {
        frozeList.add(senderKey);
    }

    public void setFrozenOff(String senderKey) {
        frozeList.remove(senderKey);
        if (frozeList.isEmpty() && requestToClose) {
            requestToClose = false;
            close();
        }
    }

    public int clearFrozeList() {
        int size = frozeList.size();
        frozeList.clear();
        return size;
    }

    public void forceClose() {
        clearFrozeList();
        super.close();
    }


    //=============================== Migrate from v.0.1b ==========================================

    private boolean isDbFileExists(String dbName) {
        File dbFile = mainContext.getDatabasePath(dbName);
        //myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        return dbFile.exists();
    }

    private void migrateFromFirstBeta(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 2) {
            migrateFromFirstBeta(db);
        }
    }

    private void migrateFromFirstBeta(SQLiteDatabase db) {
        //DebugUtils.log_d("Check " + DB_NAME + " for Exists: " + isDbFileExists(DB_NAME));
        IOFiles.saveDbToInternalDir(mainContext, DB_NAME, OLD_DB_SAVED_FILE_NAME);
        //------------- Delete Triggers ------------------------------------------------------------
        db.setLocale(Locale.getDefault());
        db.execSQL("DROP TRIGGER IF EXISTS tr_Profiles");
        db.execSQL("DROP TRIGGER IF EXISTS tr_Subjects");
        db.execSQL("DROP TRIGGER IF EXISTS tr_sGroups");
        db.execSQL("DROP TRIGGER IF EXISTS tr_Students");
        db.execSQL("DROP TRIGGER IF EXISTS tr_GroupMembers");
        db.execSQL("DROP TRIGGER IF EXISTS tr_Classes");
        db.execSQL("DROP TRIGGER IF EXISTS tr_Marks");
        db.execSQL("DROP TRIGGER IF EXISTS tr_Symbols");
        db.execSQL("DROP TRIGGER IF EXISTS tr_Absent");

        //------------- Rename existing tables -----------------------------------------------------

        final String oldSubjects = "oldSubjects";
        final String oldGroups = "oldGroups";
        final String oldStudents = "oldStudents";
        final String oldGroupMembers = "oldGroupMembers";
        final String oldClasses = "oldClasses";
        final String oldMarks = "oldMarks";
        final String oldSymbols = "oldSymbols";
        final String oldAbsent = "oldAbsent";
        //Profiles table

        db.execSQL("ALTER TABLE Subjects RENAME TO " + oldSubjects);
        db.execSQL("ALTER TABLE sGroups RENAME TO " + oldGroups);
        db.execSQL("ALTER TABLE Students RENAME TO " + oldStudents);
        db.execSQL("ALTER TABLE GroupMembers RENAME TO " + oldGroupMembers);
        db.execSQL("ALTER TABLE Classes RENAME TO " + oldClasses);
        db.execSQL("ALTER TABLE Marks RENAME TO " + oldMarks);
        db.execSQL("ALTER TABLE Symbols RENAME TO " + oldSymbols);
        db.execSQL("ALTER TABLE Absent RENAME TO " + oldAbsent);

        //----------------- Create New Tables ------------------------------------------------------
        onCreate(db);

        //------------------ Transfer Data ---------------------------------------------------------

        db.execSQL("INSERT INTO " + TableSubjects.TABLE_NAME + "(" +
                           TableSubjects.KEY_ID + ", " +
                           TableSubjects.KEY_ABBR + ", " +
                           TableSubjects.KEY_TITLE + ")" +
                           "SELECT _id, Abbr, Subject FROM " + oldSubjects);

        db.execSQL("INSERT INTO " + TableGroups.TABLE_NAME + "(" +
                           TableGroups.KEY_ID + ", " +
                           TableGroups.KEY_CODE + ")" +
                           "SELECT _id, sGroup FROM " + oldGroups);

        db.execSQL("INSERT INTO " + TableStudents.TABLE_NAME + "(" +
                           TableStudents.KEY_ID + ", " +
                           TableStudents.KEY_NAME_LAST + ", " +
                           TableStudents.KEY_NAME_FIRST + ", " +
                           TableStudents.KEY_NAME_MIDDLE + ")" +
                           "SELECT _id, LastName, FirstName, MiddleName FROM " + oldStudents);

        db.execSQL("INSERT INTO " + TableGroupManagement.TABLE_NAME + "(" +
                           TableGroupManagement.KEY_GROUP_ID + ", " +
                           TableGroupManagement.KEY_STUDENT_ID + ")" +
                           "SELECT GroupID, StudentID FROM " + oldGroupMembers);

        String lesson_abr = mainContext.getResources().getString(R.string.lesson_abr);
        String lesson_title = mainContext.getResources().getString(R.string.lesson_title);
        String practice_abr = mainContext.getResources().getString(R.string.practice_abr);
        String practice_title = mainContext.getResources().getString(R.string.practice_title);
        String lab_abr = mainContext.getResources().getString(R.string.lab_abr);
        String lab_title = mainContext.getResources().getString(R.string.lab_title);

        db.execSQL("INSERT INTO " + TableClassTypes.TABLE_NAME + "( " +
                           TableClassTypes.KEY_ID + ", " +
                           TableClassTypes.KEY_ABBR + ", " +
                           TableClassTypes.KEY_TITLE + " ) VALUES " +
                           "( 0, " + lesson_abr + ", " + lesson_title + " );");
        db.execSQL("INSERT INTO " + TableClassTypes.TABLE_NAME + "( " +
                           TableClassTypes.KEY_ID + ", " +
                           TableClassTypes.KEY_ABBR + ", " +
                           TableClassTypes.KEY_TITLE + " ) VALUES " +
                           "( 1, " + practice_abr + ", " + practice_title + " );");

        db.execSQL("INSERT INTO " + TableClassTypes.TABLE_NAME + "( " +
                           TableClassTypes.KEY_ID + ", " +
                           TableClassTypes.KEY_ABBR + ", " +
                           TableClassTypes.KEY_TITLE + " ) VALUES " +
                           "( 2, " + lab_abr + ", " + lab_title + " );");


        //Change date format from "dd.MM.yyyy" to "yyyy-MM-dd" (Defined in TableClasses.java)
        db.execSQL("UPDATE " + oldClasses +
                           " SET DATE = SUBSTR( DATE,7) || \"-\" || SUBSTR( DATE,4,2) || \"-\" || SUBSTR ( DATE,1,2)");
        db.execSQL("UPDATE " + oldClasses +
                           " SET RowColor = NULL WHERE RowColor = 0;");

        db.execSQL("INSERT INTO " + TableClasses.TABLE_NAME + "(" +
                           TableClasses.KEY_ID + ", " +
                           TableClasses.KEY_SUBJECT_ID + ", " +
                           TableClasses.KEY_GROUP_ID + ", " +
                           TableClasses.KEY_CLASS_TYPE_ID + ", " +
                           TableClasses.KEY_DATE + ", " +
                           TableClasses.KEY_TITLE + ", " +
                           TableClasses.KEY_ABBR + ", " +
                           TableClasses.KEY_ROW_COLOR + ", " +
                           TableClasses.KEY_NOTE + ")" +
                           "SELECT _id, SubjectID, GroupID, Type, DATE, Title, Abbr, RowColor," +
                           " Note FROM " + oldClasses);

        db.execSQL("INSERT INTO " + TableMarks.TABLE_NAME + "(" +
                           TableMarks.KEY_STUDENT_ID + ", " +
                           TableMarks.KEY_CLASS_ID + ", " +
                           TableMarks.KEY_TYPE + ", " +
                           TableMarks.KEY_MARK + ", " +
                           TableMarks.KEY_NOTE + ")" +
                           "SELECT StudentID, ClassID, " + StudyMark.TYPE_MARK +
                           ", Mark, Note FROM " + oldMarks);

        db.execSQL("INSERT INTO " + TableMarks.TABLE_NAME + "(" +
                           TableMarks.KEY_STUDENT_ID + ", " +
                           TableMarks.KEY_CLASS_ID + ", " +
                           TableMarks.KEY_TYPE + ", " +
                           TableMarks.KEY_SYMBOL + ", " +
                           TableMarks.KEY_NOTE + ")" +
                           "SELECT StudentID, ClassID, " + StudyMark.TYPE_SYMBOL +
                           ", Symbol, Note FROM " + oldSymbols);

        db.execSQL("INSERT INTO " + TableMarks.TABLE_NAME + "(" +
                           TableMarks.KEY_STUDENT_ID + ", " +
                           TableMarks.KEY_CLASS_ID + ", " +
                           TableMarks.KEY_TYPE + ", " +
                           TableMarks.KEY_NOTE + ")" +
                           "SELECT StudentID, ClassID, " + StudyMark.TYPE_ABSENT +
                           ", Note FROM " + oldAbsent);


        //------------------ Drop old tables -------------------------------------------------------

        db.execSQL("DROP TABLE IF EXISTS Profiles");
        db.execSQL("DROP TABLE IF EXISTS " + oldAbsent);
        db.execSQL("DROP TABLE IF EXISTS " + oldMarks);
        db.execSQL("DROP TABLE IF EXISTS " + oldSymbols);
        db.execSQL("DROP TABLE IF EXISTS " + oldClasses);
        db.execSQL("DROP TABLE IF EXISTS " + oldGroupMembers);
        db.execSQL("DROP TABLE IF EXISTS " + oldStudents);
        db.execSQL("DROP TABLE IF EXISTS " + oldSubjects);
        db.execSQL("DROP TABLE IF EXISTS " + oldGroups);
    }


}
