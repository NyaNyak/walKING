package com.example.walking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.walking.ServerApi.PutRanking;

import org.w3c.dom.Text;

import java.util.HashMap;

/*
요소 id 정리

constraintlayout => box + i
이미지 소스 => profile + i (1 ~ 10)
이름 => name + i
레벨 => level + i
ex) prifile3, name3, level3

내 등수 => thisRank
내 이름 => name_this
내 뱃지 => profile_this
내 레벨 => level_this
 */

public class RankingPage extends AppCompatActivity {
    ImageView goBackRank;
    ConstraintLayout[] box = new ConstraintLayout[10];
    ImageView[] profile = new ImageView[10];
    TextView[] name = new TextView[10];
    TextView[] level = new TextView[10];
    int[] badgeImage = BadgeList.badgeImg();

    TextView myRank, myName, myLevel;
    ImageView myBadge;

    //button[i] = findViewById(getResources().getIdentifier("button" + i, "id", "com.ehsehsl.osz"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_page);

        myRank = (TextView) findViewById(R.id.thisRank);
        myName = (TextView) findViewById(R.id.name_this);
        myBadge = (ImageView) findViewById(R.id.profile_this);
        myLevel = (TextView) findViewById(R.id.level_this);

        // for문을 사용한 간편 선언
        for (int i = 0;i < 10;i++){
            box[i] = (ConstraintLayout) findViewById(getResources().getIdentifier("box" + (i+1), "id", "com.example.walking"));
            profile[i] = (ImageView) findViewById(getResources().getIdentifier("profile" + (i+1), "id", "com.example.walking"));
            name[i] = (TextView) findViewById(getResources().getIdentifier("name" + (i+1), "id", "com.example.walking"));
            level[i] = (TextView) findViewById(getResources().getIdentifier("level" + (i+1), "id", "com.example.walking"));
        }

        // 서버에서 랭킹 조회 결과 받아오기
        SharedPreferences pref = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        HashMap<String, String> result = new PutRanking().putRanking(pref.getString("user_id",""));

        System.out.println(result);

        int len = Integer.parseInt(result.get("length"));
        String[] profiles = result.get("badges").split("/");
        String[] names = result.get("names").split("/");
        String[] levels = result.get("levels").split("/");
        String my_rank = result.get("ranks").split("/")[0];

        myRank.setText(my_rank);
        myName.setText(names[0]);
        myBadge.setImageResource(badgeImage[Integer.parseInt(profiles[0])]);
        myLevel.setText(levels[0]);

        for (int i = 0;i < 10;i++){
            if (i < len){
                name[i].setText(names[i + 1]);
                profile[i].setImageResource(badgeImage[Integer.parseInt(profiles[i + 1])]);
                level[i].setText(levels[i + 1]);;
            } else {
                box[i].setVisibility(View.GONE);
            }
        }

        goBackRank = (ImageView) findViewById(R.id.goBack_rank);
        goBackRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.vertical_exit);
    }
}
