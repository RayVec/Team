package com.example.lzw.team20.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;
import com.example.lzw.team20.activity.chatting.ChatActivity;
import com.example.lzw.team20.waterfallSupport.Card;
import com.example.lzw.team20.waterfallSupport.WaterfallAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MatchResultActivity extends AppCompatActivity {

    private JSONObject data;
    private String keyword;
    private Bundle bundle;
    //瀑布流
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private WaterfallAdapter mAdapter;
    private List<Card> cards=new ArrayList<>();

    //用户id  需要登录注册传入的信息
    private String uid;
    //用户昵称
    private String unick;
    //用户头像
    private String uphoto;

    public static int[] avatar=new int[]{R.drawable.avatar_default,R.drawable.h001,R.drawable.h002,R.drawable.h003,
            R.drawable.h004,R.drawable.h005,R.drawable.h006};


    //该圆桌可加入
    Boolean canJoin=true;
    String refuseMessage;
    String resultFromdata;
    private final Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case R.string.msg_joinfail:
                    Toast.makeText(MatchResultActivity.this, R.string.msg_joinfail, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private Button bt_create;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchresult);

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

        Intent intent = getIntent();
        try{
            data=new JSONObject(intent.getStringExtra("data"));
        }catch (JSONException e){
            e.printStackTrace();
        }

        bundle = intent.getExtras();
        uid = bundle.getString("userid");
        unick = bundle.getString("username");
        uphoto = bundle.getString("photo");
        keyword=bundle.getString("text_secLabel");

        bt_create=(Button)findViewById(R.id.bt_createRTable);
        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MatchResultActivity.this, TableBuildingActivity.class);

                intent.putExtras(bundle);
                intent.putExtra("data",data.toString());
                startActivity(intent);
            }
        });



        setWaterfall();

    }

    public void setWaterfall(){
        initData();
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        //设置布局管理器为2列，纵向
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new WaterfallAdapter(this, cards);
        if(cards.size()==0)
            Toast.makeText(this,"当前还没有匹配圆桌",Toast.LENGTH_SHORT).show();


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnCardClickListener(new WaterfallAdapter.OnCardClickListener(){

            //在此处编写点击响应事件

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view,WaterfallAdapter.ClickName clickName,String tid,String tname,String tphoto){
                switch (clickName){
                    case JOIN_BUTTON:

                        //Toast.makeText(getContext(),"Click button from "+tid,Toast.LENGTH_SHORT).show();
                        /*join_msgHandler=new Handler(){
                            @Override
                            public void handleMessage(Message msg){
                                switch (msg.arg1){
                                    case R.string.msg_tableisfull:
                                        Toast.makeText(getActivity(), getResources().getString(R.string.msg_tableisfull), Toast.LENGTH_SHORT).show();
                                        setCanJoinFalse();
                                        break;
                                    case R.string.msg_tableisJoined:
                                        Toast.makeText(getActivity(), getResources().getString(R.string.msg_tableisJoined), Toast.LENGTH_SHORT).show();
                                        setCanJoinFalse();
                                        break;
                                }

                            }
                        };*/
                        isFull(tid);
                        isJoin(tid);

                        if(canJoin)
                        {
                            joinTable(tid);
                            transIntentChatting(view,tid,tname,tphoto);
                        }else
                            Toast.makeText(MatchResultActivity.this, refuseMessage, Toast.LENGTH_SHORT).show();

                        break;
                    case CARD:
                        //Toast.makeText(getContext(),"Click card from "+tid,Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    public void initData(){
        //在此处编写获取数据相关代码

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String result= WebService.getRtables(keyword);
                try{
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray jsonArray=jsonObject.getJSONArray("content");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                        Card card=new Card();
                        card.tid=jsonObject1.getString("rtableid");           //圆桌id
                        card.cardName=jsonObject1.getString("rtablename");  //圆桌名
                        card.cardLabel=jsonObject1.getString("first_tag"); //圆桌类别
                        card.cardLabel_2=jsonObject1.getString("rtablename"); //二级标签
                        card.intro=jsonObject1.getString("detail");         //圆桌简介
                        String time=jsonObject1.getString("createtime");
                        card.time=jsonObject1.getString("createtime").substring(0,10)+"~"+jsonObject1.getString("ddl").substring(0,10);           //时间
                        int lastNum=jsonObject1.getInt("maxNumber")-jsonObject1.getInt("nowNumber");
                        if(lastNum>0)
                            card.lastNum = String.valueOf(lastNum);
                        else
                            card.lastNum="0";
                        card.avatarNo=jsonObject1.getString("photo");

                        cards.add(card);

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        try{
            thread.start();
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }








        //测试 假装有数据
        /*
        String[] tid={"000","001","002","003","004","005","006","007"};
        String[] cardName={"约学习","约食堂","ACM竞赛","ACM竞赛","ACM竞赛","ACM竞赛","ACM竞赛","ACM竞赛"};
        String[] cardLabel={"学习","娱乐","学习","学习","学习","学习","学习","学习"};
        String[] cardLabel_2={"自习","吃","比赛","ACM","ACM","ACM","ACM","ACM"};
        String[] intro={"如果你无法简洁的表达你的想法，那只说明你还不够了解它。那只说明你还不够了解它。",
                "如果你无法那只说明你还不够了解它。",
                "如果你无法简洁的表达你解它。",
                "如果你无法简洁的表达你解它。",
                "如果你无法简洁的表达你解它。",
                "如果你无法简洁的表达你的想法，那只说明你还不够了解它。那只说明你还不够了解它。",
                "如果你无法简洁的表达你的想法，那只说明你还不够了解它。那只说明你还不够了解它。",
                "如果你无法那只说明你还不够了解它。"};
        String[] time={"2017.2.13-2018.3.15","2017.2.13-2018.3.15","2017.2.13-2018.3.15",
                "2017.2.13-2018.3.15","2017.2.13-2018.3.15","2017.2.13-2018.3.15",
                "2017.2.13-2018.3.15","2017.2.13-2018.3.15"};
        String[] lastNum={"2","3","5","5","5","5","5","5"};
        String[] head={"E:\\AndroidProject\\Meizhi-master\\Waterfall\\app\\src\\main\\res\\mipmap-hdpi\\head.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524042047489&di=2e72d6ffb68c0c1bb62f9584ddd6f828&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0138a0565ed39532f875ae34c10c69.jpg%402o.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524042047489&di=2e72d6ffb68c0c1bb62f9584ddd6f828&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0138a0565ed39532f875ae34c10c69.jpg%402o.jpg"};

        for(int i=0;i<8;i++){
            Card card=new Card();
            card.tid=tid[i];             //圆桌id
            card.cardName=cardName[i];   //圆桌名
            card.cardLabel=cardLabel[i]; //圆桌类别
            card.cardLabel_2=cardLabel_2[i]; //二级标签
            card.intro=intro[i];         //圆桌简介
            card.time=time[i];           //时间
            card.lastNum=lastNum[i];     //剩余席位
            //card.headUrl=head[i];

            cards.add(card);
        }*/

    }

    private void setCanJoinFalse(){
        canJoin=false;
    }

    private void setMessage(String message){
        refuseMessage=message;
    }

    //判断该圆桌是否已满
    private void isFull(final String tid){
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    String info = WebService.getOne(tid);
                    if (!TextUtils.isEmpty(info)) {
                        try {
                            JSONObject json = new JSONObject(info);
                            String nowNumber = json.getString("nowNumber");
                            String maxNumber=json.getString("maxNumber");
                            int last=Integer.valueOf(maxNumber)-Integer.valueOf(nowNumber);
                            if (last<=0) {
                                /*Message msg = join_msgHandler.obtainMessage();
                                msg.arg1 = R.string.msg_tableisfull;
                                join_msgHandler.sendMessage(msg);*/
                                setCanJoinFalse();
                                setMessage("该圆桌已满");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //判断该用户是否已加入圆桌
    private void isJoin(final String tid){
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    String info = WebService.joinRTable(tid);
                    if (!TextUtils.isEmpty(info)) {
                        try {
                            //JSONObject json = new JSONObject(info);
                            //String result = json.getString("result");
                            if (info.equals("false")) {
                                /*Message msg = join_msgHandler.obtainMessage();
                                msg.arg1 = R.string.msg_tableisJoined;
                                join_msgHandler.sendMessage(msg);*/
                                setCanJoinFalse();
                                setMessage("已经是该圆桌成员");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //该用户加入圆桌
    private void joinTable(final String tid) {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    String info = WebService.joinRTable(tid);
                    if (!TextUtils.isEmpty(info)) {
                        try {
                            JSONObject json = new JSONObject(info);
                            String result = json.getString("result");
                            if (result.equals("false")) {
                                Message msg = msgHandler.obtainMessage();
                                msg.arg1 = R.string.msg_joinfail;
                                msgHandler.sendMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //点击join跳转至圆桌聊天界面（暂时）
    private void transIntentChatting(View v,String tid,String tname,String tphoto){
        Intent intent=new Intent(this, ChatActivity.class);

        //创建Bundle打包数据
        Bundle bundle=new Bundle();
        bundle.putString("myaccount",uid);
        bundle.putString("deskaccount",tid);
        bundle.putString("desknick",tname);
        bundle.putString("mynick",unick);
        bundle.putString("myavatar",uphoto);
        bundle.putString("deskavatar",tphoto);

        intent.putExtras(bundle);
        intent.putExtra("data",data.toString());

        startActivity(intent);


    }
}
