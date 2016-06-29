package com.armor.brelo.ui.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.armor.brelo.R;

public class DINLTProRegularEditText extends EditText {

	public DINLTProRegularEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Regular.otf");
		setTypeface(font);
		setBackgroundResource(R.drawable.brelo_edit_text_background);
	}

	public DINLTProRegularEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Regular.otf");
		setTypeface(font);
		setBackgroundResource(R.drawable.brelo_edit_text_background);
	}

	public DINLTProRegularEditText(Context context) {
		super(context);
		Typeface font = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/DIN Next LT Pro Regular.otf");
		setTypeface(font);
		setBackgroundResource(R.drawable.brelo_edit_text_background);
	}
}