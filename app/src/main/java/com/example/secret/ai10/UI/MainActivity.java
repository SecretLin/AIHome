package com.example.secret.ai10.UI;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import com.example.secret.ai10.ConfigModule.GosAirlinkChooseDeviceWorkWiFiActivity;
import com.example.secret.ai10.DeviceModuel.ControlDeviceActivity;
import com.example.secret.ai10.DeviceModuel.DeviceConfig;
import com.example.secret.ai10.GosApplication;
import com.example.secret.ai10.MyApplication;
import com.example.secret.ai10.QRCode.CaptureActivity;
import com.example.secret.ai10.R;
import com.example.secret.ai10.Service.LocationService;
import com.example.secret.ai10.UI.fragment.DeviceListFragment;
import com.example.secret.ai10.UI.fragment.ModeFragment;
import com.example.secret.ai10.UserModule.GosChangeUserPasswordActivity;
import com.example.secret.ai10.UserModule.GosUserLoginActivity;
import com.example.secret.ai10.database.CustomMode;
import com.example.secret.ai10.utils.DisplayUtils;
import com.example.secret.ai10.view.Popwindow;
import com.example.secret.ai10.view.SlideMenu;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import app.mosn.zdepthshadowlayout.ZDepth;
import app.mosn.zdepthshadowlayout.ZDepthShadowLayout;

public class MainActivity extends Activity implements View.OnClickListener {


    SlideMenu mSlideMenu;
    LinearLayout main;
    Popwindow popwindow;

    private ImageButton btnShowMenu, btnAddDevice;

    private static final int REQUEST_CODE_SETTING = 100;

    private static final int REQUEST_ZXINGCODE_SETTING = 200;
    public SharedPreferences spf;
    public static final String SPF_Name = "set";
    private String uid, token;

    private TextView tvMonitor, tvMode, tvConstant, tvLoc, tvTemp, tvHum,tvCond;
    //定位服务
    private LocationService locationService;

    private RelativeLayout layout_manage, layout_newDevice, layout_offline, layout_unbind,
            layout_changePW, layout_logout;



    public boolean isMenuOpen = false;

    /*
    获取第一个设备及对应信息。
     */
    GizWifiDevice mDevice;
    private enum handler_key {

        /**
         * 更新界面
         */
        UPDATE_UI,

        DISCONNECT,
    }
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
    private Runnable mRunnable = new Runnable() {
        public void run() {
            if (isDeviceCanBeControlled()) {
//                progressDialog.cancel();
            } else {
                toastDeviceNoReadyAndExit();
            }
        }

    };
    // 数据点"情景模式"对应的存储数据
    protected static int data_HomeMode;
    // 数据点"情景模式"对应的标识名
    protected static final String KEY_HOMEMODE = "HomeMode";
    private void initDevices() {

        List<GizWifiDevice> list = DeviceConfig.deviceslist;

        List<GizWifiDevice> onlineBind = new ArrayList<>();


        for (GizWifiDevice device : list){
            if (GizWifiDeviceNetStatus.GizDeviceOnline == device.getNetStatus()
                    || GizWifiDeviceNetStatus.GizDeviceControlled == device.getNetStatus()) {
                if (device.isBind()) {

                    onlineBind.add(device);
                }
            }

        }

        if (!onlineBind.isEmpty()){
            mDevice = onlineBind.get(0);
            mDevice.setSubscribe(true);
            System.out.println("=======device name:"+mDevice.getAlias());
            mDevice.setListener(gizWifiDeviceListener);
        }
        else {
            System.out.println("=======no device");
        }

    }
    GizWifiDeviceListener gizWifiDeviceListener = new GizWifiDeviceListener() {

        /**
         * 用于设备订阅
         */
        public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {

        }


        /**
         * 用于获取设备状态
         */
        public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
                                   java.util.concurrent.ConcurrentHashMap<String, Object> dataMap, int sn) {
            Log.i("liang====", "接收到数据");
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS && dataMap.get("data") != null) {
                getDataFromReceiveDataMap(dataMap);
                mHandler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
            }
        }

        /**
         * 用于设备状态变化
         */
        public void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
            if (netStatus == GizWifiDeviceNetStatus.GizDeviceControlled) {
                mHandler.removeCallbacks(mRunnable);
//                progressDialog.cancel();
            } else {
                mHandler.sendEmptyMessage(handler_key.DISCONNECT.ordinal());
            }
        }
    };
    List<CustomMode> list = new ArrayList<>();
    private void initData() {


        CustomMode customMode = new CustomMode();
        customMode.setModeName("手动");
//        customMode.setOpen(1);
        list.add(customMode);

        CustomMode customMode1 = new CustomMode();
        customMode1.setModeName("自动");
//        customMode.setOpen(0);
        list.add(customMode1);

        CustomMode customMode2 = new CustomMode();
        customMode2.setModeName("工作日");
//        customMode.setOpen(0);
        list.add(customMode2);

        CustomMode customMode3 = new CustomMode();
        customMode3.setModeName("睡眠");
//        customMode.setOpen(0);
        list.add(customMode3);

        CustomMode customMode4 = new CustomMode();
        customMode4.setModeName("外出");
//        customMode.setOpen(0);
        list.add(customMode4);
    }
    /**
     * Description:根据保存的的数据点的值来更新UI
     */

    protected void updateUI() {

        tvMode.setText(list.get(data_HomeMode).getModeName()+"");
        System.out.println("=====homemode:"+data_HomeMode);


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
    private boolean isDeviceCanBeControlled() {
        if (mDevice != null) {
            return mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
        } else {
            return false;
        }
    }

    private void toastDeviceNoReadyAndExit() {
        Toast.makeText(this, "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
//        finish();
    }

    private void toastDeviceDisconnectAndExit() {
        Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
//        finish();
    }
    protected void getDataFromReceiveDataMap(ConcurrentHashMap<String, Object> dataMap) {
        // 已定义的设备数据点，有布尔、数值和枚举型数据

        if (dataMap.get("data") != null) {
            ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
            for (String dataKey : map.keySet()) {
                if (dataKey.equals(KEY_HOMEMODE)) {
                    data_HomeMode = (int) map.get(dataKey);
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spf = getSharedPreferences(SPF_Name, Context.MODE_PRIVATE);
        uid = spf.getString("uid", "");
        token = spf.getString("token", "");
        System.out.println("token ==== " + token);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        tvLoc = (TextView) findViewById(R.id.tvLoc);
        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvHum = (TextView) findViewById(R.id.tvHum);
        tvCond = (TextView) findViewById(R.id.tvCond);


        mSlideMenu = (SlideMenu) findViewById(R.id.slideMenu);

//        //获取设备和设备的数据
//        initData();
//        initDevices();

        mSlideMenu.setOnSlideListener(new SlideMenu.OnSlideListener() {
            @Override
            public void onOpen() {

                isMenuOpen = true;
                locationService.unregisterListener(BDLocListener); //注销掉监听
                locationService.stop(); //停止定位服务


            }

            @Override
            public void onClose() {

                isMenuOpen = false;
                locationService = ((GosApplication) getApplication()).locationService;

                //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
                locationService.registerListener(BDLocListener);
                //注册监听
                int type = getIntent().getIntExtra("from", 0);
                if (type == 0) {
                    locationService.setLocationOption(locationService.getDefaultLocationClientOption());
                } else if (type == 1) {
                    locationService.setLocationOption(locationService.getOption());
                }
                locationService.start();// 定位SDK


            }

            @Override
            public void onDraging(float fraction) {

            }
        });
        mSlideMenu.closeMenu();

        //点击主布局，收回侧滑菜单
        main = (LinearLayout) findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideMenu.closeMenu();
            }
        });


        //点击主布侧滑菜单局左上角的按钮，显示
        btnShowMenu = (ImageButton) findViewById(R.id.btnShowMenu);
        btnShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideMenu.showMenu();
            }
        });


        AndPermission.with(this)
                .requestCode(REQUEST_CODE_SETTING)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION).callback(mypermissionlistener).rationale(new RationaleListener() {

            @Override
            public void showRequestPermissionRationale(int arg0, Rationale arg1) {
                AndPermission.rationaleDialog(MainActivity.this, arg1).show();
            }
        }).send();

        btnAddDevice = (ImageButton) findViewById(R.id.addDevice);
        btnAddDevice.setOnClickListener(this);

        tvMonitor = (TextView) findViewById(R.id.tvMonitor);
        tvMode = (TextView) findViewById(R.id.tvMode);
        tvConstant = (TextView) findViewById(R.id.tvConstant);
        tvMonitor.setOnClickListener(this);
        tvMode.setOnClickListener(this);
        tvConstant.setOnClickListener(this);


        //侧滑菜单的点击事件
        layout_manage = (RelativeLayout) findViewById(R.id.l1);
        layout_newDevice = (RelativeLayout) findViewById(R.id.l2);
        layout_offline = (RelativeLayout) findViewById(R.id.l3);
        layout_unbind = (RelativeLayout) findViewById(R.id.l4);
        layout_changePW = (RelativeLayout) findViewById(R.id.l5);
        layout_logout = (RelativeLayout) findViewById(R.id.l6);

        layout_logout.setOnClickListener(this);
        layout_manage.setOnClickListener(this);
        layout_changePW.setOnClickListener(this);
        layout_unbind.setOnClickListener(this);
        layout_offline.setOnClickListener(this);
        layout_newDevice.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //右上角添加设备
            case R.id.addDevice:
                if (popwindow == null) {
                    //自定义的单击事件
                    OnClickLintener paramOnClickListener = new OnClickLintener();
                    popwindow = new Popwindow(MainActivity.this, paramOnClickListener, DisplayUtils.dp2px(MainActivity.this, 160), DisplayUtils.dp2px(MainActivity.this, 95));
                    //监听窗口的焦点事件，点击窗口外面则取消显示
                    popwindow.getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                popwindow.dismiss();
                            }
                        }
                    });
                }
                //设置默认获取焦点
                popwindow.setFocusable(true);
                //以某个控件的x和y的偏移量位置开始显示窗口
                popwindow.showAsDropDown(btnAddDevice, 0, 0);
                //如果窗口存在，则更新
                popwindow.update();
                break;
            /*
            侧滑菜单的点击事件
             */
            //设备管理
            case R.id.l1:
                Intent intent = new Intent(this,DeviceListActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("uid",uid);
//                bundle.putString("token",token);
                intent.putExtra("toActivity","设备管理");
//                intent.putExtras(bundle);
                startActivity(intent);
                break;

            //权限管理
            case R.id.l2:
                Intent intent1 = new Intent(this,DeviceListActivity.class);
                Bundle bundle1 = new Bundle();
//                bundle1.putString("uid",uid);
//                bundle1.putString("token",token);
                intent1.putExtra("toActivity","权限管理");
//                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;

            //离线设备
            case R.id.l3:

                startActivity(new Intent(this, OfflineDeviceActivity.class));

                break;

            //解绑设备
            case R.id.l4:
                Intent intent2 = new Intent(this,UnBindDeviceActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("uid",uid);
                bundle2.putString("token",token);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;

            //修改密码
            case R.id.l5:
                startActivity(new Intent(this, GosChangeUserPasswordActivity.class));
                break;

            //退出
            case R.id.l6:
                logoutToClean();
                startActivity(new Intent(this, GosUserLoginActivity.class));
                finish();
                break;

                //监测
            case R.id.tvMonitor:
                Intent intent3 = new Intent(this,DeviceListActivity.class);

//                bundle1.putString("uid",uid);
//                bundle1.putString("token",token);
                intent3.putExtra("toActivity","监测");
//                intent1.putExtras(bundle1);
                startActivity(intent3);
                break;
            //模式
            case R.id.tvMode:
                startActivity(new Intent(this, ModeActivity.class));
                break;
            //常用
            case R.id.tvConstant:
                Intent intent4 = new Intent(this,DeviceListActivity.class);

//                bundle1.putString("uid",uid);
//                bundle1.putString("token",token);
                intent4.putExtra("toActivity","常用");
//                intent1.putExtras(bundle1);
                startActivity(intent4);
                break;


            /*
            对话框的按钮事件
             */
            case R.id.btnCancel:
                dialog.dismiss();
                break;
            case R.id.btnConfirm:
                name = etDeviceName.getText().toString();

                if (name != null) {

                    System.out.println("===================" + devices[0]);
                    GizWifiSDK.sharedInstance().bindDevice(uid,token,devices[0],devices[1],etDeviceName.getText().toString());


                    dialog.dismiss();
                    toast("绑定成功");
                    popwindow.dismiss();
                } else {
                    toast("备注不能为空");
                }
                break;
        }
    }

//    public void updateDialog() {
//        if (name.equals("大厅")) {
//            layoutLiving.setBackgroundResource(R.mipmap.hall_blue);
//        } else {
//            layoutLiving.setBackgroundResource(R.mipmap.hall_white);
//        }
//        if (name.equals("浴室")) {
//            layoutWC.setBackgroundResource(R.mipmap.wc_blue);
//        } else {
//            layoutWC.setBackgroundResource(R.mipmap.wc_white);
//        }
//        if (name.equals("卧室")) {
//            layoutBed.setBackgroundResource(R.mipmap.bedroom_blue);
//        } else {
//            layoutBed.setBackgroundResource(R.mipmap.bedroom_white);
//        }
//    }

    class OnClickLintener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scanQRCode:
                    AndPermission.with(MainActivity.this)
                            .requestCode(REQUEST_ZXINGCODE_SETTING)
                            .permission(Manifest.permission.CAMERA).callback(mypermissionlistener).rationale(new RationaleListener() {

                        @Override
                        public void showRequestPermissionRationale(int arg0, Rationale arg1) {
                            AndPermission.rationaleDialog(MainActivity.this, arg1).show();
                        }
                    }).send();

                    break;

                case R.id.addNewDevice:
                    if (!checkNetwork(MainActivity.this)) {
                        Toast.makeText(MainActivity.this, R.string.network_error, 2000).show();
                    } else {
                        Intent intent1 = new Intent(MainActivity.this, GosAirlinkChooseDeviceWorkWiFiActivity.class);
                        startActivity(intent1);
                    }
                    break;

                default:
                    break;
            }

        }

    }

    /**
     * 检查网络连通性（工具方法）
     *
     * @param context
     * @return
     */
    public boolean checkNetwork(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = conn.getActiveNetworkInfo();
        if (net != null && net.isConnected()) {
            return true;
        }
        return false;
    }

    //获取权限
    PermissionListener mypermissionlistener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantPermissions) {
            switch (requestCode) {


                case REQUEST_ZXINGCODE_SETTING:
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, 1);
                    break;


                default:

                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {

                // 第二种：用自定义的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, REQUEST_CODE_SETTING)
                        .setTitle("权限申请失败")
                        .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                        .setPositiveButton("好，去设置")
                        .show();

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();


        GizWifiSDK.sharedInstance().setListener(mListener);
        initDevices();
        initData();
        updateUI();



        mSlideMenu.closeMenu();

        locationService = ((GosApplication) getApplication()).locationService;

        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(BDLocListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK

        getStatusOfDevice();

    }

    public String name = null;
    public String[] devices = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (resultCode == -1) {
            Bundle bundle = data.getExtras();
            final String[] strings = (String[]) bundle.getSerializable("device");
            devices = strings;
            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {

                showDialog();

            } else {
                Toast.makeText(this, "没有登录，不能绑定设备", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private AlertDialog dialog;
    private Button btnCancel, btnConfirm;
    private EditText etDeviceName;

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.set_device_tag, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
        etDeviceName = (EditText) view.findViewById(R.id.etDeviceName);

        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    // 接收设备列表变化上报，刷新UI
    GizWifiSDKListener mListener = new GizWifiSDKListener() {
        @Override
        public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {

            // 提示错误原因
            if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Log.d("", "result: " + result.name());
            }
            // 显示变化后的设备列表
            Log.d("", "discovered deviceList: " + deviceList);
            DeviceConfig.deviceslist = deviceList;
        }

        @Override
        public void didBindDevice(GizWifiErrorCode result, String did) {

            System.out.println("=========result:"+result.name());
            System.out.println("======binding..."+did);

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();


        mHandler.removeCallbacks(mRunnable);
        // 退出页面，取消设备订阅
        if (mDevice != null) {
            mDevice.setSubscribe(false);
            mDevice.setListener(null);
        }
    }

    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isMenuOpen) {
                mSlideMenu.closeMenu();
                return true;
            }

        }


        return super.onKeyDown(keyCode, event);

    }

    /**
     * 注销函数
     */
    void logoutToClean() {
        spf.edit().putString("UserName", "").commit();
        spf.edit().putString("PassWord", "").commit();
        spf.edit().putString("Uid", "").commit();
        spf.edit().putString("thirdUid", "").commit();

        spf.edit().putString("Token", "").commit();


    }


    /**
     * 显示请求字符串
     *
     * @param str
     */
    public void logMsg(String str) {
        final String s = str;
        try {
            if (tvLoc != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        tvLoc.post(new Runnable() {
                            @Override
                            public void run() {
                                tvLoc.setText(s);

                                System.out.println("======loc:" + s);
                            }
                        });

                    }
                }).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(BDLocListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // -----------location config ------------
        locationService = ((GosApplication) getApplication()).locationService;

        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(BDLocListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }

        locationService.start();// 定位SDK


    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    String city_name = null;
    private BDLocationListener BDLocListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                city_name = location.getCity();

                logMsg(city_name);
                getTempFromNet(city_name);
                if (location.getLocType() == BDLocation.TypeServerError) {
                    toast("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    toast("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    toast("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                locationService.unregisterListener(BDLocListener); //注销掉监听
                locationService.stop(); //停止定位服务
            } else {
                System.err.println("========error!!!!!!!location=" + location + "  type:" + location.getLocType());
            }
        }


    };

    String temp, hum,cond;

    public void getTempFromNet(final String city) {

        try {
            new Thread() {
                BufferedReader in = null;
                StringBuffer result = null;

                @Override
                public void run() {
                    super.run();
                    try {
                        String requestUrl = "https://free-api.heweather.com/s6/weather?key=de5b590c06384b5195cebc329fef83e2&location=";
//

                        URL url = new URL(requestUrl + city);
                        URLConnection connection = url.openConnection();
                        connection.connect();

                        result = new StringBuffer();
                        //读取URL的响应
                        in = new BufferedReader(new InputStreamReader(
                                connection.getInputStream(), "UTF-8"));
                        String line;
                        while ((line = in.readLine()) != null) {
                            result.append(line);
                        }
                        String s = result.toString();

                        JSONObject jo = new JSONObject(s);
                        JSONArray ja = jo.getJSONArray("HeWeather6");
                        JSONObject jo1 = ja.getJSONObject(0);
                        JSONObject now = jo1.getJSONObject("now");
                        hum = now.getString("hum");

                        temp = now.getString("tmp");

                        cond = now.getString("cond_txt");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvHum.setText("湿度:" + hum + "%");
                                tvTemp.setText(temp);
                                tvCond.setText(cond);
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }

                }
            }.start();


        } catch (Exception e) {
//            Log.e(TAG, e.toString());
        }

    }
}
