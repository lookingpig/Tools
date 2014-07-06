package org.lookingpig.Tools.Service.MessageService.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息模型
 * 
 * @author Pig
 * 
 */
public class Message {
	private String sender;
	private String sendNumber;
	private String sendTime;
	private Map<String, String> contents;
	private Object caller;
	private Object session;

	/**
	 * 获得会话
	 * @return 会话
	 */
	public Object getSession() {
		return session;
	}

	/**
	 * 设置会话
	 * @param session 会话
	 */
	public void setSession(Object session) {
		this.session = session;
	}

	/**
	 * 获得发送者
	 * 
	 * @return 发送者
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * 设置发送者
	 * 
	 * @param sender
	 *            发送者
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * 获得发送者号码
	 * 
	 * @return 发送者号码
	 */
	public String getSendNumber() {
		return sendNumber;
	}

	/**
	 * 设置发送者号码
	 * 
	 * @param sendNumber
	 *            发送者号码
	 */
	public void setSendNumber(String sendNumber) {
		this.sendNumber = sendNumber;
	}

	/**
	 * 获得发送时间
	 * 
	 * @return 发送时间
	 */
	public String getSendTime() {
		return sendTime;
	}

	/**
	 * 设置发送时间
	 * 
	 * @param sendTime
	 *            发送时间
	 */
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	/**
	 * 获得全部内容
	 * 
	 * @return 全部内容
	 */
	public Map<String, String> getContents() {
		return contents;
	}

	/**
	 * 设置全部内容
	 * 
	 * @param contents
	 *            全部内容
	 */
	public void setContents(Map<String, String> contents) {
		this.contents = contents;
	}

	/**
	 * 获得制定内容
	 * 
	 * @param key
	 *            索引
	 * @return 指定内容
	 */
	public String getContent(String key) {
		return this.contents.get(key);
	}

	/**
	 * 增加内容
	 * 
	 * @param key
	 *            索引
	 * @param content
	 *            内容
	 */
	public void addContent(String key, String content) {
		if (null == this.contents) {
			contents = new HashMap<String, String>();
		}

		this.contents.put(key, content);
	}
	
	/**
	 * 获得调用者
	 * @return 调用者
	 */
	public Object getCaller() {
		return caller;
	}

	/**
	 * 设置调用者
	 * @param caller 调用者
	 */
	public void setCaller(Object caller) {
		this.caller = caller;
	}

	@Override
	public String toString() {
		return "{sender: " + sender + ", sendNumber: " + sendNumber + ", sendTime: " + sendTime + ", contents: "
				+ contents + "}";
	}
}
