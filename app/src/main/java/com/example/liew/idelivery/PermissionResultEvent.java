package com.example.liew.idelivery;

public class PermissionResultEvent {
    private boolean mGranted;
    public PermissionResultEvent(boolean granted) {
        mGranted = granted;
    }

    public boolean getGranted() {
        return mGranted;
    }
}
