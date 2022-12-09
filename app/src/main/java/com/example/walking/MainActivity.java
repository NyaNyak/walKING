package com.example.walking;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.walking.ServerApi.PutAll;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ImageView profilePage, ranking, userProfile, gift;
    TextView initInfo; //임시 정보 리셋 기능 구현을 위해
    Button reward;
    ProgressBar walkProgress, expProgress;
    ConstraintLayout goToShop;
    SensorManager sensorManager;
    Sensor stepCountSensor;
    String saveSteps, saveCounterSteps, saveInitSteps;
    //ActivityResultLauncher<Intent> walkGoalReturn;
    HashMap<String, String> result;

    //유저 정보 변수
    TextView count, goalCount, level, userName, exp, point, distance, calorie;

    //성별 저장을 위한 변수
    Boolean isMale;

    //경험치 계산을 위한 변수
    int calcExp;
    float calc;
    int addExp = 0;
    int totalWalk = 0;
    int curExp = 0;

    //레벨링 계산을 위한 변수
    int userLevel = 1;

    //보상 포인트 지급을 위한 변수
    int addPoint = 0;
    Boolean getReward = false;

    //성별에 따라 달라지는 1km당 평균 걸음
    int avgStepsPer1km = 0;

    //성별에 따라 달라지는 1km당 평균 소모 칼로리
    int avgCalPer1km = 0;

    //칼로리 계산을 위한 변수
    int calorieValue = 0;

    //거리 계산을 위한 변수
    float distValue = 0.00f;

    //뒤로가기 두번으로 앱 종료를 위한 변수
    long backKeyPressedTime = 0;

    //날짜 비교를 위한 변수
    String today;

    //현재 걸음 수를 저장하는 변수
    int currentSteps = 0;
    //하루 걸음수를 계산하기 위해 TYPE_STEP_COUNTER 센서가 측정한 걸음수(휴대폰 부팅 이후부터의 총 걸음수)를 저장하는 변수
    int counterSteps = 0;
    //휴대폰 부팅 여부에 따라서 설정
    int initSteps = 0;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profilePage = (ImageView) findViewById(R.id.profilePage);
        ranking = (ImageView) findViewById(R.id.ranking);
        userProfile = (ImageView) findViewById(R.id.user_profile);
        goToShop = (ConstraintLayout) findViewById(R.id.constraintLayout3);
        reward = (Button) findViewById(R.id.reward);
        gift = (ImageView) findViewById(R.id.gift);
        count = (TextView) findViewById(R.id.count);
        goalCount = (TextView) findViewById(R.id.goalCount);
        walkProgress = (ProgressBar) findViewById(R.id.progress_bar);
        expProgress = (ProgressBar) findViewById(R.id.levelBar);

        //임시 정보 리셋 구현을 위해
        initInfo = (TextView) findViewById(R.id.title2);

        //유저 정보
        level = (TextView) findViewById(R.id.level);
        userName = (TextView) findViewById(R.id.user);
        exp = (TextView) findViewById(R.id.exp);
        point = (TextView) findViewById(R.id.point);
        distance = (TextView) findViewById(R.id.distance);
        calorie = (TextView) findViewById(R.id.calorie);

        //유저 정보 화면에 뿌리기
        SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        userLevel = Integer.parseInt(pref.getString("level",""));
        Log.i("check: ", "current level is "+userLevel);
        userName.setText(pref.getString("user_name",""));
        //성별 설정
        isMale = Boolean.parseBoolean(pref.getString("gender", ""));
        if(isMale){
            avgStepsPer1km = 1300;
            avgCalPer1km = 65;
        }else{
            avgStepsPer1km = 1500;
            avgCalPer1km = 45;
        }

        point.setText(pref.getString("point",""));
        getReward = Boolean.parseBoolean(pref.getString("getReward", ""));
        distance.setText(pref.getString("total_dist",""));
        calorie.setText(pref.getString("total_kcal",""));
        goalCount.setText(pref.getString("walk_goal", ""));
        userProfile.setImageResource(BadgeList.badgeImg()[Integer.parseInt(pref.getString("set_badge","0"))]);

        //임시로 서버에 정보 전달
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result = new PutAll().putAll(pref);
            }
        });


        //로컬에 저장된 걸음 수를 불러와 레이아웃에 표시
        SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
        SharedPreferences prefs = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        saveSteps = prefs.getString("today_walk", "0");
        saveCounterSteps = todaySteps.getString("counterSteps", "0");
        saveInitSteps = todaySteps.getString("initSteps", "0");

        counterSteps = Integer.parseInt(saveCounterSteps);
        currentSteps = Integer.parseInt(saveSteps);
        initSteps = Integer.parseInt(saveInitSteps);
        count.setText(saveSteps);

        //걸음 목표 달성 정도 프로그레스바로 표시
        float progress = (float)currentSteps/Float.parseFloat(goalCount.getText().toString())*100;
        //Toast.makeText(getApplicationContext(),Integer.toString((int)progress), Toast.LENGTH_SHORT).show();
        walkProgress.setProgress((int)progress);

        //exp 계산
        totalWalk = Integer.parseInt(prefs.getString("exp", ""));
        addExp = Integer.parseInt(prefs.getString("total_walk", ""));

        //totalWalk = addExp + currentSteps - (userLevel-1)*1000;
        level.setText(prefs.getString("level",""));
        Log.i("check", "walk is " + totalWalk);
        calc = ((float) totalWalk/(Float.parseFloat(level.getText().toString())*1000))*100;
        Log.i("check", "level*100 is " + ((float) totalWalk/(Float.parseFloat(level.getText().toString())*1000))*100);
        calcExp = (int)calc;
        exp.setText(Integer.toString(calcExp) + " percent");
        expProgress.setProgress((int)calcExp);

        //경험치 달성 정도 프로그레스바로 표시
        expProgress.setProgress((int)calcExp);

        //걸음 센서 사용을 위해 퍼미션체크
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        //걸음 센서 연결
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //디바이스 센서 존재 여부 알림
        if(stepCountSensor == null){
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
        }


        //[임시] 걸음 수 리셋 버튼(헤더 부분 타이틀 "KING" 클릭 리스너)
        initInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walkProgress.setProgress(20);
                currentSteps = 0;
                counterSteps = 0;
                initSteps = 0;
                distValue = 0.00f;
                calorieValue = 0;
                addExp = 0;
                totalWalk = 0;
                getReward = false;

                //로컬에 0으로 초기화된 걸음수 저장
                SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = todaySteps.edit();
                editor.putString("counterSteps", Integer.toString(counterSteps));
                editor.putString("initSteps", Integer.toString(initSteps));
                editor.commit();

                //로컬에 초기화된 거리와 칼로리 저장
                SharedPreferences prefs = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = prefs.edit();
                editor2.putString("today_walk", Integer.toString(currentSteps));
                editor2.putString("total_walk", Integer.toString(addExp));
                editor2.putString("getReward", Boolean.toString(getReward));
                editor2.putString("exp", "0");
                editor2.putString("point","0");
                editor2.putString("level", "1");
                editor2.putString("total_dist", String.format("%.2f", distValue));
                editor2.putString("total_kcal", Integer.toString(calorieValue));
                editor2.commit();

                count.setText(String.valueOf(currentSteps));
                distance.setText(String.format("%.2f", distValue));
                calorie.setText(String.valueOf(calorieValue));
                walkProgress.setProgress(0);
                level.setText(pref.getString("level", ""));
                point.setText(pref.getString("point", ""));
                exp.setText(pref.getString("exp", "") + " percent");
                expProgress.setProgress(0);
            }
        });

        //액티비티 전환
        profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.vertical_enter,R.anim.none);
            }
        });

        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RankingPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.vertical_enter, R.anim.none);
            }
        });


        goToShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PointShop.class);
                startActivity(intent);
                overridePendingTransition(R.anim.horizon_enter, R.anim.none);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        goalCount.setText(pref.getString("walk_goal", ""));
        level.setText(pref.getString("level", ""));
        point.setText(pref.getString("point", ""));
        userProfile.setImageResource(BadgeList.badgeImg()[Integer.parseInt(pref.getString("set_badge","0"))]);

        //현재 날짜 받아오기
        Calendar cal = Calendar.getInstance();
        Date nowDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        today = dateFormat.format(nowDate);
        //Toast.makeText(getApplicationContext(), today, Toast.LENGTH_SHORT).show();
    }

    public void onStart(){
        super.onStart();
        if(stepCountSensor != null){
            //걸음 센서 속도 설정
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void onStop(){
        super.onStop();

        if(sensorManager != null){
            //센서 멈춤
            sensorManager.unregisterListener(this);
        }
    }

    //걸음 수, 칼로리, 거리, 보상 획득 여부 초기화
    public void initTodaySteps(){
        counterSteps = 0;
        initSteps = 0;
        distValue = 0.00f;
        calorieValue = 0;
        //addExp = 0;
        //totalWalk = 0;
        getReward = false;

        //로컬에 0으로 초기화된 걸음수 저장
        SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = todaySteps.edit();
        editor.putString("counterSteps", Integer.toString(counterSteps));
        editor.putString("initSteps", Integer.toString(initSteps));
        editor.commit();

        //로컬에 초기화된 거리와 칼로리 저장, 날짜 변경
        SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = pref.edit();
        editor2.putString("total_walk", Integer.toString(Integer.parseInt(pref.getString("total_walk", ""))+currentSteps));
        currentSteps = 0;
        editor2.putString("today_walk", Integer.toString(currentSteps));
        editor2.putString("getReward", Boolean.toString(getReward));
        editor2.putString("total_dist", String.format("%.2f", distValue));
        editor2.putString("total_kcal", Integer.toString(calorieValue));
        editor2.putString("date", today);
        editor2.commit();

        count.setText(String.valueOf(currentSteps));
        distance.setText(String.format("%.2f", distValue));
        calorie.setText(String.valueOf(calorieValue));
        walkProgress.setProgress(0);

        Toast.makeText(getApplicationContext(), "하루 걸음 수가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
        SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);

        //하루가 지났으면 걸음수 초기화
        if(!today.equals(pref.getString("date", ""))){
            initTodaySteps();
        }

        //센서 동작
        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            //현재까지의 걸음수를 0으로 초기화 했을때의 동작 (초기화 버튼을 클릭하거나, 자정이 되었거나 - 이후 구현할것)
            if(counterSteps < 1){
                //counterSteps : 센서가 측정한 휴대폰 부팅 이후부터의 총 걸음수를 기준값으로 사용하기 위한 것
                counterSteps = (int) sensorEvent.values[0];

                //경험치 누적을 위해
                addExp = Integer.parseInt(pref.getString("exp", ""));

                //로컬에 값 저장
                SharedPreferences.Editor editor = todaySteps.edit();
                editor.putString("counterSteps", Integer.toString(counterSteps));
                editor.commit();
            }
            //휴대폰 부팅 시 센서가 초기화됨. 따라서 현재 걸음 수 계산에 쓰이는 변수들을 적절히 조정
            String lastCount = todaySteps.getString("counterSteps", "0");
            if(sensorEvent.values[0] <= Integer.parseInt(lastCount)){
                counterSteps = (int) sensorEvent.values[0];
                initSteps = Integer.parseInt(pref.getString("today_walk", "0"));
                SharedPreferences.Editor editor = todaySteps.edit();
                editor.putString("counterSteps", Integer.toString(counterSteps));
                editor.putString("initSteps", Integer.toString(initSteps));
                editor.commit();
            }

            //[현재 걸음수를 표시하기 위한 수식]
            //걸음 센서는 휴대폰을 종료하지 않는 이상, 앱 종료 여부와 상관없이 계속 걸음수를 누적중.
            //즉 현재 걸음 수는 센서가 측정한 총 걸음에서 기준값으로 저장해둔 counterSteps을 빼서 구한다.
            currentSteps = initSteps + (int) sensorEvent.values[0] - counterSteps;
            count.setText(Integer.toString(currentSteps));

            float progress = (float)currentSteps/Float.parseFloat(goalCount.getText().toString())*100;
            //Toast.makeText(getApplicationContext(),Integer.toString((int)progress), Toast.LENGTH_SHORT).show();
            walkProgress.setProgress((int)progress);

            int curLevel = Integer.parseInt(level.getText().toString());

            int totalExp = 1000*(curLevel-1)*(curLevel); // 원래 100* ... /2 이라 그냥 50만 곱하기

            //걸음수만큼 경험치 증가
            // 기존은 이전렙-1 * 1000 을 넘으면 그만큼 빼서 계산하는걸로 했는데
            // 레벨은 누적이라 totalExp를 선언하고 사용했두
            //나중에 1000 곱하는걸로 수정
            if((addExp + currentSteps) >= totalExp){
                totalWalk = addExp + currentSteps - totalExp;
            }else{
                totalWalk = addExp + currentSteps;
            }

            calc = ((float) totalWalk/(Float.parseFloat(level.getText().toString())*1000))*100;
            calcExp = (int)calc;
            //Toast.makeText(getApplicationContext(), Integer.toString(Integer.parseInt((pref.getString("exp", ""))+addExp)/(Integer.parseInt(level.getText().toString())*10)), Toast.LENGTH_SHORT).show();
            exp.setText(Integer.toString(calcExp) + " percent");
            expProgress.setProgress((int)calcExp);

            //경험치 다 채우면 레벨업
            while(totalWalk >= curLevel*1000){
                totalWalk -= curLevel*1000;
                curLevel+=1;
            }
            SharedPreferences.Editor editor2 = pref.edit();
            editor2.putString("exp", Integer.toString(totalWalk));
            editor2.putString("level", Integer.toString(curLevel));
            editor2.putString("today_walk", Integer.toString(currentSteps));
            //editor2.putString("total_walk", pref.getString("exp",""));
            editor2.commit();
            userLevel = Integer.parseInt(pref.getString("level", ""));
            //totalWalk = addExp + currentSteps - (userLevel-1)*1000;
            level.setText(pref.getString("level",""));
            Log.i("check", "walk is " + totalWalk);
            calc = ((float) totalWalk/(Float.parseFloat(level.getText().toString())*1000))*100;
            Log.i("check", "level*100 is " + ((float) totalWalk/(Float.parseFloat(level.getText().toString())*1000))*100);
            calcExp = (int)calc;
            exp.setText(Integer.toString(calcExp) + " percent");
            expProgress.setProgress((int)calcExp);

            //목표 달성 시 포인트 지급
            int goal = Integer.parseInt(goalCount.getText().toString());
            //int goal = 100;
            if(!getReward && currentSteps >= goal ){
                reward.setEnabled(true);
                gift.setVisibility(View.VISIBLE);
                reward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addPoint = goal/10 + (goal - 1000)/1000*50;
                        getReward = true;
                        SharedPreferences.Editor editor2 = pref.edit();
                        editor2.putString("point", Integer.toString(Integer.parseInt(point.getText().toString())+addPoint));
                        editor2.putString("getReward", Boolean.toString(getReward));
                        editor2.commit();
                        point.setText(pref.getString("point",""));
                        Toast.makeText(getApplicationContext(), addPoint + "포인트 획득!", Toast.LENGTH_SHORT).show();
                        reward.setEnabled(false);
                        gift.setVisibility(View.INVISIBLE);
                    }
                });
            }
            else{
                reward.setEnabled(false);
                gift.setVisibility(View.INVISIBLE);
            }

            //걸음 수로 거리 계산
            distValue = (float) currentSteps / avgStepsPer1km;
            distance.setText(String.format("%.2f", distValue));

            //거리로 칼로리 계산
            calorieValue = Math.round(distValue * avgCalPer1km);
            calorie.setText(Integer.toString(calorieValue));

            Log.i("log: ", "New step detected by STEP_COUNTER sensor. Total step count: " + currentSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //why am i empty?
    }

    @Override
    protected void onPause() {
        //액티비티 종료 시 로컬에 걸음수, 거리, 칼로리 저장
        SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = todaySteps.edit();
        editor.putString("counterSteps", Integer.toString(counterSteps));
        editor.putString("initSteps", Integer.toString(initSteps));
        editor.commit();

        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (System.currentTimeMillis() > backKeyPressedTime + 2500){
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            finishAffinity();
        }
    }
}