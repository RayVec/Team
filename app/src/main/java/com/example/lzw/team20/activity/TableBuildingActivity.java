package com.example.lzw.team20.activity;

import android.app.DatePickerDialog;
import java.util.Calendar;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;
import com.example.lzw.team20.fragment.TableFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class TableBuildingActivity extends AppCompatActivity {

    public String info;
    private JSONObject data;

    private String needid;     //需求id
    private String uphoto;     //用户头像

    private RadioGroup radio_group_1;
    private RadioButton rbtn_1;
    private RadioButton rbtn_2;
    private RadioButton rbtn_3;
    //保存一级标签选项
    private int selectType;

    private EditText text_secLabel;
    private EditText text_introduce;
    private EditText text_memberNumMax;

    private RadioGroup radio_group_2;
    private RadioButton rbtn_one;
    private RadioButton rbtn_more;
    //保存单人/多人选项
    private int selectMemNum;


    private Button btn_invite;
    private Button btn_tableConfirm;

    private TextView text_date;

    private final Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case R.string.msg_tablecreated:
                    Toast.makeText(TableBuildingActivity.this, getResources().getString(R.string.msg_tablecreated), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_building);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("自建圆桌");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bindViews();
        getData();


        //日历
        ImageButton btn_dateChoose=(ImageButton)findViewById(R.id.button_chooseDate);
        btn_dateChoose.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar c=Calendar.getInstance();
                //创建日期选择框实例
                new DatePickerDialog(TableBuildingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //点击事件
                        //Toast.makeText(getApplicationContext(),"choose date "+year+"-"+month+"-"+dayOfMonth,Toast.LENGTH_SHORT).show();
                        String stMonth=month<9?("0"+String.valueOf(month+1)):String.valueOf(month+1);
                        String stDay=dayOfMonth<10?("0"+String.valueOf(dayOfMonth)):String.valueOf(dayOfMonth);
                        text_date.setText(year+"-"+stMonth+"-"+stDay);
                    }
                },
                        //设置初始日期
                        c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void bindViews(){
        radio_group_1=(RadioGroup)findViewById(R.id.radio_group_1);
        rbtn_1=(RadioButton)findViewById(R.id.rbtn_1);
        rbtn_2=(RadioButton)findViewById(R.id.rbtn_2);
        rbtn_3=(RadioButton)findViewById(R.id.rbtn_3);

        text_secLabel=(EditText)findViewById(R.id.text_secLabel);
        text_introduce=(EditText)findViewById(R.id.text_tableIntroduce);
        text_date=(TextView)findViewById(R.id.text_date);
        text_memberNumMax=(EditText)findViewById(R.id.text_memberNumMax);

        radio_group_2=(RadioGroup)findViewById(R.id.radio_group_2);
        rbtn_one=(RadioButton)findViewById(R.id.rbtn_one);
        rbtn_more=(RadioButton)findViewById(R.id.rbtn_more);


        btn_invite=(Button)findViewById(R.id.btn_invite);
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"邀请好友模块暂未上线，敬请期待",Toast.LENGTH_SHORT).show();

            }
        });



        btn_tableConfirm=(Button)findViewById(R.id.btn_tableConfirm);
        btn_tableConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTable();
            }
        });
    }

    //获取MatchActivity传入数值
    public void getData(){
        try{
            data=new JSONObject(this.getIntent().getStringExtra("data"));
        }catch (JSONException e){
            e.printStackTrace();
        }

        Bundle bundle=this.getIntent().getExtras();

        uphoto=bundle.getString("photo");
        selectType=bundle.getInt("selectType");
        switch (selectType){
            case 1:
                rbtn_1.setChecked(true);
                break;
            case 2:
                rbtn_2.setChecked(true);
                break;
            case 3:
                rbtn_3.setChecked(true);
                break;
        }

        text_secLabel.setText(bundle.getString("text_secLabel"));
        text_introduce.setText(bundle.getString("text_introduce"));

        selectMemNum=bundle.getInt("selectMemNum");
        switch (selectMemNum){
            case 1:
                rbtn_one.setChecked(true);
                break;
            case 2:
                rbtn_more.setChecked(true);
                break;
        }
        needid=bundle.getString("needid");


    }

    //创建圆桌
    public void createTable(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                info= WebService.createRTable(needid,text_secLabel.getText().toString(),text_date.getText().toString()+" 00:00:00",text_memberNumMax.getText().toString(),uphoto);
                if (!TextUtils.isEmpty(info)){
                    Message msg = msgHandler.obtainMessage();
                    msg.arg1 = R.string.msg_tablecreated;
                    msgHandler.sendMessage(msg);
                }

            }
        });
        try{
            thread.start();
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }


        //返回圆桌界面
        Intent intent=new Intent(TableBuildingActivity.this, MainActivity.class);
        intent.putExtra("data",data.toString());
        startActivity(intent);

    }
}
