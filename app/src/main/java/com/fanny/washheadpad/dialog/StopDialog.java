package com.fanny.washheadpad.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.fanny.washheadpad.R;
import com.fanny.washheadpad.util.SocketUtil;

import java.io.IOException;

//import android.support.v4.app.DialogFragment;

/**
 * Created by Fanny on 18/4/18.
 */

public class StopDialog extends DialogFragment implements View.OnClickListener {

    private View dialogView;
    private TextView tvYes;
    private TextView tvCancle;
    private TextView tvTitle;
    private String tag;
    private Activity activity;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        dialogView = View.inflate(getActivity(), R.layout.layout_power_stop, null);

        tvYes = dialogView.findViewById(R.id.tv_power_yes);
        tvCancle = dialogView.findViewById(R.id.tv_power_cancle);
        tvTitle = dialogView.findViewById(R.id.tv_power_title);
        tvYes.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
        tag = getTag();
        switch (tag) {
            case "poweroff":
                tvTitle.setText("断开与洗头机的连接？");
                break;
            case "stop":
                tvTitle.setText("急停？");
                break;
        }

        builder.setView(dialogView);

        return builder.create();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_power_yes:
                if (tag.equals("poweroff")) {

                    stopCallBack.onClick("poweroff");
                    /**
                     * 断开连接
                     */
//                    if (SocketUtil.socket != null) {
                        /**
                         * 发送退出命令
                         */
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        }).start();
//                    }
                }
                if (tag.equals("stop")) {
                    /**
                     * 发送急停命令
                     */
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            SocketUtil.SendDataByte(SocketUtil.getOutputStream(), sendData);
//                        }
//                    }).start();
                    stopCallBack.onClick("stop");
                }
                dismiss();
                break;
            case R.id.tv_power_cancle:
                dismiss();
                break;

        }
    }


    public interface StopCallBack {
        void onClick(String str);
    }

    private StopCallBack stopCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        if (context instanceof StopCallBack) {
            stopCallBack = (StopCallBack) context;
        }
    }
}
