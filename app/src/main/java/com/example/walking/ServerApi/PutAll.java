package com.example.walking.ServerApi;

import static com.example.walking.BuildConfig.serverKey;

import android.app.DownloadManager;
import android.content.SharedPreferences;

import com.example.walking.JSONParse;
import com.example.walking.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collector;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PutAll {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SERVER = serverKey;

    public HashMap<String, String> putAll(SharedPreferences pref){
        HashMap<String, String> result;
        AllThread th = new AllThread(pref);
        th.start();

        try {
            th.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        result = th.getResult();
        return result;
    }

    private static class AllThread extends Thread{
        SharedPreferences pref;
        String strUrl = SERVER + "/users/update/all";
        HashMap<String, String> result;
        OkHttpClient client = new OkHttpClient();

        public AllThread(SharedPreferences pref){
            this.pref = pref;
        }

        public void run(){
            try {
                JSONObject json = new JSONObject();
                json.put("user_id", pref.getString("user_id",""));
                json.put("today_walk", Integer.parseInt(pref.getString("today_walk","0")));
                json.put("point", Integer.parseInt(pref.getString("point","0")));
                json.put("exp", Integer.parseInt(pref.getString("exp","0")));
                json.put("level", Integer.parseInt(pref.getString("level","1")));
                json.put("total_walk", Integer.parseInt(pref.getString("total_walk","0")));
                json.put("total_dist", Float.parseFloat(pref.getString("total_dist","0")));
                json.put("total_kcal", Integer.parseInt(pref.getString("total_kcal","0")));
                json.put("set_badge", pref.getString("set_badge","0"));

                RequestBody body = RequestBody.create(JSON, json.toString());

                Request request = new Request.Builder()
                        .url(strUrl)
                        .put(body)
                        .build();

                Response response = client.newCall(request).execute();
                result = JSONParse.JsonParse(response.body().string());
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        public HashMap<String, String> getResult() { return this.result; }
    }
}
