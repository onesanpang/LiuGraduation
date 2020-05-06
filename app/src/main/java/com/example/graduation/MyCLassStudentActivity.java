package com.example.graduation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.StatusBarTransparent;
import com.google.gson.Gson;

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
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyCLassStudentActivity extends AppCompatActivity {

    private TextView textGrade;
    private ImageView imageView;
    private TextView textBottom;
    private String url = "http://47.106.112.29:8080/class/getClassMembers";
    private String userUrl = "http://47.106.112.29:8080/class/removeUserBatch";
    private ListView listView;
    private String grade;
    private String classNumber;
    private List<Student> studentList;
    private int itemPosition;
    private Button button;
    private SharedPreferences sp;
    private String identity;
    private List<Integer> delectStudentList;
    private Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_class_student);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
    }

    private void initView() {
        textGrade = findViewById(R.id.myclassstudent_text_grade);
        imageView = findViewById(R.id.myclassstudent_image_back);
        textBottom = findViewById(R.id.myclassstudent_text_bottom);
        button = findViewById(R.id.myclassstudent_buttton);

        listView = findViewById(R.id.myclassstudent_listview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        MyClassActivity.MyClass myClass = (MyClassActivity.MyClass) bundle.getSerializable("class");
        if (myClass != null) {
            grade = myClass.getGrade();
            textGrade.setText(grade + "年级");
            classNumber = myClass.getCcnumber();
            studentList = new ArrayList<>();
            delectStudentList = new ArrayList<>();
            getStudent(url);
        }
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        identity = sp.getString("identity", "");
    }

    private void getStudent(String url) {
        RequestBody body = new FormBody.Builder()
                .add("classNumber", classNumber)
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (data.length() > 0) {
                                    for (int i = 0; i < data.length(); i++) {
                                        Student student = new Student(data.optJSONObject(i).optInt("uid"), data.optJSONObject(i).optString("name"));
                                        studentList.add(student);
                                    }
                                    adapter = new Adapter(MyCLassStudentActivity.this, studentList);
                                    listView.setAdapter(adapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            adapter.setDefSelect(position);
                                            itemPosition = position;
                                        }
                                    });

                                    if (identity.equals("2")) {
                                        button.setVisibility(View.VISIBLE);
                                        button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                removeStudent(userUrl);
                                            }
                                        });
                                    }
                                } else {
                                    textBottom.setVisibility(View.VISIBLE);
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

    class Student {
        int uid;
        String name;

        public Student(int uid, String name) {
            this.uid = uid;
            this.name = name;
        }

        public int getUid() {
            return uid;
        }

        public String getName() {
            return name;
        }
    }

    class Adapter extends BaseAdapter {
        private List<Student> list;
        private Context context;
        int defItem;

        public Adapter(Context context, List<Student> list) {
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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.gradelistview_item, null);
            TextView grade = convertView.findViewById(R.id.gradelistview_text_grade);
            grade.setText(list.get(position).getName());
            if (defItem == position) {
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.gradelistview_item_click, null);
                convertView.setBackground(drawable);

            } else {
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.gradelistview_item_normal, null);
                convertView.setBackground(drawable);
            }
            return convertView;
        }

        public void setDefSelect(int position) {
            this.defItem = position;
            notifyDataSetChanged();
        }
    }

    private void removeStudent(String url) {
        delectStudentList.add(studentList.get(itemPosition).getUid());
        Log.e("用户",classNumber+"   "+studentList.get(itemPosition).getUid());
        DelectStudent delectStudent = new DelectStudent(classNumber,delectStudentList);
        Gson gson = new Gson();
        String json = gson.toJson(delectStudent);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.optInt("ec") == 200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyCLassStudentActivity.this, "移出成功", Toast.LENGTH_SHORT).show();
                                studentList.remove(itemPosition);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class DelectStudent {
        String classNumber;
        List<Integer> uidList;

        public DelectStudent(String classNumber, List<Integer> uidList) {
            this.classNumber = classNumber;
            this.uidList = uidList;
        }
    }

}
