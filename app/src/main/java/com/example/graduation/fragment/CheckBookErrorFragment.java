package com.example.graduation.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

public class CheckBookErrorFragment extends Fragment {
    private ListView listView;
    private SharedPreferences sp;
    private String uid;
    private String historyUrl = "http://47.106.112.29:8080/history/getErrorHistory";
    private List<ErrorHistory> errorHistoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkbookerrorfragment_layout, container, false);
        initView(view);
        getErrorHistory(historyUrl);
        return view;
    }

    private void initView(View view) {
        listView = view.findViewById(R.id.chechbookerrorfragment_listview);
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
                                        for (int j = 0; j < jsonArray.length(); j++) {
                                            PhoneResultsActivity.CheckHistory checkHistory = new PhoneResultsActivity.CheckHistory(jsonArray.optJSONObject(j).optBoolean("isCorrect")
                                                    , jsonArray.optJSONObject(j).optString("sourceStr"), jsonArray.optJSONObject(j).optString("targetStr"));
                                            checkHistories.add(checkHistory);
                                        }
                                        ErrorHistory errorHistory = new ErrorHistory(data.optJSONObject(i).optString("dataStr"), data.optJSONObject(i).optString("errorSize"), checkHistories);
                                        if (data.optJSONObject(i).optInt("type") == 1) {
                                            errorHistoryList.add(errorHistory);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Adapter adapter = new Adapter(getContext(), errorHistoryList);
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

    static class ErrorHistory {
        String dataStr;
        String errrorSize;
        List<PhoneResultsActivity.CheckHistory> list;

        public ErrorHistory(String dataStr, String errrorSize, List<PhoneResultsActivity.CheckHistory> list) {
            this.dataStr = dataStr;
            this.errrorSize = errrorSize;
            this.list = list;
        }

        public String getDataStr() {
            return dataStr;
        }

        public String getErrrorSize() {
            return errrorSize;
        }

        public List<PhoneResultsActivity.CheckHistory> getList() {
            return list;
        }
    }

    static class Adapter extends BaseAdapter {

        private List<ErrorHistory> list;
        private Context mContext;

        public Adapter(Context mContext, List<ErrorHistory> list) {
            this.mContext = mContext;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.checkerrorhistory_item, null);
            TextView text1 = convertView.findViewById(R.id.checkerrorhistoryitem_text_dataStr);
            TextView text2 = convertView.findViewById(R.id.checkerrorhistoryitem_text_errorSize);
            LinearLayout linearLayout = convertView.findViewById(R.id.checkerrorhistoryitem_linear_result);
            text1.setText(list.get(position).getDataStr());
            text2.setText("错题数：" + list.get(position).errrorSize);
            for (int i = 0; i < list.get(position).getList().size(); i++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.checkerrorhistoryitrm_linear, null);
                TextView t1 = view.findViewById(R.id.checkerrorhistoryitemlinear_text_isCorrect);
                TextView t2 = view.findViewById(R.id.checkerrorhistoryitemlinear_text_sourceStr);
                TextView t3 = view.findViewById(R.id.checkerrorhistoryitemlinear_text_targetStr);
                if (list.get(position).getList().get(i).getCorrect() == true) {
                    t1.setText("正确");
                } else {
                    t1.setText("错误");
                    t1.setTextColor(Color.parseColor("#F44336"));
                }
                t2.setText("原始答案：" + list.get(position).getList().get(i).getSourceStr());
                t3.setText("正确答案：" + list.get(position).getList().get(i).getTargetStr());

                linearLayout.addView(view);
            }
            return convertView;
        }
    }
}
