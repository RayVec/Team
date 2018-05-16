package com.example.lzw.team20.activity.rtableDetailSupport;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lzw.team20.R;
import com.example.lzw.team20.activity.mineSupport.RTableSimple;

import java.util.List;

public class MyMemberAdapter extends BaseAdapter {
    private List<MemberSimple> mData;
    private Context context;

    public static int[] avatar=new int[]{R.drawable.avatar_default,R.drawable.h001,R.drawable.h002,R.drawable.h003,
            R.drawable.h004,R.drawable.h005,R.drawable.h006};

    public void setmData(List<MemberSimple> members){
        this.mData=members;
    }

    public void setContext(Context context){
        this.context=context;
    }

    @Override
    public int getCount(){
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position){
        return mData.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2){
        View view=View.inflate(context, R.layout.members_item,null);
        MemberSimple member=mData.get(position);


        ImageView memberAvator=view.findViewById(R.id.iv_head);
        if(member.uphoto.equals("null")||member.uphoto==null||member.uphoto.isEmpty())
            memberAvator.setImageResource(avatar[1]);
        else
            memberAvator.setImageResource(avatar[Integer.valueOf(member.uphoto)]);


        TextView memberName=view.findViewById(R.id.tv_membername);
        memberName.setText(member.userName);


        return view;

    }
}
