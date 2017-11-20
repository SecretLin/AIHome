package com.example.secret.ai10.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.secret.ai10.Adapter.DeviceListAdapter;
import com.example.secret.ai10.ConfigModule.GosAirlinkChooseDeviceWorkWiFiActivity;
import com.example.secret.ai10.DeviceModuel.ControlDeviceActivity;
import com.example.secret.ai10.DeviceModuel.DeviceConfig;
import com.example.secret.ai10.R;
import com.example.secret.ai10.utils.RecyclerItemClickListener;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.tencent.tauth.AuthActivity;

import java.util.ArrayList;
import java.util.List;

public class DeviceListActivity extends Activity {

    private RecyclerView rv;
    private DeviceListAdapter adapter;
    private GizWifiDevice mDevice;
    private ScrollView llNoDevice;

    private TextView tvTitle;

    private String toActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_device_list);

        rv = (RecyclerView) findViewById(R.id.rv);
        adapter = new DeviceListAdapter(this);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("设备列表");

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));


        //没有设备的时候
        llNoDevice = (ScrollView) findViewById(R.id.llNoDevice);
        final Button btnNoDevice = (Button) findViewById(R.id.btnNoDevice);
        btnNoDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DeviceListActivity.this, GosAirlinkChooseDeviceWorkWiFiActivity.class);
                startActivity(intent1);
            }
        });
        toActivity = getIntent().getStringExtra("toActivity").trim();
        System.out.println("=======toActivity:"+toActivity);
        initDevices();



        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

               GizWifiDevice device = adapter.getItem(position);

               if (toActivity.equals("设备管理")){
                   Intent intent = new Intent(DeviceListActivity.this,ControlDeviceActivity.class);
                   Bundle bundle = new Bundle();
                   bundle.putParcelable("device",device);
                   intent.putExtras(bundle);
                   startActivity(intent);
               }
               else if (toActivity.equals("权限管理")){
                   Intent intent = new Intent(DeviceListActivity.this,AuthActivity.class);
                   Bundle bundle = new Bundle();
                   bundle.putParcelable("device",device);
                   intent.putExtras(bundle);
                   startActivity(intent);
               }
               else if (toActivity.equals("监测")){
                   Intent intent = new Intent(DeviceListActivity.this,MonitorActivity.class);
                   Bundle bundle = new Bundle();
                   bundle.putParcelable("device",device);
                   intent.putExtras(bundle);
                   startActivity(intent);
               }
               else if (toActivity.equals("常用")){
                   Intent intent = new Intent(DeviceListActivity.this,ConstantActivity.class);
                   Bundle bundle = new Bundle();
                   bundle.putParcelable("device",device);
                   intent.putExtras(bundle);
                   startActivity(intent);
               }



            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    private void initDevices() {

        List<GizWifiDevice> list = DeviceConfig.deviceslist;

        List<GizWifiDevice> onlineBind = new ArrayList<>();

        int count = 0;
        for (GizWifiDevice device : list){
            if (GizWifiDeviceNetStatus.GizDeviceOnline == device.getNetStatus()
                    || GizWifiDeviceNetStatus.GizDeviceControlled == device.getNetStatus()) {
                if (device.isBind()) {

                    count++;

                    onlineBind.add(device);
                }
            }

        }

        if (!onlineBind.isEmpty()){
            GizWifiDevice device = onlineBind.get(0);
            System.out.println("==========hhhhhhhh");
            adapter.bindData(onlineBind);
            llNoDevice.setVisibility(View.GONE);
            if (count==1){
                System.out.println("==========111111111111111");
                if (toActivity.equals("设备管理")){
                    Intent intent = new Intent(DeviceListActivity.this,ControlDeviceActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("device",device);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (toActivity.equals("权限管理")){
                    Intent intent = new Intent(DeviceListActivity.this,AuthActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("device",device);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (toActivity.equals("监测")){
                    Intent intent = new Intent(DeviceListActivity.this,MonitorActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("device",device);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (toActivity.equals("常用")){
                    Intent intent = new Intent(DeviceListActivity.this,ConstantActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("device",device);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                finish();
            }
            System.out.println("====device num:"+onlineBind.size());
        }


        else {
            System.out.println("============none");
            llNoDevice.setVisibility(View.VISIBLE);
        }


    }
}
