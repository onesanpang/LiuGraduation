package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.graduation.java.PracticesItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PracticesActivity extends AppCompatActivity {

    private ListView listView;
    private String data;
    private List<String> lists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practices);

        Bundle bundle=getIntent().getExtras();
        data= bundle.getString("result");

        Log.e("data",data);

        initVIew();
        setListView();
    }

    private void initVIew(){
        listView = findViewById(R.id.practices_listview);

        lists = new ArrayList<>();

    }
    private void setListView(){
        try {
            JSONObject object = new JSONObject(data);
            JSONArray jsonArray = object.optJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                String s = jsonArray.optString(i);
                lists.add(s);
            }

            PracticesItemAdapter itemAdapter = new PracticesItemAdapter(this,lists);
            listView.setAdapter(itemAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
