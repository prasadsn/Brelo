package com.armor.brelo.ui.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class DINLTProLightButton extends Button {

	public DINLTProLightButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Light.otf");
		setTypeface(font);
	}

	public DINLTProLightButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Light.otf");
		setTypeface(font);
	}

	public DINLTProLightButton(Context context) {
		super(context);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Light.otf");
		setTypeface(font);
	}
}