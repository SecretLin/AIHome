package com.example.secret.ai10.Adapter;

import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.secret.ai10.R;
import com.example.secret.ai10.UI.ModeActivity;
import com.example.secret.ai10.UI.fragment.ModeFragment;
import com.example.secret.ai10.database.CustomMode;
import com.gizwits.gizwifisdk.api.GizWifiDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Secret on 2017/9/30.
 */

public class ModeAdapter extends RecyclerView.Adapter {

    private List<CustomMode> list = new ArrayList<>();
    private Context context;
    private ModeFragment fragment;
    protected static final String KEY_HOMEMODE = "HomeMode";

    private GizWifiDevice mdevice;

    public int isSelected = 0;
    public void bindData(List<CustomMode> list){
        if (!list.isEmpty()){
            this.list.clear();
            this.list.addAll(list);
        }
    }
    public ModeAdapter(Context context, GizWifiDevice device){
        this.context = context;

        mdevice = device;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModeHolder(LayoutInflater.from(context).inflate(R.layout.item_mode,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ModeHolder modeHolder = (ModeHolder) holder;
        modeHolder.tvModeName.setText(list.get(position).getModeName());
        if (isSelected == position){
            modeHolder.sw_onoff.setChecked(true);
        }else {
            modeHolder.sw_onoff.setChecked(false);
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
    public void sendCommand(String key, Object value) {
        if (value == null) {
            return;
        }
        int sn = 5;
        ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
        hashMap.put(key, value);
        // 同时下发多个数据点需要一次性在map中放置全部需要控制的key，value值
        // hashMap.put(key2, value2);
        // hashMap.put(key3, value3);
        mdevice.write(hashMap, sn);
        Log.i("liang", "下发命令：" + hashMap.toString());
    }
    @Override
    public int getItemCount() {
        return list.size();
    }




    class ModeHolder extends RecyclerView.ViewHolder {

        public TextView tvModeName;
        public Switch sw_onoff;

        public ModeHolder(View itemView) {
            super(itemView);

            tvModeName = (TextView) itemView.findViewById(R.id.tvModeName);
            sw_onoff = (Switch) itemView.findViewById(R.id.sw_OnOff);


            sw_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        sendCommand(KEY_HOMEMODE,getAdapterPosition());
                        isSelected = getAdapterPosition();
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        };
                        handler.post(runnable);
                    }
                }
            });
        }
    }
}
