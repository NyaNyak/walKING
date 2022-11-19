package com.example.walking;

import android.app.Activity;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class UserInfo extends AppCompatActivity {
    public void userInfo(SharedPreferences pref, SharedPreferences.Editor editor, HashMap<String, String> result){
        for (String key : result.keySet()){
            System.out.println(key + " " + result.get(key));
            editor.putString(key, result.get(key));
        }
        editor.apply();
    }
}
