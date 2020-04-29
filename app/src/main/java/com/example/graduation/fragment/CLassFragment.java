package com.example.graduation.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduation.MyClassActivity;
import com.example.graduation.R;
import com.example.graduation.TeacherActivity;

public class CLassFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences sp;
    private String myclass;
    private String identity = "";
    private Button button;
    private LinearLayout linearMyClass,linearWork,linearButton;


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

        linearMyClass.setOnClickListener(this);
        linearWork.setOnClickListener(this);
        linearButton.setOnClickListener(this);

        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        identity = sp.getString("identity","");

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
        }

        myclass = sp.getString("myclass","");
        if (myclass.equals("yes")){
            linearMyClass.setVisibility(View.VISIBLE);
            linearWork.setVisibility(View.VISIBLE);
            linearButton.setVisibility(View.GONE);
        }

        if (linearMyClass.getVisibility() == View.VISIBLE){
            linearMyClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), MyClassActivity.class));
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
    }

    @Override
    public void onClick(View v) {
    }
}
