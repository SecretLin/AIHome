package com.example.secret.ai10.DeviceModuel;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Secret on 2017/9/28.
 */

public class DeviceConfig {
    /** 设备列表 */
    public static List<GizWifiDevice> deviceslist = new ArrayList<GizWifiDevice>();

    private GizWifiSDKListener gizWifiSDKListener = new GizWifiSDKListener() {

        /** 用于设备列表 */
        public void didDiscovered(GizWifiErrorCode result, java.util.List<GizWifiDevice> deviceList) {

        }

        /** 用于用户匿名登录 */
        public void didUserLogin(GizWifiErrorCode result, java.lang.String uid, java.lang.String token) {

        }

        /** 用于设备解绑 */
        public void didUnbindDevice(GizWifiErrorCode result, java.lang.String did) {

        }

        /** 用于设备绑定 */
        public void didBindDevice(GizWifiErrorCode result, java.lang.String did) {

        }

        /** 用于设备绑定（旧） */
        public void didBindDevice(int error, String errorMessage, String did) {

        };

        /** 用于绑定推送 */
        public void didChannelIDBind(GizWifiErrorCode result) {

        }

    };
}
