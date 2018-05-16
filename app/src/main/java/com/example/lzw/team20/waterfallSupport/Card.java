package com.example.lzw.team20.waterfallSupport;
import java.io.Serializable;
/**
 * Created by lzw on 2018/4/20.
 */

public class Card implements Serializable{
    public String tid;                  //圆桌id
    public String cardName;             //圆桌名称
    public String cardLabel;            //圆桌类型
    public String cardLabel_2;          //圆桌二级标签
    public String avatarNo;              //头像URL？？？？？？？？？？
    public String intro;                //圆桌简介
    public String time;                 //圆桌起止时间
    public String lastNum;              //剩余席位
}