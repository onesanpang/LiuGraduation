package com.example.graduation.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
                        final JSONArray jsonArray = object.optJSONArray("data");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    AllImageCheckHistory history = new AllImageCheckHistory(jsonArray.optJSONObject(i).optString("photoUrl"),
                                            jsonArray.optJSONObject(i).optString("dataStr"), jsonArray.optJSONObject(i).optInt("standardSize"),
                                            jsonArray.optJSONObject(i).optInt("errorSize"));
                                    allImageCheckHistoryLists.add(history);
                                }
                                listView.setAdapter(new Adapter(getContext(),allImageCheckHistoryLists));
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class AllImageCheckHistory {
        String photoUrl;
        String dataStr;
        int standardSize;
        int errorSize;

        public AllImageCheckHistory(String photoUrl, String dataStr, int standardSize, int errorSize) {
            this.photoUrl = photoUrl;
            this.dataStr = dataStr;
            this.standardSize = standardSize;
            this.errorSize = errorSize;
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

    class Adapter extends BaseAdapter {
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

            dataStr.setText(imageCheckHistoryList.get(position).getDataStr());
            Glide.with(context).load("http://47.106.112.29:8080/" +imageCheckHistoryList.get(position).getPhotoUrl()).into(photoUrl);
            standardSize.setText("答对"+String.valueOf(imageCheckHistoryList.get(position).getStandardSize())+"道");
            errorSize.setText("答错"+String.valueOf(imageCheckHistoryList.get(position).getErrorSize())+"道");
            return convertView;
        }
    }
}
