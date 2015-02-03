package com.drprog.sjournal.db.prefs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.drprog.sjournal.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Romka on 02.02.14.
 */
public class SQLiteProfileHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "profile.db";
    private static final int DB_VERSION = 3;
    private static final String DB_TEST = "profile.dbt";    // Test db-file
    private static SQLiteProfileHelper ourInstance = null;
    //private Context mainContext;
    public final TableDimensions dimensions;
    private volatile boolean requestToClose = false;
    private final List<String> frozeList = new ArrayList<String>();
    private Context mainContext;

    private SQLiteProfileHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mainContext = context;
        dimensions = new TableDimensions(context);
    }

    public static synchronized SQLiteProfileHelper getInstance(Context context) {
        return getInstance(context, false);
    }

    public static synchronized SQLiteProfileHelper getInstance(Context context,
            boolean getWritableDb) {
        if (ourInstance == null) {
            assert context != null;
            ourInstance = new SQLiteProfileHelper(context.getApplicationContext());
        }
        if (getWritableDb) ourInstance.getWritableDatabase();
//        Log.d(DebugUtils.TAG_DEBUG,
          //    "-----SQLiteProfileHelper.getInstance(" + context.getApplicationContext() + ")-----");
        return ourInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableDimensions.onCreateDb(db);
        /*IOFiles ioFiles = new IOFiles(mainContext);
        try {
            ioFiles.copyTestDB(DB_NAME, DB_TEST);
            db.setLocale(Locale.getDefault());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        fillDefaultTable(db);

    }

    private void fillDefaultTable(SQLiteDatabase db) {
/*        Resources res = mainContext.getResources();
        db.beginTransaction();
        String values = res.getString(R.string.profile_default_blank);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //res.getString(R.string.profile_default_blank));
        values = mainContext.getString(R.string.profile_default_table);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //mainContext.getString(R.string.profile_default_table));
        values = mainContext.getString(R.string.profile_default_topic_text);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //mainContext.getString(R.string.profile_default_topic_text));
        values = mainContext.getString(R.string.profile_default_num_top);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //mainContext.getString(R.string.profile_default_num_top));
        values = mainContext.getString(R.string.profile_default_num_left);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //mainContext.getString(R.string.profile_default_num_left));
        values = mainContext.getString(R.string.profile_default_name_top);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //mainContext.getString(R.string.profile_default_name_top));
        values = mainContext.getString(R.string.profile_default_name_left);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //mainContext.getString(R.string.profile_default_name_left));
        values = mainContext.getString(R.string.profile_default_class_top);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //mainContext.getString(R.string.profile_default_class_top));
        values = mainContext.getString(R.string.profile_default_class_body);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //mainContext.getString(R.string.profile_default_class_body));
        values = mainContext.getString(R.string.profile_default_add_col);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //mainContext.getString(R.string.profile_default_add_col));
        values = mainContext.getString(R.string.profile_default_absent_ceil);
        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + " VALUES" + values);
                           //mainContext.getString(R.string.profile_default_absent_ceil));

//        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME_UTILS +
//                           " VALUES(1,\"CurrentProfile\",BLOB(\"Default\"));");
        db.setTransactionSuccessful();
        db.endTransaction();*/




        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",11,0,NULL,-2,-2,NULL,NULL,NULL,17,NULL,0,0,0,0,0,0,0,0);"//KEY_BLANK
                           mainContext.getString(R.string.profile_default_blank));

        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",10,-16777216,NULL,-2,-2,NULL,NULL,NULL,17,NULL,0,0,0,0,0,0,0,0);");//KEY_TABLE
                           mainContext.getString(R.string.profile_default_table));

        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",1,-16744320,NULL,-1,-2,20,2,-1,17,NULL,0,0,0,0,1,1,1,1);");//KEY_TOPIC_TEXT
                           mainContext.getString(R.string.profile_default_topic_text));

        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",2,-16733271,20,-2,-1,12,0,-16777216,17,NULL,0,0,0,0,1,1,1,1);");//KEY_NUM_TOP
                           mainContext.getString(R.string.profile_default_num_top));

        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",3,-9868951,20,-2,-1,14,0,-1,17,1,0,7,7,0,1,1,1,1);");//KEY_NUM_LEFT
                           mainContext.getString(R.string.profile_default_num_left));

        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",4,-16733271,250,-2,-1,16,0,-16777216,17,1,0,7,7,0,1,1,1,1);");//KEY_NAME_TOP
                           mainContext.getString(R.string.profile_default_name_top));

        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",5,-16713062,250,-2,-1,24,0,-16777216,19,1,4,7,7,0,1,1,1,1);");//KEY_NAME_LEFT
                           mainContext.getString(R.string.profile_default_name_left));

        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",6,-2031617,100,-2,-1,16,0,-16777216,17,NULL,0,0,0,0,1,1,1,1);");//KEY_CLASS_TOP
                           mainContext.getString(R.string.profile_default_class_top));

        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",7,-7876870,100,-2,-1,24,0,-16777216,17,1,0,7,7,0,1,1,1,1);");//KEY_CLASS_BODY
                           mainContext.getString(R.string.profile_default_class_body));

        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",8,-5192482,70,-2,-1,24,0,-16777216,17,1,0,7,7,0,1,1,1,1);");//KEY_ADD_COL
                           mainContext.getString(R.string.profile_default_add_col));

        db.execSQL("INSERT INTO " + TableDimensions.TABLE_NAME + //" VALUES (\"Default\",12,-5192482,100,-2,-1,24,0,-16777216,17,1,0,7,7,0,1,1,1,1);");//KEY_ABSENT_CEIL
                           mainContext.getString(R.string.profile_default_absent_ceil));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        dimensions.onOpenDb(db);
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

    //===========================
    private boolean checkDatabaseFile(String dbName) {
        File dbFile = mainContext.getDatabasePath(dbName);
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                                             SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            db.setLocale(Locale.getDefault());
            db.close();
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }
}
