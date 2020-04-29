package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.StatusBarTransparent;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserIdentityActivity extends AppCompatActivity {

    private ImageView imageBack;
    private String[] grade = {"一","二","三","四","五","六",};
    private List<String> gradeList;
    private ListView gradeListView;
    private SharedPreferences sp;
    private String uid;
    private TextView textSave;
    private int itemPosition;
    private RadioGroup radioGroup;
    private int identity;
    private String updateUrl = "http://47.106.112.29:8080/user/updateNickName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_identity);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();

    }

    private void initView(){
        imageBack = findViewById(R.id.useridentity_image_back);
        textSave = findViewById(R.id.useridentity_text_save);

        gradeListView = findViewById(R.id.useridentity_listview_grade);

        radioGroup = findViewById(R.id.useridentity_radiogroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               if (checkedId == R.id.useridentity_parent_but){
                   identity = 1;
               }else if (checkedId == R.id.useridentity_teacher_but){
                   identity = 2;
               }
            }
        });


        gradeList = new ArrayList<>();
        for (int i = 0; i < grade.length; i++) {
            gradeList.add(grade[i]);
        }

        final Adapter adapter = new Adapter(this,gradeList);
        gradeListView.setAdapter(adapter);
        gradeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setDefSelect(position);
                itemPosition = position;
            }
        });

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid","");

        textSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo(updateUrl);
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUserInfo(String url){
        Person person = new Person(uid,grade[itemPosition],identity);
        Gson gson = new Gson();
        String json = gson.toJson(person);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("useridentity",response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserIdentityActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    class Adapter extends BaseAdapter {
        private Context context;
        private List<String> list;
        int defItem;
        public Adapter(Context context,List<String> list){
            this.context = context;
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

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.gradelistview_item,null);
            TextView grade = convertView.findViewById(R.id.gradelistview_text_grade);
            grade.setText(list.get(position)+"年级");
            if (defItem == position){
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.gradelistview_item_click, null);
                convertView.setBackground(drawable);

            }else{
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.gradelistview_item_normal, null);
                convertView.setBackground(drawable);
            }
            return convertView;
        }

        public void setDefSelect(int position){
            this.defItem = position;
            notifyDataSetChanged();
        }
    }

    class Person{
        String id;
        String grade;
        int identity;

        public Person(String id,String grade,int identity){
            this.id = id;
            this.grade = grade;
            this.identity = identity;
        }
    }

}
