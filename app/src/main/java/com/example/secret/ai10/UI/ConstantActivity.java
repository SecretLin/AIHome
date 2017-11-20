package com.example.secret.ai10.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secret.ai10.ConfigModule.GosAirlinkChooseDeviceWorkWiFiActivity;
import com.example.secret.ai10.DeviceModuel.fragment.LivingRoom;
import com.example.secret.ai10.R;
import com.example.secret.ai10.view.ThridSwitchSeekBar;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

public class ConstantActivity extends Activity implements View.OnClickListener {
    // 数据点"开灯"对应的标识名
    protected static final String KEY_LED_ONOFF = "LED_OnOff";
    // 数据点"开关窗"对应的标识名
    protected static final String KEY_WIN_ONOFF = "Win_OnOff";
    // 数据点"开关窗帘"对应的标识名
    protected static final String KEY_CURTAIN_ONOFF = "Curtain_OnOff";

    // 数据点"开灯"对应的存储数据
    protected static boolean data_LED_OnOff;
    // 数据点"开关窗"对应的存储数据
    protected static boolean data_Win_OnOff;
    // 数据点"开关窗帘"对应的存储数据
    protected static boolean data_Curtain_OnOff;
    private GizWifiDevice mDevice;
    private Switch sw_bool_LED_OnOff;
    private Switch sw_bool_Win_OnOff;
    private Switch sw_bool_Curtain_OnOff;
    private ProgressDialog progressDialog;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_constant);
        initView();
        initDevice();

    }

    private void initView() {

        setProgressDialog();

        sw_bool_LED_OnOff = (Switch) findViewById(R.id.sw_bool_LED_OnOff);
        sw_bool_Win_OnOff = (Switch) findViewById(R.id.sw_bool_Win_OnOff);
        sw_bool_Curtain_OnOff = (Switch) findViewById(R.id.sw_bool_Curtain_OnOff);


        sw_bool_LED_OnOff.setOnClickListener(this);
        sw_bool_Win_OnOff.setOnClickListener(this);
        sw_bool_Curtain_OnOff.setOnClickListener(this);

    }

    private void initDevice() {

        mDevice = getIntent().getParcelableExtra("device");
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
        Toast.makeText(this, "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
//        finish();
    }

    private void toastDeviceDisconnectAndExit() {
        Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
//        finish();
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
            }
        }
    }

    /**
     * 设置ProgressDialog
     */
    public void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        String loadingText = getString(R.string.loadingtext);
        progressDialog.setMessage(loadingText);
        progressDialog.setCanceledOnTouchOutside(false);
    }

}
