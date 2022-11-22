package com.example.walking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SelectBadge extends AppCompatActivity {
    Button selectCancel, selectOk;
    Animation scaleUp, scaleDown;
    ImageButton btnArray[] = new ImageButton[37];
    int selectedIdx;
    final private int[] btnId = BadgeList.badgeId();
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_badge);

        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        for (int i = 0; i < btnId.length; i++){
            final int index;
            index = i;
            btnArray[index] = (ImageButton) findViewById(btnId[index]);
            btnArray[index].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        btnArray[index].startAnimation(scaleDown);
                        btnArray[index].setSelected(true);
                        selectedIdx = index;
                        for (int j = 0; j < btnId.length; j++) {
                            final int index2;
                            index2 = j;
                            if (index != index2) {
                                btnArray[index2].setSelected(false);
                                btnArray[index2].clearAnimation();
                            }
                        }
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        btnArray[index].startAnimation(scaleUp);
                        selectedIdx = index;
                        for (int j = 0; j < btnId.length; j++) {
                            final int index2;
                            index2 = j;
                            if (index != index2) {
                                btnArray[index2].clearAnimation();
                            }
                        }
                        btnArray[index].performClick();
                    }

                    return true;
                }
            });
        }

        selectCancel = (Button) findViewById(R.id.selectCancel);
        selectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!(view.isSelected()));
                finish();
            }
        });

        selectOk = (Button) findViewById(R.id.selectOk);
        selectOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!(view.isSelected()));
                Intent outIntent = new Intent(getApplicationContext(), UserPage.class);
                outIntent.putExtra("Idx", selectedIdx);
                setResult(RESULT_OK, outIntent);
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.horizon_exit);
    }
}
