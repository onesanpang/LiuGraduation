package com.example.graduation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.graduation.java.BitmaptoFiles;
import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.StatusBarTransparent;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddHomeWorkActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textName;
    private ImageView imageBack, imageAdd;
    private EditText editText;
    private Button button;
    private String gradeName;
    private String classNumber;
    private String uploadPhotoUrl = "http://47.106.112.29:8080/class/work/uploadPhoto";
    private String addHomeUrl = "http://47.106.112.29:8080/class/work/addHomeWork";
    private Intent intent;
    private final int REQUEST_CODE_PICK_IMAGE = 3;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home_work);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }

        initView();
    }

    private void initView() {
        textName = findViewById(R.id.addhomework_text_classname);
        imageBack = findViewById(R.id.addhomework_image_back);
        imageAdd = findViewById(R.id.addhomework_image_addimage);
        button = findViewById(R.id.addhomework_buttton);
        editText = findViewById(R.id.addhomework_edit_workinfo);

        imageAdd.setOnClickListener(this);
        imageBack.setOnClickListener(this);
        button.setOnClickListener(this);
        //
        Bundle bundle = getIntent().getExtras();
        MyClassActivity.MyClass myClass = (MyClassActivity.MyClass) bundle.getSerializable("myclass");
        gradeName = myClass.grade;
        classNumber = myClass.getCcnumber();
        textName.setText(gradeName+"年级");
        //Log.e("发布接受的数据", myClass.school + "" + myClass.getCcnumber());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addhomework_image_back:
                finish();
                break;
            case R.id.addhomework_image_addimage:
                openGallery();
                break;
            case R.id.addhomework_buttton:
                addHomeWork(addHomeUrl);
                break;
        }
    }

    private void openGallery() {
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }
    private void sendImage(final File file, final String url) {
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
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    //Log.e("上传图片发布作业返回结果",response.body().string());
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.optInt("ec") == 200){
                            imageUrl = "http://47.106.112.29:8080/"+object.optString("data");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(AddHomeWorkActivity.this).load(imageUrl).into(imageAdd);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bit = null;
                    try {
                        bit = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
                        sendImage(BitmaptoFiles.compressImage(bit), uploadPhotoUrl);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void  addHomeWork(String url){
        String s = editText.getText().toString();
        String aa = imageUrl;
        String bb = classNumber;
        HomeWork homeWork = new HomeWork(editText.getText().toString(),imageUrl,classNumber);
        Gson gson = new Gson();
        String json = gson.toJson(homeWork);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.optInt("ec") == 200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddHomeWorkActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Log.e("发布作业结果",response.body().string());
            }
        });
    }
    class HomeWork {
        String content;
        String photoOne;
        String classNumber;

        public HomeWork(String content, String photoOne, String classNumber) {
            this.content = content;
            this.photoOne = photoOne;
            this.classNumber = classNumber;
        }
    }
}
