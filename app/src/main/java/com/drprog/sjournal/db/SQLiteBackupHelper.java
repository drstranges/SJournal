package com.drprog.sjournal.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.drprog.sjournal.db.exception.NotValidBackupDbException;
import com.drprog.sjournal.db.utils.DataCheck;
import com.drprog.sjournal.utils.IOFiles;

import java.io.File;
import java.io.IOException;

/**
 * Created by Romka on 26.08.23.
 */

public class SQLiteBackupHelper extends SQLiteOpenHelper {

    public static final String TEMP_DB_NAME = "SJournalDB-tmp.db";
    public static final String BACKUP_DB_NAME_PREFIX = "SJournalDB-backup";

    private SQLiteBackupHelper(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }

    public static void restoreDB(Context context, Uri sourceFileUri) throws NotValidBackupDbException {
        //Validate temp DB
        assertFileIsValidBackup(context, sourceFileUri);

        //Force close current DB
        SQLiteJournalHelper.getWritableInstance(context).forceClose();

        //Make an internal backup of current DB
        String currentBackupFileName = BACKUP_DB_NAME_PREFIX + System.currentTimeMillis() + ".dbk";
        File currentBackupFile = IOFiles.saveDbToInternalDir(
                context,
                SQLiteJournalHelper.DB_NAME,
                currentBackupFileName
        );
        //Restore backup
        try {
            IOFiles.restoreDB(
                    context,
                    SQLiteJournalHelper.DB_NAME,
                    sourceFileUri
            );
            SQLiteJournalHelper.getWritableInstance(context);
        } catch (IOException e) {
            try {
                IOFiles.restoreDB(context, SQLiteJournalHelper.DB_NAME, currentBackupFile);
                SQLiteJournalHelper.getWritableInstance(context);
            } catch (IOException ex) {
                throw new NotValidBackupDbException("Error while restoring DB to main file: " + e.getMessage() + ". Rollback failed: " + ex.getMessage());
            }
            throw new NotValidBackupDbException("Error while restoring DB to main file: " + e.getMessage());
        }
    }

    private static void assertFileIsValidBackup(Context context, Uri sourceFileUri) throws NotValidBackupDbException {
        // Save DB from source as temp db
        try {
            IOFiles.restoreDB(context, TEMP_DB_NAME, sourceFileUri);
        } catch (IOException e) {
            throw new NotValidBackupDbException("Error while restoring db to temp: " + e.getMessage());
        }
        try (SQLiteBackupHelper dbHelper = new SQLiteBackupHelper(context, TEMP_DB_NAME, SQLiteJournalHelper.DB_VERSION)) {
            dbHelper.validateBackupDb();
        } catch (Exception e) {
            throw new NotValidBackupDbException(e.getMessage());
        }
    }

    private void validateBackupDb() throws SQLiteException, NotValidBackupDbException {
        //Check that all tables exists
        SQLiteDatabase db = this.getReadableDatabase();
        assertBackupDbHasTable(db, TableGroups.TABLE_NAME);
        assertBackupDbHasTable(db, TableSubjects.TABLE_NAME);
        assertBackupDbHasTable(db, TableClassTypes.TABLE_NAME);
        assertBackupDbHasTable(db, TableStudents.TABLE_NAME);
        assertBackupDbHasTable(db, TableClasses.TABLE_NAME);
        assertBackupDbHasTable(db, TableMarks.TABLE_NAME);
        assertBackupDbHasTable(db, TableGroupManagement.TABLE_NAME);
        assertBackupDbHasTable(db, TableSummaryEntries.TABLE_NAME);
        assertBackupDbHasTable(db, TableRules.TABLE_NAME);
        assertBackupDbHasTable(db, TableRuleManagement.TABLE_NAME);
    }

    private static void assertBackupDbHasTable(SQLiteDatabase db, String tableName) throws NotValidBackupDbException {
        if (!checkDbHasTable(db, tableName)) {
            throw new NotValidBackupDbException("Can't find table: " + tableName);
        }
    }

    private static boolean checkDbHasTable(SQLiteDatabase db, String tableName) {
        Cursor cur = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name=?;", new String[]{tableName});
        if (DataCheck.checkCursor(cur)) {
            int count = cur.getInt(0);
            cur.close();
            return count == 1;
        }
        return false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConfigure(SQLiteDatabase db) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
    }


}
