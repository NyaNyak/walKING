package com.example.walking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SelectBadge extends AppCompatActivity {
    Button selectCancel, selectOk;
    ImageButton btnArray[] = new ImageButton[37];
    int selectedIdx;
    final private int btnId[] = {R.id.leopardNormal, R.id.leopardEpic, R.id.leopardSpecial,
    R.id.foxNormal, R.id.foxEpic, R.id.foxSpecial, R.id.eagleNormal, R.id.eagleEpic,
    R.id.eagleSpecial, R.id.wolfNormal, R.id.wolfEpic, R.id.wolfSpecial, R.id.tigerNormal,
    R.id.tigerEpic, R.id.tigerSpecial, R.id.penguinNormal, R.id.penguinEpic, R.id.penguinSpecial,
    R.id.catNormal, R.id.catEpic, R.id.catSpecial, R.id.alpacaNormal, R.id.alpacaEpic, R.id.alpacaSpecial, R.id.dogeNormal, R.id.dogeEpic,
    R.id.dogeSpecial, R.id.faceNormal, R.id.faceEpic, R.id.faceSpecial, R.id.pepeNormal,
    R.id.pepeEpic, R.id.pepeSpecial, R.id.techNormal, R.id.techEpic, R.id.techSpecial, R.id.foxHidden};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_badge);

        for (int i = 0; i < btnId.length; i++){
            final int index;
            index = i;
            btnArray[index] = (ImageButton) findViewById(btnId[index]);
            btnArray[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setSelected(!view.isSelected());
                    selectedIdx = index;
                    for(int j = 0; j < btnId.length; j++){
                        final int index2;
                        index2 = j;
                        if(index!=index2){
                            btnArray[index2].setSelected(false);
                        }
                    }

                }
            });
        }

        selectCancel = (Button) findViewById(R.id.selectCancel);
        selectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        selectOk = (Button) findViewById(R.id.selectOk);
        selectOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
