package com.example.walking;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class UserPage extends AppCompatActivity {
    ImageView goBackUser;
    ConstraintLayout setGoal, logOut;
    Dialog dialog;
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

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        logOut = (ConstraintLayout) findViewById(R.id.setting3);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    //로그아웃 확인창 기능
    public void showDialog(){
        dialog.show();
        //취소 버튼 누르면
        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //확인 버튼 누르면
        Button ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로컬에 저장된 계정 정보 삭제
                SharedPreferences autoLogIn = getSharedPreferences("autoLogIn", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = autoLogIn.edit();
                editor.clear();
                editor.commit();

                //앱 종료
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.vertical_exit);
    }
}