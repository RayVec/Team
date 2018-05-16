package com.example.lzw.team20.activity.chatting;

public class RecentEntity {
	private int avatar;
	private String account;
	private String nick;
	private String content;
	private String time;
	private int unRead;
	private boolean isHost;

	public RecentEntity(int avatar,String account,String nick,String content,String time,int unRead){
		this.avatar=avatar;
		this.account=account;
		this.nick=nick;
		this.content=content;
		this.time=time;
		this.unRead=unRead;
	}
	public RecentEntity(){
	}

	public int getAvatar() {
		return avatar;
	}

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
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

	public int getUnRead(){return unRead;}

	public void setUnRead(int unRead){this.unRead=unRead;}
}
