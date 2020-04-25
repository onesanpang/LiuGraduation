package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.graduation.java.StatusBarTransparent;

public class TeacherActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageBack;
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
        imageBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.teacher_image_back:
                finish();
                break;
        }
    }
}
