package com.armor.brelo.controller;

import com.armor.brelo.db.model.Lock;

import java.util.List;

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

    public static Lock getLock(String macAddress) {
        RealmResults<Lock> users = Realm.getDefaultInstance().where(Lock.class).contains("macAddress", macAddress).findAll();
        if(users!=null && users.size()>0)
            return users.get(0);
        return null;
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

    public static void updateLock(Lock lock) {
        Realm.getDefaultInstance().beginTransaction();
        Realm.getDefaultInstance().copyToRealmOrUpdate(lock);
        Realm.getDefaultInstance().commitTransaction();
    }
    public static void updateLocks(List<Lock> locks) {
        Realm.getDefaultInstance().beginTransaction();
        Realm.getDefaultInstance().copyToRealmOrUpdate(locks);
        Realm.getDefaultInstance().commitTransaction();
    }
}
