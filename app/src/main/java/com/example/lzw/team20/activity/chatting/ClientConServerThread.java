/**
 * 客户端和服务器端保持通信的线程
 * 不断地读取服务器发来的数据
 */
package com.example.lzw.team20.activity.chatting;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import common.ChatMessage;
import common.ChatMessageType;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ClientConServerThread extends Thread {
	private Context context;
	private  Socket s;
	public Object obj;
	public Socket getS() {return s;}
	public ClientConServerThread(Context context,Socket s){
		this.context=context;
		this.s=s;
	}

	@Override
	public void run() {
		while(true){
			ObjectInputStream ois = null;
			ChatMessage m;
			try {
				ois = new ObjectInputStream(s.getInputStream());
				m=(ChatMessage) ois.readObject();
				//把从服务器获得的消息通过广播发送
				//广播内容依次为发送者，发送者昵称，发送者头像，内容，时间
				Intent intent = new Intent("org.yhn.yq.mes");
				String[] message=new String[]{
						m.getType(),
						m.getDesk()+"",
						m.getSenderNick(),
						m.getSenderAvatar()+"",
						m.getContent(),
						m.getSendTime(),
				};
				intent.putExtra("message", message);
				context.sendBroadcast(intent);
			}
			catch (Exception e) {
				while(true) {
					try {
						s=new Socket();
						s.connect(new InetSocketAddress("118.24.39.31", 5469), 5000);
					} catch (SocketTimeoutException s) {
						Log.d("reconnect","fail");
					}
					catch (IOException e1){
						Log.d("reconnect","fail");
					}
					try {
						ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
						oos.writeObject(obj);
						ObjectInputStream ois1 = new ObjectInputStream(s.getInputStream());
						ChatMessage ms = (ChatMessage) ois1.readObject();
						if (ms.getType().equals(ChatMessageType.SUCCESS)) {
							Log.d("reconnect","success");
							break;
						} else if (ms.getType().equals(ChatMessageType.FAIL)) {
							Log.d("reconnect","fail");
						}
					} catch (IOException e1) {
						Log.d("reconnect","fail");
					} catch (ClassNotFoundException e1) {
						Log.d("reconnect","fail");
					}
				}
			}
		}
	}

}
