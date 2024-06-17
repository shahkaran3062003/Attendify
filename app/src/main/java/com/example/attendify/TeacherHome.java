package com.example.attendify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class TeacherHome extends AppCompatActivity {

    RecyclerView rcvClass;

    MaterialButton btnCreateClass;

    ArrayList<ClassModel> classList = new ArrayList<>();
    RecyclerClassAdapter adapter;
    CustomProgressBar pBar;
    String baseAPI;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);


        rcvClass = findViewById(R.id.rcvClass);
        rcvClass.setLayoutManager(new LinearLayoutManager(this));
        btnCreateClass = findViewById(R.id.btnCreateClass);
        baseAPI = getString(R.string.baseApi);
        pBar = new CustomProgressBar(this);


        btnCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createClass = new Intent(getApplicationContext(), CreateClass.class);
                startActivity(createClass);
                finish();
            }
        });


        registerForContextMenu(rcvClass);

        getAllClass();


    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getGroupId();
        switch (item.getOrder()) {
            case 0:
                // Edit action
                editItem(position);
                return true;
            case 1:
                // Delete action
                deleteItem(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void editItem(int position) {
        // TODO: 18-05-2024 add edit logic
        Intent editClass = new Intent(this, CreateClass.class);
        Bundle bundle = new Bundle();
        bundle.putString("name",classList.get(position).classTitle);
        bundle.putString("branch",classList.get(position).classBranch);
        bundle.putString("sem",classList.get(position).classSem);
        bundle.putString("port",classList.get(position).port);
        bundle.putString("id",classList.get(position).classId);

        editClass.putExtras(bundle);
        startActivity(editClass);
        finish();

//        Toast.makeText(this, "Edit item at position " + position, Toast.LENGTH_SHORT).show();

    }

    private void deleteItem(int position) {
        // TODO: 18-05-2024 add delte logic
        pBar.show();
        String classId = classList.get(position).classId;

        JSONObject param = new JSONObject();
        try {
            param.put("id",classId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                baseAPI + "/class/delete.php",
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.hide();

                        try{
                            if(response.has("message")){
                                Toast.makeText(TeacherHome.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                if(response.get("message").toString().equals("success")){
                                    recreate();
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

    public void getAllClass(){
        pBar.show();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                baseAPI + "/class/read_all.php",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.hide();
                        try {
                            if (response.has("message")) {
                                Toast.makeText(getApplicationContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray jsonArray = response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    classList.add(new ClassModel(obj.get("id").toString(), obj.get("name").toString(), obj.get("branch").toString(), obj.get("sem").toString(), obj.get("port").toString(),obj.get("student_count").toString()));
                                }
                                adapter = new RecyclerClassAdapter(getApplicationContext(),classList);
                                adapter.setOnClickListener(new RecyclerClassAdapter.OnClickListener() {
                                    @Override
                                    public void onClick(int position, ClassModel classDetail) {
                                        Intent classHome = new Intent(getApplicationContext(), ClassHome.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("classId",classDetail.classId);
                                        bundle.putString("port",classDetail.port);
                                        classHome.putExtras(bundle);

                                        startActivity(classHome);
                                        finish();


                                    }
                                });
                                rcvClass.setAdapter(adapter);

                            }
                        } catch (JSONException e){
                            throw new RuntimeException(e);
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