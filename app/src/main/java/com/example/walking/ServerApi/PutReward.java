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

public class PutReward {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String SERVER = serverKey;

    public Boolean putReward(String id, int point){
        RewardThread th = new RewardThread(id, point);
        th.start();

        try{
            th.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return th.getResult();
    }

    private static class RewardThread extends Thread{
        String id;
        int point;
        String Url = SERVER + "/users/update/reward";
        HashMap<String, String> result;
        OkHttpClient client = new OkHttpClient();

        public RewardThread(String id, int point){
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

        public Boolean getResult() {
            return !result.containsKey("detail");
        }
    }
}
