package com.fanny.washheadpad.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanny.washheadpad.R;
import com.fanny.washheadpad.util.FragmentSwitchTool;
import com.fanny.washheadpad.util.SocketUtil;
import com.fanny.washheadpad.util.ToastUtil;

import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetActivity extends AppCompatActivity implements SetTempFragment.TempCallBack, SetModeFragment.ModeCallBack, SetHairFragment.HairCallBack {

    @BindView(R.id.rl_temp)
    RelativeLayout rlTemp;
    @BindView(R.id.rl_mode)
    RelativeLayout rlMode;
    @BindView(R.id.rl_hair)
    RelativeLayout rlHair;
    @BindView(R.id.ll_set_content)
    FrameLayout llSetContent;
    @BindView(R.id.tv_set_temp)
    TextView tvSetTemp;
    @BindView(R.id.tv_set_mode)
    TextView tvSetMode;
    @BindView(R.id.tv_set_hair)
    TextView tvSetHair;
    @BindView(R.id.iv_set_exit)
    ImageView ivSetExit;
//    private char[] sendData;

    private String TAG = "SetActivity";
    private Intent resultIntent;

    private String mHairFlag = "";//控制一定需要选择发型才能退出界面的操作
    private String mModeFlag = "";//控制一定需要选择模式才能退出界面的操作
    private String waterTemp = "";//控制一定需要选择水温才能退出界面的操作


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        setFinishOnTouchOutside(false);
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.height = (int) (display.getHeight() * 0.8);
        attributes.width = (int) (display.getWidth() * 0.8);
        getWindow().setAttributes(attributes);


        FragmentSwitchTool tool = new FragmentSwitchTool(getSupportFragmentManager(), R.id.ll_set_content);
        tool.setClickableViews(
                rlTemp, rlMode, rlHair
        );
        tool.setShowAnimator(false);
        tool.addSelectedViews(new View[]{tvSetTemp}).addSelectedViews(new View[]{tvSetMode})
                .addSelectedViews(new View[]{tvSetHair});
        tool.setFragments(SetTempFragment.class, SetModeFragment.class, SetHairFragment.class);

        tool.changeTag(rlTemp);

        mThreadPool = Executors.newCachedThreadPool();

        resultIntent = new Intent();

        initSocketData();

    }

    private byte[] sendData;

    /**
     * 发送控制洗头机数据协议：
     * <p>
     * 11字节   12字节
     * 4F        53        开始开机自检
     * 48        54        开始头型检测
     * <p>
     * 58        4B        快洗 启动
     * 4D        4D        慢洗 启动
     * <p>
     * 4D        57        手动润湿
     * 4D        53        手动加洗发液
     * 4D        58        手动搓揉
     * 4D        46        手动冲洗
     * 4D        50        手动风干
     * 4D        45        手动完成
     * <p>
     * 4C        41        快洗时间设置
     * 4C        42        慢洗时间设置
     * 4C        43        手动洗时间设置
     * <p>
     * 4C        4C        长发设置
     * 4C        53        短发设置
     * <p>
     * 53        54        水箱水温设置
     * 53        57        润湿时间设置
     * 53        53        洗发液次数设置
     * 53        58        搓揉设置
     * 53        46        冲洗设置
     * 53        50        风干设置
     * <p>
     * 44        53        消毒
     * 50        53        当前流程暂停与再启动
     * 45        54        退出当前流程
     * 45        53        软件急停
     */

    private void initSocketData() {
        sendData = new byte[23];
        sendData[0] = (byte) 0xEA;
        sendData[1] = (byte) 0xEB;
        sendData[2] = 0x0B;
        sendData[3] = 0x00;
        sendData[4] = 0x0A;
        sendData[5] = 0x01;
        sendData[6] = 0x02;
        sendData[7] = 0x00;
        sendData[8] = 0x01;
        sendData[9] = '$';
        sendData[10] = 0x4F;
        sendData[11] = 0x53;
        sendData[12] = 0;
        sendData[13] = 0;
        sendData[14] = 0;
        sendData[15] = 0;
        sendData[16] = 0;
        sendData[17] = 0;
        sendData[18] = 0;
        sendData[19] = '#';
        sendData[20] = 0x55;
        sendData[21] = (byte) 0xE5;
        sendData[22] = (byte) 0xD4;
    }

    @OnClick({R.id.rl_temp, R.id.rl_mode, R.id.rl_hair, R.id.iv_set_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_temp:
                break;
            case R.id.rl_mode:
                break;
            case R.id.rl_hair:
                break;
            case R.id.iv_set_exit:

                if (waterTemp.equals("")) {
                    ToastUtil.show(this, "请设定水温");
                } else {
                    if (mModeFlag.equals("")) {
                        ToastUtil.show(this, "请选择洗发模式");
                    } else {
                        if (mHairFlag.equals("长发") || mHairFlag.equals("短发")) {
                            finish();
                        } else {
                            ToastUtil.show(this, "请选择发型");
                        }
                    }
                }

                break;
        }
    }

    private Handler sendHandler;
    private OutputStream out;
    private ExecutorService mThreadPool;

    @Override
    protected void onStart() {
        super.onStart();
        if (SocketUtil.socket == null) {
            Log.e(TAG, "socket null");
        } else {
            if (SocketUtil.socket.isConnected()) {
                Log.e(TAG, "socket isConnect");

                Runnable oneRun = new Runnable() {
                    @Override
                    public void run() {
                        out = SocketUtil.getOutputStream();
                        Looper.prepare();
                        sendHandler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                switch (msg.what) {
                                    case 0x01:
                                        SocketUtil.SendData(out, (char[]) msg.obj);
                                        break;
                                    case 0x02:
//                                        SocketUtil.SendData(out, (char[]) msg.obj);
                                        SocketUtil.SendDataByte(out, (byte[]) msg.obj);
                                        break;
                                }
                            }
                        };
                        Looper.loop();
                    }
                };

                mThreadPool.execute(oneRun);

            } else {
                Log.e(TAG, "socket not con");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThreadPool.shutdown();
    }

    /**
     * 水温设置
     *
     * @param data
     */
    @Override
    public void onClick(String data) {

        waterTemp = data;

        if (sendHandler != null) {

            /**
             * 解析水温数据［31，41］
             */
            String a = data.substring(0, 1);//高位
            String b = data.substring(1);//低位
            int m = Integer.parseInt(a, 16);
            int n = Integer.parseInt(b, 16);
            Log.e(TAG, m + "");
            Log.e(TAG, n + "");

            sendData[10] = 0x53;
            sendData[11] = 0x54;
            sendData[12] = (byte) m;
            sendData[13] = (byte) n;
            sendData[14] = 0x00;

            Message msg = new Message();
            msg.what = 0x02;
            msg.obj = sendData;
            sendHandler.sendMessage(msg);
        } else {
            Log.e(TAG, "null");
        }
    }

    /**
     * 模式具体数据设置
     *
     * @param data
     */
    @Override
    public void onClick1(String tag, String data) {
        if (sendHandler != null) {
            int m = 0;
            Message msg = new Message();
            msg.what = 0x02;

            switch (tag) {
                case "wet":
                    /**
                     * 解析数据
                     */
                    m = Integer.parseInt(data, 16);
                    Log.e(TAG, m + "");

                    sendData[10] = 'S';
                    sendData[11] = 'W';
                    sendData[12] = 0x00;
                    sendData[13] = (byte) m;
                    sendData[14] = 0x00;

                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);

                    resultIntent.putExtra("wet", m);

                    break;
                case "liquid":
                    /**
                     * 解析数据
                     */
                    m = Integer.parseInt(data, 16);
                    Log.e(TAG, m + "");

                    sendData[10] = 'S';
                    sendData[11] = 'S';
                    sendData[12] = 0x00;
                    sendData[13] = (byte) m;
                    sendData[14] = 0x00;

                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);

                    resultIntent.putExtra("liquid", m);

                    break;
                case "knead":

                    /**
                     * 解析数据
                     */
                    m = Integer.parseInt(data, 16);
                    Log.e(TAG, m + "");

                    sendData[10] = 'S';
                    sendData[11] = 'M';
                    sendData[12] = 0x00;
                    sendData[13] = (byte) m;
                    sendData[14] = 0x00;

                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);

                    resultIntent.putExtra("knead", m);

                    break;
                case "wash":
                    /**
                     * 解析数据
                     */
                    m = Integer.parseInt(data, 16);
                    Log.e(TAG, m + "");

                    sendData[10] = 'S';
                    sendData[11] = 'F';
                    sendData[12] = 0x00;
                    sendData[13] = (byte) m;
                    sendData[14] = 0x00;

                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);

                    resultIntent.putExtra("wash", m);

                    break;
                case "dry":

                    /**
                     * 解析数据
                     */
                    m = Integer.parseInt(data, 16);
                    Log.e(TAG, m + "");

                    sendData[10] = 'S';
                    sendData[11] = 'P';
                    sendData[12] = 0x00;
                    sendData[13] = (byte) m;
                    sendData[14] = 0x00;

                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);

                    resultIntent.putExtra("dry", m);

                    break;
            }
        }
    }

    /**
     * 模式设置
     *
     * @param mode
     */
    @Override
    public void onClick2(String mode) {

        mModeFlag = mode;

        resultIntent.putExtra("mode", mode);
//        setResult(0x77, resultIntent);

        if (sendHandler != null) {
            Message msg = new Message();
            msg.what = 0x02;
            switch (mode) {
                case "快洗":
                    sendData[10] = 0x4C;
                    sendData[11] = 0x41;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;

                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                    break;
                case "慢洗":
                    sendData[10] = 0x4C;
                    sendData[11] = 0x42;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;

                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                    break;
                case "自定义洗":
                    sendData[10] = 0x4C;
                    sendData[11] = 0x43;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;

                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                    break;
            }
        }
    }


    /**
     * 发型设置
     *
     * @param data
     */
    @Override
    public void onClick3(String data) {

        /**
         * 设置全局变量：hair的类型
         */
        mHairFlag = data;

        resultIntent.putExtra("hair", data);
        setResult(0x77, resultIntent);


        if (sendHandler != null) {
            Message msg = new Message();
            msg.what = 0x02;
            switch (data) {
                case "长发":
                    sendData[10] = 0x4C;
                    sendData[11] = 0x4C;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;

                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                    break;
                case "短发":
                    sendData[10] = 0x4C;
                    sendData[11] = 0x53;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;

                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                    break;
            }
        }
    }
}
