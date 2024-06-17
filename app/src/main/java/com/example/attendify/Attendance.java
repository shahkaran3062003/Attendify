package com.example.attendify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class Attendance extends AppCompatActivity implements RecyclerStudentAttendanceAdapter.OnItemClickListener{

    MaterialButton btnConfirm;
    ImageButton btnBack;
    OtpEditText edtOTP;

    RecyclerView rcvStudent;
    RecyclerStudentAttendanceAdapter adapter;
    ArrayList<StudentAttendanceModel> studentList = new ArrayList<StudentAttendanceModel>();
    CustomProgressBar pBar;
    String baseApi;
    String classId;
    String otp;
    Integer PORT;
    Boolean isWorkDone;
    int countNumber;
    HashMap<String, Integer> studentIdToIndexHashMap = new HashMap<>();

    Activity activity;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        activity = this;
        isWorkDone = false;

        Bundle bundle = getIntent().getExtras();
        classId = bundle.getString("classId");
        PORT = Integer.parseInt(bundle.getString("port"));

        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);
        edtOTP = findViewById(R.id.edtOTP);
        pBar = new CustomProgressBar(this);
        baseApi = getString(R.string.baseApi);

        rcvStudent = findViewById(R.id.rcvStudentAttendance);
        rcvStudent.setLayoutManager(new LinearLayoutManager(this));

        int min = 100000,max = 999999;
        otp = Integer.toString(new Random().nextInt(max - min)+min);
        edtOTP.setText(otp);

        countNumber = 0;

        getClassDetails();

        sendAttendanceSignal();

        receiveResponse();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 18-05-2024 add student attendance to database

                stopWork();
                studentList = adapter.studentList;
                for (StudentAttendanceModel data: studentList) {
                    markAttendance(data);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void stopWork(){
        isWorkDone = true;
    }


    private void sendMessage(InetAddress clientAdd,String Message){
        Log.d("API", "sendMessage: "+clientAdd);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DatagramSocket socket = new DatagramSocket();
                    byte[] sendData = Message.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,clientAdd,PORT);
                    socket.send(sendPacket);
                    Log.d("S", "sendAttendanceSignal: Signal Send."+Message);
//                    Thread.sleep(1000);
                    socket.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendAttendanceSignal(){

        try {
            InetAddress broadcast = InetAddress.getByName("255.255.255.255");
            sendMessage(broadcast,"attendance");

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }


    }

    private void receiveResponse(){
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

                        processResponse(receivePacket.getAddress(),receiveMessage,activity);
                    }
                }catch (Exception e){

                }
            }
        }).start();
    }







    private void processResponse(InetAddress clientAdd, final String response, Activity activity){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {


                if(response.equals("attendance")){
                    return;
                }
                Log.d("API", "STUDENT RESPONCE : "+response);
                String[] resp = response.split(",");

                String currentOTP = ((OtpEditText) activity.findViewById(R.id.edtOTP)).getText().toString();
                Log.d("API", "OTP Value: " + resp[0] + " " + currentOTP);

                if (!resp[0].equals(currentOTP)) {
                    sendMessage(clientAdd, "invalid");
                    return;}

                if (studentIdToIndexHashMap.containsKey(resp[1])) {
                    int position = studentIdToIndexHashMap.get(resp[1]);
                    studentList.get(position).setPresent();
                    adapter.notifyItemChanged(position);

                    sendMessage(clientAdd, "done");
                }

            }

        });
    }

    @Override
    public void onPresentClick(int position) {
        studentList.get(position).setPresent();
//        Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAbsentClick(int position) {
        studentList.get(position).setAbsent();
//        Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
    }

    public void getClassDetails()  {
        pBar.show();

        JSONObject param = new JSONObject();
        try {
            param.put("id",classId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request  = new JsonObjectRequest(
                Request.Method.POST,
                baseApi + "/class/read_all_student.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.hide();
                        try{
                            if(response.has("message")){
                                Toast.makeText(Attendance.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                JSONArray jsonArray = response.getJSONArray("data");
                                countNumber = jsonArray.length();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    studentList.add(new StudentAttendanceModel(obj.get("id").toString(),obj.get("name").toString(),obj.get("rollNo").toString(),obj.get("profilePic").toString()));
                                    studentIdToIndexHashMap.put(obj.get("id").toString(),i);
                                }

                                adapter = new RecyclerStudentAttendanceAdapter(Attendance.this,studentList);

                                adapter.setOnItemClickListener(Attendance.this);
                                rcvStudent.setAdapter(adapter);

                            }
                        }catch (JSONException e){
                            Toast.makeText(Attendance.this, ""+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override

                    public void onErrorResponse(VolleyError error) {
                        pBar.hide();
                        Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    void markAttendance(StudentAttendanceModel model){
        pBar.show();

        JSONObject param = new JSONObject();

        try {
            param.put("classId",classId);
            param.put("studentId",model.id);
            param.put("status",model.isPresent);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                baseApi + "/attendance/insert.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.hide();
                        countNumber--;

                        try {

                            if (response.has("message")){
                                String message = response.get("message").toString();
                                if(message.equals("success")){
                                    if(countNumber==0){
                                        Toast.makeText(Attendance.this, "Attendance Taken!!!", Toast.LENGTH_SHORT).show();
                                        getTodayAttendance();
//                                        finish();
                                    }
                                }
                                else if(message.equals("Attendance already taken")){
                                    Toast.makeText(Attendance.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (JSONException e){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pBar.hide();
                        Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    void getTodayAttendance(){
        JSONObject param = new JSONObject();

        try {
            param.put("classId",classId);
            Calendar today = Calendar.getInstance();
            String todayStr = ""+today.get(Calendar.YEAR)+"-"+(today.get(Calendar.MONTH)+1)+"-"+today.get(Calendar.DATE);
            param.put("date",todayStr);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.d("API", "getTodayAttendance: "+param);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                baseApi + "/attendance/read_class_by_date.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("message")){
                                Toast.makeText(Attendance.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                JSONArray obj = response.getJSONArray("data");
                                createTodayAttendance(obj,param.get("date").toString());
                            }
                        }catch (JSONException e){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Attendance.this, "Error!!!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    void createTodayAttendance(JSONArray obj,String date){
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet  = hssfWorkbook.createSheet("Attendance");

        Row headerRow = hssfSheet.createRow(0);
        String[] cols = {"Roll No","Name",date};

        for(int i=0;i<cols.length;i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(cols[i]);
        }

        Log.d("API", "createTodayAttendance: "+obj);

        for(int r = 0;r<obj.length();r++){
            try {
                JSONObject temp = obj.getJSONObject(r);
                Row row = hssfSheet.createRow(r+1);

                row.createCell(0).setCellValue(temp.get("rollNo").toString());
                row.createCell(1).setCellValue(temp.get("name").toString());
                row.createCell(2).setCellValue((temp.get("status").toString().equals("1") ? "P":"A"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        saveSheet(hssfWorkbook,date);
    }

    private void saveSheet(HSSFWorkbook hssfWorkbook,String date){
        StorageManager storageManager = (StorageManager)getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);

        File outputFile = new File(storageVolume.getDirectory().getPath() + "/Download/Attendance-"+date+".xls");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            hssfWorkbook.write(fileOutputStream);
            fileOutputStream.close();
            hssfWorkbook.close();

            Toast.makeText(this, "Attendance Save in Downloads!!!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (FileNotFoundException e) {

            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
