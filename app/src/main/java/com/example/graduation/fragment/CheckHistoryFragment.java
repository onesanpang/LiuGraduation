package com.example.graduation.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.graduation.PhoneResultsActivity;
import com.example.graduation.R;
import com.example.graduation.java.HttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckHistoryFragment extends Fragment {
    private ListView listView;
    private SharedPreferences sp;
    private String uid;
    private List<AllImageCheckHistory> allImageCheckHistoryLists;
    private String historyUrl = "http://47.106.112.29:8080/history/getAllImageCheckHistory";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkhistoryfragment_layout, container, false);
        initView(view);
        getAllImageCheckHistory(historyUrl);
        return view;
    }

    private void initView(View view) {
        listView = view.findViewById(R.id.checkhistoryfragment_listview);
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
        allImageCheckHistoryLists = new ArrayList<>();
    }

    private void getAllImageCheckHistory(String url) {
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
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.optInt("ec") == 200) {
                        final JSONArray data = object.optJSONArray("data");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < data.length(); i++) {
                                    final JSONArray jsonArray;
                                    try {
                                        jsonArray = new JSONArray(data.optJSONObject(i).optString("result"));
                                        List<PhoneResultsActivity.CheckHistory> checkHistories = new ArrayList<>();
                                        for (int j = 0; j <jsonArray.length(); j++) {
                                            PhoneResultsActivity.CheckHistory checkHistory = new PhoneResultsActivity.CheckHistory(jsonArray.optJSONObject(j).optBoolean("isCorrect")
                                                    , jsonArray.optJSONObject(j).optString("sourceStr"), jsonArray.optJSONObject(j).optString("targetStr"));
                                            checkHistories.add(checkHistory);
                                        }
                                        AllImageCheckHistory history = new AllImageCheckHistory(data.optJSONObject(i).optString("photoUrl"),
                                                data.optJSONObject(i).optString("dataStr"), data.optJSONObject(i).optInt("standardSize"),
                                                data.optJSONObject(i).optInt("errorSize"),checkHistories);
                                        allImageCheckHistoryLists.add(history);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                listView.setAdapter(new Adapter(getContext(), allImageCheckHistoryLists));
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static class AllImageCheckHistory {
        String photoUrl;
        String dataStr;
        int standardSize;
        int errorSize;
        List<PhoneResultsActivity.CheckHistory> list;

        public AllImageCheckHistory(String photoUrl, String dataStr, int standardSize, int errorSize,List<PhoneResultsActivity.CheckHistory> list) {
            this.photoUrl = photoUrl;
            this.dataStr = dataStr;
            this.standardSize = standardSize;
            this.errorSize = errorSize;
            this.list = list;
        }

        public List<PhoneResultsActivity.CheckHistory> getList() {
            return list;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public String getDataStr() {
            return dataStr;
        }

        public int getStandardSize() {
            return standardSize;
        }

        public int getErrorSize() {
            return errorSize;
        }
    }

    static class Adapter extends BaseAdapter {
        private List<AllImageCheckHistory> imageCheckHistoryList;
        private Context context;

        public Adapter(Context context, List<AllImageCheckHistory> imageCheckHistoryList) {
            this.context = context;
            this.imageCheckHistoryList = imageCheckHistoryList;
        }

        @Override
        public int getCount() {
            return imageCheckHistoryList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageCheckHistoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.checkhistoryfragmentlistview_item, null);
            TextView dataStr = convertView.findViewById(R.id.checkhistoryfragmentlistview_text_dataStr);
            ImageView photoUrl = convertView.findViewById(R.id.checkhistoryfragmentlistview_image_photoUrl);
            TextView standardSize = convertView.findViewById(R.id.checkhistoryfragmentlistview_text_standardSize);
            TextView errorSize = convertView.findViewById(R.id.checkhistoryfragmentlistview_text_errorSize);
            LinearLayout linearLayout = convertView.findViewById(R.id.checkhistoryfragmentlistview_linear_result);

            dataStr.setText(imageCheckHistoryList.get(position).getDataStr());
            if (imageCheckHistoryList.get(position).getPhotoUrl() != null && !TextUtils.isEmpty(imageCheckHistoryList.get(position).getPhotoUrl())) {
                photoUrl.setVisibility(View.VISIBLE);
                Glide.with(context).load("http://47.106.112.29:8080/" + imageCheckHistoryList.get(position).getPhotoUrl()).into(photoUrl);
            }
            standardSize.setText("答对" + String.valueOf(imageCheckHistoryList.get(position).getStandardSize()) + "道");
            errorSize.setText("答错" + String.valueOf(imageCheckHistoryList.get(position).getErrorSize()) + "道");
            for (int i = 0; i < imageCheckHistoryList.get(position).getList().size(); i++) {
                View view = LayoutInflater.from(context).inflate(R.layout.checkerrorhistoryitrm_linear, null);
                TextView t1 = view.findViewById(R.id.checkerrorhistoryitemlinear_text_isCorrect);
                TextView t2 = view.findViewById(R.id.checkerrorhistoryitemlinear_text_sourceStr);
                TextView t3 = view.findViewById(R.id.checkerrorhistoryitemlinear_text_targetStr);
                if (imageCheckHistoryList.get(position).getList().get(i).getCorrect() == true) {
                    t1.setText("正确");
                } else {
                    t1.setText("错误");
                    t1.setTextColor(Color.parseColor("#F44336"));
                }
                t2.setText("原始答案：" + imageCheckHistoryList.get(position).getList().get(i).getSourceStr());
                t3.setText("正确答案：" + imageCheckHistoryList.get(position).getList().get(i).getTargetStr());
                linearLayout.addView(view);
            }
            return convertView;
        }
    }
}
