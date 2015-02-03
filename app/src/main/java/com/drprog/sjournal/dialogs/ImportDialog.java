package com.drprog.sjournal.dialogs;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.TableGroups;
import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.db.utils.JournalImporter;
import com.drprog.sjournal.dialogs.utils.BaseDialogFragment;
import com.drprog.sjournal.dialogs.utils.StudentImportListAdapter;
import com.drprog.sjournal.R;
import com.drprog.sjournal.utils.IOFiles;
import com.drprog.sjournal.utils.RunUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 11.08.2014.
 */
public class ImportDialog extends BaseDialogFragment {
    private static final String TAG = "IMPORT_DIALOG_FRAGMENT";
    private static final String ARG_GROUP_ID = "ARG_GROUP_ID";
    private List<Student> studentList;
    private SQLiteJournalHelper dbHelper;
    private StudentImportListAdapter adapterListView;
    private Long argGroupId;
    private Spinner spinnerFile;
    private Spinner spinnerEncoding;
    private Spinner spinnerGroup;
    private ListView listView;
    private Button btnScan;
    private Button btnOk;
    private AsyncParseTask asyncParseTask;
    private AsyncImportTask asyncImportTask;
    private ProgressBar progressBar;


    public static ImportDialog newInstance(Fragment callback, Integer requestCode, Long groupId) {
        ImportDialog dialog = new ImportDialog();
        if (callback != null && requestCode != null) {
            dialog.setTargetFragment(callback, requestCode);
        }
        if (groupId != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(ARG_GROUP_ID, groupId);
            dialog.setArguments(bundle);
        }
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_GROUP_ID)) {
                argGroupId = getArguments().getLong(ARG_GROUP_ID);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (getDialog() != null) {
            getDialog().setTitle(R.string.dialog_import_students_title);
        }
        final View v = inflater.inflate(R.layout.dialog_import, container);
        spinnerFile = (Spinner) v.findViewById(R.id.spinner1);
        spinnerEncoding = (Spinner) v.findViewById(R.id.spinner2);
        spinnerGroup = (Spinner) v.findViewById(R.id.spinner3);
        listView = (ListView) v.findViewById(R.id.listView);
        btnScan = (Button) v.findViewById(R.id.button_scan);
        btnOk = (Button) v.findViewById(R.id.button_ok);
        TextView emptyViewFile = (TextView) v.findViewById(R.id.emptyFile);
        spinnerFile.setEmptyView(emptyViewFile);
        TextView emptyViewList = (TextView) v.findViewById(R.id.emptyList);
        listView.setEmptyView(emptyViewList);
        TextView emptyViewGroup = (TextView) v.findViewById(R.id.emptyGroup);
        spinnerGroup.setEmptyView(emptyViewGroup);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        IOFiles ioFiles = new IOFiles(getActivity());
        final List<File> fileList = ioFiles.searchFile(IOFiles.DIR_IMPORT, IOFiles.FILE_IMPORT_EXT);
        if (fileList != null) {
            ArrayAdapter<String> adapterFile =
                    new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                                             IOFiles.extractFileNames(
                                                     fileList)
                    );
            adapterFile.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFile.setAdapter(adapterFile);

        } else {
            RunUtils.showToast(getActivity(), R.string.error_iofile);
        }

        ArrayAdapter<String> adapterEncoding =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                                         getActivity().getResources()
                                                 .getStringArray(R.array.encoding));
        adapterEncoding.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEncoding.setAdapter(adapterEncoding);

        dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
        List<StudyGroup> groupList = dbHelper.groups.getAll(TableGroups.KEY_CODE);
        ArrayAdapter<StudyGroup> adapterGroup =
                new ArrayAdapter<StudyGroup>(getActivity(), android.R.layout.simple_spinner_item,
                                             groupList);
        adapterGroup.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapterGroup);
        int pos = getJournalItemPos(argGroupId, groupList);
        if (pos >= 0) spinnerGroup.setSelection(pos);

        if (studentList == null) { studentList = new ArrayList<Student>(); }
        adapterListView = new StudentImportListAdapter(getActivity(), studentList);
        listView.setAdapter(adapterListView);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLists();
                int pos = spinnerFile.getSelectedItemPosition();
                if (fileList != null && !fileList.isEmpty() && pos != -1) {
                    File file = fileList.get(pos);
                    if (!file.exists()) {
                        RunUtils.showToast(getActivity(), R.string.error_file_not_exists);
                        return;
                    }
                    if (file.length() > IOFiles.MAX_IMPORT_FILE_SIZE) {
                        RunUtils.showToast(getActivity(), R.string.error_file_size_limit);
                        return;
                    }
                    String charsetName = spinnerEncoding.getSelectedItem().toString();
                    if (charsetName == null) charsetName = "UTF-8";

                    //Async
                    asyncParseTask = new AsyncParseTask();
                    asyncParseTask.execute(new ImportFile(file, charsetName));
                }
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncDoImport();
                //showImportAlertDialog();
            }
        });

        if (studentList != null && !studentList.isEmpty()) {
            btnOk.setEnabled(true);
            if (asyncImportTask != null) {
                preImportExecute();

            }
        }

    }

    private void clearLists() {
        if (studentList != null) {
            studentList.clear();
        }
        ListAdapter oldAdapter = listView.getAdapter();
        if (oldAdapter != null) {
            ((StudentImportListAdapter) oldAdapter).clear();
        }
        btnOk.setEnabled(false);
    }

    private void refreshListView(List<Student> studentList) {
        this.studentList = studentList;

        if (listView.getAdapter() == null) {
            adapterListView = new StudentImportListAdapter(getActivity(), this.studentList);
        } else {
            adapterListView = ((StudentImportListAdapter) listView.getAdapter());
        }
        adapterListView.setStudentList(this.studentList);
        adapterListView.notifyDataSetChanged();

        if (studentList.isEmpty()) {
            btnOk.setEnabled(false);
            RunUtils.showToast(getActivity(), R.string.dialog_import_data_empty);
        } else {
            btnOk.setEnabled(true);
        }
    }

//    private void showImportAlertDialog() {
//        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
//        adb.setIcon(android.R.drawable.ic_dialog_info);
//        adb.setTitle(R.string.dialog_import_students_request_title);
//        adb.setMessage(R.string.dialog_import_students_request_message);
//        adb.setPositiveButton(android.R.string.ok,this);
//        adb.setNegativeButton(android.R.string.cancel,null);
//        adb.show();
//    }


    private void asyncDoImport() {
        Object o = spinnerGroup.getSelectedItem();
        if (o == null) {
            RunUtils.showToast(getActivity(), R.string.dialog_import_group_not_selected);
            return;
        }
        Long groupId = ((StudyGroup) o).getId();

        if (!(asyncImportTask != null &&
                asyncImportTask.getStatus() == AsyncTask.Status.RUNNING
                && !asyncImportTask.isCancelled())) {
            asyncImportTask = new AsyncImportTask();
            asyncImportTask.execute(groupId);
        }


    }

    private void preImportExecute() {
        progressBar.setIndeterminate(false);
        progressBar.setMax(studentList.size());
        spinnerGroup.setEnabled(false);
        spinnerEncoding.setEnabled(false);
        spinnerFile.setEnabled(false);
        listView.setVisibility(View.GONE);
        btnScan.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);
        if (getDialog() != null) { getDialog().setCanceledOnTouchOutside(false); }
    }

    private void postImportExecute(String s) {
        showResultDialog(s);
        sendCallbackOk();
        dismiss();
    }

    private void showResultDialog(String s) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setIcon(android.R.drawable.ic_dialog_info);
        adb.setTitle(R.string.dialog_import_successful);
        adb.setMessage(s);
        adb.setPositiveButton(android.R.string.ok, null);
        adb.show();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) { getDialog().setDismissMessage(null); }
        super.onDestroyView();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (asyncImportTask != null && !asyncImportTask.isCancelled() &&
                asyncImportTask.getStatus() ==
                        AsyncTask.Status.RUNNING) {
            asyncImportTask.cancel(false);
        }
    }

    public class ImportFile {
        public File file;
        public String charsetName = "UTF-8";

        public ImportFile(File file, String charsetName) {
            this.file = file;
            this.charsetName = charsetName;
        }
    }

    private class AsyncParseTask extends AsyncTask<ImportFile, Integer, List<Student>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setIndeterminate(true);
        }

        @Override
        protected List<Student> doInBackground(ImportFile... params) {
            ImportFile importFile = params[0];
            return JournalImporter.importStudentsFromFile(importFile.file, importFile.charsetName);
        }

        @Override
        protected void onPostExecute(List<Student> studentList) {
            super.onPostExecute(studentList);
            progressBar.setIndeterminate(false);
            refreshListView(studentList);
        }

    }

    private class AsyncImportTask extends AsyncTask<Long, Integer, String> {
        SQLiteJournalHelper dbHelper;
        String resultFormat;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dbHelper = SQLiteJournalHelper.getInstance(getActivity(), true);
            resultFormat = getString(R.string.dialog_result_format);
            preImportExecute();
        }

        @Override
        protected String doInBackground(Long... params) {
            dbHelper.setFrozenOn(TAG);
            Long groupId = params[0];
            int numImported = 0;
            int numGeneratedId = 0;
            int size = studentList.size();
            Long id;
            for (int pos = 0; pos < studentList.size(); pos++) {
                if (isCancelled()) {
                    break;
                }
                Student student = studentList.get(pos);
                if (student.isValid() != Student.CODE_VALID) continue;
                id = student.getId();
                if (dbHelper.students.exists(id)) continue;
                if (id == null) {
                    student.setId(null);
                    numGeneratedId++;
                }
                dbHelper.getWritableDatabase().beginTransaction();
                try {
                    long res1 = dbHelper.students.insert(student);
                    long res2 = dbHelper.students.insertStudentInGroup(res1, groupId);
                    if (res1 != -1 && res2 != -1) {
                        numImported++;
                        dbHelper.getWritableDatabase().setTransactionSuccessful();
                    }
                } finally {
                    dbHelper.getWritableDatabase().endTransaction();
                }
                publishProgress(pos + 1);
            }
            dbHelper.setFrozenOff(TAG);

            String result = String.format(resultFormat, numImported,
                                          size, numGeneratedId);

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btnScan.setEnabled(true);
            spinnerGroup.setEnabled(true);
            spinnerEncoding.setEnabled(true);
            spinnerFile.setEnabled(true);
            postImportExecute(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dbHelper.setFrozenOff(TAG);
        }
    }

}
