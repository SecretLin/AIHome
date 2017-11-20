package com.example.secret.ai10.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secret.ai10.ConfigModule.GosAirlinkChooseDeviceWorkWiFiActivity;
import com.example.secret.ai10.DeviceModuel.DeviceConfig;
import com.example.secret.ai10.R;
import com.example.secret.ai10.view.RevertRPB;
import com.example.secret.ai10.view.RoundProgressBar1;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorActivity extends Activity {
    // 数据点"室外温度"对应的标识名
    protected static final String KEY_OUT_TEMP = "Out_Temp";
    // 数据点"室外湿度"对应的标识名
    protected static final String KEY_OUT_HUM = "Out_Hum";
    // 数据点"大厅温度"对应的标识名
    protected static final String KEY_HALL_TEMP = "Hall_Temp";
    // 数据点"大厅湿度"对应的标识名
    protected static final String KEY_HALL_HUM = "Hall_Hum";
    // 数据点"大厅是否有人"对应的标识名
    protected static final String KEY_HALL_HUMAN = "Hall_human";
    // 数据点"厕所是否有人"对应的标识名
    protected static final String KEY_WC_HUMAN = "WC_human";
    // 数据点"卧室是否有人"对应的标识名
    protected static final String KEY_ROOM_HUMAN = "Room_human";



    // 数据点"室外温度"对应的存储数据
    protected static int data_Out_Temp;
    // 数据点"室外湿度"对应的存储数据
    protected static int data_Out_Hum;

    // 数据点"室外空气质量指数"对应的标识名
    protected static final String KEY_Out_Air_Num = "Out_Air_Num";
    // 数据点"大厅温度"对应的存储数据
    protected static int data_Hall_Temp;
    // 数据点"大厅湿度"对应的存储数据
    protected static int data_Hall_Hum;

    // 数据点"室外空气质量指数"对应的存储数据
    protected static int data_out_air_qq;

    // 数据点"大厅是否有人"对应的存储数据
    protected static boolean data_Hall_human;
    // 数据点"厕所是否有人"对应的存储数据
    protected static boolean data_WC_human;
    // 数据点"卧室是否有人"对应的存储数据
    protected static boolean data_Room_human;

    // 数据点"室外空气质量"对应的标识名
    protected static final String KEY_OUT_AIR = "Out_Air";
    // 数据点"室内空气质量"对应的标识名
    protected static final String KEY_IN_AIR = "In_Air";

    // 数据点"室内空气质量"对应的存储数据
    protected static int data_In_Air;
    // 数据点"室外空气质量"对应的存储数据
    protected static int data_Out_Air;
    private GizWifiDevice mDevice;
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

    TextView tvOutAirQ, tvInsideAirQ,
            tvOutHum, tvInsideHum,
            tvOutTemp, tvInsideTemp,
            tvAir,tvTemp;
    RoundProgressBar1 airQBar, humBar, tempBar;
    RevertRPB tempBar1;

    private ImageView ivSofa,ivBed,ivWC;
    private TextView tvLivingroom,tvWC,tvBedroom;



    private TextView tvTitle;

    private Button btnNoDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_monitor);
        initView();
        initDevice();


    }

    private void initView() {


        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("监测");

        btnNoDevice = (Button) findViewById(R.id.btnNoDevice);
        btnNoDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MonitorActivity.this, GosAirlinkChooseDeviceWorkWiFiActivity.class);
                startActivity(intent1);
            }
        });

        tvInsideAirQ = (TextView) findViewById(R.id.inside_air_q);
        tvInsideHum = (TextView) findViewById(R.id.inside_hum);
        tvInsideTemp = (TextView) findViewById(R.id.inside_temp);
        tvOutAirQ = (TextView) findViewById(R.id.out_air_q);
        tvOutHum = (TextView) findViewById(R.id.out_hum);
        tvOutTemp = (TextView) findViewById(R.id.out_temp);

        tvAir = (TextView) findViewById(R.id.air);
        tvTemp = (TextView) findViewById(R.id.temp);

        ivSofa = (ImageView) findViewById(R.id.ivSofa);
        ivBed = (ImageView) findViewById(R.id.ivBed);
        ivWC = (ImageView) findViewById(R.id.ivBathtub);
        tvLivingroom = (TextView) findViewById(R.id.tvLivingroom);
        tvBedroom = (TextView) findViewById(R.id.tvBedroom);
        tvWC = (TextView) findViewById(R.id.tvWC);

        airQBar = (RoundProgressBar1) findViewById(R.id.airQBar);
        humBar = (RoundProgressBar1) findViewById(R.id.humBar);
        tempBar = (RoundProgressBar1) findViewById(R.id.tempBar);

        tempBar1 = (RevertRPB) findViewById(R.id.tempBar1);

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

//            System.out.println("hhhhhhhhhhhhhhhhhhh");
        getStatusOfDevice();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
        // 退出页面，取消设备订阅
        if (mDevice != null) {
            mDevice.setSubscribe(false);
            mDevice.setListener(null);
        }

    }

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

    protected void getDataFromReceiveDataMap(ConcurrentHashMap<String, Object> dataMap) {
        // 已定义的设备数据点，有布尔、数值和枚举型数据

        if (dataMap.get("data") != null) {
            ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
            for (String dataKey : map.keySet()) {
                //室外温度
                if (dataKey.equals(KEY_OUT_TEMP)) {

                    data_Out_Temp = (Integer) map.get(dataKey);
                }
                //室外湿度
                if (dataKey.equals(KEY_OUT_HUM)) {

                    data_Out_Hum = (Integer) map.get(dataKey);
                }

                //室外空气质量
                if (dataKey.equals(KEY_OUT_AIR)) {
                    data_Out_Air = (Integer) map.get(dataKey);
                }
                //室内空气质量
                if (dataKey.equals(KEY_IN_AIR)) {
                    data_In_Air = (Integer) map.get(dataKey);
                }
                //大厅湿度
                if (dataKey.equals(KEY_HALL_HUM)) {
                    data_Hall_Hum = (Integer) map.get(dataKey);
                }
                //大厅温度
                if (dataKey.equals(KEY_HALL_TEMP)) {
                    data_Hall_Temp = (Integer) map.get(dataKey);
                }
                //室外空气质量指数
                if (dataKey.equals(KEY_Out_Air_Num)){
                    data_out_air_qq = (Integer) map.get(dataKey);
                }


                if (dataKey.equals(KEY_HALL_HUMAN)) {
                    data_Hall_human = (Boolean) map.get(dataKey);
                }
                if (dataKey.equals(KEY_WC_HUMAN)) {
                    data_WC_human = (Boolean) map.get(dataKey);
                }
                if (dataKey.equals(KEY_ROOM_HUMAN)) {
                    data_Room_human = (Boolean) map.get(dataKey);
                }

            }
        }


    }

    /**
     * Description:根据保存的的数据点的值来更新UI
     */

    private int airQ;
    protected void updateUI() {


        tvOutTemp.setText("室外空气温度：" + data_Out_Temp + "");
        if (data_Out_Hum!=0){
            tvOutHum.setText("室外空气湿度：" + data_Out_Hum + "");
        }



        switch (data_In_Air){
            case 0:
                tvInsideAirQ.setText("室内空气质量：良好");
                break;
            case 1:
                tvInsideAirQ.setText("室内空气质量：一般");
                break;
            case 2:
                tvInsideAirQ.setText("室内空气质量：较差");
                break;
        }

        switch (data_Out_Air){
            case 0:
                tvOutAirQ.setText("室外空气质量：良好");
                break;
            case 1:
                tvOutAirQ.setText("室外空气质量：一般");
                break;
            case 2:
                tvOutAirQ.setText("室外空气质量：较差");
                break;
            case 3:
                tvOutAirQ.setText("室外空气质量：糟糕");
                break;
            case 4:
                tvOutAirQ.setText("室外空气质量：危险");
                break;
            case 5:
                tvOutAirQ.setText("室外空气质量：死人");
                break;
        }

        if (data_Hall_Hum!=0){
            tvInsideHum.setText("室内空气湿度：" + data_Hall_Hum);
        }

        tvInsideTemp.setText("室内空气温度：" + data_Hall_Temp);

        tvAir.setText(data_out_air_qq+"");
        tvTemp.setText(data_Out_Temp+"°C");

        if (data_Hall_human){
            ivSofa.setImageResource(R.mipmap.sofa_blue);
            tvLivingroom.setText("大厅有人");
            tvLivingroom.setTextColor(Color.parseColor("#35C0C5"));
        }
        else {
            ivSofa.setImageResource(R.mipmap.sofa_grey);
            tvLivingroom.setText("大厅无人");
            tvLivingroom.setTextColor(Color.parseColor("#BCBCBC"));
        }

        if (data_Room_human){
            ivBed.setImageResource(R.mipmap.bed_blue);
            tvBedroom.setText("卧室有人");
            tvBedroom.setTextColor(Color.parseColor("#35C0C5"));
        }
        else {
            ivBed.setImageResource(R.mipmap.sofa_grey);
            tvBedroom.setText("卧室无人");
            tvBedroom.setTextColor(Color.parseColor("#BCBCBC"));
        }

        if (data_WC_human){
            ivWC.setImageResource(R.mipmap.bathtub_blue);
            tvWC.setText("浴室有人");
            tvWC.setTextColor(Color.parseColor("#35C0C5"));
        }
        else {
            ivWC.setImageResource(R.mipmap.bathtub_gray);
            tvWC.setText("浴室无人");
            tvWC.setTextColor(Color.parseColor("#BCBCBC"));
        }

        airQ = data_out_air_qq/5;
        airQBar.setProgress(airQ);
        humBar.setProgress(data_Out_Hum);
        if (data_Out_Temp<0){
//            tempBar.setRingProgressColor(Color.parseColor("#000000"));
            tempBar1.setProgress(-data_Out_Temp);
            tempBar1.setVisibility(View.VISIBLE);
            tempBar.setVisibility(View.INVISIBLE);
//            tempBar.setSweetAngle(-(360 * tempBar.getProgress() / tempBar.getMax()));


        }
        else {
            tempBar.setProgress(data_Out_Temp);
            tempBar.setVisibility(View.VISIBLE);
            tempBar1.setVisibility(View.GONE);
        }

    }

    /**
     * Description:页面加载后弹出等待框，等待设备可被控制状态回调，如果一直不可被控，等待一段时间后自动退出界面
     */
    private void getStatusOfDevice() {
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
        Toast.makeText(this, "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void toastDeviceDisconnectAndExit() {
        Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        finish();
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


}
