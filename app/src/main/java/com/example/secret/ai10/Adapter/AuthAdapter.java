package com.example.secret.ai10.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secret.ai10.R;
import com.example.secret.ai10.database.Auth;
import com.example.secret.ai10.database.BindDevice;
import com.gizwits.gizwifisdk.api.GizWifiDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Secret on 2017/11/20.
 */

public class AuthAdapter extends RecyclerView.Adapter {

    private List<Auth> list = new ArrayList<>();
    private Context context;

    public boolean isShow = false;

    public List<Auth> selectedItems = new ArrayList<>();

    public boolean isCheckBoxPress = false;

    CheckBoxChangedListener listener;
    GizWifiDevice mDevice;
    // 数据点"RFID开门权限管理"对应的标识名
    protected static final String KEY_RFID_KEY_C = "RFID_Key_C";
    public void bindData(List<Auth> list){
        if (!list.isEmpty()){
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    public boolean isDeleting;

    public AuthAdapter(Context context,GizWifiDevice device){
        this.context = context;
        mDevice = device;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AuthHolder holder = new AuthHolder(LayoutInflater.from(context).inflate(R.layout.item_auth,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final AuthHolder myHolder = (AuthHolder) holder;
        myHolder.tvName.setText(list.get(position).getName());


//        if (list.get(position).isSelected()==1){
//            myHolder.checkBox.setChecked(true);
//            selectedItems.add(list.get(position));
//            myHolder.checkBox.setBackground(context.getResources().getDrawable(R.drawable.check_box));
//        }
//        else {
//            myHolder.checkBox.setChecked(false);
//            myHolder.checkBox.setBackground(context.getResources().getDrawable(R.drawable.un_check));
//            selectedItems.remove(list.get(position));
//        }

//        listener.setCheckChanged();


        if (isShow){
            myHolder.btnDelete.setVisibility(View.VISIBLE);
            myHolder.btnDelete.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP){
                        myHolder.btnDelete.getParent().requestDisallowInterceptTouchEvent(true);
                        if (isDeleting){
                            sendCommand(KEY_RFID_KEY_C,2);
                        }
                        else {
                            Toast.makeText(context,"请将卡放在感应器前面",Toast.LENGTH_SHORT).show();
                        }

                    }

                    return false;
                }
            });
//            myHolder.checkBox.setVisibility(View.VISIBLE);
//
//            myHolder.checkBox.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    myHolder.checkBox.getParent().requestDisallowInterceptTouchEvent(true);
//                    if (myHolder.checkBox.isChecked()){
//                        if (event.getAction() == MotionEvent.ACTION_UP ){
//                            System.out.println("I'm unCheck");
//                            list.get(position).setSelected(0);
//                            isCheckBoxPress = true;
//
//
//                        }
//                    }
//                    else {
//                        if (event.getAction() == MotionEvent.ACTION_UP ){
//                            System.out.println("I'm Check~");
//                            list.get(position).setSelected(1);
//                            isCheckBoxPress = true;
//
//
//                        }
//                    }
//
//                    return false;
//                }
//            });


        }
//        else {
//            myHolder.checkBox.setVisibility(View.GONE);
//        }


    }
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
    @Override
    public int getItemCount() {
        return list.size();
    }

    class AuthHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView btnDelete;

        public AuthHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            btnDelete = (TextView) itemView.findViewById(R.id.btndelete);

        }
    }
    public interface CheckBoxChangedListener{
        void setCheckChanged();
    }
    public void setListener(AuthAdapter.CheckBoxChangedListener listener){
        this.listener = listener;
    }
}
