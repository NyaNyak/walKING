package com.example.walking;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SetWalkGoal extends AppCompatActivity {
    ImageView goBackGoal;
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
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.horizon_exit);
    }
}
