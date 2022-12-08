package com.example.walking;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walking.ServerApi.PutNewBadge;

import java.util.HashMap;

public class PointShop extends AppCompatActivity {
    ImageView goBackShop;
    Button buy1, buy2, buy3, buy4;
    Dialog dialog, badgeDialog, animationDialog;
    int userLevel, userPoint;
    int price;
    int levelUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_shop);

        SharedPreferences prefs = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        userLevel = Integer.parseInt(prefs.getString("level", ""));
        userPoint = Integer.parseInt(prefs.getString("point", ""));

        //메인으로 돌아가기
        goBackShop = (ImageView) findViewById(R.id.goBack_shop);

        goBackShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //다이얼로그 생성
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);

        badgeDialog = new Dialog(this);
        badgeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        badgeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        badgeDialog.setContentView(R.layout.badge_dialog);

        animationDialog = new Dialog(this);
        animationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        animationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        animationDialog.setContentView(R.layout.animation_dialog);
        animationDialog.setCancelable(false);

        //경험치 아이템 가격 설정
        if(userLevel > 2){
            price = 50 + (userLevel - 1) * 10;
        }else{
            price = 50;
        }

        //경험치 아이템 1
        buy1 = (Button) findViewById(R.id.price1);
        buy1.setText(Integer.toString(price));
        buy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop(1, 2);
            }
        });
        //경험치 아이템2
        buy2 = (Button) findViewById(R.id.price2);
        buy2.setText(Integer.toString(price*3));
        buy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop(2, 4);
            }
        });
        //경험치 아이템3
        buy3 = (Button) findViewById(R.id.price3);
        buy3.setText(Integer.toString(price*5));
        buy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop(4, 8);
            }
        });
        //뱃지 뽑기
        buy4 =(Button) findViewById(R.id.badgePrice);
        buy4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop();
            }
        });
    }

    public int getPrice(int min){
        switch (min){
            case 1:
                return Integer.parseInt(buy1.getText().toString());
            case 2:
                return Integer.parseInt(buy2.getText().toString());
            case 4:
                return Integer.parseInt(buy3.getText().toString());
            default:
                break;
        }
        return 0;
    }

    public void getRandom(int min){
        int random = (int) (Math.random()*100+1);
        switch (min){
            case 1:
                if(random <= 30)
                    levelUp = 2;
                else
                    levelUp = 1;
                break;
            case 2:
                if(random <= 10)
                    levelUp = 4;
                else if(random <= 30)
                    levelUp = 3;
                else
                    levelUp = 2;
                break;
            case 4:
                if(random == 1)
                    levelUp = 8;
                else if(random <= 7)
                    levelUp = 7;
                else if(random <= 15)
                    levelUp = 6;
                else if(random <= 30)
                    levelUp = 5;
                else
                    levelUp = 4;
                break;
            default:
                break;
        }
    }

    //경험치 구매 전용
    public void showDialogShop(int min, int max){
        TextView dialogText = dialog.findViewById(R.id.dialogText);
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
                SharedPreferences prefs = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                userLevel = Integer.parseInt(prefs.getString("level", ""));
                userPoint = Integer.parseInt(prefs.getString("point", ""));
                if(userPoint - getPrice(min) >= 0){
                    getRandom(min);
                    Toast.makeText(getApplicationContext(),levelUp +" 레벨 up!", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor2 = prefs.edit();
                    editor2.putString("level", Integer.toString(userLevel + levelUp));
                    editor2.putString("point", Integer.toString(userPoint - getPrice(min)));
                    editor2.commit();
                    userLevel = Integer.parseInt(prefs.getString("level", ""));
                    if(userLevel > 2){
                        price = 50 + (userLevel - 1) * 10;
                    }else{
                        price = 50;
                    }
                    buy1.setText(Integer.toString(price));
                    buy2.setText(Integer.toString(price*3));
                    buy3.setText(Integer.toString(price*5));
                }else{
                    Toast.makeText(getApplicationContext(),"포인트가 부족합니다.", Toast.LENGTH_SHORT).show();
                }

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

        ImageView badgeResult = (ImageView) badgeDialog.findViewById(R.id.badgeResult);
        TextView badgeName = (TextView) badgeDialog.findViewById(R.id.badgeName);
        TextView ownedBadge = (TextView) badgeDialog.findViewById(R.id.ownedBadge);
        TextView getBadge = (TextView) badgeDialog.findViewById(R.id.getBadge);
        Button badgeOk = (Button) badgeDialog.findViewById(R.id.badgeOk);

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
                SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                userPoint = Integer.parseInt(pref.getString("point", ""));
                if(userPoint - 300 >= 0) {
                    HashMap<String, String> result = new PutNewBadge().putNewBadge(pref.getString("user_id",""), Integer.parseInt(pref.getString("point", "")));
                    //result.get("index"); 뱃지 인덱스  str
                    //result.get("name"); 뱃지 이름 str
                    //result.get("check"); 중복여부 true:최초 false:중복
                    //result.get("payback"); 중복시 페이백 str 중복 아닐때 쓰려고하면 터짐
                    //BadgeList.badgeImg()[]
                    Log.i("yasuo","result is"+result);
                    SharedPreferences.Editor editor2 = pref.edit();
                    badgeResult.setImageResource(BadgeList.badgeImg()[Integer.parseInt(result.get("index"))]);
                    badgeName.setText(result.get("name"));
                    if((result.get("check").equals("true"))){
                        ownedBadge.setVisibility(View.GONE);
                        editor2.putString("point", Integer.toString(userPoint - 300));
                        editor2.commit();
                    }else{
                        ownedBadge.setVisibility(View.VISIBLE);
                        getBadge.setText(result.get("payback") + "포인트를 획득하였습니다!");
                        editor2.putString("point", Integer.toString(userPoint - 300 + Integer.parseInt(result.get("payback"))));
                        editor2.commit();
                    }
                    dialog.dismiss();
                    animationDialog.show();
                    frameAnimation.start();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animationDialog.dismiss();

                            //이거 대신 가챠 다이얼로그 띄우면 됨
                            //Toast.makeText(getApplicationContext(),"가챠성공!",Toast.LENGTH_SHORT).show();
                            badgeDialog.show();
                            badgeOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    badgeDialog.dismiss();
                                }
                            });
                        }
                    },1600);
                }
                else{
                    Toast.makeText(getApplicationContext(),"포인트가 부족합니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.horizon_exit);
    }
}
