package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.LogonModul;
import com.example.graduation.java.StatusBarTransparent;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogonActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    private Button butRegister;
    private EditText editMail, editPass;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private final int WHAT = 1;
    private String logonUrl = "http://47.106.112.29:8080/user/login";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT:
                    Toast.makeText(LogonActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    editor.putString("uid", String.valueOf(msg.obj));
                    editor.putString("email",editMail.getText().toString());
                    editor.putString("password",editPass.getText().toString());
                    editor.commit();
                    startActivity(new Intent(LogonActivity.this, ImportActivity.class));
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initView();
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
    }

    private void initView() {
        button = findViewById(R.id.mian_logon_button);
        butRegister = findViewById(R.id.main_register_button);

        editMail = findViewById(R.id.logon_edit_mail);
        editPass = findViewById(R.id.logon_edit_password);

        button.setOnClickListener(this);
        butRegister.setOnClickListener(this);

        sp = getSharedPreferences("user",MODE_PRIVATE);
        editor = sp.edit();

        if (sp.getString("email","") != null){
            editMail.setText(sp.getString("email",""));
            editPass.setText(sp.getString("password",""));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mian_logon_button:
                //startActivity(new Intent(LogonActivity.this,ImportActivity.class));
                logon(logonUrl);
                //finish();
                break;
            case R.id.main_register_button:
                startActivity(new Intent(LogonActivity.this, RegisterActivity.class));
                break;
        }
    }

    private void logon(String url) {
        String mail = editMail.getText().toString();
        String password = editPass.getText().toString();

        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请检查输入", Toast.LENGTH_SHORT).show();
        } else {
            LogonModul modul = new LogonModul(mail, password);
            Gson gson = new Gson();
            String json = gson.toJson(modul);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.body() != null) {
                        try {
                            final JSONObject object = new JSONObject(response.body().string());
                            if (object.has("ec")) {
                                if (object.optInt("ec") == RegisterActivity.SUCCESS) {
                                    Message msg = new Message();
                                    msg.what = WHAT;
                                    msg.obj = object.optInt("data");
                                    mHandler.sendMessage(msg);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LogonActivity.this, object.optString("em"), Toast.LENGTH_SHORT).show();
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


}
