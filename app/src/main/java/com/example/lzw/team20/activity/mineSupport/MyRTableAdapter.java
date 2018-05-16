package com.example.lzw.team20.activity.mineSupport;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lzw.team20.R;
import com.example.lzw.team20.waterfallSupport.CircleImageView;

import java.util.List;

public class MyRTableAdapter extends BaseAdapter{
    private List<RTableSimple> mData;
    private Context context;

    public static int[] avatar=new int[]{R.drawable.avatar_default,R.drawable.h001,R.drawable.h002,R.drawable.h003,
            R.drawable.h004,R.drawable.h005,R.drawable.h006};

    public void setmData(List<RTableSimple> cards){
        this.mData=cards;
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
        View view=View.inflate(context, R.layout.createtables_item,null);
        RTableSimple rtable=mData.get(position);

        CircleImageView rtablephoto=view.findViewById(R.id.iv_head);
        if(rtable.tphoto==null||rtable.tphoto.isEmpty()||rtable.tphoto.equals("null"))
            rtablephoto.setImageResource(avatar[1]);
        else
            rtablephoto.setImageResource(avatar[Integer.valueOf(rtable.tphoto)]);

        TextView rtablename=view.findViewById(R.id.tv_rtable_name);
        rtablename.setText(rtable.tableName);

        TextView rtableIntro=view.findViewById(R.id.tv_rtable_intro);
        String intro=null;
        if(rtable.intro.length()>=12)
            intro=rtable.intro.substring(0,11)+"...";
        else
            intro=rtable.intro;
        rtableIntro.setText(intro);

        return view;

    }
}
