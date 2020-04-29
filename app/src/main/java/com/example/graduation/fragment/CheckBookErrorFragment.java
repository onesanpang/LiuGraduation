package com.example.graduation.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduation.R;
import com.example.graduation.java.HttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckBookErrorFragment extends Fragment {
    private ListView listView;
    private SharedPreferences sp;
    private String uid;
    private String historyUrl = "http://47.106.112.29:8080/history/getErrorHistory";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkbookerrorfragment_layout,container,false);
        initView(view);
        getErrorHistory(historyUrl);
        return view;
    }
    private void initView(View view){
        listView = view.findViewById(R.id.chechbookerrorfragment_listview);
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
    }

    private void getErrorHistory(String url){
        RequestBody body = new FormBody.Builder()
                .add("uid",uid)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("getErrorHistory",response.body().string());

                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.optInt("ec") == 200){
                        JSONArray jsonArray = 
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
