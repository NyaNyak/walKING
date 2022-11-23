package com.example.walking;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver(){}

    NotificationManager manager;
    NotificationCompat.Builder builder;

    //notification 채널 설정
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        builder = null;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        //알람창 클릭시 메인화면으로
        Intent intent2 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        //걸음 수 초기화
        SharedPreferences todaySteps = context.getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = todaySteps.edit();
        editor.putString("steps", "0");
        editor.putString("counterSteps", "0");
        editor.putString("initSteps", "0");
        editor.commit();

        builder.setContentTitle("일일 걸음 수가 초기화되었습니다.");
        builder.setSmallIcon(R.drawable.icon2);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        manager.notify(1, notification);
    }
}
