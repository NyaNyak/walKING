package com.example.walking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class UserPage extends AppCompatActivity {
    ImageView goBackUser;
    ConstraintLayout setGoal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);
        goBackUser= (ImageView) findViewById(R.id.goBack_user);

        goBackUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setGoal = (ConstraintLayout) findViewById(R.id.setting2);
        setGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SetWalkGoal.class);
                startActivity(intent);
                overridePendingTransition(R.anim.horizon_enter, R.anim.none);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.vertical_exit);
    }
}