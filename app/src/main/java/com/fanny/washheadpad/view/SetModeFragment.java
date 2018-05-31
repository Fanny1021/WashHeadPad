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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Fanny on 18/4/17.
 */

public class SetModeFragment extends Fragment implements SpinnerAdapter.IOnItemSelectListener {

    @BindView(R.id.tv_quick_wash)
    TextView tvQuickWash;
    @BindView(R.id.tv_slow_wash)
    TextView tvSlowWash;
    @BindView(R.id.tv_custom_wash)
    TextView tvCustomWash;
    @BindView(R.id.tv_choseWet)
    TextView tvChoseWet;
    @BindView(R.id.rl_chose_wet)
    RelativeLayout rlChoseWet;
    @BindView(R.id.tv_choseLiquid)
    TextView tvChoseLiquid;
    @BindView(R.id.rl_chose_liquid)
    RelativeLayout rlChoseLiquid;
    @BindView(R.id.tv_choseKnead)
    TextView tvChoseKnead;
    @BindView(R.id.rl_chose_knead)
    RelativeLayout rlChoseKnead;
    @BindView(R.id.tv_choseWash)
    TextView tvChoseWash;
    @BindView(R.id.rl_chose_wash)
    RelativeLayout rlChoseWash;
    @BindView(R.id.tv_chosedry)
    TextView tvChosedry;
    @BindView(R.id.rl_chose_dry)
    RelativeLayout rlChoseDry;
    Unbinder unbinder;


    private Activity activity;
    private List<String> wetList;
    private List<String> liquidList;
    private List<String> kneadList;
    private List<String> washList;
    private List<String> dryList;
    private List<TextView> tvList;

    private SpinnerAdapter adapter;
    private SpinnerPopWindow spinnerPopWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_set_mode, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        adapter = new SpinnerAdapter(activity, wetList);
        adapter.refreshData(wetList, 0);

        spinnerPopWindow = new SpinnerPopWindow(activity);
        spinnerPopWindow.setAdatper(adapter);
        spinnerPopWindow.setItemListener(this);


        tvList = new ArrayList<>();
        tvList.add(tvQuickWash);
        tvList.add(tvSlowWash);
        tvList.add(tvCustomWash);

//        setViewSelect(0);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wetList = new ArrayList<>();
        liquidList = new ArrayList<>();
        kneadList = new ArrayList<>();
        washList = new ArrayList<>();
        dryList = new ArrayList<>();
//        initData(0x01);
    }

    private void clearData() {
        if (wetList != null && wetList.size() > 0) {
            wetList.clear();
        }
        if (liquidList != null && liquidList.size() > 0) {
            liquidList.clear();
        }
        if (kneadList != null && kneadList.size() > 0) {
            kneadList.clear();
        }
        if (washList != null && washList.size() > 0) {
            washList.clear();
        }
        if (dryList != null && dryList.size() > 0) {
            dryList.clear();
        }
    }

    private void initData(int i) {
        clearData();
        switch (i) {
            case 0x01:
                callBack.onClick2("快洗");
                for (int m = 1; m < 3; m++) {
                    wetList.add(m + "");
                    liquidList.add(m + "");
                    kneadList.add(m + "");
                    washList.add(m + "");
                    dryList.add(m + "");
                }
                break;
            case 0x02:
                callBack.onClick2("慢洗");
                for (int m = 1; m < 6; m++) {
                    wetList.add(m + "");
                    liquidList.add(m + "");
                    kneadList.add(m + "");
                    washList.add(m + "");
                    dryList.add(m + "");
                }
                break;
            case 0x03:
                callBack.onClick2("自定义洗");
                for (int m = 1; m < 6; m++) {
                    wetList.add(m + "");
                    liquidList.add(m + "");
                    kneadList.add(m + "");
                    washList.add(m + "");
                    dryList.add(m + "");
                }
                break;
        }
    }

    @OnClick({R.id.tv_quick_wash, R.id.tv_slow_wash, R.id.tv_custom_wash, R.id.rl_chose_wet, R.id.rl_chose_liquid, R.id.rl_chose_knead, R.id.rl_chose_wash, R.id.rl_chose_dry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_quick_wash:
                setViewSelect(0);
                initData(0x01);
                break;
            case R.id.tv_slow_wash:
                setViewSelect(1);
                initData(0x02);
                break;
            case R.id.tv_custom_wash:
                setViewSelect(2);
                initData(0x03);
                break;
            case R.id.rl_chose_wet:
                showSpinWindow("wet");//显示SpinerPopWindow
                break;
            case R.id.rl_chose_liquid:
                showSpinWindow("liquid");
                break;
            case R.id.rl_chose_knead:
                showSpinWindow("knead");
                break;
            case R.id.rl_chose_wash:
                showSpinWindow("wash");
                break;
            case R.id.rl_chose_dry:
                showSpinWindow("dry");
                break;
        }
    }

    private void showSpinWindow(String str) {
        spinnerPopWindow.setWidth(tvChoseWet.getWidth());
        spinnerPopWindow.setHeight(300);
        switch (str) {
            case "wet":
                spinnerPopWindow.setTag("wet");
                spinnerPopWindow.showAsDropDown(tvChoseWet);
                break;
            case "liquid":
                spinnerPopWindow.setTag("liquid");
                spinnerPopWindow.showAsDropDown(tvChoseLiquid);
                break;
            case "knead":
                spinnerPopWindow.setTag("knead");
                spinnerPopWindow.showAsDropDown(tvChoseKnead);
                break;
            case "wash":
                spinnerPopWindow.setTag("wash");
                spinnerPopWindow.showAsDropDown(tvChoseWash);
                break;
            case "dry":
                spinnerPopWindow.setTag("dry");
                spinnerPopWindow.showAsDropDown(tvChosedry);
                break;
        }
    }

    public void setViewSelect(int i) {
        for (int m = 0; m < tvList.size(); m++) {
            if (m == i) {
                tvList.get(m).setSelected(true);
            } else {
                tvList.get(m).setSelected(false);
            }
        }
    }

    @Override
    public void onItemClick(int pos) {
        String tag = spinnerPopWindow.getTag();
        switch (tag) {
//            String value = wetList.get(pos);
            case "wet":
                String value = wetList.get(pos);
                tvChoseWet.setText(value.toString());
                callBack.onClick1(tag, value);
                break;
            case "liquid":
                String value1 = liquidList.get(pos);
                tvChoseLiquid.setText(value1.toString());
                callBack.onClick1(tag, value1);
                break;
            case "knead":
                String value2 = kneadList.get(pos);
                tvChoseKnead.setText(value2.toString());
                callBack.onClick1(tag, value2);
                break;
            case "wash":
                String value3 = washList.get(pos);
                tvChoseWash.setText(value3.toString());
                callBack.onClick1(tag, value3);
                break;
            case "dry":
                String value4 = dryList.get(pos);
                tvChosedry.setText(value4.toString());
                callBack.onClick1(tag, value4);
                break;
        }
    }

    public interface ModeCallBack {
        void onClick1(String tag, String data);

        void onClick2(String mode);
    }

    private ModeCallBack callBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        if (context instanceof ModeCallBack) {
            callBack = (ModeCallBack) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callBack = null;
    }
}
