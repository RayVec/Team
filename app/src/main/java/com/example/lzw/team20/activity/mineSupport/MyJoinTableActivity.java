package com.example.lzw.team20.activity.mineSupport;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;
import com.example.lzw.team20.activity.chatting.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyJoinTableActivity extends AppCompatActivity {

    private JSONObject data;
    private String userid;
    private String unick;
    private String uphoto;

    private ListView lv_joinTables;
    private MyRTableAdapter myRTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_jointables);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("我参与的圆桌");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try{
            data=new JSONObject(getIntent().getStringExtra("data"));
            userid=data.getString("userid");
            unick=data.getString("username");
            uphoto=data.getString("photo");
        }catch (JSONException e){
            e.printStackTrace();
        }

        lv_joinTables=findViewById(R.id.mine_jointables);
        myRTableAdapter=new MyRTableAdapter();
        myRTableAdapter.setContext(this);
        List<RTableSimple> rtables=getMyJoinTables();
        if(rtables.size()==0){
            Toast.makeText(getApplicationContext(),"您当前还没有加入圆桌~",Toast.LENGTH_SHORT).show();
        }else{
            myRTableAdapter.setmData(rtables);

            lv_joinTables.setAdapter(myRTableAdapter);
            lv_joinTables.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    RTableSimple rtable=(RTableSimple) myRTableAdapter.getItem(i);
                    transIntentChatting(view,rtable.tid,rtable.tableName,rtable.tphoto);

                }
            });
        }

    }

    public List<RTableSimple> getMyJoinTables(){
        try {
            final List<RTableSimple> rTableSimples = new ArrayList<>();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try{
                        String result = WebService.getUserAttendAll();

                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            RTableSimple rTableSimple = new RTableSimple();
                            rTableSimple.tid = jsonObject1.getString("rtableid");           //圆桌id
                            rTableSimple.tableName = jsonObject1.getString("rtablename");  //草稿一级标签
                            rTableSimple.intro = jsonObject1.getString("detail"); //草稿二级标签
                            rTableSimple.tphoto=jsonObject1.getString("photo");
                            rTableSimples.add(rTableSimple);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
            thread.join();

            return rTableSimples;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //点击item跳转至圆桌聊天界面
    private void transIntentChatting(View v,String tid,String tname,String tphoto){
        Intent intent=new Intent(MyJoinTableActivity.this, ChatActivity.class);

        //创建Bundle打包数据
        Bundle bundle=new Bundle();
        bundle.putString("myaccount",userid);
        bundle.putString("deskaccount",tid);
        bundle.putString("desknick",tname);
        bundle.putString("mynick",unick);
        bundle.putString("myavatar",uphoto);
        bundle.putString("deskavatar",tphoto);

        intent.putExtras(bundle);

        startActivity(intent);


    }
}
