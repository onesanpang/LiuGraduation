package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.School;
import com.example.graduation.java.StatusBarTransparent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchSchoolActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageBack;
    private TextView textClacle;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private EditText editSchool;
    private ListView listView;
    private String schoollUrl = "http://47.106.112.29:8080/school/getAllSchoolByName";
    private List<School> schoolList;
    private Adapter adapter;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    adapter = new Adapter(SearchSchoolActivity.this,schoolList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            editor.putString("school",schoolList.get(position).getName());
                            editor.commit();
                            finish();
                        }
                    });
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_school);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }

        initView();
    }

    private void initView(){
        imageBack = findViewById(R.id.searchSchool_image_back);
        textClacle = findViewById(R.id.searchSchool_text_search);
        editSchool = findViewById(R.id.searchSchool_edit);
        listView = findViewById(R.id.searchSchool_listview);
        imageBack.setOnClickListener(this);
        textClacle.setOnClickListener(this);

        sp = getSharedPreferences("modul",MODE_PRIVATE);
        editor = sp.edit();
        schoolList = new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.searchSchool_image_back:
                finish();
                break;
            case R.id.searchSchool_text_search:
                schoolList.clear();
                String school = editSchool.getText().toString();
                if (school != null && !TextUtils.isEmpty(school)){
                    getSchoolInfo(schoollUrl,school);
                }else{
                    Toast.makeText(this, "请检查输入", Toast.LENGTH_SHORT).show();
                }
                hideEdit(editSchool);
                break;
        }
    }

    private void getSchoolInfo(String url,String name){
        RequestBody body = new FormBody.Builder()
                .add("schoolName",name)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //Log.e("school",response.body().string());//草泥马这玩意只能用一次，第二次了！！！
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.optInt("ec") == 200){
                        JSONArray jsonArray = object.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            School school = new School(jsonArray.getJSONObject(i).optString("schoolName"),
                                    jsonArray.getJSONObject(i).optString("schoolMotto"));
                            schoolList.add(school);
                        }

                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class Adapter extends BaseAdapter{
        private Context context;
        private List<School> schoolLists;

        public Adapter(Context context,List<School> schools){
            this.context = context;
            this.schoolLists = schools;
        }
        @Override
        public int getCount() {
            return schoolLists.size();
        }

        @Override
        public Object getItem(int position) {
            return schoolLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.school_listview_item,null);
            TextView textName = convertView.findViewById(R.id.schoolitem_text_title);
            TextView textMotto = convertView.findViewById(R.id.schoolitem_text_motto);

            textName.setText(schoolLists.get(position).getName());
            textMotto.setText(schoolLists.get(position).getMotto());
            return convertView;
        }
    }

    /**
     * 隐藏输入法
     */
    private void hideEdit(EditText edit){
        InputMethodManager inputMethodManager =(InputMethodManager)getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edit.getWindowToken(),0);
    }

}
