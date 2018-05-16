package com.example.lzw.team20.activity.chatting;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyTime {
	public static String geTime(){
		Date date=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=df.format(date);
		return time;
	}

}
