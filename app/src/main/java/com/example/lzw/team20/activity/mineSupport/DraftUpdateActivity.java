package com.example.lzw.team20.activity.mineSupport;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;
import com.example.lzw.team20.activity.MatchActivity;
import com.example.lzw.team20.activity.TableBuildingActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class DraftUpdateActivity extends AppCompatActivity {
    private JSONObject data;

    private String userid;
    private String needid;         //保存用户id
    private String school;         //保存用户学校
    private String info;

    private RadioGroup radio_group_1;
    private RadioButton rbtn_1;
    private RadioButton rbtn_2;
    private RadioButton rbtn_3;
    private String first_tag;//保存一级标签选项
    private int selectType;

    private EditText text_secLabel;
    private EditText text_introduce;

    private RadioGroup radio_group_2;
    private RadioButton rbtn_one;
    private RadioButton rbtn_more;
    private int selectMemNum;//保存单人/多人选项

    private Button btn_invite;
    private Button btn_match;
    private Button btn_draft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_drafts_update);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("我的草稿");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bindViews();
        getData();
    }

    private void bindViews() {
        radio_group_1 = (RadioGroup) findViewById(R.id.radio_group_1);
        rbtn_1 = (RadioButton) findViewById(R.id.rbtn_1);
        rbtn_2 = (RadioButton) findViewById(R.id.rbtn_2);
        rbtn_3 = (RadioButton) findViewById(R.id.rbtn_3);

        text_secLabel = (EditText) findViewById(R.id.text_secLabel);
        text_introduce = (EditText) findViewById(R.id.text_tableIntroduce);

        radio_group_2 = (RadioGroup) findViewById(R.id.radio_group_2);
        rbtn_one = (RadioButton) findViewById(R.id.rbtn_one);
        rbtn_more = (RadioButton) findViewById(R.id.rbtn_more);

        btn_invite = (Button) findViewById(R.id.btn_invite);
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Click invite more", Toast.LENGTH_SHORT).show();

            }
        });

        btn_match = (Button) findViewById(R.id.btn_match);
        //测试:暂时使点击智能匹配按钮后跳转至自建圆桌界面
        btn_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchThread(v);
            }
        });

        btn_draft = (Button) findViewById(R.id.btn_draft);
        btn_draft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draftThread(v);
            }
        });
    }

    //获取MatchActivity传入数值
    public void getData() {
        try {
            data = new JSONObject(getIntent().getStringExtra("data"));
            userid = data.getString("userid");
            school="武汉大学";
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Draft draft = (Draft) getIntent().getSerializableExtra("draft");

        first_tag = draft.firstTag;

        switch (first_tag) {
            case "学习":
                rbtn_1.setChecked(true);
                selectType = 1;
                break;
            case "娱乐":
                rbtn_2.setChecked(true);
                selectType = 2;
                break;
            case "运动":
                rbtn_3.setChecked(true);
                selectType = 3;
                break;
        }

        text_secLabel.setText(draft.secondTag);
        text_introduce.setText(draft.detail);

        //TODO:暂时全部设为单人，待好友模块完成后修改
        selectMemNum = 1;
        switch (selectMemNum) {
            case 1:
                rbtn_one.setChecked(true);
                break;
            case 2:
                rbtn_more.setChecked(true);
                break;
        }

        needid = draft.needid;

    }

    //智能匹配 跳转
    public void matchThread(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //调用创建需求函数
                info = WebService.createNeed(userid, first_tag, text_secLabel.getText().toString(), text_introduce.getText().toString(), String.valueOf(selectMemNum), school);
                if (!TextUtils.isEmpty(info)) {
                    try {
                        String needid = info;

                        //暂时为跳转到自建圆桌界面
                        Intent intent = new Intent(DraftUpdateActivity.this, TableBuildingActivity.class);

                        //创建Bundle打包数据
                        Bundle bundle = new Bundle();
                        bundle.putString("userid", userid);
                        bundle.putString("needid", needid);
                        bundle.putInt("selectType", selectType);
                        bundle.putString("text_secLabel", text_secLabel.getText().toString());
                        bundle.putString("text_introduce", text_introduce.getText().toString());
                        bundle.putInt("selectMemNum", selectMemNum);

                        intent.putExtras(bundle);

                        startActivity(intent);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();





    }

    //保存修改
    public void draftThread(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //调用存为草稿函数
                info = WebService.changeDraft(needid, first_tag, text_secLabel.getText().toString(), text_introduce.getText().toString(), String.valueOf(selectMemNum), school);
                if (!TextUtils.isEmpty(info)) {
                    Looper.prepare();
                    Toast.makeText(DraftUpdateActivity.this, "已保存修改", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    //返回到上一页
                    finish();

                }
            }
        }).start();
    }
}
