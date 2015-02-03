package com.drprog.sjournal.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.drprog.sjournal.dialogs.utils.BaseAlertDialogFragment;
import com.drprog.sjournal.dialogs.utils.DialogClickListener;

/**
 * Created by Romka on 25.07.2014.
 */
public class AlertDialogFragment extends BaseAlertDialogFragment {

    public static AlertDialogFragment newInstance(Fragment clickListener, int requestCode,
            String title, String message) {
        return newInstance(clickListener, requestCode, title, message,
                           null, null, true, true, null, null);
    }

    public static AlertDialogFragment newInstance(Fragment clickListener, int requestCode,
            int titleResId, int messageResId) {

        String title = clickListener.getResources().getString(titleResId);
        String message = clickListener.getResources().getString(messageResId);
        return newInstance(clickListener, requestCode, title, message,
                           null, null, true, true, null, null);
    }

    public static AlertDialogFragment newInstance(
            Fragment clickListener, int requestCode,
            int titleResId, int messageResId,
            Bundle bundle, Integer iconResId,
            boolean positiveButtonOn, boolean negativeButtonOn,
            Integer posBtnTitleResId, Integer negBtnTitleResId) {

        String title = clickListener.getResources().getString(titleResId);
        String message = clickListener.getResources().getString(messageResId);
        return newInstance(clickListener, requestCode, title, message,
                           bundle, iconResId, positiveButtonOn, negativeButtonOn,
                           posBtnTitleResId, negBtnTitleResId);
    }

    public static AlertDialogFragment newInstance(
            Fragment clickListener, int requestCode,
            String title, String message,
            Bundle bundle, Integer iconResId,
            boolean positiveButtonOn, boolean negativeButtonOn,
            Integer posBtnTitleResId, Integer negBtnTitleResId) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment.setTargetFragment(clickListener, requestCode);
        Bundle args = new Bundle();
        if (bundle != null) args.putBundle("bundle", bundle);
        if (iconResId != null) args.putInt("iconResId", iconResId);
        if (title != null) args.putString("title", title);
        if (message != null) args.putString("message", message);
        if (posBtnTitleResId != null) args.putInt("posBtnTextResId", posBtnTitleResId);
        if (negBtnTitleResId != null) args.putInt("negBtnTextResId", negBtnTitleResId);
        args.putBoolean("positiveButtonOn", positiveButtonOn);
        args.putBoolean("negativeButtonOn", negativeButtonOn);
        alertDialogFragment.setArguments(args);
        return alertDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        if (iconResId != null) {
            adb.setIcon(iconResId);
        } else { adb.setIcon(android.R.drawable.ic_dialog_alert); }
        if (title != null) { adb.setTitle(title); }
        if (message != null) { adb.setMessage(message); }
        if (positiveButtonOn) {
            int resId = posBtnTextResId != null ? posBtnTextResId : android.R.string.ok;
            adb.setPositiveButton(resId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (callback != null) {
                        callback.onDialogResult(requestCode,
                                                DialogClickListener.DialogResultCode.RESULT_OK,
                                                bundle);
                    }
                }
            });

        }
        if (negativeButtonOn) {
            int resId = negBtnTextResId != null ? negBtnTextResId : android.R.string.cancel;
            adb.setNegativeButton(resId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (callback != null) {
                        callback.onDialogResult(requestCode,
                                                DialogClickListener.DialogResultCode.RESULT_NO,
                                                bundle);
                    }
                }
            });
        }
        return adb.create();
    }
}
