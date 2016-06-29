package com.armor.brelo.ui.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class DINLTProRegularTextView extends TextView {

	public DINLTProRegularTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Regular.otf");
		setTypeface(font);
	}

	public DINLTProRegularTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Regular.otf");
		setTypeface(font);
	}

	public DINLTProRegularTextView(Context context) {
		super(context);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Regular.otf");
		setTypeface(font);
	}
}