package com.education.mhmeccsclients;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mySingletonInstance;
    private RequestQueue requestQueue;
    private static Context myContext;

    private MySingleton(Context context){
        myContext = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(myContext.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }

    public static synchronized MySingleton getInstance(Context context){
        if(mySingletonInstance == null){
            mySingletonInstance = new MySingleton(context);
        }

        return mySingletonInstance;
    }

}
