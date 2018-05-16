package com.example.lzw.team20.activity.chatting;

public class ChatEntity {
	private int avatar;
	private String content;
	private String nick;
	private String time;
	private int type;//0左，1中，2右

	public ChatEntity(int avatar,String content,String time,int type,String nick){
		this.avatar=avatar;
		this.content = content;
		this.time = time;
		this.type=type;
		this.nick=nick;
	}
	public ChatEntity(String content,int type){
		this.content=content;
		this.type=type;
	}
	public int getAvatar() {
		return avatar;
	}
	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}
}
