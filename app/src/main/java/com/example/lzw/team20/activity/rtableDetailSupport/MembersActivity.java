package com.example.lzw.team20.activity.rtableDetailSupport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;
import com.example.lzw.team20.activity.mineSupport.MyRTableAdapter;
import com.example.lzw.team20.activity.mineSupport.RTableSimple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MembersActivity extends AppCompatActivity {
    private JSONObject data;

    private String tid;

    private String memberName;
    private String memberPhoto;

    private ListView lv_members;
    private MyMemberAdapter adapter;
    public static int[] avatar=new int[]{R.drawable.avatar_default,R.drawable.h001,R.drawable.h002,R.drawable.h003,
            R.drawable.h004,R.drawable.h005,R.drawable.h006};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rtabledetail_members);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setTitle("圆桌成员");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try{
            tid=getIntent().getStringExtra("tid");
        }catch (Exception e){
            e.printStackTrace();
        }


        lv_members=findViewById(R.id.rtabledetail_members);
        adapter=new MyMemberAdapter();
        adapter.setContext(this);
        List<MemberSimple> members=getMembers();
        if(members.size()==0){
            Toast.makeText(getApplicationContext(),"该圆桌当前无成员~",Toast.LENGTH_SHORT).show();
        }else{
            adapter.setmData(members);

            lv_members.setAdapter(adapter);
            lv_members.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MemberSimple member=(MemberSimple) adapter.getItem(i);

                    //TODO:写好用户界面后跳转
                }
            });
        }

    }

    public List<MemberSimple> getMembers(){
        try {
            final List<MemberSimple> members = new ArrayList<>();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try{
                        String result = WebService.getMembers(tid);

                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            memberPhoto=null;
                            memberName=null;

                            MemberSimple member = new MemberSimple();
                            member.uid = jsonObject1.getString("userid");           //用户id
                            //member.userName = jsonObject1.getString("username");
                            getMemberInfo(member.uid);
                            if(memberPhoto==null||memberPhoto.equals("null"))
                                member.uphoto="1";
                            else
                                member.uphoto=memberPhoto;

                            if(memberName==null||memberName.equals("null"))
                                member.userName="null";
                            else
                                member.userName=memberName;

                            members.add(member);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
            thread.join();

            return members;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getMemberInfo(final String uid){
        try{
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String result = WebService.getUser(uid);
                        if (!TextUtils.isEmpty(result)) {
                            JSONObject json = new JSONObject(result);
                            JSONObject data=json.getJSONObject("data");

                            setMemberPhoto(data.getString("photo"));
                            setMemberName(data.getString("username"));
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }


    }

    private void setMemberPhoto(String photo){
        memberPhoto=photo;
    }
    private void setMemberName(String name){
        memberName=name;
    }
}
