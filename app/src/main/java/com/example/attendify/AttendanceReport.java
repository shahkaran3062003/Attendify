package com.example.attendify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AttendanceReport extends AppCompatActivity {

    CalendarView clvFrom,clvTo;
    MaterialButton btnGetReport;
    ImageView btnBack;
    Calendar cFrom,cTo;

    String fromDate="";
    String toDate = "";

    SimpleDateFormat sdf;
    CustomProgressBar pBar;
    String baseApi;
    String classId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);

        Bundle bundle = getIntent().getExtras();
        classId = bundle.getString("classId").toString();

        btnBack = findViewById(R.id.btnBack);
        clvFrom = findViewById(R.id.clvFrom);
        clvTo = findViewById(R.id.clvTo);
        btnGetReport = findViewById(R.id.btnGetReport);
        cFrom = Calendar.getInstance();
        cTo = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        pBar = new CustomProgressBar(this);
        baseApi = getString(R.string.baseApi);

        cFrom.add((Calendar.MONTH),-1);

        clvFrom.setDate(cFrom.getTimeInMillis(),true,true);
        clvTo.setDate(cTo.getTimeInMillis(),true,true);


        fromDate = cFrom.get(Calendar.YEAR)+"-"+(cFrom.get(Calendar.MONTH)+1) +"-"+cFrom.get(Calendar.DATE);
        toDate = cTo.get(Calendar.YEAR)+"-"+(cTo.get(Calendar.MONTH)+1)+"-"+cTo.get(Calendar.DATE);

        clvFrom.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                fromDate = year+"-"+(month+1)+"-"+dayOfMonth;


            }
        });

        clvTo.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                toDate = year+"-"+(month+1)+"-"+dayOfMonth;
            }
        });




        btnGetReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    try {
                        Date from = sdf.parse(fromDate);
                        Date to = sdf.parse(toDate);

                        if(from.after(to)){
                            Toast.makeText(AttendanceReport.this, "Invalid 'From' and 'To' dates!!!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AttendanceReport.this, ""+fromDate+":"+toDate, Toast.LENGTH_SHORT).show();
                            getAttendanceData(fromDate, toDate);
                        }
                    } catch (ParseException e) {
                        Toast.makeText(AttendanceReport.this, "Error", Toast.LENGTH_SHORT).show();

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

    void getAttendanceData(String start,String end){
        pBar.show();
        JSONObject param = new JSONObject();

        try {
            param.put("classId",classId);
            param.put("startDate",start);
            param.put("endDate",end);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                baseApi + "/attendance/read_class_by_start_end_date.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.hide();
                        try {
                            if(response.has("message")){
                                Toast.makeText(AttendanceReport.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }else{
                                JSONArray arr = response.getJSONArray("data");
                                createAttendanceFile(arr);
                            }
                        }catch (JSONException e){

                        }
                        Log.d("API", "onResponse: "+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pBar.hide();
                        Log.d("API", "onErrorResponse: "+error);
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(AttendanceReport.this);
        queue.add(request);

        Log.d("API", "getAttendanceData: "+param);
    }


    void createAttendanceFile(JSONArray arr){

        String rollNo,date,name,status;
        List<String> listRoll,listDate;
        listRoll = new ArrayList<>();
        listDate = new ArrayList<>();
        HashMap<String,String> rollName = new HashMap<>();

        for(int i=0;i<arr.length();i++){
            try {
                JSONObject obj = arr.getJSONObject(i);

                rollNo =  obj.get("rollNo").toString();
                name = obj.get("name").toString();
                date = obj.get("date").toString();
                rollName.put(rollNo,name);

                if(!listRoll.contains(rollNo)){
                    listRoll.add(rollNo);
                }
                if(!listDate.contains(date)){
                    listDate.add(date);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        Collections.sort(listRoll);

        HashMap<String,Integer> rollRow = new HashMap<>();
        HashMap<String,Integer> dateCol = new HashMap<>();


        for(int i=0;i<listRoll.size();i++){
            rollRow.put(listRoll.get(i),i+1);
        }

        for(int i=0;i<listDate.size();i++){
            dateCol.put(listDate.get(i),i+2);
        }

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet  = hssfWorkbook.createSheet("Attendance");

        Row headerRow = hssfSheet.createRow(0);

        headerRow.createCell(0).setCellValue("Roll No");
        headerRow.createCell(1).setCellValue("Name");

        for(int i=0;i<listDate.size();i++){
            headerRow.createCell(i+2).setCellValue(listDate.get(i));
        }

        for(int i=0;i<listRoll.size();i++){
            Row row = hssfSheet.createRow(i+1);
            row.createCell(0).setCellValue(listRoll.get(i));
            row.createCell(1).setCellValue(rollName.get(listRoll.get(i)));
            for(int j=0;j<listDate.size();j++){
                row.createCell(j+2);
            }
        }

        for(int i=0;i<arr.length();i++){
            try {
                JSONObject obj = arr.getJSONObject(i);
                rollNo = obj.get("rollNo").toString();
                date = obj.get("date").toString();
                status = obj.get("status").toString();

                Row row = hssfSheet.getRow(rollRow.get(rollNo));
                Cell cell = row.getCell(dateCol.get(date));
                
                if(status.equals("1")){
                    cell.setCellValue("P");
                }else{
                    cell.setCellValue("A");
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        saveSheet(hssfWorkbook);


        Log.d("API", "createAttendanceFile: "+listRoll);
        Log.d("API", "createAttendanceFile: "+listDate);
    }

    private void saveSheet(HSSFWorkbook hssfWorkbook){
        StorageManager storageManager = (StorageManager)getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);

        File outputFile = new File(storageVolume.getDirectory().getPath() + "/Download/Attendance-"+fromDate+"_"+toDate+"_"+".xls");

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