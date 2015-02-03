package com.drprog.sjournal.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.drprog.sjournal.db.utils.BaseJournalEntity;
import com.drprog.sjournal.db.utils.CompareSign;
import com.drprog.sjournal.utils.RunUtils;

/**
 * Created by Romka on 18.08.2014.
 */
public class Rule extends BaseJournalEntity implements Parcelable {

    public static final Parcelable.Creator<Rule> CREATOR = new Parcelable.Creator<Rule>() {
        public Rule createFromParcel(Parcel source) {return new Rule(source);}

        public Rule[] newArray(int size) {return new Rule[size];}
    };
    //    private int summaryEntryId;
    private CompareSign operator;
    //private CompareSign operatorSecond;
    private String argument;
    //private String argumentSecond;
    private Integer argumentNum = null;
    //private Integer argumentSecondNum = null;
    private int result;

    public Rule() {
    }

    public Rule(CompareSign operator, String args,
            int result) { //, CompareSign operatorSecond, String argsSecond
//        this.summaryEntryId = summaryEntryId;
        this.operator = operator;
        this.argument = args;
//        this.operatorSecond = operatorSecond;
//        this.argumentSecond = argsSecond;
        this.result = result;
//        if (RunUtils.isNumeric(argsSecond)){
//            argumentSecondNum = Integer.valueOf(argsSecond);
//        }
        if (RunUtils.tryParse(args) != null) {
            argumentNum = Integer.valueOf(args);
        }
    }

//    public int getSummaryEntryId() {
//        return summaryEntryId;
//    }
//
//    public void setSummaryEntryId(int summaryEntryId) {
//        this.summaryEntryId = summaryEntryId;
//    }

    public Integer applyTo(String x) {
        if (x != null && argument != null && RunUtils.tryParse(x) != null && argumentNum != null) {
            return operator.applyTo(Integer.valueOf(x), argumentNum, result);
        } else {
            return operator.applyTo(x, argument, result);
        }

    }

    public Integer applyTo(int x) {
        if (argument != null && argumentNum != null) {
            return operator.applyTo(x, argumentNum, result);
        }
        return null;
    }

    public CompareSign getOperator() {
        return operator;
    }

    public void setOperator(CompareSign operator) {
        this.operator = operator;
    }

    public int getOperatorId() {
        return operator.getId();
    }

    public void setOperatorId(int id) {
        this.operator = CompareSign.getById(id);
    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String args) {
        this.argument = args;
        if (RunUtils.tryParse(args) != null) {
            argumentNum = Integer.valueOf(args);
        }
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        Rule rule = (Rule) o;

        if (result != rule.result) return false;
        if (argument != null ? !argument.equals(rule.argument) : rule.argument != null) {
            return false;
        }
        return operator == rule.operator;

    }

    @Override
    public int hashCode() {
        int result1 = operator.hashCode();
        result1 = 31 * result1 + (argument != null ? argument.hashCode() : 0);
        result1 = 31 * result1 + result;
        return result1;
    }

    @Override
    public String toString() {
        //return "[ IF " + operator.getSign() + ' ' + argument + " THEN " + result + " ]";
        //return "[ " + operator.getSign() + ' ' + argument + " ---> " + result + " ]";
        return "[ " + operator.getSign() + ' ' + argument + " ] \u21D2 " + result;
    }

    @Override
    public int describeContents() { return 0; }

    private Rule(Parcel in) {
        if (in.readByte() != 0) { this.id = (Long) in.readValue(Long.class.getClassLoader()); }

        int tmpOperator = in.readInt();
        this.operator = tmpOperator == -1 ? null : CompareSign.values()[tmpOperator];

        this.result = in.readInt();

        if (in.readByte() != 0) { this.argument = in.readString(); }
        if (in.readByte() != 0) {
            this.argumentNum = (Integer) in.readValue(Integer.class.getClassLoader());
        }


    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if (this.id != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.id);
        } else { dest.writeByte((byte) 0); }

        dest.writeInt(this.operator == null ? -1 : this.operator.ordinal());

        dest.writeInt(this.result);

        if (this.argument != null) {
            dest.writeByte((byte) 1);
            dest.writeString(this.argument);
        } else { dest.writeByte((byte) 0); }

        if (this.argumentNum != null) {
            dest.writeByte((byte) 1);
            dest.writeValue(this.argumentNum);
        } else { dest.writeByte((byte) 0); }


    }
}
