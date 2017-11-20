package com.example.secret.ai10.UI.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.secret.ai10.Adapter.DeviceListAdapter;
import com.example.secret.ai10.ConfigModule.GosAirlinkChooseDeviceWorkWiFiActivity;
import com.example.secret.ai10.DeviceModuel.DeviceConfig;
import com.example.secret.ai10.R;
import com.example.secret.ai10.utils.RecyclerItemClickListener;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Secret on 2017/11/16.
 */

public class DeviceListFragment extends Fragment {


    private RecyclerView rv;
    private DeviceListAdapter adapter;
    private GizWifiDevice mDevice;
    private ScrollView llNoDevice;

    private TextView tvTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_devicelist, container, false);


        rv = (RecyclerView) view.findViewById(R.id.rv);
        adapter = new DeviceListAdapter(getContext());

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("设备列表");

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));


        //没有设备的时候
        llNoDevice = (ScrollView) view.findViewById(R.id.llNoDevice);
        Button btnNoDevice = (Button) view.findViewById(R.id.btnNoDevice);
        btnNoDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext(), GosAirlinkChooseDeviceWorkWiFiActivity.class);
                startActivity(intent1);
            }
        });

        initDevices();

        rv.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                ModeFragment modeFragment = ModeFragment.newInstance(adapter.getItem(position));

                getFragmentManager().beginTransaction().hide(DeviceListFragment.this)
                        .add(R.id.framelayout, modeFragment)
                        .addToBackStack("")
                        .commit();

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        return view;
    }

    private void initDevices() {

        List<GizWifiDevice> list = DeviceConfig.deviceslist;

        List<GizWifiDevice> onlineBind = new ArrayList<>();

        int count = 0;
        for (GizWifiDevice device : list) {
            if (GizWifiDeviceNetStatus.GizDeviceOnline == device.getNetStatus()
                    || GizWifiDeviceNetStatus.GizDeviceControlled == device.getNetStatus()) {
                if (device.isBind()) {

                    count++;

                    onlineBind.add(device);
                }
            }


        }
        System.out.println("======count:" + count);
        if (!onlineBind.isEmpty()) {
            adapter.bindData(onlineBind);
            GizWifiDevice device = onlineBind.get(0);
            if (count == 1) {

                ModeFragment modeFragment = ModeFragment.newInstance(device);

                getFragmentManager().beginTransaction().hide(DeviceListFragment.this)
                        .add(R.id.framelayout, modeFragment)
                        .commit();

            }

        }
        else {
            llNoDevice.setVisibility(View.VISIBLE);

        }

    }
}
