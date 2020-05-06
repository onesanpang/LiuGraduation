package com.example.graduation.fragment;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.graduation.AddHomeWorkActivity;
import com.example.graduation.MyClassActivity;
import com.example.graduation.ParentActivity;
import com.example.graduation.R;
import com.example.graduation.SelectClassActivity;
import com.example.graduation.TeacherActivity;
import com.example.graduation.java.HttpUtil;

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

public class CLassFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences sp;
    private String uid;
    private String myclass;
    private String addclass;
    private String identity = "";
    private Button button;
    private LinearLayout linearMyClass,linearWork,linearButton,linearShowWork;
    private LinearLayout linearAddClass;
    private ListView listView;
    private List<Work> workLists;
    private String getHomeWorkUrl = "http://47.106.112.29:8080/class/work/getHomeWork";
    private Adapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.classfragment_layout,container,false);
        initView(view);
        return view;
    }
    private void initView(View view){

        button = view.findViewById(R.id.class_button);

        linearMyClass = view.findViewById(R.id.class_linear_myclass);
        linearWork = view.findViewById(R.id.class_linear_work);
        linearButton = view.findViewById(R.id.class_linear_button);
        linearShowWork = view.findViewById(R.id.class_linear_showwork);
        linearAddClass = view.findViewById(R.id.class_linear_addclass);
        listView = view.findViewById(R.id.class_listview);
        linearMyClass.setOnClickListener(this);
        linearAddClass.setOnClickListener(this);
        linearWork.setOnClickListener(this);
        linearButton.setOnClickListener(this);

        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        identity = sp.getString("identity","");
        Log.e("class身份",identity);
        uid = sp.getString("uid","");

        if (identity.equals("2")){
            button.setText("老师创建班级");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), TeacherActivity.class));

                }
            });
        }else if (identity.equals("1")){
            button.setText("家长加入班级");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(new Intent(getActivity(), ParentActivity.class)));
                }
            });
        }
        //老师创建班级之后的界面显示
        myclass = sp.getString("myclass","");
        if (myclass.equals("yes")){
            linearMyClass.setVisibility(View.VISIBLE);
            linearWork.setVisibility(View.VISIBLE);
            linearButton.setVisibility(View.GONE);
            linearShowWork.setVisibility(View.VISIBLE);

        }
        //家长加入班级之后的界面显示
        addclass = sp.getString("addclass","");
        if (addclass.equals("yes")){
            linearButton.setVisibility(View.GONE);
            linearAddClass.setVisibility(View.VISIBLE);
            linearShowWork.setVisibility(View.VISIBLE);
        }

        if (linearMyClass.getVisibility() == View.VISIBLE){
            linearMyClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), MyClassActivity.class));
                }
            });
        }
        if (linearAddClass.getVisibility() == View.VISIBLE){
            linearAddClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), MyClassActivity.class));
                }
            });
        }
        if (linearWork.getVisibility() == View.VISIBLE){
            linearWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), SelectClassActivity.class));
                }
            });
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (linearShowWork.getVisibility() ==View.VISIBLE){
            workLists = new ArrayList<>();
            getWork(getHomeWorkUrl);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        myclass = sp.getString("myclass","");
        if (myclass.equals("yes")){
            linearMyClass.setVisibility(View.VISIBLE);
            linearWork.setVisibility(View.VISIBLE);
            linearButton.setVisibility(View.GONE);
        }
        addclass = sp.getString("addclass","");
        if (addclass.equals("yes")){
            linearButton.setVisibility(View.GONE);
            linearAddClass.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
    }

    class Adapter extends BaseAdapter{

        private Context mContext;
        private List<Work> list;

        private Adapter(Context mContext,List<Work> list){
            this.mContext = mContext;
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.classfragmentlistview_item,null);
            TextView text1 = convertView.findViewById(R.id.classfragmentlistview_text_time);
            TextView text2 = convertView.findViewById(R.id.classfragmentlistview_text_name);
            TextView text3 = convertView.findViewById(R.id.classfragmentlistview_text_context);
            final ImageView image = convertView.findViewById(R.id.classfragmentlistview_image);

            text1.setText(list.get(position).getCreateTime());
            text2.setText(list.get(position).getGrade()+"年级");
            text3.setText(list.get(position).getContents());
            final int posi = position;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(mContext).load(list.get(posi).getPhotoOne()).into(image);
                }
            });
            return convertView;
        }
    }
    class Work{
        String content;
        String createTime;
        String photoOne;
        String grade;
        public Work(String content, String createTime, String photoOne, String grade){
            this.content = content;
            this.createTime = createTime;
            this.photoOne = photoOne;
            this.grade = grade;
        }

        public String getContents() {
            return content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getPhotoOne() {
            return photoOne;
        }

        public String getGrade() {
            return grade;
        }
    }

    private void getWork(String url){
        RequestBody body = new FormBody.Builder()
                .add("uid",uid)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //Log.e("查看作业返回值",response.body().string());
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.optInt("ec") == 200){
                        final JSONArray array = object.getJSONArray("data");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < array.length(); i++) {
                                    Work work = new Work(array.optJSONObject(i).optString("content"),array.optJSONObject(i).optString("createTime"),
                                            array.optJSONObject(i).optString("photoOne"),array.optJSONObject(i).optString("grade"));
                                    workLists.add(work);
                                }
                                adapter = new Adapter(getContext(),workLists);
                                listView.setAdapter(adapter);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
