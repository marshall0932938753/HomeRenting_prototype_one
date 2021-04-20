package com.example.homerenting_prototype_one.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.homerenting_prototype_one.model.User;

import org.json.JSONException;
import org.json.JSONObject;

public class SessionManager {
    private String TAG = "SessionMananger";

    SharedPreferences pref;

    Editor editor;

    Context _context;

    @SuppressLint("StaticFieldLeak")
    private static SessionManager INSTANCE = null;

    User user;

    @SuppressLint("CommitPrefEdits")
    private SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences("MovingAppPref", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static SessionManager getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new SessionManager(context);
        }
        return INSTANCE;
    }

    public void createLoginSession(String id, String userJsonObjStr){
        editor.putBoolean("IsLoggedIn" , true);
        editor.putString("company_id", id);
        editor.putString("company", userJsonObjStr);
        editor.commit();
    }

    public User getUserDetail(){
        if(user == null) user = new User();
        String userJsonObjStr = pref.getString("company", null);
        if(userJsonObjStr != null){
            try {
                JSONObject obj = new JSONObject(userJsonObjStr);
                user.setId(obj.getString("company_id"));
                user.setName(obj.getString("company_name"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "company detail data no json");
            }
        }
        return user;
    }
}
