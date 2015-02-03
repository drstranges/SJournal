package com.drprog.sjournal.db.entity;

import com.drprog.sjournal.db.utils.BaseJournalEntity;

/**
 * Created by Romka on 30.01.14.
 */
public class StudyGroup extends BaseJournalEntity {
    //private Long id;
    private String code = "";
    private int semester = 1;
    //private String specialtyCode;
    //private String specialty;

    public StudyGroup() {
    }

    public StudyGroup(Long id, String code) { //}, String specialtyCode, String specialty) {
        this.id = id;
        this.code = code;
        //this.specialtyCode = specialtyCode;
        //this.specialty = specialty;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

//    public String getSpecialtyCode() {
//        return specialtyCode;
//    }

//    public void setSpecialtyCode(String specialtyCode) {
//        this.specialtyCode = specialtyCode;
//    }
//
//    public String getSpecialty() {
//        return specialty;
//    }
//
//    public void setSpecialty(String specialty) {
//        this.specialty = specialty;
//    }


    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        StudyGroup that = (StudyGroup) o;

        return code.equals(that.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}