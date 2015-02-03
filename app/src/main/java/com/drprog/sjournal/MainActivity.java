package com.drprog.sjournal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.drprog.sjournal.blank.BlankFragment;
import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.prefs.SQLiteProfileHelper;
import com.drprog.sjournal.dialogs.AboutDialog;
import com.drprog.sjournal.dialogs.BackupDbDialog;
import com.drprog.sjournal.dialogs.HelpDialog;
import com.drprog.sjournal.utils.RunUtils;



public class MainActivity extends Activity {
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
        SQLiteJournalHelper.getInstance(this, true).forceClose();
        SQLiteProfileHelper.getInstance(this, true).forceClose();

        SQLiteJournalHelper.getInstance(this, true);
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
        switch (id) {
            case R.id.menu_settings:
                showPreferences();
                return true;
            case R.id.menu_database_export:
                showBackUpDialog(BackupDbDialog.DIALOG_BACKUP);
                return true;

            case R.id.menu_database_restore:
                showBackUpDialog(BackupDbDialog.DIALOG_RESTORE);
                return true;
            case R.id.menu_help:
                showHelpDialog();
                return true;
            case R.id.menu_about:
                showAboutDialog();
                return true;
            default:
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

    private void showBackUpDialog(int dialogBackup) {
        BackupDbDialog.newInstance(dialogBackup)
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
