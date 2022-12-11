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

public class PutNewBadge {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String SERVER = serverKey;

    public HashMap<String, String> putNewBadge(String id, int point){
        HashMap<String, String> result;
        // id, point 입력
        NewBadgeThread th = new NewBadgeThread(id, point);
        th.start();

        try{
            th.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        result = th.getResult();
        return result;
    }

    private static class NewBadgeThread extends Thread{
        String id;
        int point;
        String Url = SERVER + "/users/update/new_badge";
        HashMap<String, String> result;
        OkHttpClient client = new OkHttpClient();

        public NewBadgeThread(String id, int point){
            this.id = id;
            this.point = point;
        }

        public void run(){
            try {
                JSONObject json = new JSONObject();
                json.put("user_id", id);
                json.put("point", point);

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
