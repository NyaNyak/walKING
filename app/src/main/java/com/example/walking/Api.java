package com.example.walking;

import static com.example.walking.BuildConfig.serverKey;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final String SERVER = serverKey;

    public static class HttpAsyncTask extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();
        HashMap<String, String> result;

        @Override
        protected String doInBackground(String... params){
            String result = null;
            String strUrl = params[0];

            try{
                Request request = new Request.Builder()
                        .url(strUrl)
                        .build();
                Response response = client.newCall(request).execute();

                System.out.println(response.body().string());
            }catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        protected HashMap<String, String> putLogin(String... params){
            LoginThread th = new LoginThread(params[0], params[1]);
            th.start();

            try {
                th.join();
                System.out.println("join");
            } catch (Exception e) {
                e.printStackTrace();
            }

            result = th.getResult();
            return result;
        }
    }

    public static class LoginThread extends Thread{
        String id, password;
        String strUrl = SERVER + "/users/login";
        HashMap<String, String> result;
        OkHttpClient client = new OkHttpClient();

        public LoginThread(String id, String password){
            this.id = id;
            this.password = password;
        }

        public void run(){
            try {
                JSONObject json = new JSONObject();
                json.put("user_id", id);
                json.put("password", password);

                RequestBody body = RequestBody.create(JSON, json.toString());

                Request request = new Request.Builder()
                        .url(strUrl)
                        .put(body)
                        .build();

                Response response = client.newCall(request).execute();
                result = JSONParse.JsonParse(response.body().string());
                //result = JSONJsonParse(response.body().string());
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        public HashMap<String, String> getResult(){
            return this.result;
        }
    }
}
