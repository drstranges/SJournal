package com.drprog.sjournal.dialogs.utils;

import android.os.Bundle;

/**
 * Created by Romka on 26.07.2014.
 */
public interface DialogClickListener {
    public static final String KEY_CALLBACK_BUNDLE_TAG = "KEY_CALLBACK_BUNDLE_TAG";

    public void onDialogResult(int requestCode, DialogResultCode resultCode, Bundle bundle);

    public enum DialogResultCode {RESULT_OK, RESULT_NO, RESULT_CANCEL, RESULT_MIDDLE}
}
