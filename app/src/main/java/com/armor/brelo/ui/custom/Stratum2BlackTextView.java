package com.armor.brelo.ui.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class Stratum2BlackTextView extends TextView {

	public Stratum2BlackTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/Stratum2-Black.otf");
		setTypeface(font);
	}

	public Stratum2BlackTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/Stratum2-Black.otf");
		setTypeface(font);
	}

	public Stratum2BlackTextView(Context context) {
		super(context);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/Stratum2-Black.otf");
		setTypeface(font);
	}
}