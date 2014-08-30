package org.lookingpig.Tools.Service.Socket;


/**
 * 事件
 * @author Pig
 *
 */
public class ClientEvent {
	private NIOSocketServer.Client client;
	private int type;
	private String message;
	
	public ClientEvent() {
		
	}
	
	/**
	 * 获得触发事件的客户端
	 * @return 客户端
	 */
	public NIOSocketServer.Client getClient() {
		return client;
	}
	
	/**
	 * 设置触发事件的客户端
	 * @param client 客户端
	 */
	public void setClient(NIOSocketServer.Client client) {
		this.client = client;
	}
	
	/**
	 * 获得事件类型
	 * @return 类型
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * 设置事件类型
	 * @param type 类型
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * 获得客户端消息
	 * @return 客户端消息
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * 设置客户端消息
	 * @param message 客户端消息
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
