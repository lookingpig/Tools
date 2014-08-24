package org.lookingpig.Tools.Service.Socket;

/**
 * 事件监听器
 * @author Pig
 *
 */
public interface EventListener {
	
	/**
	 * 当事件发生时触发
	 * @param event 事件
	 */
	public void onEvent(ClientEvent event);
}
