package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class LedMatrixActivity extends AppCompatActivity {

    /*BEGIN VARIABLES*/
    //Ip address
    private String ipAddress = COMMON.DEFAULT_IP_ADDRESS;

    //Widgets
    SeekBar seekBarR, seekBarG, seekBarB;
    View colourPreviewView;
    TextView ConnectTextView;

    //Colour variables
    private int alpha, red, green, blue, currentColour;
    int ledDefaultColour;
    Vector ledDefaultColourVec;
    Integer[][][] ledColours = new Integer[8][8][3]; //[row][column][colour selection]

    //Intent and Bundle variable
    Intent intent;
    Bundle configBundle;

    //Volley request variables
    private RequestQueue queue;
    Map<String, String> paramClear = new HashMap<String, String>();
    /*END VARIABLES*/


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.led_matrix);

        Init();
    }

    /**
     * @brief initialization of TextViews, EditTexts and the data given through Extras in Intent
     */
    private void Init(){
        intent = getIntent();
        configBundle = intent.getExtras();

        /*BEGIN TEXT INIT*/
        ipAddress = configBundle.getString(COMMON.CONFIG_IP_ADDRESS, COMMON.DEFAULT_IP_ADDRESS);

        ConnectTextView=findViewById(R.id.LedConnectTextView);
        ConnectTextView.setText("Connecting to: "+getURL(ipAddress));
        /*END TEXT INIT*/

        /*BEGIN COLOUR INIT*/
        alpha=0xff;
        red=0x00;
        green=0x00;
        blue=0x00;
        ledDefaultColour= ResourcesCompat.getColor(getResources(), R.color.ledDefaultColour, null);
        ledDefaultColourVec=intToRgbConverter(ledDefaultColour);
        currentColour = ledDefaultColour;
        colourPreviewView=findViewById(R.id.ColourPreviewView);
        /*END COLOUR INIT*/

        /*BEGIN SEEKBAR INIT*/
        seekBarR=(SeekBar)findViewById(R.id.seekBarR);
        seekBarG=(SeekBar)findViewById(R.id.seekBarG);
        seekBarB=(SeekBar)findViewById(R.id.seekBarB);

        seekBarR.setMax(255);
        seekBarG.setMax(255);
        seekBarB.setMax(255);

        seekBarR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue=progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentColour=seekBarUpdate('R',progressChangedValue);
                colourPreviewView.setBackgroundColor(currentColour);
            }
        });

        seekBarG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue=progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentColour=seekBarUpdate('G',progressChangedValue);
                colourPreviewView.setBackgroundColor(currentColour);
            }
        });

        seekBarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue=progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentColour=seekBarUpdate('B',progressChangedValue);
                colourPreviewView.setBackgroundColor(currentColour);
            }
        });
        /*END SEEKBAR INIT*/

        /*BEGIN VOLLEY REQUEST QUEUE INIT*/
        queue= Volley.newRequestQueue(this);
        for (int i=0; i<8; i++) {
            for(int j=0; j<8; j++){
                String data = "["+Integer.toString(i)+","+Integer.toString(j)+",0,0,0]";
                paramClear.put(ledIndexToTagConverter(i,j), data);
            }
        }
        /*END VOLLEY REQUEST QUEUE INIT*/
    }

    /**
     * @brief updates colour selected by colourSelect to the changedValue
     * @param colourSelect 'R', 'G' or 'B' key
     * @param changedValue changed value to be saved in global variable
     * @retval ARGB intiger value of a colour
     */
    private int seekBarUpdate(char colourSelect, int changedValue){
        switch (colourSelect){
            case 'R': red=changedValue; break;
            case 'G': green=changedValue; break;
            case 'B': blue=changedValue; break;
            default: break;
        }
        alpha=(red+green+blue)/3;
        return argbToIntConverter(alpha, red, green, blue);
    }

    /**
     * @brief converts 4 ARGB varaibles into one int variable
     * @param _alpha transparency of a colour
     * @param _red red element of a colour
     * @param _green green element of a colour
     * @param _blue blue element of a colour
     * @retval int value of a colour
     */
    private int argbToIntConverter(int _alpha, int _red, int _green, int _blue){
        return (_alpha & 0xff) << 24 | (_red & 0xff) << 16 |(_green & 0xff) << 8 | (_blue & 0xff);
    }


    /**
     * @brief converts x and y int data into LED matrix string tag
     * @param x row of a led pixel
     * @param y column of a led pixel
     * @retval String "LEDxy"
     */
    private String ledIndexToTagConverter(int x, int y){
        return "LED"+Integer.toString(x)+Integer.toString(y);
    }

    /**
     * @brief converts int RGB code to vector
     * @param RGBColour int ARGB
     * @retval converted Vector[3]
     */
    private Vector intToRgbConverter(int RGBColour){
        int _red = (RGBColour >> 16) & 0xff;
        int _green = (RGBColour >> 8) & 0xff;
        int _blue = RGBColour & 0xff;
        Vector rgb= new Vector(3);
        rgb.add(0, _red);
        rgb.add(1, _green);
        rgb.add(2, _blue);
        return  rgb;
    }


    /**
     * @brief Returns the string URL address
     * @param ip string value of current ip address
     * @retval URL address
     */
    private String getURL(String ip){
        return ("http://" + ip + "/" + COMMON.LED_FILE_NAME);
    }


    /**
     * @brief Handles the logic for pressing any button on led_matrix layout
     * @param v View from which the code takes the ID of pressed button.
     */
    public void ledBtn_onClick(View v) {
        switch (v.getId()){
            case R.id.ledBtnClear:{
                setAllLedColoursToDefault();
                sendLedClearRequest();
                break;
            }
            case R.id.ledBtnSend:{
                sendLedChangeRequest();
                break;
            }
            default:{/*Do nothing*/}
        }
    }

    /**
     * @brief Handles the logic for pressing any view on led_matrix layout
     * @param v View from which the code takes the ID of pressed View
     */
    public void View_onClick(View v) {
        v.setBackgroundColor(currentColour);
        String tag=(String)v.getTag();
        Vector index=ledTagToIndexConverter(tag);
        int _x=(int)index.get(0);
        int _y=(int)index.get(1);

        ledColours[_x][_y][0]=red;
        ledColours[_x][_y][1]=green;
        ledColours[_x][_y][2]=blue;
    }

    /**
     * @brief gets position of a pixel from its tag
     * @param tag string tag of a pixel
     * @retval Vector[2] containing x-y position of a pixel
     */
    private Vector ledTagToIndexConverter(String tag){
        Vector _vector=new Vector(2);
        _vector.add(0, Character.getNumericValue(tag.charAt(3)));
        _vector.add(1, Character.getNumericValue(tag.charAt(4)));
        return _vector;
    }

    /**
     * @brief converts pixel x-y position value into Json String
     * @param x x-position of a pixel
     * @param y y-position of a pixel
     * @retval converted string Json
     */
    private String ledIndexToJsonStringConverter(int x, int y){
       String _x=Integer.toString(x);
       String _y=Integer.toString(y);
       String _red= Integer.toString(ledColours[x][y][0]);
        String _green= Integer.toString(ledColours[x][y][1]);
        String _blue= Integer.toString(ledColours[x][y][2]);
        // Example: [0,0,255,100,10]
        return "["+_x+","+_y+","+_red+","+_green+","+_blue+"]";
    }

    /**
     * @brief checks if led colours are null
     * @retval FALSE if any colour is null, TRUE if all colours are not null
     */
    private boolean isLedColourNotNull(int x, int y){
        return !((ledColours[x][y][0]==null)||(ledColours[x][y][1]==null)||(ledColours[x][y][2]==null));
    }

    /**
     * @brief sets all colours on all pixels to NULL
     */
    private void setAllLedColoursInArrayToDefault(){
        for(int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                ledColours[i][j][0]=null;
                ledColours[i][j][1]=null;
                ledColours[i][j][2]=null;
            }
        }
    }

    /**
     * @brief sets all Views in layout to default colour
     */
    private void setAllLedColoursToDefault(){
        TableLayout tb=(TableLayout)findViewById(R.id.ledTableLayout);
        View ledInd;
        for(int i=0; i<8; i++) {
            for (int j = 0; j < 8; j++) {
                ledInd = tb.findViewWithTag(ledIndexToTagConverter(i, j));
                ledInd.setBackgroundColor(ledDefaultColour);
            }
        }
        setAllLedColoursInArrayToDefault();
        sendLedClearRequest();
    }

    /**
     * @brief gets Map<String, String> with coloured pixel data
     * @retval Map<String, String> with coloured pixel data
     */
    private Map<String, String> getLedDisplayParams(){
        String led;
        String colour;
        Map<String, String> params = new HashMap<String, String>();
        for(int i=0; i<8; i++) {
            for (int j = 0; j < 8; j++) {
                if(isLedColourNotNull(i, j)){
                    led=ledIndexToTagConverter(i, j);
                    colour=ledIndexToJsonStringConverter(i, j);
                    params.put(led, colour);
                }
            }
        }
        return params;
    }



    /**
     * @brief sends request to change data on physical device or emulator
     */
    private void sendLedChangeRequest(){
        String url = getURL(ipAddress);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        String msg = error.getMessage();
                        if( msg != null) {
                            Log.d("Error.Response", msg);
                        } else {
                            // error type specific code
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                return getLedDisplayParams();
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    /**
     * @brief sends request to clear pixel colours on physical device or emulator
     */
    private void sendLedClearRequest(){
        String url = getURL(ipAddress);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        String msg = error.getMessage();
                        if( msg != null) {
                            Log.d("Error.Response", msg);
                        } else {
                            // error type specific code
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                return paramClear;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

}
