package com.drprog.sjournal.dialogs.utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * Created by Romka on 13.08.2014.
 */
public abstract class BaseAsyncProgressDialogFragment extends DialogFragment {
    public static final String ARG_PROGRESS_STYLE = "ARG_PROGRESS_STYLE";
    public static final String ARG_MAX_PROGRESS = "ARG_MAX_PROGRESS";
    public static final String ARG_IS_CANCELABLE = "ARG_IS_CANCELABLE";

    private AsyncTask myTask;
    //private boolean isAttached = false;

    private Integer argProgressStyle = ProgressDialog.STYLE_SPINNER;
    private Integer argMaxProgress = 100;
    private boolean argIsCancelable = false;

//    public static BaseAsyncProgressDialogFragment newInstance(Integer argProgressStyle, Integer argMaxProgress, boolean argIsCancelable){
//        BaseAsyncProgressDialogFragment dialogFragment = new BaseAsyncProgressDialogFragment();
//        setProgressArg(dialogFragment, argProgressStyle, argMaxProgress, argIsCancelable);
//        return dialogFragment;
//    }

    protected static void setArguments(DialogFragment dialog, Bundle bundle,
            Integer argProgressStyle,
            Integer argMaxProgress, boolean argIsCancelable) {
        if (bundle == null) bundle = new Bundle();
        if (argProgressStyle != null) {
            bundle.putInt(ARG_PROGRESS_STYLE, argProgressStyle);
        }
        if (argMaxProgress != null) {
            bundle.putInt(ARG_MAX_PROGRESS, argMaxProgress);
        }
        bundle.putBoolean(ARG_IS_CANCELABLE, argIsCancelable);
        dialog.setArguments(bundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_PROGRESS_STYLE)) {
            argProgressStyle = getArguments().getInt(ARG_PROGRESS_STYLE);
        }
        if (getArguments().containsKey(ARG_MAX_PROGRESS)) {
            argMaxProgress = getArguments().getInt(ARG_MAX_PROGRESS);
        }
        if (getArguments().containsKey(ARG_IS_CANCELABLE)) {
            argIsCancelable = getArguments().getBoolean(ARG_IS_CANCELABLE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(false);
        //progressDialog.setCancelable(argIsCancelable);
        progressDialog.setProgressStyle(argProgressStyle);
        progressDialog.setMax(argMaxProgress);
        setCancelable(argIsCancelable);
        return progressDialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (myTask == null) {
            myTask = getNewTaskExecute();
        }
        setRetainInstance(true);
    }

    protected abstract AsyncTask getNewTaskExecute();

    public void setProgress(Integer progress) {
        if (progress == null) return;
        if (getDialog() != null) {
            ((ProgressDialog) getDialog()).setProgress(progress);
        }

    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        isAttached = true;
//
//    }
//
//    @Override
//    public void onDetach() {
//        isAttached = false;
//        super.onDetach();
//    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) { getDialog().setDismissMessage(null); }
        super.onDestroyView();
    }


}
