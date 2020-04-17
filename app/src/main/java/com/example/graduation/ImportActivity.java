package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduation.fragment.CLassFragment;
import com.example.graduation.fragment.CheckFragment;
import com.example.graduation.fragment.MyFragment;
import com.example.graduation.fragment.PracticeFragment;
import com.example.graduation.java.StatusBarTransparent;

import java.util.ArrayList;
import java.util.List;

public class ImportActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textCheck,textClass,textMy,textPractice;
    private ViewPager viewPager;
    private List<Fragment> mFragments;
    //四个Tab对应的布局
    private LinearLayout mTabCheck,mTabClass,mTabMy,mTabPractice;
    private FragmentPagerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
        initDatas();

    }

    private void initView(){
        viewPager = findViewById(R.id.inport_viewpager);

        mTabCheck = findViewById(R.id.bottom_tab_check);
        mTabClass = findViewById(R.id.bottom_tab_class);
        mTabMy = findViewById(R.id.bottom_tab_my);
        mTabPractice = findViewById(R.id.bottom_tab_pratice);
        textCheck = findViewById(R.id.bottom_tab_check_text);
        textClass = findViewById(R.id.bottom_tab_class_text);
        textMy = findViewById(R.id.bottom_tab_my_text);

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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bottom_tab_check:
                break;
            case R.id.bottom_tab_class:
                break;
            case R.id.bottom_tab_my:
                break;
            case R.id.bottom_tab_pratice:
                break;
        }
    }

    private void clearTab(){

    }
}
