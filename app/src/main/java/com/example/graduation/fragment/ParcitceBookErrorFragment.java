package com.example.graduation.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduation.PhoneResultsActivity;
import com.example.graduation.R;
import com.example.graduation.java.HttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ParcitceBookErrorFragment extends Fragment {
    private ListView listView;
    private SharedPreferences sp;
    private String uid;
    private String historyUrl = "http://47.106.112.29:8080/history/getErrorHistory";
    private List<CheckBookErrorFragment.ErrorHistory> errorHistoryList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parciticebookerrorfragment_layout,container,false);
        initView(view);
        getErrorHistory(historyUrl);
        return view;
    }

    private void initView(View view) {
        listView = view.findViewById(R.id.parcticebookerrorfragment_listview);
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
        errorHistoryList = new ArrayList<>();
    }

    private void getErrorHistory(String url) {
        RequestBody body = new FormBody.Builder()
                .add("uid", uid)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.optInt("ec") == 200) {
                        final JSONArray data = jsonObject.optJSONArray("data");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < data.length(); i++) {
                                    final JSONArray jsonArray;
                                    try {
                                        jsonArray = new JSONArray(data.optJSONObject(i).optString("result"));
                                        List<PhoneResultsActivity.CheckHistory> checkHistories = new ArrayList<>();
                                        ;
                                        for (int j = 0; j < jsonArray.length(); j++) {
                                            PhoneResultsActivity.CheckHistory checkHistory = new PhoneResultsActivity.CheckHistory(jsonArray.optJSONObject(j).optBoolean("isCorrect")
                                                    , jsonArray.optJSONObject(j).optString("sourceStr"), jsonArray.optJSONObject(j).optString("targetStr"));
                                            checkHistories.add(checkHistory);
                                        }
                                        CheckBookErrorFragment.ErrorHistory errorHistory = new CheckBookErrorFragment.ErrorHistory(data.optJSONObject(i).optString("dataStr"), data.optJSONObject(i).optString("errorSize"), checkHistories);
                                        if (data.optJSONObject(i).optInt("type") == 2) {
                                            errorHistoryList.add(errorHistory);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                CheckBookErrorFragment.Adapter adapter = new CheckBookErrorFragment.Adapter(getContext(), errorHistoryList);
                                listView.setAdapter(adapter);
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
