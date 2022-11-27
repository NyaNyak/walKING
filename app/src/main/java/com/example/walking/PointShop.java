package com.example.walking;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import org.w3c.dom.Text;

public class PointShop extends AppCompatActivity {
    ImageView goBackShop;
    Button buy1, buy2, buy3, buy4;
    Dialog dialog, animationDialog;
    int totalExp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_shop);
        goBackShop = (ImageView) findViewById(R.id.goBack_shop);

        goBackShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);

        animationDialog = new Dialog(this);
        animationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        animationDialog.setContentView(R.layout.animation_dialog);
        animationDialog.setCancelable(false);

        buy1 = (Button) findViewById(R.id.price1);
        buy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop(100);
            }
        });
        buy2 = (Button) findViewById(R.id.price2);
        buy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop(300);
            }
        });
        buy3 = (Button) findViewById(R.id.price3);
        buy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop(500);
            }
        });
        buy4 = (Button) findViewById(R.id.badgePrice);
        buy4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop();
            }
        });
    }

    //경험치 구매 전용
    public void showDialogShop(int ratio){
        TextView dialogText = dialog.findViewById(R.id.dialogText);
        dialogText.setText("구매하시겠습니까?");
        dialog.show();

        SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        int exp = Integer.parseInt(pref.getString("total_walk", ""));
        int level = Integer.parseInt(pref.getString("level", "0"));
        // 현재 계산에 쓸 값들 불러오기
        int nowExp = Integer.parseInt(pref.getString("nowExp","0"));
        // 현재 레벨 전까지의 총 경험치
        totalExp = 0;

        // 현재 레벨 전까지의 총 경험치
        for (int i = 1;i <= level;i++) totalExp += i;
        totalExp *= 1000;

        System.out.println("total " + totalExp + " now " + nowExp);


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
                dialog.dismiss();
                SharedPreferences.Editor editor2 = pref.edit();

                int getExp = level * ratio;

                int nowExp = Integer.parseInt(pref.getString("nowExp", "0")) + getExp;

                System.out.println("exp " + nowExp + " level " + level);

                if (nowExp >= level * 1000){
                    editor2.putString("level", Integer.toString(level + 1));
                    nowExp -= level * 1000;
                }

                editor2.putString("nowExp", Integer.toString(nowExp));
                editor2.putString("total_walk", Integer.toString(exp + getExp));
                editor2.commit();
                Toast.makeText(getApplicationContext(),"경험치 " + getExp+" 획득!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //뱃지뽑기 전용
    public void showDialogShop(){
        TextView dialogText = dialog.findViewById(R.id.dialogText);
        //애니메이션
        final ImageView img_loading_frame = (ImageView) animationDialog.findViewById(R.id.load_animation);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        //애니메이션 실행 후 다이얼로그 종료를 위한 핸들러
        Handler handler = new Handler();

        dialogText.setText("구매하시겠습니까?");
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
                dialog.dismiss();
                animationDialog.show();
                frameAnimation.start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animationDialog.dismiss();

                        //이거 대신 가챠 다이얼로그 띄우면 됨
                        Toast.makeText(getApplicationContext(),"가챠성공!",Toast.LENGTH_SHORT).show();
                    }
                },1600);
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.horizon_exit);
    }
}
