package com.example.graduation;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.graduation.java.BitmaptoFiles;
import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.StatusBarTransparent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearIcon, linearName, linearIdentity, linearSchool;
    private Dialog dialog;
    private TextView textMail;
    private SharedPreferences sp;
    private TextView bottomPhoto, bottomGallery, bottomCancel;
    private Intent intent;
    private Uri imageUri;
    private final int CAMERA_RESULT_CODE = 2;
    private final int REQUEST_CODE_PICK_IMAGE = 3;
    private ImageView imageBack;
    private final String url = "http://47.106.112.29:8080//user/uploadIcon";//上传的url
    private String uid;
    private String userInfoUrl = "http://47.106.112.29:8080/user/getUser";
    private ImageView imageIcon;
    private TextView textName, textGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo(userInfoUrl);
    }

    private void initView() {
        linearIcon = findViewById(R.id.userinfo_linear_icon);
        linearName = findViewById(R.id.userinfo_linear_name);
        linearIdentity = findViewById(R.id.userinfo_linear_identity);
//        linearSchool = findViewById(R.id.userinfo_linear_school);
        textMail = findViewById(R.id.userinfo_text_mail);
        imageBack = findViewById(R.id.userinfo_image_back);
        imageIcon = findViewById(R.id.userinfo_image_icon);
        textName = findViewById(R.id.userinfo_text_name);
        textGrade = findViewById(R.id.userinfo_text_grade);

        linearIcon.setOnClickListener(this);
        linearName.setOnClickListener(this);
        linearIdentity.setOnClickListener(this);
        imageBack.setOnClickListener(this);

        sp = getSharedPreferences("user", MODE_PRIVATE);
        String mail = sp.getString("email", "");
        uid = sp.getString("uid", "");
        textMail.setText(mail);
        textMail.setTextColor(Color.parseColor("#000000"));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                startActivity(new Intent(UserInfoActivity.this, UserNameActivity.class));
                break;
            case R.id.userinfo_image_back:
                finish();
                break;
            case R.id.userinfo_linear_identity:
                startActivity(new Intent(UserInfoActivity.this, UserIdentityActivity.class));
                break;
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
        imageUri = Uri.fromFile(output);
        intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_RESULT_CODE);
    }

    private void openGallery() {
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
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
                        loadImage(BitmaptoFiles.compressImage(bit), url);
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
                        loadImage(BitmaptoFiles.compressImage(bit), url);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void loadImage(final File file, final String url) {
        final String uid = sp.getString("uid", "");
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient();
                    //创建RequestBody
                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
                    //构建MultipartBody
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", "testImage.png", fileBody)
                            .addFormDataPart("id", uid)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void getUserInfo(String url) {
        RequestBody body = new FormBody.Builder()
                .add("id", uid)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("response", "error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body().toString() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.has("ec")) {
                            if (object.optInt("ec") == RegisterActivity.SUCCESS) {
                                final JSONObject data = object.optJSONObject("data");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textName.setText(data.optString("nickName"));
                                        String s;
                                        if (data.optInt("identity") == 1) {
                                            s = "家长";
                                        } else {
                                            s = "老师";
                                        }
                                        if (data.optString("grade").equals(" null") || TextUtils.isEmpty(data.optString("grade"))) {
                                            textGrade.setText("未填写 ");
                                        } else {
                                            textGrade.setText((data.optString("grade") + "年级") + s);
                                        }
                                        if (!data.optString("uicon").equals("null")) {
                                            Glide.with(UserInfoActivity.this).load("http://47.106.112.29:8080/" + data.optString("uicon")).into(imageIcon);
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
