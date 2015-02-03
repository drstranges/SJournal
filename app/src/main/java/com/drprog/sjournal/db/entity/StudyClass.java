package com.drprog.sjournal.db.entity;

import com.drprog.sjournal.db.utils.BaseJournalEntity;

/**
 * Created by Romka on 01.02.14.
 */
public class StudyClass extends BaseJournalEntity {
    //private Long id;
    private int semester;
    private long groupId;
    private long subjectId;
    private long classTypeId;
    private String date;
    private String theme;
    private String abbr;
    private Integer rowWidth;
    private Integer rowColor;
    private Integer textColor;
    private String note;


    public StudyClass() {
    }

    public StudyClass(Long id, int semester, long groupId, long subjectId, long classTypeId,
            String date, String theme, String abbr, Integer rowWidth,
            Integer rowColor, Integer textColor, String note) {
        this.id = id;
        this.semester = semester;
        this.groupId = groupId;
        this.subjectId = subjectId;
        this.classTypeId = classTypeId;
        this.date = date;
        this.theme = theme;
        this.abbr = abbr;
        this.rowWidth = rowWidth;
        this.rowColor = rowColor;
        this.textColor = textColor;
        this.note = note;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public long getClassTypeId() {
        return classTypeId;
    }

    public void setClassTypeId(long classTypeId) {
        this.classTypeId = classTypeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public Integer getRowWidth() {
        return rowWidth;
    }

    public void setRowWidth(Integer rowWidth) {
        this.rowWidth = rowWidth;
    }

    public Integer getRowColor() {
        return rowColor;
    }

    public void setRowColor(Integer rowColor) {
        this.rowColor = rowColor;
    }

    public Integer getTextColor() {
        return textColor;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
//        final StringBuffer sb = new StringBuffer(abbr);
//        sb.append("\n").append(date);
//        return sb.toString();
        return abbr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        StudyClass that = (StudyClass) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (classTypeId != that.classTypeId) return false;
        if (groupId != that.groupId) return false;
        if (semester != that.semester) return false;
        if (subjectId != that.subjectId) return false;
        if (abbr != null ? !abbr.equals(that.abbr) : that.abbr != null) return false;
        if (!date.equals(that.date)) return false;
        return !(theme != null ? !theme.equals(that.theme) : that.theme != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + semester;
        result = 31 * result + (int) (groupId ^ (groupId >>> 32));
        result = 31 * result + (int) (subjectId ^ (subjectId >>> 32));
        result = 31 * result + (int) (classTypeId ^ (classTypeId >>> 32));
        result = 31 * result + date.hashCode();
        result = 31 * result + (theme != null ? theme.hashCode() : 0);
        result = 31 * result + (abbr != null ? abbr.hashCode() : 0);
        return result;
    }
}
