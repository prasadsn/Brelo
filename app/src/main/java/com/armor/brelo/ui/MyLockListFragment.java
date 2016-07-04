package com.armor.brelo.ui;

import android.os.Bundle;

import com.armor.brelo.db.model.Lock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prsn0001 on 6/21/2016.
 */
public class MyLockListFragment extends LockListFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lockType There are two lock types 1. My Locks 2. Shared Locks
     * @return A new instance of fragment LockListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyLockListFragment newInstance(int lockType) {
        MyLockListFragment fragment = new MyLockListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, lockType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public List<Lock> getLockList() {
        return getTestDataMyLocks();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private List<Lock> getTestDataMyLocks(){
        List<Lock> list = new ArrayList<Lock>();
//        list.add(new Lock("Home Lock", Lock.LOCK_STATUS.OPEN.ordinal(), true));
//        list.add(new Lock("Form House Lock", Lock.LOCK_STATUS.CLOSED.ordinal(), true));
//        list.add(new Lock("Office Lock", Lock.LOCK_STATUS.LOCKED.ordinal(), true));
        return list;
    }

}
