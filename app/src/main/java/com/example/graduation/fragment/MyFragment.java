package com.example.graduation.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduation.R;
import com.example.graduation.RegisterActivity;
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

public class MyFragment extends Fragment {
    private SharedPreferences sp;
    private String uid;
    private String userInfoUrl = "http://47.106.112.29:8080/user/updateNickName";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myfragment_layout,container,false);

        initView(view);
        getUserInfo(userInfoUrl);
        return view;
    }

    private void initView(View view){
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid","");
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
