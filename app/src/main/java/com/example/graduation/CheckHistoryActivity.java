package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.graduation.fragment.CLassFragment;
import com.example.graduation.fragment.CheckFragment;
import com.example.graduation.fragment.CheckHistoryFragment;
import com.example.graduation.fragment.MyFragment;
import com.example.graduation.fragment.ParciticeHistoryFragment;
import com.example.graduation.fragment.PracticeFragment;
import com.example.graduation.java.StatusBarTransparent;

import java.util.ArrayList;
import java.util.List;

public class CheckHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageBack;
    private TextView textCheck,textParcity;
    private View viewCheck,viewParcity;
    private ViewPager viewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_history);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
            StatusBarTransparent.makeStatusBarTransparent(this);
        }
        initView();
        initDatas();
    }

    private void initView(){
        imageBack = findViewById(R.id.checkhistory_image_back);
        textCheck = findViewById(R.id.checkhistory_text_check);
        textParcity = findViewById(R.id.checkhistory_text_parcitice);
        viewCheck = findViewById(R.id.checkhistory_view_check);
        viewParcity = findViewById(R.id.checkhistory_view_parcitice);
        viewPager = findViewById(R.id.checkhistory_viewpager);

        imageBack.setOnClickListener(this);
        textCheck.setOnClickListener(this);
        textParcity.setOnClickListener(this);
    }

    private void initDatas(){
        mFragments = new ArrayList<>();
        mFragments.add(new CheckHistoryFragment());
        mFragments.add(new ParciticeHistoryFragment());
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
            case R.id.checkhistory_image_back:
                finish();
                break;
            case R.id.checkhistory_text_check:
                selectTab(0);
                break;
            case R.id.checkhistory_text_parcitice:
                selectTab(1);
                break;
        }
    }

    private void selectTab(int position){
        clearTab();
        switch(position){
            case 0:
                viewCheck.setBackgroundColor(Color.parseColor("#F7D981"));
                textCheck.setTextColor(Color.parseColor("#000000"));
                break;
            case 1:
                viewParcity.setBackgroundColor(Color.parseColor("#F7D981"));
                textParcity.setTextColor(Color.parseColor("#000000"));
                break;
        }

        viewPager.setCurrentItem(position);
    }

    private void clearTab(){
        viewCheck.setBackgroundColor(Color.parseColor("#ffffff"));
        textCheck.setTextColor(Color.parseColor("#cccccc"));
        viewParcity.setBackgroundColor(Color.parseColor("#ffffff"));
        textParcity.setTextColor(Color.parseColor("#cccccc"));
    }

}
