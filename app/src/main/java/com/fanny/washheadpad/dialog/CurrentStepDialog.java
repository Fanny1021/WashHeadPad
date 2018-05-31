package com.fanny.washheadpad.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
//import android.support.v4.app.DialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.fanny.washheadpad.R;
import com.fanny.washheadpad.util.ToastUtil;

import java.net.Socket;

/**
 * Created by Fanny on 18/4/18.
 */

public class CurrentStepDialog extends DialogFragment implements View.OnClickListener {

    private View dialogView;
    private TextView tvContinue;
    private TextView tvSkip;
    private TextView tvCurrentStep;
    private Activity activity;


    private boolean isStop = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        dialogView = View.inflate(getActivity(), R.layout.layout_current_step, null);
        tvContinue = dialogView.findViewById(R.id.tv_step_continue);
        tvSkip = dialogView.findViewById(R.id.tv_step_next);
        tvContinue.setOnClickListener(this);
        tvSkip.setOnClickListener(this);
        tvCurrentStep = dialogView.findViewById(R.id.tv_current_step);
        String tag = getTag();
        if (!tag.equals("")) {
            tvCurrentStep.setText(tag);
        }
        builder.setView(dialogView);

        return builder.create();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public interface StepCallBack {
        void onClick(String str);
    }

    private StepCallBack stepCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        if (context instanceof StepCallBack) {
            stepCallBack = (StepCallBack) context;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_step_continue:
                if (!isStop) {
                    stepCallBack.onClick("pause");
                    tvContinue.setText("继续");
                    isStop = !isStop;
                } else {
                    stepCallBack.onClick("continue");
                    tvContinue.setText("暂停");
                    isStop = !isStop;
                    dismiss();
                }
                break;
            case R.id.tv_step_next:
                if (isStop) {
                    ToastUtil.show(activity.getApplicationContext(),"请退出暂停步骤再执行");
                } else {
                    stepCallBack.onClick("next");
                    dismiss();
                }
                break;

        }
    }
}
