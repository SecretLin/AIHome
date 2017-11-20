package com.example.secret.ai10.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.secret.ai10.R;
import com.example.secret.ai10.database.Auth;
import com.example.secret.ai10.database.BindDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Secret on 2017/11/20.
 */

public class AuthAdapter extends RecyclerView.Adapter {

    private List<Auth> list = new ArrayList<>();
    private Context context;

    public boolean isShow = false;

    public List<Auth> selectedItems = new ArrayList<>();

    public boolean isCheckBoxPress = false;

    AuthAdapter.CheckBoxChangedListener listener;

    public void bindData(List<Auth> list){
        if (!list.isEmpty()){
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    public AuthAdapter(Context context){
        this.context = context;
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
    public int getItemCount() {
        return list.size();
    }

    class AuthHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private CheckBox checkBox;

        public AuthHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }
    public interface CheckBoxChangedListener{
        void setCheckChanged();
    }
    public void setListener(AuthAdapter.CheckBoxChangedListener listener){
        this.listener = listener;
    }
}
