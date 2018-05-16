package com.example.lzw.team20.activity;
import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Myh on 2018/3/27.
 */

public class RegisterActivity extends AppCompatActivity {

    ImageView image;//头像
    private EditText name;
    private EditText tel;
    private EditText password;
    private EditText password2;
    private EditText vericode;
    private Button btn_confirm;
    private Button btn_returnLogin;
    private Button btn_get_verifi_code;
    private ProgressDialog dialog;
    private String info;
    public final static int Register=1;
    public static int[] avatar=new int[]{R.drawable.avatar_default,R.drawable.h001,R.drawable.h002,R.drawable.h003,
            R.drawable.h004,R.drawable.h005,R.drawable.h006};
    private int choose=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });*/

        bindView();
    }
    public void bindView(){
        image=(ImageView)findViewById(R.id.image);
        setPhoto();
        name=(EditText)findViewById(R.id.name);
        tel=(EditText)findViewById(R.id.tel);
        password=(EditText)findViewById(R.id.password);
        password2=(EditText)findViewById(R.id.password2);
        vericode=(EditText)findViewById(R.id.vericode);
        btn_get_verifi_code=(Button)findViewById(R.id.btn_get_verifi_code);
        btn_confirm=(Button)findViewById(R.id.btn_confirm);
        btn_returnLogin=(Button)findViewById(R.id.btn_returnlogin);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(RegisterActivity.this,PhotoActivity.class);
                startActivity(intent);
            }
        });

        btn_get_verifi_code.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String telStr = tel.getText().toString();
                if ("".equals(telStr) || telStr.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "电话号码应为11位数字", Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(new GetCodeThread()).start();
                }
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String codeStr=vericode.getText().toString();
                String nameStr=name.getText().toString();
                String passwordStr=password.getText().toString();
                String passwordStr2=password2.getText().toString();
                if(choose==-1){
                    Toast.makeText(RegisterActivity.this, "请选择头像！", Toast.LENGTH_SHORT).show();
                } else if (nameStr.equals("")||passwordStr.equals("")||passwordStr2.equals("")){
                    Toast.makeText(RegisterActivity.this, "用户名和密码为必填字段！", Toast.LENGTH_SHORT).show();
                }else if (!passwordStr2.equals(passwordStr)){
                    Toast.makeText(RegisterActivity.this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
                }else if(codeStr.equals("")||codeStr.length()!=6){
                    Toast.makeText(RegisterActivity.this, "验证码应为6位数字！", Toast.LENGTH_SHORT).show();
                }else {//存数据
                    dialog = new ProgressDialog(RegisterActivity.this);// 提示框
                    dialog.setTitle("提示");
                    dialog.setMessage("正在注册，请稍后...");
                    dialog.setCancelable(false);
                    dialog.show();
                    // 创建子线程，分别进行Get和Post传输
                    new Thread(new RegisterThread()).start();
                }
            }
        });

        btn_returnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });
    }

    public void setPhoto(){
        Bundle bundle=this.getIntent().getExtras();
        if (bundle != null) {
            choose = bundle.getInt("photo");
        }
        if (choose != -1) {
            switch (choose){
                case 0:
                    image.setImageResource(avatar[0]);
                    break;
                case 1:
                    image.setImageResource(avatar[1]);
                    break;
                case 2:
                    image.setImageResource(avatar[2]);
                    break;
                case 3:
                    image.setImageResource(avatar[3]);
                    break;
                case 4:
                    image.setImageResource(avatar[4]);
                    break;
                case 5:
                    image.setImageResource(avatar[5]);
                    break;
            }
        }

    }

    public class GetCodeThread implements Runnable{
        @Override
        public void run(){
            info=WebService.executeSendShortMessage(tel.getText().toString());
            if (!TextUtils.isEmpty(info)){
                try {
                    JSONObject json=new JSONObject(info);
                    String result=json.getString("result");
                    if (result.equals("false")){
                        Looper.prepare();
                        Toast.makeText(RegisterActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(RegisterActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public class RegisterThread implements Runnable {
        @Override
        public void run() {
            info= WebService.executeRegister(name.getText().toString(), password.getText().toString(),tel.getText().toString(),vericode.getText().toString(),choose);

            if(!TextUtils.isEmpty(info)){
                try {
                    JSONObject json=new JSONObject(info);
                    String result=json.getString("result");
                    if (result.equals("false")){
                        dialog.dismiss();
                        Looper.prepare();
                        Toast.makeText(RegisterActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else {
                        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                        intent.putExtra("name",name.getText().toString());//含参数的传递
                        intent.putExtra("password",password.getText().toString());
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
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }



}



