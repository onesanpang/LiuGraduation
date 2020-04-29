package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.StatusBarTransparent;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserNameActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textSave;
    private EditText edteName;
    private String updateUrl = "http://47.106.112.29:8080/user/updateNickName";
    private ImageView imageBack;
    private SharedPreferences sp;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
    }

    private void initView(){
        imageBack = findViewById(R.id.username_image_back);
        edteName = findViewById(R.id.username_edit_name);
        textSave = findViewById(R.id.username_text_save);

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid","");
        textSave.setOnClickListener(this);
        imageBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.username_image_back:
                finish();
                break;
            case R.id.username_text_save:
                if (edteName.getText().toString() != null){
                    updateName(updateUrl);
                }
                break;
        }
    }

    private void updateName(String url){
        Person person = new Person(uid,edteName.getText().toString());
        Gson gson = new Gson();
        String json = gson.toJson(person);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("userinfo",response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserNameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }
    class Person{
        String id;
        String nickName;

        public Person(String id,String nickName){
            this.id = id;
            this.nickName = nickName;
        }
    }
}
