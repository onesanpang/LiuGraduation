package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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

public class TeacherActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageBack;
    private TextView textSchool;
    private SharedPreferences sp;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String school;
    private String uid;
    private ListView listView;
    private String[] grade = {"一","二","三","四","五","六",};
    private List<String> gradeList;
    private int itemPosition;
    private String url = "http://47.106.112.29:8080/class/createClass";
    private Button butCreate;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 1:
                    editor.putString("myclass","yes");
                    editor.commit();
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
    }
    private void initView(){
        imageBack = findViewById(R.id.teacher_image_back);
        textSchool = findViewById(R.id.teacher_text_school);
        textSchool.setOnClickListener(this);
        imageBack.setOnClickListener(this);
        listView = findViewById(R.id.teacher_listview);
        butCreate = findViewById(R.id.teacher_button_ceate);

        butCreate.setOnClickListener(this);

        sp = getSharedPreferences("modul",MODE_PRIVATE);
        school = sp.getString("school","");
        if (school!=null && !TextUtils.isEmpty(school)){
            textSchool.setText(school);
        }
        sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        uid = sharedPreferences.getString("uid","");
        gradeList = new ArrayList<>();
        for (int i = 0; i < grade.length; i++) {
            gradeList.add(grade[i]);
        }
        final Adapter adapter = new Adapter(this,gradeList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setDefSelect(position);
                itemPosition = position;
            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        school = sp.getString("school","");
        if (school!=null && !TextUtils.isEmpty(school)){
            textSchool.setText(school);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.teacher_image_back:
                finish();
                break;
            case R.id.teacher_text_school:
                startActivity(new Intent(TeacherActivity.this,SearchSchoolActivity.class));
                break;
            case R.id.teacher_button_ceate:
                createClass(url);
                break;
        }
    }

    private void createClass(String url){
        ClassModel classModel = new ClassModel(school,grade[itemPosition],uid);
        Gson gson = new Gson();
        String json = gson.toJson(classModel);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("createClass",response.body().string());
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        });
    }

    class ClassModel {
        private String school;
        private String grade;
        private String createId;

        public ClassModel(String school,String grade,String createId){
            this.school = school;
            this.grade = grade;
            this.createId = createId;
        }

    }


  class Adapter extends BaseAdapter{
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
}
