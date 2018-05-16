package com.example.lzw.team20.activity.chatting;
import java.util.List;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lzw.team20.R;

public class RecentAdapter extends BaseAdapter{
	private Context context;
	private List<RecentEntity> list;
	LayoutInflater inflater;
	public static int[] unReads={R.drawable.message_count1,R.drawable.message_count2,R.drawable.message_count3,R.drawable.message_count4,R.drawable.message_count5,R.drawable.message_count6,R.drawable.message_count7,R.drawable.message_count8,R.drawable.message_count9,R.drawable.message_count10};

	public RecentAdapter(Context context,List<RecentEntity> list){
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	public View getView(int position, View convertView, ViewGroup root) {
		convertView = inflater.inflate(R.layout.recent_listview_item, null);

		ImageView avatar=(ImageView) convertView.findViewById(R.id.iv_avatar_r);
		TextView nick=(TextView) convertView.findViewById(R.id.tv_nick_r);
		TextView content=(TextView) convertView.findViewById(R.id.tv_chat_content_r);
		ImageView unRead=(ImageView) convertView.findViewById(R.id.iv_tip_mes_r);
		TextView time=(TextView) convertView.findViewById(R.id.tv_time_r);

		RecentEntity re=list.get(position);
		avatar.setImageResource(ChatActivity.avatar[re.getAvatar()]);
		nick.setText(re.getNick());
		content.setText(re.getContent());
		if(re.getUnRead()>0&&re.getUnRead()<10) {
			unRead.setImageResource(unReads[re.getUnRead()-1]);
		}
		else if(re.getUnRead()>=10) {
			unRead.setImageResource(unReads[9]);
		}
		time.setText(re.getTime());

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
