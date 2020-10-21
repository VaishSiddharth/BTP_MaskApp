package com.btp.maskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.btp.maskapp.Utils.Permissions;
import com.btp.maskapp.Utils.PreferenceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Executor;

import static com.btp.maskapp.Utils.Permissions.REQUEST_CODE_REQUIRED_PERMISSIONS;
import static com.btp.maskapp.Utils.Permissions.REQUIRED_PERMISSIONS;

public class MainActivity extends AppCompatActivity {

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView verifyUser = findViewById(R.id.verify);
        verifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), OpenCameraForVerify.class));
            }
        });

        MyTaskExecutor taskExecutor = new MyTaskExecutor();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //do work
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                BluetoothDevice hc05 = bluetoothAdapter.getRemoteDevice("00:19:10:08:E8:25");
                BluetoothSocket bluetoothSocket = null;
                int bluetoothConnectRetry = 0;
                do {
                    try {
                        bluetoothSocket = hc05.createInsecureRfcommSocketToServiceRecord(mUUID);
                        bluetoothSocket.connect();
                        System.out.println(bluetoothSocket.isConnected());
                        bluetoothConnectRetry++;

                        OutputStream outputStream = bluetoothSocket.getOutputStream();

                        while (bluetoothSocket.isConnected()) {
                            InputStream inputStream = bluetoothSocket.getInputStream();
                            inputStream.skip(inputStream.available());
                            String temperature = "";
                            for (int i = 0; i < 5; i++) {
                                temperature = temperature + (char) inputStream.read();
                            }
                            System.out.println("Arduino Reading " + temperature);
                            if (!temperature.equalsIgnoreCase("") && Double.parseDouble(temperature) > 30) {
                                outputStream.write(48);
                                PreferenceManager.init(getApplicationContext());      //setup preference manager for app
                                if (!Permissions.hasPermissions(getApplicationContext(), REQUIRED_PERMISSIONS)) {       //check permissions
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
                                    }
                                }
                                PreferenceManager.setStringValue("TEMP", temperature);
                                bluetoothSocket.close();
                                startActivity(new Intent(getApplicationContext(), OpenCameraForVerify.class));
                                break;
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                while (false);

            }
        });
    }

    class MyTaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }
}

//Arduino working code
/*

#include <Wire.h>
#include <Adafruit_MLX90614.h>

Adafruit_MLX90614 mlx = Adafruit_MLX90614();

int buzzer = 11;
int irPin = 7;
int sensorOut = HIGH;
int trigger;

void setup() {
  //  Serial.println("Adafruit MLX90614 test");
  Serial.begin(9600);
  mlx.begin();
  pinMode(irPin, INPUT);
  delay(3000);
}

void loop() {
  sensorOut = digitalRead(irPin);
  if (sensorOut == LOW)
  {
    if (mlx.readObjectTempC() > 37.2)
    {
      Serial.print(mlx.readObjectTempC());
      delay(200);
    }
    else
    {
      Serial.print(mlx.readObjectTempC());
      delay(200);
    }
    trigger = Serial.read();
    if (trigger == 48)
    {
      tone(buzzer, 450);
      delay(500);
      noTone(buzzer);
      delay(500);
    }
    else
    {
      noTone(buzzer);
    }
  }
  delay(200);
}

*/