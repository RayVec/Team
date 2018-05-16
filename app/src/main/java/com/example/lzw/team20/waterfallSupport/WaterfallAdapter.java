package com.example.lzw.team20.waterfallSupport;

import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.lzw.team20.R;

import java.util.List;

/**
 * Created by lzw on 2018/4/20.
 */

public class WaterfallAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private Context mContext;
    private List<Card> mData; //定义数据源
    private OnCardClickListener onCardClickListener;    //监听接口

    public static int[] avatar=new int[]{R.drawable.avatar_default,R.drawable.h001,R.drawable.h002,R.drawable.h003,
            R.drawable.h004,R.drawable.h005,R.drawable.h006};

    //定义构造方法，默认传入上下文和数据源
    public WaterfallAdapter(Context context, List<Card> data) {
        mContext = context;
        mData = data;
    }


    @Override  //将ItemView渲染进来，创建ViewHolder
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.waterfallitem, null,false);

        return new MyViewHolder(view);


    }

    @Override  //将数据源的数据绑定到相应控件上
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder holder2 = (MyViewHolder) holder;
        Card card = mData.get(position);

        if(card.avatarNo==null||card.avatarNo.isEmpty()||card.avatarNo.equals("null"))
            holder2.head.setImageResource(avatar[1]);
        else
            holder2.head.setImageResource(avatar[Integer.valueOf(card.avatarNo)]);
        holder2.cardName.setText(card.cardName);
        holder2.cardLabel.setText(card.cardLabel);
        holder2.cardLabel_2.setText(card.cardLabel_2);
        holder2.intro.setText(card.intro);
        holder2.time.setText(card.time);
        holder2.lastNum.setText(card.lastNum);

        //若lastNum=0，则将join按钮设为unable
        //if(card.lastNum.equals("0"))
            //holder2.join_button.setEnabled(false);

        //将圆桌id及圆桌名设置为当前view的tag及当前view中join按钮的tag
        holder2.itemView.setTag(R.id.tag_rtableid,card.tid);
        holder2.itemView.setTag(R.id.tag_rtablename,card.cardName);
        holder2.itemView.setTag(R.id.tag_rtablephoto,card.avatarNo);

        holder2.join_button.setTag(R.id.tag_rtableid,card.tid);
        holder2.join_button.setTag(R.id.tag_rtablename,card.cardName);
        holder2.join_button.setTag(R.id.tag_rtablephoto,card.avatarNo);



        //如果设置了回调，则进行click监听
        /*if(mOnCardClickListener!=null){
            holder2.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mOnCardClickListener.onButtonClick(v,(int)v.getTag());
                }
            });
            holder2.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mOnCardClickListener.onItemClick(v,(int)v.getTag());
                }
            });
        }*/
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    //一个卡片有两个有效点击
    public enum ClickName {
        CARD,
        JOIN_BUTTON
    }

    //定义自己的ViewHolder，将View的控件引用在成员变量上
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView head;
        public TextView cardName;
        public TextView cardLabel;
        public TextView cardLabel_2;
        public TextView intro;
        public TextView time;
        public TextView lastNum;
        public Button join_button;


        public MyViewHolder(View itemView) {
            super(itemView);
            head = (CircleImageView) itemView.findViewById(R.id.head);
            cardName = (TextView) itemView.findViewById(R.id.card_name);
            cardLabel = (TextView) itemView.findViewById(R.id.card_label);
            cardLabel_2=(TextView) itemView.findViewById(R.id.card_label_2);
            intro = (TextView) itemView.findViewById(R.id.intro);
            time = (TextView) itemView.findViewById(R.id.time);
            lastNum = (TextView) itemView.findViewById(R.id.last_num);
            join_button = (Button) itemView.findViewById(R.id.join_button);

            join_button.setOnClickListener(WaterfallAdapter.this);
            itemView.setOnClickListener(WaterfallAdapter.this);


        }
    }

    //设置监听
    public void setOnCardClickListener(OnCardClickListener mListener) {
        this.onCardClickListener = mListener;
    }

    public void onClick(View v) {
        //使用getTag获取圆桌id
        String tid =(String)v.getTag(R.id.tag_rtableid);
        String tname=(String)v.getTag(R.id.tag_rtablename);
        String tphoto=(String)v.getTag(R.id.tag_rtablephoto);
        if(onCardClickListener!=null){
            switch (v.getId()) {
                case R.id.join_button:
                    onCardClickListener.onClick(v, ClickName.JOIN_BUTTON,tid,tname,tphoto);
                    break;
                default:
                    onCardClickListener.onClick(v,ClickName.CARD,tid,tname,tphoto);
                    break;
            }
        }

    }

    //定义接口实现监听
    public interface OnCardClickListener{
        void onClick(View v, ClickName clickName, String tid,String tname,String tphoto);  //点击圆桌,需要传递圆桌ID
    }


}
