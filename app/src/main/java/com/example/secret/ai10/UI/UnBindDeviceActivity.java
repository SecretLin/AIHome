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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.secret.ai10.Adapter.UnbindAdapter;
import com.example.secret.ai10.ConfigModule.GosAirlinkChooseDeviceWorkWiFiActivity;
import com.example.secret.ai10.DeviceModuel.ControlDeviceActivity;
import com.example.secret.ai10.DeviceModuel.DeviceConfig;
import com.example.secret.ai10.R;
import com.example.secret.ai10.database.BindDevice;
import com.example.secret.ai10.utils.RecyclerItemClickListener;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;

import java.util.ArrayList;
import java.util.List;

public class UnBindDeviceActivity extends Activity implements UnbindAdapter.CheckBoxChangedListener {


    private RecyclerView rv;
    private UnbindAdapter adapter;

    private boolean isLongClick = false;
    private RelativeLayout layout_select;
    private LinearLayout layout_delete,btnDelete;
    RelativeLayout layoutTitle;
    public TextView tvCount,tvCancel,tvSelectAll,tvCancelAll,tvEdit;

    String uid,token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_un_bind_device);

        uid = getIntent().getStringExtra("uid").trim();
        token = getIntent().getStringExtra("token").trim();

        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvSelectAll = (TextView) findViewById(R.id.tvSelectAll);
        tvCancelAll = (TextView) findViewById(R.id.tvCancelAll);
        tvCount = (TextView) findViewById(R.id.tvCount);

        tvEdit =(TextView) findViewById(R.id.tvEdit);


        layout_delete = (LinearLayout) findViewById(R.id.layout_delete);
        layout_select = (RelativeLayout) findViewById(R.id.layout_select);
        layoutTitle = (RelativeLayout)findViewById(R.id.layoutTitle);
        btnDelete = (LinearLayout) findViewById(R.id.btndelete);
        rv = (RecyclerView) findViewById(R.id.rv);
        adapter = new UnbindAdapter(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        initDevices();
        adapter.setListener(this);

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLongClick = true;
                adapter.isShow = true;
                adapter.notifyDataSetChanged();

                layoutTitle.setVisibility(View.GONE);
                layout_delete.setVisibility(View.VISIBLE);
                layout_select.setVisibility(View.VISIBLE);

//                if (!adapter.isCheckBoxPress){
//
//                    devices.get(position).setSelected(true);
//                    adapter.notifyItemChanged(position);
//
//                    System.out.println("click~click~hhhh");
//                }
            }
        });
        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isLongClick){
                    if (devices.get(position).isSelected()){
                        devices.get(position).setSelected(false);
                    }else {
                        devices.get(position).setSelected(true);
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

                    devices.get(position).setSelected(true);
                    adapter.notifyItemChanged(position);

                    System.out.println("click~click~hhhh");
                }
            }
        }));
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BindDevice device : devices){
                    GizWifiDevice d = device.getDevice();
                    System.out.println("unbinding....."+uid+","+d.getDid());
                    GizWifiSDK.sharedInstance().unbindDevice(uid,token,d.getDid());
                }
                devices.removeAll(adapter.selectedItems);


                adapter.selectedItems.clear();
                adapter.notifyDataSetChanged();
                layout_delete.setVisibility(View.GONE);
                layout_select.setVisibility(View.GONE);
                layoutTitle.setVisibility(View.VISIBLE);
                adapter.isShow = false;

            }
        });

        tvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(devices.size());
                adapter.selectedItems.clear();

                for (BindDevice d:devices){
                    d.setSelected(true);
                }
                adapter.notifyDataSetChanged();
                tvCancelAll.setVisibility(View.VISIBLE);
                tvSelectAll.setVisibility(View.GONE);
            }
        });
        tvCancelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BindDevice d:adapter.selectedItems){
                    d.setSelected(false);
                }

                adapter.selectedItems.clear();
                adapter.notifyDataSetChanged();
                tvSelectAll.setVisibility(View.VISIBLE);
                tvCancelAll.setVisibility(View.GONE);
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BindDevice d:adapter.selectedItems){
                    d.setSelected(false);
                }
                adapter.isShow = false;
                layout_delete.setVisibility(View.GONE);
                layout_select.setVisibility(View.GONE);
                layoutTitle.setVisibility(View.VISIBLE);
                adapter.selectedItems.clear();
                adapter.notifyDataSetChanged();

            }
        });

    }
    List<BindDevice> devices = new ArrayList<>();

    private void initDevices() {

        List<GizWifiDevice> list = DeviceConfig.deviceslist;

        for (GizWifiDevice device : list){
            BindDevice bindDevice = new BindDevice();
            if (GizWifiDeviceNetStatus.GizDeviceOnline == device.getNetStatus()
                    || GizWifiDeviceNetStatus.GizDeviceControlled == device.getNetStatus()) {
                if (device.isBind()) {

                    bindDevice.setDevice(device);
                    bindDevice.setOnline(true);
                    devices.add(bindDevice);
                }
            }
            else {
                if (device.isBind()){
                    bindDevice.setDevice(device);
                    bindDevice.setOnline(false);
                    devices.add(bindDevice);
                }
            }

        }
        adapter.bindData(devices);


    }

    @Override
    public void setCheckChanged() {
        tvCount.setText("已选择"+String.valueOf(adapter.selectedItems.size())+"个");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (adapter.isShow){
            for (BindDevice d:adapter.selectedItems){
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
