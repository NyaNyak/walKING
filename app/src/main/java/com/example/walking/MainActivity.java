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

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ImageView profilePage, ranking, userProfile, gift;
    TextView count, goalCount;
    Button reward;
    ProgressBar walkProgress;
    ConstraintLayout goToShop;
    SensorManager sensorManager;
    Sensor stepCountSensor;
    String saveSteps, saveCounterSteps, saveInitSteps;
    ActivityResultLauncher<Intent> walkGoalReturn;

    //유저 정보 변수
    TextView level, userName, exp, point, distance, calorie;

    //뒤로가기 두번으로 앱 종료를 위한 변수
    long backKeyPressedTime = 0;

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
        count = (TextView) findViewById(R.id.count);
        goalCount = (TextView) findViewById(R.id.goalCount);
        walkProgress = (ProgressBar) findViewById(R.id.progress_bar);

        //유저 정보
        level = (TextView) findViewById(R.id.level);
        userName = (TextView) findViewById(R.id.user);
        exp = (TextView) findViewById(R.id.exp);
        point = (TextView) findViewById(R.id.point);
        distance = (TextView) findViewById(R.id.distance);
        calorie = (TextView) findViewById(R.id.calorie);

        //유저 정보 화면에 뿌리기
        SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        level.setText(pref.getString("level",""));
        userName.setText(pref.getString("user_name",""));
        //exp는 일단 넘기고
        point.setText(pref.getString("point",""));
        distance.setText(pref.getString("total_dist",""));
        calorie.setText(pref.getString("total_kcal",""));
        goalCount.setText(pref.getString("walk_goal", ""));

        //변경한 목표 불러오기 위한 인텐트
        walkGoalReturn = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            int res = data.getIntExtra("Goal",1000);
                            goalCount.setText(Integer.toString(res));
                            //Toast.makeText(getApplicationContext(),Integer.toString(res),Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        //로컬에 저장된 걸음 수를 불러와 레이아웃에 표시
        SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
        saveSteps = todaySteps.getString("steps", "0");
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

        //[임시] 걸음 수 리셋 버튼(이후에 보상받기버튼으로 재구현)
        reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walkProgress.setProgress(20);
                currentSteps = 0;
                counterSteps = 0;
                initSteps = 0;
                //로컬에 0으로 초기화된 걸음수 저장
                SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = todaySteps.edit();
                editor.putString("steps", Integer.toString(currentSteps));
                editor.putString("counterSteps", Integer.toString(counterSteps));
                editor.putString("initSteps", Integer.toString(initSteps));
                editor.commit();
                count.setText(String.valueOf(currentSteps));
                walkProgress.setProgress(0);
            }
        });

        //액티비티 전환 시에도 걸음 수 로컬에 저장
        profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = todaySteps.edit();
                editor.putString("steps", Integer.toString(currentSteps));
                editor.putString("counterSteps", Integer.toString(counterSteps));
                editor.putString("initSteps", Integer.toString(initSteps));
                editor.commit();

                float progress = (float)currentSteps/Float.parseFloat(goalCount.getText().toString())*100;
                //Toast.makeText(getApplicationContext(),Integer.toString((int)progress), Toast.LENGTH_SHORT).show();
                walkProgress.setProgress((int)progress);

                Intent intent = new Intent(getApplicationContext(), UserPage.class);
                walkGoalReturn.launch(intent);
                overridePendingTransition(R.anim.vertical_enter,R.anim.none);
            }
        });

        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = todaySteps.edit();
                editor.putString("steps", Integer.toString(currentSteps));
                editor.putString("counterSteps", Integer.toString(counterSteps));
                editor.putString("initSteps", Integer.toString(initSteps));
                editor.commit();

                float progress = (float)currentSteps/Float.parseFloat(goalCount.getText().toString())*100;
                //Toast.makeText(getApplicationContext(),Integer.toString((int)progress), Toast.LENGTH_SHORT).show();
                walkProgress.setProgress((int)progress);

                Intent intent = new Intent(getApplicationContext(), RankingPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.vertical_enter, R.anim.none);
            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = todaySteps.edit();
                editor.putString("steps", Integer.toString(currentSteps));
                editor.putString("counterSteps", Integer.toString(counterSteps));
                editor.putString("initSteps", Integer.toString(initSteps));
                editor.commit();

                float progress = (float)currentSteps/Float.parseFloat(goalCount.getText().toString())*100;
                //Toast.makeText(getApplicationContext(),Integer.toString((int)progress), Toast.LENGTH_SHORT).show();
                walkProgress.setProgress((int)progress);

                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        goToShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = todaySteps.edit();
                editor.putString("steps", Integer.toString(currentSteps));
                editor.putString("counterSteps", Integer.toString(counterSteps));
                editor.putString("initSteps", Integer.toString(initSteps));
                editor.commit();

                float progress = (float)currentSteps/Float.parseFloat(goalCount.getText().toString())*100;
                //Toast.makeText(getApplicationContext(),Integer.toString((int)progress), Toast.LENGTH_SHORT).show();
                walkProgress.setProgress((int)progress);

                Intent intent = new Intent(getApplicationContext(), PointShop.class);
                startActivity(intent);
                overridePendingTransition(R.anim.horizon_enter, R.anim.none);
            }
        });

    }

    public void onStart(){
        super.onStart();
        if(stepCountSensor != null){
            //걸음 센서 속도 설정
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void onStop(){
        SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = todaySteps.edit();
        editor.putString("steps", Integer.toString(currentSteps));
        editor.putString("counterSteps", Integer.toString(counterSteps));
        editor.putString("initSteps", Integer.toString(initSteps));
        editor.commit();
        super.onStop();
        if(sensorManager != null){
            //센서 멈춤
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            //현재까지의 걸음수를 0으로 초기화 했을때의 동작 (초기화 버튼을 클릭하거나, 자정이 되었거나 - 이후 구현할것)
            if(counterSteps < 1){
                //counterSteps : 센서가 측정한 휴대폰 부팅 이후부터의 총 걸음수를 기준값으로 사용하기 위한 것
                counterSteps = (int) sensorEvent.values[0];
                //로컬에 값 저장

                SharedPreferences.Editor editor = todaySteps.edit();
                editor.putString("counterSteps", Integer.toString(counterSteps));
                editor.commit();
            }
            //휴대폰 부팅 시 센서가 초기화됨. 따라서 현재 걸음 수 계산에 쓰이는 변수들을 적절히 조정
            String lastCount = todaySteps.getString("counterSteps", "0");
            if(sensorEvent.values[0] <= Integer.parseInt(lastCount)){
                counterSteps = (int) sensorEvent.values[0];
                initSteps = Integer.parseInt(todaySteps.getString("steps", "0"));
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

            Log.i("log: ", "New step detected by STEP_COUNTER sensor. Total step count: " + currentSteps);
            /*
            if(sensorEvent.values[0] == 1.0f){
                currentSteps ++;
                count.setText(String.valueOf(currentSteps));
            }
            */

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void finish() {
        //액티비티 종료 시 로컬에 걸음수 저장
        SharedPreferences todaySteps = getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = todaySteps.edit();
        editor.putString("steps", Integer.toString(currentSteps));
        editor.putString("counterSteps", Integer.toString(counterSteps));
        editor.putString("initSteps", Integer.toString(initSteps));
        editor.commit();
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