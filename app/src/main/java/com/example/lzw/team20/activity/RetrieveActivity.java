package com.example.lzw.team20.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Myh on 2018/4/21.
 */

public class RetrieveActivity extends AppCompatActivity {

    //ImageView image;//头像
    private EditText tel;
    private EditText password;
    private EditText password2;
    private EditText vericode;
    private Button btn_confirm;
    private Button btn_get_verifi_code;
    private ProgressDialog dialog;
    private String info;
    public final static int Retrieve=1;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("找回密码");
        toolbar.setTitleTextColor(getResources().getColor(R.color.bg_black));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RetrieveActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bindView();
    }
    public void bindView(){
        tel=(EditText)findViewById(R.id.tel);
        password=(EditText)findViewById(R.id.password);
        password2=(EditText)findViewById(R.id.password2);
        vericode=(EditText)findViewById(R.id.vericode);
        btn_get_verifi_code=(Button)findViewById(R.id.btn_get_verifi_code);
        btn_confirm=(Button)findViewById(R.id.btn_confirm);
        btn_get_verifi_code.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String telStr = tel.getText().toString();
                if ("".equals(telStr) || telStr.length() != 11) {
                    Toast.makeText(RetrieveActivity.this, "电话号码应为11位数字", Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(new RetrieveActivity.GetCodeThread()).start();
                }
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String codeStr=vericode.getText().toString();
                String passwordStr=password.getText().toString();
                String passwordStr2=password2.getText().toString();
                if (!passwordStr2.equals(passwordStr)){
                    Toast.makeText(RetrieveActivity.this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
                }else if(codeStr.equals("")||codeStr.length()!=6){
                    Toast.makeText(RetrieveActivity.this, "验证码应为6位数字！", Toast.LENGTH_SHORT).show();
                }else {//存数据
                    dialog = new ProgressDialog(RetrieveActivity.this);// 提示框
                    dialog.setTitle("提示");
                    dialog.setMessage("正在重置，请稍后...");
                    dialog.setCancelable(false);
                    dialog.show();
                    // 创建子线程，分别进行Get和Post传输
                    new Thread(new RetrieveActivity.RetrieveThread()).start();
                }
            }
        });
    }

    public class GetCodeThread implements Runnable{
        @Override
        public void run(){
            info= WebService.executeSendShortMessage(tel.getText().toString());
            if (!TextUtils.isEmpty(info)){
                try {
                    JSONObject json=new JSONObject(info);
                    String result=json.getString("result");
                    if (result.equals("false")){
                        Looper.prepare();
                        Toast.makeText(RetrieveActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(RetrieveActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public class RetrieveThread implements Runnable {
        @Override
        public void run() {
            info= WebService.executeRetrieve(tel.getText().toString(),password.getText().toString(),vericode.getText().toString());
            if(!TextUtils.isEmpty(info)){
                try {
                    JSONObject json=new JSONObject(info);
                    String result=json.getString("result");
                    if (result.equals("false")){
                        dialog.dismiss();
                        Looper.prepare();
                        Toast.makeText(RetrieveActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else {
                        Intent intent=new Intent(RetrieveActivity.this,LoginActivity.class);
                        finish();
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                dialog.dismiss();
                Looper.prepare();
                Toast.makeText(RetrieveActivity.this, "找回密码失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }
}
