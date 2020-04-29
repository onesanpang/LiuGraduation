package com.example.graduation;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.graduation.fragment.CLassFragment;
import com.example.graduation.fragment.CheckFragment;
import com.example.graduation.fragment.MyFragment;
import com.example.graduation.fragment.PracticeFragment;
import com.example.graduation.java.HttpUtil;
import com.example.graduation.java.StatusBarTransparent;

import org.jetbrains.annotations.NotNull;
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

import static com.example.graduation.R.id;
import static com.example.graduation.R.layout;
import static com.example.graduation.R.mipmap;

public class ImportActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textCheck,textClass,textMy,textPractice;
    private ImageView imageCheck,imageClass,imageMy,imagePractice;
    private ViewPager viewPager;
    private List<Fragment> mFragments;
    //四个Tab对应的布局
    private LinearLayout mTabCheck,mTabClass,mTabMy,mTabPractice;
    private FragmentPagerAdapter mAdapter;
    private String userInfoUrl = "http://47.106.112.29:8080/user/getUser";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String uid;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            editor.putString("nickName", String.valueOf(msg.obj));
            editor.putString("identity", String.valueOf(msg.what));
            editor.commit();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_import);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
        initDatas();
        getUserInfo(userInfoUrl);

    }

    private void initView(){
        viewPager = findViewById(id.inport_viewpager);

        mTabCheck = findViewById(id.bottom_tab_check);
        mTabClass = findViewById(id.bottom_tab_class);
        mTabMy = findViewById(id.bottom_tab_my);
        mTabPractice = findViewById(id.bottom_tab_pratice);
        textCheck = findViewById(id.bottom_tab_check_text);
        textClass = findViewById(id.bottom_tab_class_text);
        textMy = findViewById(id.bottom_tab_my_text);
        textPractice = findViewById(id.bottom_tab_pratice_text);
        imageCheck = findViewById(id.bottom_tab_check_img);
        imageMy = findViewById(id.bottom_tab_my_img);
        imagePractice = findViewById(id.bottom_tab_pratice_img);
        imageClass = findViewById(id.bottom_tab_class_img);
        sp = getSharedPreferences("user",MODE_PRIVATE);
        editor = sp.edit();
        uid = sp.getString("uid","");

        mTabCheck.setOnClickListener(this);
        mTabClass.setOnClickListener(this);
        mTabMy.setOnClickListener(this);
        mTabPractice.setOnClickListener(this);
    }

    private void initDatas(){
        mFragments = new ArrayList<>();
        mFragments.add(new CheckFragment());
        mFragments.add(new PracticeFragment());
        mFragments.add(new CLassFragment());
        mFragments.add(new MyFragment());
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
                selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        selectTab(0);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case id.bottom_tab_check:
                selectTab(0);
                break;
            case id.bottom_tab_class:
                selectTab(2);
                break;
            case id.bottom_tab_my:
                selectTab(3);
                break;
            case id.bottom_tab_pratice:
                selectTab(1);
                break;
        }
    }
    private void selectTab(int position){
        clearTab();
        switch(position){
            case 0:
                imageCheck.setImageResource(mipmap.camera_yellow);
                textCheck.setTextColor(Color.parseColor("#000000"));
                break;
            case 1:
                imagePractice.setImageResource(mipmap.practice_yellow);
                textPractice.setTextColor(Color.parseColor("#000000"));
                break;
            case 2:
                imageClass.setImageResource(mipmap.class_yellow);
                textClass.setTextColor(Color.parseColor("#000000"));
                break;
            case 3:
                imageMy.setImageResource(mipmap.my_yellow);
                textMy.setTextColor(Color.parseColor("#000000"));
                break;
        }

        viewPager.setCurrentItem(position);
    }

    private void clearTab(){
        imageCheck.setImageResource(mipmap.camera_black);
        textCheck.setTextColor(Color.parseColor("#cccccc"));
        imagePractice.setImageResource(mipmap.practice_black);
        textPractice.setTextColor(Color.parseColor("#cccccc"));
        imageClass.setImageResource(mipmap.class_black);
        textClass.setTextColor(Color.parseColor("#cccccc"));
        imageMy.setImageResource(mipmap.my_black);
        textMy.setTextColor(Color.parseColor("#cccccc"));
    }

    private void getUserInfo(String url){
        RequestBody body = new FormBody.Builder()
                .add("id",uid)
                .build();
        HttpUtil.sendJsonOkhttpRequest(url, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("response","error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body().toString() != null){
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.has("ec")){
                            if (object.optInt("ec") == RegisterActivity.SUCCESS){
                                final JSONObject data = object.optJSONObject("data");
                                Message msg = new Message();
                                msg.what = data.optInt("identity");
                                msg.obj = data.optString("nickName");
                                mHandler.sendMessage(msg);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
