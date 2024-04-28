package com.example.attendify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class TeacherActivity extends AppCompatActivity {


    private Button btnTeacherSend,btnStop;
    private TextView txtTeacherIp,txtStudentResponse;

    private final int PORT = 12345;

    private boolean isWorkDone = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        txtTeacherIp = findViewById(R.id.txtTeacherIp);
        btnTeacherSend = findViewById(R.id.btnTeacherSend);
        txtStudentResponse = findViewById(R.id.txtStudentResponse);
        btnStop = findViewById(R.id.btnStop);

        txtTeacherIp.setText(getIP());



        btnTeacherSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAttendance();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWork();
            }
        });

        receiveResponce();
    }

    private void takeAttendance(){
        sendAttendanceSignal();
    }

    private void receiveResponce(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DatagramSocket socket = new DatagramSocket(PORT);
                    isWorkDone = false;
                    while(!isWorkDone){
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
                        socket.receive(receivePacket);
                        String receiveMessage = new String(receivePacket.getData(),0,receivePacket.getLength());
                        processResponse(receiveMessage);
                    }
                }catch (Exception e){

                }


            }
        }).start();
    }

    private void processResponse(final String response){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                txtStudentResponse.setText(response);
                System.out.println(response);
            }
        });
    }

    private void stopWork(){
        isWorkDone = true;
    }



    private void sendAttendanceSignal(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DatagramSocket socket = new DatagramSocket();

                    byte[] sendData = "Attendance".getBytes();
                    InetAddress broadastAddr = InetAddress.getByName("255.255.255.255");
                    DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,broadastAddr,PORT);


                    socket.send(sendPacket);
                    Log.d("S", "sendAttendanceSignal: Signal Send");
                    socket.close();

                }
                catch (Exception e){

                    e.printStackTrace();
                }
            }
        }).start();

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