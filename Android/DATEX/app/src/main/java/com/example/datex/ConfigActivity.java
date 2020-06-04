package com.example.datex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class ConfigActivity extends AppCompatActivity {

    /* BEGIN config textboxes */
    EditText ipEditText;
    EditText sampleTimeEditText;
    /* END config textboxes */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_acivity);

        // get the Intent that started this Activity
        Intent intent = getIntent();

        // get the Bundle that stores the data of this Activity
        Bundle configBundle = intent.getExtras();

        ipEditText = findViewById(R.id.ipEditTextConfig);
        String ip = configBundle.getString(DATA.CONFIG_IP_ADDRESS, DATA.DEFAULT_IP_ADDRESS);
        ipEditText.setText(ip);

        sampleTimeEditText = findViewById(R.id.sampleTimeEditTextConfig);
        int st = configBundle.getInt(DATA.CONFIG_SAMPLE_TIME, DATA.DEFAULT_SAMPLE_TIME);
        sampleTimeEditText.setText(Integer.toString(st));
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(DATA.CONFIG_IP_ADDRESS, ipEditText.getText().toString());
        intent.putExtra(DATA.CONFIG_SAMPLE_TIME, sampleTimeEditText.getText().toString());
    setResult(RESULT_OK, intent);
    super.onBackPressed();
}
}