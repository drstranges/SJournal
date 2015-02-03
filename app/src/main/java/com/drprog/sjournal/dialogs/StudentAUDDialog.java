package com.drprog.sjournal.dialogs;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;

import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.TableGroups;
import com.drprog.sjournal.db.TableStudents;
import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.dialogs.utils.BaseAUDDialog;
import com.drprog.sjournal.preferences.PrefsManager;
import com.drprog.sjournal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 01.08.2014.
 */
public class StudentAUDDialog extends BaseAUDDialog {

    private static final String ARG_STUDENT_ID = "argStudentId";
    private static final String ARG_GROUP_ID = "argGroupId";

    private Long argStudentId;
    private Long argGroupId;

    private Switch viewSwitchId;
    private EditText viewId;
    private EditText viewLastName;
    private EditText viewFirstName;
    private EditText viewMiddleName;
    private EditText viewGroups;
    private EditText viewEMail;
    private EditText viewPhoneMob;
    private EditText viewPhoneHome;
    private EditText viewNote;
    private Button btnGroupSelect;
    private Button btnOk;
    private final List<StudyGroup> selectedGroupList = new ArrayList<StudyGroup>();
    private List<StudyGroup> allGroupsList;
    private boolean[] checkedItems;


    public static StudentAUDDialog newInstance(Fragment clickListener, Integer requestCode,
            int dialogType,
            Long studentId, Long groupId) {
        StudentAUDDialog dialog = new StudentAUDDialog();
        if (clickListener != null && requestCode != null) {
            dialog.setTargetFragment(clickListener, requestCode);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_TYPE, dialogType);
        if (studentId != null) { bundle.putLong(ARG_STUDENT_ID, studentId); }
        if (groupId != null) { bundle.putLong(ARG_GROUP_ID, groupId); }
        dialog.setArguments(bundle);
        return dialog;
    }

    public static StudentAUDDialog newInstance(Fragment clickListener, Integer requestCode,
            int dialogType,
            Long studentId) {
        return newInstance(clickListener, requestCode, dialogType, studentId, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_STUDENT_ID)) {
            argStudentId = getArguments().getLong(ARG_STUDENT_ID);
        }
        if (getArguments().containsKey(ARG_GROUP_ID)) {
            argGroupId = getArguments().getLong(ARG_GROUP_ID);
        }
    }

    @Override
    public void onResume() {
        if (getDialog() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            //params.height = WindowManager.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setAttributes(params);
        }
        super.onResume();
    }

    @Override
    protected View dlgAdd(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_student_add_title);
        }

        if (argGroupId != null) {
            for (int i = 0; i < checkedItems.length; i++) {
                if (allGroupsList.get(i).getId().equals(argGroupId)) {
                    checkedItems[i] = true;
                    selectedGroupList.add(allGroupsList.get(i));
                    viewGroups.setText(selectedGroupList.toString());
                    break;
                }
            }
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean switchId = viewSwitchId.isChecked();
                String studentIdString = viewId.getText().toString();
                //String groupsString = viewGroups.getText().toString();
                String lastName = viewLastName.getText().toString();
                String firstName = viewFirstName.getText().toString();
                String middleName = viewMiddleName.getText().toString();
                String email = viewEMail.getText().toString();
                String phoneMob = viewPhoneMob.getText().toString();
                String phoneHome = viewPhoneHome.getText().toString();
                String note = viewNote.getText().toString();
                Long id = null;
                SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);

                if (switchId) {
                    if (!checkEntry(viewId, studentIdString, null, null)) {
                        return;
                    } else {
                        id = Long.valueOf(studentIdString);
                        if (dbHelper.students.get(id) != null) {
                            showNotify(viewId, R.string.error_student_id_exist);
                            return;
                        }
                    }
                }

                if (selectedGroupList.isEmpty()) {
                    showNotify(null, R.string.dialog_student_add_group_not_selected);
                    return;
                }

                if (!checkEntry(viewLastName, lastName, null, null)
                        || !checkEntry(viewFirstName, firstName, null, null)) {
                    return;
                }
                if (middleName != null) {
                    if (middleName.equals("")) {
                        middleName = null;
                    } else if (middleName.charAt(0) == ' ') {
                        showNotify(viewMiddleName, R.string.error_entry_start_with_space);
                        return;
                    }
                }
//                    if (email != null) {
//                        if (email.equals("")) {
//                            email = null;
//                        } else if (email.charAt(0) == ' ') {
//                            showNotify(R.string.error_entry_start_with_space);
//                            return;
//                        }
//                    }
//                    if (phoneMob != null) {
//                        if (phoneMob.equals("")) {
//                            phoneMob = null;
//                        } else if (phoneMob.charAt(0) == ' ') {
//                            showNotify(R.string.error_entry_start_with_space);
//                            return;
//                        }
//                    }
//                    if (phoneHome != null) {
//                        if (phoneHome.equals("")) {
//                            phoneHome = null;
//                        } else if (phoneHome.charAt(0) == ' ') {
//                            showNotify(R.string.error_entry_start_with_space);
//                            return;
//                        }
//                    }
//                    if (note != null) {
//                        if (note.equals("")) {
//                            note = null;
//                        } else if (note.charAt(0) == ' ') {
//                            showNotify(R.string.error_entry_start_with_space);
//                            return;
//                        }
//                    }
                Student studentNew =
                        new Student(id, lastName, firstName, middleName, email, phoneMob,
                                    phoneHome,
                                    note);
                long res = dbHelper.students.insert(studentNew);
                if (res > 0) {
                    for (StudyGroup group : selectedGroupList) {
                        dbHelper.students.insertStudentInGroup(res, group.getId());
                    }
                }
                if (res == -1) {
                    showNotify(null, R.string.error);
                } else {
                    sendCallbackOk();
                    dismiss();
                }

            }
        });


        return rootView;
    }

    @Override
    protected View dlgUpdate(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (fillForm()) {
            if (getDialog() != null) {
                getDialog().setTitle(R.string.dialog_student_update_title);
            }
        } else {
            rootView = getStudentChoiceDialog(inflater, container);
        }

        btnOk.setText(R.string.change);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean switchId = viewSwitchId.isChecked();
                String studentIdString = viewId.getText().toString();
                //String groupsString = viewGroups.getText().toString();
                String lastName = viewLastName.getText().toString();
                String firstName = viewFirstName.getText().toString();
                String middleName = viewMiddleName.getText().toString();
                String email = viewEMail.getText().toString();
                String phoneMob = viewPhoneMob.getText().toString();
                String phoneHome = viewPhoneHome.getText().toString();
                String note = viewNote.getText().toString();
                Long id;
                SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);

                if (switchId) {
                    if (!checkEntry(viewId, studentIdString, null, null)) {
                        return;
                    } else {
                        id = Long.valueOf(studentIdString);
                        if (dbHelper.students.get(id) != null) {
                            if (!studentIdString.equals(argStudentId.toString())) {
                                showNotify(viewId, R.string.error_student_id_exist);
                                return;
                            }
                        }
                    }
                } else {
                    id = argStudentId;
                }

                if (selectedGroupList.isEmpty()) {
                    showNotify(null, R.string.dialog_student_add_group_not_selected);
                    return;
                }

                if (!checkEntry(viewLastName, lastName, null, null)
                        || !checkEntry(viewFirstName, firstName, null, null)) {
                    return;
                }
                if (middleName != null) {
                    if (middleName.equals("")) {
                        middleName = null;
                    } else if (middleName.charAt(0) == ' ') {
                        showNotify(viewMiddleName, R.string.error_entry_start_with_space);
                        return;
                    }
                }
//                if (email != null) {
//                    if (email.equals("")) {
//                        email = null;
//                    } else if (email.charAt(0) == ' ') {
//                        showNotify(R.string.error_entry_start_with_space);
//                        return;
//                    }
//                }
//                if (phoneMob != null) {
//                    if (phoneMob.equals("")) {
//                        phoneMob = null;
//                    } else if (phoneMob.charAt(0) == ' ') {
//                        showNotify(R.string.error_entry_start_with_space);
//                        return;
//                    }
//                }
//                if (phoneHome != null) {
//                    if (phoneHome.equals("")) {
//                        phoneHome = null;
//                    } else if (phoneHome.charAt(0) == ' ') {
//                        showNotify(R.string.error_entry_start_with_space);
//                        return;
//                    }
//                }
//                if (note != null) {
//                    if (note.equals("")) {
//                        note = null;
//                    } else if (note.charAt(0) == ' ') {
//                        showNotify(R.string.error_entry_start_with_space);
//                        return;
//                    }
//                }
                Student studentNew =
                        new Student(id, lastName, firstName, middleName, email, phoneMob,
                                    phoneHome,
                                    note);
                long res = dbHelper.students.update(argStudentId, studentNew);
                if ((res > 0) && (!selectedGroupList.isEmpty())) {
                    dbHelper.students.deleteStudentFromAllGroup(studentNew.getId());
                    for (StudyGroup group : selectedGroupList) {
                        dbHelper.students
                                .insertStudentInGroup(studentNew.getId(), group.getId());
                    }
                }
                if (res == -1) {
                    showNotify(null, R.string.error);
                } else {
                    sendCallbackOk();
                    dismiss();
                }

            }
        });

        return rootView;
    }

    @Override
    protected View dlgDelete(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (fillForm()) {
            if (getDialog() != null) {
                getDialog().setTitle(R.string.dialog_student_delete_title);
            }
        } else {
            rootView = getStudentChoiceDialog(inflater, container);
        }

        btnOk.setText(R.string.delete);
        viewSwitchId.setChecked(false);
        viewSwitchId.setEnabled(false);
        viewFirstName.setEnabled(false);
        viewLastName.setEnabled(false);
        viewMiddleName.setEnabled(false);
        viewEMail.setEnabled(false);
        viewPhoneMob.setEnabled(false);
        viewPhoneHome.setEnabled(false);
        viewNote.setEnabled(false);
        btnGroupSelect.setEnabled(false);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener onClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteJournalHelper dbHelper =
                                        SQLiteJournalHelper.getInstance(getActivity(), true);
                                long res = dbHelper.students.delete(argStudentId);
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
                        .setTitle(R.string.dialog_student_delete_are_you_sure)
                        .setPositiveButton(android.R.string.ok, onClickListener)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setCancelable(false)
                        .create().show();
            }
        });


        return rootView;
    }

    @Override
    protected View dlgAUD(int dialogType, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        //Inflate Layout
        View v = inflater.inflate(R.layout.dialog_student, container, false);
        //Find Views
        viewSwitchId = (Switch) v.findViewById(R.id.switchId);
        viewId = (EditText) v.findViewById(R.id.editId);
        viewLastName = (EditText) v.findViewById(R.id.editLastName);
        viewFirstName = (EditText) v.findViewById(R.id.editFirstName);
        viewMiddleName = (EditText) v.findViewById(R.id.editMiddleName);
        viewGroups = (EditText) v.findViewById(R.id.editGroups);
        viewGroups.setSaveEnabled(false);
        viewEMail = (EditText) v.findViewById(R.id.editEMail);
        viewPhoneMob = (EditText) v.findViewById(R.id.editPhoneMob);
        viewPhoneHome = (EditText) v.findViewById(R.id.editPhoneHome);
        viewNote = (EditText) v.findViewById(R.id.editNote);
        btnGroupSelect = (Button) v.findViewById(R.id.buttonGroupsChoice);
        btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ImageButton btnEmail = (ImageButton) v.findViewById(R.id.button_email);
        ImageButton btnPhone1 = (ImageButton) v.findViewById(R.id.button_phone1);
        ImageButton btnPhone2 = (ImageButton) v.findViewById(R.id.button_phone2);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", viewEMail.getText().toString(), null));
                //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
        btnPhone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", viewPhoneMob.getText().toString());
                //smsIntent.putExtra("sms_body","Body of Message");
                startActivity(smsIntent);
            }
        });
        btnPhone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", viewPhoneHome.getText().toString());
                //smsIntent.putExtra("sms_body","Body of Message");
                startActivity(smsIntent);
            }
        });
        //SwitchIdSet
        viewSwitchId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewId.setEnabled(isChecked);
                viewId.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                viewNote.setLines(isChecked ? 4 : 3);
            }
        });
        viewSwitchId.setChecked(PrefsManager.getInstance(getActivity()).getPrefs()
                                        .getBoolean(PrefsManager.PREFS_MANUAL_STUDENT_ID, false));
        final SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
        allGroupsList = dbHelper.groups.getAll(TableGroups.KEY_CODE);
        checkedItems = new boolean[allGroupsList.size()];

        List<String> var = dbHelper.groups.getAllInColumn(TableGroups.KEY_CODE);
        final CharSequence[] groupCodeSequence =
                var.toArray(new CharSequence[var.size()]);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_student_select_groups_title).setCancelable(false)
                .setMultiChoiceItems(groupCodeSequence, checkedItems,
                                     new DialogInterface.OnMultiChoiceClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which,
                                                 boolean isChecked) {

                                         }
                                     }
                )
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedGroupList.clear();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                selectedGroupList.add(allGroupsList.get(i));
                            }
                        }
                        viewGroups.setText(selectedGroupList.toString());
                    }
                });
        btnGroupSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                builder.show();
            }
        });

        return v;
    }

    private boolean fillForm() {
        if (argStudentId != null) {
            SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
            Student student = dbHelper.students.get(argStudentId);
            selectedGroupList.addAll(dbHelper.groups.getGroupsByStudentId(student.getId()));
            viewGroups.setText(selectedGroupList.toString());
            for (int i = 0; i < allGroupsList.size(); i++) {
                checkedItems[i] = selectedGroupList.contains(allGroupsList.get(i));
            }
            viewId.setText(String.valueOf(student.getId()));
            viewLastName.setText(student.getLastName());
            viewFirstName.setText(student.getFirstName());
            viewMiddleName.setText(student.getMiddleName());
            viewEMail.setText(student.getEmail());
            viewPhoneMob.setText(student.getMobilePhone());
            viewPhoneHome.setText(student.getPhone());
            viewNote.setText(student.getNote());
            return true;
        }
        return false;
    }

    private View getStudentChoiceDialog(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_choice, container);
        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
        final List<Student> studentList = dbHelper.students.getAll(TableStudents.KEY_NAME_LAST);
        ArrayAdapter<Student> adapter =
                new ArrayAdapter<Student>(getActivity(), R.layout.item_list, studentList);
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_student_select_title);
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = (Student) parent.getItemAtPosition(position);
                StudentAUDDialog.newInstance((Fragment) getCallback(), getRequestCode(), dialogType,
                                             student.getId())
                        .show(getFragmentManager(), "dialog");
                dismiss();
            }
        });
        return rootView;
    }

}
