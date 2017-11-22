package com.example.secret.ai10.DeviceModuel.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secret.ai10.ConfigModule.GosAirlinkChooseDeviceWorkWiFiActivity;
import com.example.secret.ai10.DeviceModuel.DeviceConfig;
import com.example.secret.ai10.R;
import com.example.secret.ai10.view.ThridSwitchSeekBar;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Secret on 2017/9/29.
 */

public class Bedroom extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener, SeekBar.OnSeekBarChangeListener {

    /*
     * ===========================================================
	 * 以下key值对应开发者在云端定义的数据点标识名
	 * ===========================================================
	 */
// 数据点"开灯"对应的标识名
    protected static final String KEY_LED_ONOFF = "LED_OnOff";
    // 数据点"开关窗"对应的标识名
    protected static final String KEY_WIN_ONOFF = "Win_OnOff";
    // 数据点"开关窗帘"对应的标识名
    protected static final String KEY_CURTAIN_ONOFF = "Curtain_OnOff";
    // 数据点"卧室空调"对应的标识名
    protected static final String KEY_ROOM_AC_ONOFF = "Room_AC_OnOff";
    // 数据点"卧室湿度控制"对应的标识名
    protected static final String KEY_ROOM_HUMC = "Room_humC";
    // 数据点"卧室空调温度"对应的标识名
    protected static final String KEY_ROOM_AIRC = "Room_AirC";

    /*
     * ===========================================================
     * 以下数值对应开发者在云端定义的可写数值型数据点增量值、数据点定义的分辨率、seekbar滚动条补偿值
     * _ADDITION:数据点增量值
     * _RATIO:数据点定义的分辨率
     * _OFFSET:seekbar滚动条补偿值
     * APP与设备定义的协议公式为：y（APP接收的值）=x（设备上报的值）* RATIO（分辨率）+ADDITION（增量值）
     * 由于安卓的原生seekbar无法设置最小值，因此代码中增加了一个补偿量OFFSET
     * 实际上公式中的：x（设备上报的值）=seekbar的值+补偿值
     * ===========================================================
     */
// 数据点"卧室空调温度"对应seekbar滚动条补偿值
    protected static final int ROOM_AIRC_OFFSET = 0;
    // 数据点"卧室空调温度"对应数据点增量值
    protected static final int ROOM_AIRC_ADDITION = 16;
    // 数据点"卧室空调温度"对应数据点定义的分辨率
    protected static final int ROOM_AIRC_RATIO = 1;

    /*
     * ===========================================================
     * 以下变量对应设备上报类型为布尔、数值、扩展数据点的数据存储
     * ===========================================================
     */
    // 数据点"开灯"对应的存储数据
    protected static boolean data_LED_OnOff;
    // 数据点"开关窗"对应的存储数据
    protected static boolean data_Win_OnOff;
    // 数据点"开关窗帘"对应的存储数据
    protected static boolean data_Curtain_OnOff;
    // 数据点"卧室空调"对应的存储数据
    protected static boolean data_Room_AC_OnOff;
    // 数据点"卧室湿度控制"对应的存储数据
    protected static int data_Room_humC;
    // 数据点"卧室空调温度"对应的存储数据
    protected static int data_Room_AirC;

    private GizWifiDevice mDevice;

    private Switch sw_bool_LED_OnOff;
    private Switch sw_bool_Win_OnOff;
    private Switch sw_bool_Curtain_OnOff;
    private Switch sw_bool_Air_OnOff;
    private ThridSwitchSeekBar sb_Room_Hum;
    private SeekBar sb_Room_AirC;
    private ScrollView llNoDevice;
    private TextView tvTitle,tv_data_Room_AirC;
    private enum handler_key {

        /**
         * 更新界面
         */
        UPDATE_UI,

        DISCONNECT,
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            if (isDeviceCanBeControlled()) {
//                progressDialog.cancel();
            } else {
                toastDeviceNoReadyAndExit();
            }
        }

    };

    /**
     * The handler.
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler_key key = handler_key.values()[msg.what];
            switch (key) {
                case UPDATE_UI:
                    updateUI();
                    break;
                case DISCONNECT:
                    toastDeviceDisconnectAndExit();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bedroom, container, false);
        initView(view);
        initDevice();


        return view;
    }

    private void initView(View view) {

        sw_bool_LED_OnOff = (Switch) view.findViewById(R.id.sw_bool_LED_OnOff);
        sw_bool_Win_OnOff = (Switch) view.findViewById(R.id.sw_bool_Win_OnOff);
        sw_bool_Curtain_OnOff = (Switch) view.findViewById(R.id.sw_bool_Curtain_OnOff);
        tv_data_Room_AirC = (TextView) view.findViewById(R.id.tv_data_Room_AirC);

        sw_bool_Air_OnOff = (Switch) view.findViewById(R.id.sw_bool_AC_OnOff);
        sb_Room_AirC = (SeekBar) view.findViewById(R.id.sb_data_Room_AirC);
        sb_Room_Hum = (ThridSwitchSeekBar) view.findViewById(R.id.sb_Room_Hum);

        sw_bool_LED_OnOff.setOnClickListener(this);
        sw_bool_Win_OnOff.setOnClickListener(this);
        sw_bool_Curtain_OnOff.setOnClickListener(this);
        sw_bool_Air_OnOff.setOnClickListener(this);

        sb_Room_AirC.setOnSeekBarChangeListener(this);
        //没有设备的时候
        llNoDevice = (ScrollView) view.findViewById(R.id.llNoDevice);
        Button btnNoDevice = (Button) view.findViewById(R.id.btnNoDevice);
        btnNoDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), GosAirlinkChooseDeviceWorkWiFiActivity.class);
                startActivity(intent1);
            }
        });

        //湿度控制
        sb_Room_Hum.setSeekTouchListenr(new ThridSwitchSeekBar.SeekTouchListener() {
            @Override
            public void touchTop(SeekBar seekBar) {
                if (data_Room_humC!=1){
                    seekBar.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                    Toast.makeText(getContext(),"开启加湿模式",Toast.LENGTH_SHORT).show();
                    data_Room_humC = 1;
                    sendCommand(KEY_ROOM_HUMC,sb_Room_Hum);
                }


            }

            @Override
            public void touchMiddle(SeekBar seekBar) {
                if (data_Room_humC!=0){
                    seekBar.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_gray_thumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.gray_track));
                    Toast.makeText(getContext(),"关闭",Toast.LENGTH_SHORT).show();
                    data_Room_humC = 0;
                    sendCommand(KEY_ROOM_HUMC,sb_Room_Hum);
                }

            }

            @Override
            public void touchEnd(SeekBar seekBar) {
                if (data_Room_humC!=2){
                    seekBar.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                    Toast.makeText(getContext(),"开启抽湿模式",Toast.LENGTH_SHORT).show();
                    data_Room_humC = 2;
                    sendCommand(KEY_ROOM_HUMC,sb_Room_Hum);
                }

            }
        });

        tvTitle = (TextView) view.findViewById(R.id.tvPlace);
        tvTitle.setText("卧室");
    }

    private void initDevice() {
        mDevice = getArguments().getParcelable("device");
        mDevice.setSubscribe(true);
        mDevice.setListener(gizWifiDeviceListener);
        Log.i("Apptest==", mDevice.getDid());
    }


    @Override
    public void onResume() {
        super.onResume();

        getStatusOfDevice();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
        // 退出页面，取消设备订阅
        if (mDevice!=null){
            mDevice.setSubscribe(false);
            mDevice.setListener(null);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sw_bool_LED_OnOff:
                sendCommand(KEY_LED_ONOFF, sw_bool_LED_OnOff.isChecked());
                break;
            case R.id.sw_bool_Win_OnOff:
                sendCommand(KEY_WIN_ONOFF, sw_bool_Win_OnOff.isChecked());
                break;
            case R.id.sw_bool_Curtain_OnOff:
                sendCommand(KEY_CURTAIN_ONOFF, sw_bool_Curtain_OnOff.isChecked());
                break;
            case R.id.sw_bool_AC_OnOff:
                sendCommand(KEY_ROOM_AC_ONOFF, sw_bool_Air_OnOff.isChecked());
                break;

            default:
                break;
        }
    }

    /*
     * ========================================================================
     * EditText 点击键盘“完成”按钮方法
     * ========================================================================
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        switch (v.getId()) {
            default:
                break;
        }
        hideKeyBoard();
        return false;

    }

    protected void hideKeyBoard() {
        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /*
     * ========================================================================
	 * seekbar 回调方法重写
	 * ========================================================================
	 */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {

            case R.id.sb_data_Room_AirC:
                tv_data_Room_AirC.setText(formatValue((progress + ROOM_AIRC_OFFSET) * ROOM_AIRC_RATIO + ROOM_AIRC_ADDITION, 1));
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sb_data_Room_AirC:
                sendCommand(KEY_ROOM_AIRC, (seekBar.getProgress() + ROOM_AIRC_OFFSET ) * ROOM_AIRC_RATIO + ROOM_AIRC_ADDITION);
                break;
            default:
                break;
        }
    }

    /**
     * Description:根据保存的的数据点的值来更新UI
     */
    protected void updateUI() {

        sw_bool_LED_OnOff.setChecked(data_LED_OnOff);
        sw_bool_Win_OnOff.setChecked(data_Win_OnOff);
        sw_bool_Curtain_OnOff.setChecked(data_Curtain_OnOff);

        sw_bool_Air_OnOff.setChecked(data_Room_AC_OnOff);

        tv_data_Room_AirC.setText(data_Room_AirC+"");
        sb_Room_AirC.setProgress((int)((data_Room_AirC - ROOM_AIRC_ADDITION) / ROOM_AIRC_RATIO - ROOM_AIRC_OFFSET));

        switch (data_Room_humC){
            case 0:
                sb_Room_Hum.setProgress(17);
                sb_Room_Hum.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                sb_Room_Hum.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                break;
            case 1:
                sb_Room_Hum.setProgress(50);
                sb_Room_Hum.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_gray_thumb));
                sb_Room_Hum.setProgressDrawable(getResources().getDrawable(R.drawable.gray_track));
                break;
            case 2:
                sb_Room_Hum.setProgress(83);
                sb_Room_Hum.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                sb_Room_Hum.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                break;
        }
    }


    /**
     * Description:页面加载后弹出等待框，等待设备可被控制状态回调，如果一直不可被控，等待一段时间后自动退出界面
     */
    private void getStatusOfDevice() {
        System.out.println("========isControl " + isDeviceCanBeControlled());
        // 设备是否可控
        if (isDeviceCanBeControlled()) {
            // 可控则查询当前设备状态
            mDevice.getDeviceStatus();
        } else {
            // 显示等待栏
//            progressDialog.show();
            if (mDevice != null) {
                if (mDevice.isLAN()) {
                    // 小循环10s未连接上设备自动退出
                    mHandler.postDelayed(mRunnable, 10000);
                } else {
                    // 大循环20s未连接上设备自动退出
                    mHandler.postDelayed(mRunnable, 20000);
                }
            }
        }
    }

    /**
     * 发送指令,下发单个数据点的命令可以用这个方法
     * <p>
     * <h3>注意</h3>
     * <p>
     * 下发多个数据点命令不能用这个方法多次调用，一次性多次调用这个方法会导致模组无法正确接收消息，参考方法内注释。
     * </p>
     *
     * @param key   数据点对应的标识名
     * @param value 需要改变的值
     */
    private void sendCommand(String key, Object value) {
        if (value == null) {
            return;
        }
        int sn = 5;
        ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
        hashMap.put(key, value);
        // 同时下发多个数据点需要一次性在map中放置全部需要控制的key，value值
        // hashMap.put(key2, value2);
        // hashMap.put(key3, value3);
        mDevice.write(hashMap, sn);
        Log.i("liang", "下发命令：" + hashMap.toString());
    }

    private boolean isDeviceCanBeControlled() {
        if (mDevice != null) {
            return mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
        } else {
            return false;
        }
    }

    private void toastDeviceNoReadyAndExit() {
        Toast.makeText(getActivity(), "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
//        finish();
    }

    private void toastDeviceDisconnectAndExit() {
        Toast.makeText(getActivity(), "连接已断开", Toast.LENGTH_SHORT).show();
//        finish();
    }


    /**
     * Description:显示格式化数值，保留对应分辨率的小数个数，比如传入参数（20.3656，0.01），将返回20.37
     *
     * @param date  传入的数值
     * @param scale 保留多少位小数
     * @return
     */
    protected String formatValue(double date, Object scale) {
        if (scale instanceof Double) {
            DecimalFormat df = new DecimalFormat(scale.toString());
            return df.format(date);
        }
        return Math.round(date) + "";
    }


    GizWifiDeviceListener gizWifiDeviceListener = new GizWifiDeviceListener() {

        /** 用于设备订阅 */
        public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {

            System.out.println("subscribe ==== " + isSubscribed);
        }

        ;

        /** 用于获取设备状态 */
        public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
                                   java.util.concurrent.ConcurrentHashMap<String, Object> dataMap, int sn) {
            Log.i("liang====", "接收到数据");

            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS && dataMap.get("data") != null) {
                getDataFromReceiveDataMap(dataMap);
                mHandler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
            }
        }

        ;

//        /** 用于设备硬件信息 */
//        public void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device,
//                                       java.util.concurrent.ConcurrentHashMap<String, String> hardwareInfo) {
//
//        };

//        /** 用于修改设备信息 */
//        public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
//
//        };

        /** 用于设备状态变化 */
        public void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
            if (netStatus == GizWifiDeviceNetStatus.GizDeviceControlled) {
                mHandler.removeCallbacks(mRunnable);
//                progressDialog.cancel();
            } else {
                mHandler.sendEmptyMessage(handler_key.DISCONNECT.ordinal());
            }
        }

        ;

    };

    protected void getDataFromReceiveDataMap(ConcurrentHashMap<String, Object> dataMap) {
        // 已定义的设备数据点，有布尔、数值和枚举型数据

        if (dataMap.get("data") != null) {
            ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
            for (String dataKey : map.keySet()) {
                if (dataKey.equals(KEY_LED_ONOFF)) {
                    data_LED_OnOff = (Boolean) map.get(dataKey);
                }
                if (dataKey.equals(KEY_WIN_ONOFF)) {
                    data_Win_OnOff = (Boolean) map.get(dataKey);
                }
                if (dataKey.equals(KEY_CURTAIN_ONOFF)) {
                    data_Curtain_OnOff = (Boolean) map.get(dataKey);
                }
                if (dataKey.equals(KEY_ROOM_AC_ONOFF)) {
                    data_Room_AC_OnOff = (Boolean) map.get(dataKey);
                }
                if (dataKey.equals(KEY_ROOM_HUMC)) {
                    data_Room_humC = (Integer) map.get(dataKey);
                }
                if (dataKey.equals(KEY_ROOM_AIRC)) {

                    data_Room_AirC = (Integer) map.get(dataKey);
                }
            }
        }
    }
}
