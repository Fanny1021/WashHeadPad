package com.fanny.washheadpad.dialog;

//import android.app.AlertDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.fanny.washheadpad.R;
import com.fanny.washheadpad.util.SharePrefrenceUtil;
import com.fanny.washheadpad.util.SocketUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 身份证对话框
 */
public class SocketConDialog extends DialogFragment {

    private String TAG = "SocketConDialog";
    private ExecutorService mThreadPool;
    private Socket socket;
    private AlertDialog.Builder socket_con_builder;
    private View socket_con_view;
    private AutoCompleteTextView socket_con_ip;
    private EditText socket_con_port;
    private Button socket_con_connect;
    private Button socket_con_disconnect;
    private ProgressBar socket_con_socket;
    private String strIP;
    private String strPort;
    private LinearLayout ll_con;
    private LinearLayout ll_con_process;
    private String tag;
    private Activity activity;


    @Override  //适合对简单dialog进行处理，可以利用Dialog.Builder直接返回Dialog对象
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.e(TAG, "oncreateDialog");
        socket_con_builder = new AlertDialog.Builder(getActivity());
        socket_con_view = View.inflate(getActivity(), R.layout.dialog_socket_connect, null);

        /****************************************ll_con进入初始化连接界面*****************************************/
        ll_con = socket_con_view.findViewById(R.id.ll_con);

        /****************************************ll_con_process设备连接失败后，连接布局*****************************************/
        ll_con_process = socket_con_view.findViewById(R.id.ll_con_process);
        socket_con_ip = (AutoCompleteTextView) socket_con_view.findViewById(R.id.auto_tv_ip);
        // 获取搜索ip记录文件内容
        String history = (String) SharePrefrenceUtil.getData(getContext(), "ip_history", "");

        // 用逗号分割内容返回数组
        String[] history_arr = history.split(",");

        ArrayAdapter<String> arr_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, history_arr);

        // 保留前50条数据
        if (history_arr.length > 10) {
            String[] newArrays = new String[10];
            // 实现数组之间的复制
            System.arraycopy(history_arr, 0, newArrays, 0, 10);
            arr_adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, history_arr);
        }

        // 设置ip选择框的适配器
        socket_con_ip.setAdapter(arr_adapter);
        socket_con_port = (EditText) socket_con_view.findViewById(R.id.et_socket_port);
        socket_con_connect = (Button) socket_con_view.findViewById(R.id.btn_socket_connect);
        socket_con_disconnect = (Button) socket_con_view.findViewById(R.id.btn_socket_disconnect);
//        socket_con_socket = (ProgressBar) socket_con_view.findViewById(R.id.pb_socket);

        View titleView = View.inflate(getActivity(), R.layout.dia_title, null);
        socket_con_builder.setTitle("连接设备")
                .setCustomTitle(titleView)
                .setView(socket_con_view);

        return socket_con_builder.create();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        /**
         * tag标识：设备id
         */
        tag = getTag();
        mThreadPool = Executors.newCachedThreadPool();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.e(TAG, "onActivityCreated");
        /**
         * 进行网络连接／设备连接
         * 根据tag来连接绑定设备
         */

        if (tag.contains("01")) {
            processCon("192.168.1.120", "8899");
        }
//        if (tag.contains("02")) {
//            processCon("192.168.0.159", "8899");
//        }
//        if (tag.contains("03")) {
//            processCon("192.168.1.120", "8899");
//        }
        if (tag.contains("04")) {
            processCon("10.10.100.254", "8899");
        }
        /**
         * 连接上一次网络地址
         */
//        String strIPAD= (String) SharePrefrenceUtil.getData(getContext(),SharePrefrenceUtil.IP_DRESS,"");
//        String strPORT= (String) SharePrefrenceUtil.getData(getContext(),SharePrefrenceUtil.PORT_NUM,"");
//        processCon(strIPAD,strPORT);

        socket_con_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = socket_con_ip.getText().toString();
                //保存ip地址
                String[] arr = text.split(".");
                if (arr.length < 4) {
                    // 获取搜索框信息
                    String old_text = (String) SharePrefrenceUtil.getData(getContext(), "ip_history", "");
//                                            SharedPreferences mysp = getSharedPreferences("search_history", 0);
//                                            String old_text = mysp.getString("history", "暂时没有搜索记录");

                    // 利用StringBuilder.append新增内容，逗号便于读取内容时用逗号拆分开
                    StringBuilder builder = new StringBuilder(old_text);
                    builder.append(text + ",");

                    // 判断搜索内容是否已经存在于历史文件，已存在则不重复添加
                    if (!old_text.contains(text + ",")) {
//                                                SharedPreferences.Editor myeditor = mysp.edit();
//                                                myeditor.putString("history", builder.toString());
//                                                myeditor.commit();
                        SharePrefrenceUtil.saveParam(getContext(), "ip_history", builder.toString());
//                        Toast.makeText(getContext(), text + "添加成功", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getContext(), text + "已存在", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(getContext(), text + "添加失败", Toast.LENGTH_SHORT).show();
                }

                strIP = socket_con_ip.getText().toString();
                strPort = socket_con_port.getText().toString();

                /**
                 * 重新建立网络连接
                 * 清空线程池
                 */
                if (!mThreadPool.isShutdown()) {
                    mThreadPool.shutdown();
                }
                processCon(strIP, strPort);
            }
        });

        socket_con_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(socket==null || !socket.isConnected()){

                    callBack2.onClick(null);
                }
                dismiss();
            }
        });

    }

    /**
     * 连接socket方法
     *
     * @param ip
     * @param port
     */
    private void processCon(final String ip, final String port) {

        Log.e(TAG, "processCon");

        if (SocketUtil.socket != null) {
            /**
             * 清空连接的socket
             */
            try {
                SocketUtil.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Runnable runnable0 = new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, Integer.parseInt(port));
                    socket.setSoTimeout(200);
                    if (socket != null && socket.isConnected()) {
                        Log.e(TAG, "已连接");
                        SocketUtil.setSocket(socket);
                        SocketUtil.setConnectStaus(1);
//                        EventBus.getDefault().post("3");

                        /**
                         * 保存socket的ip和端口号
                         */
                        SharePrefrenceUtil.saveParam(getContext(), SharePrefrenceUtil.IP_DRESS, ip);
                        SharePrefrenceUtil.saveParam(getContext(), SharePrefrenceUtil.PORT_NUM, port);
                        /**
                         * 通知全局socket状态
                         */
                        Message message = new Message();
                        message.what = 3;
                        UIHandler.sendMessage(message);
                    }else {
                        Message message = new Message();
                        message.what = 4;
                        UIHandler.sendMessage(message);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e(TAG,e.getMessage().toString());
                    /**
                     * 通知全局socket状态
                     */
                    Message message = new Message();
                    message.what = 5;
                    UIHandler.sendMessage(message);
                }

            }
        };

        if(mThreadPool.isShutdown()){
            mThreadPool = Executors.newCachedThreadPool();
        }

        mThreadPool.execute(runnable0);


    }

    public interface CallBack2 {
        void onClick(Socket socket);
    }

    private CallBack2 callBack2;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        if (context instanceof CallBack2) {
            callBack2 = (CallBack2) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callBack2 = null;
        if (!mThreadPool.isShutdown()) {
            mThreadPool.shutdown();
        }
    }

    Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    Log.e(TAG,"3");
                    if (socket_con_builder != null) {
                        /**
                         * eventbus发送连接成功事件操作
                         */

//                        EventBus.getDefault().post("3");
                        /**
                         * 连接成功后，通知activity更新当前洗头机名称
                         */
                        callBack2.onClick(socket);
                        dismiss();
                    }
                    break;
                case 4:
                    Log.e(TAG,"4");
//                    Toast.makeText(getActivity().getBaseContext(), "socket连接失败", Toast.LENGTH_SHORT).show();
                    if (socket_con_builder != null) {
                        /**
                         * eventbus发送连接失败事件操作
                         */
//                        EventBus.getDefault().post("4");
//                        socket_con_dialog.setCanceledOnTouchOutside(true);
//                        socket_con_dialog.dismiss();
                        callBack2.onClick(socket);
                        ll_con_process.setVisibility(View.GONE);
                        ll_con.setVisibility(View.VISIBLE);
                    }
                    break;
                case 5:
                    Log.e(TAG,"5");
//                    Toast.makeText(getActivity().getBaseContext(), "socket连接异常", Toast.LENGTH_SHORT).show();
                    if (socket_con_builder != null) {
                        /**
                         * eventbus发送连接异常事件操作
                         */
//                        EventBus.getDefault().post("5");
//                        socket_con_dialog.setCanceledOnTouchOutside(true);
//                        socket_con_dialog.dismiss();
                        callBack2.onClick(socket);
                        ll_con_process.setVisibility(View.GONE);
                        ll_con.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

}
