package com.example.walking.ServerApi;

import static com.example.walking.BuildConfig.serverKey;

import android.app.DownloadManager;

import com.example.walking.JSONParse;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PutWalkGoal {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String SERVER = serverKey;

    public HashMap<String, String> putWalkGoal(String... params){
        HashMap<String, String> result;
        WalkGoalThread th = new WalkGoalThread(params[0], Integer.parseInt(params[1]));
        th.start();

        try {
            th.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        result = th.getResult();
        return result;
    }

    private static class WalkGoalThread extends Thread{
        String id;
        int goal;
        String strUrl = SERVER + "/users/update/walk_goal";
        HashMap<String, String> result;
        OkHttpClient client = new OkHttpClient();

        public WalkGoalThread(String id, int goal){
            this.id = id;
            this.goal = goal;
        }

        public void run(){
            try {
                JSONObject json = new JSONObject();
                json.put("user_id", id);
                json.put("walk_goal", goal);

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
