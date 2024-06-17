package com.example.attendify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClassHome extends AppCompatActivity {

    ImageButton btnBack,btnCalendar;
    RecyclerView rcvStudent;
    ArrayList<StudentModel> studentList = new ArrayList<StudentModel>();

    MaterialButton btnAttendance;

    RecyclerStudentAdapter adapter;

    CustomProgressBar pBar;
    String baseApi;
    String classId;
    String port;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_home);

        Bundle bundle = getIntent().getExtras();

        classId = bundle.getString("classId").toString();
        port = bundle.getString("port").toString();

        btnBack = findViewById(R.id.btnBack);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnAttendance = findViewById(R.id.btnTakeAttendance);
        rcvStudent = findViewById(R.id.rcvStudent);
        rcvStudent.setLayoutManager(new LinearLayoutManager(this));

        pBar = new CustomProgressBar(this);
        baseApi = getString(R.string.baseApi);

        registerForContextMenu(rcvStudent);


        getClassDetails();



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TeacherHome.class));
                finish();
            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendanceReport = new Intent(getApplicationContext(), AttendanceReport.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("classId",classId);
                bundle1.putString("port",port);
                attendanceReport.putExtras(bundle1);
                startActivity(attendanceReport);
            }
        });

        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendance = new Intent(getApplicationContext(), Attendance.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("classId",classId);
                bundle1.putString("port",port);
                attendance.putExtras(bundle1);
                startActivity(attendance);

            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = item.getGroupId();
        removeItem(position);
        return true;
    }

    private void removeItem(int position){
        // TODO: 18-05-2024 remove student from class


        pBar.show();
        String studentId = studentList.get(position).id;
        JSONObject param = new JSONObject();

        try {
            param.put("id",studentId);
            param.put("classId",classId);
        } catch (JSONException e) {
            pBar.hide();
            throw new RuntimeException(e);
        }

        Log.d("API", "removeItem: "+param);


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                baseApi + "/student/remove_student.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.hide();

                        try {
                            if (response.has("message")) {
                                Toast.makeText(ClassHome.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                if(response.get("message").toString().equals("success")){
                                    recreate();
                                }
                            }
                        }catch (JSONException e){
                            Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pBar.hide();
                        Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                        Log.d("API", "onErrorResponse: "+error);
                    }
                }
        );


        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);


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
                                Toast.makeText(ClassHome.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                JSONArray jsonArray = response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    studentList.add(new StudentModel(obj.get("id").toString(),obj.get("name").toString(),obj.get("rollNo").toString(),obj.get("contact").toString(),obj.get("profilePic").toString()));
                                }

                                adapter = new RecyclerStudentAdapter(getApplicationContext(),studentList);

                                rcvStudent.setAdapter(adapter);

                            }
                        }catch (JSONException e){
                            Toast.makeText(ClassHome.this, ""+e, Toast.LENGTH_SHORT).show();
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
}