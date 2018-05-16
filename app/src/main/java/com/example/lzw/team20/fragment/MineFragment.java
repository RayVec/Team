package com.example.lzw.team20.fragment;
import com.example.lzw.team20.Picture;
import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;
import com.example.lzw.team20.activity.InfoActivity;
import com.example.lzw.team20.activity.LoginActivity;
import com.example.lzw.team20.activity.MainActivity;
import com.example.lzw.team20.activity.mineSupport.DraftsActivity;
import com.example.lzw.team20.activity.mineSupport.MyCreateTableActivity;
import com.example.lzw.team20.activity.mineSupport.MyJoinTableActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Myh on 2018/3/23.
 */

public class MineFragment extends Fragment {

    private TextView text_name;
    private TextView text_id;
    private ImageView image_head;

    private TextView infomation;
    private TextView friend;
    private TextView draft;
    private TextView table_part;
    private TextView table_create;
    private TextView about;
    private TextView signout;

    private String info;
    private JSONObject data;
    private ProgressDialog dialog;

    public static int[] avatar=new int[]{R.drawable.avatar_default,R.drawable.h001,R.drawable.h002,R.drawable.h003,
            R.drawable.h004,R.drawable.h005,R.drawable.h006};

    public MineFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_mine,container,false);
        text_name=(TextView)view.findViewById(R.id.text_name);
        text_id=(TextView)view.findViewById(R.id.text_id);
        image_head=(ImageView)view.findViewById(R.id.imageView);
        setHeader();
        infomation=(TextView)view.findViewById(R.id.info);
        setInfo();
        friend=(TextView)view.findViewById(R.id.friend);
        setFriend();
        draft=(TextView)view.findViewById(R.id.draft);
        setDraft();
        table_part=(TextView)view.findViewById(R.id.table_part);
        setTable_part();
        table_create=(TextView)view.findViewById(R.id.table_create);
        setTable_create();
        about=(TextView)view.findViewById(R.id.about);
        setAbout();
        signout=(TextView)view.findViewById(R.id.signout);
        setOut();

        return view;
    }

    public void setHeader(){
        Intent intent=getActivity().getIntent();
        try {
            data=new JSONObject(intent.getStringExtra("data"));
            String str1="用户名："+data.getString("username");
            text_name.setText(str1);
            String str2="ID："+data.getString("userid");
            text_id.setText(str2);
            String photo=data.getString("photo");
            switch (photo){
                case "0":
                    image_head.setImageResource(avatar[0]);
                    break;
                case "1":
                    image_head.setImageResource(avatar[1]);
                    break;
                case "2":
                    image_head.setImageResource(avatar[2]);
                    break;
                case "3":
                    image_head.setImageResource(avatar[3]);
                    break;
                case "4":
                    image_head.setImageResource(avatar[4]);
                    break;
                case "5":
                    image_head.setImageResource(avatar[5]);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setInfo(){
        infomation.setOnClickListener(new View.OnClickListener() {//给按钮设置单击事件监听
            @Override
            public void onClick(View v){
                Intent intent=new Intent(getActivity(), InfoActivity.class);
                intent.putExtra("data",data.toString());
                startActivity(intent);
            }
        });
    }
    public void setFriend(){
        friend.setOnClickListener(new View.OnClickListener() {//给按钮设置单击事件监听
            @Override
            public void onClick(View v){
                Toast.makeText(getContext(),"好友模块暂未上线，敬请期待",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setDraft(){
        draft.setOnClickListener(new View.OnClickListener() {//给按钮设置单击事件监听
            @Override
            public void onClick(View v){
                Intent intent=new Intent(getActivity(), DraftsActivity.class);
                intent.putExtra("data",data.toString());
                startActivity(intent);
            }
        });
    }
    public void setTable_part(){
        table_part.setOnClickListener(new View.OnClickListener() {//给按钮设置单击事件监听
            @Override
            public void onClick(View v){
                Intent intent=new Intent(getActivity(), MyJoinTableActivity.class);
                intent.putExtra("data",data.toString());
                startActivity(intent);
            }
        });
    }
    public void setTable_create(){
        table_create.setOnClickListener(new View.OnClickListener() {//给按钮设置单击事件监听
            @Override
            public void onClick(View v){
                Intent intent=new Intent(getActivity(), MyCreateTableActivity.class);
                intent.putExtra("data",data.toString());
                startActivity(intent);
            }
        });
    }
    public void setAbout(){
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("同道组温馨提示");
                builder.setMessage("有任何bug或意见请投递至背锅组长邮箱:892349323@qq.com~");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    public void setOut(){
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                new Thread(new MineFragment.OutThread()).start();

            }
        });
    }
    public class OutThread implements Runnable {
        @Override
        public void run() {

            info= WebService.executeOut();
            if(!TextUtils.isEmpty(info)){
                try {
                    JSONObject json=new JSONObject(info);
                    String result=json.getString("result");
                    if (result.equals("false")){
                        dialog.dismiss();
                        Looper.prepare();
                        Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else {
                        Intent intent=new Intent(getActivity(),LoginActivity.class);//退出后重新登录
                        startActivity(intent);
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                dialog.dismiss();
                Looper.prepare();
                Toast.makeText(getContext(), "退出失败！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }


/*
    //对头像进行处理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            bp = (Bitmap) data.getExtras().get("data");
            //将bitmap 转成byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bp.compress(Bitmap.CompressFormat.PNG,100,baos);
            bpByte=baos.toByteArray();
            //byte[]转String
            bpStr= Base64.encodeToString(bpByte,Base64.DEFAULT);

            image_head.setImageBitmap(bp);
            source = Picture.setRgb(bp);
            source.setLBP();
            sourceList = source.getHistogram();
            BigDecimal bg;
            double curr = 0.0;
            double newCurr = 0.0;

            for (int i = 0; i < sourceList.size(); i++) {
                curr = sourceList.get(i);
                bg = new BigDecimal(curr);
                newCurr = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                sourceStr += String.valueOf(newCurr) + " ";
            }
        }
    }
*/


}
