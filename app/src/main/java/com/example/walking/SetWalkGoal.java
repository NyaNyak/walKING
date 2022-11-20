package com.example.walking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walking.ServerApi.PutWalkGoal;

import java.util.HashMap;

public class SetWalkGoal extends AppCompatActivity {
    ImageView goBackGoal;
    String id, goal, number;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_walk_goal);

        goBackGoal = (ImageView) findViewById(R.id.goBack_goal);
        goBackGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //number picker에 표시되는 숫자 설정
        int start = 1000;
        String[] numbers = new String[50];
        for(int i = 0; i<50; i++){
            numbers[i] = start + "";
            start = start + 1000;
        }
        NumberPicker numPicker = findViewById(R.id.numPicker);
        numPicker.setWrapSelectorWheel(false);
        numPicker.setMaxValue(50);
        numPicker.setMinValue(1);
        numPicker.setDisplayedValues(numbers);

        numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                number = numbers[i1 - 1];
            }
        });
    }

    @Override
    public void finish() {
        super.finish();

        SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        id = pref.getString("user_id", "");
        goal = pref.getString("walk_goal", "");

        if (!goal.equals(number)){
            HashMap<String, String> result = new PutWalkGoal().putWalkGoal(id, number);

            if (result.containsKey("detail")){
                Toast.makeText(getApplicationContext(), result.get("detail"), Toast.LENGTH_SHORT).show();
            } else {
                UserInfo save = new UserInfo();
                save.userInfo(editor, result);
            }
        }

        overridePendingTransition(R.anim.none, R.anim.horizon_exit);
    }
}
