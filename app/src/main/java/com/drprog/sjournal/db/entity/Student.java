package com.drprog.sjournal.db.entity;

import com.drprog.sjournal.db.utils.BaseJournalEntity;

/**
 * Created by Romka on 01.02.14.
 */
public class Student extends BaseJournalEntity {
    public static final int CODE_VALID = 0;
    public static final int CODE_ERROR_ID = 1;
    public static final int CODE_ERROR_LAST_NAME = 2;
    public static final int CODE_ERROR_FIRST_NAME = 4;
    //private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String mobilePhone;
    private String phone;
    private String note;

    public Student() {
    }

    public Student(Long id, String lastName, String firstName, String middleName, String email,
            String mobilePhone, String phone, String note) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.email = email;
        this.mobilePhone = mobilePhone;
        this.phone = phone;
        this.note = note;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(lastName);
        sb.append(' ');
        //        if (fullNameMode) {
        sb.append(firstName).append(' ');
        if (middleName != null) sb.append(middleName);
//        } else {
//            sb.append(firstName.charAt(0)).append(". ")
//                    .append(middleName.charAt(0)).append('.');
        //}
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (id != null ? !id.equals(student.id) : student.id != null) return false;
        if (!firstName.equals(student.firstName)) return false;
        if (!lastName.equals(student.lastName)) return false;
        return !(middleName != null ? !middleName.equals(student.middleName) :
                student.middleName != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + lastName.hashCode();
        result = 31 * result + firstName.hashCode();
        result = middleName != null ? (31 * result + middleName.hashCode()) : result;
        return result;
    }

    //@return errorCode
    public int isValid() {
        int errorCode = 0;
        if (id != null && id < 0) { errorCode |= CODE_ERROR_ID; }
        if (lastName == null || lastName.isEmpty()) { errorCode |= CODE_ERROR_LAST_NAME; }
        if (firstName == null || firstName.isEmpty()) { errorCode |= CODE_ERROR_FIRST_NAME; }
        return errorCode;
    }

}