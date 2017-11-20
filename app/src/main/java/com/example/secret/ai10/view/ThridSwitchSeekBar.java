package com.example.secret.ai10.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by Secret on 2017/11/15.
 */

public class ThridSwitchSeekBar extends android.support.v7.widget.AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {


    private SeekTouchListener touchListener;


    /**
     * 新的Progress值,初始值是5 为了解决左右显示不全问题
     */
    private int newProgress = 50;


    public ThridSwitchSeekBar(Context context) {
        super(context);
        setOnSeekBarChangeListener(this);
        setProgress(50);//三挡开关,为了解决thumb显示不全，所以设为5
    }

    public ThridSwitchSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnSeekBarChangeListener(this);
        setProgress(50);
    }

    public ThridSwitchSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnSeekBarChangeListener(this);
        setProgress(50);
    }

    /**
     * 进度值改变时,滑动的时候设置进度值
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        newProgress = progress;

    }

    /**
     * 进度值开始滑动时
     *
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 进度值停止滑动时
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        if (newProgress < 30) {//如果progress<25,因为左右缩进了5%,所以加了5%,为30%则滑到第一档
            newProgress = 10;
            setProgress(17);
            if (touchListener != null) {
                touchListener.touchTop(seekBar);
            }

        } else if (newProgress >=70) {//如果progress>75,因为左右缩进了5%,所以减了5%,为70%则滑到第三档
            newProgress = 90;
            setProgress(83);
            if (touchListener != null) {
                touchListener.touchEnd(seekBar);
            }

        } else {//到中档
            newProgress = 50;
            setProgress(50);

            if (touchListener != null) {
                touchListener.touchMiddle(seekBar);
            }
        }


    }

    /**
     * 滑动监听
     */
    public interface SeekTouchListener {
        void touchTop(SeekBar seekBar);//滑到第一档

        void touchMiddle(SeekBar seekBar);//滑到中档

        void touchEnd(SeekBar seekBar);//滑到第三档

    }


    public void setSeekTouchListenr(SeekTouchListener touchListenr) {
        this.touchListener = touchListenr;

    }


}