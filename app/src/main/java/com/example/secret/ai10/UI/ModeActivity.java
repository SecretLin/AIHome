package com.example.secret.ai10.UI;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.secret.ai10.R;
import com.example.secret.ai10.UI.fragment.DeviceListFragment;

public class ModeActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mode);
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.framelayout,new DeviceListFragment()).
                commit();


    }



}
