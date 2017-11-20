package com.example.secret.ai10.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.secret.ai10.R;
import com.gizwits.gizwifisdk.api.GizWifiDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Secret on 2017/11/16.
 */

public class DeviceListAdapter extends RecyclerView.Adapter {

    private List<GizWifiDevice> list = new ArrayList<>();
    private Context context;
    public boolean isOffline = false;

    public void bindData(List<GizWifiDevice> list){
        if (!list.isEmpty()){
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    public DeviceListAdapter(Context context){
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        DeviceListHolder holder = new DeviceListHolder(LayoutInflater.from(context).inflate(R.layout.item_devicelist,parent,false));

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        DeviceListHolder deviceListHolder = (DeviceListHolder) holder;
        deviceListHolder.tvName.setText(list.get(position).getRemark());
        if (isOffline){
            deviceListHolder.btnMore.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public GizWifiDevice getItem(int position){
        return list.get(position);
    }



    class DeviceListHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        private ImageView btnMore;
        public DeviceListHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvDeviceName);
            btnMore =(ImageView) itemView.findViewById(R.id.btnMore);
        }
    }
}
