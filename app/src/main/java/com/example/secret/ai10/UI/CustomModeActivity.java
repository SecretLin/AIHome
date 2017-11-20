package com.example.secret.ai10.UI;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secret.ai10.R;
import com.example.secret.ai10.DeviceModuel.fragment.Bedroom;
import com.example.secret.ai10.DeviceModuel.fragment.LivingRoom;
import com.example.secret.ai10.DeviceModuel.fragment.WC;
import com.example.secret.ai10.database.AuthSql;
import com.example.secret.ai10.database.CustomMode;

import java.util.ArrayList;
import java.util.List;

public class CustomModeActivity extends FragmentActivity implements View.OnClickListener {
    ViewPager vp;
    LinearLayout l1, l2, l3;
    ImageView iv1, iv2, iv3,btnCancel,btnConfirm;
    TextView tv1, tv2, tv3;
    List<Fragment> fragments;
    FragmentPagerAdapter pagerAdapter;
    Fragment fragment;
    int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_mode);

        init();
        initEvent();
        Selected(1);
    }
    private void init() {
        vp = (ViewPager) findViewById(R.id.vp);
        l1 = (LinearLayout) findViewById(R.id.layout_wc);
        l2 = (LinearLayout) findViewById(R.id.layout_livingroom);
        l3 = (LinearLayout) findViewById(R.id.layout_bedroom);
        iv1 = (ImageView) findViewById(R.id.ivBathtub);
        iv2 = (ImageView) findViewById(R.id.ivSofa);
        iv3 = (ImageView) findViewById(R.id.ivBed);
        tv1 = (TextView) findViewById(R.id.tvWC);
        tv2 = (TextView) findViewById(R.id.tvLivingroom);
        tv3 = (TextView) findViewById(R.id.tvBedroom);

        btnCancel = (ImageView) findViewById(R.id.btnCancel);
        btnConfirm = (ImageView) findViewById(R.id.btnConfirm);


        fragments = new ArrayList<>();
        Fragment livingFragment = new LivingRoom();
        Fragment bedFragment = new Bedroom();
        Fragment wcFragment = new WC();
        fragments.add(wcFragment);
        fragments.add(livingFragment);
        fragments.add(bedFragment);

        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //super.destroyItem(container,position,object);
            }

        };

        vp.setAdapter(pagerAdapter);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = vp.getCurrentItem();
                ImagePressed(currentItem);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void initEvent() {
        l1.setOnClickListener(this);
        l2.setOnClickListener(this);
        l3.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_wc:
                Selected(0);
                break;
            case R.id.layout_livingroom:
                Selected(1);
                break;
            case R.id.layout_bedroom:

                Selected(2);
                break;

            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btnConfirm:
                showDialog();

                break;

            case R.id.btnDialogCancel:
                dialog.dismiss();
                break;

            case R.id.btnDialogConfirm:
//                CustomMode customMode = new CustomMode();
//                customMode.setModeName(etModeName.getText().toString());
//                customMode.setOpen(0);
//                AuthSql authSql = new AuthSql(CustomModeActivity.this);
//                authSql.saveContent(customMode);
//                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
                break;
        }
    }
    private AlertDialog dialog;
    private EditText etModeName;
    private Button btnDialogCancel,btnDialogConfirm;
    private void showDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_custom_mode, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        etModeName = (EditText) view.findViewById(R.id.etModeName);
        btnDialogCancel = (Button) view.findViewById(R.id.btnDialogCancel);
        btnDialogConfirm = (Button) view.findViewById(R.id.btnDialogConfirm);

        etModeName.setOnClickListener(this);
        btnDialogCancel.setOnClickListener(this);
        btnDialogConfirm.setOnClickListener(this);
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }
    public void ReSetImage() {
        iv1.setImageResource(R.mipmap.bathtub_gray);
        iv2.setImageResource(R.mipmap.sofa_grey);
        iv3.setImageResource(R.mipmap.bed_gray);
    }
    public void Selected(int i) {

        System.out.println("i ========"+i);
        ImagePressed(i);
        vp.setCurrentItem(i);
    }

    public void ImagePressed(int i) {
        ReSetImage();
        switch (i) {
            case 0:
                iv1.setImageResource(R.mipmap.bathtub_blue);

                break;
            case 1:
                iv2.setImageResource(R.mipmap.sofa_blue);
                break;
            case 2:
                iv3.setImageResource(R.mipmap.bed_blue);
                break;
        }

    }
}
