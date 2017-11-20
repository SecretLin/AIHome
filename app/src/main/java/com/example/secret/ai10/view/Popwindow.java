package com.example.secret.ai10.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.secret.ai10.R;

/**
 * Created by Secret on 2017/9/26.
 */

public class Popwindow extends PopupWindow{
    private View mainView;
    private TextView tvScan, tvAdd;

    public Popwindow(Activity paramActivity, View.OnClickListener paramOnClickListener, int paramInt1, int paramInt2){
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(R.layout.popupwindow, null);
        //分享布局
        tvScan = (TextView) mainView.findViewById(R.id.scanQRCode);
        //复制布局
        tvAdd = (TextView) mainView.findViewById(R.id.addNewDevice);
//        //设置每个子布局的事件监听器
        if (paramOnClickListener != null){
            tvScan.setOnClickListener(paramOnClickListener);
            tvAdd.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);
        //设置显示隐藏动画
        setAnimationStyle(R.style.AnimationPreview);
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));
    }
}