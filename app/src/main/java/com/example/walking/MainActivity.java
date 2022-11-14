package com.example.walking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ImageView profilePage, ranking, userProfile, gift;
    ConstraintLayout goToShop;
    String idAuto, passwordAuto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profilePage = (ImageView) findViewById(R.id.profilePage);
        profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.vertical_enter,R.anim.none);
            }
        });

        ranking = (ImageView) findViewById(R.id.ranking);
        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RankingPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.vertical_enter, R.anim.none);
            }
        });

        userProfile = (ImageView) findViewById(R.id.user_profile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        goToShop = (ConstraintLayout) findViewById(R.id.constraintLayout3);
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
    public void finish() {
        super.finish();
    }
}