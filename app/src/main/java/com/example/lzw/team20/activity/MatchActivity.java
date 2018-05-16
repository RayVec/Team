package com.example.lzw.team20.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.lzw.team20.R.id.vpager;

/**
 * Created by Myh on 2018/4/19.
 */

public class MatchActivity extends AppCompatActivity {

    private JSONObject data;

    private String userID;         //保存用户id
    private String school;         //保存用户学校
    private String uphoto;
    private String info;


    private RadioGroup radio_group_1;
    private RadioButton rbtn_1;
    private RadioButton rbtn_2;
    private RadioButton rbtn_3;
    private int selectType;//保存一级标签选项

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
        setContentView(R.layout.activity_match);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("智能匹配需求");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        bindViews();

    }

    private void bindViews() {
        try {
            Intent intent = getIntent();
            data = new JSONObject(intent.getStringExtra("data"));
            userID = data.getString("userid");
            school = "武汉大学";
            uphoto = data.getString("photo");

            radio_group_1 = (RadioGroup) findViewById(R.id.radio_group_1);
            rbtn_1 = (RadioButton) findViewById(R.id.rbtn_1);
            rbtn_2 = (RadioButton) findViewById(R.id.rbtn_2);
            rbtn_3 = (RadioButton) findViewById(R.id.rbtn_3);
            radio_group_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    switch (checkedId) {
                        case R.id.rbtn_1:
                            selectType = 1;
                            break;
                        case R.id.rbtn_2:
                            selectType = 2;
                            break;
                        case R.id.rbtn_3:
                            selectType = 3;
                            break;
                    }
                }
            });

            text_secLabel = (EditText) findViewById(R.id.text_secLabel);
            text_introduce = (EditText) findViewById(R.id.text_tableIntroduce);

            radio_group_2 = (RadioGroup) findViewById(R.id.radio_group_2);
            rbtn_one = (RadioButton) findViewById(R.id.rbtn_one);
            rbtn_more = (RadioButton) findViewById(R.id.rbtn_more);
            radio_group_2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    switch (checkedId) {
                        case R.id.rbtn_one:
                            selectMemNum = 1;
                            break;
                        case R.id.rbtn_more:
                            selectMemNum = 2;
                            break;

                    }
                }
            });

            btn_invite = (Button) findViewById(R.id.btn_invite);

            btn_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "邀请好友模块暂未上线，敬请期待", Snackbar.LENGTH_SHORT).show();
                }
            });
            btn_match = (Button) findViewById(R.id.btn_match);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //智能匹配 跳转
    public void matchThread(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String first_tag;
                switch (selectType) {
                    case 1:
                        first_tag = "学习";
                        break;
                    case 2:
                        first_tag = "娱乐";
                        break;
                    case 3:
                        first_tag = "运动";
                        break;
                    default:
                        first_tag = null;
                }
                //调用创建圆桌函数

                info = WebService.createNeed(userID, first_tag, text_secLabel.getText().toString(), text_introduce.getText().toString(), String.valueOf(selectMemNum), school);
                if (!TextUtils.isEmpty(info)) {
                    try {
                        String needid = info;



                        Intent intent = new Intent(MatchActivity.this, MatchResultActivity.class);

                        //创建Bundle打包数据
                        Bundle bundle = new Bundle();
                        bundle.putString("userid", userID);
                        bundle.putString("needid", needid);
                        bundle.putString("photo",uphoto);
                        bundle.putInt("selectType", selectType);
                        bundle.putString("text_secLabel", text_secLabel.getText().toString());
                        bundle.putString("text_introduce", text_introduce.getText().toString());
                        bundle.putInt("selectMemNum", selectMemNum);

                        intent.putExtras(bundle);
                        intent.putExtra("data",data.toString());

                        startActivity(intent);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }

    //存为草稿 跳转
    public void draftThread(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String first_tag;
                switch (selectType) {
                    case 1:
                        first_tag = "学习";
                        break;
                    case 2:
                        first_tag = "娱乐";
                        break;
                    case 3:
                        first_tag = "运动";
                        break;
                    default:
                        first_tag = null;
                }

                //调用存为草稿函数
                info = WebService.createDraft(userID, first_tag, text_secLabel.getText().toString(), text_introduce.getText().toString(), String.valueOf(selectMemNum), school);
                if (!TextUtils.isEmpty(info)) {
                    Looper.prepare();
                    Toast.makeText(MatchActivity.this, "已保存需求到草稿箱", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    //返回到上一页
                    finish();

                }
            }
        }).start();


    }
}
