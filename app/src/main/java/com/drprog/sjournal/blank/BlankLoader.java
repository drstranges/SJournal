package com.drprog.sjournal.blank;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.widget.LinearLayout;

import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.db.entity.StudyClass;
import com.drprog.sjournal.db.entity.StudyClassType;
import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.db.entity.StudyMark;
import com.drprog.sjournal.db.entity.StudySubject;
import com.drprog.sjournal.db.entity.SummaryEntry;
import com.drprog.sjournal.db.prefs.Dimensions;
import com.drprog.sjournal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 15.08.2014.
 * Used example by Alex Lockwood
 * http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
 */
public class BlankLoader extends AsyncTaskLoader<LinearLayout> {
    public static final String TAG = "BLANK_LOADER";
    private Long groupId;
    private Long subjectId;
    private Long classTypeId;
    private boolean isBlankSummary = false;
    private boolean isCancelled = false;
    private LinearLayout mData;
    private Blank blank;

    public BlankLoader(Context context, Long groupId, Long subjectId, Long classTypeId,
            boolean reloadStyle, List<Dimensions> dimensionsList, boolean isBlankSummary) {
        //,Blank.OnBlankClickListener blankClickListener) {
        super(context);
//        mData = new LinearLayout(context);
        isCancelled = false;
        this.groupId = groupId;
        this.subjectId = subjectId;
        this.classTypeId = classTypeId;
        this.isBlankSummary = isBlankSummary;
        if (blank == null) {
            blank = new Blank(context, null, R.string.fio);
        } else {
            blank.setContext(context);
            if (reloadStyle) blank.reloadDimensionProfile();
        }
        if (!reloadStyle && dimensionsList != null && !dimensionsList.isEmpty()) {
            blank.setDimensionsList(dimensionsList, true);
            //isCancelled = true;
            //TODO: not draw, only change style...
        }
        //blank.setBlankClickListener(blankClickListener);
        blank.setBlankLayout(new LinearLayout(context));
    }


    @Override
    public LinearLayout loadInBackground() {
        if (isCancelled) return null;
        if (isBlankSummary) {
            return drawBlankOfSummary();
        } else {
            return drawBlankOfClasses();
        }
    }

    public LinearLayout drawBlankOfClasses() {
        if (groupId == null || subjectId == null || classTypeId == null) return null;

        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getContext());
        dbHelper.setFrozenOn(TAG);
        try {

            StudyGroup studyGroup = dbHelper.groups.get(groupId);
            StudySubject studySubject = dbHelper.subjects.get(subjectId);
            StudyClassType studyClassType = dbHelper.classTypes.get(classTypeId);
            if (studyGroup == null || studySubject == null || studyClassType == null) return null;
            blank.setData(studyGroup.getCode() + "  |  " +
                                  studySubject.toString() + "  |  " +
                                  studyClassType.getAbbr(),
                          dbHelper.classes.getClasses(groupId, subjectId, classTypeId,
                                                      studyGroup.getSemester()),
                          dbHelper.students.getStudentsByGroupId(groupId),
                          dbHelper.marks.getMarks(groupId, subjectId, classTypeId,
                                                  studyGroup.getSemester())
            );
        } finally {
            dbHelper.setFrozenOff(TAG);
        }
        return blank.drawBlank();
    }

    public LinearLayout drawBlankOfSummary() {
        if (groupId == null || subjectId == null) return null;

        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getWritableInstance(getContext());
        dbHelper.setFrozenOn(TAG);
        try {
            StudyGroup studyGroup = dbHelper.groups.get(groupId);
            StudySubject studySubject = dbHelper.subjects.get(subjectId);
            if (studyGroup == null || studySubject == null) return null;

            List<Student> studentList = dbHelper.students.getStudentsByGroupId(groupId);

            List<SummaryEntry> summaryEntryList = dbHelper.summaryEntries
                    .getEntries(groupId, subjectId, studyGroup.getSemester());

            List<BlankTagHandler> tagHandlerList = new ArrayList<BlankTagHandler>();

            for (SummaryEntry entry : summaryEntryList) {
                entry.ruleList = dbHelper.rules.getRules(entry.getId());
                if (entry.getClassId() != null) {
                    StudyClass studyClass = dbHelper.classes.get(entry.getClassId());
                    if (studyClass != null) {
                        entry.setText(studyClass.getAbbr());
                    } else {
                        entry.setText("NaN");
                    }

                    List<StudyMark> studyMarkList =
                            dbHelper.marks.getMarks(groupId, entry.getClassId());
                    for (StudyMark mark : studyMarkList) {
                        BlankTagHandler tagHandler =
                                new BlankTagHandler(BlankTagHandler.CeilType.MARK,
                                                    R.id.blank_class_body);
                        tagHandler.setClassId(entry.getId());
                        tagHandler.setStudentId(mark.getStudentId());
                        tagHandler.setValue(entry.applyRules(getContext(), mark));
                        tagHandlerList.add(tagHandler);
                    }
                } else {
                    String text = "";
                    if (entry.getClassTypeId() != null) {
                        StudyClassType classType = dbHelper.classTypes.get(entry.getClassTypeId());
                        if (classType != null) {
                            text = classType.getAbbr() + "\n";
                        } else {
                            entry.setText("NaN");
                        }
                    }
                    text = text + getContext().getResources().getString(R.string.absent_num);
                    entry.setText(text);

                    for (Student student : studentList) {
                        BlankTagHandler tagHandler =
                                new BlankTagHandler(BlankTagHandler.CeilType.MARK,
                                                    R.id.blank_class_body);
                        tagHandler.setClassId(entry.getId());
                        tagHandler.setStudentId(student.getId());
                        int absentNum = dbHelper.marks.getAbsentNumInSemester(groupId, student.getId(),
                                                                              entry.getSubjectId(),
                                                                              entry.getClassTypeId(),
                                                                              studyGroup
                                                                                      .getSemester()
                        );
                        tagHandler.setValue(entry.applyRules(absentNum));
                        tagHandlerList.add(tagHandler);
                    }
                }
            }

            blank.setDataSum(studyGroup.getCode() + "  |  " + studySubject.toString(),
                             summaryEntryList, studentList, tagHandlerList);
        } finally {
            dbHelper.setFrozenOff(TAG);
        }
        return blank.drawSummaryBlank();
    }

    @Override
    public void deliverResult(LinearLayout data) {
        if (isCancelled) return;
        if (isReset()) {
            data = null;
        }

        //LinearLayout oldData = data;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);//oldData);
        }

//        if (oldData != null && oldData != data) {
//            oldData = null;
//        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        //Observer?

        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mData != null) {
            mData = null;
        }
    }

    @Override
    public void onCanceled(LinearLayout data) {
        super.onCanceled(data);
    }
}
