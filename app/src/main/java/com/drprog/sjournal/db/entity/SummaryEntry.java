package com.drprog.sjournal.db.entity;

import android.content.Context;

import com.drprog.sjournal.db.utils.BaseJournalEntity;
import com.drprog.sjournal.preferences.PrefsManager;
import com.drprog.sjournal.R;

import java.util.List;

/**
 * Created by Romka on 01.02.14.
 */
public class SummaryEntry extends BaseJournalEntity {
    public List<Rule> ruleList;
    private String text;
    private int semester;
    private long groupId;
    private long subjectId;
    private Long classTypeId;
    private Long classId;
    private Integer rowWidth;
    private Integer rowColor;
    private Integer textColor;
    private boolean isCounted = true;

    public SummaryEntry() {
    }

    public SummaryEntry(int semester, long groupId, long subjectId, Long classTypeId,
            Long classId) {
        this.semester = semester;
        this.groupId = groupId;
        this.subjectId = subjectId;
        this.classTypeId = classTypeId;
        this.classId = classId;
    }


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


    public Long getClassTypeId() {
        return classTypeId;
    }

    public void setClassTypeId(Long classTypeId) {
        this.classTypeId = classTypeId;
    }

    public List<Rule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Rule> ruleList) {
        this.ruleList = ruleList;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SummaryEntry that = (SummaryEntry) o;

        if (groupId != that.groupId) return false;
        if (semester != that.semester) return false;
        if (subjectId != that.subjectId) return false;
        return !(classTypeId != null ? !classTypeId.equals(that.classTypeId) :
                that.classTypeId != null);

    }

    @Override
    public int hashCode() {
        int result = semester;
        result = 31 * result + (int) (groupId ^ (groupId >>> 32));
        result = 31 * result + (int) (subjectId ^ (subjectId >>> 32));
        result = 31 * result + (classTypeId != null ? classTypeId.hashCode() : 0);
        return result;
    }

    public int applyRules(int num) {
        if (ruleList.isEmpty()) return num;
        Integer res;
        for (Rule rule : ruleList) {
            res = rule.applyTo(num);
            if (res != null) return res;
        }
        return 0;
    }

    public int applyRules(String text) {
        Integer res;
        for (Rule rule : ruleList) {
            res = rule.applyTo(text);
            if (res != null) return res;
        }
        return 0;
    }

    public int applyRules(Context ctx, StudyMark mark) {
        switch (mark.getType()) {
            case StudyMark.TYPE_ABSENT:
                String absentSymbol = PrefsManager.getInstance(ctx).getPrefs()
                        .getString(PrefsManager.PREFS_BLANK_ABSENT_SYMBOL,
                                   ctx.getResources().getString(R.string.symbol_absent));
                return applyRules(absentSymbol);
            case StudyMark.TYPE_MARK:
                return applyRules(mark.getMark());
            case StudyMark.TYPE_SYMBOL:
                return applyRules(mark.getSymbol());
            case StudyMark.TYPE_NO_MARK:
                return applyRules(null);
        }
        return 0;
    }

    public boolean isCounted() {
        return isCounted;
    }

    public void setCounted(boolean isCounted) {
        this.isCounted = isCounted;
    }
}
