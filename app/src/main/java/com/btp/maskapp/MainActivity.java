package com.btp.maskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Executor;

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

                        while (true) {
                            InputStream inputStream = bluetoothSocket.getInputStream();
                            inputStream.skip(inputStream.available());
                            byte b = (byte) inputStream.read();
                            if ((char) b == '0') {
                                outputStream.write(48);
                            }
                            System.out.println((char) b);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                while (!bluetoothSocket.isConnected() && bluetoothConnectRetry < 3);

            }
        });
    }

    class MyTaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }
}