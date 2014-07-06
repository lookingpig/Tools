package org.lookingpig.Tools.Service.MessageService;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 消息服务工厂
 * 
 * @author Pig
 * 
 */
public class MessageServiceFactory {
	private static Logger logger = LogManager.getLogger(MessageServiceFactory.class);
	private static MessageServiceFactory factory;
	private static final String ELEMENT_PATH_SERVICE = "//service";
	private static final String ELEMENT_PATH_INCLUDE = "//include";
	private static final String ELEMENT_NAME_CLASS = "class";
	private static final String ELEMENT_NAME_KEY = "key";

	private Map<String, MessageService> services;

	private MessageServiceFactory() {
		services = new HashMap<String, MessageService>();
	}

	/**
	 * 获得消息服务工厂
	 * 
	 * @return 消息服务工厂
	 */
	public static MessageServiceFactory getFactory() {
		if (null == factory) {
			factory = new MessageServiceFactory();
		}

		return factory;
	}

	/**
	 * 获得消息服务
	 * 
	 * @param key
	 *            索引
	 * @return 消息服务
	 */
	public MessageService getService(String key) {
		return services.get(key);
	}

	/**
	 * 加载消息服务配置文件
	 * 
	 * @param path
	 *            配置文件路径
	 */
	public void loadServices(String path) {
		loadServices(new File(MessageServiceFactory.class.getClassLoader().getResource("/").getPath() + path));
	}

	/**
	 * 加载消息服务配置文件
	 * 
	 * @param config
	 *            配置文件
	 */
	public void loadServices(File config) {
		logger.info("-开始加载消息服务配置文件。");

		if (null == config) {
			logger.error("加载消息服务配置文件失败！没有传入配置文件。");
			return;
		}

		if (!config.exists()) {
			logger.error("加载消息服务配置文件失败！配置文件不存在，路径：" + config.getPath());
			return;
		}

		SAXReader reader = new SAXReader();
		Document doc = null;

		try {
			doc = reader.read(config);
		} catch (DocumentException e) {
			logger.error("无法解析消息服务配置文件！path : " + config.getPath(), e);
			return;
		}

		// 遍历每一个服务配置项目
		List<?> list = doc.selectNodes(ELEMENT_PATH_SERVICE);
		Element serviceElement;
		Element e;
		String path;
		String key;
		Object service;
		int index = 0;

		try {
			for (Object o : list) {
				serviceElement = (Element) o;
				e = serviceElement.element(ELEMENT_NAME_CLASS);

				if (null == e) {
					logger.warn("消息服务配置缺少class！index：" + index);
					continue;
				}

				path = e.getText();
				e = serviceElement.element(ELEMENT_NAME_KEY);

				if (null == e) {
					logger.warn("消息服务配置缺少key！index：" + index);
					continue;
				}

				key = e.getText();
				service = Class.forName(path).newInstance();

				if (!(service instanceof MessageService)) {
					logger.warn("消息服务没有实现服务接口！index: " + index);
					continue;
				}

				services.put(key, (MessageService) service);
				index++;
			}
		} catch (InstantiationException e1) {
			logger.error("消息服务实例化失败！index: " + index, e1);
			return;
		} catch (IllegalAccessException e1) {
			logger.error("消息服务缺少公共构造器！index: " + index, e1);
			return;
		} catch (ClassNotFoundException e1) {
			logger.error("消息服务没有找到！index: " + index, e1);
			return;
		}

		// 遍历其他包含文件
		list = doc.selectNodes(ELEMENT_PATH_INCLUDE);

		for (Object o : list) {
			e = (Element) o;
			loadServices(e.getText());
		}

		logger.info("-加载消息服务配置文件完成。");
	}
	
	/**
	 * 清楚服务缓存
	 */
	public void clearServiceCatch() {
		services.clear();
	}
}
