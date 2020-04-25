package com.example.graduation.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduation.R;

public class CLassFragment extends Fragment {
    private SharedPreferences sp;
    private String identity = "";
    private Button button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.classfragment_layout,container,false);
        initView(view);
        return view;
    }
    private void initView(View view){

        button = view.findViewById(R.id.class_button);

        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        identity = sp.getString("identity","");
        if (identity.equals("1")){
            button.setText("老师创建班级");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }else if (identity.equals("2")){
            button.setText("家长加入班级");
        }
    }
}
