package com.example.secret.ai10.UI.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secret.ai10.Adapter.ModeAdapter;
import com.example.secret.ai10.R;
import com.example.secret.ai10.database.AuthSql;
import com.example.secret.ai10.database.CustomMode;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Secret on 2017/11/16.
 */

public class ModeFragment extends Fragment {
    private ModeAdapter myAdapter;
    private RecyclerView rv;
    private AuthSql db = new AuthSql(getContext());
    private RelativeLayout layout_custom;
    private TextView tvTitle;

    // 数据点"情景模式"对应的标识名
    protected static final String KEY_HOMEMODE = "HomeMode";
    // 数据点"情景模式"对应的存储数据
    protected static int data_HomeMode;
    private GizWifiDevice mDevice;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mode,container,false);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("情景模式");

        rv = (RecyclerView) view.findViewById(R.id.rv);

        initDevice();


        myAdapter = new ModeAdapter(getContext(),mDevice);
        initData();
        rv.setAdapter(myAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }

    private void initData() {
        List<CustomMode> list = new ArrayList<>();

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

        myAdapter.bindData(list);





    }
    private void initDevice() {

        mDevice = getArguments().getParcelable("device");
        mDevice.setSubscribe(true);
        mDevice.setListener(gizWifiDeviceListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
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


    /**
     * Description:根据保存的的数据点的值来更新UI
     */
    protected void updateUI() {

        myAdapter.isSelected = data_HomeMode;
        myAdapter.notifyDataSetChanged();


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




    private boolean isDeviceCanBeControlled() {
        if (mDevice != null) {
            return mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
        } else {
            return false;
        }
    }

    private void toastDeviceNoReadyAndExit() {
        Toast.makeText(getContext(), "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
//        finish();
    }

    private void toastDeviceDisconnectAndExit() {
        Toast.makeText(getContext(), "连接已断开", Toast.LENGTH_SHORT).show();
//        finish();
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


    public static ModeFragment newInstance(GizWifiDevice device){

        ModeFragment fragment = new ModeFragment();
        Bundle args = new Bundle();
        args.putParcelable("device",device);
        fragment.setArguments(args);
        return fragment;
    }

}
