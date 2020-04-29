package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class MyClassActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sp;
    private String uid;
    private String myClass = "http://47.106.112.29:8080/class/getAllClass";
    private ListView listView;
    private TextView textAddClass;
    private ImageView imageBack;
    private List<MyClass> myClassLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_class);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }

        initView();
    }

    private void initView(){

        listView = findViewById(R.id.myclass_listview);
        textAddClass = findViewById(R.id.myclass_text_addclass);
        imageBack = findViewById(R.id.myclass_image_back);

        textAddClass.setOnClickListener(this);
        imageBack.setOnClickListener(this);
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid","");

        myClassLists = new ArrayList<>();
        getClassInfo(myClass);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MyClassActivity.this, "点击了item", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void getClassInfo(String url){
        RequestBody body = new FormBody.Builder()
                .add("uid",uid)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.optInt("ec") == 200){
                        final JSONArray jsonArray = new JSONArray(object.optString("data"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    MyClass myClass = new MyClass(jsonArray.optJSONObject(i).optString("school"),jsonArray.optJSONObject(i).optString("grade"),
                                            jsonArray.optJSONObject(i).optInt("createId"),jsonArray.optJSONObject(i).optString("cnumber"),
                                            jsonArray.optJSONObject(i).optInt("mnumber"));
                                    myClassLists.add(myClass);
                                }

                                Adapter adapter = new Adapter(MyClassActivity.this,myClassLists);
                                listView.setAdapter(adapter);

                                adapter.setOnItemMoreClickListener(new Adapter.onItemMoreListener() {
                                    @Override
                                    public void onMoreClick(int i) {
                                        Toast.makeText(MyClassActivity.this, "点击了删除", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.myclass_text_addclass:
                startActivity(new Intent(MyClassActivity.this,TeacherActivity.class));
                break;
            case R.id.myclass_image_back:
                finish();
                break;
        }
    }

    class MyClass{
        String school;
        String grade;
        int createId;
        String ccnumber;
        int mnumber;

        public MyClass(String school,String grade,int createId,String ccnumber,int mnumber){
            this.school = school;
            this.grade = grade;
            this.createId = createId;
            this.ccnumber = ccnumber;
            this.mnumber = mnumber;
        }

        public String getSchool() {
            return school;
        }

        public String getGrade() {
            return grade;
        }

        public int getCreateId() {
            return createId;
        }

        public String getCcnumber() {
            return ccnumber;
        }

        public int getMnumber() {
            return mnumber;
        }
    }

    static class Adapter extends BaseAdapter{

        private Context context;
        private List<MyClass> classList;

        public Adapter(Context context,List<MyClass> classList){
            this.context = context;
            this.classList = classList;
        }
        @Override
        public int getCount() {
            return classList.size();
        }

        @Override
        public Object getItem(int position) {
            return classList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (view == null){
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.myclasslistview_item,null);
                viewHolder.grade = view.findViewById(R.id.myclasslistview_text_grade);
                viewHolder.number = view.findViewById(R.id.myclasslistview_text_mnumber);
                viewHolder.cnumber = view.findViewById(R.id.myclasslistview_text_cnumber);
                viewHolder.school = view.findViewById(R.id.myclasslistview_text_school);
                viewHolder.delect = view.findViewById(R.id.myclasslistview_linear_delect);
                view.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.grade.setText(classList.get(position).getGrade()+"年级");
            viewHolder.number.setText("人数："+classList.get(position).getMnumber());
            viewHolder.cnumber.setText("班级号："+classList.get(position).getCcnumber());
            viewHolder.school.setText("学校："+classList.get(position).getSchool());

            viewHolder.delect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemMoreListener.onMoreClick(position);
                }
            });

            return view;
        }

        /**
         * 详情按钮的监听接口
         */
         public interface onItemMoreListener {
            void onMoreClick(int i);
        }

        private onItemMoreListener mOnItemMoreListener;

        public void setOnItemMoreClickListener(onItemMoreListener mOnItemMoreListener) {
            this.mOnItemMoreListener = mOnItemMoreListener;
        }
        class ViewHolder{
            TextView grade;
            TextView number;
            TextView cnumber;
            TextView school;
            LinearLayout delect;
        }
    }
}
