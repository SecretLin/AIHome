package com.example.secret.ai10.DeviceModuel.fragment;


import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
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

public class LivingRoom extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener, SeekBar.OnSeekBarChangeListener {

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
    // 数据点"大厅空调"对应的标识名
    protected static final String KEY_HALL_AC_ONOFF = "Hall_AC_OnOff";
    // 数据点"大厅湿度控制"对应的标识名
    protected static final String KEY_HALL_HUMC = "Hall_humC";
    // 数据点"大厅空调温度"对应的标识名
    protected static final String KEY_HALL_AIRC = "Hall_AirC";
    // 数据点"大厅是否有人"对应的标识名
    protected static final String KEY_HALL_HUMAN = "Hall_human";
    // 数据点"室内光照模式"对应的标识名
    protected static final String KEY_LED_MODE = "LED_Mode";
    // 数据点"大厅温度"对应的标识名
    protected static final String KEY_HALL_TEMP = "Hall_Temp";
    // 数据点"大厅湿度"对应的标识名
    protected static final String KEY_HALL_HUM = "Hall_Hum";
    // 数据点"红色值"对应的标识名
    protected static final String KEY_LED_R = "LED_R";
    // 数据点"绿色值"对应的标识名
    protected static final String KEY_LED_G = "LED_G";
    // 数据点"蓝色值"对应的标识名
    protected static final String KEY_LED_B = "LED_B";
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

    // 数据点"大厅空调温度"对应seekbar滚动条补偿值
    protected static final int HALL_AIRC_OFFSET = 0;
    // 数据点"大厅空调温度"对应数据点增量值
    protected static final int HALL_AIRC_ADDITION = 16;
    // 数据点"大厅空调温度"对应数据点定义的分辨率
    protected static final int HALL_AIRC_RATIO = 1;

    // 数据点"红色值"对应seekbar滚动条补偿值
    protected static final int LED_R_OFFSET = 0;
    // 数据点"红色值"对应数据点增量值
    protected static final int LED_R_ADDITION = 0;
    // 数据点"红色值"对应数据点定义的分辨率
    protected static final int LED_R_RATIO = 1;

    // 数据点"绿色值"对应seekbar滚动条补偿值
    protected static final int LED_G_OFFSET = 0;
    // 数据点"绿色值"对应数据点增量值
    protected static final int LED_G_ADDITION = 0;
    // 数据点"绿色值"对应数据点定义的分辨率
    protected static final int LED_G_RATIO = 1;

    // 数据点"蓝色值"对应seekbar滚动条补偿值
    protected static final int LED_B_OFFSET = 0;
    // 数据点"蓝色值"对应数据点增量值
    protected static final int LED_B_ADDITION = 0;
    // 数据点"蓝色值"对应数据点定义的分辨率
    protected static final int LED_B_RATIO = 1;
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
    // 数据点"大厅空调"对应的存储数据
    protected static boolean data_Hall_AC_OnOff;
    // 数据点"大厅湿度控制"对应的存储数据
    protected static int data_Hall_humC;
    // 数据点"大厅空调温度"对应的存储数据
    protected static int data_Hall_AirC;
    // 数据点"大厅湿度"对应的存储数据
    protected static int data_Hall_Hum;
    // 数据点"室内光照模式"对应的存储数据
    protected static int data_LED_Mode;
    // 数据点"红色值"对应的存储数据
    protected static int data_LED_R;
    // 数据点"绿色值"对应的存储数据
    protected static int data_LED_G;
    // 数据点"蓝色值"对应的存储数据
    protected static int data_LED_B;
    /*
     * ===========================================================
     * 以下key值对应设备硬件信息各明细的名称，用与回调中提取硬件信息字段。
     * ===========================================================
     */
    protected static final String WIFI_HARDVER_KEY = "wifiHardVersion";
    protected static final String WIFI_SOFTVER_KEY = "wifiSoftVersion";
    protected static final String MCU_HARDVER_KEY = "mcuHardVersion";
    protected static final String MCU_SOFTVER_KEY = "mcuSoftVersion";
    protected static final String WIFI_FIRMWAREID_KEY = "wifiFirmwareId";
    protected static final String WIFI_FIRMWAREVER_KEY = "wifiFirmwareVer";
    protected static final String PRODUCT_KEY = "productKey";

    private GizWifiDevice mDevice;

    private ProgressDialog progressDialog;

    private Switch sw_bool_LED_OnOff;
    private Switch sw_bool_Win_OnOff;
    private Switch sw_bool_Curtain_OnOff;
    private Switch sw_bool_Hall_AC_OnOff;
    private ThridSwitchSeekBar sb_Hall_Hum;
    private ThridSwitchSeekBar sb_Hall_LED_Mode;
    private SeekBar sb_data_Red,sb_data_Green,sb_data_Blue;
    private SeekBar sb_data_Hall_AirC;
    private TextView tv_data_Hall_AirC,tv_data_Red,tv_data_Green,tv_data_Blue;
    private ScrollView llNoDevice;
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
                progressDialog.dismiss();
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
        View view = inflater.inflate(R.layout.fragment_livingroom, container, false);
        initView(view);
        initDevice();


        return view;
    }

    private void initView(View view) {

        setProgressDialog();

        sw_bool_LED_OnOff = (Switch) view.findViewById(R.id.sw_bool_LED_OnOff);
        sw_bool_Win_OnOff = (Switch) view.findViewById(R.id.sw_bool_Win_OnOff);
        sw_bool_Curtain_OnOff = (Switch) view.findViewById(R.id.sw_bool_Curtain_OnOff);
        sw_bool_Hall_AC_OnOff = (Switch) view.findViewById(R.id.sw_bool_Hall_AC_OnOff);

        sb_Hall_Hum = (ThridSwitchSeekBar) view.findViewById(R.id.sb_Hall_Hum);
        sb_data_Hall_AirC = (SeekBar) view.findViewById(R.id.sb_data_Hall_AirC);
        sb_Hall_LED_Mode = (ThridSwitchSeekBar) view.findViewById(R.id.sb_LED_mode);
        sb_data_Red = (SeekBar) view.findViewById(R.id.sb_data_Red);
        sb_data_Green = (SeekBar) view.findViewById(R.id.sb_data_Green);
        sb_data_Blue = (SeekBar) view.findViewById(R.id.sb_data_Blue);

        tv_data_Hall_AirC = (TextView) view.findViewById(R.id.tv_data_Hall_AirC);
        tv_data_Red = (TextView) view.findViewById(R.id.tv_data_Red);
        tv_data_Green = (TextView) view.findViewById(R.id.tv_data_Green);
        tv_data_Blue = (TextView) view.findViewById(R.id.tv_data_Blue);

        sw_bool_LED_OnOff.setOnClickListener(this);
        sw_bool_Win_OnOff.setOnClickListener(this);
        sw_bool_Curtain_OnOff.setOnClickListener(this);

        //湿度控制
        sb_Hall_Hum.setSeekTouchListenr(new ThridSwitchSeekBar.SeekTouchListener() {
            @Override
            public void touchTop(SeekBar seekBar) {
                if (data_Hall_humC!=1){
                    seekBar.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                    Toast.makeText(getContext(),"开启加湿模式",Toast.LENGTH_SHORT).show();
                    data_Hall_humC = 1;
                    sendCommand(KEY_HALL_HUM,data_Hall_Hum);
                }


            }

            @Override
            public void touchMiddle(SeekBar seekBar) {
                if (data_Hall_humC!=0){
                    seekBar.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_gray_thumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.gray_track));
                    Toast.makeText(getContext(),"关闭",Toast.LENGTH_SHORT).show();
                    data_Hall_humC = 0;
                    sendCommand(KEY_HALL_HUM,data_Hall_Hum);
                }

            }

            @Override
            public void touchEnd(SeekBar seekBar) {
                if (data_Hall_humC!=2){
                    seekBar.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                    Toast.makeText(getContext(),"开启抽湿模式",Toast.LENGTH_SHORT).show();
                    data_Hall_humC = 2;
                    sendCommand(KEY_HALL_HUM,data_Hall_Hum);
                }

            }
        });

        //光照模式控制
        sb_Hall_LED_Mode.setSeekTouchListenr(new ThridSwitchSeekBar.SeekTouchListener() {
            @Override
            public void touchTop(SeekBar seekBar) {
                if (data_LED_Mode!=1){
                    seekBar.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                    Toast.makeText(getContext(),"光亮",Toast.LENGTH_SHORT).show();
                    data_LED_Mode = 1;
                    sendCommand(KEY_LED_MODE,data_LED_Mode);
                }


            }

            @Override
            public void touchMiddle(SeekBar seekBar) {
                if (data_LED_Mode!=0){
                    seekBar.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_gray_thumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.gray_track));
                    Toast.makeText(getContext(),"柔和",Toast.LENGTH_SHORT).show();
                    data_LED_Mode = 0;
                    sendCommand(KEY_LED_MODE,data_LED_Mode);
                }

            }

            @Override
            public void touchEnd(SeekBar seekBar) {
                if (data_LED_Mode!=2){
                    seekBar.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                    seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                    Toast.makeText(getContext(),"幽暗",Toast.LENGTH_SHORT).show();
                    data_LED_Mode = 2;
                    sendCommand(KEY_LED_MODE,data_LED_Mode);
                }

            }
        });

        sb_data_Hall_AirC.setOnSeekBarChangeListener(this);
        sb_data_Red.setOnSeekBarChangeListener(this);
        sb_data_Green.setOnSeekBarChangeListener(this);
        sb_data_Blue.setOnSeekBarChangeListener(this);

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
            case R.id.sw_bool_Hall_AC_OnOff:
                sendCommand(KEY_HALL_AC_ONOFF, sw_bool_Hall_AC_OnOff.isChecked());
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

            case R.id.sb_data_Hall_AirC:
                tv_data_Hall_AirC.setText(formatValue((progress + HALL_AIRC_OFFSET) * HALL_AIRC_RATIO + HALL_AIRC_ADDITION, 1));
                break;
            case R.id.sb_data_Red:
                System.out.println("=========red:"+progress);
                tv_data_Red.setText(formatValue((progress + LED_R_OFFSET) * LED_R_RATIO + LED_R_ADDITION, 1));
                break;
            case R.id.sb_data_Green:
                tv_data_Green.setText(formatValue((progress + LED_G_OFFSET) * LED_G_RATIO + LED_G_ADDITION, 1));
                break;
            case R.id.sb_data_Blue:
                tv_data_Blue.setText(formatValue((progress + LED_B_OFFSET) * LED_B_RATIO + LED_B_ADDITION, 1));
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
            case R.id.sb_data_Hall_AirC:
                sendCommand(KEY_HALL_AIRC, (seekBar.getProgress() + HALL_AIRC_OFFSET) * HALL_AIRC_RATIO + HALL_AIRC_ADDITION);
                break;
            case R.id.sb_data_Red:

                sendCommand(KEY_LED_R, (seekBar.getProgress() + LED_R_OFFSET ) * LED_R_RATIO + LED_R_ADDITION);
                break;
            case R.id.sb_data_Green:
                sendCommand(KEY_LED_G, (seekBar.getProgress() + LED_G_OFFSET ) * LED_G_RATIO + LED_G_ADDITION);
                break;
            case R.id.sb_data_Blue:
                sendCommand(KEY_LED_B, (seekBar.getProgress() + LED_B_OFFSET ) * LED_B_RATIO + LED_B_ADDITION);
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
        sw_bool_Hall_AC_OnOff.setChecked(data_Hall_AC_OnOff);

        tv_data_Hall_AirC.setText(data_Hall_AirC + "°c");
        sb_data_Hall_AirC.setProgress((int) ((data_Hall_AirC - HALL_AIRC_ADDITION) / HALL_AIRC_RATIO - HALL_AIRC_OFFSET));

        tv_data_Red.setText(data_LED_R+"");
        sb_data_Red.setProgress((int)((data_LED_R - LED_R_ADDITION) / LED_R_RATIO - LED_R_OFFSET));

        tv_data_Green.setText(data_LED_G+"");
        sb_data_Green.setProgress((int)((data_LED_G - LED_G_ADDITION) / LED_G_RATIO - LED_G_OFFSET));

        tv_data_Blue.setText(data_LED_B+"");
        sb_data_Blue.setProgress((int)((data_LED_B - LED_B_ADDITION) / LED_B_RATIO - LED_B_OFFSET));

        switch (data_Hall_humC){
            case 0:
                sb_Hall_Hum.setProgress(17);
                sb_Hall_Hum.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                sb_Hall_Hum.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                break;
            case 1:
                sb_Hall_Hum.setProgress(50);
                sb_Hall_Hum.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_gray_thumb));
                sb_Hall_Hum.setProgressDrawable(getResources().getDrawable(R.drawable.gray_track));
                break;
            case 2:
                sb_Hall_Hum.setProgress(83);
                sb_Hall_Hum.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                sb_Hall_Hum.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                break;
        }

        switch (data_LED_Mode){
            case 0:
                sb_Hall_LED_Mode.setProgress(17);
                sb_Hall_LED_Mode.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                sb_Hall_LED_Mode.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                break;
            case 1:
                sb_Hall_LED_Mode.setProgress(50);
                sb_Hall_LED_Mode.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_gray_thumb));
                sb_Hall_LED_Mode.setProgressDrawable(getResources().getDrawable(R.drawable.gray_track));
                break;
            case 2:
                sb_Hall_LED_Mode.setProgress(83);
                sb_Hall_LED_Mode.setThumb(getResources().getDrawable(R.drawable.thirdseekbar_blue_thumb));
                sb_Hall_LED_Mode.setProgressDrawable(getResources().getDrawable(R.drawable.blue_track));
                break;
        }

    }

    /**
     * Description:页面加载后弹出等待框，等待设备可被控制状态回调，如果一直不可被控，等待一段时间后自动退出界面
     */
    private void getStatusOfDevice() {
        System.out.println("========isControl "+isDeviceCanBeControlled());
        // 设备是否可控
        if (isDeviceCanBeControlled()) {
            // 可控则查询当前设备状态
            mDevice.getDeviceStatus();
        } else {
            // 显示等待栏
            progressDialog.show();
            if (mDevice!=null){
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
        if (mDevice!=null){
            return mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
        }
        else {
            return false;
        }
    }

    private void toastDeviceNoReadyAndExit() {
        Toast.makeText(getActivity(), "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
//        finish();
    }

    private void toastDeviceDisconnectAndExit() {
        Toast.makeText(getActivity(), "连接已断开", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
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

        /** 用于获取设备状态 */
        public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
                                   java.util.concurrent.ConcurrentHashMap<String, Object> dataMap, int sn) {
            Log.i("liang====", "接收到数据");
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS && dataMap.get("data") != null) {
                getDataFromReceiveDataMap(dataMap);
                mHandler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
            }
        }

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
                progressDialog.dismiss();
            } else {
                mHandler.sendEmptyMessage(handler_key.DISCONNECT.ordinal());
            }
        }

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
                if (dataKey.equals(KEY_HALL_AC_ONOFF)) {
                    data_Hall_AC_OnOff = (Boolean) map.get(dataKey);
                }

                if (dataKey.equals(KEY_HALL_HUMC)) {
                    data_Hall_humC = (Integer) map.get(dataKey);
                }

                if (dataKey.equals(KEY_HALL_AIRC)) {

                    data_Hall_AirC = (Integer) map.get(dataKey);
                }
                if (dataKey.equals(KEY_LED_MODE)) {
                    data_LED_Mode = (Integer) map.get(dataKey);
                }
                if (dataKey.equals(KEY_LED_R)) {

                    data_LED_R = (Integer) map.get(dataKey);
                }
                if (dataKey.equals(KEY_LED_G)) {

                    data_LED_G = (Integer) map.get(dataKey);
                }
                if (dataKey.equals(KEY_LED_B)) {

                    data_LED_B = (Integer) map.get(dataKey);
                }
            }
        }
    }

    /**
     * 设置ProgressDialog
     */
    public void setProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        String loadingText = getString(R.string.loadingtext);
        progressDialog.setMessage(loadingText);
        progressDialog.setCanceledOnTouchOutside(false);
    }

}
