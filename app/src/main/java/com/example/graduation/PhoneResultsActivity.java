package com.example.graduation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.StatusBarTransparent;

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

public class PhoneResultsActivity extends AppCompatActivity {
    private ImageView imageBack;
    private TextView textRight,textError;
    private ListView listView;
    private int id;
    private List<CheckHistory> checkHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_results);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
        getResult(id);
    }

    private void initView(){
        imageBack = findViewById(R.id.phoneresult_image_back);
        listView = findViewById(R.id.phoneresult_listview);
        textRight = findViewById(R.id.phoneresult_text_rightsize);
        textError = findViewById(R.id.phoneresult_text_errorsize);

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkHistoryList = new ArrayList<>();

        Bundle bundle=getIntent().getExtras();
        id= bundle.getInt("checkresult");
        Log.e("resultid", String.valueOf(id));
    }

    //获取返回的额结果
    private void getResult(int id){
        RequestBody body = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .build();
        HttpUtil.sendJsonOkhttpRequest("http://47.106.112.29:8080/history/getCheckHistoryById", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.optInt("ec") == 200){
                        final JSONObject data = jsonObject.getJSONObject("data");
                        final JSONArray jsonArray = new JSONArray(data.optString("result"));
                        int length = jsonArray.length();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    textRight.setText("正确题数："+data.optInt("standardSize"));
                                    textError.setText("错误题数："+data.optInt("errorSize"));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        CheckHistory checkHistory = new CheckHistory(jsonArray.getJSONObject(i).optBoolean("isCorrect")
                                        ,jsonArray.getJSONObject(i).optString("sourceStr"),jsonArray.getJSONObject(i).optString("targetStr"));
                                        checkHistoryList.add(checkHistory);
                                    }
                                    Adapter adapter = new Adapter(PhoneResultsActivity.this,checkHistoryList);
                                    listView.setAdapter(adapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class Adapter extends BaseAdapter{
        List<CheckHistory> list;
        Context context;
        public Adapter(Context context,List<CheckHistory> list){
            this.list = list;
            this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.checkhistorylistview_item,null);
            TextView text1 = convertView.findViewById(R.id.checkhistoryitem_text_isCorrect);
            TextView text2 = convertView.findViewById(R.id.checkhistoryitem_text_sourceStr);
            TextView text3 = convertView.findViewById(R.id.checkhistoryitem_text_targetStr);
            if (list.get(position).getCorrect() == true){
                text1.setText("答案：正确");
            }else{
                text1.setText("答案：错误");
            }

            text2.setText("原始答案："+list.get(position).getSourceStr());
            text3.setText("正确答案："+list.get(position).getTargetStr());
            return convertView;
        }
    }

    class CheckHistory{
       private boolean isCorrect;
       private String sourceStr;
       private String targetStr;
       public CheckHistory(boolean isCorrect,String sourceStr,String targetStr){
           this.isCorrect = isCorrect;
           this.sourceStr = sourceStr;
           this.targetStr = targetStr;
       }

        public boolean getCorrect() {
            return isCorrect;
        }

        public String getSourceStr() {
            return sourceStr;
        }

        public String getTargetStr() {
            return targetStr;
        }
    }
}
