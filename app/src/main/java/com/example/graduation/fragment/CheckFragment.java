package com.example.graduation.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.graduation.PhoneResultsActivity;
import com.example.graduation.R;
import com.example.graduation.java.HttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class CheckFragment extends Fragment implements View.OnClickListener {

    private ImageView imageCamera;
    private final String url = "http://47.106.112.29:8080/work/uploadAndCheck";//上传的url
    private Intent intent;
    private final int REQUEST_PERMISSIONS = 1;
    private ImageView imageIcon;
    private final int CAMERA_RESULT_CODE = 2;
    private final int REQUEST_CODE_PICK_IMAGE = 3;
    private LinearLayout linearCamera;

    private Dialog dialog;
    private View inflate;
    private TextView bottomPhoto, bottomGallery, bottomCancel;
    private Uri imageUri;
    private SharedPreferences sp;


    private LinearLayout linearMyclass;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkfragment_layout, container, false);
        initView(view);

        initPermission();
        return view;
    }

    private void initView(View view) {
        imageCamera = view.findViewById(R.id.check_image_camera);
        imageIcon = view.findViewById(R.id.check_image);
        linearCamera = view.findViewById(R.id.check_linear_camera);

        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        imageCamera.setOnClickListener(this);
        linearCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_image_camera:
                showBottomDialog();
                break;
            case R.id.check_linear_camera:
                showBottomDialog();
                break;
            case R.id.bottom_dialog_photo:
                dialog.dismiss();
                openSysCamera();
                break;
            case R.id.bottom_dialog_gallery:
                dialog.dismiss();
                openGallery();
                break;
            case R.id.bottom_dialog_cancel:
                dialog.dismiss();
                break;
        }
    }


    private void showBottomDialog() {
        dialog = new Dialog(getContext(), R.style.BottomStyle);
        inflate = LayoutInflater.from(getContext()).inflate(R.layout.bsd_new_order, null);
        bottomPhoto = inflate.findViewById(R.id.bottom_dialog_photo);
        bottomGallery = inflate.findViewById(R.id.bottom_dialog_gallery);
        bottomCancel = inflate.findViewById(R.id.bottom_dialog_cancel);

        bottomPhoto.setOnClickListener(this);
        bottomCancel.setOnClickListener(this);
        bottomGallery.setOnClickListener(this);

        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 10;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }


    /**
     * 初始化相机相关权限
     * 适配6.0+手机的运行时权限
     */
    private void initPermission() {
        String[] permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        //检查权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 之前拒绝了权限，但没有点击 不再询问 这个时候让它继续请求权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_PERMISSIONS);
            } else {
                //注册相机权限
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getContext(), "拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void openSysCamera() {
        //最后一个参数是文件夹的名称，可以随便起
        File file = new File(Environment.getExternalStorageDirectory(), "拍照");
        if (!file.exists()) {
            file.mkdir();
        }

        //这里将时间戳作为不同照片的名称
        File output = new File(file, System.currentTimeMillis() + ".jpg");

        /**
         * 如果该文件夹已经存在，则删除它，否则创建一个
         */
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageUri  = Uri.fromFile(output);
        intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent, CAMERA_RESULT_CODE);
    }

    private void openGallery(){
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE_PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case CAMERA_RESULT_CODE:
                if (resultCode == RESULT_OK){
                    Bitmap bit = null;
                    try {
                        bit = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    imageIcon.setImageBitmap(bit);

                }
                break;
            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    Bitmap bit = null;
                    try {
                        bit = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                        loadImage(compressImage(bit),url);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void loadImage(final File file,final String url){
       final String uid = sp.getString("uid","");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //创建RequestBody
                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
                    //构建MultipartBody
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file","testImage.png",fileBody)
                            .addFormDataPart("uid",uid)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.optInt("ec") == 200){
                            Intent intent = new Intent(getActivity(), PhoneResultsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("checkresult",object.optInt("data"));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static File compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 500) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        //图片名
        String format1 = format.format(date);

        File file = new File(Environment.getExternalStorageDirectory(), format1 + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        Log.d("aaaa", "compressImage: " + file);
        // recycleBitmap(bitmap);
        return file;
    }

}
