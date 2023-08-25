package com.drprog.sjournal.db.utils;

import android.net.Uri;

import com.drprog.sjournal.App;
import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.utils.IOFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romka on 11.08.2014.
 */
public class JournalImporter {
    public static final int FIELD_ID = 0;
    public static final int FIELD_LAST_NAME = 1;
    public static final int FIELD_FIRST_NAME = 2;
    public static final int FIELD_MIDDLE_NAME = 3;
    public static final int FIELD_EMAIL = 4;
    public static final int FIELD_MOB_PHONE = 5;
    public static final int FIELD_PHONE = 6;
    public static final int FIELD_NOTE = 7;

    public static List<Student> importStudentsFromFile(Uri fileUri, String charsetName) {
        List<Student> studentList = new ArrayList<Student>();
        List<String> stringList = null;
        try {
            stringList = IOFiles.readFileEx(App.sInstance, fileUri, charsetName);
        } catch (IOException e) {
            return studentList;
        }
        if (stringList.isEmpty()) return studentList;
        String[] splitStrings;
        boolean isWithId;
        String numStr;
        for (String string : stringList) {
            string = string.replaceAll("\\p{C}", "");
            splitStrings = string.split(";");
            if (splitStrings.length < 2) {
                splitStrings = string.split(",");
                if (splitStrings.length == 0) continue;
            }
            int initPos = 0;
            while (splitStrings.length > initPos + 1 && splitStrings[initPos].equals("")) {
                initPos++;
            }
            if (splitStrings.length < initPos + FIELD_ID + 1) continue;
            isWithId = isNumeric(splitStrings[initPos + FIELD_ID]);
            Student student = new Student();
            if (isWithId) {
                student.setId(Long.parseLong(splitStrings[initPos + FIELD_ID]));
            } else { initPos--; }
            int length = splitStrings.length - initPos;
            if (length > FIELD_LAST_NAME) {
                student.setLastName(splitStrings[initPos + FIELD_LAST_NAME]);
            }
            if (length > FIELD_FIRST_NAME) {
                student.setFirstName(splitStrings[initPos + FIELD_FIRST_NAME]);
            }
            if (length > FIELD_MIDDLE_NAME) {
                student.setMiddleName(splitStrings[initPos + FIELD_MIDDLE_NAME]);
            }
            if (length > FIELD_EMAIL) { student.setEmail(splitStrings[initPos + FIELD_EMAIL]); }
            if (length > FIELD_MOB_PHONE) {
                numStr = getNumeric(splitStrings[initPos + FIELD_MOB_PHONE]);
                if (!numStr.isEmpty()) { student.setMobilePhone(numStr); }
            }
            if (length > FIELD_PHONE) {
                numStr = getNumeric(splitStrings[initPos + FIELD_PHONE]);
                if (!numStr.isEmpty()) { student.setPhone(numStr); }
            }
            if (length > FIELD_NOTE) { student.setNote(splitStrings[initPos + FIELD_NOTE]); }
            studentList.add(student);
        }


        return studentList;
    }

    public static boolean isNumeric(String str) {
        return str.matches("\\d+");  //match a number with optional '-' and decimal.
    }

    public static String getNumeric(String str) {
        return str.replaceAll("[^\\d]", "");
    }

}
