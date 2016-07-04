package com.armor.brelo.ui;

import android.os.Bundle;

import com.armor.brelo.db.model.Lock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prsn0001 on 6/21/2016.
 */
public class SharedLockListFragment extends LockListFragment {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lockType There are two lock types 1. My Locks 2. Shared Locks
     * @return A new instance of fragment LockListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SharedLockListFragment newInstance(int lockType) {
        SharedLockListFragment fragment = new SharedLockListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, lockType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public List<Lock> getLockList() {
        return getTestDataSharedLocks();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private List<Lock> getTestDataSharedLocks(){
        List<Lock> list = new ArrayList<Lock>();
//        list.add(new Lock("Rahul's Lock", Lock.LOCK_STATUS.CLOSED.ordinal(), true));
        return list;
    }
}
