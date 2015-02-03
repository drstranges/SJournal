package com.drprog.sjournal.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.drprog.sjournal.blank.BlankFragment;
import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.IOFiles;
import com.drprog.sjournal.utils.RunUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Romka on 10.08.2014.
 */
public class BackupDbDialog extends DialogFragment {
    public static final int DIALOG_BACKUP = 1;
    private int dialogType = DIALOG_BACKUP;
    public static final int DIALOG_RESTORE = 2;
    protected static final String DIALOG_TYPE = "dialogType";

    public static BackupDbDialog newInstance(int dialogType) {
        BackupDbDialog dialog = new BackupDbDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_TYPE, dialogType);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogType = getArguments().getInt(DIALOG_TYPE);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (dialogType == DIALOG_RESTORE) {
            return dlgRestore(savedInstanceState);
        }
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = null;
        if (dialogType == DIALOG_BACKUP) {
            v = dlgBackup(inflater, container, savedInstanceState);
        }
        return v;
    }

    private Dialog dlgRestore(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setTitle(R.string.dialog_database_restore_title);
        final IOFiles ioFiles = new IOFiles(getActivity());

        final List<File> fileList = ioFiles.searchFile(IOFiles.DIR_BACKUP, IOFiles.FILE_BACKUP_EXT);
        if (fileList != null) {
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(getActivity(), R.layout.item_list,
                                             IOFiles.extractFileNames(
                                                     fileList));
            adb.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BlankFragment blankFragment = (BlankFragment)
                            getFragmentManager().findFragmentByTag(BlankFragment.TAG);
                    if (blankFragment != null) blankFragment.clearBlank();
                    SQLiteJournalHelper.getInstance(getActivity(), true).forceClose();
                    ioFiles.restoreDB(SQLiteJournalHelper.DB_NAME, IOFiles.DIR_BACKUP,
                                      fileList.get(which).getName());
                    SQLiteJournalHelper.getInstance(getActivity(), true);
                    if (blankFragment != null) blankFragment.refreshBlank();
                }
            });
        } else {
            adb.setMessage(R.string.dialog_database_backup_list_empty);
        }
        return adb.create();
    }

    private View dlgBackup(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (getDialog() != null) getDialog().setTitle(R.string.dialog_database_backup_title);
        final View v = inflater.inflate(R.layout.dialog_edit_ok_cancel, container, false);

        final EditText editText = (EditText) v.findViewById(R.id.edit);
        editText.setHint(R.string.dialog_database_backup_edit_hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        Button btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = editText.getText().toString();
                if ((fileName == null) || (fileName.equals(""))) {
                    editText.setError(getActivity().getResources().getString(
                            R.string.error_entry_not_specify));
                    return;
                }
                if (fileName.charAt(0) == ' ') {
                    editText.setError(getActivity().getResources().getString(
                            R.string.error_entry_start_with_space));
                    return;
                }
                fileName = fileName + IOFiles.FILE_BACKUP_EXT;
                IOFiles ioFiles = new IOFiles(getActivity());
                if (!IOFiles.isExternalStorageWritable()) {
                    RunUtils.showToast(getActivity(), R.string.error_ext_storage_not_writable);
                    return;
                }
                if (!ioFiles.isFileExists(IOFiles.DIR_BACKUP, fileName)) {
                    editText.setError(
                            getActivity().getResources().getString(R.string.error_file_exists));
                    return;
                }
                SQLiteJournalHelper.getInstance(getActivity()).close();
                ioFiles.exportDB(SQLiteJournalHelper.DB_NAME, IOFiles.DIR_BACKUP, fileName);
                RunUtils.showToast(getActivity(), R.string.db_save_ok);
                dismiss();
            }

        });

        return v;
    }
}
