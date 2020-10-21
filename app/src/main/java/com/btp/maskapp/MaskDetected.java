package com.btp.maskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.btp.maskapp.Utils.Permissions;
import com.btp.maskapp.Utils.PreferenceManager;

import static com.btp.maskapp.Utils.Permissions.REQUEST_CODE_REQUIRED_PERMISSIONS;
import static com.btp.maskapp.Utils.Permissions.REQUIRED_PERMISSIONS;

public class MaskDetected extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mask_detected);
        TextView textView = findViewById(R.id.bodyTemp);
        PreferenceManager.init(this);      //setup preference manager for app
        if (!Permissions.hasPermissions(this, REQUIRED_PERMISSIONS)) {       //check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
            }
        }
        String temperature = PreferenceManager.getStringValue("TEMP");
        textView.setText("Body Temperature is: " + temperature);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }, 2000);
    }
}