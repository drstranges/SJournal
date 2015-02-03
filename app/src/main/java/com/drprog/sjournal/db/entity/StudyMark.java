package com.drprog.sjournal.db.entity;

import com.drprog.sjournal.db.utils.BaseJournalEntity;

/**
 * Created by Romka on 01.02.14.
 */
public class StudyMark extends BaseJournalEntity {

    public static final int TYPE_NO_MARK = 0;
    public static final int TYPE_MARK = 1;
    public static final int TYPE_SYMBOL = 2;
    public static final int TYPE_ABSENT = 3;

    //private Long id;
    private long studentId;
    private long classId;
    private int type;
    private Integer mark;
    private String symbol;
    private String note;

    public StudyMark() {
    }

    public StudyMark(Long id, long studentId, long classId, int type, Integer mark, String symbol,
            String note) {
        this.id = id;
        this.studentId = studentId;
        this.classId = classId;
        this.type = type;
        this.mark = mark;
        this.symbol = symbol;
        this.note = note;
    }

    public StudyMark(long studentId, long classId, int type) {
        this.studentId = studentId;
        this.classId = classId;
        this.type = type;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        switch (type) {
            case TYPE_MARK:
                return String.valueOf(mark);
            case TYPE_SYMBOL:
                return symbol;
            case TYPE_ABSENT:
                return symbol;
            case TYPE_NO_MARK:
                break;
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        StudyMark studyMark = (StudyMark) o;

        if (classId != studyMark.classId) return false;
        return studentId == studyMark.studentId;

    }

    @Override
    public int hashCode() {
        int result = (int) (studentId ^ (studentId >>> 32));
        result = 31 * result + (int) (classId ^ (classId >>> 32));
        return result;
    }
}
