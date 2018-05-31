package com.fanny.washheadpad.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanny.washheadpad.R;
import com.fanny.washheadpad.adapter.SpinnerAdapter;
import com.fanny.washheadpad.util.SpinnerPopWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fanny on 18/4/17.
 */

public class SetTempFragment extends Fragment implements View.OnClickListener, SpinnerAdapter.IOnItemSelectListener {

    private Activity activity;
    private List<String> list;
    private SpinnerPopWindow spinnerPopWindow;
    private SpinnerAdapter adapter;
    private TextView mTvTemp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_set_temp, container, false);
        mTvTemp = rootView.findViewById(R.id.tv_chosetemp);
        RelativeLayout rlTemp = rootView.findViewById(R.id.rl_chose_temp);
        rlTemp.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        list.add("31");
        list.add("32");
        list.add("33");
        list.add("34");
        list.add("35");
        list.add("36");
        list.add("37");
        list.add("38");
        list.add("39");
        list.add("40");
        list.add("41");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new SpinnerAdapter(activity, list);
        adapter.refreshData(list, 0);

        spinnerPopWindow = new SpinnerPopWindow(activity);
        spinnerPopWindow.setAdatper(adapter);
        spinnerPopWindow.setItemListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_chose_temp:
                showSpinWindow();//显示SpinerPopWindow
                break;
        }
    }

    private void showSpinWindow() {
        spinnerPopWindow.setWidth(mTvTemp.getWidth());
        spinnerPopWindow.setHeight(300);
        spinnerPopWindow.showAsDropDown(mTvTemp);
    }

    @Override
    public void onItemClick(int pos) {
        String value = list.get(pos);
        mTvTemp.setText(value.toString());

        callBack.onClick(value);
    }

    public interface TempCallBack {
        void onClick(String data);
    }

    private TempCallBack callBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        if (context instanceof TempCallBack) {
            callBack = (TempCallBack) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callBack = null;
    }
}
