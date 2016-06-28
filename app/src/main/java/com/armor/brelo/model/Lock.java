package com.armor.brelo.model;

/**
 * Created by prsn0001 on 6/17/2016.
 */
public class Lock {
    public enum LOCK_STATUS { OPEN, LOCKED, CLOSED };
    private String mLockName;
    private int mLockStatus;
    private boolean mSharable;

    public Lock(String lockName, int lockStatus, boolean sharable){
        mLockName = lockName;
        mLockStatus = lockStatus;
        mSharable = sharable;
    }
    public int getmLockStatus() {
        return mLockStatus;
    }

    public void setmLockStatus(int mLockStatus) {
        this.mLockStatus = mLockStatus;
    }

    public String getmLockName() {
        return mLockName;
    }

    public void setmLockName(String mLockName) {
        this.mLockName = mLockName;
    }

    public boolean ismSharable() {
        return mSharable;
    }

    public void setmSharable(boolean mSharable) {
        this.mSharable = mSharable;
    }
}
