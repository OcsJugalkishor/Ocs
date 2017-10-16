package com.dharmshala.marulohar.utils;

/**
 * Created by user on 3/7/2017.
 */
public enum WSConsts {

    LOGGING_STATE("enabled");
    private String desiredConstant;

    WSConsts(String constant) {
        this.desiredConstant = constant;
    }

    public String toString() {
        return desiredConstant;
    }
}
