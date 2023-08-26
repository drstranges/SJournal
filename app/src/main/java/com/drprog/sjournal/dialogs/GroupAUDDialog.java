package com.drprog.sjournal.dialogs;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.TableGroups;
import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.dialogs.utils.BaseAUDDialog;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.RunUtils;

import java.util.List;

/**
 * Created by Romka on 01.08.2014.
 */
public class GroupAUDDialog extends BaseAUDDialog {

    private static final String ARG_GROUP_ID = "argGroupId";

    private Long argGroupId;

    public static GroupAUDDialog newInstance(Fragment clickListener, Integer requestCode,
            int dialogType,
            Long groupId) {
        GroupAUDDialog dialog = new GroupAUDDialog();
        if (clickListener != null && requestCode != null) {
            dialog.setTargetFragment(clickListener, requestCode);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_TYPE, dialogType);
        if (groupId != null) { bundle.putLong(ARG_GROUP_ID, groupId); }
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_GROUP_ID)) {
            argGroupId = getArguments().getLong(ARG_GROUP_ID);
        }
    }

    @Override
    protected View dlgAdd(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        //Set Title
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_group_add_title);
        }
        //Inflate Layout
        View v = inflater.inflate(R.layout.dialog_edit_ok_cancel, container, false);
        //Find Views
        final EditText viewGroupCode = (EditText) v.findViewById(R.id.edit);
        Button btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //Set Hints
        viewGroupCode.setHint(R.string.dialog_group_add_hint);
        //Query DB
        final SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        final List<String> groupCodeList = dbHelper.groups.getAllInColumn(TableGroups.KEY_CODE);
        //Set Listeners
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupCode = viewGroupCode.getText().toString();
                if (checkEntry(viewGroupCode, groupCode, groupCodeList, null)) {
                    long res = dbHelper.groups.insert(new StudyGroup(null, groupCode));
                    if (res == -1) {
                        showNotify(null, R.string.error);
                    } else {
                        dismiss();
                    }
                }
            }
        });
        return v;
    }

    @Override
    protected View dlgUpdate(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        //Set Title
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_group_update_title);
        }
        //Inflate Layout
        final View v = inflater.inflate(R.layout.dialog_spinner_edit_ok_cancel, container, false);
        //Find Views
        final Spinner spinnerGroupOld = (Spinner) v.findViewById(R.id.spinner);
        final EditText viewGroupNew = (EditText) v.findViewById(R.id.edit);
        Button btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //Set Hints
        viewGroupNew.setHint(R.string.dialog_group_update_hint);
        //Query DB
        final SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        final List<StudyGroup> groupList = dbHelper.groups.getAll(TableGroups.KEY_CODE);
        final List<String> codeList = dbHelper.groups.getAllInColumn(TableGroups.KEY_CODE);
        if (groupList.isEmpty()) {
            showNotify(null, R.string.list_empty);
        }
        //Set Adapters
        ArrayAdapter<StudyGroup> groupCodeAdapter =
                new ArrayAdapter<StudyGroup>(getActivity(), android.R.layout.simple_spinner_item,
                                             groupList);
        groupCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroupOld.setAdapter(groupCodeAdapter);

        int pos = getJournalItemPos(argGroupId, groupList);
        if (pos >= 0) spinnerGroupOld.setSelection(pos);


        //Set Listeners
        spinnerGroupOld.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem() != null) {
                    StudyGroup studyGroup = (StudyGroup) parent.getSelectedItem();
                    viewGroupNew.setText(studyGroup.getCode());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupCode = viewGroupNew.getText().toString();
                StudyGroup selectedItem = (StudyGroup) spinnerGroupOld.getSelectedItem();
                if (selectedItem != null) {
                    if (checkEntry(viewGroupNew, groupCode, codeList, selectedItem.getCode())) {
                        selectedItem.setCode(groupCode);
                        long res = dbHelper.groups.update(selectedItem.getId(), selectedItem);
                        if (res <= 0) {
                            showNotify(null, R.string.error);
                        } else {
                            sendCallbackOk();
                            dismiss();
                        }

                    }
                }
            }
        });

        return v;
    }


    @Override
    protected View dlgDelete(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        //Set Title
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_group_delete_title);
        }
        //Inflate Layout
        final View v =
                inflater.inflate(R.layout.dialog_spinner_checkbox_ok_cancel, container, false);
        //Find Views
        final Spinner spinnerGroup = (Spinner) v.findViewById(R.id.spinner);
        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
        Button btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        checkBox.setText(R.string.dialog_group_delete_with_students);
        //Query DB
        final SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        final List<StudyGroup> groupList = dbHelper.groups.getAll(TableGroups.KEY_CODE);
        if (groupList.isEmpty()) {
            showNotify(null, R.string.list_empty);
        }
        //Set Adapters
        ArrayAdapter<StudyGroup> groupCodeAdapter =
                new ArrayAdapter<StudyGroup>(getActivity(), android.R.layout.simple_spinner_item,
                                             groupList);
        groupCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(groupCodeAdapter);

        int pos = getJournalItemPos(argGroupId, groupList);
        if (pos >= 0) spinnerGroup.setSelection(pos);
        //Set Listeners
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudyGroup group = (StudyGroup) spinnerGroup.getSelectedItem();
                if (group == null) return;
                final long groupId = group.getId();
                final List<Student> studentList =
                        dbHelper.students.getStudentsByGroupId(groupId);
                if (studentList.isEmpty()) {
                    long res = dbHelper.groups.delete(groupId);
                    if (res <= 0) {
                        RunUtils.showToast(getActivity(), R.string.error);
                    } else { sendCallbackOk(); }
                    dismiss();
                } else {
                    if (!checkBox.isChecked()) {
                        RunUtils.showToast(getActivity(), R.string.error_group_contains_students);
                    } else {
                        DialogInterface.OnClickListener onClickListener =
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        long res = -1;
                                        dbHelper.getWritableDatabase().beginTransaction();
                                        try {
                                            for (Student student : studentList) {
                                                long studentId = student.getId();
                                                List<StudyGroup> studentGroupsList =
                                                        dbHelper.groups
                                                                .getGroupsByStudentId(studentId);
                                                if (studentGroupsList.size() <= 1) {
                                                    dbHelper.students.delete(studentId);
                                                } else {
                                                    dbHelper.students
                                                            .deleteStudentFromGroup(studentId,
                                                                                    groupId);
                                                }
                                            }
                                            if (dbHelper.students
                                                    .getStudentsByGroupId(groupId).isEmpty()) {
                                                res = dbHelper.groups.delete(groupId);
                                            }
                                            if (res > 0) {
                                                dbHelper.getWritableDatabase()
                                                        .setTransactionSuccessful();
                                            }
                                        } finally {
                                            dbHelper.getWritableDatabase().endTransaction();
                                            //TODO: Delete group this in the loop...
                                        }
                                        if (res <= 0) {
                                            RunUtils.showToast(getActivity(), R.string.error);
                                        } else {
                                            sendCallbackOk();
                                            dismiss();
                                        }
                                    }
                                };
                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.dialog_group_delete_alert_delete_students)
                                .setPositiveButton(android.R.string.ok, onClickListener)
                                .setNegativeButton(android.R.string.cancel, null)
                                .setCancelable(false)
                                .create().show();

                    }

                }
            }
        });

        return v;
    }
}
