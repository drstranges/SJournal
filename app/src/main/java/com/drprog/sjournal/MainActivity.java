package com.drprog.sjournal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.drprog.sjournal.db.exception.NotValidBackupDbException;
import com.drprog.sjournal.blank.BlankFragment;
import com.drprog.sjournal.db.SQLiteBackupHelper;
import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.prefs.SQLiteProfileHelper;
import com.drprog.sjournal.dialogs.AboutDialog;
import com.drprog.sjournal.dialogs.HelpDialog;
import com.drprog.sjournal.utils.IOFiles;
import com.drprog.sjournal.utils.RunUtils;



public class MainActivity extends Activity {

    private static final int REQUEST_CODE_PICK_DB_EXPORT_LOCATION = 305;
    private static final int REQUEST_CODE_PICK_DB_SOURCE_TO_RESTORE_LOCATION = 306;
    private int backPressCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BlankFragment blankFragment =
                (BlankFragment) getFragmentManager().findFragmentByTag(BlankFragment.TAG);
        if (blankFragment == null) {
            //DebugUtils.initDatabase(getApplicationContext());
            blankFragment = new BlankFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.mainContainer, blankFragment, BlankFragment.TAG)
                    .commit();

        }
        SQLiteJournalHelper.getWritableInstance(this).forceClose();
        SQLiteProfileHelper.getInstance(this, true).forceClose();

        SQLiteJournalHelper.getWritableInstance(this);
        SQLiteProfileHelper.getInstance(this, true);

        //Log.d(DebugUtils.TAG_DEBUG, "-----MainActivity.onCreate-----");


    }

    @Override
    protected void onStart() {
        super.onStart();
       // Log.d(DebugUtils.TAG_DEBUG, "-----MainActivity.onStart-----");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d(DebugUtils.TAG_DEBUG, "-----MainActivity.onRestart-----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //SQLiteJournalHelper.getInstance(getApplicationContext(), true);
        //Log.d(DebugUtils.TAG_DEBUG, "-----MainActivity.onResume-----");
        //Log.d(DebugUtils.TAG_DEBUG, "" + sqLiteDatabase);
    }

    @Override
    protected void onPause() {
        super.onPause();

       // Log.d(DebugUtils.TAG_DEBUG, "-----MainActivity.onPause-----");

    }

    @Override
    protected void onStop() {
        super.onStop();
       // Log.d(DebugUtils.TAG_DEBUG, "-----MainActivity.onStop-----");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // Log.d(DebugUtils.TAG_DEBUG, "-----MainActivity.onDestroy-----");
        SQLiteJournalHelper.getInstance(this).forceClose();
        SQLiteProfileHelper.getInstance(this).forceClose();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

//        menu.add("Debug: Clear DB");
//        menu.add("Test 1");
//        menu.add("Test 2");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //                if (item.getTitle().equals("Debug: Clear DB")) {
        //                    DebugUtils.initDatabase(getApplicationContext());
        //                    return true;
        //                }
        //                if (item.getTitle().equals("Test 1")) {
        //                    showTest1();
        //                    return true;
        //                }
        //                if (item.getTitle().equals("Test 2")) {
        //
        //                    return true;
        //                }
        if (id == R.id.menu_settings) {
            showPreferences();
            return true;
        } else if (id == R.id.menu_database_export) {
            onDbBackupOptionClicked();
            return true;
        } else if (id == R.id.menu_database_restore) {
            onDbRestoreOptionClicked();
            return true;
        } else if (id == R.id.menu_help) {
            showHelpDialog();
            return true;
        } else if (id == R.id.menu_about) {
            showAboutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        AboutDialog.newInstance()
                .show(getFragmentManager(), "dialog");
    }

    private void showHelpDialog() {
        HelpDialog.newInstance(HelpDialog.HelpId.GENERAL)
                .show(getFragmentManager(), "dialog");
    }

    private void showPreferences() {
        try{
            Intent intent = new Intent(MainActivity.this, com.drprog.sjournal.PrefsActivity.class);
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            RunUtils.showToast(this,R.string.error);
        }
    }

//    private void showTest1() {
//        DebugUtils.deleteJournalDatabase(getApplicationContext());
//    }


    private void onDbBackupOptionClicked() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, "sjournal_backup.dbk");

        startActivityForResult(intent, REQUEST_CODE_PICK_DB_EXPORT_LOCATION);
    }

    private void onDbRestoreOptionClicked() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.dialog_database_restore_title)
                .setMessage(R.string.dialog_database_restore_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> pickDbSourceToRestore())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void pickDbSourceToRestore() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, REQUEST_CODE_PICK_DB_SOURCE_TO_RESTORE_LOCATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_DB_EXPORT_LOCATION && resultCode == Activity.RESULT_OK) {
            Uri targetFileUri = data.getData();
            if (targetFileUri != null) {
                backupDb(targetFileUri);
            }
        } else if (requestCode == REQUEST_CODE_PICK_DB_SOURCE_TO_RESTORE_LOCATION && resultCode == Activity.RESULT_OK) {
            Uri sourceFileUri = data.getData();
            if (sourceFileUri != null) {
                restoreDb(sourceFileUri);
            }
        }
    }

    private void backupDb(Uri destinationUri) {
        SQLiteJournalHelper.getInstance(this).close();
        boolean saved = IOFiles.exportDB(
                this.getApplicationContext(),
                SQLiteJournalHelper.DB_NAME,
                destinationUri
        );
        if (saved) {
            RunUtils.showToast(this, R.string.db_save_ok);
        } else {
            RunUtils.showToast(this, R.string.error);
        }
    }

    private void restoreDb(Uri sourceFileUri) {
        BlankFragment blankFragment = (BlankFragment)
                getFragmentManager().findFragmentByTag(BlankFragment.TAG);
        if (blankFragment != null) blankFragment.clearBlank();
        try {
            SQLiteBackupHelper.restoreDB(this, sourceFileUri);
            RunUtils.showToast(this, R.string.toast_db_restored_successfully);
        } catch (NotValidBackupDbException e) {
            e.printStackTrace();
            RunUtils.showToast(this, R.string.toast_db_restore_error);
        }
        if (blankFragment != null) blankFragment.refreshBlank();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            backPressCount++;
            if (backPressCount > 1) {
                super.onBackPressed();
            } else { RunUtils.showToast(getApplicationContext(), R.string.back_to_exit); }
        } else {
            backPressCount = 0;
            super.onBackPressed();
        }
    }
//        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

//    Thread.UncaughtExceptionHandler defaultExceptionHandler;
//    Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
//        @Override
//        public void uncaughtException(Thread t, Throwable e) {
//            e.printStackTrace();
//
//            PrefsManager.clearBackgroundPrefs(getActivity());
//            RunUtils.showNotify(getActivity(), R.string.error_uncaught);
//
//            defaultExceptionHandler.uncaughtException(t, e);
//        }
//    };
}
