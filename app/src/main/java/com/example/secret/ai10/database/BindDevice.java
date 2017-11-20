package com.example.secret.ai10.database;

import com.gizwits.gizwifisdk.api.GizWifiDevice;

/**
 * Created by Secret on 2017/11/19.
 */

public class BindDevice {

    private GizWifiDevice device;
    private boolean isOnline;
    private boolean isSelected;
    public GizWifiDevice getDevice() {
        return device;
    }

    public void setDevice(GizWifiDevice device) {
        this.device = device;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
