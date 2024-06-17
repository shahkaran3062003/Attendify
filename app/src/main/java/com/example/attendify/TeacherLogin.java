package com.example.attendify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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

public class TeacherLogin extends AppCompatActivity {

    TextView txtSignUp;
    MaterialButton btnLogin;
    TextInputEditText edtEmail,edtPassword;
    CustomProgressBar pBar;
    String baseAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        txtSignUp = findViewById(R.id.txtSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        pBar = new CustomProgressBar(this);
        baseAPI = getString(R.string.baseApi);

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherLogin.this, TeacherRegister.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmptyField() && isEmailCorrect()){
                    pBar.show();
                    JSONObject params = new JSONObject();
                    try {
                        params.put("email",edtEmail.getText().toString());
                        params.put("password",edtPassword.getText().toString());
                    } catch (JSONException e) {
                        pBar.hide();
                        throw new RuntimeException(e);
                    }

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            baseAPI + "/teacher/login.php",
                            params,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    pBar.hide();

                                    if(response.has("message")){
                                        try {
                                            Toast.makeText(TeacherLogin.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                            if(response.get("message").toString().equals("success")){
                                                Intent home = new Intent(getApplicationContext(), TeacherHome.class);
                                                startActivity(home);
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            pBar.hide();
                                            throw new RuntimeException(e);
                                        }
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

                    RequestQueue requestQueue = Volley.newRequestQueue(TeacherLogin.this);
                    requestQueue.add(request);
                }
            }
        });
    }

    boolean isEmptyField() {

        if(TextUtils.isEmpty(edtEmail.getText())){
            Toast.makeText(this, "Enter Email.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(edtPassword.getText())){
            Toast.makeText(this, "Enter Password.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    boolean isEmailCorrect() {
        if(Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()){
            return true;
        }
        Toast.makeText(this, "Invalid Email.", Toast.LENGTH_SHORT).show();
        return  false;

    }
}