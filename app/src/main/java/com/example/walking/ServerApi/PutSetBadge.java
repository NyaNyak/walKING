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

public class PutSetBadge {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String SERVER = serverKey;

    public HashMap<String, String> putSetBadge(String... params){
        HashMap<String, String> result;
        SetBadgeThread th = new SetBadgeThread(params[0], params[1]);
        th.start();

        try{
            th.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        result = th.getResult();
        return result;
    }

    private static class SetBadgeThread extends Thread{
        String id, badge;
        String Url = SERVER + "/users/update/badge";
        HashMap<String, String> result;
        OkHttpClient client = new OkHttpClient();

        public SetBadgeThread(String id, String badge){
            this.id = id;
            this.badge = badge;
        }

        public void run(){
            try {
                JSONObject json = new JSONObject();
                json.put("user_id", id);
                json.put("badge", badge);

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
