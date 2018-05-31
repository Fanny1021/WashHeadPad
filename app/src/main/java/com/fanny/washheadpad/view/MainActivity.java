package com.fanny.washheadpad.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanny.washheadpad.R;
import com.fanny.washheadpad.dialog.CurrentStepDialog;
import com.fanny.washheadpad.dialog.LoadingDialog;
import com.fanny.washheadpad.dialog.SocketConDialog;
import com.fanny.washheadpad.dialog.StopDialog;
import com.fanny.washheadpad.util.IPAddressUtil;
import com.fanny.washheadpad.util.SocketUtil;
import com.fanny.washheadpad.util.ToastUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SocketConDialog.CallBack2, CurrentStepDialog.StepCallBack,
        StopDialog.StopCallBack {

    @BindView(R.id.drawer_user_photo)
    ImageView drawerUserPhoto;
    @BindView(R.id.btn_menu_login)
    Button btnMenuLogin;
    @BindView(R.id.drawer_unlogin)
    LinearLayout drawerUnlogin;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.btn_menu_logout)
    Button btnMenuLogout;
    @BindView(R.id.drawer_user_msg)
    LinearLayout drawerUserMsg;
    @BindView(R.id.ll_wash)
    RelativeLayout llWash;
    @BindView(R.id.ll_community)
    RelativeLayout llCommunity;
    @BindView(R.id.ll_question)
    RelativeLayout llQuestion;
    @BindView(R.id.ll_setup)
    RelativeLayout llSetup;
    @BindView(R.id.im_menu)
    ImageView imMenu;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.im_power)
    ImageView imPower;
    @BindView(R.id.im_setup)
    ImageView imSetup;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.btn2)
    Button btn2;
    @BindView(R.id.btn3)
    Button btn3;
    @BindView(R.id.btn4)
    Button btn4;
    @BindView(R.id.btn5)
    Button btn5;
    @BindView(R.id.btn6)
    Button btn6;
    @BindView(R.id.btn7)
    Button btn7;
    @BindView(R.id.iv_set_bg)
    ImageView ivSetBg;
    @BindView(R.id.tv_run_mode)
    TextView tvRunMode;
    @BindView(R.id.tv_run_time)
    TextView tvRunTime;
    @BindView(R.id.tv_run_temp)
    TextView tvRunTemp;
    @BindView(R.id.tv_run_warn)
    TextView tvRunWarn;
    @BindView(R.id.tv_machine_staus)
    TextView tvMachineStaus;
    @BindView(R.id.tv_run_timeSec)
    TextView tvRunTimeSec;
    @BindView(R.id.tv_liquid_time)
    TextView tvLiquidTime;
    @BindView(R.id.ll_liquid)
    LinearLayout llLiquid;
    @BindView(R.id.ll_other)
    LinearLayout llOther;

    //    private boolean isRun = false;
    private Timer timer;
    private boolean headFlag1 = false;// 头部检测标示。累积到3个标示都为true时才代表检测完毕
    private boolean headFlag2 = false;
    private boolean headFlag3 = false;
    //    private int runPro = -1;//  洗头机运行状态标示。-1代表初始值未连接  0代表头型检测完毕  1代表启动
    private int runStep = 0;// 手动操作洗头机步骤标示。1代表湿润 2代表加洗发液 3代表搓揉 4代表冲洗 5代表风干 6代表结束 7代表消毒
    //    private int runFlag = -1;// －1代表未开机  0代表洗头机开启但未运行(自检完毕)  1代表洗头机正在运行
    private int mRunFlag = -1;// 洗头机运行状态标示  －1代表未连接到洗头机的wifi   0代表洗头机连接上socket  1代表洗头机自检完毕  2代表洗头机头部检测完毕  3代表洗头机已开启启动（快洗／慢洗）
    private boolean ifRun = false;// 洗头机运行操作标示  true代表运行 false代表暂停

    private String HairMode = "";
    private String Hair = "";
    private int wetNum = 60;
    private int liquidNum = 60;
    private int kneadNum = 60;
    private int washNum = 60;
    private int dryNum = 60;
    int mMin = 0;
    int mSec = 0;
    boolean ifOver = false;

    private void reset() {
        /**
         * 初始化数据
         */
        initSocketData();
        /**
         * 初始化flag标示
         */
        headFlag1 = false;
        headFlag2 = false;
        headFlag3 = false;
//        runFlag = -1;
//        runPro = -1;
        runStep = 0;
        mRunFlag = -1;
        ifRun = false;
//        HairMode="";//洗发模式重置
        mMin = 0;
        mSec = 0;
        ifOver = false;
    }


    private String TAG = "MainActivity";
    private ExecutorService mThreadPool;
    private Socket socket;
    private Handler sendHandler;
    private OutputStream out;
    private InputStream in;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mThreadPool = Executors.newCachedThreadPool();

        initSocketData();

//        setBtnUnUseable();
//        imageView.setOnClickListener(null);
//
//        imMenu.setOnClickListener(this);
//        imPower.setOnClickListener(this);
//        imSetup.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
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

    /**
     * 更新ui界面的handler
     */
    Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    /**
                     * 洗头机自检loading
                     */
                    Log.e(TAG, "自检ing");
                    loadingDialog = new LoadingDialog();
                    loadingDialog.setCancelable(false);
                    loadingDialog.show(getSupportFragmentManager(), "开机自检");
                    break;

                case 0x02:
                    String msgData = (String) msg.obj;
                    String msg11 = msgData.substring(30, 32);
                    String msg12 = msgData.substring(33, 35);

                    String msg13 = msgData.substring(36, 38);
                    String msg14 = msgData.substring(39, 41);
                    String msg15 = msgData.substring(42, 44);
                    /**
                     * 接受洗头机数据协议：
                     * 11字节   12字节
                     * 43        57        等待开机自检
                     * 43        54        开机自检正常
                     *
                     * 5A        54        左部头型检测完毕
                     * 5A        45        左部头型检测未完毕
                     * 59        54        右部头型检测完毕
                     * 59        45        右部头型检测未完毕
                     * 42        54        后部头型检测完毕
                     * 42        45        后部头型检测未完毕
                     *
                     * 57        54        水箱实时水温
                     *
                     * 57        41        水温超限报警
                     * 57        45        水温传感器异常
                     * 41        53        洗发液过低报警
                     * 41        44        消毒液过低报警
                     * 41        57        水箱水位过低报警
                     *
                     * 41        46        洗头结束
                     *
                     * 52        57        润湿剩余时间
                     * 52        53        加洗发液剩余次数
                     * 52        4D        搓揉剩余时间
                     * 52        46        冲洗剩余时间
                     * 52        50        风干剩余时间
                     *
                     */

                    switch (msg11) {
                        case "43":

                            if (msg12.equals("57")) {
                                /**
                                 * 等待洗头机开机自检
                                 */
                                reset();
                                Log.e(TAG, "等待开机自检");
                                /**
                                 * 洗头机自检loading
                                 */
                                Log.e(TAG, "自检ing");
//                                if (loadingDialog != null) {
//                                    loadingDialog.dismiss();
//                                }
//                                loadingDialog = new LoadingDialog();
//                                loadingDialog.setCancelable(false);
//                                loadingDialog.show(getSupportFragmentManager(), "等待开机自检");

                            }
                            if (msg12.equals("54")) {
                                /**
                                 * 洗头机自检完毕
                                 */
                                Log.e(TAG, "自检完毕");

                                mRunFlag = 1;

                                if (loadingDialog != null) {
                                    loadingDialog.dismiss();
                                    tvMachineStaus.setText("开机自检完毕");
                                }
                            }

                            break;
                        case "5A":
                            if (msg12.equals("54")) {
                                Log.e(TAG, "左头部检测完毕");
                                headFlag1 = true;
                                tvMachineStaus.setText("左头部检测完毕");
                            }
                            if (msg12.equals("45")) {
                                Log.e(TAG, "左头部检测未完毕");
                                headFlag1 = false;
                                tvMachineStaus.setText("左头部检测异常");
                            }
                            if (headFlag1 == true && headFlag2 == true && headFlag3 == true && loadingDialog != null) {
                                mRunFlag = 2;
//                                imageView.setImageResource(R.mipmap.icon_start_l);
                                tvMachineStaus.setText("头部检测完毕");
                                loadingDialog.dismiss();
                            }
                            break;
                        case "59":
                            if (msg12.equals("54")) {
                                Log.e(TAG, "右头部检测完毕");
                                headFlag2 = true;
                                tvMachineStaus.setText("右头部检测完毕");
                            }
                            if (msg12.equals("45")) {
                                Log.e(TAG, "右头部检测未完毕");
                                headFlag2 = false;
                                tvMachineStaus.setText("右头部检测异常");
                            }
                            if (headFlag1 == true && headFlag2 == true && headFlag3 == true && loadingDialog != null) {
                                mRunFlag = 2;
//                                imageView.setImageResource(R.mipmap.icon_start_l);
                                tvMachineStaus.setText("头部检测完毕");
                                loadingDialog.dismiss();
                            }
                            break;
                        case "42":
                            if (msg12.equals("54")) {
                                Log.e(TAG, "后头部检测完毕");
                                headFlag3 = true;
                                tvMachineStaus.setText("后头部检测完毕");
                            }
                            if (msg12.equals("45")) {
                                Log.e(TAG, "后头部检测未完毕");
                                headFlag3 = false;
                                tvMachineStaus.setText("后头部检测异常");
                            }
                            if (headFlag1 == true && headFlag2 == true && headFlag3 == true && loadingDialog != null) {
                                mRunFlag = 2;
//                                imageView.setImageResource(R.mipmap.icon_start_l);
                                tvMachineStaus.setText("头部检测完毕");
                                loadingDialog.dismiss();
                            }
                            break;
                        case "57":
                            if (msg12.equals("54")) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(msg13.substring(1, 2)).append(msg14.substring(1, 2));
                                Log.e(TAG, "水温实时数据:" + sb.toString());
                                tvRunTemp.setText(sb.toString());

                            }
                            if (msg12.equals("41")) {
                                Log.e(TAG, "水温超限:" + msg13 + ":" + msg14 + ":" + msg15);
                                tvRunWarn.setText("水温超限");
                            }
                            if (msg12.equals("45")) {
                                Log.e(TAG, "水温传感:" + msg13 + ":" + msg14 + ":" + msg15);
                                tvRunWarn.setText("水温传感异常");
                            }
                            break;

                        case "41":
                            if (msg12.equals("53")) {
                                Log.e(TAG, "洗发液报警:" + msg13 + ":" + msg14 + ":" + msg15);
                                tvRunWarn.setText("洗发液过低");
                            }
                            if (msg12.equals("44")) {
                                Log.e(TAG, "消毒液报警:" + msg13 + ":" + msg14 + ":" + msg15);
                                tvRunWarn.setText("消毒液过低");
                            }
                            if (msg12.equals("57")) {
                                Log.e(TAG, "水位报警:" + msg13 + ":" + msg14 + ":" + msg15);
                                tvRunWarn.setText("水箱谁为过低");
                            }

                            /**
                             * 洗头机结束命令
                             */
                            if (msg12.equals("46")) {
                                Log.e(TAG, "洗头结束");

                                /**
                                 * 当前洗头机已完成，控制图片闪烁
                                 */
                                if (timer != null) {
                                    timer.cancel();
                                }
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.sendEmptyMessage(6);
                                    }
                                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个


                                //需要一个当前已结束的flag
                                /**
                                 * 结束之后，将reset数据，重新洗的话需要从头型检测开始
                                 */
//                                if (ifOver) {
                                reset();
                                ifOver = true;
                                mRunFlag = 1;//处于自检完毕状态
                                ifRun = true;//洗头机正在运行
//                                }
                            }
                            break;

                        case "52":
                            if (msg12.equals("57")) {

                                /**
                                 * 当前正在润湿，控制图片闪烁
                                 */
                                if (timer != null) {
                                    timer.cancel();
                                }
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.sendEmptyMessage(1);
                                    }
                                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个


                                llLiquid.setVisibility(View.GONE);
                                llOther.setVisibility(View.VISIBLE);

                                Log.e(TAG, "润湿剩余时间:" + msg13 + ":" + msg14 + ":" + msg15);

                                /**
                                 * 秒换算分钟单位
                                 */
                                if (wetNum >= 60) {
                                    mMin = wetNum / 60;
                                    mSec = (wetNum % 60);
                                } else {
                                    mMin = 0;
                                    mSec = wetNum;
                                }

                                tvRunTime.setText(mMin + "");
                                tvRunTimeSec.setText(mSec + "");

                                wetNum = wetNum - 10;
                                if (wetNum < 0) {
                                    wetNum = 0;
                                }

                            }
                            if (msg12.equals("53")) {

                                /**
                                 * 当前正在加洗发液，控制图片闪烁
                                 */
                                if (timer != null) {
                                    timer.cancel();
                                }
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.sendEmptyMessage(2);
                                    }
                                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个

                                llLiquid.setVisibility(View.VISIBLE);
                                llOther.setVisibility(View.GONE);

                                Log.e(TAG, "加洗发液剩余次数:" + msg13 + ":" + msg14 + ":" + msg15);
                                tvLiquidTime.setText((liquidNum - 1) + "");
                                liquidNum = liquidNum - 1;
                                if (liquidNum < 1) {
                                    liquidNum = 1;
                                }
                            }
                            if (msg12.equals("4D")) {

                                /**
                                 * 当前正在搓揉，控制图片闪烁
                                 */
                                if (timer != null) {
                                    timer.cancel();
                                }
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.sendEmptyMessage(3);
                                    }
                                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个


                                llLiquid.setVisibility(View.GONE);
                                llOther.setVisibility(View.VISIBLE);

                                Log.e(TAG, "搓揉剩余时间:" + msg13 + ":" + msg14 + ":" + msg15);
                                /**
                                 * 秒换算分钟单位
                                 */
                                if (kneadNum >= 60) {
                                    mMin = kneadNum / 60;
                                    mSec = (kneadNum % 60);
                                } else {
                                    mMin = 0;
                                    mSec = kneadNum;
                                }

                                tvRunTime.setText(mMin + "");
                                tvRunTimeSec.setText(mSec + "");

                                kneadNum = kneadNum - 10;
                                if (kneadNum < 0) {
                                    kneadNum = 0;
                                }
                            }
                            if (msg12.equals("46")) {

                                /**
                                 * 当前正在冲洗，控制图片闪烁
                                 */
                                if (timer != null) {
                                    timer.cancel();
                                }
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.sendEmptyMessage(4);
                                    }
                                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个


                                llLiquid.setVisibility(View.GONE);
                                llOther.setVisibility(View.VISIBLE);

                                Log.e(TAG, "冲洗剩余时间:" + msg13 + ":" + msg14 + ":" + msg15);
                                if (washNum >= 60) {
                                    mMin = washNum / 60;
                                    mSec = (washNum % 60);
                                } else {
                                    mMin = 0;
                                    mSec = washNum;
                                }

                                tvRunTime.setText(mMin + "");
                                tvRunTimeSec.setText(mSec + "");

                                washNum = washNum - 10;
                                if (washNum < 0) {
                                    washNum = 0;
                                }
                            }
                            if (msg12.equals("50")) {

                                /**
                                 * 当前正在风干，控制图片闪烁
                                 */
                                if (timer != null) {
                                    timer.cancel();
                                }
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.sendEmptyMessage(5);
                                    }
                                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个


                                llLiquid.setVisibility(View.GONE);
                                llOther.setVisibility(View.VISIBLE);

                                Log.e(TAG, "风干剩余时间:" + msg13 + ":" + msg14 + ":" + msg15);
                                if (dryNum >= 60) {
                                    mMin = dryNum / 60;
                                    mSec = (dryNum % 60);
                                } else {
                                    mMin = 0;
                                    mSec = dryNum;
                                }

                                tvRunTime.setText(mMin + "");
                                tvRunTimeSec.setText(mSec + "");

                                dryNum = dryNum - 10;
                                if (dryNum < 0) {
                                    dryNum = 0;
                                }
                            }

                            break;


                        case "58":

                            /**
                             * 快洗启动成功服务器返回相同数据
                             */
                            if (msg12.equals("4B")) {
                                imageView.setImageResource(R.mipmap.icon_pause_red);
                                ifRun = true;
                                mRunFlag = 3;
                                tvMachineStaus.setText("快洗模式已开启");
                            }
//                            else {
//                                ifRun = false;
//                                mRunFlag = 2;
//                                tvMachineStaus.setText("快洗模式开启失败");
//                            }

                            /**
                             * 慢洗启动成功服务器返回相同数据
                             */
                            if (msg12.equals("4D")) {
                                imageView.setImageResource(R.mipmap.icon_pause_blue);
                                ifRun = true;
                                mRunFlag = 3;
                                tvMachineStaus.setText("慢洗模式已开启");
                            }

                            break;


//                        case "4D":
//                            if (msg12.equals("4D")) {
//                                imageView.setImageResource(R.mipmap.icon_pause_blue);
//                                ifRun = true;
//                                mRunFlag = 3;
//                                tvMachineStaus.setText("慢洗模式已开启");
//                            }
////                            else {
////                                ifRun = false;
////                                mRunFlag = 2;
////                                tvMachineStaus.setText("慢洗模式开启失败");
////                            }
//                            break;


                    }

                    break;
            }
        }
    };


    @OnClick({R.id.im_menu, R.id.btn_menu_login, R.id.ll_wash, R.id.ll_community, R.id.ll_question, R.id.ll_setup,
            R.id.im_power, R.id.im_setup, R.id.imageView, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7})
    public void onViewClicked(View view) {
//    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_menu:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.btn_menu_login:
                /**
                 * 获取ip地址测试
                 *
                 */
                String ip = IPAddressUtil.getIp(getApplicationContext());
                Log.e(TAG, "ip:" + ip);
                break;

            case R.id.ll_wash:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.ll_community:
                break;

            case R.id.ll_question:
                break;

            case R.id.ll_setup:
                break;

            case R.id.im_power:
                /**
                 * 开机按钮
                 */
                if (mRunFlag == -1 || SocketUtil.socket == null || !SocketUtil.socket.isConnected()) {
                    reset();
                    SocketConDialog socketConDialog = new SocketConDialog();
                    socketConDialog.setCancelable(false);
                    socketConDialog.show(getSupportFragmentManager(), "04");
                } else if (mRunFlag == 0) {
                    /**
                     *  此时洗头机已连接，提示是否需要断开连接?
                     */
                    StopDialog stopDialog = new StopDialog();
                    stopDialog.setCancelable(false);
                    stopDialog.show(getSupportFragmentManager(), "poweroff");

                } else if (mRunFlag == 1 || mRunFlag == 2 || mRunFlag == 3) {
                    /**
                     * 此时洗头机已连接，提示是否需要急停？
                     */
                    StopDialog stopDialog = new StopDialog();
                    stopDialog.setCancelable(false);
                    stopDialog.show(getSupportFragmentManager(), "stop");

                }


                break;

            case R.id.im_setup:
                /**
                 * 此时洗头机已经自检完毕／已启动，此时已经不再可以进行设置
                 */
//                if (mRunFlag == 2 || mRunFlag == 3) {
//                    break;
//                }
                Intent intent = new Intent(MainActivity.this, SetActivity.class);
                startActivityForResult(intent, 0x77);
                break;


            case R.id.imageView:

                if (HairMode.equals("")) {
                    break;
                }
                /**
                 * 自定义／快洗／慢洗 启动按钮
                 * 需要检测头型
                 */
                if (mRunFlag == -1 || mRunFlag == 0) {
                    break;
                }

                if (mRunFlag == 1) { //此时已自检完毕，可进行第一步检测头部操作、

                    /**
                     * 取消仍有图片闪烁的情况
                     */
                    if (timer != null) {
                        timer.cancel();
                    }

                    Log.e(TAG, "mRunFlag:" + mRunFlag);

                    if (loadingDialog == null) {
                        loadingDialog = new LoadingDialog();
                    }
                    loadingDialog.setCancelable(false);
                    loadingDialog.show(getSupportFragmentManager(), "头部检测");

                    /**
                     * 发送头部检测命令
                     */
                    if (sendHandler != null) {
                        Message msg = new Message();
                        msg.what = 0x01;
                        sendData[10] = 0x48;
                        sendData[11] = 0x54;
                        sendData[12] = 0x00;
                        sendData[13] = 0x00;
                        sendData[14] = 0x00;
                        msg.obj = sendData;
                        sendHandler.sendMessage(msg);
                    } else {
                        Log.e(TAG, "sendhandler为空");
                    }


                }

                if (mRunFlag == 2) { //此时已头部检测完毕，可进行第二步启动洗头操作

                    Log.e(TAG, "mRunFlag:" + mRunFlag);

//                    String mode = (String) tvRunMode.getText();//洗头机当前模式

                    if (HairMode.contains("快洗")) {
                        /**
                         * 发送快洗启动命令
                         */
                        if (sendHandler != null) {
                            Message msg = new Message();
                            msg.what = 0x01;
                            sendData[10] = 0x58;
                            sendData[11] = 0x4B;
                            sendData[12] = 0x00;
                            sendData[13] = 0x00;
                            sendData[14] = 0x00;
                            msg.obj = sendData;
                            sendHandler.sendMessage(msg);
                        } else {
                            Log.e(TAG, "sendhandler为空");
                        }
                        imageView.setImageResource(R.mipmap.icon_pause_red);//红色代表"快洗"
//                        ifRun = true;
//                        mRunFlag = 3;
                    }

                    if (HairMode.contains("慢洗")) {
                        /**
                         * 发送慢洗启动命令
                         */
                        if (sendHandler != null) {
                            Message msg = new Message();
                            msg.what = 0x01;
                            sendData[10] = 0x58;
                            sendData[11] = 0x4D;
                            sendData[12] = 0x00;
                            sendData[13] = 0x00;
                            sendData[14] = 0x00;
                            msg.obj = sendData;
                            sendHandler.sendMessage(msg);
                        } else {
                            Log.e(TAG, "sendhandler为空");
                        }
                        imageView.setImageResource(R.mipmap.icon_pause_blue);//蓝色代表"慢洗"
//                        ifRun = true;
//                        mRunFlag = 3;
                    }

                    if (HairMode.contains("自定义")) {
                        imageView.setImageResource(R.mipmap.icon_pause_l);//绿色代表"手动洗"
                        ifRun = true;
//                        mRunFlag = 3;
                    }

                }

                if (mRunFlag == 3) { //洗头机已经启动

                    Log.e(TAG, "mRunFlag:" + mRunFlag);

//                    String mode = (String) tvRunMode.getText();//洗头机当前模式

                    if (HairMode.contains("快洗")) {
                        if (ifRun) {
//                            ifRun = !ifRun;
//                            imageView.setImageResource(R.mipmap.icon_start_red);

//                            /**
//                             * 暂停指令
//                             */
//                            if (sendHandler != null) {
//                                Message msg = new Message();
//                                msg.what = 0x01;
//                                sendData[10] = 0x50;
//                                sendData[11] = 0x53;
//                                sendData[12] = 0x00;
//                                sendData[13] = 0x00;
//                                sendData[14] = 0x00;
//                                msg.obj = sendData;
//                                sendHandler.sendMessage(msg);
//                            } else {
//                                Log.e(TAG, "sendhandler为空");
//                            }

                            if (timer != null) {
                                timer.cancel();
                            }
                            CurrentStepDialog dialog = new CurrentStepDialog();
                            dialog.setCancelable(false);
                            dialog.show(getSupportFragmentManager(), "暂停");

                        } else {
                            ifRun = !ifRun;
                            imageView.setImageResource(R.mipmap.icon_pause_red);
                        }
                    }

                    if (HairMode.contains("慢洗")) {
                        if (ifRun) {
//                            ifRun = !ifRun;
//                            imageView.setImageResource(R.mipmap.icon_start_blue);

                            /**
                             * 暂停指令
                             */
//                            if (sendHandler != null) {
//                                Message msg = new Message();
//                                msg.what = 0x01;
//                                sendData[10] = 0x50;
//                                sendData[11] = 0x53;
//                                sendData[12] = 0x00;
//                                sendData[13] = 0x00;
//                                sendData[14] = 0x00;
//                                msg.obj = sendData;
//                                sendHandler.sendMessage(msg);
//                            } else {
//                                Log.e(TAG, "sendhandler为空");
//                            }

                            if (timer != null) {
                                timer.cancel();
                            }
                            CurrentStepDialog dialog = new CurrentStepDialog();
                            dialog.setCancelable(false);
                            dialog.show(getSupportFragmentManager(), "暂停");

                        } else {
                            ifRun = !ifRun;
                            imageView.setImageResource(R.mipmap.icon_pause_blue);
                        }
                    }

                    if (HairMode.contains("自定义")) {
                        if (ifRun) {
//                            ifRun = !ifRun;
//                            imageView.setImageResource(R.mipmap.icon_start_l);

                            /**
                             * 暂停指令
                             */
//                            if (sendHandler != null) {
//                                Message msg = new Message();
//                                msg.what = 0x01;
//                                sendData[10] = 0x50;
//                                sendData[11] = 0x53;
//                                sendData[12] = 0x00;
//                                sendData[13] = 0x00;
//                                sendData[14] = 0x00;
//                                msg.obj = sendData;
//                                sendHandler.sendMessage(msg);
//                            } else {
//                                Log.e(TAG, "sendhandler为空");
//                            }

                            if (timer != null) {
                                timer.cancel();
                            }

                            CurrentStepDialog dialog = new CurrentStepDialog();
                            dialog.setCancelable(false);
                            dialog.show(getSupportFragmentManager(), "暂停");

                        } else {
                            ifRun = !ifRun;
                            imageView.setImageResource(R.mipmap.icon_pause_l);
                        }
                    }


                }
                break;

            case R.id.btn1:

                runStep = 1;//第1步  润湿

                if (HairMode.equals("") || HairMode.equals("快洗") || HairMode.equals("慢洗")) {
                    break;
                }
                if (ifRun == false) {
                    break;
                }

                if (ifRun == true && mRunFlag == 2) {
                    mRunFlag = 3;
                }

                ToastUtil.show(getBaseContext(), "01");

                if (timer != null) {
                    timer.cancel();
                }

                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x4D;
                    sendData[11] = 0x57;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }

                /**
                 * 更新剩余时间显示控件
                 */
                tvRunTime.setText("00");
                tvRunTimeSec.setText("00");

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(1);
                    }
                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个

                break;
            case R.id.btn2:

                runStep = 2;//第2步 洗发液

                if (HairMode.equals("") || HairMode.equals("快洗") || HairMode.equals("慢洗")) {
                    break;
                }
                if (ifRun == false) {
                    break;
                }
                ToastUtil.show(getBaseContext(), "02");

                if (timer != null) {
                    timer.cancel();
                }

                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x4D;
                    sendData[11] = 0x53;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }

                /**
                 * 更新剩余时间显示控件
                 */
                tvRunTime.setText("00");
                tvRunTimeSec.setText("00");


                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(2);
                    }
                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个

                break;
            case R.id.btn3:

                runStep = 3;//第3步 搓揉

                if (HairMode.equals("") || HairMode.equals("快洗") || HairMode.equals("慢洗")) {
                    break;
                }
                if (ifRun == false) {
                    break;
                }
                ToastUtil.show(getBaseContext(), "03");


                if (timer != null) {
                    timer.cancel();
                }

                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x4D;
                    sendData[11] = 0x4D;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }

                /**
                 * 更新剩余时间显示控件
                 */
                tvRunTime.setText("00");
                tvRunTimeSec.setText("00");


                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(3);
                    }
                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个
                break;
            case R.id.btn4:

                runStep = 4;//第4步 冲洗

                if (HairMode.equals("") || HairMode.equals("快洗") || HairMode.equals("慢洗")) {
                    break;
                }
                if (ifRun == false) {
                    break;
                }
                ToastUtil.show(getBaseContext(), "04");

                if (timer != null) {
                    timer.cancel();
                }

                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x4D;
                    sendData[11] = 0x46;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }

                /**
                 * 更新剩余时间显示控件
                 */
                tvRunTime.setText("00");
                tvRunTimeSec.setText("00");

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(4);
                    }
                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个
                break;
            case R.id.btn5:

                runStep = 5;//第5步 风干

                if (HairMode.equals("") || HairMode.equals("快洗") || HairMode.equals("慢洗")) {
                    break;
                }
                if (ifRun == false) {
                    break;
                }
                ToastUtil.show(getBaseContext(), "05");

                if (timer != null) {
                    timer.cancel();
                }

                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x4D;
                    sendData[11] = 0x50;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }

                /**
                 * 更新剩余时间显示控件
                 */
                tvRunTime.setText("00");
                tvRunTimeSec.setText("00");

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(5);
                    }
                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个
                break;
            case R.id.btn6:

                runStep = 6;//第6步 结束

                if (HairMode.equals("") || HairMode.equals("快洗") || HairMode.equals("慢洗")) {
                    break;
                }
                if (ifRun == false) {
                    break;
                }
                ToastUtil.show(getBaseContext(), "06");

                if (timer != null) {
                    timer.cancel();
                }

                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x4D;
                    sendData[11] = 0x45;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }

                /**
                 * 更新剩余时间显示控件
                 */
                tvRunTime.setText("00");
                tvRunTimeSec.setText("00");

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(6);
                    }
                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个


                break;

            case R.id.btn7:

                runStep = 7;//第7步 消毒


                if (HairMode.equals("") || HairMode.equals("快洗") || HairMode.equals("慢洗")) {
//                    break;
                }
                if (!ifOver) {
                    break;
                }

                if (ifRun == false) {
                    break;
                }
                ToastUtil.show(getBaseContext(), "07");

                if (timer != null) {
                    timer.cancel();
                }

                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x44;
                    sendData[11] = 0x53;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }

                /**
                 * 更新剩余时间显示控件
                 */
                tvRunTime.setText("00");
                tvRunTimeSec.setText("00");

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(7);
                    }
                }, 0, 100);//第二个参数是隔多少秒之后开始显示，第三个是隔多久显示下一个


                break;

            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

            /**
             * 全局变量HairMode洗发模式
             */
            HairMode = data.getStringExtra("mode");
            tvRunMode.setText(HairMode);
            switch (HairMode) {
                case "快洗":
                    imageView.setImageResource(R.mipmap.icon_start_red);
                    /**
                     * 全局变量洗发次数的设置
                     */
                    wetNum = data.getIntExtra("wet", 1) * 60;
                    liquidNum = data.getIntExtra("liquid", 1);
                    kneadNum = data.getIntExtra("knead", 1) * 60;
                    washNum = data.getIntExtra("wash", 1) * 60;
                    dryNum = data.getIntExtra("dry", 1) * 60;
                    break;
                case "慢洗":
                    imageView.setImageResource(R.mipmap.icon_start_blue);
                    /**
                     * 全局变量洗发次数的设置
                     */
                    wetNum = data.getIntExtra("wet", 2) * 60;
                    liquidNum = data.getIntExtra("liquid", 2);
                    kneadNum = data.getIntExtra("knead", 2) * 60;
                    washNum = data.getIntExtra("wash", 2) * 60;
                    dryNum = data.getIntExtra("dry", 2) * 60;
                    break;
                case "自定义":
                    imageView.setImageResource(R.mipmap.icon_start_l);
                    /**
                     * 全局变量洗发次数的设置
                     */
                    wetNum = data.getIntExtra("wet", 1) * 60;
                    liquidNum = data.getIntExtra("liquid", 1);
                    kneadNum = data.getIntExtra("knead", 1) * 60;
                    washNum = data.getIntExtra("wash", 1) * 60;
                    dryNum = data.getIntExtra("dry", 1) * 60;
                    break;
            }

            /**
             * 全局变量hair长短发
             */
            Hair = data.getStringExtra("hair");


            Log.e(TAG, HairMode + "");
            Log.e(TAG, Hair + "");
            Log.e(TAG, wetNum + "");
            Log.e(TAG, liquidNum + "");
            Log.e(TAG, kneadNum + "");
            Log.e(TAG, washNum + "");
            Log.e(TAG, dryNum + "");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {

            timer.cancel();
        }
    }


    private boolean receiveMsg = true;


    /**
     * 实现socketDialog对话框的onclick
     *
     * @param socket
     */
    @Override
    public void onClick(Socket socket) {

        /**
         * socket已连接
         */
        if (socket != null && socket.isConnected()) {

            tvMachineStaus.setText("机器已连接");
            SocketUtil.setSocket(socket);
            //洗头机已经连接上socket
            mRunFlag = 0;

            /**
             * 向服务器发送的线程
             */
            Runnable oneRun = new Runnable() {
                @Override
                public void run() {
                    if (SocketUtil.socket != null && SocketUtil.socket.isConnected()) {
                        out = SocketUtil.getOutputStream();

                        /**
                         * 发送开机自检命令
                         */
                        initSocketData();//不管是否连接都是重置初始化数据,此时是字节是自检命令
                        SocketUtil.SendDataByte(out, sendData);
                        /**
                         * 自检dialog
                         */
                        Message msg = new Message();
                        msg.what = 0x01;
                        UIHandler.sendMessage(msg);

                        Looper.prepare();
                        sendHandler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                switch (msg.what) {
                                    case 0x01:
                                        SocketUtil.SendDataByte(out, (byte[]) msg.obj);
                                        break;
                                    case 0x02:

                                        break;
                                }
                            }
                        };
                        Looper.loop();
                    } else {
                        Log.e(TAG, "socket NOT CON");
                    }
                }
            };

            /**
             * 接受服务器返回数据的线程，监听数据
             */
            Runnable twoRun = new Runnable() {
                @Override
                public void run() {

                    if (SocketUtil.socket != null && SocketUtil.socket.isConnected()) {

                        in = SocketUtil.getInputStream();

                        while (receiveMsg) {
                            String RecMsg = "服务器返回消息";
                            if (in != null) {
                                try {
                                    byte buffer[] = new byte[23];
                                    int temp = 0;
                                    while ((temp = in.read(buffer)) != -1) {
                                        Log.e(TAG, "temp:" + temp + " ");

                                        /**
                                         * 接受数据转换为16进制字符
                                         */
                                        String s = BinaryToHexString(buffer);
                                        Log.e(TAG, "RecMsgHex:" + s + " ");

                                        /**
                                         * 接收到的原始字节数据
                                         */
                                        RecMsg = new String(buffer, 0, temp);
                                        Log.e(TAG, "RecMsg:" + RecMsg + " ");

                                        /**
                                         * 将服务器发送来的数据event下去
                                         */
                                        /**
                                         * 根据接受数据更新ui界面
                                         */
                                        Message message = new Message();
                                        message.what = 0x02;
                                        message.obj = s;
                                        UIHandler.sendMessage(message);
//                                        sendHandler.sendMessage(message);
//                                        EventBus.getDefault().post(RecMsg);
                                    }


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            } else {

                            }
                        }
                    }

                }
            };

            if (mThreadPool.isShutdown()) {
                mThreadPool = Executors.newCachedThreadPool();
            }

            mThreadPool.execute(twoRun);
            mThreadPool.execute(oneRun);

        } else {
            /**
             * socket未连接
             */
            tvMachineStaus.setText("机器未连接");
            //洗头机未连接socket
            mRunFlag = -1;
        }
    }

    private char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);

        return cb.array();
    }

    //将字节数组转换为16进制字符串
    public static String BinaryToHexString(byte[] bytes) {
        String hexStr = "0123456789ABCDEF";
        String result = "";
        String hex = "";
        for (byte b : bytes) {
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            result += hex + " ";
        }
        return result;
    }

    /**
     * 控制图片闪烁handler
     */
    Handler handler = new Handler() {
        int i = 0;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    i++;
                    PicRun(i % 2, 1);
                    break;
                case 2:
                    i++;
                    PicRun(i % 2, 2);
                    break;
                case 3:
                    i++;
                    PicRun(i % 2, 3);
                    break;
                case 4:
                    i++;
                    PicRun(i % 2, 4);
                    break;
                case 5:
                    i++;
                    PicRun(i % 2, 5);
                    break;
                case 6:
                    i++;
                    PicRun(i % 2, 6);
                    break;
                case 7:
                    i++;
                    PicRun(i % 2, 7);
                    break;
            }
        }
    };

    int[] mipRes = {R.mipmap.icon_step_bg, R.mipmap.icon_step_one, R.mipmap.icon_step_two
            , R.mipmap.icon_step_three, R.mipmap.icon_step_four, R.mipmap.icon_step_five
            , R.mipmap.icon_step_six, R.mipmap.icon_step_seven};

    private void PicRun(int i, int j) {
        switch (i) {
            case 0:
                ivSetBg.setImageResource(mipRes[0]);
                break;
            case 1:
                ivSetBg.setImageResource(mipRes[j]);
                break;
        }
    }

    /**
     * 实现currentstepdialog对话框（暂停／继续／下一步)的点击事件
     *
     * @param str
     */
    @Override
    public void onClick(String str) {
        switch (str) {
            case "pause":

                ifRun = false;
//                changeImage();

                /**
                 * 发送暂停指令
                 */
                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x50;
                    sendData[11] = 0x53;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }
                break;

            case "continue":

//                changeImage();
                ifRun = true;

                /**
                 * 发送继续指令
                 */
                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x50;
                    sendData[11] = 0x53;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }
                break;
            case "next":

//                changeImage();
                ifRun = true;

                /**
                 * 发送进行下一步操作指令
                 */
                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x45;
                    sendData[11] = 0x54;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }

                /**
                 * 更新剩余时间显示控件
                 */
                tvRunTime.setText("00");
                tvRunTimeSec.setText("00");

                break;
            case "poweroff":
                Log.e(TAG, "poweroff");
                break;
            case "stop":
                Log.e(TAG, "stop");
                if (sendHandler != null) {
                    Message msg = new Message();
                    msg.what = 0x01;
                    sendData[10] = 0x45;
                    sendData[11] = 0x53;
                    sendData[12] = 0x00;
                    sendData[13] = 0x00;
                    sendData[14] = 0x00;
                    msg.obj = sendData;
                    sendHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, "sendhandler为空");
                }
                break;
        }

    }

    private void changeImage() {
//        String mode = (String) tvRunMode.getText();//洗头机当前模式

        if (HairMode.contains("快洗")) {
            if (ifRun) {
                imageView.setImageResource(R.mipmap.icon_start_red);
            } else {
                imageView.setImageResource(R.mipmap.icon_pause_red);
            }
        }

        if (HairMode.contains("慢洗")) {
            if (ifRun) {
                imageView.setImageResource(R.mipmap.icon_start_blue);
            } else {
                imageView.setImageResource(R.mipmap.icon_pause_blue);
            }
        }

        if (HairMode.contains("自定义")) {
            if (ifRun) {
                imageView.setImageResource(R.mipmap.icon_start_l);
            } else {
                imageView.setImageResource(R.mipmap.icon_pause_l);
            }
        }
    }
}
