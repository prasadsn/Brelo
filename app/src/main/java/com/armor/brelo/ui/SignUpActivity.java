package com.armor.brelo.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.armor.brelo.R;

public class SignUpActivity extends Activity implements View.OnClickListener{

    TextView signInTabButton;
    Button signUpTabButton;
    Button signInButton;
    ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        signInTabButton = (TextView) findViewById(R.id.btn_tab_sign_in);
        signUpTabButton = (Button) findViewById(R.id.btn_tab_sign_up);
        signInButton = (Button) findViewById(R.id.btn_sign_in);
    }

    @Override
    protected void onStart() {
        super.onStart();
        signInTabButton.setOnClickListener(this);
        signUpTabButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_tab_sign_in:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSignInView();
                    }
                });
                break;
            case R.id.btn_tab_sign_up:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSignUpView();
                    }
                });
                break;
            case R.id.btn_sign_in:
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void setSignUpView(){
        viewFlipper.showNext();
        signUpTabButton.setBackgroundResource(R.drawable.button_bottom_stroke);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/DIN Next LT Pro Bold.otf");
        signUpTabButton.setTypeface(font);
        signUpTabButton.setTypeface(signUpTabButton.getTypeface(), Typeface.BOLD);

        font = Typeface.createFromAsset(getAssets(),
                "fonts/DIN Next LT Pro Light.otf");
        signInTabButton.setTypeface(font);
        signInTabButton.setTypeface(signInTabButton.getTypeface(), Typeface.NORMAL);
    }

    private void setSignInView(){
        viewFlipper.showPrevious();
        signInTabButton.setBackgroundResource(R.drawable.button_bottom_stroke);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/DIN Next LT Pro Bold.otf");
        signInTabButton.setTypeface(font);
        signInTabButton.setTypeface(signInTabButton.getTypeface(), Typeface.BOLD);

        font = Typeface.createFromAsset(getAssets(),
                "fonts/DIN Next LT Pro Light.otf");
        signUpTabButton.setTypeface(font);
        signUpTabButton.setTypeface(signUpTabButton.getTypeface(), Typeface.NORMAL);
    }
}