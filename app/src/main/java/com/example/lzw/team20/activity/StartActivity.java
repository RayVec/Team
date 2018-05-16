package com.example.lzw.team20.activity;
import com.example.lzw.team20.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Myh on 2018/4/10.
 */

public class StartActivity extends AppCompatActivity {

    private TextView textView;
    private Button btn_pass;
    private MyCountDownTimer timer;
    private long TIME = 5000;
    private final long INTERVAL = 1000;

    private Handler mHander = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(TIME!=0){
                goHome();}
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_pass= (Button) findViewById(R.id.btn_pass);
        btn_pass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                TIME=0;
            }
        });
        textView = (TextView) findViewById(R.id.tv);
        mHander.sendEmptyMessageDelayed(0,TIME);
        startTimer();
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long time = millisUntilFinished / 1000;

            textView.setText(String.format("%2ds", time));

        }

        @Override
        public void onFinish() {
            textView.setText("0s");
        }
    }

    /**
     * 开始倒计时
     */
    private void startTimer() {
        if (timer == null) {
            timer = new MyCountDownTimer(TIME, INTERVAL);
        }
        timer.start();
    }

    /**
     * 取消倒计时
     */
    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }
    private  void goHome()
    {
        Intent intent = new Intent(StartActivity.this,LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

}


