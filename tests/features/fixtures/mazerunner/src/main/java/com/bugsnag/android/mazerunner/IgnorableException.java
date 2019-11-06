package com.bugsnag.android.mazerunner;

public class IgnorableException extends Exception {
    public IgnorableException(String errorMessage) {
        super(errorMessage);
    }
}