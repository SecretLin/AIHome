package com.example.secret.ai10;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.example.secret.ai10.Service.LocationService;

public class GosApplication extends Application {

	public static int flag = 0;

	public LocationService locationService;
	public Vibrator mVibrator;

	private static GosApplication INSTANCE;
	public static GosApplication INSTANCE(){
		return INSTANCE;
	}
	private void setInstance(GosApplication app) {
		setBmobIMApplication(app);
	}
	private static void setBmobIMApplication(GosApplication a) {
		GosApplication.INSTANCE = a;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		/***
		 *
		 * 初始化定位sdk，建议在Application中创建
		 */
		locationService = new LocationService(getApplicationContext());
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
		SDKInitializer.initialize(getApplicationContext());

		setInstance(this);


	}

}
