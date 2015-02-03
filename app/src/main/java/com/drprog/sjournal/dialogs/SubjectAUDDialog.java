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
import android.widget.EditText;
import android.widget.Spinner;

import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.TableClasses;
import com.drprog.sjournal.db.TableSubjects;
import com.drprog.sjournal.db.entity.StudySubject;
import com.drprog.sjournal.dialogs.utils.BaseAUDDialog;
import com.drprog.sjournal.R;

import java.util.List;

/**
 * Created by Romka on 01.08.2014.
 */
public class SubjectAUDDialog extends BaseAUDDialog {
    private static final String ARG_SUBJECT_ID = "argSubjectId";

    private Long argSubjectId;


    public static SubjectAUDDialog newInstance(Fragment clickListener, Integer requestCode,
            int dialogType,
            Long subjectId) {
        SubjectAUDDialog dialog = new SubjectAUDDialog();
        if (clickListener != null && requestCode != null) {
            dialog.setTargetFragment(clickListener, requestCode);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_TYPE, dialogType);
        if (subjectId != null) { bundle.putLong(ARG_SUBJECT_ID, subjectId); }
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_SUBJECT_ID)) {
            argSubjectId = getArguments().getLong(ARG_SUBJECT_ID);
        }
    }

    @Override
    protected View dlgAdd(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        //Set Title
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_subject_add_title);
        }
        //Inflate Layout
        final View v = inflater.inflate(R.layout.dialog_two_edit_ok_cancel, container, false);
        //Find Views
        final EditText viewAbbr = (EditText) v.findViewById(R.id.edit1);
        final EditText viewTitle = (EditText) v.findViewById(R.id.edit2);
        Button btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //Set Hints
        viewAbbr.setHint(R.string.dialog_subject_add_abbr_hint);
        viewTitle.setHint(R.string.dialog_subject_add_title_hint);
        //Query DB
        final SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
        final List<String> abbrList = dbHelper.subjects.getAllInColumn(TableSubjects.KEY_ABBR);
        final List<String> titleList = dbHelper.subjects.getAllInColumn(TableSubjects.KEY_TITLE);
        //Set Listeners
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String abbr = viewAbbr.getText().toString();
                String title = viewTitle.getText().toString();
                if (checkEntry(viewAbbr, abbr, abbrList, null) &&
                        checkEntry(viewTitle, title, titleList, null)) {
                    long res = dbHelper.subjects.insert(new StudySubject(null, title, abbr, -1));
                    //TODO: Implement choice a picture for Subjects: Picture placeholder and dialog with gridView with pictures.
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
            getDialog().setTitle(R.string.dialog_subject_update_title);
        }
        //Inflate Layout
        final View v =
                inflater.inflate(R.layout.dialog_spinner_two_edit_ok_cancel, container, false);
        //Find Views
        final Spinner spinnerSubjects = (Spinner) v.findViewById(R.id.spinner);
        final EditText viewAbbr = (EditText) v.findViewById(R.id.edit1);
        final EditText viewTitle = (EditText) v.findViewById(R.id.edit2);
        Button btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //Set Hints
        viewAbbr.setHint(R.string.dialog_subject_add_abbr_hint);
        viewTitle.setHint(R.string.dialog_subject_add_title_hint);
        //Query DB
        final SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
        final List<StudySubject> studySubjectList =
                dbHelper.subjects.getAll(TableSubjects.KEY_ABBR);
        final List<String> abbrList =
                dbHelper.subjects.getAllInColumn(TableSubjects.KEY_ABBR);
        final List<String> titleList =
                dbHelper.subjects.getAllInColumn(TableSubjects.KEY_TITLE);
        if (studySubjectList.isEmpty()) {
            showNotify(null, R.string.list_empty);
        }

        // Set Adapters
        ArrayAdapter adapter =
                new ArrayAdapter<StudySubject>(getActivity(), android.R.layout.simple_spinner_item,
                                               studySubjectList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(adapter);

        int pos = getJournalItemPos(argSubjectId, studySubjectList);
        if (pos >= 0) spinnerSubjects.setSelection(pos);

        //Set Listeners
        spinnerSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                    long id) {
                if (parent.getSelectedItem() != null) {
                    StudySubject studySubject = (StudySubject) parent.getSelectedItem();
                    viewAbbr.setText(studySubject.getAbbr());
                    viewTitle.setText(studySubject.getTitle());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String abbr = viewAbbr.getText().toString();
                String title = viewTitle.getText().toString();
                StudySubject selectedItem = (StudySubject) spinnerSubjects.getSelectedItem();
                if (selectedItem != null) {
                    if (checkEntry(viewAbbr, abbr, abbrList, selectedItem.getAbbr()) &&
                            checkEntry(viewTitle, title, titleList, selectedItem.getTitle())) {
                        selectedItem.setAbbr(abbr);
                        selectedItem.setTitle(title);
                        long res = dbHelper.subjects.update(selectedItem.getId(), selectedItem);
                        //TODO: Implement choice a picture for Subjects: Picture placeholder and dialog with gridView with pictures.
                        if (res == -1) {
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
            getDialog().setTitle(R.string.dialog_subject_delete_title);
        }
        //Inflate Layout
        final View v = inflater.inflate(R.layout.dialog_spinner_ok_cancel, container, false);
        //Find Views
        final Spinner spinnerSubjects = (Spinner) v.findViewById(R.id.spinner);
        Button btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //Query DB
        final SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
        final List<StudySubject> studySubjectList =
                dbHelper.subjects.getAll(TableSubjects.KEY_ABBR);
        if (studySubjectList.isEmpty()) {
            showNotify(null, R.string.list_empty);
        }
        // Set Adapters
        ArrayAdapter adapter =
                new ArrayAdapter<StudySubject>(getActivity(), android.R.layout.simple_spinner_item,
                                               studySubjectList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(adapter);

        int pos = getJournalItemPos(argSubjectId, studySubjectList);
        spinnerSubjects.setSelection(pos);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StudySubject selectedItem = (StudySubject) spinnerSubjects.getSelectedItem();
                if (selectedItem != null) {

                    if (dbHelper.classes
                            .isClassContain(TableClasses.KEY_SUBJECT_ID, selectedItem.getId())) {
                        DialogInterface.OnClickListener onClickListener =
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        long res = dbHelper.subjects.delete(selectedItem.getId());
                                        if (res == -1) {
                                            showNotify(null, R.string.error);
                                        } else {
                                            sendCallbackOk();
                                            dismiss();
                                        }
                                    }
                                };

                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.dialog_subject_delete_are_you_sure)
                                .setPositiveButton(android.R.string.ok, onClickListener)
                                .setNegativeButton(android.R.string.cancel, null)
                                .setCancelable(false)
                                .create().show();
                    } else {
                        long res = dbHelper.subjects.delete(selectedItem.getId());
                        if (res == -1) {
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

}
