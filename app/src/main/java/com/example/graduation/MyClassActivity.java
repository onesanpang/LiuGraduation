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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyClassActivity extends AppCompatActivity implements View.OnClickListener {

    private String uid;
    private SharedPreferences sp;
    private String identity;
    private String myClass = "http://47.106.112.29:8080/class/getAllClass";
    private String quitToClassUrl = "http://47.106.112.29:8080/class/quitToClass";
    private String dissolveClassUrl = "http://47.106.112.29:8080/class/dissolveClass";
    private ListView listView;
    private TextView textAddClass;
    private ImageView imageBack;
    private List<MyClass> myClassLists;
    private Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }

        initView();
    }

    private void initView() {

        listView = findViewById(R.id.myclass_listview);
        textAddClass = findViewById(R.id.myclass_text_addclass);
        imageBack = findViewById(R.id.myclass_image_back);

        textAddClass.setOnClickListener(this);
        imageBack.setOnClickListener(this);
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
        identity = sp.getString("identity", "");
        if (identity.equals("1")) {
            textAddClass.setText("加入新班级");
        } else if (identity.equals("2")) {
            textAddClass.setText("添加班级");
        }
        myClassLists = new ArrayList<>();
        getClassInfo(myClass);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyClassActivity.this, MyCLassStudentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("class", myClassLists.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void getClassInfo(String url) {
        RequestBody body = new FormBody.Builder()
                .add("uid", uid)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                Log.e("家长拉去班级信息",body);
                try {
                    JSONObject object = new JSONObject(body);
                    if (object.optInt("ec") == 200) {
                        final JSONArray jsonArray = new JSONArray(object.optString("data"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    MyClass myClass = new MyClass(jsonArray.optJSONObject(i).optString("school"), jsonArray.optJSONObject(i).optString("grade"),
                                            jsonArray.optJSONObject(i).optInt("createId"), jsonArray.optJSONObject(i).optString("cnumber"),
                                            jsonArray.optJSONObject(i).optInt("mnumber"));
                                    myClassLists.add(myClass);
                                }

                                adapter = new Adapter(MyClassActivity.this, myClassLists, identity);
                                listView.setAdapter(adapter);

                                adapter.setOnItemMoreClickListener(new Adapter.onItemMoreListener() {
                                    @Override
                                    public void onMoreClick(int i) {
                                        String position = myClassLists.get(i).getCcnumber();
                                        if (identity.equals("2")) {
                                            dissolveClass(dissolveClassUrl, position, i);
                                        } else if (identity.equals("1")) {
                                            dissolveClass(quitToClassUrl,position,i);
                                        }
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
        switch (v.getId()) {
            case R.id.myclass_text_addclass:
                if (identity.equals("2")) {
                    startActivity(new Intent(MyClassActivity.this, TeacherActivity.class));
                } else if (identity.equals("1")) {
                    startActivity(new Intent(MyClassActivity.this, ParentActivity.class));
                }
                break;
            case R.id.myclass_image_back:
                finish();
                break;
        }
    }

    static class MyClass implements Serializable {
        String school;
        String grade;
        int createId;
        String ccnumber;
        int mnumber;

        public MyClass(String school, String grade, int createId, String ccnumber, int mnumber) {
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

    //老师解散班级
    private void dissolveClass(String url, String number, final int i) {
        RequestBody body = new FormBody.Builder()
                .add("classNumber", number)
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myClassLists.remove(i);
                                adapter.notifyDataSetChanged();
                                if (identity.equals("2")) {
                                    Toast.makeText(MyClassActivity.this, "解散班级成功", Toast.LENGTH_SHORT).show();
                                }else if (identity.equals("1")){
                                    Toast.makeText(MyClassActivity.this, "退出班级成功", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onRestart() {
        super.onRestart();
        getClassInfo(myClass);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myClassLists.clear();
        adapter.notifyDataSetChanged();
    }

    static class Adapter extends BaseAdapter {

        private Context context;
        private List<MyClass> classList;
        private String identity;

        public Adapter(Context context, List<MyClass> classList, String identity) {
            this.context = context;
            this.classList = classList;
            this.identity = identity;
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
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.myclasslistview_item, null);
                viewHolder.grade = view.findViewById(R.id.myclasslistview_text_grade);
                viewHolder.number = view.findViewById(R.id.myclasslistview_text_mnumber);
                viewHolder.cnumber = view.findViewById(R.id.myclasslistview_text_cnumber);
                viewHolder.school = view.findViewById(R.id.myclasslistview_text_school);
                viewHolder.delect = view.findViewById(R.id.myclasslistview_linear_delect);
                viewHolder.dissolve = view.findViewById(R.id.myclasslistView_item_dissolveclass);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.grade.setText(classList.get(position).getGrade() + "年级");
            viewHolder.number.setText("人数：" + classList.get(position).getMnumber());
            viewHolder.cnumber.setText("班级号：" + classList.get(position).getCcnumber());
            viewHolder.school.setText("学校：" + classList.get(position).getSchool());

            if (identity.equals("1")) {
                viewHolder.dissolve.setText("退出班级");
            } else if (identity.equals("2")) {
                viewHolder.dissolve.setText("解散班级");
            }

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

        class ViewHolder {
            TextView grade;
            TextView number;
            TextView cnumber;
            TextView school;
            TextView dissolve;
            LinearLayout delect;
        }
    }
}
