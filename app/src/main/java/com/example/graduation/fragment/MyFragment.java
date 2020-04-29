package com.example.graduation.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.graduation.CheckHistoryActivity;
import com.example.graduation.ErrorBookActivity;
import com.example.graduation.LogonActivity;
import com.example.graduation.R;
import com.example.graduation.RegisterActivity;
import com.example.graduation.UserInfoActivity;
import com.example.graduation.java.HttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences sp;
    private String uid;
    private String userInfoUrl = "http://47.106.112.29:8080/user/getUser";
    private String imageUrl = "http://img0.imgtn.bdimg.com/it/u=2222611632,3911399686&fm=26&gp=0.jpg";
    private ImageView imageBackground,imageIcon;
    private LinearLayout linearError,linearStudy,exitLogon;

    private TextView textName,textGrade;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myfragment_layout,container,false);

        initView(view);
        getUserInfo(userInfoUrl);
        return view;
    }

    private void initView(View view){

        imageBackground = view.findViewById(R.id.myfragment_image_mybackground);
        textName = view.findViewById(R.id.myfragment_text_name);
        textGrade = view.findViewById(R.id.myfragment_text_grade);
        imageIcon = view.findViewById(R.id.myfragment_image_icon);
        exitLogon = view.findViewById(R.id.myfragment_linear_exit);
        linearError = view.findViewById(R.id.myfragment_linear_errorbook);
        linearStudy = view.findViewById(R.id.myfragment_linear_studyhistory);
        imageIcon.setOnClickListener(this);
        exitLogon.setOnClickListener(this);
        linearError.setOnClickListener(this);
        linearStudy.setOnClickListener(this);

        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid","");

        //添加背景图片
        Glide.with(getContext()).load(imageUrl).into(imageBackground);


        exitLogon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Log.e("nickName",sp.getString("nickName",""));

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.myfragment_text_grade:
                startActivity(new Intent(getActivity(),UserInfoActivity.class));
                break;
            case R.id.myfragment_image_icon:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.myfragment_linear_exit:
                startActivity(new Intent(getActivity(), LogonActivity.class));
                getActivity().finish();
                break;
            case R.id.myfragment_linear_errorbook:
                startActivity(new Intent(getActivity(), ErrorBookActivity.class));
                break;
            case R.id.myfragment_linear_studyhistory:
                startActivity(new Intent(getActivity(), CheckHistoryActivity.class));
                break;
        }
    }

    private void getUserInfo(String url){
        RequestBody body = new FormBody.Builder()
                .add("id",uid)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("response","error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body().toString() != null){
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.has("ec")){
                            if (object.optInt("ec") == RegisterActivity.SUCCESS){
                               final JSONObject data = object.optJSONObject("data");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textName.setText(data.optString("nickName"));
                                        if (data.optString("grade") == null){
                                            textGrade.setText("添加年级 + ");
                                        }else{
                                            textGrade.setText((data.optString("grade")+"年级"));
                                        }
                                        Glide.with(getContext()).load("http://47.106.112.29:8080/"+data.optString("uicon")).into(imageIcon);
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
