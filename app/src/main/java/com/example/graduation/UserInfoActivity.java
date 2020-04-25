package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduation.java.StatusBarTransparent;

import java.io.File;
import java.io.FileNotFoundException;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearIcon,linearName,linearIdentity,linearSchool;
    private Dialog dialog;
    private TextView textMail;
    private SharedPreferences sp;
    private TextView bottomPhoto, bottomGallery, bottomCancel;
    private Intent intent;
    private Uri imageUri;
    private final int REQUEST_PERMISSIONS = 1;
    private final int CAMERA_RESULT_CODE = 2;
    private final int REQUEST_CODE_PICK_IMAGE = 3;
    private ImageView imageBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
        initPermission();
    }

    private void initView(){
        linearIcon = findViewById(R.id.userinfo_linear_icon);
        linearName = findViewById(R.id.userinfo_linear_name);
        linearIdentity = findViewById(R.id.userinfo_linear_identity);
        linearSchool = findViewById(R.id.userinfo_linear_school);
        textMail = findViewById(R.id.userinfo_text_mail);
        imageBack = findViewById(R.id.userinfo_image_back);

        linearIcon.setOnClickListener(this);
        linearName.setOnClickListener(this);
        linearIdentity.setOnClickListener(this);
        linearSchool.setOnClickListener(this);
        imageBack.setOnClickListener(this);

        sp = getSharedPreferences("user",MODE_PRIVATE);
        String mail = sp.getString("email","");
        textMail.setText(mail);
        textMail.setTextColor(Color.parseColor("#000000"));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.userinfo_linear_icon:
                showBottomDialog();
                break;
            case R.id.bottom_dialog_photo:
                dialog.dismiss();
                openSysCamera();
                break;
            case R.id.bottom_dialog_gallery:
                dialog.dismiss();
                openGallery();
                break;
            case R.id.bottom_dialog_cancel:
                dialog.dismiss();
                break;
            case R.id.userinfo_linear_name:
                startActivity(new Intent(UserInfoActivity.this,UserNameActivity.class));
                break;
            case R.id.userinfo_image_back:
                finish();
                break;
            case R.id.userinfo_linear_identity:
                startActivity(new Intent(UserInfoActivity.this,UserIdentityActivity.class));
            case R.id.userinfo_linear_school:
                startActivity(new Intent(UserInfoActivity.this,SearchSchoolActivity.class));
        }
    }

    private void showBottomDialog() {
        dialog = new Dialog(this, R.style.BottomStyle);
        View inflate = LayoutInflater.from(this).inflate(R.layout.bsd_new_order, null);
        bottomPhoto = inflate.findViewById(R.id.bottom_dialog_photo);
        bottomGallery = inflate.findViewById(R.id.bottom_dialog_gallery);
        bottomCancel = inflate.findViewById(R.id.bottom_dialog_cancel);

        bottomPhoto.setOnClickListener(this);
        bottomCancel.setOnClickListener(this);
        bottomGallery.setOnClickListener(this);

        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 10;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    private void initPermission() {
        String[] permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        //检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 之前拒绝了权限，但没有点击 不再询问 这个时候让它继续请求权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
            } else {
                //注册相机权限
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void openSysCamera() {
        //最后一个参数是文件夹的名称，可以随便起
        File file = new File(Environment.getExternalStorageDirectory(), "拍照");
        if (!file.exists()) {
            file.mkdir();
        }

        //这里将时间戳作为不同照片的名称
        File output = new File(file, System.currentTimeMillis() + ".jpg");

        /**
         * 如果该文件夹已经存在，则删除它，否则创建一个
         */
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageUri  = Uri.fromFile(output);
        intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent, CAMERA_RESULT_CODE);
    }
    private void openGallery(){
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    Bitmap bit = null;
                    try {
                        bit = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
                break;
            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bit = null;
                    try {
                        bit = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }
}
