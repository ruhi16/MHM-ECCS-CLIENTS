package com.education.mhmeccsclients;

import android.content.SharedPreferences;

public class TokenManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static TokenManager INSTANCE = null;

    private TokenManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    public static synchronized TokenManager getInstance(SharedPreferences prefs){
        if(INSTANCE == null){
            INSTANCE = new TokenManager(prefs);
        }
        return INSTANCE;
    }

    public void saveToekn(Token token){
        editor.putString("ACCESS_TOKEN", token.getAccessToken()).commit();
        editor.putString("REFRESH_TOKEN", token.getRefreshToken()).commit();
    }
    public void deleteToekn(){
        editor.remove("ACCESS_TOKEN").commit();
        editor.remove("REFRESH_TOKEN").commit();
    }
    public Token getToken(){
        Token token = new Token();
        token.setAccessToken(prefs.getString("ACCESS_TOKEN",null));
        token.setRefreshToken(prefs.getString("REFRESH_TOKEN",null));
        return token;
    }



}
