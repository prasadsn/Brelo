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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private int mSeries2Index;
    private int mPie2Index;
    private int mLockIndex;
    private View mLockNameIconLayout;
    private OnLockChangeListener lockChangeListener;
    private Animation zoomout;
    private Animation zoomin;

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
        mLockNameIconLayout = getActivity().findViewById(R.id.lock_name_icon_layout);
        zoomin = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in_animation);
        zoomout = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_out_animation);
        zoomin.setAnimationListener(new ZoomInAnimationListener());
        mLockNameIconLayout.setAnimation(zoomin);
        mLockNameIconLayout.setAnimation(zoomout);
    }

    private class ZoomInAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mLockNameIconLayout.startAnimation(zoomout);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
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
                createAnimation();
                playOpenAnimation(getDecoView());
//				currentLockIndex = 1;
                break;
            case 2:
                view = getActivity().getLayoutInflater().inflate(R.layout.lock_closed, null);
//                view.setBackground((Drawable) getResources().getDrawable(R.drawable.lock_locked_button));
                layout.addView(view, params);
                createAnimation();
                playLatchAnimation(getDecoView());

//				currentLockIndex = 2;
                break;
            case 3:
                view = getActivity().getLayoutInflater().inflate(R.layout.lock_locked, null);
//                view.setBackground((Drawable) getResources().getDrawable(R.drawable.lock_locked_button));
                layout.addView(view, params);
                createAnimation();
                playLockAnimation(getDecoView());

//				currentLockIndex = 3;
                break;
        }
        mLockNameIconLayout = getActivity().findViewById(R.id.lock_name_icon_layout);
    }

    private void playLockAnimation(DecoView decoView) {
        final DecoEvent.ExecuteEventListener eventListener = new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent event) {
            }

            @Override
            public void onEventEnd(DecoEvent event) {
                getDecoView().getChartSeries(mPieIndex).reset();
                mLockNameIconLayout.startAnimation(zoomin);
            }
        };
        final DecoEvent.ExecuteEventListener eventListener1 = new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent event) {
            }

            @Override
            public void onEventEnd(DecoEvent event) {
                getOuterDecoView().setVisibility(View.GONE);
            }
        };
        decoView.addEvent(new DecoEvent.Builder(100f)
                .setIndex(mSeries1Index)
                .setListener(eventListener)
                .build());
        getOuterDecoView().addEvent(new DecoEvent.Builder(0)
                .setIndex(mSeries2Index)
                .setListener(eventListener1)
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
                mLockNameIconLayout.startAnimation(zoomin);
            }
        };
        final DecoEvent.ExecuteEventListener eventListener1 = new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent event) {
            }

            @Override
            public void onEventEnd(DecoEvent event) {
                getOuterDecoView().setVisibility(View.GONE);
            }
        };
        decoView.addEvent(new DecoEvent.Builder(100f)
                .setIndex(mSeries1Index)
                .setListener(eventListener)
                .build());
        getOuterDecoView().addEvent(new DecoEvent.Builder(0)
                .setIndex(mSeries2Index)
                .setListener(eventListener1)
                .build());
    }

    private void playOpenAnimation(DecoView decoView) {
        final DecoEvent.ExecuteEventListener eventListener = new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent event) {
            }

            @Override
            public void onEventEnd(DecoEvent event) {
                getDecoView().setVisibility(View.GONE);
                mLockNameIconLayout.startAnimation(zoomin);
            }
        };
        final DecoEvent.ExecuteEventListener eventListener1 = new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent event) {
            }

            @Override
            public void onEventEnd(DecoEvent event) {
                getOuterDecoView().setVisibility(View.GONE);
            }
        };
        decoView.addEvent(new DecoEvent.Builder(100f)
                .setIndex(mSeries1Index)
                .setListener(eventListener)
                .build());
        getOuterDecoView().addEvent(new DecoEvent.Builder(0)
                .setIndex(mSeries2Index)
                .setListener(eventListener1)
                .build());
    }

    @Override
    protected void createTracks() {
        int innerSeriesItemColor = Color.parseColor("#ffffff");
//        if(mLockIndex == 2)
            innerSeriesItemColor = getResources().getColor(R.color.white);
        addSeries(innerSeriesItemColor);
    }

    private void addSeries(int innerSeriesItemColor) {
        final DecoView innerDecoView = getDecoView();
        final DecoView outerDecoView = getOuterDecoView();
        final View view = getView();
        if (innerDecoView == null || view == null) {
            return;
        }
        view.setBackgroundColor(getResources().getColor(R.color.white));

        innerDecoView.executeReset();
        innerDecoView.deleteAll();

        final float seriesMax = 100f;

        float circleInset = getDimension(20);

        SeriesItem seriesBack1Item = new SeriesItem.Builder(innerSeriesItemColor)
                .setRange(0, seriesMax, 0)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_PIE)
                .setSpinDuration(500)
                .setInset(new PointF(circleInset, circleInset))
                .build();

        mPieIndex = innerDecoView.addSeries(seriesBack1Item);

        SeriesItem series1Item = new SeriesItem.Builder(innerSeriesItemColor)
                .setRange(0, seriesMax, 0)
                .setLineWidth(getDimension(25))
                .setSpinDuration(500)
                .setInterpolator(new LinearInterpolator())
                .build();

        series1Item.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                innerDecoView.getChartSeries(mPieIndex).setPosition(percentComplete < 1.0f ? percentComplete * seriesMax : 0f);
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries1Index = innerDecoView.addSeries(series1Item);


        SeriesItem seriesBack2Item = new SeriesItem.Builder(innerSeriesItemColor)
                .setRange(0, seriesMax, 100)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_PIE)
                .setSpinDuration(500)
                .setInset(new PointF(circleInset, circleInset))
                .build();

        mPie2Index = outerDecoView.addSeries(seriesBack2Item);

        SeriesItem series2Item = new SeriesItem.Builder(innerSeriesItemColor)
                .setRange(0, seriesMax, 100)
                .setLineWidth(getDimension(25))
                .setSpinDuration(500)
                .setInterpolator(new LinearInterpolator())
                .build();

        series2Item.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                outerDecoView.getChartSeries(mPieIndex).setPosition(percentComplete < 1.0f ? percentComplete * seriesMax : 0f);
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries2Index = outerDecoView.addSeries(series2Item);

    }
}