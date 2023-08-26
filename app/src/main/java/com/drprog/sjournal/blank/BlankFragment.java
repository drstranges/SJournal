package com.drprog.sjournal.blank;

import android.app.Activity;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.entity.StudyClassType;
import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.db.entity.StudySubject;
import com.drprog.sjournal.db.prefs.Dimensions;
import com.drprog.sjournal.dialogs.AddClassWithAbsentDialog;
import com.drprog.sjournal.dialogs.ClassAUDDialog;
import com.drprog.sjournal.dialogs.ClassTypeAUDDialog;
import com.drprog.sjournal.dialogs.ClassTypeChoiceFragment;
import com.drprog.sjournal.dialogs.GroupAUDDialog;
import com.drprog.sjournal.dialogs.GroupChoiceFragment;
import com.drprog.sjournal.dialogs.ImportDialog;
import com.drprog.sjournal.dialogs.MarkAUDDialog;
import com.drprog.sjournal.dialogs.StudentAUDDialog;
import com.drprog.sjournal.dialogs.SubjectAUDDialog;
import com.drprog.sjournal.dialogs.SubjectChoiceFragment;
import com.drprog.sjournal.dialogs.SumEntryAUDDialog;
import com.drprog.sjournal.dialogs.utils.BaseAUDDialog;
import com.drprog.sjournal.dialogs.utils.DialogClickListener;
import com.drprog.sjournal.preferences.PrefsManager;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.CalendarSyncUtils;
import com.drprog.sjournal.utils.Constants;
import com.drprog.sjournal.utils.IOFiles;
import com.drprog.sjournal.utils.RunUtils;


import java.util.List;

/**
 * Created by Romka on 17.07.2014.
 */
public class BlankFragment extends Fragment implements DialogClickListener,
        LoaderManager.LoaderCallbacks<LinearLayout> {
    public static final String TAG = "TAG_BLANK_FRAGMENT";
    public static final int CODE_DIALOG_CHOICE_GROUP = 1;
    public static final int CODE_DIALOG_CHOICE_SUBJECT = 2;
    public static final int CODE_DIALOG_CHOICE_CLASS_TYPE = 3;
    public static final int CODE_DIALOG_GROUP_AUD = 4;
    public static final int CODE_DIALOG_SUBJECT_AUD = 5;
    public static final int CODE_DIALOG_CLASS_TYPE_AUD = 6;
    public static final int CODE_DIALOG_CLASS_AUD = 7;
    public static final int CODE_DIALOG_STUDENT_AUD = 8;
    public static final int CODE_DIALOG_MARK_AUD = 9;
    public static final int CODE_DIALOG_IMPORT = 10;
    public static final int LOADER_BLANK_REFRESH = 1;
    public static final String LOADER_KEY_RELOAD_STYLE = "LOADER_KEY_RELOAD_STYLE";
    public static final int CODE_FRAGMENT_BLANK_STYLE = 11;
    public static final int CODE_DIALOG_SUM_ENTRY_AUD = 12;
    public static final int CODE_ADD_CLASS_WITH_ABSENT = 13;

    public static final String KEY_DIALOG_AUD = "KEY_DIALOG_AUD";
    public static final String KEY_DIALOG_CHOICE = "KEY_DIALOG_CHOICE";
    public static final String KEY_DIALOG_CHOICE_SUBJECT = "KEY_DIALOG_CHOICE_SUBJECT";
    public static final String KEY_DIALOG_CHOICE_GROUP = "KEY_DIALOG_CHOICE_GROUP";
    public static final String KEY_DIALOG_CHOICE_CLASS_TYPE = "KEY_DIALOG_CHOICE_CLASS_TYPE";
    public static final String KEY_DIALOG_CLASS_AUD = "KEY_DIALOG_CLASS_AUD";
    public static final String KEY_DIALOG_MARK_AUD = "KEY_DIALOG_MARK_AUD";
    public static final String KEY_DIALOG_IMPORT = "KEY_DIALOG_IMPORT";
    public static final String KEY_FRAGMENT_BLANK_STYLE = "KEY_FRAGMENT_BLANK_STYLE";
    public static final String KEY_DIALOG_SUM_ENTRY_AUD = "KEY_DIALOG_SUM_ENTRY_AUD";


    private static final String KEY_LAST_GST_HANDLER_TITLE = "KEY_LAST_GST_HANDLER_TITLE";
    public static final int REQUEST_CODE_CHECK_CALENDAR_PERMISSION = 3001;

    private static enum ExportKey {NO_EXPORT, EXPORT_IMG, EXPORT_CSV}
    private static final int REQUEST_CODE_PICK_EXPORT_LOCATION = 302;

    private ProgressBar progressBar;
    private LinearLayout topChoiceBar;
    private int topChoiceBarVisibility = View.VISIBLE;
    private ExportKey exportKey = ExportKey.NO_EXPORT;

    private Uri exportFileUri;

    private SQLiteJournalHelper dbHelper;
    private Long selectedGroupId;
    private Long selectedSubjectId;
    private Long selectedClassTypeId;
    String lastGstHandlerTitle;
    final Blank.OnBlankClickListener blankClickListener = new Blank.OnBlankClickListener() {
        @Override
        public void onBlankClick(View v) {
            Object o = v.getTag();
            if (o == null || !(o instanceof BlankTagHandler)) return;
            BlankTagHandler tagHandler = (BlankTagHandler) o;
            switch (tagHandler.getCeilType()) {
                case NONE:
                    break;
                case CLASS:
                    showClassUpdateDialogIfPrefs(tagHandler.getClassId());
//                    boolean iPref = PrefsManager.getInstance(getActivity()).getPrefs()
//                            .getString(PrefsManager.PREFS_BLANK_SYMBOL_FORMAT,
//                                       "0"));
//                    showClassAUDDialog(BaseAUDDialog.DIALOG_UPDATE, tagHandler.getClassId(), null,
//                                       null,
//                                       null);
                    break;
                case SUM_ENTRY:
                    break;
                case STUDENT:
                    break;
                case NO_MARK:
                    break;
                case MARK:
                    break;
                case SYMBOL:
                    break;
                case ABSENT:
                    break;
                case ADD_COL_CEIL:
                    showClassAUDDialog(BaseAUDDialog.DIALOG_ADD, null, selectedSubjectId,
                                       selectedGroupId,
                                       selectedClassTypeId);
                    break;
                case ADD_ROW_CEIL:
                    showStudentAUDDialog(BaseAUDDialog.DIALOG_ADD, null, selectedGroupId);
                    break;
                case ADD_COL_CEIL_SUM:
                    showSumEntryAUDDialog(BaseAUDDialog.DIALOG_ADD, null);
                    break;
            }
        }

        @Override
        public void onBlankLongClick(View v) {
            Object o = v.getTag();
            if (o == null || !(o instanceof BlankTagHandler)) return;
            BlankTagHandler tagHandler = (BlankTagHandler) o;
            switch (tagHandler.getCeilType()) {
                case NONE:
                    break;
                case CLASS:
                    showClassContextMenu(tagHandler.getClassId());
                    break;
                case SUM_ENTRY:
                    showSumEntryContextMenu(tagHandler.getClassId());
                    break;
                case STUDENT:
                    showStudentContextMenu(tagHandler.getStudentId());
                    break;
                case NO_MARK:
                    showMarkAUDDialog(BaseAUDDialog.DIALOG_ADD,
                                      tagHandler);//.getClassId(), tagHandler.getStudentId());
                    break;
                case MARK:
                case SYMBOL:
                case ABSENT:
                    showMarkAUDDialog(BaseAUDDialog.DIALOG_UPDATE,
                                      tagHandler);//.getClassId(), tagHandler.getStudentId());
                    break;
                case ADD_COL_CEIL:
                    showClassAUDDialog(BaseAUDDialog.DIALOG_ADD, null, selectedSubjectId,
                                       selectedGroupId,
                                       selectedClassTypeId);
                    break;
                case ADD_ROW_CEIL:

                    break;
                case ADD_COL_CEIL_SUM:
                    break;
            }
           // Log.d(DebugUtils.TAG_DEBUG, "-----Blank.onLongClick: " + tagHandler.getCeilType());
        }
    };


    private boolean isBlankSummary = false;
    private List<Dimensions> dimensionsList;

    //============================= Create Fragment ================================================
    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            restoreSavedInPrefs();
        } else {
            restoreInstanceState(savedInstanceState);
        }

    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        topChoiceBarVisibility = PrefsManager.getInstance(getActivity()).getPrefs()
                .getInt(PrefsManager.PREFS_TOP_CHOICE_PANEL_VISIBILITY, View.VISIBLE);
        if (topChoiceBarVisibility == View.GONE) {
            topChoiceBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == REQUEST_CODE_CHECK_CALENDAR_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean isSynced = calendarSync();
                if (isSynced) refreshBlank();
            } else {
                PrefsManager.getInstance(getActivity()).getPrefs()
                        .edit().putBoolean(PrefsManager.PREFS_CALENDAR_SYNC, false).apply();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean calendarSync() {
        Boolean isSync = PrefsManager.getInstance(getActivity()).getPrefs()
                                                .getBoolean(PrefsManager.PREFS_CALENDAR_SYNC, false);
        if (! isSync) return false;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_CODE_CHECK_CALENDAR_PERMISSION);
                return false;
            }
        }

        Boolean isWindow = PrefsManager.getInstance(getActivity()).getPrefs()
                .getBoolean(PrefsManager.PREFS_CALENDAR_SYNC_WINDOW,
                            true);
        long window = 0;
        if (isWindow){
            window = 5*60*1000; //5 minutes in milliseconds
        }
        dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        List<StudyGroup> groupList = dbHelper.groups.getAll(null);
        List<StudySubject> subjectList = dbHelper.subjects.getAll(null);
        List<StudyClassType> classTypeList = dbHelper.classTypes.getAll(null);
        CalendarSyncUtils.GSTHandler gstHandler = CalendarSyncUtils.syncCalendarEvents(getActivity(),window,groupList,subjectList,classTypeList);
        if (gstHandler != null && gstHandler.eventTitle != null && (lastGstHandlerTitle == null || !gstHandler.eventTitle.equals(lastGstHandlerTitle))){
            if (selectedGroupId == null || selectedSubjectId == null || selectedClassTypeId == null
                    || !selectedGroupId.equals(gstHandler.groupId)
                    || !selectedSubjectId.equals(gstHandler.subjectId)
                    || !selectedClassTypeId.equals(gstHandler.classTypeId)){


//                RunUtils.getAlertDialog(getActivity(),android.R.drawable.ic_dialog_info,
//                                        R.string.calendar_sync_query_title,
//                                        R.string.calendar_sync_query_message,
//                                        true,true,okClickListener, null,null,null);
                lastGstHandlerTitle = gstHandler.eventTitle;
                selectedGroupId = gstHandler.groupId;
                selectedSubjectId = gstHandler.subjectId;
                selectedClassTypeId = gstHandler.classTypeId;
                RunUtils.showToast(getActivity(), String.format(getString(R.string.calendar_sync_with_event_complete),gstHandler.eventTitle));
                return true;
            }
//            RunUtils.showToast(getActivity(), String.format(getString(R.string.calendar_sync_with_event_complete),gstHandler.eventTitle)
//            + "\n Group: " + dbHelper.groups.get(selectedGroupId).getCode() );
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.VISIBLE);
        boolean isSynced = calendarSync();
//        if (getLoaderManager().getLoader(LOADER_BLANK_REFRESH) == null){
            getLoaderManager().initLoader(LOADER_BLANK_REFRESH, null, this);
//        }else{
            if (isSynced) refreshBlank();
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        assert rootView != null;

        if (savedInstanceState == null) {
            restoreSavedInPrefs();
        } else {
            restoreInstanceState(savedInstanceState);
        }

        Button btnSubjectTypeChoice = (Button) rootView.findViewById(R.id.btn_class_type_choice);
        Button btnSubjectChoice = (Button) rootView.findViewById(R.id.btn_subject_choice);
        Button btnGroupChoice = (Button) rootView.findViewById(R.id.btn_group_choice);

        btnGroupChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGroupChoiceDialog(null, null);
            }
        });
        btnSubjectChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubjectChoiceDialog(null, null);
            }
        });
        btnSubjectTypeChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClassTypeChoiceDialog(selectedSubjectId, selectedGroupId);
            }
        });

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        topChoiceBar = (LinearLayout) rootView.findViewById(R.id.topChoiceBar);


        //Log.d(DebugUtils.TAG_DEBUG, "-----BlankFragmen.onCreateView-----");
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveStateInPrefs();
        //Log.d(DebugUtils.TAG_DEBUG, "---BlankFragment.onDestroy------------------------");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (selectedGroupId != null && selectedSubjectId != null && selectedClassTypeId != null) {
            outState.putLong(Constants.KEY_GROUP_ID, selectedGroupId);
            outState.putLong(Constants.KEY_SUBJECT_ID, selectedSubjectId);
            outState.putLong(Constants.KEY_CLASS_TYPE_ID, selectedClassTypeId);
        }
        if (lastGstHandlerTitle != null){
            outState.putString(KEY_LAST_GST_HANDLER_TITLE, lastGstHandlerTitle);
        }
        saveStateInPrefs();
        //Log.d(DebugUtils.TAG_DEBUG, "---BlankFragment.onSaveInstanceState------------------------");
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if ((savedInstanceState != null) && (selectedGroupId == null && selectedSubjectId == null &&
                selectedClassTypeId == null)) {
            if (savedInstanceState.containsKey(Constants.KEY_GROUP_ID)) {
                selectedGroupId = savedInstanceState.getLong(Constants.KEY_GROUP_ID);
            }
            if (savedInstanceState.containsKey(Constants.KEY_SUBJECT_ID)) {
                selectedSubjectId = savedInstanceState.getLong(Constants.KEY_SUBJECT_ID);
            }
            if (savedInstanceState.containsKey(Constants.KEY_CLASS_TYPE_ID)) {
                selectedClassTypeId = savedInstanceState.getLong(Constants.KEY_CLASS_TYPE_ID);
            }
            if (savedInstanceState.containsKey(KEY_LAST_GST_HANDLER_TITLE)) {
                lastGstHandlerTitle = savedInstanceState.getString(KEY_LAST_GST_HANDLER_TITLE);
            }
        }
    }

    private void restoreSavedInPrefs() {
        Long lastGroupId = PrefsManager.getInstance(getActivity()).getPrefs()
                .getLong(PrefsManager.PREFS_LAST_SELECTED_GROUP_ID, -1L);
        Long lastSubjectId = PrefsManager.getInstance(getActivity()).getPrefs().getLong(
                PrefsManager.PREFS_LAST_SELECTED_SUBJECT_ID, -1L);
        Long lastClassTypeId = PrefsManager.getInstance(getActivity()).getPrefs()
                .getLong(PrefsManager.PREFS_LAST_SELECTED_CLASS_TYPE_ID, -1L);
        if (lastGroupId >= 0 && lastSubjectId >= 0 && lastClassTypeId >= 0) {
            selectedGroupId = lastGroupId;
            selectedSubjectId = lastSubjectId;
            selectedClassTypeId = lastClassTypeId;
        }
    }

    private void saveStateInPrefs() {
        if (selectedGroupId != null && selectedSubjectId != null && selectedClassTypeId != null) {
            SharedPreferences.Editor editor = PrefsManager.getInstance(
                    getActivity()).getPrefs().edit();
            editor.putLong(PrefsManager.PREFS_LAST_SELECTED_GROUP_ID, selectedGroupId);
            editor.putLong(PrefsManager.PREFS_LAST_SELECTED_SUBJECT_ID, selectedSubjectId);
            editor.putLong(PrefsManager.PREFS_LAST_SELECTED_CLASS_TYPE_ID, selectedClassTypeId);
            editor.apply();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.blank, menu);
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_blank_group_select
                || itemId == R.id.menu_blank_subject_select
                || itemId == R.id.menu_blank_class_type_select) {
            //do nothing
        } else {
            getFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if (itemId == R.id.menu_blank_panel_show) {
            showChoicePanel();
        } else if (itemId == R.id.menu_blank_group_select) {
            showGroupChoiceDialog(null, null);
        } else if (itemId == R.id.menu_blank_subject_select) {
            showSubjectChoiceDialog(null, null);
        } else if (itemId == R.id.menu_blank_class_type_select) {
            showClassTypeChoiceDialog(selectedSubjectId, selectedGroupId);
        } else if (itemId == R.id.menu_blank_type_select) {
            toggleBlankType();
        } else if (itemId == R.id.menu_semester_select) {
            showSemesterSelectDialog();
        } else if (itemId == R.id.menu_blank_add_class_absents_select) {
            showAbsentsSelectDialog();
        } else if (itemId == R.id.menu_group_add) {
            GroupAUDDialog.newInstance(null, null, BaseAUDDialog.DIALOG_ADD, null)
                    .show(getFragmentManager(), KEY_DIALOG_AUD);
        } else if (itemId == R.id.menu_group_update) {
            GroupAUDDialog.newInstance(this, CODE_DIALOG_GROUP_AUD, BaseAUDDialog.DIALOG_UPDATE,
                            selectedGroupId)
                    .show(getFragmentManager(), KEY_DIALOG_AUD);
        } else if (itemId == R.id.menu_group_delete) {
            GroupAUDDialog.newInstance(this, CODE_DIALOG_GROUP_AUD, BaseAUDDialog.DIALOG_DELETE,
                            selectedGroupId)
                    .show(getFragmentManager(), KEY_DIALOG_AUD);
        } else if (itemId == R.id.menu_subject_add) {
            SubjectAUDDialog.newInstance(null, null, BaseAUDDialog.DIALOG_ADD, null)
                    .show(getFragmentManager(), KEY_DIALOG_AUD);
        } else if (itemId == R.id.menu_subject_update) {
            SubjectAUDDialog
                    .newInstance(this, CODE_DIALOG_SUBJECT_AUD, BaseAUDDialog.DIALOG_UPDATE,
                            selectedSubjectId)
                    .show(getFragmentManager(), KEY_DIALOG_AUD);
        } else if (itemId == R.id.menu_subject_delete) {
            SubjectAUDDialog
                    .newInstance(this, CODE_DIALOG_SUBJECT_AUD, BaseAUDDialog.DIALOG_DELETE,
                            selectedSubjectId)
                    .show(getFragmentManager(), KEY_DIALOG_AUD);
        } else if (itemId == R.id.menu_class_type_add) {
            ClassTypeAUDDialog.newInstance(null, null, BaseAUDDialog.DIALOG_ADD, null)
                    .show(getFragmentManager(), KEY_DIALOG_AUD);
        } else if (itemId == R.id.menu_class_type_update) {
            ClassTypeAUDDialog
                    .newInstance(this, CODE_DIALOG_CLASS_TYPE_AUD, BaseAUDDialog.DIALOG_UPDATE,
                            selectedClassTypeId)
                    .show(getFragmentManager(), KEY_DIALOG_AUD);
        } else if (itemId == R.id.menu_class_type_delete) {
            ClassTypeAUDDialog
                    .newInstance(this, CODE_DIALOG_CLASS_TYPE_AUD, BaseAUDDialog.DIALOG_DELETE,
                            selectedClassTypeId)
                    .show(getFragmentManager(), KEY_DIALOG_AUD);
        } else if (itemId == R.id.menu_student_add) {
            showStudentAUDDialog(BaseAUDDialog.DIALOG_ADD, null, selectedGroupId);
        } else if (itemId == R.id.menu_student_update) {
            showStudentAUDDialog(BaseAUDDialog.DIALOG_UPDATE, null, null);
        } else if (itemId == R.id.menu_student_delete) {
            showStudentAUDDialog(BaseAUDDialog.DIALOG_DELETE, null, null);
        } else if (itemId == R.id.menu_student_import) {
            ImportDialog.newInstance(this, CODE_DIALOG_IMPORT, selectedGroupId)
                    .show(getFragmentManager(), KEY_DIALOG_IMPORT);
        } else if (itemId == R.id.menu_blank_style) {
            showBlankStyleFragment();
        } else if (itemId == R.id.menu_export_img) {
            if (!RunUtils.showMessageIfFree(getFragmentManager())) {
                doExportImg();
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //============================= Refresh/Draw Blank =============================================

    private void showChoicePanel() {
        if (topChoiceBar.getVisibility() != View.GONE) {
            topChoiceBarVisibility = View.GONE;
        } else {
            topChoiceBarVisibility = View.VISIBLE;
        }
        topChoiceBar.setVisibility(topChoiceBarVisibility);
        SharedPreferences.Editor editor = PrefsManager.getInstance(
                getActivity()).getPrefs().edit();
        editor.putInt(PrefsManager.PREFS_TOP_CHOICE_PANEL_VISIBILITY, topChoiceBarVisibility);
        editor.apply();
    }

    public LinearLayout clearBlank() {

        LinearLayout blankLayout = (LinearLayout) getActivity().findViewById(R.id.blankLayout);
        if (blankLayout != null) { blankLayout.removeAllViews(); }
        return blankLayout;
    }

    private void refreshMark(BlankTagHandler tagHandler) {
        LinearLayout blankLayout = (LinearLayout) getActivity().findViewById(R.id.blankLayout);
        //TextView targetView = (TextView) blankLayout.findViewWithTag(tagHandler);
        TextView targetView = tagHandler.getTextView();
        if (targetView == null) return;

/*        TextView studentView = (TextView) blankLayout.findViewWithTag(new BlankTagHandler(
                BlankTagHandler.CeilType.STUDENT, null, tagHandler.getStudentId(), null));
        if (studentView != null) {
            targetView.setTextSize(studentView.getTextSize());
//                    blank.getDimensionsContainer().findDimensions(Blank.KEY_CLASS_BODY)
//                            .getTextSize());
        }*/
        if (tagHandler.getCeilType() == BlankTagHandler.CeilType.SYMBOL) {
            int iPref = Integer.valueOf(PrefsManager.getInstance(getActivity()).getPrefs()
                                                .getString(PrefsManager.PREFS_BLANK_SYMBOL_FORMAT,
                                                           "0"));
            Blank.setTextByPrefs(targetView, targetView.getWidth(), iPref, tagHandler.getText());
        } else { targetView.setText(tagHandler.getText()); }
        targetView.setTag(tagHandler);
        targetView = (TextView) blankLayout.findViewWithTag(new BlankTagHandler(
                BlankTagHandler.CeilType.ABSENT_ROW_CEIL, tagHandler.getClassId(), -1L, null));
        if (targetView == null) return;
        targetView.setText(String.valueOf(SQLiteJournalHelper.getWritableInstance(getActivity()).marks
                                                  .getAbsentNumInClass(selectedGroupId,tagHandler.getClassId())));
    }

    public void refreshBlank() {
        refreshBlank(false, true);
    }

    public void refreshBlank(boolean reloadStyle, boolean clearBlank) {
        if (clearBlank) clearBlank();
        Bundle bundle = new Bundle();
        bundle.putBoolean(LOADER_KEY_RELOAD_STYLE, reloadStyle);
        getLoaderManager().restartLoader(LOADER_BLANK_REFRESH, bundle, this);
    }

    public void refreshBlank(List<Dimensions> dimensionsList) {
        this.dimensionsList = dimensionsList;
        refreshBlank(false, false);
    }


    //============================= Dialogs ========================================================

    private void toggleBlankType() {
        getFragmentManager()
                .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        isBlankSummary = !isBlankSummary;
        MenuItem menuItem = menu.findItem(R.id.menu_blank_type_select);
        if (isBlankSummary) {
            menuItem.setTitle(R.string.menu_blank_type_classes);
        } else {
            menuItem.setTitle(R.string.menu_blank_type_summary);
        }
        refreshBlank();
    }

    private void refreshSemester(Integer newSemester) {
        if (menu == null) return;
        MenuItem menuItem = menu.findItem(R.id.menu_semester_select);
        if (selectedGroupId != null) {
            dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
            StudyGroup studyGroup = dbHelper.groups.get(selectedGroupId);
            if (studyGroup != null) {
                if (newSemester != null) {
                    studyGroup.setSemester(newSemester);
                    dbHelper.groups.update(studyGroup.getId(), studyGroup);
                    refreshBlank();
                }
                menuItem.setVisible(true);
                menuItem.setTitle(
                        getString(R.string.menu_semester_format, studyGroup.getSemester()));
                return;
            }
        }
        menuItem.setVisible(false);

    }

    private void showSemesterSelectDialog() {
        if (selectedGroupId == null) return;
        dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        StudyGroup studyGroup = dbHelper.groups.get(selectedGroupId);
        if (studyGroup == null) return;
        final NumberPicker numberPicker = new NumberPicker(getActivity());
//        FrameLayout.LayoutParams layoutParams_WC_WC =
//                new FrameLayout.LayoutParams(LinearLayout.MarginLayoutParams.WRAP_CONTENT,
//                                                    LinearLayout.MarginLayoutParams.WRAP_CONTENT);
//        numberPicker.setLayoutParams(layoutParams_WC_WC);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(12);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        DialogInterface.OnClickListener okClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refreshSemester(numberPicker.getValue());
            }
        };
        RunUtils.getAlertDialog(getActivity(), android.R.drawable.ic_dialog_info,
                                R.string.dialog_semester_choice_title, null, true, true,
                                okClickListener, null, null, numberPicker).show();
        numberPicker.setValue(studyGroup.getSemester());

    }

    private void showAbsentsSelectDialog() {
        getFragmentManager()
                .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (selectedGroupId == null || selectedSubjectId == null || selectedClassTypeId == null) {
            RunUtils.showToast(getActivity(), R.string.toast_blank_not_selected);
            return;
        }
        AddClassWithAbsentDialog
                .newInstance(this, CODE_ADD_CLASS_WITH_ABSENT, selectedGroupId, selectedSubjectId,
                             selectedClassTypeId).show(getFragmentManager(),
                                                       AddClassWithAbsentDialog.TAG);


    }

    private void showBlankStyleFragment() {
        getFragmentManager()
                .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_off_right,
                                        R.animator.slide_in_left, R.animator.slide_off_right);
        transaction.replace(R.id.rightContainer, new BlankStyleFragment(), BlankStyleFragment.TAG);
        transaction.addToBackStack(KEY_FRAGMENT_BLANK_STYLE);
        transaction.commit();
    }

    private void showChoiceDialog(Long subjectId, Long groupId, Long classTypeId) {
        if (groupId == null) {
            showGroupChoiceDialog(subjectId, classTypeId);
        } else if (subjectId == null) {
            showSubjectChoiceDialog(groupId, classTypeId);
        } else if (classTypeId == null) {
            showClassTypeChoiceDialog(subjectId, groupId);
        }
    }

    private void showGroupChoiceDialog(Long subjectId, Long classType) {
        getFragmentManager()
                .popBackStackImmediate(KEY_DIALOG_AUD, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager()
                .popBackStackImmediate(KEY_FRAGMENT_BLANK_STYLE,
                                       FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Fragment frag = getFragmentManager().findFragmentByTag(KEY_DIALOG_CHOICE_GROUP);
        if (frag != null) {
            getFragmentManager()
                    .popBackStackImmediate(KEY_DIALOG_CHOICE,
                                           FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            frag = GroupChoiceFragment
                    .newInstance(this, CODE_DIALOG_CHOICE_GROUP, subjectId, classType);

            getFragmentManager().beginTransaction()
                    //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.dialogContainer, frag, KEY_DIALOG_CHOICE_GROUP)
                    .addToBackStack(KEY_DIALOG_CHOICE)
                    .commit();
        }
    }

    private void showSubjectChoiceDialog(Long groupId, Long classType) {
        getFragmentManager()
                .popBackStackImmediate(KEY_DIALOG_AUD, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager()
                .popBackStackImmediate(KEY_FRAGMENT_BLANK_STYLE,
                                       FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Fragment frag = getFragmentManager().findFragmentByTag(KEY_DIALOG_CHOICE_SUBJECT);
        if (frag != null) {
            getFragmentManager()
                    .popBackStackImmediate(KEY_DIALOG_CHOICE,
                                           FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            frag = SubjectChoiceFragment
                    .newInstance(this, CODE_DIALOG_CHOICE_SUBJECT, groupId, classType);
            getFragmentManager().beginTransaction()
                    //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.dialogContainer, frag, KEY_DIALOG_CHOICE_SUBJECT)
                    .addToBackStack(KEY_DIALOG_CHOICE)
                    .commit();
        }
    }

    private void showClassTypeChoiceDialog(Long subjectId, Long groupId) {
        getFragmentManager()
                .popBackStackImmediate(KEY_DIALOG_AUD, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager()
                .popBackStackImmediate(KEY_FRAGMENT_BLANK_STYLE,
                                       FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Fragment frag = getFragmentManager().findFragmentByTag(KEY_DIALOG_CHOICE_CLASS_TYPE);
        if (frag != null) {
            getFragmentManager()
                    .popBackStackImmediate(KEY_DIALOG_CHOICE,
                                           FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            frag = ClassTypeChoiceFragment
                    .newInstance(this, CODE_DIALOG_CHOICE_CLASS_TYPE, subjectId, groupId);

            getFragmentManager().beginTransaction()
                    //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.dialogContainer, frag, KEY_DIALOG_CHOICE_CLASS_TYPE)
                    .addToBackStack(KEY_DIALOG_CHOICE)
                    .commit();
        }
    }

    private void showClassUpdateDialogIfPrefs(Long classId) {
        boolean iPref = PrefsManager.getInstance(getActivity()).getPrefs()
                .getBoolean(PrefsManager.PREFS_BLANK_EDIT_CLASS_ON_CLICK,
                            false);
        if (iPref){
            showClassAUDDialog(BaseAUDDialog.DIALOG_UPDATE, classId, null,null,null);
        }
    }

    private void showClassAUDDialog(int dialogType, Long classId, Long subjectId, Long groupId,
            Long classTypeId) {
        //TODO: popBackStack (null)
        getFragmentManager()
                .popBackStackImmediate(KEY_DIALOG_CHOICE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager()
                .popBackStackImmediate(KEY_FRAGMENT_BLANK_STYLE,
                                       FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        Fragment frag = getFragmentManager().findFragmentByTag(KEY_DIALOG_AUD);
//        if (frag != null) {
//
//        }
        getFragmentManager()
                .popBackStackImmediate(KEY_DIALOG_AUD, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment frag = ClassAUDDialog
                .newInstance(this, CODE_DIALOG_CLASS_AUD, dialogType, classId, groupId, subjectId,
                             classTypeId);
        getFragmentManager().beginTransaction()
                //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.dialogContainer, frag, KEY_DIALOG_CLASS_AUD)
                .addToBackStack(KEY_DIALOG_AUD)
                .commit();
    }

    private void showSumEntryAUDDialog(int dialogType, Long entryId) {
        getFragmentManager()
                .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        SumEntryAUDDialog frag = SumEntryAUDDialog
                .newInstance(this, CODE_DIALOG_SUM_ENTRY_AUD, dialogType, entryId, selectedGroupId,
                             selectedSubjectId);
//        getFragmentManager().beginTransaction()
//                //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                .replace(R.id.dialogContainer, frag, KEY_DIALOG_SUM_ENTRY_AUD)
//                .addToBackStack(KEY_DIALOG_AUD)
//                .commit();
        frag.show(getFragmentManager(), SumEntryAUDDialog.TAG);
    }

    private void showMarkAUDDialog(int dialogType, BlankTagHandler tagHandler) {
        getFragmentManager()
                .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        MarkAUDDialog
                .newInstance(this, CODE_DIALOG_MARK_AUD, dialogType, tagHandler)
                .show(getFragmentManager(), KEY_DIALOG_MARK_AUD);
    }

    private void showStudentAUDDialog(int dialogType, Long studentId, Long groupId) {
        getFragmentManager()
                .popBackStackImmediate(KEY_FRAGMENT_BLANK_STYLE,
                                       FragmentManager.POP_BACK_STACK_INCLUSIVE);
        StudentAUDDialog.newInstance(this, CODE_DIALOG_STUDENT_AUD, dialogType, studentId, groupId)
                .show(getFragmentManager(), KEY_DIALOG_AUD);
    }

    private void showStudentContextMenu(final long studentId) {
        final int MENU_ADD = 0;
        final int MENU_UPDATE = 1;
        final int MENU_DELETE_IN_GROUP = 2;
        final int MENU_DELETE = 3;
        final String[] itemList = new String[4];
        itemList[MENU_ADD] = getActivity().getResources().getString(R.string.menu_add);
        itemList[MENU_UPDATE] = getActivity().getResources().getString(R.string.menu_update);
        itemList[MENU_DELETE_IN_GROUP] =
                getActivity().getResources().getString(R.string.menu_delete_in_group);
        itemList[MENU_DELETE] = getActivity().getResources().getString(R.string.menu_delete_full);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setItems(itemList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case MENU_ADD:
                        showStudentAUDDialog(BaseAUDDialog.DIALOG_ADD, null, selectedGroupId);
                        break;
                    case MENU_UPDATE:
                        showStudentAUDDialog(BaseAUDDialog.DIALOG_UPDATE, studentId, null);
                        break;
                    case MENU_DELETE:
                        showStudentAUDDialog(BaseAUDDialog.DIALOG_DELETE, studentId, null);
                        break;
                    case MENU_DELETE_IN_GROUP:
                        dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
                        List<StudyGroup> groupList =
                                dbHelper.groups.getGroupsByStudentId(studentId);
                        if (groupList != null && groupList.size() > 1) {
                            dbHelper.students.deleteStudentFromGroup(studentId, selectedGroupId);
                            refreshBlank();
                        } else {
                            RunUtils.showToast(getActivity(),
                                               R.string.delete_student_only_one_group);
                        }
                        break;
                }
            }
        });
        adb.show();
    }

    private void showClassContextMenu(final long classId) {
        final int MENU_ADD = 0;
        final int MENU_UPDATE = 1;
        final int MENU_DELETE = 2;
        final String[] itemList = new String[3];
        itemList[MENU_ADD] = getActivity().getResources().getString(R.string.menu_add);
        itemList[MENU_UPDATE] = getActivity().getResources().getString(R.string.menu_update);
        itemList[MENU_DELETE] = getActivity().getResources().getString(R.string.menu_delete);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setItems(itemList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case MENU_ADD:
                        showClassAUDDialog(BaseAUDDialog.DIALOG_ADD, null, selectedSubjectId,
                                           selectedGroupId, selectedClassTypeId);
                        break;
                    case MENU_UPDATE:
                        showClassAUDDialog(BaseAUDDialog.DIALOG_UPDATE, classId, selectedSubjectId,
                                           selectedGroupId, selectedClassTypeId);
                        break;
                    case MENU_DELETE:
                        showClassAUDDialog(BaseAUDDialog.DIALOG_DELETE, classId, selectedSubjectId,
                                           selectedGroupId, selectedClassTypeId);
                        break;
                }
            }
        });
        adb.show();
    }

    private void showSumEntryContextMenu(final Long entryId) {
        final int MENU_ADD = 0;
        final int MENU_UPDATE = 1;
        final int MENU_DELETE = 2;
        final String[] itemList = new String[3];
        itemList[MENU_ADD] = getActivity().getResources().getString(R.string.menu_add);
        itemList[MENU_UPDATE] = getActivity().getResources().getString(R.string.menu_update);
        itemList[MENU_DELETE] = getActivity().getResources().getString(R.string.menu_delete);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setItems(itemList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case MENU_ADD:
                        showSumEntryAUDDialog(BaseAUDDialog.DIALOG_ADD, null);
                        break;
                    case MENU_UPDATE:
                        showSumEntryAUDDialog(BaseAUDDialog.DIALOG_UPDATE, entryId);
                        break;
                    case MENU_DELETE:
                        showSumEntryAUDDialog(BaseAUDDialog.DIALOG_DELETE, entryId);
                        break;
                }
            }
        });
        adb.show();
    }

    //============================= onDialogResult =================================================

    private void requestChoiceLogic(Bundle bundle) {
        Long subjectId = null;
        Long groupId = null;
        Long classTypeId = null;
        if (bundle.containsKey(Constants.KEY_SUBJECT_ID)) {
            subjectId = bundle.getLong(Constants.KEY_SUBJECT_ID);
        }
        if (bundle.containsKey(Constants.KEY_GROUP_ID)) {
            groupId = bundle.getLong(Constants.KEY_GROUP_ID);
        }
        if (bundle.containsKey(Constants.KEY_CLASS_TYPE_ID)) {
            classTypeId = bundle.getLong(Constants.KEY_CLASS_TYPE_ID);
        }
        if (subjectId != null && groupId != null && classTypeId != null) {
            getFragmentManager()
                    .popBackStack(KEY_DIALOG_CHOICE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            selectedGroupId = groupId;
            selectedSubjectId = subjectId;
            selectedClassTypeId = classTypeId;
            refreshBlank();
        } else {
            showChoiceDialog(subjectId, groupId, classTypeId);
        }
    }

    @Override
    public void onDialogResult(int requestCode, DialogResultCode resultCode,
            Bundle bundle) {

        if (resultCode == DialogResultCode.RESULT_OK) {

            if (requestCode == CODE_DIALOG_CHOICE_GROUP || requestCode ==
                    CODE_DIALOG_CHOICE_SUBJECT ||
                    requestCode == CODE_DIALOG_CHOICE_CLASS_TYPE) {
                requestChoiceLogic(bundle);
            } else if (requestCode == CODE_DIALOG_MARK_AUD) {
                if (bundle != null &&
                        bundle.containsKey(DialogClickListener.KEY_CALLBACK_BUNDLE_TAG)) {
                    BlankTagHandler tagHandler = (BlankTagHandler) bundle
                            .getSerializable(DialogClickListener.KEY_CALLBACK_BUNDLE_TAG);
                    refreshMark(tagHandler);
                }
            } else {
//            if (requestCode == CODE_DIALOG_CLASS_AUD || requestCode ==
//                    CODE_DIALOG_STUDENT_AUD
//                    || requestCode == CODE_DIALOG_CLASS_TYPE_AUD || requestCode == CODE_DIALOG_GROUP_AUD
//                    || requestCode == CODE_DIALOG_SUBJECT_AUD || requestCode == CODE_DIALOG_IMPORT || requestCode == CODE_DIALOG_SUM_ENTRY_AUD)
                refreshBlank();
            }

        } else if (resultCode == DialogResultCode.RESULT_NO) {
            getFragmentManager()
                    .popBackStack(KEY_DIALOG_CHOICE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public Loader<LinearLayout> onCreateLoader(int id, Bundle args) {

        Loader<LinearLayout> loader = null;
        switch (id) {
            case LOADER_BLANK_REFRESH:
                boolean reloadStyle = false;
                if (args != null) {
                    if (args.containsKey(LOADER_KEY_RELOAD_STYLE)) {
                        reloadStyle = args.getBoolean(LOADER_KEY_RELOAD_STYLE, false);
                    }
                }
                if (dimensionsList == null) progressBar.setVisibility(View.VISIBLE);
                loader = new BlankLoader(getActivity(), selectedGroupId, selectedSubjectId,
                                         selectedClassTypeId, reloadStyle, dimensionsList,
                                         isBlankSummary);
                dimensionsList = null;
                break;
            default:

        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<LinearLayout> loader, LinearLayout data) {
        progressBar.setVisibility(View.GONE);
        LinearLayout blankLayout = clearBlank();
        if (blankLayout != null && data != null) {
            exportBlank(exportKey, exportFileUri, data);
            ViewGroup parent = (ViewGroup) data.getParent();
            if (parent != null) parent.removeView(data);
            blankLayout.addView(data);
            data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BlankStyleFragment frag = (BlankStyleFragment) getFragmentManager()
                            .findFragmentByTag(BlankStyleFragment.TAG);
                    if (frag != null && frag.isVisible()) {
                        frag.btnClickListener.onClick(v);
                    } else {
                        blankClickListener.onBlankClick(v);
                    }
                }
            });
            data.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Fragment frag = getFragmentManager().findFragmentByTag(BlankStyleFragment.TAG);
                    if (frag != null && frag.isVisible()) {
                        //
                    } else {
                        blankClickListener.onBlankLongClick(v);
                    }
                    return false;
                }
            });
            refreshSemester(null);

        }

    }

    private void doExportImg() {
        if (selectedGroupId == null || selectedSubjectId == null ||
                (selectedClassTypeId == null && !isBlankSummary)) {
            RunUtils.showToast(getContext(), R.string.toast_blank_not_selected);
            return;
        }

        String fileName = "";
        dbHelper = SQLiteJournalHelper.getWritableInstance(getActivity());
        StudyGroup group = dbHelper.groups.get(selectedGroupId);
        if(group != null){
            fileName += group.getCode();
        }
        StudySubject subject = dbHelper.subjects.get(selectedSubjectId);
        if(subject != null){
            fileName += "_" + subject.getAbbr();
        }
        if (!isBlankSummary) {
            StudyClassType classType = dbHelper.classTypes.get(selectedClassTypeId);
            if (classType != null){
                fileName += "_" + classType.getAbbr();
            }
        }
        if(fileName.isEmpty()) return;
        pickLocationToSaveFile(fileName);
    }

    private void pickLocationToSaveFile(String filename) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TITLE, filename);

        startActivityForResult(intent, REQUEST_CODE_PICK_EXPORT_LOCATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_EXPORT_LOCATION && resultCode == Activity.RESULT_OK) {
            exportFileUri = data.getData();
            if (exportFileUri != null) {
                exportKey = ExportKey.EXPORT_IMG;
                refreshBlank(true, true);
            } else {
                exportKey = ExportKey.NO_EXPORT;
            }
        }
    }

    private void exportBlank(ExportKey exportKey, Uri exportFileUri, LinearLayout data) {
        if (data == null || exportFileUri == null || selectedGroupId == null || selectedSubjectId == null ||
                (selectedClassTypeId == null && !isBlankSummary)) {
            return;
        }

        switch (exportKey){
            case NO_EXPORT:
                break;
            case EXPORT_IMG:
                ExportUtils.exportToBitmap(getActivity(), exportFileUri, data);

                this.exportKey = ExportKey.NO_EXPORT;
                break;
            case EXPORT_CSV:
                ExportUtils.exportToCSV(getActivity(), exportFileUri, selectedGroupId,
                        selectedSubjectId,
                        selectedClassTypeId, isBlankSummary);
                break;
        }
    }


    @Override
    public void onLoaderReset(Loader<LinearLayout> loader) {
        progressBar.setVisibility(View.GONE);
    }

}
