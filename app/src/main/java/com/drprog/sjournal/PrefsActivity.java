package com.drprog.sjournal;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.drprog.sjournal.R;

import java.util.List;

/**
 * Created by Romka on 16.07.2014.
 */
public class PrefsActivity extends PreferenceActivity {

//    public static PrefsActivity newInstance(){
//        PrefsActivity prefsActivity = new PrefsActivity(){
//            @Override
//            protected boolean isValidFragment(String fragmentName) {
//                return true;
//            }
//        };
//        return prefsActivity;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.preferences_title);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.prefs_headers, target);
    }


    public static class PrefsFragmentBlank extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_blank);
        }
    }

    public static class PrefsFragmentGeneral extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_general);

        }
    }

    public static class PrefsFragmentBlankSummary extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_blank_results);

        }
    }

//    @Override
//    protected boolean isValidFragment(String fragmentName) {
//        if(PrefsActivity.class.getName().equals(fragmentName)){
//            return true;
//        }
//        if(PrefsFragmentBlank.class.getName().equals(fragmentName)){
//            return true;
//        }
//        if(PrefsFragmentGeneral.class.getName().equals(fragmentName)){
//            return true;
//        }
//        return false;
//    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected boolean isValidFragment(String fragmentName) {
//            return PrefsActivity.class.getName().equals(fragmentName)
//                    || PrefsFragmentGeneral.class.getName().equals(fragmentName)
//                    || PrefsFragmentBlank.class.getName().equals(fragmentName)
//                    || super.isValidFragment(fragmentName);
        return true;

    }

}
