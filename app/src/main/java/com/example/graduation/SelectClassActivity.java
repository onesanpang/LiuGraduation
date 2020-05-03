package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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

public class SelectClassActivity extends AppCompatActivity {
    private ImageView imageBack;
    private ListView listView;
    private List<MyClassActivity.MyClass> myClassLists;
    private SharedPreferences sp;
    private String myClass = "http://47.106.112.29:8080/class/getAllClass";
    private String uid;
    private int itemPosition;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
        getClassInfo(myClass);
    }

    private void initView(){
        imageBack = findViewById(R.id.selectclass_image_back);
        listView = findViewById(R.id.selectclass_listview);
        button = findViewById(R.id.selectclass_buttton);

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        uid = sp.getString("uid","");
        myClassLists = new ArrayList<>();
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SelectClassActivity.this, itemPosition+"", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SelectClassActivity.this,AddHomeWorkActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("myclass",myClassLists.get(itemPosition));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
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
                                    MyClassActivity.MyClass myClass = new MyClassActivity.MyClass(jsonArray.optJSONObject(i).optString("school"),jsonArray.optJSONObject(i).optString("grade"),
                                            jsonArray.optJSONObject(i).optInt("createId"),jsonArray.optJSONObject(i).optString("cnumber"),
                                            jsonArray.optJSONObject(i).optInt("mnumber"));
                                    myClassLists.add(myClass);
                                }
                                final Adapter adapter = new Adapter(SelectClassActivity.this,myClassLists);
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        adapter.setDefSelect(position);
                                        itemPosition = position;
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

    class Adapter extends BaseAdapter {
        private Context context;
        private List<MyClassActivity.MyClass> list;
        int defItem;
        public Adapter(Context context,List<MyClassActivity.MyClass> myClassLists){
            this.context = context;
            this.list= myClassLists;
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
            grade.setText(list.get(position).getGrade()+"年级");
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
