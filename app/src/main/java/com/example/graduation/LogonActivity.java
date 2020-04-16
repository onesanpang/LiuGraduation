package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.LogonModul;
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
    private EditText editMail,editPass;
    private String logonUrl = "http://47.106.112.29:8080/user/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        initView();
    }

    private void initView(){
        button = findViewById(R.id.mian_logon_button);
        butRegister = findViewById(R.id.main_register_button);

        editMail = findViewById(R.id.logon_edit_mail);
        editPass = findViewById(R.id.logon_edit_password);

        button.setOnClickListener(this);
        butRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.mian_logon_button:
                logon(logonUrl);
                //finish();
                break;
            case R.id.main_register_button:
                startActivity(new Intent(LogonActivity.this,RegisterActivity.class));
                break;
        }
    }

    private void logon(String url){
        String mail = editMail.getText().toString();
        String password = editPass.getText().toString();

        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "请检查输入", Toast.LENGTH_SHORT).show();
        }else{
            LogonModul modul = new LogonModul(mail,password);
            Gson gson = new Gson();
            String json = gson.toJson(modul);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.body() != null){
                        try {
                            final JSONObject object = new JSONObject(response.body().string());
                            if (object.has("ec")){
                                if (object.optInt("ec") == RegisterActivity.SUCCESS){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LogonActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    startActivity(new Intent(LogonActivity.this,ImportActivity.class));
                                }else{
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
