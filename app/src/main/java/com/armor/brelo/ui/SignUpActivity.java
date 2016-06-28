package com.armor.brelo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.armor.brelo.R;

public class SignUpActivity extends Activity implements View.OnClickListener{

    Button signInTabButton;
    Button signUpTabButton;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        signInTabButton = (Button) findViewById(R.id.btn_tab_sign_in);
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
                break;
            case R.id.btn_tab_sign_up:
                break;
            case R.id.btn_sign_in:
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
