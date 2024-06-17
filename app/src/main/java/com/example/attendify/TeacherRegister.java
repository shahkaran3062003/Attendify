package com.example.attendify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;


public class TeacherRegister extends AppCompatActivity {


    MaterialButton btnLogin,btnSignUp;
    ImageButton btnBack;

    TextInputEditText edtName,edtEmail,edtContact,edtPassword,edtCnfPassword;

    CustomProgressBar pBar;
    String baseAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register);
        btnSignUp = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtContact = findViewById(R.id.edtContact);
        edtPassword = findViewById(R.id.edtPassword);
        edtCnfPassword = findViewById(R.id.edtCnfPassword);
        pBar  = new CustomProgressBar(this);
        baseAPI = getString(R.string.baseApi);




        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(!isEmptyField() && isNameCorrect() && isEmailCorrect() && isContactCorrect() && isPasswordCorrect()){
                        JSONObject params = new JSONObject();
                        try {
                            params.put("name",edtName.getText().toString());
                            params.put("email",edtEmail.getText().toString());
                            params.put("contact",edtContact.getText().toString());
                            params.put("password",edtPassword.getText().toString());

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        pBar.show();

                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.POST,
                                baseAPI + "/teacher/register.php",
                                params,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        pBar.hide();
                                        if(response.has("message")){
                                            try {
                                                Toast.makeText(TeacherRegister.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                                if(response.get("message").toString().equals("success")){
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

                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        queue.add(request);
                    }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    boolean isEmptyField(){

        if(TextUtils.isEmpty(edtName.getText())){
            Toast.makeText(this, "Enter Name.", Toast.LENGTH_SHORT).show();
            return  true;
        }

        if(TextUtils.isEmpty(edtEmail.getText())){
            Toast.makeText(this, "Enter Email.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(edtContact.getText())){
            Toast.makeText(this, "Enter Contact", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(edtPassword.getText())){
            Toast.makeText(this, "Enter Password.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(edtCnfPassword.getText())){
            Toast.makeText(this, "Enter Confirm Password.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return  false;
    }

    boolean isPasswordCorrect(){
        int len = edtPassword.getText().length();
        if(len <8 || len > 16){
            Toast.makeText(this, "Password Length should between 8 to 16.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edtPassword.getText().toString().equals(edtCnfPassword.getText().toString())){
            return true;
        }
        Toast.makeText(this, "Password don't Match.", Toast.LENGTH_SHORT).show();
        return false;
    }

    boolean isEmailCorrect() {
        if(Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()){
            return true;
        }
        Toast.makeText(this, "Invalid Email.", Toast.LENGTH_SHORT).show();
        return  false;

    }

    boolean isContactCorrect(){
        if(edtContact.getText().length()==10){
            return true;
        }
        Toast.makeText(this, "Invalid Contact Number.", Toast.LENGTH_SHORT).show();
        return false;
    }

    boolean isNameCorrect(){

        String name = edtName.getText().toString();

        if(!name.matches("[a-zA-Z ]*")){
            Toast.makeText(this, "Invalid Name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }

}