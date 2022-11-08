package com.example.walking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LogIn extends AppCompatActivity {
    Button logInButton, goSignIn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        logInButton = (Button) findViewById(R.id.logInButton);
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
}
