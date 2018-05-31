package com.fanny.washheadpad.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanny.washheadpad.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Fanny on 18/4/17.
 */

public class SetHairFragment extends Fragment {

    @BindView(R.id.im_hair_long)
    ImageView imHairLong;
    @BindView(R.id.im_hair_short)
    ImageView imHairShort;
    @BindView(R.id.tv_hair_chose)
    TextView tvHairChose;
    Unbinder unbinder;

    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_set_hair, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.im_hair_long, R.id.im_hair_short})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.im_hair_long:
                tvHairChose.setText("长发");
                callBack.onClick3("长发");
                break;
            case R.id.im_hair_short:
                tvHairChose.setText("短发");
                callBack.onClick3("短发");
                break;
        }
    }

    public interface HairCallBack {
        void onClick3(String data);
    }

    private HairCallBack callBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        if (context instanceof HairCallBack) {
            callBack = (HairCallBack) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callBack = null;
    }
}
