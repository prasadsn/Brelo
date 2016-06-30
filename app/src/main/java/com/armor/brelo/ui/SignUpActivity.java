package com.armor.brelo.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.armor.brelo.R;
import com.armor.brelo.controller.AuthenticationManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpActivity extends Activity implements View.OnClickListener{

    TextView signInTabButton;
    Button signUpTabButton;
    Button signInButton;
    ViewFlipper viewFlipper;
    private ImageView icon, takePictureIcon;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CROP = 2;
    private EditText signInUserName, signInPassword, signUpFullname, signUpEmail, signUpPhoneNumber, signUpPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        signInTabButton = (TextView) findViewById(R.id.btn_tab_sign_in);
        signUpTabButton = (Button) findViewById(R.id.btn_tab_sign_up);
        signInButton = (Button) findViewById(R.id.btn_sign_in);
        icon = (ImageView) findViewById(R.id.icon);
        takePictureIcon = (ImageView) findViewById(R.id.take_picture_icon);
        signInUserName = (EditText) findViewById(R.id.edit_text_sign_in_username);
        signInPassword = (EditText) findViewById(R.id.edit_text_sign_in_password);
        signUpFullname = (EditText) findViewById(R.id.edit_text_sign_up_fullname);
        signUpEmail = (EditText) findViewById(R.id.edit_text_sign_up_email);
        signUpPhoneNumber = (EditText) findViewById(R.id.edit_text_sign_up_phonenumber);
        signUpPassword = (EditText) findViewById(R.id.edit_text_sign_up_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        signInTabButton.setOnClickListener(this);
        signUpTabButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        takePictureIcon.setOnClickListener(this);
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
                if(viewFlipper.getDisplayedChild() == 0){
                    CharSequence username = signInUserName.getText();
                    CharSequence password = signInPassword.getText();
                    if(AuthenticationManager.isUserExists())
                        if(AuthenticationManager.validateUser())
                } else {

                }
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.take_picture_icon:
                dispatchTakePictureIntent();
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            performCrop(data.getData());

        } else if(requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = getCircularBitmap(imageBitmap);
            takePictureIcon.setImageBitmap(imageBitmap);
            takePictureIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            takePictureIcon.setBackgroundResource(0);

        }
    }
    private void performCrop(Uri picUri) {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 100);
            cropIntent.putExtra("outputY", 100);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
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
        icon.setVisibility(View.GONE);
        takePictureIcon.setVisibility(View.VISIBLE);
        signInButton.setText("SING UP");
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
        icon.setVisibility(View.VISIBLE);
        takePictureIcon.setVisibility(View.GONE);
        signInButton.setText("SING IN");
    }
}