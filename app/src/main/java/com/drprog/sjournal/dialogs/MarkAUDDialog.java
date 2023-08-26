package com.drprog.sjournal.dialogs;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TabHost;

import com.drprog.sjournal.blank.BlankTagHandler;
import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.db.entity.StudyClass;
import com.drprog.sjournal.db.entity.StudyMark;
import com.drprog.sjournal.dialogs.utils.BaseAUDDialog;
import com.drprog.sjournal.dialogs.utils.BaseChoiceAdapter;
import com.drprog.sjournal.dialogs.utils.MarkChoiceAdapter;
import com.drprog.sjournal.preferences.PrefsManager;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.RunUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Romka on 01.08.2014.
 */
public class MarkAUDDialog extends BaseAUDDialog {
    //private static final String ARG_MARK_ID = "argMarkId";
    private static final String ARG_CLASS_ID = "argClassId";
    private static final String ARG_STUDENT_ID = "argStudentId";
    private static final String TAB_TAG_SYMBOL = "TAB_TAG_SYMBOL";
    private static final String TAB_TAG_MARK = "TAB_TAG_MARK";
    private static final String ARG_TAG_HANDLER = "ARG_TAG_HANDLER";

    private Long argClassId;
    private Long argStudentId;

    private TabHost tabHost;
    private GridView gridViewSymbols;
    private GridView gridViewMarks;
    private Button btnDelete;
    private BlankTagHandler tagHandler;


    public static MarkAUDDialog newInstance(Fragment clickListener, Integer requestCode,
            int dialogType, BlankTagHandler tagHandler) { //Long classId, Long studentId) {
        MarkAUDDialog dialog = new MarkAUDDialog();
        if (clickListener != null && requestCode != null) {
            dialog.setTargetFragment(clickListener, requestCode);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_TYPE, dialogType);
        //if (classId != null) { bundle.putLong(ARG_CLASS_ID, classId); }
        //if (studentId != null) { bundle.putLong(ARG_STUDENT_ID, studentId); }
        if (tagHandler != null) { bundle.putSerializable(ARG_TAG_HANDLER, tagHandler); }
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_TAG_HANDLER)) {
            tagHandler = (BlankTagHandler) getArguments().getSerializable(ARG_TAG_HANDLER);
            argClassId = tagHandler.getClassId();
            argStudentId = tagHandler.getStudentId();
        }
//        if (getArguments().containsKey(ARG_CLASS_ID)) {
//            argClassId = getArguments().getLong(ARG_CLASS_ID);
//        }
//        if (getArguments().containsKey(ARG_STUDENT_ID)) {
//            argStudentId = getArguments().getLong(ARG_STUDENT_ID);
//        }

    }


    @Override
    protected View dlgAdd(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (tabHost != null) {
            tabHost.setCurrentTabByTag(TAB_TAG_MARK);
        }
        btnDelete.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    protected View dlgUpdate(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        StudyMark studyMark = dbHelper.marks.get(argStudentId, argClassId);
        if (studyMark != null) {
            switch (studyMark.getType()) {
                case StudyMark.TYPE_NO_MARK:
                    if (tabHost != null) {
                        tabHost.setCurrentTabByTag(TAB_TAG_MARK);
                    }
                    return null;//dlgAdd(rootView,inflater,container,savedInstanceState);
                case StudyMark.TYPE_MARK:
                    if (tabHost != null) {
                        tabHost.setCurrentTabByTag(TAB_TAG_MARK);
                        List itemList =
                                ((MarkChoiceAdapter) gridViewMarks.getAdapter()).getItemList();
                        if (itemList.contains(studyMark.getMark().toString())) {
                            ((MarkChoiceAdapter) gridViewMarks.getAdapter())
                                    .setCheckedPosition(
                                            itemList.indexOf(studyMark.getMark().toString()));
                        } else {
                            ((MarkChoiceAdapter) gridViewMarks.getAdapter())
                                    .setItemAddText(studyMark.getMark().toString());
                            ((MarkChoiceAdapter) gridViewMarks.getAdapter())
                                    .setCheckedPosition(itemList.size() + 1);
                        }
                    }

                    break;
                case StudyMark.TYPE_SYMBOL:
                    if (tabHost != null) {
                        tabHost.setCurrentTabByTag(TAB_TAG_SYMBOL);
                        List itemList =
                                ((MarkChoiceAdapter) gridViewSymbols.getAdapter()).getItemList();
                        if (itemList.contains(studyMark.getSymbol())) {
                            ((MarkChoiceAdapter) gridViewSymbols.getAdapter())
                                    .setCheckedPosition(
                                            itemList.indexOf(studyMark.getSymbol()));
                        } else {
                            ((MarkChoiceAdapter) gridViewSymbols.getAdapter())
                                    .setItemAddText(studyMark.getSymbol());
                            ((MarkChoiceAdapter) gridViewSymbols.getAdapter())
                                    .setCheckedPosition(MarkChoiceAdapter.TYPE_ADD);
                        }
                    }

                    break;
                case StudyMark.TYPE_ABSENT:
                    if (tabHost != null) {
                        tabHost.setCurrentTabByTag(TAB_TAG_MARK);
                        ((MarkChoiceAdapter) gridViewMarks.getAdapter())
                                .setCheckedPosition(MarkChoiceAdapter.TYPE_MORE);
                    }
            }
        }
        return rootView;
    }

    @Override
    protected View dlgDelete(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return rootView;
    }

    @Override
    protected View dlgAUD(int dialogType, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (argStudentId == null || argClassId == null) return null;
        final SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        Student student = dbHelper.students.get(argStudentId);
        StudyClass studyClass = dbHelper.classes.get(argClassId);
        if (getDialog() != null) {
            final StringBuilder sb = new StringBuilder(student.getLastName());
            sb.append(' ');
            sb.append(student.getFirstName().charAt(0)).append(". ");
            if (student.getMiddleName() != null) {
                sb.append(student.getMiddleName().charAt(0)).append('.');
            }
            sb.append(" - ").append(studyClass.getAbbr());
            sb.append(" (").append(studyClass.getDate()).append(")");
            getDialog().setTitle(sb.toString());
        }
        //Inflate Layout
        View v = inflater.inflate(R.layout.dialog_mark, container, false);
        //Find Views
        btnDelete = (Button) v.findViewById(R.id.button_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int res = deleteMark();
                if (res > 0) {
                    tagHandler.setCeilType(BlankTagHandler.CeilType.NO_MARK);
                    tagHandler.setText("");
                    sendCallbackOk(tagHandler);
//                    sendCallbackOk(new BlankTagHandler(BlankTagHandler.CeilType.NO_MARK,
//                                                       argClassId, argStudentId, ""));
                }
            }
        });
        tabHost = (TabHost) v.findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec(TAB_TAG_SYMBOL);
        tabSpec.setIndicator(getActivity().getResources().getString(R.string.mark_note));
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec(TAB_TAG_MARK);
        tabSpec.setIndicator(getActivity().getResources().getString(R.string.mark));
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);

        gridViewMarks = (GridView) v.findViewById(R.id.gridViewMarks);
        gridViewSymbols = (GridView) v.findViewById(R.id.gridViewSymbols);
        fillGridView(gridViewMarks, PrefsManager.PREFS_MARK_SCALE_USER_SET,
                     getActivity().getResources().getString(R.string.mark_scale_set_default), true);
        fillGridView(gridViewSymbols, PrefsManager.PREFS_SYMBOL_USER_SET,
                     getActivity().getResources().getString(R.string.symbol_set_default), false);

        gridViewSymbols.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id >= 0) {
                    String symbol = parent.getItemAtPosition(position).toString();
                    long res = createMark(
                            new StudyMark(null, argStudentId, argClassId, StudyMark.TYPE_SYMBOL,
                                          null, symbol, null)
                    );
                    if (res >= 0) {
                        tagHandler.setCeilType(BlankTagHandler.CeilType.SYMBOL);
                        tagHandler.setText(symbol);
                        sendCallbackOk(tagHandler);
//                                new BlankTagHandler(BlankTagHandler.CeilType.SYMBOL,
//                                                           argClassId, argStudentId, symbol));
                    }
                } else if (id == BaseChoiceAdapter.TYPE_ADD) {
                    showSymbolChoiceDialog();
                }
            }
        });
        gridViewMarks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id >= 0) {
                    String symbol = parent.getItemAtPosition(position).toString();
                    Integer mark = RunUtils.tryParse(symbol);
                    if (mark == null) return;
                    long res = createMark(
                            new StudyMark(null, argStudentId, argClassId, StudyMark.TYPE_MARK,
                                          mark, null, null));
                    if (res >= 0) {
                        tagHandler.setCeilType(BlankTagHandler.CeilType.MARK);
                        tagHandler.setText(symbol);
                        sendCallbackOk(tagHandler);
//                                new BlankTagHandler(BlankTagHandler.CeilType.MARK,
//                                                           argClassId, argStudentId, symbol));
                    }
                } else if (id == BaseChoiceAdapter.TYPE_ADD) {
                    showMarkChoiceDialog();
                } else if (id == BaseChoiceAdapter.TYPE_MORE) {
                    long res = createMark(
                            new StudyMark(null, argStudentId, argClassId, StudyMark.TYPE_ABSENT,
                                          null, null, null));
                    if (res >= 0) {
                        String absentSymbol = PrefsManager.getInstance(getActivity()).getPrefs()
                                .getString(PrefsManager.PREFS_BLANK_ABSENT_SYMBOL,
                                           getActivity().getResources()
                                                   .getString(R.string.symbol_absent));
                        tagHandler.setCeilType(BlankTagHandler.CeilType.ABSENT);
                        tagHandler.setText(absentSymbol);
                        sendCallbackOk(tagHandler);
//                                new BlankTagHandler(BlankTagHandler.CeilType.ABSENT,
//                                                           argClassId, argStudentId, absentSymbol));
                    }
                }
            }
        });

        return v;
    }


    private void showSymbolChoiceDialog() {
        String defaultSymbol = null;
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        StudyMark studyMark = dbHelper.marks.get(argStudentId, argClassId);
        if (studyMark != null) defaultSymbol = studyMark.getSymbol();
        final EditText editText = new EditText(getActivity());
        editText.setId(android.R.id.edit);
        editText.setHint(R.string.dialog_mark_symbol_enter_hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        if (defaultSymbol != null) editText.setText(defaultSymbol);
        DialogInterface.OnClickListener onClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String symbol = editText.getText().toString();
                        if (symbol != null && !symbol.equals("") && !symbol.equals(" ")) {
                            long res = createMark(new StudyMark(null, argStudentId, argClassId,
                                                                StudyMark.TYPE_SYMBOL,
                                                                null, symbol, null));
                            if (res >= 0) {
                                tagHandler.setCeilType(BlankTagHandler.CeilType.SYMBOL);
                                tagHandler.setText(symbol);
                                sendCallbackOk(tagHandler);
//                                sendCallbackOk(new BlankTagHandler(BlankTagHandler.CeilType.SYMBOL,
//                                                                  argClassId, argStudentId, symbol));
                                dismiss();
                            }
                        }
                    }
                };

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.dialog_mark_symbol_enter_title)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, onClickListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }

    private void showMarkChoiceDialog() {
        String defaultSymbol = null;
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        StudyMark studyMark = dbHelper.marks.get(argStudentId, argClassId);
        if (studyMark != null && studyMark.getType() == StudyMark.TYPE_MARK) {
            defaultSymbol = studyMark.getMark().toString();
        }
        final EditText editText = new EditText(getActivity());
        editText.setId(android.R.id.edit);
        editText.setHint(R.string.dialog_mark_mark_enter_hint);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (defaultSymbol != null) editText.setText(defaultSymbol);
        DialogInterface.OnClickListener onClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String symbol = editText.getText().toString();
                        if (symbol != null && !symbol.equals("") && !symbol.equals(" ")) {
                            Integer mark = RunUtils.tryParse(symbol);
                            if (mark == null) return;
                            long res = createMark(new StudyMark(null, argStudentId, argClassId,
                                                                StudyMark.TYPE_MARK,
                                                                mark, null, null));
                            if (res >= 0) {
                                tagHandler.setCeilType(BlankTagHandler.CeilType.MARK);
                                tagHandler.setText(symbol);
                                sendCallbackOk(tagHandler);
//                                sendCallbackOk(new BlankTagHandler(BlankTagHandler.CeilType.MARK,
//                                                                   argClassId, argStudentId,
//                                                                   symbol));
                                dismiss();
                            }
                        }
                    }
                };

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.dialog_mark_mark_enter_title)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, onClickListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }

    private int deleteMark() {
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        StudyMark studyMark = dbHelper.marks.get(argStudentId, argClassId);
        if (studyMark != null) {
            int res = dbHelper.marks.delete(studyMark.getId());
            if (res == -1) {
                showNotify(null, R.string.error);
            }
            return res;
        }
        return -1;
    }

    private long createMark(StudyMark studyMark) {
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        if (studyMark != null) {
            deleteMark();
            long res = dbHelper.marks.insert(studyMark);
            if (res == -1) {
                showNotify(null, R.string.error);
            }
            return res;
        }
        return -1;
    }


    private void fillGridView(GridView gridView, String tagPrefsUserSet, String defaultSet,
            boolean isMore) {
        String prefsString = PrefsManager.getInstance(getActivity()).getPrefs()
                .getString(tagPrefsUserSet, defaultSet);
        if (prefsString != null) {
            List<String> itemList;
            if (!prefsString.equals("")) {
                itemList = Arrays.asList(prefsString.split(","));
            } else { itemList = new ArrayList<String>(); }
            MarkChoiceAdapter adapter =
                    new MarkChoiceAdapter(getActivity(), itemList, isMore);
            gridView.setAdapter(adapter);
        }
    }



}
