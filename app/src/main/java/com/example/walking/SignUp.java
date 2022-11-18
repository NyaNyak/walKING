package com.example.walking;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walking.ServerApi.PostSignIn;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    EditText idSet, pwSet, pwCheck, nameSet;
    RadioGroup genderSet;
    Button signInButton;
    String id, pw, pwChk, name;
    HashMap<String, String> result;
    String gender = "";

    //정규표현식
    String idRegex = "^[a-z]{1}[a-z0-9_]{4,14}$";
    String pwRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";
    String nameRegex = "^[가-힣a-zA-Z0-9._ -]{2,}\\$";
    Pattern idPattern = Pattern.compile(idRegex);
    Pattern pwPattern = Pattern.compile(pwRegex);
    Pattern namePattern = Pattern.compile(nameRegex);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        idSet = (EditText) findViewById(R.id.idSet);
        pwSet = (EditText) findViewById(R.id.pwSet);
        pwCheck = (EditText) findViewById(R.id.pwCheck);
        nameSet = (EditText) findViewById(R.id.nameSet);
        InputFilter[] byteFilter = new InputFilter[]{new ByteLengthFilter(14, "KSC5601")};
        nameSet.setFilters(byteFilter);
        genderSet = (RadioGroup) findViewById(R.id.genderSet);
        signInButton = (Button) findViewById(R.id.signInButton);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = idSet.getText().toString();
                pw = pwSet.getText().toString();
                pwChk = pwCheck.getText().toString();
                name = nameSet.getText().toString();

                if (id.equals("")){
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(!idPattern.matcher(id).matches()){
                    Toast.makeText(getApplicationContext(), "아이디 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } else if (pw.equals("")){
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(!pwPattern.matcher(pw).matches()){
                    Toast.makeText(getApplicationContext(), "비밀번호 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } else if (pwChk.equals("")){
                    Toast.makeText(getApplicationContext(), "비밀번호를 한번 더 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!pw.equals(pwChk)){
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if (name.equals("")){
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(!namePattern.matcher(name).matches()){
                    Toast.makeText(getApplicationContext(), "사용할 수 없는 닉네임입니다.", Toast.LENGTH_SHORT).show();
                } else if (gender.equals("")){
                    Toast.makeText(getApplicationContext(), "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else{
                    result = new PostSignIn().postSignIn(id, pw, name, gender);
                    if (result.containsKey("detail")){
                        Toast.makeText(getApplicationContext(), result.get("detail"), Toast.LENGTH_SHORT).show();
                    }else {
                        //입력한 아이디, 비밀번호 로컬에 저장
                        SharedPreferences autoLogIn = getSharedPreferences("autoLogIn", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = autoLogIn.edit();
                        editor.putString("id", id);
                        editor.putString("password", pw);
                        editor.commit();

                        //로그인 성공 메시지
                        Toast.makeText(getApplicationContext(), result.get("user_name")+"님 환영합니다.", Toast.LENGTH_SHORT).show();

                        //홈화면으로 이동
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });


        genderSet.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.maleSet:
                        gender = "true";
                        break;
                    case R.id.femaleSet:
                        gender = "false";
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.vertical_exit_reverse);
    }
}
