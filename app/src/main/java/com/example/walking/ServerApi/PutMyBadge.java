package com.example.walking.ServerApi;

import static com.example.walking.BuildConfig.serverKey;

import com.example.walking.JSONParse;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PutMyBadge {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String SERVER = serverKey;

    public long putMyBadge(String id){
        HashMap<String, String> result;
        MyBadgeThread th = new MyBadgeThread(id);
        th.start();

        try{
            th.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        result = th.getResult();
        return Long.parseLong(result.get("badge"));
    }

    private static class MyBadgeThread extends Thread{
        String id;
        String Url = SERVER + "/users/my_badge";
        HashMap<String, String> result;
        OkHttpClient client = new OkHttpClient();

        public MyBadgeThread(String id){
            this.id = id;
        }

        public void run(){
            try {
                JSONObject json = new JSONObject();
                json.put("user_id", id);

                RequestBody body = RequestBody.create(JSON, json.toString());

                Request request = new Request.Builder()
                        .url(Url)
                        .put(body)
                        .build();

                Response response = client.newCall(request).execute();
                result = JSONParse.JsonParse(response.body().string());
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        public HashMap<String, String> getResult() {
            return this.result;
        }
    }
}
