package com.example.lzw.team20.activity.chatting;
import java.util.List;



import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lzw.team20.R;

public class MsgAdapter extends BaseAdapter{
    private Context context;
    private List<ChatEntity> list;//目前需要显示的所有聊天信息
    private LayoutInflater inflater;
    private int[] avatars=new int[]{0,R.drawable.h001,R.drawable.h002,R.drawable.h003, R.drawable.h004,R.drawable.h005,R.drawable.h006};

    public MsgAdapter(Context context,List<ChatEntity> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup root) {
        ImageView avatar;
        TextView content;
        TextView time;
        TextView nick;
        ChatEntity ce=list.get(position);
        if(ce.getType()==0){
            convertView = inflater.inflate(R.layout.chat_listview_item_left, null);
            avatar=(ImageView) convertView.findViewById(R.id.avatar_chat_left);
            content=(TextView) convertView.findViewById(R.id.message_chat_left);
            time=(TextView) convertView.findViewById(R.id.sendtime_chat_left);
            nick=convertView.findViewById(R.id.left_nick);
            avatar.setImageResource(avatars[ce.getAvatar()]);
            content.setText(ce.getContent());
            time.setText(ce.getTime());
            nick.setText(ce.getNick());
        }
        else if(ce.getType()==1){
            convertView=inflater.inflate(R.layout.chat_listview_item_middle,null);
            TextView text=convertView.findViewById(R.id.message_chat_middle);
            text.setText(ce.getContent());
            text.setGravity(Gravity.CENTER);
        }
        else if(ce.getType()==2){
            System.out.println("我的消息");
            convertView=inflater.inflate(R.layout.chat_listview_item_right, null);
            avatar=(ImageView) convertView.findViewById(R.id.avatar_chat_right);
            content=(TextView) convertView.findViewById(R.id.message_chat_right);
            time=(TextView) convertView.findViewById(R.id.sendtime_chat_right);
            nick=convertView.findViewById(R.id.right_nick);
            avatar.setImageResource(avatars[ce.getAvatar()]);
            content.setText(ce.getContent());
            time.setText(ce.getTime());
            nick.setText(ce.getNick());
        }
        return convertView;
    }
    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
}
