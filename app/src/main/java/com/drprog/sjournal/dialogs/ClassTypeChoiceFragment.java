package com.drprog.sjournal.dialogs;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.drprog.sjournal.blank.BlankFragment;
import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.TableClasses;
import com.drprog.sjournal.db.entity.StudyClassType;
import com.drprog.sjournal.dialogs.utils.BaseAUDDialog;
import com.drprog.sjournal.dialogs.utils.BaseChoiceAdapter;
import com.drprog.sjournal.dialogs.utils.ClassTypeChoiceAdapter;
import com.drprog.sjournal.dialogs.utils.DialogClickListener;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.Constants;

import java.util.List;

/**
 * Created by Romka on 28.07.2014.
 */
public class ClassTypeChoiceFragment extends DialogFragment {
    private DialogClickListener callback;
    private int requestCode;
    private Long groupId;
    private Long subjectId;

    public static ClassTypeChoiceFragment newInstance(Fragment clickListener, int requestCode,
            Long subjectId, Long groupId) {
        ClassTypeChoiceFragment classTypeChoiceFragment = new ClassTypeChoiceFragment();
        classTypeChoiceFragment.setTargetFragment(clickListener, requestCode);
        if (subjectId != null || groupId != null) {
            Bundle args = new Bundle();
            if (subjectId != null) {
                args.putLong(Constants.KEY_SUBJECT_ID, subjectId);
            }
            if (groupId != null) {
                args.putLong(Constants.KEY_GROUP_ID, groupId);
            }
            classTypeChoiceFragment.setArguments(args);
        }
        return classTypeChoiceFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (DialogClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Calling fragment must implement DialogClickListener interface");
        }
        requestCode = getTargetRequestCode();
        if (getArguments() != null) {
            if (getArguments().containsKey(Constants.KEY_GROUP_ID)) {
                groupId = getArguments().getLong(Constants.KEY_GROUP_ID);
            }
            if (getArguments().containsKey(Constants.KEY_SUBJECT_ID)) {
                subjectId = getArguments().getLong(Constants.KEY_SUBJECT_ID);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (getDialog() != null) getDialog().setTitle(R.string.dialog_subject_choice_title);
        final View v = inflater.inflate(R.layout.dialog_grid, container, false);

        final GridView gridView = (GridView) v.findViewById(R.id.grid1);
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
        final List<StudyClassType> classTypeList =
                dbHelper.classTypes.getAllByFilter(subjectId, groupId);
        ClassTypeChoiceAdapter adapter =
                new ClassTypeChoiceAdapter(getActivity(), classTypeList,
                                           (subjectId != null || groupId != null ||
                                                   classTypeList.isEmpty()), true
                );
        gridView.setAdapter(adapter);
        registerForContextMenu(gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callback == null) return;

                if (id == BaseChoiceAdapter.TYPE_MORE) {
                    SQLiteJournalHelper dbHelper =
                            SQLiteJournalHelper.getInstance(getActivity(), true);
                    classTypeList.clear();
                    classTypeList.addAll(dbHelper.classTypes.getAll(TableClasses.KEY_ABBR));
                    ClassTypeChoiceAdapter adapter =
                            new ClassTypeChoiceAdapter(getActivity(), classTypeList, false, true);
                    gridView.setAdapter(adapter);

//                    callback.onDialogResult(requestCode,
//                                            DialogClickListener.DialogResultCode.RESULT_MORE,
//                                            null);
                } else if (id == BaseChoiceAdapter.TYPE_ADD) {
                    callback.onDialogResult(requestCode,
                                            DialogClickListener.DialogResultCode.RESULT_NO,
                                            null);
                    ClassTypeAUDDialog.newInstance(null, null, BaseAUDDialog.DIALOG_ADD, null)
                            .show(getFragmentManager(), "dialog");
                } else {
                    Bundle bundle = new Bundle();
                    long classTypeId = id;
                    bundle.putLong(Constants.KEY_CLASS_TYPE_ID, classTypeId);
                    if (groupId != null) {
                        bundle.putLong(Constants.KEY_GROUP_ID, groupId);
                    }
                    if (subjectId != null) {
                        bundle.putLong(Constants.KEY_SUBJECT_ID, subjectId);
                    }
                    callback.onDialogResult(requestCode,
                                            DialogClickListener.DialogResultCode.RESULT_OK,
                                            bundle);
                    dismiss();
                }
            }
        });

        return v;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        callback.onDialogResult(requestCode, DialogClickListener.DialogResultCode.RESULT_CANCEL,
                                null);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (info.id < 0) return;
        StudyClassType classType =
                SQLiteJournalHelper.getInstance(getActivity(), true).classTypes.get(info.id);
        if (classType != null) {
            menu.setHeaderTitle(classType.toString());
        } else {
            return;
        }
        menu.setHeaderIcon(android.R.drawable.ic_menu_info_details);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contextmenu_choice, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info.id < 0) return false;
        int itemId = item.getItemId();
        if (itemId == R.id.context_menu_edit) {
            ClassTypeAUDDialog
                    .newInstance((Fragment) callback, BlankFragment.CODE_DIALOG_CLASS_TYPE_AUD,
                            BaseAUDDialog.DIALOG_UPDATE, info.id)
                    .show(getFragmentManager(), "dialog");
            callback.onDialogResult(requestCode,
                    DialogClickListener.DialogResultCode.RESULT_NO,
                    null);
        } else if (itemId == R.id.context_menu_delete) {
            ClassTypeAUDDialog
                    .newInstance((Fragment) callback, BlankFragment.CODE_DIALOG_CLASS_TYPE_AUD,
                            BaseAUDDialog.DIALOG_DELETE, info.id)
                    .show(getFragmentManager(), "dialog");
            callback.onDialogResult(requestCode,
                    DialogClickListener.DialogResultCode.RESULT_NO,
                    null);
        }
        return true;
    }
}
