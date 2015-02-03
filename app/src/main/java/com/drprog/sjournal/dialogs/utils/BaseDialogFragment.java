package com.drprog.sjournal.dialogs.utils;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.EditText;

import com.drprog.sjournal.db.utils.BaseJournalEntity;
import com.drprog.sjournal.utils.RunUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Romka on 13.08.2014.
 */
public abstract class BaseDialogFragment extends DialogFragment {


    protected DialogClickListener callback;
    protected Integer requestCode;

    protected static int getJournalItemPos(Long id, List list) {
        if ((list != null) && (id != null) && (!list.isEmpty())) {
            for (int i = 0; i < list.size(); i++) {
                if (((BaseJournalEntity) list.get(i)).getId().equals(id)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getTargetFragment() != null) {
            try {
                callback = (DialogClickListener) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException(
                        "Calling fragment must implement DialogClickListener interface");
            }
            requestCode = getTargetRequestCode();
        }
    }

    protected DialogClickListener getCallback() {
        return callback;
    }

    protected Integer getRequestCode() {
        return requestCode;
    }

    protected void sendCallbackOk() {
        sendCallbackOk((Serializable) null);
    }

    protected void sendCallbackOk(Serializable tag) {
        if (callback != null) {
            Bundle bundle = new Bundle();
            if (tag != null) {
                bundle.putSerializable(DialogClickListener.KEY_CALLBACK_BUNDLE_TAG, tag);
            }
            callback.onDialogResult(requestCode,
                                    DialogClickListener.DialogResultCode.RESULT_OK,
                                    bundle);
        }
        dismiss();
    }

    protected void sendCallbackOk(Parcelable tag) {
        if (callback != null) {
            Bundle bundle = new Bundle();
            if (tag != null) bundle.putParcelable(DialogClickListener.KEY_CALLBACK_BUNDLE_TAG, tag);
            callback.onDialogResult(requestCode,
                                    DialogClickListener.DialogResultCode.RESULT_OK,
                                    bundle);
        }
        dismiss();
    }

    //====================== Utils ================================================================
    protected void showNotify(EditText v, int resId) {
        if (v != null) { v.setError(getResources().getString(resId)); } else {
            RunUtils.showToast(getActivity(), resId);
        }
    }

}
