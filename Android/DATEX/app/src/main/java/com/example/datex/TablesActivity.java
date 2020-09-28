package com.example.datex;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class TablesActivity extends AppCompatActivity {


    int sampleTime =DATA.DEFAULT_SAMPLE_TIME;
    String ipAddress =DATA.DEFAULT_IP_ADDRESS;
    int sampleQuantity =DATA.DEFAULT_SAMPLE_QUANTITY;
    TextView virables[];
    TextView name_view;
    TextView value_view;
    TextView unit_view;
    int clearing_buffor_size;


    /* BEGIN request timer */
    private RequestQueue queue;
    private Timer requestTimer;
    private long requestTimerTimeStamp = 0;
    private long requestTimerPreviousTime = -1;
    private boolean requestTimerFirstRequest = true;
    private boolean requestTimerFirstRequestAfterStop;
    private TimerTask requestTimerTask;
    private final Handler handler = new Handler();
    /* END request timer */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);

        // get the Intent that started this Activity
        Intent intent = getIntent();

        // get the Bundle that stores the data of this Activity
        Bundle configBundle = intent.getExtras();
        ipAddress = configBundle.getString(DATA.CONFIG_IP_ADDRESS, DATA.DEFAULT_IP_ADDRESS);

        sampleTime = configBundle.getInt(DATA.CONFIG_SAMPLE_TIME, DATA.DEFAULT_SAMPLE_TIME);

        sampleQuantity = configBundle.getInt(DATA.CONFIG_SAMPLE_QUANTITY, DATA.DEFAULT_SAMPLE_QUANTITY);


        // Initialize Volley request queue
        queue = Volley.newRequestQueue(TablesActivity.this);

    }
    /**
     * @brief Create JSON file URL from IoT server IP.
     * @param ipAddress IP address (string)
     * @retval GET request URL
     */
    private String getURL(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.FILE_NAME_TABLES);
    }
    private String getURL_RPY(String ipAddress) {
        return ("http://" + ipAddress + "/" + DATA.RPY_FILE_NAME);
    }

    public void btns_onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtnTable: {
                startRequestTimer();
                break;
            }
            case R.id.stopBtnTable: {
                stopRequestTimerTask();
                break;
            }
            default: {
                // do nothing
            }
        }
    }
    private void errorHandling(int errorCode) {
        switch(errorCode) {
            case DATA.ERROR_TIME_STAMP:
                //textViewError.setText("ERR #1");
                Log.d("errorHandling", "Request time stamp error.");
                break;
            case DATA.ERROR_NAN_DATA:
                //textViewError.setText("ERR #2");
                Log.d("errorHandling", "Invalid JSON data.");
                break;
            case DATA.ERROR_RESPONSE:
                //textViewError.setText("ERR #3");
                Log.d("errorHandling", "GET request VolleyError.");
                break;
            default:
                //textViewError.setText("ERR ??");
                Log.d("errorHandling", "Unknown error.");
                break;
        }
    }

    /* @brief Starts new 'Timer' (if currently not exist) and schedules periodic task.
     */
    private void startRequestTimer() {
        if(requestTimer == null) {
            // set a new Timer
            requestTimer = new Timer();

            // initialize the TimerTask's job
            initializeRequestTimerTask();
            requestTimer.schedule(requestTimerTask, 0, sampleTime);

            // clear error message
            // textViewError.setText("");
        }
    }

    /* @brief Stops request timer (if currently exist)
     * and sets 'requestTimerFirstRequestAfterStop' flag.
     */
    private void stopRequestTimerTask() {
        // stop the timer, if it's not already null
        if (requestTimer != null) {
            requestTimer.cancel();
            requestTimer = null;
            requestTimerFirstRequestAfterStop = true;
        }
    }

    private String getRawDataFromResponse(String response, Integer item,String name) {
        JSONArray jObject;
       JSONObject x = null;
        String out = null;

        // Create generic JSON object form string
        try {
            jObject = new JSONArray(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return out;
        }

        // Read chart data form JSON object
        try {
            x = jObject.getJSONObject(item);
            out = x.get(name).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return out;
    }


    /**
     * @brief Initialize request timer period task with 'Handler' post method as 'sendGetRequest'.
     */
    private void initializeRequestTimerTask() {
        requestTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() { sendGetRequest();}
                });

            }
        };
    }

    /**
     * @brief Sending GET request to IoT server using 'Volley'.
     */
    private void sendGetRequest()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURL(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            responseHandling(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(DATA.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



    /**
     * @brief Validation of client-side time stamp based on 'SystemClock'.
     */
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

    /**
     * @brief GET response handling - chart data series updated with IoT server data.
     */

    private void responseHandling(String response) throws JSONException {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);
            double timeStamp = requestTimerTimeStamp / 1000.0; // [sec]

            // get raw data from JSON response
            String data1 = "error";
            String data2 = "error";
            String data3 = "error";


            Vector<String>buffor  = new Vector<String>();
            // set ip text in box


            int i = 0;


            while (true) {
                data1 = getRawDataFromResponse(response, i, "name");
                if (data1 == null) {
                    break;
                }
                data2 = getRawDataFromResponse(response, i + 1, "value");
                if (data2 == null) {
                    break;
                }
                data3 = getRawDataFromResponse(response, i + 2, "unit");
                if (data3 == null) {
                    break;
                }

                buffor.add(data1);
                buffor.add(data2);
                buffor.add(data3);


                i = i + 3;
            }

            int buffer_iteration = 0;
            int id = 1;
            //clearng textview
            while (buffer_iteration< clearing_buffor_size) {

                name_view = (TextView)findViewById(getResources().getIdentifier("name"+ id, "id", getPackageName()));
                name_view.setText(null);


                value_view= (TextView) findViewById(getResources().getIdentifier("value"+id, "id", getPackageName()));
                value_view.setText(null);


                unit_view = (TextView) findViewById(getResources().getIdentifier("unit"+id, "id", getPackageName()));
                unit_view.setText(null);
                id++;
                buffer_iteration = buffer_iteration + 3;
            }

            id=1;
            buffer_iteration = 0;
            //set data to textview
            while (buffer_iteration < buffor.size()) {

                String name = buffor.get(buffer_iteration);
                String value = buffor.get(buffer_iteration + 1);
                String unit = buffor.get(buffer_iteration + 2);
                name_view = (TextView)findViewById(getResources().getIdentifier("name"+ id, "id", getPackageName()));
                name_view.setText(name);


                value_view= (TextView) findViewById(getResources().getIdentifier("value"+id, "id", getPackageName()));
                value_view.setText(value);


                unit_view = (TextView) findViewById(getResources().getIdentifier("unit"+id, "id", getPackageName()));
                unit_view.setText(unit);
                id++;
                buffer_iteration = buffer_iteration + 3;
            }

            clearing_buffor_size=buffor.size();
            buffor.clear();


            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }

}