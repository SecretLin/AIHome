package com.example.secret.ai10.UI;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class OfflineDeviceActivity extends Activity {

    private RecyclerView rv;
    private DeviceListAdapter adapter;
    private GizWifiDevice mDevice;


    private TextView tvTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_offline_device);

        rv = (RecyclerView) findViewById(R.id.rv);
        adapter = new DeviceListAdapter(this);
        adapter.isOffline = true;

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("离线设备");

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));



        initDevices();

//        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                GizWifiDevice device = adapter.getItem(position);
//                Intent intent = new Intent(OfflineDeviceActivity.this,ControlDeviceActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("device",device);
//                intent.putExtras(bundle);
//                startActivity(intent);
//
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//        }));

    }

    private void initDevices() {

        List<GizWifiDevice> list = DeviceConfig.deviceslist;

        List<GizWifiDevice> offlineBind = new ArrayList<>();

        for (GizWifiDevice device : list){
            if (GizWifiDeviceNetStatus.GizDeviceOffline == device.getNetStatus()
                    ) {
                if (device.isBind()) {

                    offlineBind.add(device);
                }
            }

            if (!offlineBind.isEmpty()){
                adapter.bindData(offlineBind);
            }

        }


    }
}
