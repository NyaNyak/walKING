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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.walking.ServerApi.PutAll;

import org.w3c.dom.Text;

import java.util.HashMap;

public class UserPage extends AppCompatActivity {
    ImageView goBackUser, currentBadge;
    Button changeBadge;
    ConstraintLayout setGoal, logOut;
    Dialog dialog;
    ActivityResultLauncher<Intent> selectedBadgeReturn;
    TextView userName, userId;
    HashMap<String, String> result;
    final private int[] imgId = BadgeList.badgeImg();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        userName = (TextView) findViewById(R.id.userName);
        userId = (TextView) findViewById(R.id.userId);
        currentBadge = (ImageView) findViewById(R.id.current_badge);

        SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        userName.setText(pref.getString("user_name",""));
        userId.setText(pref.getString("user_id",""));
        currentBadge.setImageResource(imgId[Integer.parseInt(pref.getString("set_badge","0"))]);

        goBackUser= (ImageView) findViewById(R.id.goBack_user);

        goBackUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        selectedBadgeReturn = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            int ans = data.getIntExtra("Idx",0);
                            currentBadge.setImageResource(imgId[ans]);
                            editor.putString("set_badge",Integer.toString(ans));
                            editor.apply();
                        }
                    }
                });
        changeBadge = (Button) findViewById(R.id.change_badge);
        changeBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectBadge.class);
                selectedBadgeReturn.launch(intent);
                overridePendingTransition(R.anim.horizon_enter, R.anim.none);
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

                SharedPreferences todaySteps= getSharedPreferences("todaySteps", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = todaySteps.edit();
                editor2.clear();
                editor2.commit();

                SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = pref.edit();

                result = new PutAll().putAll(pref);
                System.out.println(result.get("detail"));

                editor1.clear();
                editor1.apply();

                //앱 종료
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        //로컬에 저장된 목표걸음수 불러와서 메인에 전달해준다
        //SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        //String saveGoal = pref.getString("walk_goal", "");
        //Intent outIntent = new Intent(getApplicationContext(), MainActivity.class);
        //outIntent.putExtra("Goal", Integer.parseInt(saveGoal));
        //setResult(RESULT_OK, outIntent);
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.vertical_exit);
    }
}
