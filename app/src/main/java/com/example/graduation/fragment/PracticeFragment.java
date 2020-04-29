package com.example.graduation.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduation.PracticesActivity;
import com.example.graduation.R;
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

public class PracticeFragment extends Fragment implements View.OnClickListener {
    private EditText editSize;
    private SharedPreferences sp;
    private String uid;
    private Button butGetSize;
    private String Url = "http://47.106.112.29:8080/work/getWork";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.practicefragment_layout,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        editSize = view.findViewById(R.id.practicefragment_edit_size);
        butGetSize = view.findViewById(R.id.practicefragment_but_getsize);
        butGetSize.setOnClickListener(this);
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid","");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.practicefragment_but_getsize:
                sendSize(Url);
                break;
        }
    }

    private void sendSize(String url){
        RequestBody body = new FormBody.Builder()
                .add("id",uid)
                .add("size","10")
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String data = response.body().string();
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.optInt("ec") == 200){
                        Intent intent = new Intent(getActivity(), PracticesActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("result",data);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
