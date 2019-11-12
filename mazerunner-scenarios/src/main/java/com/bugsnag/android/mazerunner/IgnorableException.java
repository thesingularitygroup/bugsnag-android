package com.bugsnag.android.mazerunner;

public class IgnorableException extends Exception {

    public static final long serialVersionUID = 42L;

    public IgnorableException(String errorMessage) {
        super(errorMessage);
    }
}