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
import com.drprog.sjournal.db.TableGroups;
import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.dialogs.utils.BaseAUDDialog;
import com.drprog.sjournal.dialogs.utils.BaseChoiceAdapter;
import com.drprog.sjournal.dialogs.utils.DialogClickListener;
import com.drprog.sjournal.dialogs.utils.GroupChoiceAdapter;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.Constants;

import java.util.List;

/**
 * Created by Romka on 17.07.2014.
 */
public class GroupChoiceFragment extends DialogFragment {

    private GridView gridView;

    private DialogClickListener callback;
    private int requestCode;
    private Long subjectId;
    private Long classTypeId;

    public static GroupChoiceFragment newInstance(Fragment clickListener, int requestCode,
            Long subjectId, Long classTypeId) {
        GroupChoiceFragment groupChoiceFragment = new GroupChoiceFragment();
        groupChoiceFragment.setTargetFragment(clickListener, requestCode);
        if (subjectId != null || classTypeId != null) {
            Bundle args = new Bundle();
            if (subjectId != null) {
                args.putLong(Constants.KEY_SUBJECT_ID, subjectId);
            }
            if (classTypeId != null) {
                args.putLong(Constants.KEY_CLASS_TYPE_ID, classTypeId);
            }
            groupChoiceFragment.setArguments(args);
        }
        return groupChoiceFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Check restoreInstanceState after double rotating when Fragment is in backStack!
        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar_Fullscreen);
        try {
            callback = (DialogClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Calling fragment must implement DialogClickListener interface");
        }
        requestCode = getTargetRequestCode();
        if (getArguments() != null) {
            if (getArguments().containsKey(Constants.KEY_SUBJECT_ID)) {
                subjectId = getArguments().getLong(Constants.KEY_SUBJECT_ID);
            }
            if (getArguments().containsKey(Constants.KEY_CLASS_TYPE_ID)) {
                classTypeId = getArguments().getLong(Constants.KEY_CLASS_TYPE_ID);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (getDialog() != null) getDialog().setTitle(R.string.dialog_group_choice_title);
        final View v = inflater.inflate(R.layout.dialog_grid, container, false);

        gridView = (GridView) v.findViewById(R.id.grid1);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
        final List<StudyGroup> groupList = dbHelper.groups.getAllByFilter(subjectId, classTypeId);
        GroupChoiceAdapter adapter =
                new GroupChoiceAdapter(getActivity(), groupList,
                                       (subjectId != null || classTypeId != null ||
                                               groupList.isEmpty()), true
                );
        gridView.setAdapter(adapter);
        registerForContextMenu(gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callback == null) return;

                if (id >= 0) {
                    long groupId = id;
                    Bundle bundle = new Bundle();
                    bundle.putLong(Constants.KEY_GROUP_ID, groupId);
                    if (subjectId != null) {
                        bundle.putLong(Constants.KEY_SUBJECT_ID, subjectId);
                    }
                    if (classTypeId != null) {
                        bundle.putLong(Constants.KEY_CLASS_TYPE_ID, classTypeId);
                    }
                    callback.onDialogResult(requestCode,
                                            DialogClickListener.DialogResultCode.RESULT_OK,
                                            bundle);
                    dismiss();
                } else if (id == BaseChoiceAdapter.TYPE_MORE) {
                    SQLiteJournalHelper dbHelper =
                            SQLiteJournalHelper.getInstance(getActivity(), true);
                    groupList.clear();
                    groupList.addAll(dbHelper.groups.getAll(TableGroups.KEY_CODE));
                    GroupChoiceAdapter adapter =
                            new GroupChoiceAdapter(getActivity(), groupList, false, true);
                    gridView.setAdapter(adapter);
//                    callback.onDialogResult(requestCode,
//                                            DialogClickListener.DialogResultCode.RESULT_MORE,
//                                            null);
                } else if (id == BaseChoiceAdapter.TYPE_ADD) {
                    callback.onDialogResult(requestCode,
                                            DialogClickListener.DialogResultCode.RESULT_NO,
                                            null);
                    GroupAUDDialog.newInstance(null, null, BaseAUDDialog.DIALOG_ADD, null)
                            .show(getFragmentManager(), "dialog");
                }
            }
        });
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
        StudyGroup group = SQLiteJournalHelper.getInstance(getActivity(), true).groups.get(info.id);
        if (group != null) {
            menu.setHeaderTitle(group.toString());
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
            GroupAUDDialog.newInstance((Fragment) callback, BlankFragment.CODE_DIALOG_GROUP_AUD,
                            BaseAUDDialog.DIALOG_UPDATE, info.id)
                    .show(getFragmentManager(), "dialog");
            callback.onDialogResult(requestCode,
                    DialogClickListener.DialogResultCode.RESULT_NO,
                    null);
        } else if (itemId == R.id.context_menu_delete) {
            GroupAUDDialog.newInstance((Fragment) callback, BlankFragment.CODE_DIALOG_GROUP_AUD,
                            BaseAUDDialog.DIALOG_DELETE, info.id)
                    .show(getFragmentManager(), "dialog");
            callback.onDialogResult(requestCode,
                    DialogClickListener.DialogResultCode.RESULT_NO,
                    null);
        }
        return true;
    }
}
