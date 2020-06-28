package com.education.mhmeccsclients;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class ActivityRegister extends AppCompatActivity {
    TextInputLayout inLyUserName, inLyUserEmail, inLyUserPassword;
    TextInputEditText userName, userEmail, userPassword;

    Button btnRegister;
    ProgressBar progressBar;

    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inLyUserName = (TextInputLayout) findViewById(R.id.act_reg_ly_user_name);
        userName = (TextInputEditText) findViewById(R.id.act_reg_user_name);

        inLyUserEmail = (TextInputLayout) findViewById(R.id.act_reg_ly_user_email);
        userEmail = (TextInputEditText) findViewById(R.id.act_reg_user_email);

        inLyUserPassword = (TextInputLayout) findViewById(R.id.act_reg_ly_user_password);
        userPassword = (TextInputEditText) findViewById(R.id.act_reg_user_password);

        btnRegister = (Button) findViewById(R.id.act_reg_btn_register);
        progressBar = findViewById(R.id.act_reg_prgsbar);


        final AwesomeValidation awesomeValidation = new AwesomeValidation(TEXT_INPUT_LAYOUT);

        awesomeValidation.addValidation(inLyUserName, "[a-zA-Z\\s]+", "Name must not be empty!");
        awesomeValidation.addValidation(inLyUserEmail, android.util.Patterns.EMAIL_ADDRESS, "Enter a valid email address!");
        awesomeValidation.addValidation(inLyUserPassword, "[a-zA-Z0-9]{6,}+", "Password must be validate!");


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    Log.d("testapp", "Local Awesome Validation Successfull");
                    userPassword.setFocusable(false  );
                    progressBar.setVisibility(View.VISIBLE);

                    registerUser();
                } else {
                    Log.d("testapp", "Local Awesome Validation Un-Successfull");

                }
            }
        });

        Log.d("testapp", "Register Started");
    }


    public void registerUser() {
        final String name = userName.getText().toString().trim();
        final String email = userEmail.getText().toString().trim();
        final String password = userPassword.getText().toString().trim();

        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("password_confirmation", password);
        final JSONObject jsonObject = new JSONObject(params);

        String api_url = "https://hsresults.herokuapp.com/api/register";
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, api_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("testapp", "Successful Response" + response);
                // write the crediencials in sharedPreference.....
                tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

                Gson gson = new Gson();
                Token token = gson.fromJson(response.toString(), Token.class);

                Log.d("testapp", "getToken: "+token.getAccessToken());
                tokenManager.saveToekn(token);

                progressBar.setVisibility(View.GONE);
                //goto Member Dashboard
                Intent dashboardIntent = new Intent(ActivityRegister.this, ActivityDashboard.class);
                ActivityRegister.this.startActivity(dashboardIntent);


//                Token token1 = tokenManager.getToken();
//                Log.d("testapp", "getToken: "+token1.getAccessToken());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
//                    Log.d("testapp", "Un-Successful Response Line 1: " + error.networkResponse.statusCode);

                    error.printStackTrace();
                    try {
                        String body = new String(error.networkResponse.data, "UTF-8");
                        Log.d("testapp", "Un-Success Response Line 4: " + body);

                        Gson gson = new Gson();
                        ApiErrors apiErrors = gson.fromJson(body, ApiErrors.class);

                        Log.d("testapp", "Message: " + apiErrors.getMessage());
//                        Log.d("testapp", "Errors: " + apiErrors.getErrors().get("name").get(0));
                        showRestErrors(apiErrors);

                    } catch (UnsupportedEncodingException e) {
                        Toast.makeText(ActivityRegister.this, "REST API Error Message JSON Parsing Error!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityRegister.this, "Technical or Connection Error!!!", Toast.LENGTH_SHORT).show();
                }


            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                //headers.put("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIyIiwianRpIjoiOWM2MmU5NDgyZDEwNGRhYTM4YzJiMzlmZjc1OGIxY2Y0MGQxMDQwN2RmNzRiOWNjNjNhYzAxODc2NmEyMzNlOTBiYmZlNTg1MzFiZGI4ZGEiLCJpYXQiOjE1OTIyMDEyMzAsIm5iZiI6MTU5MjIwMTIzMCwiZXhwIjoxNjIzNzM3MjMwLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.GQEuH-8yB4YihJzo7XTPcQib4NuPwDWHH4o6Xy-oxcrceI5Gi2YHmD3UUPkgLZsb9S5tT9Tw-1bgAF2pMyEtgHYJgtt-p3UFaQIms0L13ZYEFgXeQNXYA17PiyXBWHF6hSQMeHcR6I2LNw0PkTCRbtLs2oqW4wCKBiB1WRB16eCh5HmJDnsdRD3Q9Z7DeCUClrArFf_fzE1A8UGCoWmza1VPNRb9vSum-gvW9SuMKjs_AvST2xzouyT1AigJwnpoQOVv91uX3LS6K27ZkBNIDMexAWsm0TFzAlqHGJvmwl7Q-nwokkjkQGr30rQQ5iOs-VjBpc3e71596KQBJDltaIqIYc9_CUI251hRZmr3ImxZOKzTB8XhmOco94cTJaevbm5yeY-PsfV2TMEim3BDznTKZ3VwPEwNorzVzTE4L4peh0Vqkr1tHke-ioSMj_2x9EmFi-U0tsClavncDyu8gp9xSS4yfPd8HfocACJmI7elEv76tWFDCoBFJ2EO6ciEyP-XBuRFP0srGIYiBFvJ_arehA8uhNLef3n5U9Gu8ssDoNuQfcOHigjDEhm-fB5CMSY6qhRnSHMXBkbD4pogqSpjrZIGuD_CXMExviHou1UZ9N7RgoMeBd7QcgQL5PaDSZUHNBXtTVqAQlBagyys6fk5XGpB0pnNgDkLxd8Ocrk");

                return headers;
            }

        };


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    private void showRestErrors(ApiErrors apiErrors) {
        for(Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()){
            if(error.getKey().equals("name")){
                Log.d("testapp", "REST ERROR: name Field");
                inLyUserName.setError(apiErrors.getErrors().get("name").get(0));
            }
            if(error.getKey().equals("email")){
                Log.d("testapp", "REST ERROR: email Field");
                inLyUserEmail.setError(apiErrors.getErrors().get("email").get(0));
            }
            if(error.getKey().equals("password")){
                Log.d("testapp", "REST ERROR: password Field");
                inLyUserPassword.setError(apiErrors.getErrors().get("password").get(0));
            }
        }
    }




}