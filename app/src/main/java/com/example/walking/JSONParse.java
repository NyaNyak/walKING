package com.example.walking;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;

public class JSONParse {
    static HashMap<String, String> JsonParse(String jsonStr) {
        HashMap<String, String> res = new HashMap<>();
        try {
            JSONObject jo = new JSONObject(jsonStr);

            Iterator it = jo.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                res.put(key, jo.getString(key));
                //System.out.println("key: " + key + ", value: " + jo.getString(key));
            }
            //System.out.println(jo.toString());
            return res;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
