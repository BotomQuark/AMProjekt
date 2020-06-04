package com.example.datex;


import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Double.isNaN;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    /* BEGIN config data */
    private String ipAddress = DATA.DEFAULT_IP_ADDRESS;
    private int sampleTime = DATA.DEFAULT_SAMPLE_TIME;
    /* END config data */

    /* BEGIN widgets */
    private TextView textViewIP;
    private TextView textViewSampleTime;
    private TextView textViewError;
    /* END widgets */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void btns_onClick(View v) {
        switch (v.getId()) {
            case R.id.button_Settings: {
                configurebutton_settings();
                // openConfig();
                break;
            }
/*            case R.id.button_LED: {

                break;
            }*/
            case R.id.button_data: {
                configurebutton_data();
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
        if ((requestCode == DATA.REQUEST_CODE_CONFIG) && (resultCode == RESULT_OK)) {

            // IoT server IP address
            ipAddress = dataIntent.getStringExtra(DATA.CONFIG_IP_ADDRESS);


            // Sample time (ms)
            String sampleTimeText = dataIntent.getStringExtra(DATA.CONFIG_SAMPLE_TIME);
            sampleTime = Integer.parseInt(sampleTimeText);

        }
    }



    private void configurebutton_data() {
                Intent openDataIntent = new Intent(this, ChartsActivity.class);
                Bundle configBundle = new Bundle();
                configBundle.putInt(DATA.CONFIG_SAMPLE_TIME, sampleTime);
                openDataIntent.putExtras(configBundle);
                startActivity(openDataIntent);
    }

    private void configurebutton_settings() {
                Intent openConfigIntent = new Intent(this, ConfigActivity.class);
                Bundle configBundle = new Bundle();
                configBundle.putString(DATA.CONFIG_IP_ADDRESS, ipAddress);
                configBundle.putInt(DATA.CONFIG_SAMPLE_TIME, sampleTime);
                openConfigIntent.putExtras(configBundle);
                startActivityForResult(openConfigIntent, DATA.REQUEST_CODE_CONFIG);


    }


}


