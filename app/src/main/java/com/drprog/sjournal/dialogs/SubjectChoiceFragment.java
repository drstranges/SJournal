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
import com.drprog.sjournal.db.TableSubjects;
import com.drprog.sjournal.db.entity.StudySubject;
import com.drprog.sjournal.dialogs.utils.BaseAUDDialog;
import com.drprog.sjournal.dialogs.utils.BaseChoiceAdapter;
import com.drprog.sjournal.dialogs.utils.DialogClickListener;
import com.drprog.sjournal.dialogs.utils.SubjectChoiceAdapter;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.Constants;

import java.util.List;

/**
 * Created by Romka on 27.07.2014.
 */
public class SubjectChoiceFragment extends DialogFragment {

    private DialogClickListener callback;
    private int requestCode;
    private Long groupId;
    private Long classTypeId;

    public static SubjectChoiceFragment newInstance(Fragment clickListener, int requestCode,
            Long groupId, Long classTypeId) {
        SubjectChoiceFragment subjectChoiceFragment = new SubjectChoiceFragment();
        subjectChoiceFragment.setTargetFragment(clickListener, requestCode);
        if (groupId != null || classTypeId != null) {
            Bundle args = new Bundle();
            if (groupId != null) {
                args.putLong(Constants.KEY_GROUP_ID, groupId);
            }
            if (classTypeId != null) {
                args.putLong(Constants.KEY_CLASS_TYPE_ID, classTypeId);
            }
            subjectChoiceFragment.setArguments(args);
        }
        return subjectChoiceFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar_Fullscreen);
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
            if (getArguments().containsKey(Constants.KEY_CLASS_TYPE_ID)) {
                classTypeId = getArguments().getLong(Constants.KEY_CLASS_TYPE_ID);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (getDialog() != null) getDialog().setTitle(R.string.dialog_subject_choice_title);
        final View v = inflater.inflate(R.layout.dialog_grid, container, false);

        final GridView gridView = (GridView) v.findViewById(R.id.grid1);
        registerForContextMenu(gridView);
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
        final List<StudySubject> subjectList =
                dbHelper.subjects.getAllByFilter(groupId, classTypeId);
        SubjectChoiceAdapter adapter =
                new SubjectChoiceAdapter(getActivity(), subjectList,
                                         (groupId != null || classTypeId != null ||
                                                 subjectList.isEmpty()), true
                );
        gridView.setAdapter(adapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callback == null) return;

                if (id == BaseChoiceAdapter.TYPE_MORE) {
                    SQLiteJournalHelper dbHelper =
                            SQLiteJournalHelper.getInstance(getActivity(), true);
                    subjectList.clear();
                    subjectList.addAll(dbHelper.subjects.getAll(TableSubjects.KEY_ABBR));
                    SubjectChoiceAdapter adapter =
                            new SubjectChoiceAdapter(getActivity(), subjectList, false, true);
                    gridView.setAdapter(adapter);
//                        callback.onDialogResult(requestCode,
//                                                DialogClickListener.DialogResultCode.RESULT_MORE,
//                                                null);
                } else if (id == BaseChoiceAdapter.TYPE_ADD) {
                    callback.onDialogResult(requestCode,
                                            DialogClickListener.DialogResultCode.RESULT_NO,
                                            null);
                    SubjectAUDDialog.newInstance(null, null, BaseAUDDialog.DIALOG_ADD, null)
                            .show(getFragmentManager(), "dialog");
                } else {
                    Bundle bundle = new Bundle();
                    long subjectId = id;
                    bundle.putLong(Constants.KEY_SUBJECT_ID, subjectId);
                    if (groupId != null) {
                        bundle.putLong(Constants.KEY_GROUP_ID, groupId);
                    }
                    if (classTypeId != null) {
                        bundle.putLong(Constants.KEY_CLASS_TYPE_ID, classTypeId);
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
        StudySubject subject =
                SQLiteJournalHelper.getInstance(getActivity(), true).subjects.get(info.id);
        if (subject != null) {
            menu.setHeaderTitle(subject.toString());
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
        switch (item.getItemId()) {
            case R.id.context_menu_edit:
                SubjectAUDDialog
                        .newInstance((Fragment) callback, BlankFragment.CODE_DIALOG_SUBJECT_AUD,
                                     BaseAUDDialog.DIALOG_UPDATE, info.id)
                        .show(getFragmentManager(), "dialog");
                callback.onDialogResult(requestCode,
                                        DialogClickListener.DialogResultCode.RESULT_NO,
                                        null);
                break;
            case R.id.context_menu_delete:
                SubjectAUDDialog
                        .newInstance((Fragment) callback, BlankFragment.CODE_DIALOG_SUBJECT_AUD,
                                     BaseAUDDialog.DIALOG_DELETE, info.id)
                        .show(getFragmentManager(), "dialog");
                callback.onDialogResult(requestCode,
                                        DialogClickListener.DialogResultCode.RESULT_NO,
                                        null);
                break;
        }
        return true;
    }
}
