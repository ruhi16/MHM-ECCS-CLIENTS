package com.education.mhmeccsclients;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class ActivityLogin extends AppCompatActivity {
    TextInputLayout inLyUserEmail, inLyUserPassword;
    TextInputEditText userEmail, userPassword;

    ProgressBar progressBar;

    Button btnLogin;

    TokenManager tokenManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inLyUserEmail = (TextInputLayout) findViewById(R.id.act_log_ly_user_email);
        userEmail = (TextInputEditText) findViewById(R.id.act_log_user_email);

        inLyUserPassword = (TextInputLayout) findViewById(R.id.act_log_ly_user_password);
        userPassword = (TextInputEditText) findViewById(R.id.act_log_user_password);

        btnLogin = (Button) findViewById(R.id.act_log_btn_user_login);
        progressBar = findViewById(R.id.act_login_prgsbar);



        final AwesomeValidation awesomeValidation = new AwesomeValidation(TEXT_INPUT_LAYOUT);
        awesomeValidation.addValidation(inLyUserEmail, android.util.Patterns.EMAIL_ADDRESS, "Enter a valid email address!");
        awesomeValidation.addValidation(inLyUserPassword, "[a-zA-Z0-9]{6,}+", "Password must be validate!");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    Log.d("testapp", "LoginPage: Local Awesome Validation Successfull");
                    userPassword.setFocusable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    loginrUser();

                } else {
                    Log.d("testapp", "LoginPage: Local Awesome Validation Un-Successfull");

                }
            }
        });

    }




    public void loginrUser() {
        Log.d("testapp", "LoginPage: loginUser() started... ");

        final String email = userEmail.getText().toString().trim();
        final String password = userPassword.getText().toString().trim();

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        final JSONObject jsonObject = new JSONObject(params);

        String api_url = "https://hsresults.herokuapp.com/api/login";
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, api_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("testapp", "Login Page: Successful Response" + response);
                // write the crediencials in sharedPreference.....
                tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

                Gson gson = new Gson();
                Token token = gson.fromJson(response.toString(), Token.class);

                tokenManager.saveToekn(token);

                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //goto Member Dashboard
                Intent dashboardIntent = new Intent(ActivityLogin.this, ActivityDashboard.class);
                ActivityLogin.this.startActivity(dashboardIntent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("testapp", "LoginPage: Response Error... ");

                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);



                if (error.networkResponse != null) {
//                    Log.d("testapp", "Un-Successful Response Line 1: " + error.networkResponse.statusCode);

                    error.printStackTrace();
                    try {
                        String body = new String(error.networkResponse.data, "UTF-8");
                        Log.d("testapp", "Un-Success Response Line 4: " );

                        ApiErrors apiError = new ApiErrors();
                        apiError.setMessage("Invalid authorization, expires token or revoked,  ...");
                        Map<String, List<String>> errorLogin = new HashMap<String, List<String>>();
                        errorLogin.put("email", Arrays.asList("Email does not exist..."));
                        errorLogin.put("password", Arrays.asList("Password does not matched..."));
                        apiError.setErrors(errorLogin);
//                        for(Map.Entry<String, List<String>> err : apiError.getErrors().entrySet()){
//                            Log.d("testapp", err.getKey()+"=>"+err.getValue());
//
//                        }
                        showRestErrors(apiError);

                    } catch (UnsupportedEncodingException e) {
                        Toast.makeText(getApplicationContext(), "REST API Error Message JSON Parsing Error!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Technical or Connection Error!!!", Toast.LENGTH_SHORT).show();
                }


            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");

                return headers;
            }

        };


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }





    private void showRestErrors(ApiErrors apiErrors) {
        for(Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()){
            if(error.getKey().equals("email")){
                Log.d("testapp", "LoginPage: REST ERROR: email Field");
                inLyUserEmail.setError(apiErrors.getErrors().get("email").get(0));
            }
            if(error.getKey().equals("password")){
                Log.d("testapp", "LoginPage: REST ERROR: password Field");
                inLyUserPassword.setError(apiErrors.getErrors().get("password").get(0));
            }
        }
    }



}
