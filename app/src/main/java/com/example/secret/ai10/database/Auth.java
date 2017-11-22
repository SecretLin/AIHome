package com.example.secret.ai10.database;

import com.gizwits.gizwifisdk.api.GizWifiDevice;

/**
 * Created by Secret on 2017/11/20.
 */

public class Auth {

    private String name;
    private int isSelected;
    private String deviceName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int isSelected() {
        return isSelected;
    }

    public void setSelected(int selected) {
        isSelected = selected;
    }


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
