package com.armor.brelo.db.model;

import io.realm.RealmObject;

/**
 * Created by prsn0001 on 6/30/2016.
 */
public class Lock extends RealmObject{
    private String lockName;
    private boolean nightModeEnabled;
    private String fromDate;
    private String toDate;
    private boolean autoUnlockEnabled;
    private String macAddress;
    private int lockStatus;
    private boolean inProximity;

    public static final int LOCK_STATUS_OPEN = 1;
    public static final int LOCK_STATUS_CLOSED = 2;
    public static final int LOCK_STATUS_LOCKED = 3;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public boolean isNightModeEnabled() {
        return nightModeEnabled;
    }

    public void setNightModeEnabled(boolean nightModeEnabled) {
        this.nightModeEnabled = nightModeEnabled;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public boolean isAutoUnlockEnabled() {
        return autoUnlockEnabled;
    }

    public void setAutoUnlockEnabled(boolean autoUnlockEnabled) {
        this.autoUnlockEnabled = autoUnlockEnabled;
    }

    public int getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(int lockStatus) {
        this.lockStatus = lockStatus;
    }

    public boolean isInProximity() {
        return inProximity;
    }

    public void setInProximity(boolean inProximity) {
        this.inProximity = inProximity;
    }
}
