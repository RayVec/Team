package com.example.lzw.team20.fragment;
import com.example.lzw.team20.R;
import com.example.lzw.team20.activity.chatting.ChatActivity;
import com.example.lzw.team20.activity.chatting.HTTPUtils;
import com.example.lzw.team20.activity.chatting.Message;
import com.example.lzw.team20.activity.chatting.RecentAdapter;
import com.example.lzw.team20.activity.chatting.RecentEntity;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.ChatMessageType;


/**
 * Created by Myh on 2018/3/23.
 */

public class NewsFragment extends Fragment {

    //新增
    ListView listView;
    List<RecentEntity> recentEntityList=new ArrayList<RecentEntity>();
    String[] mes;
    String myAccount;
    String myNick;
    String myAvatar;
    NewsFragment.MyBroadcastReceiver br;
    IntentFilter myIntentFilter;

    //传值
    private JSONObject data;
    public NewsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_news,container,false);
        Intent intent=getActivity().getIntent();
        try{
            data=new JSONObject(intent.getStringExtra("data"));
            myAccount=data.getString("userid");
            myNick=data.getString("username");
            myAvatar=data.getString("photo");
        }catch (JSONException e){
            e.printStackTrace();
        }



        //注册广播
        //注册广播
        myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("org.yhn.yq.mes");
        br=new MyBroadcastReceiver();
        listView = (ListView) view.findViewById(R.id.lv_recent);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //打开聊天页面
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("deskaccount", recentEntityList.get(position).getAccount());
                intent.putExtra("desknick", recentEntityList.get(position).getNick());
                intent.putExtra("deskavatar",Integer.toString(recentEntityList.get(position).getAvatar()));
                intent.putExtra("myaccount", myAccount);
                intent.putExtra("mynick",myNick);
                intent.putExtra("myavatar",myAvatar);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStop(){
        super.onStop();
        getActivity().unregisterReceiver(br);
    }
    @Override
    public void onResume(){
        super.onResume();
        //更新界面内容
        InfoTask infoTask=new InfoTask();
        getActivity().registerReceiver(br,myIntentFilter);

        infoTask.execute();
    }

    //广播接收器
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //广播内容依次为发送者账号，发送者昵称，发送者头像，内容，时间
            mes = intent.getStringArrayExtra("message");
            if(mes[Message.TYPE].equals(ChatMessageType.COM_MES)||mes[Message.TYPE].equals(ChatMessageType.SYSTEM)) {
                Toast.makeText(context, mes[Message.SENDERNICK] + " : " + mes[Message.CONTENT], Toast.LENGTH_SHORT).show();
                int unread = getUnread(mes[Message.DESK]);
                int avatar=0;
                for(int i=0;i<recentEntityList.size();i++){
                    if(recentEntityList.get(i).getAccount().equals(mes[Message.DESK]));
                         avatar=recentEntityList.get(i).getAvatar();
                }
                RecentEntity recentEntity = new RecentEntity(avatar, mes[Message.DESK], mes[Message.SENDERNICK], mes[Message.CONTENT], mes[Message.TIME], unread);
                updateView(recentEntity);
            }
        }
    }
    public void updateView(RecentEntity recentEntity){
        updateRecentlist(recentEntity);
        listView.setAdapter(new RecentAdapter(getActivity(),recentEntityList));
    }
    public void updateRecentlist(RecentEntity recentEntity){
        boolean a=false;
        if(recentEntityList.size()==0){
            recentEntityList.add(recentEntity);
        }
        else {
            for (int i = 0; i < recentEntityList.size(); i++) {
                int compare = recentEntity.getTime().compareTo(recentEntityList.get(i).getTime());
                if (compare > 0 || compare == 0) {
                    recentEntityList.add(i, recentEntity);
                    for (int j = i + 1; j < recentEntityList.size(); j++) {
                        if (recentEntity.getAccount().equals(recentEntityList.get(j).getAccount())) {
                            recentEntityList.remove(j);
                        }
                    }
                    a = true;
                    break;
                }
            }
            if (!a) {
                recentEntityList.add(recentEntity);
            }
        }
    }
    public int getUnread(String rtableid){
        String url="http://112.74.177.29:8080/together/message/getLatest?tableid="+rtableid;
        String result=HTTPUtils.doGet(url,HTTPUtils.cookie);
        int unread=0;
        try {
            JSONArray jsonArray = new JSONArray(result);
            unread = Integer.parseInt(jsonArray.get(0).toString());
        }
        catch (JSONException e){
            Toast.makeText(getActivity(),"解析未读消息数失败",Toast.LENGTH_SHORT).show();
        }
        return unread;
    }

    public String getLatest(String rtableid){
        String url="http://112.74.177.29:8080/together/message/getLatest?tableid="+rtableid;
        String result=HTTPUtils.doGet(url,HTTPUtils.cookie);
        return result;
    }
    public class InfoTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            List<RecentEntity> desks = new ArrayList<>();
            //获取用户参与的圆桌
            String result1 = getAttendDesk();
            Log.d("mydesk", result1 + "dfdf ");
            try {
                if (result1 != null && !result1.equals("")&&!result1.equals("[]")) {
                    JSONArray jsonArray = new JSONArray(result1);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        RecentEntity desk = new RecentEntity();
                        desk.setAccount(jsonObject.getString("rtableid"));
                        if(jsonObject.getString("photo").equals("null")) {
                            desk.setAvatar(0);
                        }
                        else{
                            desk.setAvatar(Integer.parseInt(jsonObject.getString("photo")));
                        }
                        desk.setNick(jsonObject.getString("rtablename"));
                        //获取最后一条消息的信息
                        String latest = getLatest(jsonObject.getString("rtableid"));
                        System.out.println(latest);
                        JSONObject content = new JSONObject(latest);
                        JSONArray lateinfos = content.getJSONArray("data");
                        int unread = Integer.parseInt(lateinfos.get(0).toString());
                        if(lateinfos.length()<3){
                            desk.setContent("");
                            desk.setTime("");
                        }
                        else {
                            JSONObject contentobject = lateinfos.getJSONObject(2);
                            String contentinfo = contentobject.getString("content");
                            JSONObject userinfo = lateinfos.getJSONObject(1);
                            String nickname = userinfo.getString("username");
                            if(contentobject.getString("type").equals("0")) {
                                desk.setContent(nickname + " : " + contentinfo);
                            }else{
                                desk.setContent(contentinfo);
                            }
                            desk.setTime(contentobject.getString("createtime"));
                        }
                        desk.setUnRead(unread);
                        desks.add(desk);
                    }
                }
                //获取用户创建的圆桌
                String result2 = getCreatedDesk();
                System.out.print(result2);
                if (result2 != null && !result2.equals("[]")&&!result2.equals("")) {
                    JSONArray jsonArray = new JSONArray(result2);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        RecentEntity desk = new RecentEntity();
                        desk.setAccount(jsonObject.getString("rtableid"));
                        if(jsonObject.getString("photo").equals("null")) {
                            desk.setAvatar(0);
                        }
                        else{
                            desk.setAvatar(Integer.parseInt(jsonObject.getString("photo")));
                        }
                        desk.setNick(jsonObject.getString("rtablename"));

                        String latest = getLatest(jsonObject.getString("rtableid"));
                        JSONObject content = new JSONObject(latest);
                        JSONArray lateinfos = content.getJSONArray("data");
                        int unread = Integer.parseInt(lateinfos.get(0).toString());
                        if(lateinfos.length()<3){
                            desk.setContent("");
                            desk.setTime("");
                        }
                        else {
                            JSONObject contentobject = lateinfos.getJSONObject(2);
                            String contentinfo = contentobject.getString("content");
                            JSONObject userinfo = lateinfos.getJSONObject(1);
                            String nickname = userinfo.getString("username");
                            if(contentobject.getString("type").equals("0")) {
                                desk.setContent(nickname + " : " + contentinfo);
                            }else{
                                desk.setContent(contentinfo);
                            }
                            desk.setTime(contentobject.getString("createtime"));
                        }
                        desk.setUnRead(unread);
                        desks.add(desk);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(RecentActivity.this, "解析用户所有圆桌的信息失败", Toast.LENGTH_SHORT).show();
            }
            for (RecentEntity recentEntity : desks) {
                updateRecentlist(recentEntity);
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            listView.setAdapter(new RecentAdapter(getActivity(), recentEntityList));
        }
        public String getCreatedDesk(){
            String url="http://112.74.177.29:8080/together/rtable/getUserCreateAll";
            String result= HTTPUtils.doGet(url,HTTPUtils.cookie);
            return result;
        }
        public String getAttendDesk(){
            String url="http://112.74.177.29:8080/together/rtable/getUserAttendAll";
            String result=HTTPUtils.doGet(url,HTTPUtils.cookie);
            return result;
        }
    }
}
