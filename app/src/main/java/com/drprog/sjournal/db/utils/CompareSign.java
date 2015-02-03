package com.drprog.sjournal.db.utils;

/**
 * Created by Romka on 18.08.2014.
 */
public enum CompareSign {
    GREATER(0, ">"),
    GREATER_EQUAL(1, "\u2265"),
    LESS(2, "<"),
    LESS_EQUAL(3, "\u2264"),
    EQUAL(4, "="),
    NOT_EQUAL(5, "\u2260");
    private final String sign;
    private final int id;

    CompareSign(int id, String sign) {
        this.id = id;
        this.sign = sign;
    }

    public static CompareSign getById(int id) {
        switch (id) {
            case 0:
                return GREATER;
            case 1:
                return GREATER_EQUAL;
            case 2:
                return LESS;
            case 3:
                return LESS_EQUAL;
            case 4:
                return EQUAL;
            case 5:
                return NOT_EQUAL;
        }
        return null;
    }

    public Integer applyTo(int x, int y, int result) {
        switch (id) {
            case 0:
                return x > y ? result : null;
            case 1:
                return x >= y ? result : null;
            case 2:
                return x < y ? result : null;
            case 3:
                return x <= y ? result : null;
            case 4:
                return x == y ? result : null;
            case 5:
                return x != y ? result : null;
        }
        return null;
    }

    public int applyTo(String x, String y, int result) {
        switch (id) {
            case 4:
                if (x != null ? x.equals(y) : y == null) return result;
                break;
            case 5:
                if (x != null ? !x.equals(y) : y != null) return result;
                break;
        }
        return 0;
    }

    public int getId() {
        return id;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return sign;
    }

    public CompareSign getNext(boolean loop) {
        return this.ordinal() < CompareSign.values().length - 1
                ? CompareSign.values()[this.ordinal() + 1]
                : loop ? CompareSign.values()[0] : null;
    }


}
