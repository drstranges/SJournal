package com.drprog.sjournal.dialogs.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.drprog.sjournal.db.entity.Student;
import com.drprog.sjournal.R;

import java.util.List;

/**
 * Created by Romka on 12.08.2014.
 */
public class StudentImportListAdapter extends BaseAdapter {
    private static final int COLOR_VALID = Color.parseColor("#8FBC8F");
    private static final int COLOR_INVALID = Color.YELLOW;
    private static final String ENTRY_NOT_SPECIFY_MSG = "[???]";
    private List<Student> studentList;
    private Context context;

    public StudentImportListAdapter(Context context, List<Student> studentList) {
        this.studentList = studentList;
        this.context = context;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    @Override
    public int getCount() {
        return studentList.size();
    }

    @Override
    public Object getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        Long id = studentList.get(position).getId();
        return id != null ? id : -1L;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_import_student, parent,false);
            TagHandler tagHandler = new TagHandler();
            tagHandler.initField(convertView);
            convertView.setTag(tagHandler);
        }

        Student student = studentList.get(position);

        TagHandler tagHandler = (TagHandler) convertView.getTag();
        if (student.getId() != null) {
            tagHandler.viewStudentIdCaption.setVisibility(View.VISIBLE);
            tagHandler.viewStudentId.setVisibility(View.VISIBLE);
            tagHandler.viewStudentId.setText(student.getId().toString());
        } else {
            tagHandler.viewStudentId.setText("");
            tagHandler.viewStudentIdCaption.setVisibility(View.GONE);
            tagHandler.viewStudentId.setVisibility(View.GONE);
        }
        tagHandler.viewLastName.setText(student.getLastName());
        tagHandler.viewFirstName.setText(student.getFirstName());
        tagHandler.viewMiddleName.setText(student.getMiddleName());
        tagHandler.viewEmail.setText(student.getEmail());
        tagHandler.viewMobPhone.setText(student.getMobilePhone());
        tagHandler.viewPhone.setText(student.getPhone());
        tagHandler.viewNote.setText(student.getNote());
        int errorCode = student.isValid();
        if (errorCode == Student.CODE_VALID) {
            convertView.setBackgroundColor(COLOR_VALID);
            tagHandler.viewLastName.setBackgroundColor(Color.TRANSPARENT);
            tagHandler.viewFirstName.setBackgroundColor(Color.TRANSPARENT);
        } else {
            convertView.setBackgroundColor(COLOR_INVALID);
            if ((errorCode & Student.CODE_ERROR_FIRST_NAME) != 0) {
                tagHandler.viewFirstName.setText(ENTRY_NOT_SPECIFY_MSG);
                tagHandler.viewFirstName.setBackgroundColor(Color.RED);
            }
            if ((errorCode & Student.CODE_ERROR_LAST_NAME) != 0) {
                tagHandler.viewLastName.setText(ENTRY_NOT_SPECIFY_MSG);
                tagHandler.viewLastName.setBackgroundColor(Color.RED);
            }
        }
        return convertView;
    }

    public void clear() {
        studentList.clear();
        notifyDataSetChanged();
    }

    private static class TagHandler {
        TextView viewStudentIdCaption;
        TextView viewStudentId;
        TextView viewLastName;
        TextView viewFirstName;
        TextView viewMiddleName;
        TextView viewEmail;
        TextView viewMobPhone;
        TextView viewPhone;
        TextView viewNote;

        public void initField(View convertView) {
            viewStudentIdCaption = (TextView) convertView.findViewById(R.id.viewStudentIdCaption);
            viewStudentId = (TextView) convertView.findViewById(R.id.viewStudentId);
            viewLastName = (TextView) convertView.findViewById(R.id.viewLastName);
            viewFirstName = (TextView) convertView.findViewById(R.id.viewFirstName);
            viewMiddleName = (TextView) convertView.findViewById(R.id.viewMiddleName);
            viewEmail = (TextView) convertView.findViewById(R.id.viewEmail);
            viewMobPhone = (TextView) convertView.findViewById(R.id.viewMobPhone);
            viewPhone = (TextView) convertView.findViewById(R.id.viewPhone);
            viewNote = (TextView) convertView.findViewById(R.id.viewNote);
        }
    }
}
