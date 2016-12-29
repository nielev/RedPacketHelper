package com.neo.redpackethelper;

import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn;
    private boolean isServiceRunning;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn = (Button) findViewById(R.id.btn_service);
        mBtn.setOnClickListener(this);
        intent = new Intent(this, RobMoney.class);

    }

    @Override
    protected void onStart() {
        isServiceRunning = false;
        ActivityManager actvityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = actvityManager.getRunningServices(200);
        for (int i = 0; i < runningServices.size(); i++) {


            if("com.neo.redpackethelper.RobMoney".equals(runningServices.get(i).service.getClassName())){
                Log.i(RobMoney.TAG,""+i);
                mBtn.setText("停止服务");
                isServiceRunning = true;
            }
        }
        if(!isServiceRunning){
            mBtn.setText("开启服务");
        }
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_service:
                if(isServiceRunning){
                    mBtn.setText("开启服务");
                    stopService(intent);
                }else {
                    mBtn.setText("停止服务");
                    startService(intent);
                }
                break;
        }
    }
}
