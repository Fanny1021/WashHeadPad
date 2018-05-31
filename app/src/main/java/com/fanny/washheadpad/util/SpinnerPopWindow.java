package com.fanny.washheadpad.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.fanny.washheadpad.R;
import com.fanny.washheadpad.adapter.SpinnerAdapter;

import java.util.List;

/**
 * Created by Fanny on 18/4/17.
 */

public class SpinnerPopWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ListView mListView;
    private SpinnerAdapter mAdapter;
    private SpinnerAdapter.IOnItemSelectListener mItemSelectListener;

    public SpinnerPopWindow(Context context) {
        super(context);

        mContext = context;
        init();
    }

    public void setItemListener(SpinnerAdapter.IOnItemSelectListener listener) {
        mItemSelectListener = listener;
    }

    public void setAdatper(SpinnerAdapter adapter) {
        mAdapter = adapter;
        mListView.setAdapter(mAdapter);
    }


    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_spinner_window, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);

        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
    }


    public void refreshData(List<String> list, int selIndex) {
        if (list != null && selIndex != -1) {
            if (mAdapter != null) {
                mAdapter.refreshData(list, selIndex);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        dismiss();
        if (mItemSelectListener != null) {
            mItemSelectListener.onItemClick(position);
        }
    }

    private String tag;
    public void setTag(String str){
        tag=str;
    }
    public String getTag(){
        return tag;
    }
}
