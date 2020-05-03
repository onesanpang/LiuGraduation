package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.PraciticeAdapter;
import com.example.graduation.java.PraciticeItem;
import com.example.graduation.java.PracticesItemAdapter;
import com.example.graduation.java.StatusBarTransparent;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PracticesActivity extends AppCompatActivity {

    private ImageView imageBack;
    private TextView textPush;
    private ListView listView;
    private String data;
    private List<PraciticeItem> lists;
    private String url = "http://47.106.112.29:8080/work/checkWork";
    private Map<Integer,String> map = new HashMap<>();
    private SharedPreferences sp;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practices);

        Bundle bundle=getIntent().getExtras();
        data= bundle.getString("result");

        Log.e("data",data);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initVIew();
        setListView();
    }

    private void initVIew(){
        imageBack = findViewById(R.id.practices_image_back);
        listView = findViewById(R.id.practices_listview);
        textPush = findViewById(R.id.practices_text_push);

        textPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushWork(url);
            }
        });
        lists = new ArrayList<>();
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid","");
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void setListView(){
        try {
            JSONObject object = new JSONObject(data);
            JSONArray jsonArray = object.optJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                String s = jsonArray.optString(i);
                PraciticeItem item = new PraciticeItem();
                item.setText(s);
                lists.add(item);
                map.put(i,s);
            }

            PraciticeAdapter itemAdapter = new PraciticeAdapter(lists,this);
            listView.setAdapter(itemAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void pushWork(String url){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            list.add(map.get(i));
        }
        Work work = new Work(list,uid);
        Gson gson = new Gson();
        String json = gson.toJson(work);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JSONObject object = null;
                try {
                    object = new JSONObject(response.body().string());
                    if (object.optInt("ec") == 200){
                        Intent intent = new Intent(PracticesActivity.this, PhoneResultsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("practiceresult",object.optInt("data"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveList(int position,String str){
        map.put(position,str);
    }

    class Work{
        private List<String> workList;
        private String id;

        public Work(List<String> workList,String id){
            this.workList = workList;
            this.id = id;
        }
    }
}
