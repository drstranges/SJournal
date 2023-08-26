package com.drprog.sjournal.dialogs;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.TableClasses;
import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.db.entity.StudyClass;
import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.db.entity.StudyMark;
import com.drprog.sjournal.dialogs.utils.BaseDialogFragment;
import com.drprog.sjournal.R;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Romka on 22.08.2014.
 */
public class AddClassWithAbsentDialog extends BaseDialogFragment {
    public static final String TAG = "ABSENT_SELECT_DIALOG";

    private static final String ARG_GROUP_ID = "ARG_GROUP_ID";
    private static final String ARG_SUBJECT_ID = "ARG_SUBJECT_ID";
    private static final String ARG_CLASS_TYPE_ID = "ARG_CLASS_TYPE_ID";
    //private static final String ARG_CLASS_ID = "ARG_CLASS_ID";


    private Long argGroupId;
    private Long argSubjectId;
    private Long argClassTypeId;
    //private Long argClassId;

    private EditText editText;
    private ListView listView;
    private Button btnOk;


    public static AddClassWithAbsentDialog newInstance(Fragment clickListener, Integer requestCode,
            Long groupId, Long subjectId, Long classTypeId) {
        AddClassWithAbsentDialog dialog = new AddClassWithAbsentDialog();
        if (clickListener != null && requestCode != null) {
            dialog.setTargetFragment(clickListener, requestCode);
        }
        Bundle bundle = new Bundle();

        if (groupId != null) { bundle.putLong(ARG_GROUP_ID, groupId); }
        if (subjectId != null) { bundle.putLong(ARG_SUBJECT_ID, subjectId); }
        if (classTypeId != null) { bundle.putLong(ARG_CLASS_TYPE_ID, classTypeId); }
        //if (classId != null) { bundle.putLong(ARG_CLASS_ID, classId); }

        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_GROUP_ID)) {
            argGroupId = getArguments().getLong(ARG_GROUP_ID);
        }
        if (getArguments().containsKey(ARG_SUBJECT_ID)) {
            argSubjectId = getArguments().getLong(ARG_SUBJECT_ID);
        }
        if (getArguments().containsKey(ARG_CLASS_TYPE_ID)) {
            argClassTypeId = getArguments().getLong(ARG_CLASS_TYPE_ID);
        }
//        if (getArguments().containsKey(ARG_CLASS_ID)) {
//            argClassId = getArguments().getLong(ARG_CLASS_ID);
//        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_absent_check, container);
        editText = (EditText) v.findViewById(R.id.edit);
        TextView emptyView = (TextView) v.findViewById(R.id.empty);
        listView = (ListView) v.findViewById(R.id.list);
        listView.setEmptyView(emptyView);
        btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (argGroupId == null || argSubjectId == null || argClassTypeId == null) {
            return;
        }
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        StudyGroup studyGroup = dbHelper.groups.get(argGroupId);
        if (studyGroup == null) return;
        if (getDialog() != null) {
            String dateInLocal =
                    android.text.format.DateFormat.getDateFormat(getActivity())
                            .format(Calendar.getInstance().getTime());
            getDialog().setTitle(dateInLocal);
        }
        final List<Student> studentList = dbHelper.students.getStudentsByGroupId(argGroupId);


        ArrayAdapter<Student> studentArrayAdapter =
                new ArrayAdapter<Student>(getActivity(), R.layout.item_absent_list,
                                          android.R.id.text1,
                                          studentList) {
                    @Override
                    public boolean hasStableIds() {
                        return true;
                    }

                    @Override
                    public long getItemId(int position) {
                        if (position >= 0 && position < getCount()) {
                            return getItem(position).getId();
                        }
                        return -1;
                    }
                };

        listView.setAdapter(studentArrayAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String abbr = editText.getText().toString();
                if (argGroupId == null || argSubjectId == null || argClassTypeId == null) {
                    dismiss();
                    return;
                }
                if ((abbr == null) || (abbr.isEmpty())) {
                    showNotify(editText, R.string.error_entry_not_specify);
                    return;
                }
                if (abbr.charAt(0) == ' ') {
                    showNotify(editText, R.string.error_entry_start_with_space);
                    return;
                }
                SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
                int semester = dbHelper.groups.get(argGroupId).getSemester();
                String date = TableClasses.dateFormat.format(Calendar.getInstance().getTime());
                StudyClass studyClass =
                        new StudyClass(null, semester, argGroupId, argSubjectId, argClassTypeId,
                                       date, null, abbr, null, null, null, null);
                dbHelper.getWritableDatabase().beginTransaction();
                long classId = dbHelper.classes.insert(studyClass);
                boolean res = classId >= 0;
                if (classId >= 0) {
                    listView.getCheckedItemIds();
                    for (long studentId : listView.getCheckedItemIds()) {
                        StudyMark mark = new StudyMark(studentId, classId, StudyMark.TYPE_ABSENT);
                        res = res && (dbHelper.marks.insert(mark) >= 0);
                    }
                }
                if (res) {
                    dbHelper.getWritableDatabase().setTransactionSuccessful();
                } else {
                    showNotify(null, R.string.error);
                }

                dbHelper.getWritableDatabase().endTransaction();

                sendCallbackOk();
                dismiss();
            }
        });
    }
}
