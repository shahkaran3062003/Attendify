package com.example.attendify;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateClass extends AppCompatActivity {

    ImageButton btnBack;
    MaterialButton btnCreate,btnCancel;

    Bundle bundle;
    TextInputEditText edtName,edtBranch,edtSem,edtPort;
    TextView txtCreateClass;
    String classId;
    String baseAPI;
    CustomProgressBar pBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        btnBack = findViewById(R.id.btnBack);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);
        edtName = findViewById(R.id.edtName);
        edtBranch = findViewById(R.id.edtBranch);
        edtSem = findViewById(R.id.edtSem);
        edtPort = findViewById(R.id.edtPortNumber);
        txtCreateClass = findViewById(R.id.txtCreateClass);
        baseAPI = getString(R.string.baseApi);
        pBar = new CustomProgressBar(this);

        bundle = getIntent().getExtras();

        if(bundle != null){
            btnCreate.setText(R.string.txtEditClass);
            edtName.setText(bundle.getString("name"));
            edtBranch.setText(bundle.getString("branch"));
            edtSem.setText(bundle.getString("sem"));
            edtPort.setText(bundle.getString("port"));
            classId = bundle.getString("id");
            txtCreateClass.setText(R.string.txtEditClass);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent teacherHome = new Intent(getApplicationContext(), TeacherHome.class);
                startActivity(teacherHome);
                finish();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // TODO: 18-05-2024 code to create class


                if (!isEmptyField() && isNameCorrect() && isBranchCorrect() && isSemCorrect() && isPortCorrect()) {

                    JsonObjectRequest request;
                    JSONObject param;
                    if (btnCreate.getText().toString().equals("Create")) {
                        pBar.show();
                        param = new JSONObject();
                        try {
                            param.put("name", edtName.getText().toString());
                            param.put("sem", edtSem.getText().toString());
                            param.put("branch", edtBranch.getText().toString());
                            param.put("port", edtPort.getText().toString());
                        } catch (JSONException e) {
                            pBar.hide();
                            throw new RuntimeException(e);
                        }

                        request = new JsonObjectRequest(
                                Request.Method.POST,
                                baseAPI + "/class/register.php",
                                param,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        pBar.hide();

                                        try {
                                            if (response.has("message")) {
                                                Toast.makeText(CreateClass.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                                if (response.get("message").toString().equals("success")) {
                                                    Intent teacherHome = new Intent(getApplicationContext(), TeacherHome.class);
                                                    startActivity(teacherHome);
                                                    finish();
                                                }
                                            }

                                        } catch (JSONException e) {

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
                    } else {
                        pBar.show();
                        param = new JSONObject();
                        try {
                            param.put("id", classId);
                            param.put("name", edtName.getText().toString());
                            param.put("sem", edtSem.getText().toString());
                            param.put("branch", edtBranch.getText().toString());
                            param.put("port", edtPort.getText().toString());
                        } catch (JSONException e) {
                            pBar.hide();
                            throw new RuntimeException(e);
                        }

                        request = new JsonObjectRequest(
                                Request.Method.POST,
                                baseAPI + "/class/update.php",
                                param,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        pBar.hide();

                                        try {
                                            if (response.has("message")) {
                                                Toast.makeText(CreateClass.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                                if (response.get("message").toString().equals("success")) {
                                                    Intent teacherHome = new Intent(getApplicationContext(), TeacherHome.class);
                                                    startActivity(teacherHome);
                                                    finish();
                                                }
                                            }

                                        } catch (JSONException e) {

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
                    }

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(request);

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent teacherHome = new Intent(getApplicationContext(), TeacherHome.class);
                startActivity(teacherHome);
                finish();
            }
        });
    }

    boolean isEmptyField(){

        if(TextUtils.isEmpty(edtName.getText())){
            Toast.makeText(this, "Enter Name.", Toast.LENGTH_SHORT).show();
            return  true;
        }

        if(TextUtils.isEmpty(edtBranch.getText())){
            Toast.makeText(this, "Enter Branch.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(edtSem.getText())){
            Toast.makeText(this, "Enter Semester", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(edtPort.getText())){
            Toast.makeText(this, "Enter Port.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return  false;
    }

    boolean isNameCorrect(){

        String name = edtName.getText().toString();

        if(!name.matches("[a-zA-Z _0-9]*")){
            Toast.makeText(this, "Invalid Class Name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }

    boolean isBranchCorrect(){

        String name = edtBranch.getText().toString();

        if(!name.matches("[a-zA-Z ]*")){
            Toast.makeText(this, "Invalid Branch Name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }

    boolean isSemCorrect(){

        if(TextUtils.isDigitsOnly(edtSem.getText())){
            return true;
        }
        Toast.makeText(this, "Invalid Semester Number.", Toast.LENGTH_SHORT).show();
        return false;
    }

    boolean isPortCorrect(){
        if(TextUtils.isDigitsOnly(edtSem.getText())){
            return true;
        }
        Toast.makeText(this, "Invalid Port Number.", Toast.LENGTH_SHORT).show();
        return false;
    }

}