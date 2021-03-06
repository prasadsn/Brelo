package com.armor.brelo.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v4.graphics.BitmapCompat;
import android.text.TextUtils;
import android.util.Log;
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
import com.armor.brelo.db.model.User;

import org.hybridsquad.android.library.BitmapUtil;
import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class SignUpActivity extends Activity  implements CropHandler, View.OnClickListener{

    private static final String TAG = "SignUpActivity";
    TextView signInTabButton;
    Button signUpTabButton;
    Button signInButton;
    ViewFlipper viewFlipper;
    private ImageView icon, takePictureIcon;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CROP = 2;
    private EditText signInUserName, signInPassword, signUpFullname, signUpEmail, signUpPhoneNumber, signUpPassword;
    CropParams mCropParams;
    Bitmap profilePic;

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
        RealmResults<User> users = Realm.getDefaultInstance().where(User.class).findAll();
        if(users!=null && users.size()>0){
            signInUserName.setText(users.get(0).getEmail());
            signInPassword.setText(users.get(0).getPassword());
        }
        mCropParams = new CropParams(this);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                if(viewFlipper.getDisplayedChild() == 0){
                    CharSequence username = signInUserName.getText();
                    CharSequence password = signInPassword.getText();
                    if(TextUtils.isEmpty(username)){
                        Toast toast = Toast.makeText(SignUpActivity.this, "Please enter Username", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    } else if(TextUtils.isEmpty(password)){
                        Toast toast = Toast.makeText(SignUpActivity.this, "Please enter Password", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }

                    if(AuthenticationManager.isUserExists(username.toString())) {
                        if (AuthenticationManager.validateUser(username.toString(), password.toString())) {
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast toast = Toast.makeText(SignUpActivity.this, "Invalid password!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(SignUpActivity.this, "User does not exist! Please Sign Up", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    CharSequence fullName = signUpFullname.getText();
                    CharSequence email = signUpEmail.getText();
                    CharSequence phoneNumber = signUpPhoneNumber.getText();
                    CharSequence password = signUpPassword.getText();
                    if(TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) ||
                            TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
                        Toast toast = Toast.makeText(SignUpActivity.this, "Please enter all the details", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    saveImage(SignUpActivity.this, profilePic, fullName.toString().replaceAll(" ", "_"), "jpg");
                    AuthenticationManager.addUser(fullName.toString(), email.toString(), phoneNumber.toString(), password.toString());
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }}
                });
                break;
            case R.id.take_picture_icon:
                mCropParams.enable = true;
                mCropParams.compress = true;
                Intent intent = CropHelper.buildCameraIntent(mCropParams);
                startActivityForResult(intent, CropHelper.REQUEST_CAMERA);

//                dispatchTakePictureIntent();
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
        CropHelper.handleResult(this, requestCode, resultCode, data);

        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            performCrop(data.getData());

        } else if(requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = getCircularBitmap(imageBitmap);
            takePictureIcon.setImageBitmap(imageBitmap);
            takePictureIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            takePictureIcon.setBackgroundResource(0);
        }*/
    }

    public void saveImage(Context context, Bitmap b, String name, String extension){
        name = name + "." + extension;
        FileOutputStream out;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public CropParams getCropParams() {
        return mCropParams;
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        // Original or Cropped uri
        Log.d(TAG, "Crop Uri in path: " + uri.getPath());
        if (!mCropParams.compress) {
            profilePic = BitmapUtil.decodeUriAsBitmap(this, uri);
            takePictureIcon.setImageBitmap(profilePic);
        }
    }

    @Override
    public void onCompressed(Uri uri) {
        // Compressed uri
        profilePic = BitmapUtil.decodeUriAsBitmap(this, uri);
        takePictureIcon.setImageBitmap(profilePic);
    }

    @Override
    public void onCancel() {
        Toast.makeText(this, "Crop canceled!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailed(String message) {
        Toast.makeText(this, "Crop failed: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CropHelper.clearCacheDir();
    }
}