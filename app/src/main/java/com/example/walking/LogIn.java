package com.example.walking;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walking.ServerApi.PutLogin;

import java.util.HashMap;

public class LogIn extends AppCompatActivity {
    EditText idInput, pwInput;
    Button logInButton, goSignIn;
    String id, password;
    String idAuto, passwordAuto;
    HashMap<String, String> result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        //로그인 기능
        idInput = (EditText) findViewById(R.id.idInput);
        pwInput = (EditText) findViewById(R.id.pwInput);
        logInButton = (Button) findViewById(R.id.logInButton);

        //자동로그인
        SharedPreferences autoLogIn = getSharedPreferences("autoLogIn", Activity.MODE_PRIVATE);
        idAuto = autoLogIn.getString("id", null);
        passwordAuto = autoLogIn.getString("password", null);
        if(idAuto != null && passwordAuto != null){
            System.out.println(idAuto + " " + passwordAuto);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    id = idInput.getText().toString();
                    password = pwInput.getText().toString();
                    if(id.equals("") || password.equals("")){
                        Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해 주세요.",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        result = new PutLogin().putLogin(id, password);
                        if (result.containsKey("detail")){
                            Toast.makeText(getApplicationContext(), result.get("detail"), Toast.LENGTH_SHORT).show();
                        } else {
                            //입력한 아이디, 비밀번호 로컬에 저장

                            SharedPreferences autoLogIn = getSharedPreferences("autoLogIn", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = autoLogIn.edit();
                            editor.putString("id", id);
                            editor.putString("password", password);
                            editor.commit();


                            //user_info 공간에 유저 정보를 저장할 변수 선언
                            SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = pref.edit();
                            //userInfo 사용을 위해 선언
                            UserInfo save = new UserInfo();
                            //인자로 싹다 넘겨줌... 다시보니 pref는 따로 넘겨줄 필요가 없음 editor만 넘겨준다
                            save.userInfo(editor1, result);

                            //TODO 회원가입에도 위와 동일한 부분 작성 필요!

                            //로그인 성공 메시지
                            Toast.makeText(getApplicationContext(), result.get("user_name")+"님 환영합니다.", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getApplicationContext(), result.get("user_name")+"님 환영합니다.", Toast.LENGTH_SHORT).show();

                            //홈화면으로 이동
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    /**
                    else if (id.equals("admin") && password.equals("1234")) {
                        //입력한 아이디, 비밀번호 로컬에 저장
                        SharedPreferences autoLogIn = getSharedPreferences("autoLogIn", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = autoLogIn.edit();
                        editor.putString("id", id);
                        editor.putString("password", password);
                        editor.commit();
                        //홈화면으로 이동
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        result = new Api.HttpAsyncTask().putLogin(id, password);
                        System.out.println(result.size());
                        Toast.makeText(getApplicationContext(), "로그인 성공 : "+result.get("detail"), Toast.LENGTH_SHORT).show();
                    }
                     */
                }
            });
        }

        //회원가입 페이지로 이동
        goSignIn = (Button) findViewById(R.id.goSignIn);
        goSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                overridePendingTransition(R.anim.vertical_enter_reverse, R.anim.none);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }
}
