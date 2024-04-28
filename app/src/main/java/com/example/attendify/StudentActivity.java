package com.example.attendify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
public class StudentActivity extends AppCompatActivity {


    private TextView txtReceiveData,txtStudentIp;
    private Button btnRestart;

    private boolean isWorkDone = false;
    
    private final int PORT = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        txtReceiveData = findViewById(R.id.txtReceiveData);
        txtStudentIp = findViewById(R.id.txtStudentIp);
        btnRestart = findViewById(R.id.btnRestart);

        txtStudentIp.setText(getIP());


        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWorkDone = false;
                receiveSignal();
            }
        });


        receiveSignal();
    }


    private void receiveSignal(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DatagramSocket socket = new DatagramSocket(PORT);
                    isWorkDone = false;
                    while(!isWorkDone) {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                        socket.receive(receivePacket);

                        String receiveMessage = new String(receivePacket.getData(),0,receivePacket.getLength());


                        processSignal(receiveMessage);
                        sendResponse(receivePacket.getAddress());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stopWork(){
        isWorkDone = true;
    }

    private void sendResponse(InetAddress serverAddr){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();

                    byte[] responseData = getIP().getBytes();
                    DatagramPacket sendResponsePacket = new DatagramPacket(responseData,responseData.length,serverAddr,PORT);

                    socket.send(sendResponsePacket);
                    socket.close();

                    stopWork();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void processSignal(final String receiveSignal) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                txtReceiveData.setText(receiveSignal);
                System.out.println(receiveSignal);
            }
        });


    }
    

    private String getIP(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(wifiManager != null){
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddr = wifiInfo.getIpAddress();
            return formatIP(ipAddr);
        }
        return "-1";
    }

    private String formatIP(int ipAddr){
        return (ipAddr & 0xFF) + "." +
                ((ipAddr >> 8) & 0xFF) + "." +
                ((ipAddr >> 16) & 0xFF) + "." +
                ((ipAddr >> 24) & 0xFF);
    }
}