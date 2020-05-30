package com.example.myapplication;

public class COMMON {
        // activities request codes
        public final static int REQUEST_CODE_CONFIG = 1;

        // configuration info: names and default values
        public final static String CONFIG_IP_ADDRESS = "ipAddress";
        public final static String DEFAULT_IP_ADDRESS = "192.168.1.28";

        public final static String CONFIG_SAMPLE_TIME = "sampleTime";
        public final static int DEFAULT_SAMPLE_TIME = 500;

        public final static String CONFIG_API_VERSION = "API";
        public final static String DEFAULT_API_VERSION ="API";

        public final static String CONFIG_PORT = "port";
        public final static String DEFAULT_PORT = "0000";

        public final static int CONFIG_SAMPLE_QUANTITY = 0;
        public final static int DEFAULT_SAMPLE_QUANTITY = 1000;

        // error codes
        public final static int ERROR_TIME_STAMP = -1;
        public final static int ERROR_NAN_DATA = -2;
        public final static int ERROR_RESPONSE = -3;

        // IoT server data
        public final static String THP_FILE_NAME = "thpdata.json";
        public final static String LED_FILE_NAME = "setled.php";

}
