package com.example.graduation.java;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.graduation.R;

import java.util.ArrayList;
import java.util.List;

public class PracticesItemAdapter extends BaseAdapter {
    private ViewHolder mViewHolder;
    private LayoutInflater mLayoutInflater;
    private List<String> mList;
    private int mTouchItemPosition = -1;
    private List<String> editInputList;

    public PracticesItemAdapter(Context context, List<String> list) {
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
        editInputList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.practiceslistview_item, null);
            mViewHolder.mTextView = (TextView) convertView.findViewById(R.id.practicesitem_text1);
            mViewHolder.mEditText = (EditText) convertView.findViewById(R.id.practicesitem_text2);
            mViewHolder.mEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    //注意，此处必须使用getTag的方式，不能将position定义为final，写成mTouchItemPosition = position
                    mTouchItemPosition = (Integer) view.getTag();
                    return false;
                }
            });

            // 让ViewHolder持有一个TextWathcer，动态更新position来防治数据错乱；不能将position定义成final直接使用，必须动态更新
            mViewHolder.mTextWatcher = new MyTextWatcher();
            mViewHolder.mEditText.addTextChangedListener(mViewHolder.mTextWatcher);
            mViewHolder.updatePosition(position);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
            //动态更新TextWathcer的position
            mViewHolder.updatePosition(position);
        }

        mViewHolder.mTextView.setText(mList.get(position) + "  =");
        String s = "";
        if (editInputList.size()>0&&editInputList.get(position) != null) {
            s = editInputList.get(position);
        }
        mViewHolder.mEditText.setText(s);
        mViewHolder.mEditText.setTag(position);

        if (mTouchItemPosition == position) {
            mViewHolder.mEditText.requestFocus();
            mViewHolder.mEditText.setSelection(mViewHolder.mEditText.getText().length());
        } else {
            mViewHolder.mEditText.clearFocus();
        }
        return convertView;
    }


    static final class ViewHolder {
        TextView mTextView;
        EditText mEditText;
        MyTextWatcher mTextWatcher;

        //动态更新TextWathcer的position
        public void updatePosition(int position) {
            mTextWatcher.updatePosition(position);
        }
    }

    class MyTextWatcher implements TextWatcher {
        //由于TextWatcher的afterTextChanged中拿不到对应的position值，所以自己创建一个子类
        private int mPosition;

        public void updatePosition(int position) {
            mPosition = position;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editInputList.set(mPosition, s.toString());
        }
    }
}
