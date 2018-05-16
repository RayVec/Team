package com.example.lzw.team20.activity.chatting;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.lzw.team20.R;
import com.example.lzw.team20.activity.rtableDetailSupport.RTableDetailActivity;

import common.ChatMessage;
import common.ChatMessageType;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class ChatActivity extends Activity {
    public static String myInfo;
    private String nowtime;
    EditText et_input;
    private ListView chatListView;
    private FloatingActionButton floatingButton;
    private String myAccount;
    private String deskAccount;
    private String myNick;
    private String deskNick;
    private String myAvatar;
    private String deskAvatar;
    private ImageButton deskButton;
    private ImageView avatar_iv;
    IntentFilter myIntentFilter;
    private boolean isReady;
    private boolean isHost;
    private boolean isJoined;
    public List<ChatEntity> chatEntityList=new ArrayList<ChatEntity>();//所有聊天内容
    public static int[] avatar=new int[]{R.drawable.avatar_default,R.drawable.h001,R.drawable.h002,R.drawable.h003,
            R.drawable.h004,R.drawable.h005,R.drawable.h006};
    MyBroadcastReceiver br;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置不需要标题
        setContentView(R.layout.activity_chatting);   //设置主界面
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //获取用户的账号
        myAccount=getIntent().getStringExtra("myaccount");
        //获取圆桌的昵称
        deskNick=getIntent().getStringExtra("desknick");
        //获取圆桌的账号
        deskAccount=getIntent().getStringExtra("deskaccount");
        myNick= getIntent().getStringExtra("mynick");
        myAvatar=getIntent().getStringExtra("myavatar");
        deskAvatar=getIntent().getStringExtra("deskavatar");
        avatar_iv=(ImageView) findViewById(R.id.chat_top_avatar);
        if(deskAvatar==null||deskAvatar.isEmpty()||deskAvatar.equals("null"))
            avatar_iv.setImageResource(avatar[1]);
        else
            avatar_iv.setImageResource(avatar[Integer.parseInt(deskAvatar)]);   //设置头像
        TextView nick_tv=(TextView) findViewById(R.id.chat_top_nick);
        et_input=(EditText) findViewById(R.id.et_input);
        floatingButton = findViewById(R.id.fbutton_prepare);
        deskButton=findViewById(R.id.deskButton);
        deskButton.setImageResource(R.drawable.rtable);
        deskButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ChatActivity.this, RTableDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("tid",deskAccount);
                intent.putExtras(bundle);

                startActivity(intent);

            }
        });
        chatListView=findViewById(R.id.lv_chat);
        chatEntityList=new ArrayList<>();
        //获取当前用户是否准备
        if(isReady(deskAccount)==1){
            Log.d("updateReady","success");
        }
        else {
            Log.d("updateReady","fail");
            Toast.makeText(ChatActivity.this,"更新当前用户是否准备失败",Toast.LENGTH_SHORT).show();
        }
        nick_tv.setText(deskNick);
        myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("org.yhn.yq.mes");
        br=new MyBroadcastReceiver();    //创建一个广播接收器
        findViewById(R.id.ib_send).setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                ObjectOutputStream oos;
                //通过account找到该线程，从而得到OutputStream
                //得到输入的数据，并清空EditText
                String chatContent=et_input.getText().toString();
                et_input.setText("");
                //首先在后台存储此消息:
                int save=saveMessage(chatContent,deskAccount,0,MyTime.geTime());
                if(save==1) {
                    try {
                        transferMessage(chatContent, ChatMessageType.COM_MES);
                        //更新聊天内容
                        updateChatView(new ChatEntity(Integer.parseInt(myAvatar), chatContent, nowtime, 2,myNick));
                    }
                    catch (Exception e){
                        Toast.makeText(ChatActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(ChatActivity.this,"发送消息失败，请重试",Toast.LENGTH_SHORT).show();
                }
                int b=clearUnread(deskAccount);
                if(b==1){
                    Log.d("clearunread","success");
                }else{
                    Log.d("clearunread","fail");
                }
            }
        });

        chatListView.setOnTouchListener(new View.OnTouchListener()  {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        floatingButton.setVisibility(View.GONE);
                        break;
                    case MotionEvent.ACTION_UP:
                        floatingButton.setVisibility(View.VISIBLE);
                }
                return false;//注意此处不能返回true，因为如果返回true,onTouchEvent就无法执行，导致的后果是ListView无法滑动
            }
        });
        floatingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHost){
                    String content="圆桌组队成功";
                    int a=saveMessage(content,deskAccount,1,MyTime.geTime());
                    if(a==1){
                        try {
                            transferMessage(content, ChatMessageType.SYSTEM);
                            floatingButton.setImageResource(R.drawable.teamedpng);
                            floatingButton.setEnabled(false);
                            updateChatView(new ChatEntity(content,1));
                        }
                        catch (Exception e){
                            Toast.makeText(ChatActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(ChatActivity.this,"圆桌组队失败，请重试",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (isReady) {
                        int result = doUnReady(deskAccount);
                        if (result == 1) {
                            String content=myNick+" 取消了准备";
                            int a=saveMessage(content,deskAccount,1,MyTime.geTime());
                            if(a==1){
                                try {
                                    transferMessage(content,ChatMessageType.SYSTEM);
                                    floatingButton.setImageResource(R.drawable.prepare1);
                                    isReady = false;
                                    updateChatView(new ChatEntity(content,1));
                                }
                                catch (Exception e){
                                    Toast.makeText(ChatActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(ChatActivity.this,"出现意外了，取消准备失败",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "出现意外了，取消准备失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        int result = doReady(deskAccount);
                        if (result == 1) {
                            String content=myNick+"  准备了";
                            int a=saveMessage(content,deskAccount,1,MyTime.geTime());
                            if(a==1) {
                                try {
                                    transferMessage(content, ChatMessageType.SYSTEM);
                                    floatingButton.setImageResource(R.drawable.prepared);
                                    isReady = true;
                                    updateChatView(new ChatEntity(content,1));
                                }
                                catch (Exception e){
                                    Toast.makeText(ChatActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "出现意外了，准备失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                int b=clearUnread(deskAccount);
                if(b==1){
                    Log.d("clearunread","success");
                }else{
                    Log.d("clearunread","fail");
                }
            }
        });

    }
    @Override
    public void onStop(){
        super.onStop();
        unregisterReceiver(br);

    }
    @Override
    public void onResume(){
        super.onResume();
        //更新准备按钮的信息
        updateFloatingButton();
        //获取此圆桌的全部消息
        MessageTask messageTask=new MessageTask();
        messageTask.execute();
        //标记当前获取的全部消息为已读
        if(clearUnread(deskAccount)==1){
            Log.d("clearunread","success");
        }
        else{
            Log.d("clearunread","fail");
            Toast.makeText(ChatActivity.this,"标记获得的消息为已读失败",Toast.LENGTH_SHORT).show();
        }
        //注册广播
        registerReceiver(br,myIntentFilter);
    }
    //广播接收器
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] mes = intent.getStringArrayExtra("message");
            if(mes[Message.TYPE].equals(ChatMessageType.COM_MES)) {
                //更新聊天内容
                if(mes[Message.DESK].equals(deskAccount)) {
                    updateChatView(new ChatEntity(Integer.parseInt(mes[Message.SENDERAVATAR]), mes[Message.CONTENT], mes[Message.TIME], 0,mes[Message.SENDERNICK]));
                    //标记此消息为已读
                    int a = clearUnread(deskAccount);
                    if (a == 0) {
                        Toast.makeText(ChatActivity.this, "标记此消息为已读 失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else if(mes[Message.TYPE].equals(ChatMessageType.SYSTEM)){
                if(mes[Message.DESK].equals(deskAccount)) {
                    updateChatView(new ChatEntity(mes[Message.CONTENT], 1));
                    //更新准备按钮的信息
                    updateFloatingButton();
                    int a = clearUnread(deskAccount);
                    if (a == 0) {
                        Toast.makeText(ChatActivity.this, "标记系统消息为已读 失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    public void updateChatView(ChatEntity chatEntity){
        chatEntityList.add(chatEntity);
        chatListView=(ListView) findViewById(R.id.lv_chat);
        chatListView.setAdapter(new MsgAdapter(this,chatEntityList));
    }
    public int isReady(String rtableid){
        String url="http://112.74.177.29:8080/together/rtable/isReady?rtableid="+rtableid;
        String result=HTTPUtils.doGet(url,HTTPUtils.cookie);
        if(result!=null) {
            if (result.equals("true")) {
                isReady = true;
            } else if (result.equals("false")) {
                isReady = false;
            }
            return 1;
        }
        else{
            return 0;
        }
    }
    public int doReady(String rtableid){
        String url="http://112.74.177.29:8080/together/rtable/beReady?rtableid="+rtableid;
        if(HTTPUtils.doGet(url,HTTPUtils.cookie)!=null){
            return 1;
        }
        else {
            return 0;
        }
    }
    public int doUnReady(String rtableid){
        String url="http://112.74.177.29:8080/together/rtable/beUnready?rtableid="+rtableid;
        if(HTTPUtils.doGet(url,HTTPUtils.cookie)!=null){
            return 1;
        }
        else {
            return 0;
        }
    }
    public int saveMessage(String content,String rtableid,int type,String createtime){
        String url="http://112.74.177.29:8080/together/message/saveMessage";
        String data="tableid="+rtableid+"&content="+content+"&type="+type+"&createtime="+createtime;
        String result=HTTPUtils.doPost(url,HTTPUtils.cookie,data);
        if(result!=null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String a = jsonObject.getString("data");
                if (a.equals("true")) {
                    return 1;
                }else return 0;
            }
            catch (JSONException e){
                return 0;
            }
        }
        else {
            return 0;
        }
    }
    public int clearUnread(String rtableid){
        String url="http://112.74.177.29:8080/together/message/clearUnread?tableid="+rtableid;
        String result=HTTPUtils.doGet(url,HTTPUtils.cookie);
        if(result!=null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String a = jsonObject.getString("data");
                if (a.equals("true")) {
                    return 1;
                } else {
                    return 0;
                }
            }
            catch (JSONException e){
                return 0;
            }
        }
        else {
            return 0;
        }
    }
    public String getAllMsg(String rtableid){
        String url="http://112.74.177.29:8080/together/message/getAllTableMessage?tableid="+rtableid;
        return  HTTPUtils.doGet(url,HTTPUtils.cookie);
    }
    public int identity(String rtableid){
        String url="http://112.74.177.29:8080/together/rtable/getMembers?rtableid="+rtableid;
        String result=HTTPUtils.doGet(url,HTTPUtils.cookie);
        System.out.println("获取members为"+result);
        try {
            JSONArray jsonArray = new JSONArray(result);
            int identity = 1;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String userid = jsonObject.getString("userid");
                if (userid.equals(myAccount)) {
                    String a = jsonObject.getString("identity");
                    identity = Integer.parseInt(a);
                    break;
                }
            }
            return identity;
        }
        catch (JSONException e){
            return 2;
        }
    }
    public int isAllReady(String rtableid) {
        String url = "http://112.74.177.29:8080/together/rtable/isAllReady?rtableid=" + rtableid;
        String b = HTTPUtils.doGet(url, HTTPUtils.cookie);
        System.out.println(b);
        String a=b.replace(" ","");
        if(a.contains("true")){
            return 1;
        }
        else if(a.contains("false")){
            return 0;
        }
        else {
            return 2;
        }
    }
    public void transferMessage(String chatContent,String messagetype)throws Exception{
        try {
            nowtime = MyTime.geTime();
            ChatMessage m = new ChatMessage();
            m.setType(messagetype);//普通信息包
            m.setSender(myAccount);          //设置账号
            m.setSenderNick(myNick);  //设置昵称
            m.setSenderAvatar(Integer.parseInt(myAvatar));   //设置头像
            m.setDesk(deskAccount);          //设置接收者的账号
            m.setContent(chatContent);           //设置消息内容
            m.setSendTime(nowtime);    //设置发送的时间
            m.setCookie(HTTPUtils.cookie);//发送cookie
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(ManageClientConServer.getClientConServerThread(myAccount).getS().getOutputStream());
            oos.writeObject(m);
            Log.d("send", m.getContent());
        }
        catch (IOException e) {
            throw new Exception("发送消息["+chatContent+"]失败，请重试");
        }
    }
    public void updateFloatingButton(){
        int identity=identity(deskAccount);
        if(identity==0) {
            isHost=true;
            int isallready = isAllReady(deskAccount);
            System.out.println(isallready);
            if (isallready == 1) {
                floatingButton.setImageResource(R.drawable.team);
                floatingButton.setEnabled(true);
            }
            else if (isallready == 0) {
                floatingButton.setImageResource(R.drawable.team);
                floatingButton.setEnabled(false);
            }
            else if(isallready==2){
                floatingButton.setImageResource(R.drawable.team);
                floatingButton.setEnabled(false);
                Toast.makeText(ChatActivity.this,"获取当前圆桌其他用户是否全部准备 失败",Toast.LENGTH_SHORT).show();
            }
        }
        else if(identity==1){
            isHost=false;
            if(isReady) {
                floatingButton.setImageResource(R.drawable.prepared);
            }
            else{
                floatingButton.setImageResource(R.drawable.prepare1);
            }
        }
        else {
            Toast.makeText(ChatActivity.this,"获取用户是否为桌主 失败",Toast.LENGTH_SHORT).show();
        }
    }
    public class MessageTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            String msgdata=getAllMsg(deskAccount);
            System.out.println(msgdata);
            Boolean b=false;
            if(msgdata!=null&& !msgdata.equals("")){
                    chatEntityList.clear();
                    Log.d("getallmessage", "successs");
                    try {
                        JSONObject jsonObject = new JSONObject(msgdata);
                        if(jsonObject.getString("message").equals("未找此圆桌的消息")){
                            b=true;
                        }
                        else {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject message = jsonArray.getJSONObject(i);
                                    if (message.getString("type").equals("0")) {
                                        if (message.getString("userid").equals(myAccount)) {
                                            chatEntityList.add(new ChatEntity(Integer.parseInt(myAvatar), message.getString("content"), message.getString("createtime"), 2, myNick));
                                        } else {
                                            if (message.getString("userPic").equals("null")) {
                                                chatEntityList.add(new ChatEntity(1, message.getString("content"), message.getString("createtime"), 0, getNick(message.getString("userid"))));
                                            } else {
                                                chatEntityList.add(new ChatEntity(Integer.parseInt(message.getString("userPic")), message.getString("content"), message.getString("createtime"), 0, getNick(message.getString("userid"))));
                                            }
                                        }
                                    } else {
                                        chatEntityList.add(new ChatEntity(message.getString("content"), 1));
                                    }
                                }
                            }
                            b = true;
                        }
                    } catch (JSONException e) {
                        b = false;
                    }
            }
            else {
                Log.d("getallmessage","fail");
                b=false;
            }
            return b;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                chatListView.setAdapter(new MsgAdapter(ChatActivity.this, chatEntityList));
            }
            else{
                Toast.makeText(ChatActivity.this,"获取圆桌全部消息失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public String getNick(String userid){
        String url="http://112.74.177.29:8080/together/user/getUser?userid="+userid;
        String result= HTTPUtils.doGet(url,HTTPUtils.cookie);
        String nick="";
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject userinfo=jsonObject.getJSONObject("data");
            nick=userinfo.getString("username");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return nick;
    }

}
