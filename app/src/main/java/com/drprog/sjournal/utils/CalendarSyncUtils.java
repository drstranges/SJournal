package com.drprog.sjournal.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import com.drprog.sjournal.db.entity.StudyClassType;
import com.drprog.sjournal.db.entity.StudyGroup;
import com.drprog.sjournal.db.entity.StudySubject;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Romka on 30.08.2014.
 */
public class CalendarSyncUtils {
    public static class GSTHandler{
        public long groupId = -1;
        public long subjectId = -1;
        public long classTypeId = -1;
        public String eventTitle;

        public GSTHandler(long groupId, long subjectId, long classTypeId, String eventTitle) {
            this.groupId = groupId;
            this.subjectId = subjectId;
            this.classTypeId = classTypeId;
            this.eventTitle = eventTitle;
        }

    }

    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Instances.TITLE,
            //CalendarContract.Events.DTSTART,
//            CalendarContract.Events.DTEND,
            //CalendarContract.Events.DESCRIPTION
    };

    private static final int PROJECTION_TITLE = 0;
//    private static final int PROJECTION_DTSTART = 1;
//    private static final int PROJECTION_DTEND = 2;
    //private static final int PROJECTION_DESCRIPTION = 3;





    public static GSTHandler syncCalendarEvents(Context context, long window, List<StudyGroup> groupList, List<StudySubject> subjectList, List<StudyClassType> classTypeList){
        if (groupList == null || groupList.isEmpty()
                || subjectList == null || subjectList.isEmpty()
                || classTypeList == null || classTypeList.isEmpty()) return null;

        GSTHandler gstHandler = null;
        StudyGroup group = null;
        StudySubject subject = null;
        StudyClassType classType = null;

        long time = Calendar.getInstance().getTimeInMillis();
        //Uri uri = CalendarContract.Instances.CONTENT_URI;
        //String selection = "((" + CalendarContract.Instances.DTSTART + " <= ?) and (" + CalendarContract.Instances.DTEND + " >= ?))";
        //String[] selectionArgs = new String[]{String.valueOf(time - window), String.valueOf(time + window)};
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, time - window);
        ContentUris.appendId(builder, time + window);

        Cursor cur = context.getContentResolver()
                .query(builder.build(),//uri,
                       EVENT_PROJECTION,
                       null,//selection,
                       null,//selectionArgs,
                       CalendarContract.Events.DTSTART + " asc");               //sortOrder

        if (checkCursor(cur)){
            do {
                String title = cur.getString(PROJECTION_TITLE);

                if(title != null){
                    group = checkGroupContent(groupList, title);
                    subject = checkSubjectContent(subjectList, title);
                    classType =  checkClassTypeContent(classTypeList, title);
                    if(group != null && subject != null && classType != null){
                        cur.close();
                        return new GSTHandler(group.getId(),subject.getId(),classType.getId(), title);
                    }

                }
            }
            while (cur.moveToNext());
            cur.close();
        }

        return gstHandler;
    }

    private static StudyClassType checkClassTypeContent(List<StudyClassType> itemList, String title) {
        if(title == null) return null;
        title = title.toLowerCase();
        for(StudyClassType item:itemList){
            if(title.matches(".*\\b" + item.getAbbr().toLowerCase() + "\\b.*")) {
                return item;
            }
        }
        return null;
    }

    private static StudySubject checkSubjectContent(List<StudySubject> itemList, String title) {
        if(title == null) return null;
        title = title.toLowerCase();
        for(StudySubject item:itemList){
            if(title.matches(".*\\b" + item.getAbbr().toLowerCase() + "\\b.*")) {
                return item;
            }
        }
        return null;
    }

    private static StudyGroup checkGroupContent(List<StudyGroup> itemList, String title) {
        if(title == null) return null;
        title = title.toLowerCase();
        //Log.d("SJournal_Debug",title);
        for(StudyGroup item:itemList){
            //Log.d("SJournal_Debug",item.getCode().toLowerCase().toString());
            if(title.matches(".*\\b" + item.getCode().toLowerCase() + "\\b.*")) {
                //Log.d("SJournal_Debug","Selected: " + item.toString());
                return item;
            }
        }
        return null;
    }


    public static boolean checkCursor(Cursor cur) {
        if (cur == null) return false;
        if (!cur.moveToFirst()) {
            cur.close();
            return false;
        }
        return true;
    }
}
