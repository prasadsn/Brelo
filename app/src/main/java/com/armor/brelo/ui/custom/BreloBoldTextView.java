package com.armor.brelo.ui.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class BreloBoldTextView extends TextView {

	public BreloBoldTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
//				"fonts/Brandon_reg.otf");
//		setTypeface(font);
		setTypeface(getTypeface(), Typeface.BOLD);
	}

	public BreloBoldTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
//				"fonts/Brandon_reg.otf");
//		setTypeface(font);
		setTypeface(getTypeface(), Typeface.BOLD);
	}

	public BreloBoldTextView(Context context) {
		super(context);
//		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
//				"fonts/Brandon_reg.otf");
//		setTypeface(font);
		setTypeface(getTypeface(), Typeface.BOLD);
	}
}