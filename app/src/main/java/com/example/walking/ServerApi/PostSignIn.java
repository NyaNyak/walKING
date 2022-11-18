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

public class PostSignIn {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final String SERVER = serverKey;

    public HashMap<String, String> postSignIn(String... params){
        HashMap<String, String> result;
        SignThread th = new SignThread(params[0], params[1], params[2], params[3]);
        th.start();

        try {
            th.join();
        }catch (Exception e) {
            e.printStackTrace();
        }

        result = th.getResult();
        System.out.println(result);
        return result;
    }

    private static class SignThread extends Thread{
        String id, password, name;
        boolean gender;
        String strUrl = SERVER + "/users/create";
        HashMap<String, String> result;
        OkHttpClient client = new OkHttpClient();

        public SignThread(String id, String password, String name, String gender){
            this.id = id;
            this.password = password;
            this.name = name;
            this.gender = Boolean.parseBoolean(gender);
        }

        public void run(){
            try {
                JSONObject json = new JSONObject();
                json.put("user_id", id);
                json.put("pwd", password);
                json.put("user_name", name);
                json.put("gender", gender);

                RequestBody body = RequestBody.create(JSON, json.toString());

                Request request = new Request.Builder()
                        .url(strUrl)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                result = JSONParse.JsonParse(response.body().string());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public HashMap<String, String> getResult(){
            return this.result;
        }
    }
}
