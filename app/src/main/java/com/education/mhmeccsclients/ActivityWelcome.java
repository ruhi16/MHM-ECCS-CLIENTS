package com.education.mhmeccsclients;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityWelcome extends AppCompatActivity {
    Toolbar toolbar;
    Button btnRegister, btnLogin;

    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        pref = getSharedPreferences("prefs", MODE_PRIVATE);
        if(pref.contains("ACCESS_TOKEN")){
            Intent dashboardIntent = new Intent(ActivityWelcome.this, ActivityDashboard.class);
            ActivityWelcome.this.startActivity(dashboardIntent);
        }





        toolbar = findViewById(R.id.act_wel_toolbar);
        setSupportActionBar(toolbar);


        btnRegister = (Button) findViewById(R.id.act_wel_btn_register);
        btnLogin = (Button) findViewById(R.id.act_wel_btn_login);





        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent welcomeIntent = new Intent(ActivityWelcome.this, ActivityRegister.class);
                ActivityWelcome.this.startActivity(welcomeIntent);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(ActivityWelcome.this, ActivityLogin.class);
                ActivityWelcome.this.startActivity(loginIntent);
            }
        });

    }
}
