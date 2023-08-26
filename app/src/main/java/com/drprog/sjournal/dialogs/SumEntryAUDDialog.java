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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.TableRules;
import com.drprog.sjournal.db.entity.Rule;
import com.drprog.sjournal.db.entity.StudyClass;
import com.drprog.sjournal.db.entity.StudyClassType;
import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.db.entity.SummaryEntry;
import com.drprog.sjournal.db.utils.CompareSign;
import com.drprog.sjournal.dialogs.utils.BaseAUDDialog;
import com.drprog.sjournal.dialogs.utils.ColorAdapter;
import com.drprog.sjournal.dialogs.utils.MinusPlusAdapter;
import com.drprog.sjournal.dialogs.utils.RuleChoiceAdapter;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.RunUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 01.08.2014.
 */
public class SumEntryAUDDialog extends BaseAUDDialog {
    public static final String TAG = "SUM_ENTRY_AUD_DIALOG";

    private static final String ARG_ENTRY_ID = "argEntryId";
    private static final String ARG_GROUP_ID = "argGroupId";
    private static final String ARG_SUBJECT_ID = "argSubjectId";

    private static final String KEY_RULE_LIST = "RuleList";
    private static final String KEY_CLASS_ID = "ClassId";
    private static final String KEY_CLASS_TYPE_ID = "ClassTypeId";
    private static final String KEY_BG_COLOR = "bgColor";
    private static final String KEY_TEXT_COLOR = "textColor";


    private Long argEntryId;
    private Long argSubjectId;
    private Long argGroupId;

    private Integer bgColor;
    private Integer textColor;
    private Long classTypeId;
    private Long classId;

    //private Spinner spinnerColor;
    private Button btnColorSelect;
    private Spinner spinnerClassType;
    private Spinner spinnerClass;
    private ListView listViewRules;
    private Button btnOk;
    private List<Rule> ruleList;
    private CheckBox checkBoxUseForResult;

    private SQLiteJournalHelper dbHelper;

    public static SumEntryAUDDialog newInstance(int dialogType,
            Long entryId) {
        return newInstance(null, null, dialogType, entryId, null, null);
    }

    public static SumEntryAUDDialog newInstance(Fragment clickListener, Integer requestCode,
            int dialogType,
            Long entryId, Long groupId, Long subjectId) {
        SumEntryAUDDialog dialog = new SumEntryAUDDialog();
        if (clickListener != null && requestCode != null) {
            dialog.setTargetFragment(clickListener, requestCode);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_TYPE, dialogType);
        if (entryId != null) { bundle.putLong(ARG_ENTRY_ID, entryId); }
        if (groupId != null) { bundle.putLong(ARG_GROUP_ID, groupId); }
        if (subjectId != null) { bundle.putLong(ARG_SUBJECT_ID, subjectId); }
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ENTRY_ID)) {
            argEntryId = getArguments().getLong(ARG_ENTRY_ID);
        }
        if (getArguments().containsKey(ARG_GROUP_ID)) {
            argGroupId = getArguments().getLong(ARG_GROUP_ID);
        }
        if (getArguments().containsKey(ARG_SUBJECT_ID)) {
            argSubjectId = getArguments().getLong(ARG_SUBJECT_ID);
        }
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }

//    @Override
//    public void onResume() {
//        if (getDialog() != null) {
//            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            //params.height = WindowManager.LayoutParams.MATCH_PARENT;
//            getDialog().getWindow().setAttributes(params);
//        }
//        super.onResume();
//    }

    @Override
    protected View dlgAdd(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_sum_entry_add_title);
        }
        if (argSubjectId == null || argGroupId == null) return null;
        setClassTypeSpinner(null);

        setRuleListViewAdapter();

        btnOk.setText(R.string.create);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
                StudyGroup studyGroup = dbHelper.groups.get(argGroupId);
                if (studyGroup == null) return;
                SummaryEntry entry =
                        new SummaryEntry(studyGroup.getSemester(), argGroupId, argSubjectId,
                                         classTypeId, classId);
                entry.setCounted(!checkBoxUseForResult.isChecked());
                entry.setRowColor(bgColor);
                entry.setTextColor(textColor);
                long entryId = dbHelper.summaryEntries.insert(entry);
                if (entryId == -1) {
                    showNotify(null, R.string.error);
                    return;
                }
                for (Rule rule : ruleList) {
                    dbHelper.rules.addRuleToEntry(entryId, rule.getId());
                }
                showNotify(null, R.string.sum_entry_created);
                sendCallbackOk();
//                    sendCallbackOk();
//                    dismiss();
            }

        });
        return rootView;
    }


    @Override
    protected View dlgUpdate(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (argEntryId == null) return null;
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_sum_entry_update_title);
        }
        btnOk.setText(R.string.update);
        fillForm();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
                SummaryEntry entry = dbHelper.summaryEntries.get(argEntryId);
                if (entry == null) return;
                StudyGroup studyGroup = dbHelper.groups.get(entry.getGroupId());
                if (studyGroup == null) return;

                entry.setClassTypeId(classTypeId);
                entry.setClassId(classId);
                entry.setCounted(!checkBoxUseForResult.isChecked());
                entry.setRowColor(bgColor);
                entry.setTextColor(textColor);
                dbHelper.getWritableDatabase().beginTransaction();
                dbHelper.rules.deleteAllRulesFromEntry(argEntryId);
                long res = dbHelper.summaryEntries.update(argEntryId, entry);
                if (res <= 0) {
                    showNotify(null, R.string.error);
                } else {
                    for (Rule rule : ruleList) {
                        if (dbHelper.rules.addRuleToEntry(entry.getId(), rule.getId()) < 0) {
                            res = -1;
                        }

                    }
                }
                if (res > 0) {
                    dbHelper.getWritableDatabase().setTransactionSuccessful();
                    showNotify(null, R.string.sum_entry_updated);
                }
                dbHelper.getWritableDatabase().endTransaction();

                sendCallbackOk();
//                    sendCallbackOk();
//                    dismiss();
            }

        });
        return rootView;
    }

    private void fillForm() {
        if (argEntryId == null) return;
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        SummaryEntry summaryEntry = dbHelper.summaryEntries.get(argEntryId);
        if (summaryEntry == null) return;
        bgColor = summaryEntry.getRowColor();
        if (bgColor != null) { btnColorSelect.setBackgroundColor(bgColor); }
        textColor = summaryEntry.getTextColor();
        if (textColor != null) { btnColorSelect.setTextColor(textColor); }
        classTypeId = summaryEntry.getClassTypeId();
        classId = summaryEntry.getClassId();
        setClassTypeSpinner(classTypeId);
        //setClassesSpinner(classId);
        ruleList = dbHelper.rules.getRules(argEntryId);
        setRuleListViewAdapter();
        checkBoxUseForResult.setChecked(!summaryEntry.isCounted());
    }

    @Override
    protected View dlgDelete(View rootView, LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_sum_entry_delete_title);
        }
        btnOk.setText(R.string.delete);
        fillForm();
        btnColorSelect.setEnabled(false);
        spinnerClassType.setEnabled(false);
        spinnerClass.setEnabled(false);
        checkBoxUseForResult.setEnabled(false);
        listViewRules.setEnabled(false);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (argEntryId == null) {
                    dismiss();
                    return;
                }


                dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
                long res = dbHelper.summaryEntries.delete(argEntryId);
                if (res < 0) {
                    showNotify(null, R.string.error);
                } else {
                    showNotify(null, R.string.sum_entry_deleted);
                    sendCallbackOk();
                    dismiss();
                }


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
        View v = inflater.inflate(R.layout.dialog_summ_entry, container, false);
        //Find Views
        TextView emptyView1 = (TextView) v.findViewById(R.id.empty2);
        TextView emptyView2 = (TextView) v.findViewById(R.id.empty3);
        //spinnerColor = (Spinner) v.findViewById(R.id.spinner1);
        btnColorSelect = (Button) v.findViewById(R.id.button_color);
        spinnerClassType = (Spinner) v.findViewById(R.id.spinner2);
        spinnerClassType.setEmptyView(emptyView1);
        spinnerClass = (Spinner) v.findViewById(R.id.spinner3);
        spinnerClass.setEmptyView(emptyView2);
        listViewRules = (ListView) v.findViewById(R.id.listView);
        checkBoxUseForResult = (CheckBox) v.findViewById(R.id.checkBox);

        btnOk = (Button) v.findViewById(R.id.button_ok);
        Button btnCancel = (Button) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnColorSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });
//        final ColorAdapter adapter = new ColorAdapter(getActivity(), android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerColor.setAdapter(adapter);
//
//        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                bgColor = ((ColorAdapter)parent.getAdapter()).getBgColor(position);
//                textColor = ((ColorAdapter)parent.getAdapter()).getTextColor(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        return v;
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

    private void setClassTypeSpinner(Long id) {
        if (argEntryId == null && (argGroupId == null || argSubjectId == null)) return;
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        final StudyClassType classType = new StudyClassType(-1L, getString(R.string.they_all),
                                                            getString(R.string.absent_num), -1);
        final List<StudyClassType> classTypeList =
                dbHelper.classTypes.getAllByFilter(argSubjectId, argGroupId);
        classTypeList.add(0, classType);
        ArrayAdapter<StudyClassType> classTypeAdapter =
                new ArrayAdapter<StudyClassType>(getActivity(),
                                                 android.R.layout.simple_spinner_item,
                                                 classTypeList);
        classTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassType.setAdapter(classTypeAdapter);
        if (id != null) {
            for (int i = 0; i < classTypeList.size(); i++) {
                if (classTypeList.get(i).getId().equals(id)) {
                    spinnerClassType.setSelection(i);
                    break;
                }
            }
        }
        spinnerClassType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == -1) {
                    classTypeId = null;
                } else {
                    classTypeId = classTypeList.get(position).getId();
                    classTypeId = classTypeId == -1 ? null : classTypeId;
                }
                setClassesSpinner(classId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setClassesSpinner(Long id) {
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        if (argEntryId == null && (argGroupId == null || argSubjectId == null)) return;
        if (classTypeId == null || classTypeId == -1L) {
            spinnerClass.setAdapter(null);
            classId = null;
            return;
        }
        StudyGroup studyGroup = dbHelper.groups.get(argGroupId);
        if (studyGroup == null) return;
        StudyClass studyClass = new StudyClass();
        studyClass.setId(-1L);
        studyClass.setAbbr(getString(R.string.absent_num));
        final List<StudyClass> classList = dbHelper.classes
                .getClasses(argGroupId, argSubjectId, classTypeId, studyGroup.getSemester());
        classList.add(0, studyClass);
        ArrayAdapter<StudyClass> classAdapter =
                new ArrayAdapter<StudyClass>(getActivity(), android.R.layout.simple_spinner_item,
                                             classList);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(classAdapter);
        if (id != null) {
            for (int i = 0; i < classList.size(); i++) {
                if (classList.get(i).getId().equals(id)) {
                    spinnerClass.setSelection(i);
                    break;
                }
            }
        }

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == -1) {
                    classId = null;
                } else {
                    classId = classList.get(position).getId();
                    classId = classId == -1 ? null : classId;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setRuleListViewAdapter() {
        if (ruleList == null) ruleList = new ArrayList<Rule>();
        MinusPlusAdapter rulesAdapter =
                new MinusPlusAdapter<Rule>(getActivity(), R.layout.item_list_with_minus_btn,
                                           ruleList,
                                           null, R.layout.item_list_plus);
        listViewRules.setAdapter(rulesAdapter);
        listViewRules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getAdapter().getItemViewType(position)) {
                    case MinusPlusAdapter.TYPE_ADD:
                        showChoiceRuleDialog();
                        break;
                    case MinusPlusAdapter.TYPE_ITEM:
                        break;
                }

            }
        });

    }

    private void showChoiceRuleDialog() {
        dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        final List<Rule> itemList =
                dbHelper.rules.getAll(TableRules.KEY_OPERATOR_ID + ", " + TableRules.KEY_ARGUMENT);
        itemList.removeAll(ruleList);


        //TODO: change to MinusPlusAdapter with deleting rule
        RuleChoiceAdapter listAdapter =
                new RuleChoiceAdapter(getActivity(), R.layout.item_list, itemList, null,
                                      R.layout.item_list_plus);

        DialogInterface.OnClickListener listOnClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= itemList.size()) {
                            //btn plus clicked
                            showAddRuleDialog();
                        } else if (which >= 0) {
                            addRuleInList(itemList.get(which));


                        }
                    }
                };

        RunUtils.getAlertDialog(getActivity(), android.R.drawable.ic_dialog_info,
                                R.string.dialog_choice_rule_title, null, false, false, null,
                                listAdapter, listOnClickListener, null).show();
    }

    private void addRuleInList(Rule rule) {
        if (rule.getId() == null) {
            dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
            rule = dbHelper.rules.getRule(rule);
            if (rule == null) return;
        }
        if (ruleList == null) { ruleList = new ArrayList<Rule>(); }
        if (!ruleList.contains(rule)) {
            ruleList.add(rule);
            ListAdapter listAdapter = listViewRules.getAdapter();
            if (listAdapter != null) {
                ((MinusPlusAdapter) listAdapter).notifyDataSetChanged();
            } else {
                setRuleListViewAdapter();
            }

        }
    }

    private void showAddRuleDialog() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_rule_create, null);
        final EditText editArg = (EditText) view.findViewById(R.id.edit1);
        final EditText editResult = (EditText) view.findViewById(R.id.edit2);
        final Button btnSign = (Button) view.findViewById(R.id.buttonRuleOp1);
        btnSign.setText(CompareSign.GREATER.toString());
        btnSign.setTag(CompareSign.GREATER);
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompareSign sign = (CompareSign) btnSign.getTag();
                sign = sign.getNext(true);
                btnSign.setText(sign.toString());
                if (sign == CompareSign.EQUAL || sign == CompareSign.NOT_EQUAL) {
                    editArg.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    String str = editArg.getText().toString();
                    if (!str.isEmpty() && !RunUtils.isNumeric(str)) {
                        editArg.setText("");
                    }
                    editArg.setInputType(
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }

                btnSign.setTag(sign);

            }
        });


        final DialogInterface.OnClickListener okClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CompareSign sign = (CompareSign) btnSign.getTag();
                        String argS = editArg.getText().toString();
                        String resultS = editResult.getText().toString();
                        if (sign == null) return;
                        if (resultS.equals("")) resultS = "0";
                        if (argS.isEmpty()) {
                            showNotify(null, R.string.error_entry_not_added_values_not_specify);
                            return;
                        }

                        Integer result = RunUtils.tryParse(resultS);
                        if (result == null)return;
                        Rule rule = new Rule(sign, argS, result);
                        dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
                        if (!dbHelper.rules.exists(rule)) {
                            long res = dbHelper.rules.insert(rule);
                            if (res != -1) {
                                showNotify(null, R.string.rule_created_successfully);
                                addRuleInList(rule);
                            } else {
                                showNotify(null, R.string.error);
                            }
                        } else {
                            showNotify(null, R.string.rule_exists);
                            addRuleInList(rule);
                        }

                    }
                };

        AlertDialog dialog = RunUtils.getAlertDialog(getActivity(),
                                                     android.R.drawable.ic_dialog_info,
                                                     R.string.dialog_create_rule_title, null, true,
                                                     true, okClickListener,
                                                     null, null, view);

        dialog.show();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (bgColor != null) {
            outState.putInt(KEY_BG_COLOR, bgColor);
        }
        if (textColor != null) {
            outState.putInt(KEY_TEXT_COLOR, textColor);
        }

        if (classId != null) {
            outState.putLong(KEY_CLASS_ID, classId);
        }
        if (classTypeId != null) {
            outState.putLong(KEY_CLASS_TYPE_ID, classTypeId);
        }
        if (ruleList != null && !ruleList.isEmpty()) {
            outState.putParcelableArrayList(KEY_RULE_LIST,
                                            (ArrayList<? extends android.os.Parcelable>) ruleList);
        }

    }

    private void restoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState.containsKey(KEY_BG_COLOR)) {
            bgColor = savedInstanceState.getInt(KEY_BG_COLOR);
        }
        if (savedInstanceState.containsKey(KEY_TEXT_COLOR)) {
            textColor = savedInstanceState.getInt(KEY_TEXT_COLOR);
        }
        if (savedInstanceState.containsKey(KEY_CLASS_ID)) {
            classId = savedInstanceState.getLong(KEY_CLASS_ID);
        }
        if (savedInstanceState.containsKey(KEY_CLASS_TYPE_ID)) {
            classTypeId = savedInstanceState.getLong(KEY_CLASS_TYPE_ID);
        }
        if (savedInstanceState.containsKey(KEY_RULE_LIST)) {
            ruleList = savedInstanceState.getParcelableArrayList(KEY_RULE_LIST);
        }
    }
}
