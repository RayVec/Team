package com.example.lzw.team20.activity.rtableDetailSupport;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;
import com.example.lzw.team20.activity.LoginActivity;
import com.example.lzw.team20.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class RTableDetailActivity extends AppCompatActivity {

    Button btn_exit;
    ImageView iv_tablephoto;
    TextView text_tableName;
    TextView text_tableNumber;
    TextView text_tableLeader;
    TextView text_tableLabel1;
    TextView text_tableLabel2;
    TextView text_tableIntroduction;
    TextView text_peopleNumber;
    TextView text_createDate;
    TextView text_deadline;
    Button btn_partner;

    private String tid;
    private Boolean exitSuccess=false;

    public static int[] avatar=new int[]{R.drawable.avatar_default,R.drawable.h001,R.drawable.h002,R.drawable.h003,
            R.drawable.h004,R.drawable.h005,R.drawable.h006};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtabledetail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("圆桌详情");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bindViews();
        setData();
    }

    private void bindViews(){

        iv_tablephoto=(ImageView)findViewById(R.id.iv_rtablephoto);
        btn_exit=(Button)findViewById(R.id.btn_tableExit);
        text_tableName=(TextView)findViewById(R.id.tv_rtable_name);
        text_tableNumber=(TextView)findViewById(R.id.text_tableNumber);
        text_tableLeader=(TextView)findViewById(R.id.text_tableLeader);
        text_tableLabel1=(TextView)findViewById(R.id.text_tableLabel1);
        text_tableLabel2=(TextView)findViewById(R.id.text_tableLabel2);
        text_tableIntroduction=(TextView)findViewById(R.id.text_tableIntroduction);
        text_peopleNumber=(TextView)findViewById(R.id.text_peopleNumber);
        text_createDate=(TextView)findViewById(R.id.text_createDate);
        text_deadline=(TextView)findViewById(R.id.text_deadline);
        btn_partner=(Button)findViewById(R.id.btn_partner);

        btn_partner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent=new Intent(RTableDetailActivity.this,MembersActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("tid",tid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }) ;

        btn_exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO：待后台接口写好后修改

                exitTable(tid);
                if(exitSuccess){
                    Toast .makeText(RTableDetailActivity.this,"退出圆桌成功",Toast.LENGTH_SHORT ).show();
                    Intent intent=new Intent(RTableDetailActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }else
                    Toast .makeText(RTableDetailActivity.this,"退出圆桌失败，请重试",Toast.LENGTH_SHORT ).show();


            }
        }) ;
    }

    private void setData() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tid = getIntent().getExtras().getString("tid");
                    String result = WebService.getOne(tid);


                    JSONObject json = new JSONObject(result);
                    String tphoto=json.getString("photo");
                    if(tphoto.equals("null")||tphoto==null||tphoto.isEmpty())
                        iv_tablephoto.setImageResource(avatar[1]);
                    else
                        iv_tablephoto.setImageResource(avatar[Integer.valueOf(tphoto)]);
                    text_tableName.setText(json.getString("rtablename"));
                    text_tableNumber.setText(json.getString("rtableid"));
                    text_tableLeader.setText(json.getString("username"));
                    text_tableLabel1.setText(json.getString("first_tag"));
                    text_tableLabel2.setText(json.getString("rtablename"));
                    text_tableIntroduction.setText(json.getString("detail"));
                    String people = String.valueOf(json.getInt("nowNumber")) + "/" + String.valueOf(json.getInt("maxNumber"));
                    text_peopleNumber.setText(people);
                    text_createDate.setText(json.getString("createtime").substring(0, 10));
                    text_deadline.setText(json.getString("ddl").substring(0, 10));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void exitTable(final String tableid){
        try{
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    String info=WebService.exitRTable(tableid);
                    if (!TextUtils.isEmpty(info)) {
                        setSuccess();
                    }
                }
            });
            thread.start();
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void setSuccess(){
                exitSuccess=true;
    }
}

