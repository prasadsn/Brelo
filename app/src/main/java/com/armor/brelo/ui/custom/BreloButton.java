package com.armor.brelo.ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.armor.brelo.R;

public class BreloButton extends Button {

	public BreloButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Medium.otf");
		setTypeface(font);
		setBackgroundColor(getResources().getColor(R.color.dark_sky_blue));
		setTextColor(Color.WHITE);
	}

	public BreloButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Medium.otf");
		setTypeface(font);
		setBackgroundColor(getResources().getColor(R.color.dark_sky_blue));
		setTextColor(Color.WHITE);
	}

	public BreloButton(Context context) {
		super(context);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Medium.otf");
		setTypeface(font);
		setBackgroundColor(getResources().getColor(R.color.dark_sky_blue));
		setTextColor(Color.WHITE);
	}
}