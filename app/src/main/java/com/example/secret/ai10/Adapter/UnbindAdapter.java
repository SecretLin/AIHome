package com.example.secret.ai10.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.secret.ai10.R;
import com.example.secret.ai10.database.BindDevice;
import com.gizwits.gizwifisdk.api.GizWifiDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Secret on 2017/11/17.
 */

public class UnbindAdapter extends RecyclerView.Adapter {

    private List<BindDevice> list = new ArrayList<>();
    private Context context;

    public boolean isShow = false;

    public List<BindDevice> selectedItems = new ArrayList<>();

    public boolean isCheckBoxPress = false;

    CheckBoxChangedListener listener;


    public void bindData(List<BindDevice> list){
        if (!list.isEmpty()){
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    public UnbindAdapter(Context context){
        this.context = context;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        UnbindHolder holder = new UnbindHolder(LayoutInflater.from(context).inflate(R.layout.item_unbind,parent,false));

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {

        final UnbindHolder myHolder = (UnbindHolder) holder;
        myHolder.tvDevicename.setText(list.get(position).getDevice().getRemark());
        if (list.get(position).isOnline()){
            myHolder.tvOn_Offline.setText("(在线)");
        }
        else {
            myHolder.tvOn_Offline.setText("(离线)");
        }


        if (list.get(position).isSelected()){
            myHolder.checkBox.setChecked(true);
            selectedItems.add(list.get(position));
            myHolder.checkBox.setBackground(context.getResources().getDrawable(R.drawable.check_box));
        }
        else {
            myHolder.checkBox.setChecked(false);
            myHolder.checkBox.setBackground(context.getResources().getDrawable(R.drawable.un_check));
            selectedItems.remove(list.get(position));
        }
        listener.setCheckChanged();


        if (isShow){
            myHolder.checkBox.setVisibility(View.VISIBLE);

            myHolder.checkBox.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    myHolder.checkBox.getParent().requestDisallowInterceptTouchEvent(true);
                    if (myHolder.checkBox.isChecked()){
                        if (event.getAction() == MotionEvent.ACTION_UP ){
                            System.out.println("I'm unCheck");
                            list.get(position).setSelected(false);
                            isCheckBoxPress = true;


                        }
                    }
                    else {
                        if (event.getAction() == MotionEvent.ACTION_UP ){
                            System.out.println("I'm Check~");
                            list.get(position).setSelected(true);
                            isCheckBoxPress = true;


                        }
                    }

                    return false;
                }
            });


        }
        else {
            myHolder.checkBox.setVisibility(View.GONE);
        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setListener(CheckBoxChangedListener listener){
        this.listener = listener;
    }


    class UnbindHolder extends RecyclerView.ViewHolder{

        private TextView tvDevicename,tvOn_Offline;
        private CheckBox checkBox;

        public UnbindHolder(View itemView) {
            super(itemView);

            tvDevicename = (TextView) itemView.findViewById(R.id.tvDeviceName);
            tvOn_Offline = (TextView) itemView.findViewById(R.id.on_offline);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }

    public interface CheckBoxChangedListener{
        void setCheckChanged();
    }
}
