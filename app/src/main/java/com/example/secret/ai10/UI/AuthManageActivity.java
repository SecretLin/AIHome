package com.example.secret.ai10.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secret.ai10.Adapter.AuthAdapter;
import com.example.secret.ai10.DeviceModuel.DeviceConfig;
import com.example.secret.ai10.R;
import com.example.secret.ai10.UI.fragment.ModeFragment;
import com.example.secret.ai10.database.Auth;
import com.example.secret.ai10.database.AuthSql;
import com.example.secret.ai10.database.BindDevice;
import com.example.secret.ai10.utils.RecyclerItemClickListener;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManageActivity extends Activity implements View.OnClickListener,AuthAdapter.CheckBoxChangedListener {

    private RecyclerView rv;
    private LinearLayout btnAdd, btnEdit;
    private TextView tvTitle;
    private AuthAdapter adapter;

    String name = null;
    List<Auth> list = new ArrayList<>();


    // 数据点"RFID开门权限管理"对应的标识名
    protected static final String KEY_RFID_KEY_C = "RFID_Key_C";
    // 数据点"RFID权限修改情况"对应的标识名
    protected static final String KEY_RFID_KEY_R = "RFID_Key_R";

    // 数据点"RFID开门权限管理"对应的存储数据
    protected static int data_RFID_Key_C;
    // 数据点"RFID权限修改情况"对应的存储数据
    protected static int data_RFID_Key_R;

    GizWifiDevice mDevice;


    private boolean isLongClick = false;
    private RelativeLayout layout_select;
    private LinearLayout layout_delete,btnDelete,layoutTitle;
    public TextView tvCount,tvCancel,tvSelectAll,tvCancelAll;

    private ScrollView llNoAuth;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_authe_manage);
        mDevice = getIntent().getParcelableExtra("device");
        mDevice.setListener(gizWifiDeviceListener);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("权限管理");

        llNoAuth =(ScrollView) findViewById(R.id.llNoAuth);
        llNoAuth.setOnClickListener(this);

        btnAdd = (LinearLayout) findViewById(R.id.btnAdd);
        btnEdit = (LinearLayout) findViewById(R.id.btnEdit);


        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvSelectAll = (TextView) findViewById(R.id.tvSelectAll);
        tvCancelAll = (TextView) findViewById(R.id.tvCancelAll);
        tvCount = (TextView) findViewById(R.id.tvCount);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("权限管理");
        layout_delete = (LinearLayout) findViewById(R.id.layout_delete);
        layout_select = (RelativeLayout) findViewById(R.id.layout_select);
        layoutTitle = (LinearLayout)findViewById(R.id.layoutTitle);
        btnDelete = (LinearLayout) findViewById(R.id.btndelete);

        rv = (RecyclerView) findViewById(R.id.rv);

        adapter = new AuthAdapter(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isLongClick){
                    if (list.get(position).isSelected()){
                        list.get(position).setSelected(false);
                    }else {
                        list.get(position).setSelected(true);
                    }

                    adapter.notifyItemChanged(position);

                }
                else {
                    System.out.println("one~click~hhhhhhhhhhh");
                }


            }

            @Override
            public void onItemLongClick(View view, int position) {
                isLongClick = true;
                adapter.isShow = true;
                adapter.notifyDataSetChanged();

                layoutTitle.setVisibility(View.GONE);
                layout_delete.setVisibility(View.VISIBLE);
                layout_select.setVisibility(View.VISIBLE);

                if (!adapter.isCheckBoxPress){

                    list.get(position).setSelected(true);
                    adapter.notifyItemChanged(position);

                    System.out.println("click~click~hhhh");
                }
            }
        }));

        btnAdd.setOnClickListener(this);
        btnEdit.setOnClickListener(this);


        initData();
    }

    private void initData() {

        AuthSql authSql = new AuthSql(this);
        authSql.queryContent(list);
        if (list.isEmpty()){
            llNoAuth.setVisibility(View.VISIBLE);
        }
        else {
            adapter.bindData(list);
            llNoAuth.setVisibility(View.GONE);
        }



    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAdd:
                sendCommand(KEY_RFID_KEY_C,1);
                showdialog();
                break;
            case R.id.btnEdit:
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
                    if (data_RFID_Key_R == 3){
                        dialog.dismiss();
                        AuthSql authSql = new AuthSql(this);
                        Auth auth = new Auth();
                        auth.setName(name);
                        authSql.saveContent(auth);
                        toast("添加权限成功");
                    }
                } else {
                    toast("备注不能为空");
                }
                break;

                //删除按钮：
            case R.id.btndelete:
                list.removeAll(adapter.selectedItems);
                adapter.selectedItems.clear();
                adapter.bindData(list);
                layout_delete.setVisibility(View.GONE);
                layout_select.setVisibility(View.GONE);
                layoutTitle.setVisibility(View.VISIBLE);
                adapter.isShow = false;
                break;
            case R.id.tvSelectAll:
                System.out.println(list.size());
                adapter.selectedItems.clear();

                for (Auth d:list){
                    d.setSelected(true);
                }
                adapter.notifyDataSetChanged();
                tvCancelAll.setVisibility(View.VISIBLE);
                tvSelectAll.setVisibility(View.GONE);
                break;
            case R.id.tvCancelAll:
                for (Auth d:adapter.selectedItems){
                    d.setSelected(false);
                }

                adapter.selectedItems.clear();
                adapter.notifyDataSetChanged();
                tvSelectAll.setVisibility(View.VISIBLE);
                tvCancelAll.setVisibility(View.GONE);
                break;
            case R.id.tvCancel:
                for (Auth d:adapter.selectedItems){
                    d.setSelected(false);
                }
                adapter.isShow = false;
                layout_delete.setVisibility(View.GONE);
                layout_select.setVisibility(View.GONE);
                layoutTitle.setVisibility(View.VISIBLE);
                adapter.selectedItems.clear();
                adapter.notifyDataSetChanged();
                break;

            //没有权限
            case R.id.llNoAuth:
                sendCommand(KEY_RFID_KEY_C,1);
                showdialog();
                break;
        }

    }

    private void toast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }


    private AlertDialog dialog;
    private Button btnCancel, btnConfirm;
    private EditText etDeviceName;
    private void showdialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.set_device_tag, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
        etDeviceName =(EditText) view.findViewById(R.id.etDeviceName);

        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
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

        initData();


    }
    /**
     * 发送指令,下发单个数据点的命令可以用这个方法
     *
     * <h3>注意</h3>
     * <p>
     * 下发多个数据点命令不能用这个方法多次调用，一次性多次调用这个方法会导致模组无法正确接收消息，参考方法内注释。
     * </p>
     *
     * @param key
     *            数据点对应的标识名
     * @param value
     *            需要改变的值
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
        Toast.makeText(this, "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
//        finish();
    }

    private void toastDeviceDisconnectAndExit() {
        Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
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
                if (dataKey.equals(KEY_RFID_KEY_R)) {
                    data_RFID_Key_R = (int) map.get(dataKey);
                }
            }
        }
    }


    @Override
    public void setCheckChanged() {
        tvCount.setText("已选择"+String.valueOf(adapter.selectedItems.size())+"个");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (adapter.isShow){
            for (Auth d:adapter.selectedItems){
                d.setSelected(false);
            }
            adapter.isShow = false;
            layout_delete.setVisibility(View.GONE);
            layout_select.setVisibility(View.GONE);
            layoutTitle.setVisibility(View.VISIBLE);
            adapter.selectedItems.clear();
            adapter.notifyDataSetChanged();
        }

    }
}
