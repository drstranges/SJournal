package com.drprog.sjournal.blank;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.widget.LinearLayout;

import com.drprog.sjournal.R;
import com.drprog.sjournal.db.SQLiteJournalHelper;
import com.drprog.sjournal.db.TableClasses;
import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.db.entity.StudyClass;
import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.db.entity.StudyMark;
import com.drprog.sjournal.preferences.PrefsManager;
import com.drprog.sjournal.utils.IOFiles;
import com.drprog.sjournal.utils.RunUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Romka on 31.08.2014.
 */
public class ExportUtils {

    public static void exportToBitmap(Context ctx, Uri destinationfileUri, LinearLayout data){
        Bitmap bitmap = getBitmapFromView(data);
        Boolean saved = IOFiles.saveImage(ctx, bitmap, destinationfileUri);
        if (saved) {
            RunUtils.showToast(ctx, R.string.toast_blank_export_success);
        }
    }

    private static Bitmap getBitmapFromView(LinearLayout view){

        view.measure(LinearLayout.LayoutParams.WRAP_CONTENT,
                     LinearLayout.LayoutParams.WRAP_CONTENT);

        //view.measure(LinearLayout.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        //DebugUtils.testImport(view);




        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                                            view.getHeight(),
                                            Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);
        return bitmap;
    }


    public static void exportToCSV(Context context, Uri destinationfileUri, Long groupId,
            Long subjectId, Long classTypeId, boolean isBlankSummary) {
      /*  if (context == null || fileName == null || fileName.isEmpty() || groupId == null
                || subjectId == null || classTypeId == null) return;
        //TODO: Implement export
        SQLiteJournalHelper dbHelper = SQLiteJournalHelper.getInstance(context,true);
        StudyGroup studyGroup = dbHelper.groups.get(groupId);
        if (studyGroup == null) return;
        List<Student> studentList = dbHelper.students.getStudentsByGroupId(groupId);
        String absentSymbol = PrefsManager.getInstance(context).getPrefs()
                .getString(PrefsManager.PREFS_BLANK_ABSENT_SYMBOL,
                           context.getResources().getString(R.string.symbol_absent));
        Integer iStudentPref = Integer.valueOf(PrefsManager.getInstance(context).getPrefs()
                                                .getString(PrefsManager.PREFS_BLANK_NAME_FORMAT,
                                                           "0"));
        Integer iClassPref = Integer.valueOf(PrefsManager.getInstance(context).getPrefs()
                                                .getString(PrefsManager.PREFS_BLANK_CLASS_STYLE,
                                                           "0"));
        //IOFiles ioFiles = new IOFiles(context);
        List<String> csvLineList = new ArrayList<String>();

        if(isBlankSummary){

        }else{
            List<StudyClass> classList = dbHelper.classes
                    .getClasses(groupId, subjectId, classTypeId, studyGroup.getSemester());
            List<StudyMark> markList =
                    dbHelper.marks.getMarks(groupId, subjectId, classTypeId, studyGroup.getSemester());
            StringBuilder sb = new StringBuilder(context.getResources().getString(R.string.fio));
            for(StudyClass studyClass:classList) {
                sb.append(";").append(getClassText(context,studyClass,iClassPref));
            }


            csvLineList.add(sb.toString());
            for(Student student:studentList){
                StringBuilder sb = new StringBuilder(getStudentText(student,iStudentPref));
                sb.append(";").append()

                csvLineList.add(sb.toString());
            }
        }*/
    }


    private static String findMarkText(List<StudyMark> markList, long studentId, long classId, String absentSmbol){
        String text = " ";
        if (markList == null) return text;
        for (StudyMark mark : markList) {
            if ((mark.getStudentId() == studentId) && (mark.getClassId() == classId)) {
                switch (mark.getType()) {
                    case StudyMark.TYPE_MARK:
                        return String.valueOf(mark);
                    case StudyMark.TYPE_SYMBOL:
                        return mark.getSymbol();
                    case StudyMark.TYPE_ABSENT:
                        return absentSmbol;
                    default:
                }
                break;
            }
        }
        return text;
    }
    private static String getStudentText(Student student, int formatPref){
        String str = " ";
        switch (formatPref) {
            case 0:
                str = String.format("%s %c.%c.",
                                    student.getLastName(),
                                    student.getFirstName().charAt(0),
                                    student.getMiddleName().charAt(0));
                break;
            case 1:
                str = String.format("%s %s %s",
                                    student.getLastName(),
                                    student.getFirstName(),
                                    student.getMiddleName());
                break;
            case 2:
                str = String.format("%s %s %c.", student.getLastName(),
                                    student.getFirstName(),
                                    student.getMiddleName().charAt(0));
                break;
            case 3:
                str = String.format("%s %s", student.getLastName(),
                                    student.getFirstName());
                break;
            case 4:
                str = String.format("%s %c.", student.getLastName(),
                                    student.getFirstName().charAt(0));
                break;
            default:
                str = "err";
        }
        return str;
    }
    private static String getClassText(Context context, StudyClass studyClass, int formatPref){
        String str = " ";
        switch (formatPref) {
            case 0:
                str = studyClass.getAbbr() + "\n" + formatDate(context,studyClass.getDate());
                break;
            case 1:
                str = studyClass.getAbbr();
                break;
            case 2:
                str = formatDate(context,studyClass.getDate());
                break;
            default:
                str = "err";
        }
        return str;
    }

    private static String formatDate(Context context, String dateToFormat) {
        String finalDateTime = "";
        if (dateToFormat != null) {
            try {
                Date date = TableClasses.dateFormat.parse(dateToFormat);
                finalDateTime =
                        android.text.format.DateFormat.getDateFormat(context).format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return finalDateTime;

    }
}
