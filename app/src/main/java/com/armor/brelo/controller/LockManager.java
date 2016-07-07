package com.armor.brelo.controller;

import com.armor.brelo.db.model.Lock;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by prsn0001 on 6/30/2016.
 */
public class LockManager {

    public static boolean isLockExists(String macAddress){
        RealmResults<Lock> users = Realm.getDefaultInstance().where(Lock.class).contains("macAddress", macAddress).findAll();
        if(users!=null && users.size()>0)
            return true;
        else return false;
    }

    public static void addLock(String macAddress, String lockName, String fromDate, String toDate, boolean nightModeEnabled, boolean autoLockEnabled){
        Realm.getDefaultInstance().beginTransaction();
        Lock lock = Realm.getDefaultInstance().createObject(Lock.class);
        lock.setMacAddress(macAddress);
        lock.setLockName(lockName);
        lock.setFromDate(fromDate);
        lock.setToDate(toDate);
        lock.setNightModeEnabled(nightModeEnabled);
        lock.setAutoUnlockEnabled(autoLockEnabled);
        Realm.getDefaultInstance().commitTransaction();
    }

    public static RealmResults<Lock> getAllLocks() {
        RealmResults<Lock> locks = Realm.getDefaultInstance().where(Lock.class).findAll();
        return locks;
    }
}
