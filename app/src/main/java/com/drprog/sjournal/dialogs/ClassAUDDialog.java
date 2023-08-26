package com.drprog.sjournal.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.TableClasses;
import com.drprog.sjournal.db.entity.StudyClass;
import com.drprog.sjournal.dialogs.utils.BaseAUDDialog;
import com.drprog.sjournal.dialogs.utils.ColorAdapter;
import com.drprog.sjournal.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Romka on 01.08.2014.
 */
public class ClassAUDDialog extends BaseAUDDialog implements DatePickerDialog.OnDateSetListener {
    private static final String ARG_CLASS_ID = "argClassId";
    private static final String ARG_GROUP_ID = "argGroupId";
    private static final String ARG_SUBJECT_ID = "argSubjectId";
    private static final String ARG_CLASS_TYPE_ID = "argClassTypeId";
    private static final String KEY_CALENDAR = "Calendar";
    private static final String KEY_BG_COLOR = "bgColor";
    private static final String KEY_TEXT_COLOR = "textColor";

    public final SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL");
    public final SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    public final SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE");
    public final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private Integer bgColor;
    private Integer textColor;
    private Calendar calendar = Calendar.getInstance();
    private Long argClassId;
    private Long argGroupId;
    private Long argSubjectId;
    private Long argClassTypeId;

    private RelativeLayout layoutDate;
    private TextView viewDay;
    private TextView viewMonth;
    private TextView viewYear;
    private TextView viewDayOfWeek;
    private EditText viewAbbr;
    private EditText viewTheme;
    private EditText viewNote;
    private Button btnColorSelect;
    private Button btnOk;

    public static ClassAUDDialog newInstance(int dialogType,
            Long classId) {
        return newInstance(null, null, dialogType, classId, null, null, null);
    }

    public static ClassAUDDialog newInstance(Fragment clickListener, Integer requestCode,
            int dialogType,
            Long classId, Long groupId, Long subjectId, Long classTypeId) {
        ClassAUDDialog dialog = new ClassAUDDialog();
        if (clickListener != null && requestCode != null) {
            dialog.setTargetFragment(clickListener, requestCode);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_TYPE, dialogType);
        if (classId != null) { bundle.putLong(ARG_CLASS_ID, classId); }
        if (groupId != null) { bundle.putLong(ARG_GROUP_ID, groupId); }
        if (subjectId != null) { bundle.putLong(ARG_SUBJECT_ID, subjectId); }
        if (classTypeId != null) { bundle.putLong(ARG_CLASS_TYPE_ID, classTypeId); }
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_CLASS_ID)) {
            argClassId = getArguments().getLong(ARG_CLASS_ID);
        }
        if (getArguments().containsKey(ARG_GROUP_ID)) {
            argGroupId = getArguments().getLong(ARG_GROUP_ID);
        }
        if (getArguments().containsKey(ARG_SUBJECT_ID)) {
            argSubjectId = getArguments().getLong(ARG_SUBJECT_ID);
        }
        if (getArguments().containsKey(ARG_CLASS_TYPE_ID)) {
            argClassTypeId = getArguments().getLong(ARG_CLASS_TYPE_ID);
        }

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
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
            getDialog().setTitle(R.string.dialog_class_add_title);
        }
        btnOk.setText(R.string.create);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String abbr = viewAbbr.getText().toString();
                String title = viewTheme.getText().toString();
                String note = viewNote.getText().toString();
                if (argGroupId == null || argSubjectId == null || argClassTypeId == null) {
                    dismiss();
                    return;
                }
                SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
                int semester = dbHelper.groups.get(argGroupId).getSemester();
                String date = TableClasses.dateFormat.format(calendar.getTime());
                StudyClass studyClass =
                        new StudyClass(null, semester, argGroupId, argSubjectId, argClassTypeId,
                                       date, title, abbr, null, bgColor, textColor, note);
                long res = dbHelper.classes.insert(studyClass);
                if (res < 0) {
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
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_class_update_title);
        }
        btnOk.setText(R.string.update);
        fillForm();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String abbr = viewAbbr.getText().toString();
                String theme = viewTheme.getText().toString();
                String note = viewNote.getText().toString();
                if (argClassId == null) {
                    dismiss();
                    return;
                }
                if (!checkEntry(viewAbbr, abbr, null, null)) return;
                SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
                StudyClass studyClass = dbHelper.classes.get(argClassId);
                String date = TableClasses.dateFormat.format(calendar.getTime());
                studyClass.setDate(date);
                studyClass.setTheme(theme);
                studyClass.setAbbr(abbr);
                if (bgColor != null) { studyClass.setRowColor(bgColor); }
                if (bgColor != null) { studyClass.setTextColor(textColor); }
                studyClass.setNote(note);
                long res = dbHelper.classes.update(argClassId, studyClass);
                if (res < 0) {
                    showNotify(null, R.string.error);
                } else {
                    sendCallbackOk();
                    dismiss();
                }
            }
        });
        return rootView;
    }

    private void fillForm() {
        if (argClassId == null) return;
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        StudyClass studyClass = dbHelper.classes.get(argClassId);
        viewAbbr.setText(studyClass.getAbbr());
        viewTheme.setText(studyClass.getTheme());
        viewNote.setText(studyClass.getNote());
        bgColor = studyClass.getRowColor();
        if (bgColor != null) { btnColorSelect.setBackgroundColor(bgColor); }
        textColor = studyClass.getTextColor();
        if (textColor != null) { btnColorSelect.setTextColor(textColor); }
        try {
            Date date = TableClasses.dateFormat.parse(studyClass.getDate());
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setDate();
    }

    @Override
    protected View dlgDelete(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_class_delete_title);
        }
        btnOk.setText(R.string.delete);
        fillForm();
        layoutDate.setEnabled(false);
        btnColorSelect.setEnabled(false);
        viewAbbr.setEnabled(false);
        viewTheme.setEnabled(false);
        viewNote.setEnabled(false);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (argClassId == null) {
                    dismiss();
                    return;
                }

                DialogInterface.OnClickListener onClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteJournalHelper dbHelper =
                                        SQLiteJournalHelper.getWritableInstance(getActivity());
                                long res = dbHelper.classes.delete(argClassId);
                                if (res < 0) {
                                    showNotify(null, R.string.error);
                                } else {
                                    sendCallbackOk();
                                    dismiss();
                                }
                            }
                        };

                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.dialog_class_delete_are_you_sure)
                        .setPositiveButton(android.R.string.ok, onClickListener)
                        .setNegativeButton(android.R.string.cancel, null)
                                //.setCancelable(false)
                        .create().show();

            }
        });
        return rootView;
    }

    @Override
    protected View dlgAUD(int dialogType, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
        //Inflate Layout
        View v = inflater.inflate(R.layout.dialog_class, container, false);
        //Find Views
        //Find Views
        layoutDate = (RelativeLayout) v.findViewById(R.id.layoutDate);
        viewDay = (TextView) v.findViewById(R.id.textViewDay);
        viewMonth = (TextView) v.findViewById(R.id.textViewMonth);
        viewYear = (TextView) v.findViewById(R.id.textViewYear);
        viewDayOfWeek = (TextView) v.findViewById(R.id.textViewDayOfWeek);
        viewAbbr = (EditText) v.findViewById(R.id.editAbbr);
        viewTheme = (EditText) v.findViewById(R.id.editTheme);
        viewNote = (EditText) v.findViewById(R.id.editNote);
        btnColorSelect = (Button) v.findViewById(R.id.button_color);
        btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setDate();

        layoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnColorSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });

        return v;
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR),
                             calendar.get(Calendar.MONTH),
                             calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showColorPickerDialog() {

        final ColorAdapter adapter = new ColorAdapter(getActivity(), R.layout.item_list);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_color_choice)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setColor(adapter.getBgColor(which),
                                             adapter.getTextColor(which));

                                }
                            }
                );
        builder.show();

    }

    private void setColor(int bgColor, int textColor) {
        this.bgColor = bgColor;
        this.textColor = textColor;
        btnColorSelect.setBackgroundColor(bgColor);
        btnColorSelect.setTextColor(textColor);
    }

    private void setDate() {
        Date date = calendar.getTime();
        viewMonth.setText(monthFormat.format(date));
        viewDay.setText(dayFormat.format(date));
        viewDayOfWeek.setText(dayOfWeekFormat.format(date));
        viewYear.setText(yearFormat.format(date));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        setDate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(KEY_CALENDAR, calendar);
        if (bgColor != null) {
            outState.putInt(KEY_BG_COLOR, bgColor);
        }
        if (textColor != null) {
            outState.putInt(KEY_TEXT_COLOR, textColor);
        }

//        if (argClassId != null) {
//            outState.putLong(ARG_CLASS_ID, argClassId);
//        }
//        if (argClassTypeId != null) {
//            outState.putLong(ARG_CLASS_TYPE_ID, argClassTypeId);
//        }
//        if (argSubjectId != null) {
//            outState.putLong(ARG_SUBJECT_ID, argSubjectId);
//        }
//        if (argGroupId != null) {
//            outState.putLong(ARG_GROUP_ID, argGroupId);
//        }

    }

    private void restoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState.containsKey(KEY_CALENDAR)) {
            calendar = (Calendar) savedInstanceState.getSerializable(KEY_CALENDAR);
        }
        if (savedInstanceState.containsKey(KEY_BG_COLOR)) {
            bgColor = savedInstanceState.getInt(KEY_BG_COLOR);
        }
        if (savedInstanceState.containsKey(KEY_TEXT_COLOR)) {
            textColor = savedInstanceState.getInt(KEY_TEXT_COLOR);
        }

//        if (savedInstanceState.containsKey(ARG_CLASS_ID)) {
//            argClassId = savedInstanceState.getLong(ARG_CLASS_ID);
//        }
//        if (savedInstanceState.containsKey(ARG_CLASS_TYPE_ID)) {
//            argClassTypeId = savedInstanceState.getLong(ARG_CLASS_TYPE_ID);
//        }
//        if (savedInstanceState.containsKey(ARG_SUBJECT_ID)) {
//            argSubjectId = savedInstanceState.getLong(ARG_SUBJECT_ID);
//        }
//        if (savedInstanceState.containsKey(ARG_GROUP_ID)) {
//            argGroupId = savedInstanceState.getLong(ARG_GROUP_ID);
//        }
    }
}
