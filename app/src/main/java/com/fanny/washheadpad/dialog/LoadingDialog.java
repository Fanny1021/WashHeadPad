package com.fanny.washheadpad.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanny.washheadpad.R;

import java.util.Timer;
import java.util.TimerTask;

//import android.support.v4.app.DialogFragment;

/**
 * Created by Fanny on 18/4/18.
 */

public class LoadingDialog extends DialogFragment implements View.OnClickListener {

    private View dialogView;
    private LinearLayout ll_check;
    private LinearLayout ll_check_again;
    private TextView tv_load;
    private Button btn_check;
    private Button btn_cancle;
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        dialogView = View.inflate(getActivity(), R.layout.layout_loading, null);
        ll_check = dialogView.findViewById(R.id.ll_check);
        ll_check_again = dialogView.findViewById(R.id.ll_check_again);

        tv_load = dialogView.findViewById(R.id.tv_loading);
        btn_check = dialogView.findViewById(R.id.btn_check_again);
        btn_cancle = dialogView.findViewById(R.id.btn_check_cancle);

        String tag = getTag();
        if (!tag.equals("")) {
            tv_load.setText(tag);
        }

        btn_check.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x01:
                        ll_check.setVisibility(View.GONE);
                        ll_check_again.setVisibility(View.VISIBLE);
                        break;
                    case 0x02:
                        ll_check.setVisibility(View.VISIBLE);
                        ll_check_again.setVisibility(View.GONE);
                        break;
                }
            }
        };

        /**
         * 设置一个定时器，如果秒之后检测仍旧失败，则关闭对话框，提示检测失败，重新检测
         */
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0x01;
                handler.sendMessage(message);
            }
        };
        timer.schedule(timerTask, 5000);//5m后执行
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
            case R.id.btn_check_again:
                /**
                 * 发送检测命令
                 */

                ll_check.setVisibility(View.VISIBLE);
                ll_check_again.setVisibility(View.GONE);
                if (timerTask != null){
                    timerTask.cancel();  //将原任务从队列中移除
                }
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 0x01;
                        handler.sendMessage(message);
                    }
                };
                timer.schedule(timerTask, 5000);//5m后执行
                break;
            case R.id.btn_check_cancle:
                timer.cancel();
                dismiss();
                break;
        }

    }
}
