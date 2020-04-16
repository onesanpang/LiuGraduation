package com.example.graduation;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.RegisterModul;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editMail, editPass, editPassTwo, editCode;
    private Button butGetCode, butRegist;
    private RadioGroup radioGroup;
    private RadioButton butParent, butTeacher;
    private String sendMailUrl = "http://47.106.112.29:8080/user/sendMail";
    private String registerUrl = "http://47.106.112.29:8080/user/register";

    private int identity;
    public final static int SUCCESS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    /**
     * 绑定控件
     */
    private void initView() {
        editMail = findViewById(R.id.register_edit_mail);
        editPass = findViewById(R.id.register_edit_password);
        editPassTwo = findViewById(R.id.register_edit_passwordtwo);
        editCode = findViewById(R.id.register_edit_code);

        butGetCode = findViewById(R.id.register_but_getcode);
        butRegist = findViewById(R.id.register_but_regist);
        radioGroup = findViewById(R.id.register_radiogroup);
        butParent = findViewById(R.id.register_parent_but);
        butTeacher = findViewById(R.id.register_teacher_but);

        butGetCode.setOnClickListener(this);
        butRegist.setOnClickListener(this);

        //对RadioGroup进行监听
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.register_parent_but) {
                    Toast.makeText(RegisterActivity.this, butParent.getText(), Toast.LENGTH_SHORT).show();
                    identity = 1;
                } else if (checkedId == R.id.register_teacher_but) {
                    Toast.makeText(RegisterActivity.this, butTeacher.getText(), Toast.LENGTH_SHORT).show();
                    identity = 2;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_but_getcode:
                if (TextUtils.isEmpty(editMail.getText().toString())){
                    Toast.makeText(this, "请检查输入邮箱", Toast.LENGTH_SHORT).show();
                }else{
                    MyCountDownTimer count = new MyCountDownTimer(60000, 1000);
                    count.start();
                    getCode(sendMailUrl);
                }
                break;
            case R.id.register_but_regist:
                regist(registerUrl);
                break;

        }
    }

    private void getCode(String url) {
        RequestBody body = new FormBody.Builder()
                .add("mail", editMail.getText().toString())
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response != null) {
                    try {
                        final JSONObject json = new JSONObject(response.body().string());
                        if (json.has("ec")) {
                            if (json.optInt("ec") != SUCCESS) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, json.optString("em"), Toast.LENGTH_SHORT).show();
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

    private void regist(String url) {
        String mail = editMail.getText().toString();
        String verifyCode = editCode.getText().toString();
        String password = editPass.getText().toString();
        String passwordTwo = editPassTwo.getText().toString();

        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(verifyCode) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordTwo) || identity == 0) {
            Toast.makeText(this, "请检查输入是否正确", Toast.LENGTH_SHORT).show();
        } else {
            if (!password.equals(passwordTwo)) {
                Toast.makeText(this, "两次密码不一样，请重新输入", Toast.LENGTH_SHORT).show();
                editPassTwo.setText("");
            } else {
                RegisterModul modul = new RegisterModul(identity, mail, verifyCode, password);
                Gson gson = new Gson();
                String json = gson.toJson(modul);
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            final JSONObject object = new JSONObject(response.body().string());
                            if (object.has("ec")) {
                                if (object.optInt("ec") == SUCCESS) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });

                                } else if (object.optInt("ec") == 10901) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RegisterActivity.this, object.optString("em"), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }


    //button的60s后点击的方法
    private class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //改变button的颜色
            butGetCode.setBackgroundColor(Color.parseColor("#9999CC"));
            //设置不让button再次点击
            butGetCode.setClickable(false);
            //在button上面显示时间
            butGetCode.setText(millisUntilFinished / 1000 + "s");
        }

        //60s后结束onTick可再次点击
        @Override
        public void onFinish() {
            butGetCode.setText("再次获取");
            //可再次点击
            butGetCode.setClickable(true);
            butGetCode.setBackgroundColor(Color.parseColor("#0099FF"));
        }
    }
}
