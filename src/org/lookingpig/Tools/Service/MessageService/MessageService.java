package org.lookingpig.Tools.Service.MessageService;

import org.lookingpig.Tools.Service.MessageService.Model.Message;

/**
 * 消息服务接口
 * @author Pig
 *
 */
public interface MessageService {
	
	/**
	 * 服务
	 * @param message 消息
	 * @return 服务返回信息
	 */
	public Message service(Message message);
}
