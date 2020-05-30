package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class ThpChartActivity extends AppCompatActivity {

    private static final String TAG = ThpChartActivity.class.getSimpleName();

    private String ipAddress = COMMON.DEFAULT_IP_ADDRESS;
    private int sampleTime = COMMON.DEFAULT_SAMPLE_TIME;
    private TextView textViewIP;
    private TextView textViewSampleTime;
    private TextView textViewError;
    private TextView textViewConnection;

    private double tRawData;
    private double pRawData;
    private double hRawData;

    private final int dataChartMaxDataPointsNumber = 1000;
    private GraphView tChartView;
    private LineGraphSeries<DataPoint> tDataSeries;
    private GraphView pChartView;
    private LineGraphSeries<DataPoint> pDataSeries;
    private GraphView hChartView;
    private LineGraphSeries<DataPoint> hDataSeries;

    private final double tChartMinX=0;
    private final double tChartMaxX=10;
    private final double tChartMinY=-30;
    private final double tChartMaxY=105;

    private final double pChartMinX=0;
    private final double pChartMaxX=10;
    private final double pChartMinY=260;
    private final double pChartMaxY=1260;

    private final double hChartMinX=0;
    private final double hChartMaxX=10;
    private final double hChartMinY=0;
    private final double hChartMaxY=100;

    private RequestQueue queue;
    private Timer requestTimer;
    private long requestTimerTimeStamp = 0;
    private long requestTimerPreviousTime = -1;
    private boolean requestTimerFirstRequest = true;
    private boolean requestTimerFirstRequestAfterStop;
    private TimerTask requestTimerTask;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thp_chart);

        Intent intent = getIntent();

        Bundle configBundle = intent.getExtras();

        /*BEGIN TEXT INIT*/
        textViewIP=findViewById(R.id.textViewIP);
        ipAddress = configBundle.getString(COMMON.CONFIG_IP_ADDRESS, COMMON.DEFAULT_IP_ADDRESS);
        textViewIP.setText(getIpDisplayText(ipAddress));

        textViewSampleTime=findViewById(R.id.textViewSampleTime);
        sampleTime = configBundle.getInt(COMMON.CONFIG_SAMPLE_TIME, COMMON.DEFAULT_SAMPLE_TIME);
        textViewSampleTime.setText(getSampleTimeDisplayTest(Integer.toString(sampleTime)));

        textViewError=findViewById(R.id.textViewError);
        textViewError.setText("");

        textViewConnection=findViewById(R.id.ThpConnectTextView);
        /*END TEXT INIT*/

        /*BEGIN CHART INIT*/
        chartInit();
        /*END CHART INIT*/

        //Init valley request queue
        queue = Volley.newRequestQueue(ThpChartActivity.this);

        startRequestTimer();
    }


    @Override
    public void onBackPressed() {
        stopRequestTimerTask();
        finish();
    }

    private String getIpDisplayText(String ipAddress) {
        return "IP:"+ipAddress;
    }
    private String getSampleTimeDisplayTest(String toString) {
        return "SampleTime:"+toString;
    }

    private void errorHandling(int errorCode) {
        switch(errorCode) {
            case COMMON.ERROR_TIME_STAMP:
                textViewError.setText("ERR #1");
                Log.d("errorHandling", "Request time stamp error.");
                break;
            case COMMON.ERROR_NAN_DATA:
                textViewError.setText("ERR #2");
                Log.d("errorHandling", "Invalid JSON data.");
                break;
            case COMMON.ERROR_RESPONSE:
                textViewError.setText("ERR #3");
                Log.d("errorHandling", "GET request VolleyError.");
                break;
            default:
                textViewError.setText("ERR ??");
                Log.d("errorHandling", "Unknown error.");
                break;
        }
    }

    private void startRequestTimer() {
        if(requestTimer == null) {
            // set a new Timer
            requestTimer = new Timer();

            // initialize the TimerTask's job
            initializeRequestTimerTask();
            requestTimer.schedule(requestTimerTask, 0, sampleTime);

            // clear error message
            textViewError.setText("");
        }
    }

    private String getURL(String ip) {
        return ("http://" + ip + "/" + COMMON.THP_FILE_NAME);
    }

    private void stopRequestTimerTask() {
        // stop the timer, if it's not already null
        if (requestTimer != null) {
            requestTimer.cancel();
            requestTimer = null;
            requestTimerFirstRequestAfterStop = true;
        }
    }

    private void initializeRequestTimerTask() {
        requestTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() { sendGetRequest(); }
                });
            }
        };
    }

    private void sendGetRequest()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURL(ipAddress);
        textViewConnection.setText("Connecting to:"+url);
        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandling(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(COMMON.ERROR_RESPONSE); Log.e(TAG, "Error at StringRequest: " + error.getMessage()); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private long getValidTimeStampIncrease(long currentTime)
    {
        // Right after start remember current time and return 0
        if(requestTimerFirstRequest)
        {
            requestTimerPreviousTime = currentTime;
            requestTimerFirstRequest = false;
            return 0;
        }

        // After each stop return value not greater than sample time
        // to avoid "holes" in the plot
        if(requestTimerFirstRequestAfterStop)
        {
            if((currentTime - requestTimerPreviousTime) > sampleTime)
                requestTimerPreviousTime = currentTime - sampleTime;

            requestTimerFirstRequestAfterStop = false;
        }

        // If time difference is equal zero after start
        // return sample time
        if((currentTime - requestTimerPreviousTime) == 0)
            return sampleTime;

        // Return time difference between current and previous request
        return (currentTime - requestTimerPreviousTime);
    }


    private void responseHandling(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
            tRawData = getRawDataFromResponse(response, "temperature");
            hRawData = getRawDataFromResponse(response, "humidity");
            pRawData = getRawDataFromResponse(response, "pressure");

            // update chart
            if (isNaN(tRawData) || isNaN(hRawData) || isNaN(pRawData)) {
                errorHandling(COMMON.ERROR_NAN_DATA);

            } else {

                // update plot series
                double TimeStamp = requestTimerTimeStamp / 1000.0; // [sec]
                boolean tScrollGraph = (TimeStamp > tChartMaxX);
                tDataSeries.appendData(new DataPoint(TimeStamp, tRawData), tScrollGraph, dataChartMaxDataPointsNumber);


                boolean hScrollGraph = (TimeStamp > hChartMaxX);
                hDataSeries.appendData(new DataPoint(TimeStamp, hRawData), hScrollGraph, dataChartMaxDataPointsNumber);


                boolean pScrollGraph = (TimeStamp > pChartMaxX);
                pDataSeries.appendData(new DataPoint(TimeStamp, pRawData), pScrollGraph, dataChartMaxDataPointsNumber);

                // refresh chart
                tChartView.onDataChanged(true, true);
                hChartView.onDataChanged(true, true);
                pChartView.onDataChanged(true, true);
            }

            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }

    private double getRawDataFromResponse(String response, String key){
        JSONObject jObject;
        double x =Double.NaN;
        try {
            jObject =  new JSONObject(response);
        } catch(JSONException e) {
            e.printStackTrace();
            return x;
        }

        try {
            x=(double)jObject.get(key);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return x;
    }

    private void chartInit(){
        tChartView = (GraphView)findViewById(R.id.tChart);
        pChartView = (GraphView)findViewById(R.id.pChart);
        hChartView = (GraphView)findViewById(R.id.hChart);

        tDataSeries = new LineGraphSeries<>(new DataPoint[]{});
        pDataSeries = new LineGraphSeries<>(new DataPoint[]{});
        hDataSeries = new LineGraphSeries<>(new DataPoint[]{});

        tChartView.addSeries(tDataSeries);
        pChartView.addSeries(pDataSeries);
        hChartView.addSeries(hDataSeries);

        tChartView.getViewport().setXAxisBoundsManual(true);
        tChartView.getViewport().setYAxisBoundsManual(true);
        pChartView.getViewport().setXAxisBoundsManual(true);
        pChartView.getViewport().setYAxisBoundsManual(true);
        hChartView.getViewport().setXAxisBoundsManual(true);
        hChartView.getViewport().setYAxisBoundsManual(true);

        tChartView.getViewport().setMinX(tChartMinX);
        tChartView.getViewport().setMaxX(tChartMaxX);
        tChartView.getViewport().setMinY(tChartMinY);
        tChartView.getViewport().setMaxY(tChartMaxY);

        pChartView.getViewport().setMinX(pChartMinX);
        pChartView.getViewport().setMaxX(pChartMaxX);
        pChartView.getViewport().setMinY(pChartMinY);
        pChartView.getViewport().setMaxY(pChartMaxY);

        hChartView.getViewport().setMinX(hChartMinX);
        hChartView.getViewport().setMaxX(hChartMaxX);
        hChartView.getViewport().setMinY(hChartMinY);
        hChartView.getViewport().setMaxY(hChartMaxY);
    }

}
