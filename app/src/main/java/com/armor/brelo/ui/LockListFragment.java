package com.armor.brelo.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.armor.brelo.DeviceControlActivity;
import com.armor.brelo.DeviceScanActivity;
import com.armor.brelo.R;
import com.armor.brelo.db.model.Lock;
import com.armor.brelo.ui.custom.RecyclerViewEmptySupport;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LockListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public abstract class LockListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_PARAM1 = "param1";
    private RecyclerViewEmptySupport mRecyclerView;

    public enum LOCKS {MY_LOCKS, SHARED_LOCKS};

    // TODO: Rename and change types of parameters
    private int lockType;

    private OnFragmentInteractionListener mListener;

    public abstract List<Lock> getLockList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lockType = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_lock_list, container, false);
        mRecyclerView = (RecyclerViewEmptySupport) layout.findViewById(R.id.locks_recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setEmptyView(layout.findViewById(R.id.empty_view));
        mRecyclerView.setAdapter(new LocksAdapter(getLockList(), lockType));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(10));
        layout.findViewById(R.id.empty_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceScanActivity.class);
                startActivity(intent);
            }
        });
        return layout;
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public class LocksAdapter extends RecyclerView.Adapter<LocksAdapter.ViewHolder> {

        private List<Lock> mListLock;
        private int mLockType;

        public LocksAdapter(List<Lock> locks, int lockType){
            mListLock = locks;
            mLockType = lockType;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.locks_list_row, null);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
                holder.mLockName.setText(mListLock.get(position).getLockName());
//                holder.mLockStatus.setText(mListLock.get(position).getmLockStatus());
                holder.mLockIcon.setImageResource(R.drawable.closed);
                holder.mLockMacAddress = mListLock.get(position).getMacAddress();
//                if(!(mListLock.get(position).getmLockStatus() == Lock.LOCK_STATUS.CLOSED.ordinal()))
                    holder.mLockIcon.setImageResource(R.drawable.open);
        }

        @Override
        public int getItemCount() {
            return mListLock.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView mLockIcon = null;
            TextView mLockName = null;
            TextView mLockStatus = null;
            ImageView mShareIcon = null;
            String mLockMacAddress = null;
            public ViewHolder(View v){
                super(v);
                RelativeLayout layout = (RelativeLayout) v;
                layout.setOnClickListener(this);
                mLockIcon = (ImageView) layout.findViewById(R.id.lock_icon);
                mLockName = (TextView) layout.findViewById(R.id.lock_name);
                mLockStatus = (TextView) layout.findViewById(R.id.lock_status);
                mShareIcon = (ImageView) layout.findViewById(R.id.lock_share_icon);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceControlActivity.class);
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, mLockName.getText());
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, mLockMacAddress);
                startActivity(intent);
            }
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
