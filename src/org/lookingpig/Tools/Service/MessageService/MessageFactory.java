package org.lookingpig.Tools.Service.MessageService;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.lookingpig.Tools.Service.MessageService.Model.Message;


/**
 * 消息工厂
 * @author Pig
 *
 */
public class MessageFactory {
	private static Logger logger = LogManager.getLogger(MessageFactory.class); 
	private static MessageFactory factory;
	private static final String ELEMENT_NAME_MESSAGE = "message";
	private static final String ELEMENT_NAME_SENDER = "sender";
	private static final String ELEMENT_NAME_SENDENUMBER = "send-number";
	private static final String ELEMENT_NAME_SENDETIME = "send-time";
	private static final String ELEMENT_NAME_CONTENTS = "contents";
	
	private MessageFactory() {
	}
	
	public static MessageFactory getFactory() {
		if (null == factory) {
			factory = new MessageFactory();
		}
		
		return factory;
	}
	
	/**
	 * 解析消息
	 * @param message 消息数据
	 * @return 消息对象
	 */
	public Message resolve(String message) {
		Message m = null;
		
		try {
			Document doc = DocumentHelper.parseText(message);
			Element root = doc.getRootElement();
			m = new Message();

			//取得消息信息
			m.setSender(root.element(ELEMENT_NAME_SENDER).getText());
			m.setSendNumber(root.element(ELEMENT_NAME_SENDENUMBER).getText());
			m.setSendTime(root.element(ELEMENT_NAME_SENDETIME).getText());
			
			//取得消息所有内容
			Iterator<?> i = root.element(ELEMENT_NAME_CONTENTS).elementIterator();
			Element e;
			
			while (i.hasNext()) {
				e = (Element) i.next();
				m.addContent(e.getName(), e.getText());
			}
		} catch (DocumentException e) {
			logger.error("消息解析为xml失败，message: " + message, e);
		}
		
		return m;
	}
	
	/**
	 * 封装消息
	 * @param message 消息对象
	 * @return 消息数据
	 */
	public String blend(Message message) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(ELEMENT_NAME_MESSAGE);
		
		//写入消息信息
		root.addElement(ELEMENT_NAME_SENDER).setText(message.getSender());
		root.addElement(ELEMENT_NAME_SENDENUMBER).setText(message.getSendNumber());
		root.addElement(ELEMENT_NAME_SENDETIME).setText(message.getSendTime());
		
		//写入消息内容
		Element contents = root.addElement(ELEMENT_NAME_CONTENTS);
		
		for (String key : message.getContents().keySet()) {
			contents.addElement(key).setText(message.getContent(key));
		}
		
		return doc.asXML();
	}
}
