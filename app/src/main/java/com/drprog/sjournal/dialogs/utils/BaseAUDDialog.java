package com.drprog.sjournal.dialogs.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.drprog.sjournal.R;

import java.util.List;

/**
 * Created by Romka on 01.08.2014.
 */
public abstract class BaseAUDDialog extends BaseDialogFragment {
    public static final int DIALOG_ADD = 1;
    protected int dialogType = DIALOG_ADD;
    public static final int DIALOG_UPDATE = 2;
    public static final int DIALOG_DELETE = 3;
    protected static final String DIALOG_TYPE = "dialogType";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogType = getArguments().getInt(DIALOG_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = dlgAUD(dialogType, inflater, container, savedInstanceState);
        switch (dialogType) {
            case DIALOG_ADD:
                v = dlgAdd(v, inflater, container, savedInstanceState);
                break;
            case DIALOG_UPDATE:
                v = dlgUpdate(v, inflater, container, savedInstanceState);
                break;
            case DIALOG_DELETE:
                v = dlgDelete(v, inflater, container, savedInstanceState);
                break;
            default:
                v = super.onCreateView(inflater, container, savedInstanceState);
        }
        return v;
    }

    protected View dlgAUD(int dialogType, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return null;
    }

    protected abstract View dlgAdd(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState);

    protected abstract View dlgUpdate(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState);

    protected abstract View dlgDelete(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState);

    //====================== Utils ================================================================
    protected boolean checkEntry(EditText v, String entry, List checkList, String entrySkip) {
        if ((entry == null) || (entry.isEmpty())) {
            showNotify(v, R.string.error_entry_not_specify);
            return false;
        }
        if (entry.charAt(0) == ' ') {
            showNotify(v, R.string.error_entry_start_with_space);
            return false;
        }
        if (checkList != null) {
            if (checkList.contains(entry)) {
                if ((entrySkip == null) || (!entry.equals(entrySkip))) {
                    showNotify(v, R.string.error_entry_exists);
                    return false;
                }
            }
        }
        return true;
    }

}
