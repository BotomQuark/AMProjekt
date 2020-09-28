package com.example.datex;

public final class DATA {
    // activities request codes
    public final static int REQUEST_CODE_CONFIG = 1;

    // configuration info: names and default values
    public final static String CONFIG_IP_ADDRESS = "ipAddress";
    public final static String DEFAULT_IP_ADDRESS = "10.10.0.125";

    public final static String CONFIG_SAMPLE_TIME = "sampleTime";
    public final static int DEFAULT_SAMPLE_TIME = 500;

    public final static String CONFIG_SAMPLE_QUANTITY = "sampleQuantity";
    public final static int DEFAULT_SAMPLE_QUANTITY = 100;

    // error codes
    public final static int ERROR_TIME_STAMP = -1;
    public final static int ERROR_NAN_DATA = -2;
    public final static int ERROR_RESPONSE = -3;

    // IoT server data
    public final static String FILE_NAME = "tph.json";
    public final static String FILE_NAME_TABLES = "data_arrayAN.json";
    public final static String LED_FILE_NAME = "setled.php";
    public final static String JOYSTICK_FILE_NAME = "joy.json";
    public final static String RPY_FILE_NAME = "rpy.json";

}
