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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class UserPage extends AppCompatActivity {
    ImageView goBackUser, currentBadge;
    Button changeBadge;
    ConstraintLayout setGoal, logOut;
    Dialog dialog;
    ActivityResultLauncher<Intent> selectedBadgeReturn;
    final private int imgId[] = {R.drawable.leopard_normal, R.drawable.leopard_epic, R.drawable.leopard_special,
            R.drawable.fox_normal, R.drawable.fox_epic, R.drawable.fox_special, R.drawable.eagle_normal, R.drawable.eagle_epic,
            R.drawable.eagle_special, R.drawable.wolf_normal, R.drawable.wolf_epic, R.drawable.wolf_special, R.drawable.tiger_normal,
            R.drawable.tiger_epic, R.drawable.tiger_special, R.drawable.penguin_normal, R.drawable.penguin_epic, R.drawable.penguin_special,
            R.drawable.cat_normal, R.drawable.cat_epic, R.drawable.cat_special, R.drawable.alpaca_normal, R.drawable.alpaca_epic, R.drawable.alpaca_special, R.drawable.doge_normal, R.drawable.doge_epic,
            R.drawable.doge_special, R.drawable.face_normal, R.drawable.face_epic, R.drawable.face_special, R.drawable.pepe_normal,
            R.drawable.pepe_epic, R.drawable.pepe_special, R.drawable.seoultech1, R.drawable.seoultech2, R.drawable.seoultech3, R.drawable.fox_hidden};


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

        currentBadge = (ImageView) findViewById(R.id.current_badge);
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
        SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        String saveGoal = pref.getString("walk_goal", "");
        Intent outIntent = new Intent(getApplicationContext(), MainActivity.class);
        outIntent.putExtra("Goal", Integer.parseInt(saveGoal));
        setResult(RESULT_OK, outIntent);
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.vertical_exit);
    }
}
