package com.drprog.sjournal.dialogs.utils;

import android.app.DialogFragment;
import android.os.Bundle;


/**
 * Created by Romka on 28.07.2014.
 */
public class BaseAlertDialogFragment extends DialogFragment {
    protected DialogClickListener callback;
    protected int requestCode;
    protected Integer iconResId;
    protected String title;
    protected String message;
    protected Integer posBtnTextResId;
    protected Integer negBtnTextResId;
    protected Bundle bundle;
    protected boolean positiveButtonOn;
    protected boolean negativeButtonOn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (DialogClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Calling fragment must implement DialogClickListener interface");
        }
        requestCode = getTargetRequestCode();
        if (getArguments().containsKey("bundle")) { bundle = getArguments().getBundle("bundle"); }
        if (getArguments().containsKey("iconResId")) {
            iconResId = getArguments().getInt("iconResId");
        }
        if (getArguments().containsKey("title")) { title = getArguments().getString("title"); }
        if (getArguments().containsKey("message")) {
            message = getArguments().getString("message");
        }
        if (getArguments().containsKey("posBtnTextResId")) {
            posBtnTextResId = getArguments().getInt("posBtnTextResId");
        }
        if (getArguments().containsKey("negBtnTextResId")) {
            negBtnTextResId = getArguments().getInt("negBtnTextResId");
        }
        positiveButtonOn = getArguments().getBoolean("positiveButtonOn", true);
        negativeButtonOn = getArguments().getBoolean("negativeButtonOn", true);
    }

}
