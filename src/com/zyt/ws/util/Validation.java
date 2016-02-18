package com.zyt.ws.util;

/**
 * Created by Administrator on 2015/9/12.
 */
public class Validation {
    private ValidationType vt;
    private int min = -1;
    private int max = -1;
    private int initMin = -1;
    private int[] values;

    public Validation(ValidationType type, int... args) {
        this.vt = type;
        switch (vt) {
            case NOLIMIT:
                break;
            case ENUM:
                values = args;
                break;
            case RANGE:
                min = args[0];
                max = args[1];
                break;
            case LOWVARRANGE:
                initMin = args[0];
                max = args[1];
                break;
        }
    }

    public ValidationType getVt() {
        return vt;
    }

    public boolean isValid(int value, int... lowVar) {
        switch (vt) {
            case NOLIMIT:
                return true;
            case ENUM:
                for (int v : values) {
                    if (value == v) {
                        return true;
                    }
                }
                return false;
            case RANGE:
                return value >= min && value <= max;
            case LOWVARRANGE:
                return value >= (lowVar[0] + initMin) && value <= max;
        }
        return false;
    }
}
