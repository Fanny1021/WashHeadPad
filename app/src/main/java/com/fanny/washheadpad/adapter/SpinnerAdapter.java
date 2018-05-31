package com.fanny.washheadpad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fanny.washheadpad.R;

import java.util.List;

/**
 * Created by Fanny on 18/4/17.
 */

public class SpinnerAdapter extends BaseAdapter {

    public static interface IOnItemSelectListener {
        public void onItemClick(int pos);
    }

    private List<String> mObjects;

    private LayoutInflater mInflater;

    public SpinnerAdapter(Context context, List<String> mObjects) {
        this.mObjects = mObjects;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void refreshData(List<String> objects, int selIndex) {
        mObjects = objects;
        if (selIndex < 0) {
            selIndex = 0;
        }
        if (selIndex >= mObjects.size()) {
            selIndex = mObjects.size() - 1;
        }
    }


    @Override
    public int getCount() {

        return mObjects.size();
    }

    @Override
    public Object getItem(int pos) {
        return mObjects.get(pos).toString();
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_spinner_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Object item =  getItem(pos);
        viewHolder.mTextView.setText(mObjects.get(position));

        return convertView;
    }

    public static class ViewHolder {
        public TextView mTextView;
    }
}
