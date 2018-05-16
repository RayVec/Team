package com.example.lzw.team20.activity.mineSupport;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lzw.team20.R;

import java.util.List;

public class MyDraftAdapter extends BaseAdapter{
    private List<Draft> mData;
    private Context context;

    public void setmData(List<Draft> drafts){
        this.mData=drafts;
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
        View view=View.inflate(context, R.layout.drafts_item,null);
        Draft draft=mData.get(position);

        TextView draftname=view.findViewById(R.id.draft_name);
        draftname.setText(draft.secondTag);

        return view;

    }

}
