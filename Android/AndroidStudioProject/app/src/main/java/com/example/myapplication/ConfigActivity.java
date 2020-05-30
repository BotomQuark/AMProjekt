package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class  ConfigActivity extends AppCompatActivity {
    /* BEGIN config textboxes */
    EditText ipEditText;
    EditText portEditText;
    EditText ApiVersionEditText;
    EditText sampleTimeEditText;
    EditText rememberedSamplesQuantityEditText;
    /* END config textboxes */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);

        // get the Intent that started this Activity
        Intent intent = getIntent();

        // get the Bundle that stores the data of this Activity
        Bundle configBundle = intent.getExtras();

        ipEditText = findViewById(R.id.ipEditText);
        String ip = configBundle.getString(COMMON.CONFIG_IP_ADDRESS, COMMON.DEFAULT_IP_ADDRESS);
        ipEditText.setText(ip);

        sampleTimeEditText = findViewById(R.id.sampleTimeEditText);
        int st = configBundle.getInt(COMMON.CONFIG_SAMPLE_TIME, COMMON.DEFAULT_SAMPLE_TIME);
        sampleTimeEditText.setText(Integer.toString(st));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(COMMON.CONFIG_IP_ADDRESS, ipEditText.getText().toString());
        intent.putExtra(COMMON.CONFIG_SAMPLE_TIME, sampleTimeEditText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
