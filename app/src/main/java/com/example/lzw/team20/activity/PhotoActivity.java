package com.example.lzw.team20.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.lzw.team20.R;

public class PhotoActivity extends AppCompatActivity {

    private RadioGroup rg;
    private RadioButton c1;
    private RadioButton c2;
    private RadioButton c3;
    private RadioButton c4;
    private RadioButton c5;
    private RadioButton c6;
    private int choose=-1;

    private Button confirm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("选择头像");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PhotoActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        init();
    }

    public void init(){
        rg=findViewById(R.id.radio_group);
        c1=findViewById(R.id.c1);
        c2=findViewById(R.id.c2);
        c3=findViewById(R.id.c3);
        c4=findViewById(R.id.c4);
        c5=findViewById(R.id.c5);
        c6=findViewById(R.id.c6);
        confirm=findViewById(R.id.confirm);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.c1:
                        choose=0;
                        break;
                    case R.id.c2:
                        choose=1;
                        break;
                    case R.id.c3:
                        choose=2;
                        break;
                    case R.id.c4:
                        choose=3;
                        break;
                    case R.id.c5:
                        choose=4;
                        break;
                    case R.id.c6:
                        choose=5;
                        break;
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            Intent intent=new Intent(PhotoActivity.this,RegisterActivity.class);
            intent.putExtra("photo",choose);
            startActivity(intent);
            finish();
            }
        });


    }



}
