package com.midooabdaim.midooabdaimchat.data.local;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedPrefrance {
    public static SharedPreferences sh;
    public static String REMEMBER = "REMEMBER";
    public static String USER_DATA = "USER_DATA";
    public static String USER_PASSWORD = "USER_PASSWORD";


    public static void setSharedPreferences(Activity activity) {
        if (sh == null) {
            sh = activity.getSharedPreferences(
                    "userData", activity.MODE_PRIVATE);
        }
    }

    public static void savaData(Activity activity, String data_Key, boolean data_Value) {
        if (sh != null) {
            SharedPreferences.Editor editor = sh.edit();
            editor.putBoolean(data_Key, data_Value);
            editor.commit();
        } else {
            setSharedPreferences(activity);
            SharedPreferences.Editor editor = sh.edit();
            editor.putBoolean(data_Key, data_Value);
            editor.commit();
        }
    }

    public static void savaData(Activity activity, String data_Key, String data_Value) {
        if (sh != null) {
            SharedPreferences.Editor editor = sh.edit();
            editor.putString(data_Key, data_Value);
            editor.commit();
        } else {
            setSharedPreferences(activity);
            SharedPreferences.Editor editor = sh.edit();
            editor.putString(data_Key, data_Value);
            editor.commit();
        }
    }

//    public static void savaData(Activity activity,  UserData userData) {
//        if (sh != null) {
//            SharedPreferences.Editor editor = sh.edit();
//            Gson g = new Gson();
//            String jsonString = g.toJson(userData);
//            editor.putString(USER_DATA, jsonString);
//            editor.commit();
//        } else {
//            setSharedPreferences(activity);
//            SharedPreferences.Editor editor = sh.edit();
//            Gson g = new Gson();
//            String jsonString = g.toJson(userData);
//            editor.putString(USER_DATA, jsonString);
//            editor.commit();
//        }
//
//    }

    public static boolean loadDataBoolean(Activity activity, String data_Key) {
        if (sh != null) {

        } else {
            setSharedPreferences(activity);
        }
        return sh.getBoolean(data_Key, false);
    }

    public static String loadDataString(Activity activity, String data_Key) {
        if (sh != null) {

        } else {
            setSharedPreferences(activity);
        }
        return sh.getString(data_Key, "");
    }

//    public static UserData loadData(Activity activity) {
//        if (sh != null) {
//
//        } else {
//            setSharedPreferences(activity);
//
//        }
//        UserData userData = null;
//        try {
//            JSONObject request = new JSONObject(loadData(activity,USER_DATA));
//            Gson g = new Gson();
//            userData = g.fromJson(String.valueOf(request), UserData.class);
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return userData;
//    }

    public static void cleanShard(Activity activity) {
       setSharedPreferences(activity);
        if (sh != null) {
            SharedPreferences.Editor editor = sh.edit();
            editor.clear();
            editor.commit();
        }
    }
}
