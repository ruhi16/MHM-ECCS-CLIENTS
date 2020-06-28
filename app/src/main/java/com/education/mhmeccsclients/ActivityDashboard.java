package com.education.mhmeccsclients;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ActivityDashboard extends AppCompatActivity {
    TextView textView;
    Button btnLogout;
    ProgressBar progressBar;

    //Token token;
    private TokenManager tokenManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textView = (TextView) findViewById(R.id.act_das_text1) ;
        btnLogout = (Button) findViewById(R.id.act_das_btn_logout);

        progressBar = findViewById(R.id.act_das_prgsbar);



        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        //Log.d("testapp", "Access Token: " +token.getAccessToken() );
        //display user info..
        getUserInfo();




        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("testapp", "ActivityDashboard: Started...");
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                getUserLogout();
            }
        });
    }





    public void getUserInfo() {
        final Token token = tokenManager.getToken();
        String api_url = "https://hsresults.herokuapp.com/api/user";
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, api_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("testapp", "User Info: Successful Response" + response);
                try {
                    JSONObject user = response.getJSONObject("user");
                    textView.setText("Name:" +user.getString("name")+", \nEmail: "+user.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("testapp", "User Info: Un-Successful Response");
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();


                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization","Bearer "+token.getAccessToken().trim());

                return headers;
            }

        };


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }




    public void getUserLogout() {
        Log.d("testapp", "getUserLogout Started...");


        Map<String, String> params = new HashMap<String, String>();
        params.put("email", "abcd@abc.cd");
        params.put("password", "abcd");
        final JSONObject jsonObject = new JSONObject(params);

        final Token token = tokenManager.getToken();
        String api_url = "https://hsresults.herokuapp.com/api/logout";
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, api_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("testapp", "Successful Response Logout" + response.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                tokenManager.deleteToekn();

                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Toast.makeText(getApplicationContext(), "Successfully Logged Out.",Toast.LENGTH_LONG).show();


                Intent dashboardIntent = new Intent(ActivityDashboard.this, ActivityWelcome.class);
                ActivityDashboard.this.startActivity(dashboardIntent);
                finish();



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("testapp", "Un-Successful Response Logout: ");
                error.printStackTrace();

                Toast.makeText(getApplicationContext(), "Technical Error, Cant Logout!!!",Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();


                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization","Bearer "+token.getAccessToken().trim());

                return headers;
            }

        };


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }



}
