package com.drprog.sjournal.db.entity;

import com.drprog.sjournal.db.utils.BaseJournalEntity;

/**
 * Created by Romka on 31.01.14.
 */
public class StudySubject extends BaseJournalEntity {
    //private Long id;
    private String title = "";
    private String abbr = "";
    private Integer iconIndex;

    public StudySubject() {
    }

    public StudySubject(Long id, String title, String abbr, Integer iconIndex) {
        this.id = id;
        this.title = title;
        this.abbr = abbr;
        this.iconIndex = iconIndex;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public Integer getIconIndex() {
        return iconIndex;
    }

    public void setIconIndex(Integer iconIndex) {
        this.iconIndex = iconIndex;
    }

    @Override
    public String toString() {
        return abbr + " - " + title;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        StudySubject that = (StudySubject) o;

        if (!abbr.equals(that.abbr)) return false;
        return title.equals(that.title);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + abbr.hashCode();
        return result;
    }
}
