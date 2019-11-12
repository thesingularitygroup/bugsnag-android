package com.bugsnag.android.mazerunner;

import androidx.annotation.Nullable;

public class IgnorableException extends Exception {

    public static final long serialVersionUID = 42L;

    public IgnorableException(@Nullable String errorMessage) {
        super(errorMessage);
    }
}