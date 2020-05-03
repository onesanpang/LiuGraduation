package com.example.graduation.java;

import android.content.ClipData;
import android.content.Context;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.example.graduation.PracticesActivity;
import com.example.graduation.R;

import java.util.List;

public class PraciticeAdapter extends BaseAdapter implements View.OnClickListener, View.OnTouchListener, View.OnFocusChangeListener {

    private int selectedEditTextPosition = -1;
    private List<PraciticeItem> stringList;
    private Context mContext;

    public PraciticeAdapter(List<PraciticeItem> stringList,Context context){
        this.stringList = stringList;
        this.mContext = context;
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = (EditText)v;
        if (hasFocus){
            editText.addTextChangedListener(mTextWatcher);
        }else{
            editText.removeTextChangedListener(mTextWatcher);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP){
            EditText editText = (EditText)v;
            selectedEditTextPosition = (int) editText.getTag();
        }
        return false;
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.practiceslistview_item,null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.editText.setOnTouchListener(this); // 正确写法
        vh.editText.setOnFocusChangeListener(this);
        vh.editText.setTag(position);


        if (selectedEditTextPosition != -1 && position == selectedEditTextPosition){
            vh.editText.requestFocus();
        }else{
            vh.editText.clearFocus();
        }

        String text = stringList.get(position).getText();
        vh.editText.setText(text);
        vh.editText.setSelection(vh.editText.length());


        return convertView;
    }
    private TextWatcher mTextWatcher = new SimpleTextWatcher(){
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (selectedEditTextPosition != -1){
                Log.w("MyEditAdapter", "onTextPosiotion " + selectedEditTextPosition);
                PraciticeItem item = (PraciticeItem) getItem(selectedEditTextPosition);
                item.setText(s.toString());
                ((PracticesActivity)mContext).saveList(selectedEditTextPosition,s.toString());
            }
        }
    };

    public class ViewHolder {
        EditText editText;

        public ViewHolder(View convertView) {
            editText = (EditText) convertView.findViewById(R.id.practicesitem_text2);
        }
    }
}
