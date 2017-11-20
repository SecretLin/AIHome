package com.example.secret.ai10.database;

/**
 * Created by Secret on 2017/9/30.
 */

public class CustomMode {
    private String modeName;
    private int isOpen;// 0关闭 1开启

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public int isOpen() {
        return isOpen;
    }

    public void setOpen(int open) {
        isOpen = open;
    }
}
