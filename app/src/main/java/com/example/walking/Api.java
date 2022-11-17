package com.example.walking;

import static com.example.walking.BuildConfig.serverKey;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static class HttpAsyncTask extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();

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

        protected String putLogin(String... params){
            String result = null;
            String strUrl = serverKey;

            new Thread(() -> {
                try {
                    JSONObject json = new JSONObject();
                    json.put("user_id", params[0]);
                    json.put("password", params[1]);

                    RequestBody body = RequestBody.create(JSON, json.toString());

                    Request request = new Request.Builder()
                            .url(strUrl)
                            .put(body)
                            .build();

                    Response response = client.newCall(request).execute();
                    System.out.println(response.body().string());

                } catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
            return null;
        }

        protected String getAll(String... params){
            String result = null;
            String strUrl = params[0];

            new Thread (() -> {
                try {
                    Request request = new Request.Builder()
                            .url(strUrl)
                            .build();

                    Response response = client.newCall(request).execute();
                    System.out.println(response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            return null;
        }
    }

}
