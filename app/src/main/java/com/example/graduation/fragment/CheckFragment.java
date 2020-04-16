package com.example.graduation.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduation.R;

import static android.app.Activity.RESULT_OK;

public class CheckFragment extends Fragment implements View.OnClickListener {

    private ImageView imageCamera;
    private String url = "";//上传的url
    private Intent intent;
    private final int PICK = 1;
    private ImageView imageIcon;
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkfragment_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        imageCamera = view.findViewById(R.id.check_image_camera);
        imageIcon = view.findViewById(R.id.check_image_icon);
        imageCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_image_camera:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, PICK);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PICK:
                Bitmap bitmap = data.getParcelableExtra("data");
                imageIcon.setImageBitmap(bitmap);
                //uploadImg();
                break;

        }
    }
}
