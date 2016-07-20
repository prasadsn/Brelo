package com.armor.brelo.ui;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
public class LockFragment extends DecoAnimationFragment {
    private int mSeries1Index;
    private int mPieIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lock, container, false);
    }

    @Override
    protected void setupEvents() {

    }

    @Override
    public void onResume() {
        super.onResume();
        final DecoEvent.ExecuteEventListener eventListener = new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent event) {
            }

            @Override
            public void onEventEnd(DecoEvent event) {
                getDecoView().getChartSeries(mPieIndex).reset();
            }
        };
        playLockAnimation(getDecoView(), eventListener);
    }

    public void updateLockUI(int lockStatus) {
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
//				currentLockIndex = 2;
                break;
            case 3:
                view = getActivity().getLayoutInflater().inflate(R.layout.lock_locked, null);
                view.setBackground((Drawable) getResources().getDrawable(R.drawable.lock_locked_button));
                layout.addView(view, params);
//				currentLockIndex = 3;
                break;
        }

    }

    private void playLockAnimation(DecoView decoView, DecoEvent.ExecuteEventListener eventListener) {
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
        SeriesItem seriesBack1Item = new SeriesItem.Builder(Color.parseColor("#ffffff"))
                .setRange(0, seriesMax, 0)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_PIE)
                .setSpinDuration(1000)
                .setInset(new PointF(circleInset, circleInset))
                .build();

        mPieIndex = decoView.addSeries(seriesBack1Item);

        SeriesItem series1Item = new SeriesItem.Builder(Color.parseColor("#ffffff"))
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