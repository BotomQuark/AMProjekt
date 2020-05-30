package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private String ipAddress = COMMON.DEFAULT_IP_ADDRESS;
    private int sampleTime = COMMON.DEFAULT_SAMPLE_TIME;
    private Intent dataIntent;

    @Override
    protected void onCreate
            (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btns_onClick(View v) {
        switch (v.getId()) {
            case R.id.menuData: {
                dataIntent=new Intent(this, DataTableActivity.class);
                getDataIntent();
                startActivity(dataIntent);
                break;
            }
            case R.id.menuChart: {
                dataIntent=new Intent(this, ThpChartActivity.class);
                getDataIntent();
                startActivity(dataIntent);
                break;
            }
            case R.id.menuChart2: {
                dataIntent=new Intent(this, RpyChartActivity.class);
                getDataIntent();
                startActivity(dataIntent);
                break;
            }
            case R.id.menuJoystickChart: {
                dataIntent=new Intent(this, JoystickChartActivity.class);
                getDataIntent();
                startActivity(dataIntent);
                break;
            }
            case R.id.menuLed: {
                dataIntent=new Intent(this, LedMatrixActivity.class);
                getDataIntent();
                startActivity(dataIntent);
                break;
            }
            case R.id.menuOptions: {
                dataIntent=new Intent(this, ConfigActivity.class);
                getDataIntent();
                startActivityForResult(dataIntent, COMMON.REQUEST_CODE_CONFIG);
                break;
            }
            default: {
                // do nothing
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        if ((requestCode == COMMON.REQUEST_CODE_CONFIG) && (resultCode == RESULT_OK)) {

            // IoT server IP address
            ipAddress = dataIntent.getStringExtra(COMMON.CONFIG_IP_ADDRESS);

            // Sample time (ms)
            String sampleTimeText = dataIntent.getStringExtra(COMMON.CONFIG_SAMPLE_TIME);
            sampleTime = Integer.parseInt(sampleTimeText);
        }
    }

    private void getDataIntent(){
        Bundle dataBundle = new Bundle();
        dataBundle.putString(COMMON.CONFIG_IP_ADDRESS, ipAddress);
        dataBundle.putInt(COMMON.CONFIG_SAMPLE_TIME, sampleTime);
        dataIntent.putExtra(COMMON.CONFIG_IP_ADDRESS, ipAddress);
        dataIntent.putExtra(COMMON.CONFIG_SAMPLE_TIME, Integer.toString(sampleTime));
        dataIntent.putExtras(dataBundle);
    }
    public void  btn_GoToOptions() {
        startActivity(new Intent(this, ConfigActivity.class));
    }

}
