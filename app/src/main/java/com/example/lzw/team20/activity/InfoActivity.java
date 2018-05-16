package com.example.lzw.team20.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.support.annotation.IdRes;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Myh on 2018/4/21.
 */

public class InfoActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private RadioGroup radio_group;
    private RadioButton rbtn_boy;
    private RadioButton rbtn_girl;

    private CheckBox cbox_1;
    private CheckBox cbox_2;
    private CheckBox cbox_3;
    private List<String> hobbies=new ArrayList<>();
    StringBuilder str;

    private EditText text_name;
    //private Spinner spinner_school;

    private Button btn_save;

    private String info;
    private JSONObject data;
    private ProgressDialog dialog;
    private String name;
    private String school="武汉大学";
    private boolean isAutoSelect=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("个人信息");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bindViews();
    }
    private void bindViews(){

        text_name=(EditText)findViewById(R.id.text_name);
        //spinner_school=(Spinner) findViewById(R.id.spinner_school);

        //spinner_school.setSelection(0);
        /*spinner_school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (!isAutoSelect){
                    school=spinner_school.getSelectedItem().toString();
                }
                isAutoSelect=false;

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback

            }
        });*/

        radio_group=(RadioGroup)findViewById(R.id.radio_group);
        rbtn_boy=(RadioButton)findViewById(R.id.rbtn_boy);
        rbtn_girl=(RadioButton)findViewById(R.id.rbtn_girl);
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_boy:
                        Toast.makeText(getApplicationContext(),"choose the boy!",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rbtn_girl:
                        Toast.makeText(getApplicationContext(),"choose the girl!",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        cbox_1=(CheckBox)findViewById(R.id.cbox_1);cbox_1.setOnCheckedChangeListener(this);
        cbox_2=(CheckBox)findViewById(R.id.cbox_2);cbox_2.setOnCheckedChangeListener(this);
        cbox_3=(CheckBox)findViewById(R.id.cbox_3);cbox_3.setOnCheckedChangeListener(this);

        getData();

        btn_save=(Button)findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //处理用户名
                name=(text_name.getText().toString().equals("")?text_name.getHint().toString():text_name.getText().toString());
                //处理爱好
                str = new StringBuilder();
                for (int i = 0; i < hobbies.size(); i++) {
                    str.append(hobbies.get(i)).append(",");//把选择的爱好添加到string尾部
                }
                Toast.makeText(InfoActivity.this, str.toString(), Toast.LENGTH_SHORT).show();
                dialog = new ProgressDialog(InfoActivity.this);// 提示框
                dialog.setTitle("提示");
                dialog.setMessage("正在修改，请稍后...");
                dialog.setCancelable(false);
                dialog.show();
                new Thread(new InfoActivity.ModifyThread()).start();// 创建子线程，分别进行Get和Post传输

            }
        });

    }

    public void getData(){
        Intent intent=getIntent();
        try {
            data=new JSONObject(intent.getStringExtra("data"));
            text_name.setHint(data.getString("username"));
            school=data.getString("school").equals("null")?"学校":data.getString("school");
            String[] hobbyStr=data.getString("hobby").split(",");
            for(int i=0;i<hobbyStr.length;i++){
                if (hobbyStr[i].equals("null"))
                    break;
                hobbies.add(hobbyStr[i]);
                switch (hobbyStr[i]){
                    case "学习":
                        cbox_1.setChecked(true);
                        break;
                    case "娱乐":
                        cbox_2.setChecked(true);
                        break;
                    case "运动":
                        cbox_3.setChecked(true);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ModifyThread implements Runnable {
        @Override
        public void run() {

            info= WebService.executeModify(name,school,str.toString());

            if(!TextUtils.isEmpty(info)){
                try {
                    JSONObject json=new JSONObject(info);
                    String result=json.getString("result");
                    if (result.equals("false")){
                        dialog.dismiss();
                        Looper.prepare();
                        Toast.makeText(InfoActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else {
                        Intent intent=new Intent(InfoActivity.this,LoginActivity.class);//修改后重新登陆
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                dialog.dismiss();
                Looper.prepare();
                Toast.makeText(InfoActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked&&!hobbies.contains(buttonView.getText().toString().trim())){
            //添加到爱好数组
            hobbies.add(buttonView.getText().toString().trim());
        }else if (!isChecked&&hobbies.contains(buttonView.getText().toString().trim())){
            //从数组中移除
            hobbies.remove(buttonView.getText().toString().trim());
        }


    }

}

