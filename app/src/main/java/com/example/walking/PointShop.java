package com.example.walking;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class PointShop extends AppCompatActivity {
    ImageView goBackShop;
    Button buy1, buy2, buy3, buy4;
    Dialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_shop);
        goBackShop = (ImageView) findViewById(R.id.goBack_shop);

        goBackShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);

        buy1 = (Button) findViewById(R.id.price1);
        buy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop(10);
            }
        });
        buy2 = (Button) findViewById(R.id.price2);
        buy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop(30);
            }
        });
        buy3 = (Button) findViewById(R.id.price3);
        buy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop(50);
            }
        });
        buy4 = (Button) findViewById(R.id.badgePrice);
        buy4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop();
            }
        });
    }

    //경험치 구매 전용
    public void showDialogShop(float percent){
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
                int getExp = Math.round(percent/100*1000);
                Toast.makeText(getApplicationContext(),"경험치 " + getExp+" 획득!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //뱃지뽑기 전용
    public void showDialogShop(){
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
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none, R.anim.horizon_exit);
    }
}
