package com.drprog.sjournal.blank;

import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by Romka on 01.02.14.
 */
public class BlankTagHandler implements Serializable {


    private TextView textView;
    private int viewId;
    private CeilType ceilType = CeilType.NONE;
    //private int defTextSize;
    private Long classId;
    private Long studentId;
    private String text;
    private int value = 0;
    public BlankTagHandler() {
    }

    public BlankTagHandler(CeilType ceilType, int viewId) {
        this.ceilType = ceilType;
        this.viewId = viewId;
    }

    public BlankTagHandler(CeilType ceilType, Long classId, Long studentId, String text) {
        this.ceilType = ceilType;
        this.classId = classId;
        this.studentId = studentId;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        this.text = value != 0 ? String.valueOf(value) : "";
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public CeilType getCeilType() {
        return ceilType;
    }

    public void setCeilType(CeilType ceilType) {
        this.ceilType = ceilType;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
        //TODO: Implement toString Logic with iPref
    }

    // Do not rewrite
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        BlankTagHandler that = (BlankTagHandler) o;

        //if (ceilType != that.ceilType) return false;
        if (classId != null ? !classId.equals(that.classId) : that.classId != null) return false;
        if (studentId != null ? !studentId.equals(that.studentId) : that.studentId != null) {
            return false;
        }
        //if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ceilType != null ? ceilType.hashCode() : 0;
        result = 31 * result + (classId != null ? classId.hashCode() : 0);
        result = 31 * result + (studentId != null ? studentId.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    public static enum CeilType {
        NONE, CLASS, SUM_ENTRY, STUDENT, NO_MARK, MARK, SYMBOL, ABSENT,
        ADD_COL_CEIL, ADD_ROW_CEIL, ADD_COL_CEIL_SUM, SUM_RESULT, ABSENT_ROW_CEIL
    }
}
