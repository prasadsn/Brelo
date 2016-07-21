package com.armor.brelo.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import com.armor.brelo.R;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

/**
 * Created by prsn0001 on 7/20/2016.
 */
public class LockFragment extends DecoAnimationFragment implements View.OnClickListener, View.OnLongClickListener{
    private int mSeries1Index;
    private int mPieIndex;
    private int mLockIndex;
    private OnLockChangeListener lockChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock, container, false);
    }

    @Override
    protected void setupEvents() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            lockChangeListener = (OnLockChangeListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        View btnLock = getActivity().findViewById(R.id.btn_lock);
        btnLock.setOnClickListener(this);
        btnLock.setOnLongClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        lockChangeListener.onLockClick();
    }

    @Override
    public boolean onLongClick(View v) {
        return lockChangeListener.onLockLongClick();
    }

    public interface OnLockChangeListener {
        void onLockClick();
        boolean onLockLongClick();
    }
    public void updateLockUI(int lockStatus) {
        mLockIndex = lockStatus;
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.btn_lock);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getChildAt(0).getLayoutParams();
        layout.removeAllViews();
        View view = null;
        switch (lockStatus) {
            case 1:
                view = getActivity().getLayoutInflater().inflate(R.layout.lock_open, null);
                //view.setBackground((Drawable) getResources().getDrawable(R.drawable.lock_locked_button));
                layout.addView(view, params);
//				currentLockIndex = 1;
                break;
            case 2:
                view = getActivity().getLayoutInflater().inflate(R.layout.lock_closed, null);
                view.setBackground((Drawable) getResources().getDrawable(R.drawable.lock_locked_button));
                layout.addView(view, params);
                createAnimation();
                playLatchAnimation(getDecoView());

//				currentLockIndex = 2;
                break;
            case 3:
                view = getActivity().getLayoutInflater().inflate(R.layout.lock_locked, null);
                view.setBackground((Drawable) getResources().getDrawable(R.drawable.lock_locked_button));
                layout.addView(view, params);
                createAnimation();
                playLockAnimation(getDecoView());

//				currentLockIndex = 3;
                break;
        }

    }

    private void playLockAnimation(DecoView decoView) {
        final DecoEvent.ExecuteEventListener eventListener = new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent event) {
            }

            @Override
            public void onEventEnd(DecoEvent event) {
                getDecoView().getChartSeries(mPieIndex).reset();
            }
        };
        decoView.addEvent(new DecoEvent.Builder(100f)
                .setIndex(mSeries1Index)
                .setListener(eventListener)
                .build());
    }

    private void playLatchAnimation(DecoView decoView) {
        final DecoEvent.ExecuteEventListener eventListener = new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent event) {
            }

            @Override
            public void onEventEnd(DecoEvent event) {
                getDecoView().getChartSeries(mPieIndex).reset();
            }
        };
        decoView.addEvent(new DecoEvent.Builder(100f)
                .setIndex(mSeries1Index)
                .setListener(eventListener)
                .build());
    }

    @Override
    protected void createTracks() {
        final DecoView decoView = getDecoView();
        final View view = getView();
        if (decoView == null || view == null) {
            return;
        }
        view.setBackgroundColor(getResources().getColor(R.color.white));

        decoView.executeReset();
        decoView.deleteAll();

        final float seriesMax = 100f;

        float circleInset = getDimension(18);
        int seriesItemColor = Color.parseColor("#ffffff");
        if(mLockIndex == 2)
            seriesItemColor = Color.parseColor("#d4d4d4");

        SeriesItem seriesBack1Item = new SeriesItem.Builder(seriesItemColor)
                .setRange(0, seriesMax, 0)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_PIE)
                .setSpinDuration(1000)
                .setInset(new PointF(circleInset, circleInset))
                .build();

        mPieIndex = decoView.addSeries(seriesBack1Item);

        SeriesItem series1Item = new SeriesItem.Builder(seriesItemColor)
                .setRange(0, seriesMax, 0)
                .setLineWidth(getDimension(36))
                .setSpinDuration(1000)
                .setInterpolator(new LinearInterpolator())
                .build();

        series1Item.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                decoView.getChartSeries(mPieIndex).setPosition(percentComplete < 1.0f ? percentComplete * seriesMax : 0f);
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries1Index = decoView.addSeries(series1Item);

    }
}