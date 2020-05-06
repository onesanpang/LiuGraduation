package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.StatusBarTransparent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ParentActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageBack;
    private EditText classNumber,name;
    private Button button;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String enterClassUrl = "http://47.106.112.29:8080/class/enterClass";
    private String uid;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 1:
                    editor.putString("addclass","yes");
                    editor.commit();
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
    }
    private void initView(){
        imageBack = findViewById(R.id.parent_image_back);
        imageBack.setOnClickListener(this);
        classNumber = findViewById(R.id.parent_edit_classnumber);
        name = findViewById(R.id.parent_edit_name);
        button = findViewById(R.id.parent_button);
        button.setOnClickListener(this);

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sp.edit();
        uid = sp.getString("uid", "");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.parent_image_back:
                finish();
                break;
            case R.id.parent_button:
                if (classNumber.getText().toString() != null && name.getText().toString() != null){
                    addClass(enterClassUrl,classNumber.getText().toString(),name.getText().toString());
                }else{
                    Toast.makeText(this, "请检查输入", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void addClass(String url,String number,String name){
        RequestBody body = new FormBody.Builder()
                .add("cNumber",number)
                .add("uid",uid)
                .add("userName",name)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    final JSONObject object = new JSONObject(response.body().string());
                    if (object.optInt("ec") == 200){
                        if (object.optBoolean("data")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ParentActivity.this, "加入班级成功", Toast.LENGTH_SHORT).show();
                                    Message msg = new Message();
                                    msg.what = 1;
                                    mHandler.sendMessage(msg);
                                }
                            });
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ParentActivity.this, object.optString("em"), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
