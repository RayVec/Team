package com.example.lzw.team20.activity.chatting;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import common.ChatMessage;
import common.ChatMessageType;
import common.User;

import android.content.Context;

public class ChatClient {
	private Context context;
	public Socket s;
	public ChatClient(Context context){
		this.context=context;
	}
	//在后台注册当前账号的线程
	public boolean sendLoginInfo(Object obj){
		boolean b=false;
		try {
			s=new Socket();
			try{
				s.connect(new InetSocketAddress("118.24.39.31",5469),2000);
			}catch(SocketTimeoutException e){
				//连接服务器超时
				return false;
			}
			ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(obj);
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			ChatMessage ms=(ChatMessage)ois.readObject();
			if(ms.getType().equals(ChatMessageType.SUCCESS)){
				//个人信息
				ChatActivity.myInfo=ms.getContent();
				//创建一个该账号和服务器保持连接的线程
				ClientConServerThread ccst=new ClientConServerThread(context,s);
				ccst.obj=obj;
				//启动该通信线程
				ccst.start();
				//加入到管理类中
				ManageClientConServer.addClientConServerThread(((User)obj).getAccount(), ccst);
				b=true;
			}else if(ms.getType().equals(ChatMessageType.FAIL)){
				b=false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return b;
	}
}
