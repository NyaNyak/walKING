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

import java.util.Map;

public class LogIn extends AppCompatActivity {
    EditText idInput, pwInput;
    Button logInButton, goSignIn;
    String id, password;
    String idAuto, passwordAuto;
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
                        new Api.HttpAsyncTask().putLogin(id, password);
                        Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //회원가입 페이지로 이동
        goSignIn = (Button) findViewById(R.id.goSignIn);
        goSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
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
