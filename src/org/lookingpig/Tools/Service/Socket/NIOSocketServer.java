package org.lookingpig.Tools.Service.Socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 无阻塞Socket服务器
 * @author Pig
 *
 */
public class NIOSocketServer {
	/**
	 * 客户端事件-连接
	 */
	public static final int CLIENTEVENT_CONNECT = 1;
	/**
	 * 客户端事件-收信
	 */
	public static final int CLIENTEVENT_RECEIVE = 2;
	
	/**
	 * 接收缓冲区大小
	 */
	private static final int MESSAGE_RCVBUF_size = 32 * 1024;
	
	/**
	 * 消息结束标识
	 */
	private static final String MESSAGE_END_FLAG = "@EOM"; 
	
	private static final Logger logger = LogManager.getLogger(NIOSocketServer.class);
	private ServerSocketChannel server;
	private Selector selector;
	private List<Client> clients;
	private int bufSize;
	private ByteBuffer buffer;
	private Charset charset;
	private SelectorListener listener;
	private Thread lisnThr;
	private Map<Integer, List<EventListener>> eventListeners;
	
	public NIOSocketServer(String charset, int bufSize) {
		clients = new ArrayList<Client>();
		eventListeners = new HashMap<Integer, List<EventListener>>();
		this.bufSize = bufSize;
		buffer = ByteBuffer.allocate(this.bufSize);
		this.charset = Charset.forName(charset);
		listener = new SelectorListener();
	}
	
	/**
	 * 启动服务
	 * @param port 端口
	 * @return 启动是否成功
	 */
	public boolean start(String host, int port) {
		shutdown();
		boolean start = true;
		
		try {
			selector = Selector.open();
			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.setOption(StandardSocketOptions.SO_RCVBUF, MESSAGE_RCVBUF_size);
			server.socket().bind(new InetSocketAddress(host, port));
			server.register(selector, SelectionKey.OP_ACCEPT);
			lisnThr = new Thread(listener);
			lisnThr.start();
		} catch (Exception e) {
			logger.error("启动无阻塞socket服务发生异常！port: " + port + "原因：", e);
			start = false;
		}
		
		logger.info("成功启动Socket服务，端口：" + port);
		return start;
	}
	
	/**
	 * 关闭服务
	 */
	public void shutdown() {
		if (null != listener) {
			listener.stopListen();
		}
		
		if (null != lisnThr) {
			lisnThr.interrupt();
			lisnThr = null;
		}
		
		if (null != selector) {
			try {
				selector.close();
			} catch (IOException e) {
				logger.error("关闭选择器失败，原因：", e);
			}
			selector = null;
		}
		
		if (0 < clients.size()) {
			for (Client client : clients) {
					client.close();
			}
			clients.clear();
		}
		
		if (null != server) {
			try {
				server.close();
			} catch (IOException e) {
				logger.error("关闭无阻塞socket服务发生异常！原因：", e);
			}
			server = null;
		}
		
		buffer.clear();
		logger.info("成功关闭Socket服务。");
	}
		
	/**
	 * 向所有客户端发送消息
	 * @param message 消息
	 */
	public void sendMessageToAll(String message) {
		for (Client client: clients) {
			client.sendMessage(message);
		}
	}
	
	/**
	 * 注册事件
	 * @param type 事件类型
	 * @param listener 监听器
	 */
	public void register(int type, EventListener listener) {
		List<EventListener> listeners = eventListeners.get(type);
		
		if (null == listeners) {
			listeners = new ArrayList<EventListener>();
			eventListeners.put(type, listeners);
		}
		
		listeners.add(listener);
	}
	
	/**
	 * 获得已连接客户端列表
	 * @return 客户端列表
	 */
	public List<Client> getClients() {
		return clients;
	}
	
	/**
	 * 当有客户端请求连接时调用
	 */
	private void onConnection() {
		try {
			SocketChannel channel = server.accept();
			logger.info("一个客户端连接已经建立，地址：" + channel.socket());
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);
			Client client = new Client(channel);
			clients.add(client);
			
			//触发连接事件
			ClientEvent event = new ClientEvent();
			event.setType(CLIENTEVENT_CONNECT);
			event.setClient(client);
			onEvent(event);
		} catch (Exception e) {
			logger.error("与客户端建立连接失败！原因：", e);
		}
	}
	
	/**
	 * 当接收到来自客户端消息时调用
	 */
	private void onMessage(SocketChannel channel) {
		Client client = getClient(channel);
		
		try {
			CharBuffer cb;
			StringBuffer buf = client.getMessageBuf();
			int size = 0;
			
			size = channel.read(buffer);
			
			if (0 < size) {
				cb = charset.decode((ByteBuffer) buffer.flip());
				buf.append(cb.toString());
				cb.clear();
				buffer.clear();
				logger.info("接收到客户端报文，大小：" + size + "，内容：" + cb);
				
				if (buf.toString().endsWith(MESSAGE_END_FLAG)) {
					buf.delete(buf.lastIndexOf(MESSAGE_END_FLAG), buf.length());
					//触发接收消息事件
					ClientEvent event = new ClientEvent();
					event.setType(CLIENTEVENT_RECEIVE);
					event.setClient(client);
					event.setMessage(buf.toString().replaceAll("\r\n", "").replaceAll("\n", ""));
					onEvent(event); 
					buf.delete(0, buf.length());
				}
			} else {
				logger.info("与客户端连接已断开。客户端：" + channel.socket());
				clients.remove(client);
				client.close();
				return;
			}
		} catch (IOException e) {
			logger.error("接收客户端消息失败！原因：", e);
			
			if (null != client) {
				client.close();
				clients.remove(client);				
			}
		}
	}

	/**
	 * 当客户端触发事件时调用
	 * @param event 客户端事件
	 */
	private void onEvent(ClientEvent event) {
		List<EventListener> listeners = eventListeners.get(event.getType());
		
		if (null != listeners) {
			for (EventListener listener : listeners) {
				listener.onEvent(event);
			}
		}
	}
	
	/**
	 * 获取客户端对象
	 * @param channel 客户端连接
	 * @return 客户端对象
	 */
	private Client getClient(SocketChannel channel) {
		for (Client client : clients) {
			if (client.equals(channel)) {
				return client;
			}
		}
		
		return null;
	}
	
	/**
	 * 客户端
	 * @author Pig
	 *
	 */
	public class Client {
		private SocketChannel client;
		private StringBuffer messageBuf;
		private Map<String, String> attribute;
		
		public Client(SocketChannel client) {
			this.client = client;
			messageBuf = new StringBuffer();
			attribute = new HashMap<String, String>();
		}
		
		/**
		 * 向客户端发送消息
		 * @param client 客户端
		 * @param message 消息
		 */
		public void sendMessage(String message) {
			message += MESSAGE_END_FLAG;
			ByteBuffer bb = ByteBuffer.wrap(message.getBytes());
			
			try {
				client.write(bb);
			} catch (IOException e) {
				logger.error("向客户端发送消息发生异常！地址：" + client.socket() + "，原因：", e);
			}
		}
		
		/**
		 * 断开与客户端的连接
		 */
		public void close() {
			try {
				client.close();
			} catch (IOException e) {
				logger.error("断开客户端连接时发生异常！原因：", e);
			}
		}
		
		/**
		 * 获得获得客户端通讯对象
		 * @return 通讯对象
		 */
		public SocketChannel getChannel() {
			return client;
		}
		
		/**
		 * 设置客户端属性
		 * @param key 键
		 * @param value 值
		 */
		public void setAttribute(String key, String value) {
			attribute.put(key, value);
		}
		
		/**
		 * 获得客户端属性
		 * @param key 键
		 * @return
		 */
		public String getAttribute(String key) {
			return attribute.get(key);
		}
		
		/**
		 * 获得消息缓冲区
		 * @return 缓冲区
		 */
		public StringBuffer getMessageBuf() {
			return messageBuf;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (null == obj) {
				return false;
			} else if (obj instanceof Client) {
				return compareClient(this.client, ((Client) obj).getChannel());
			} else if (obj instanceof SocketChannel) {
				return compareClient(this.client, (SocketChannel) obj);
			} else {
				return false;
			}
		}
		
		/**
		 * 比较客户端
		 * @param c1 客户端1
		 * @param c2 客户端2
		 * @return 是否相同
		 */
		private boolean compareClient(SocketChannel c1, SocketChannel c2) {
			if (c1.socket().getPort() == c2.socket().getPort()
					&& c1.socket().getInetAddress().getHostAddress()
							.equals(c2.socket().getInetAddress().getHostAddress())) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 选择器监听器
	 * @author Pig
	 *
	 */
	private class SelectorListener implements Runnable {
		private boolean stop;
		
		public SelectorListener() {
			stop = false;
		}
		
		@Override
		public void run() {
			try {
				Iterator<SelectionKey> i;
				stop = false;
				
				while (!stop) {
					selector.select();
					i = selector.selectedKeys().iterator();
					
					while (i.hasNext()) {
						SelectionKey key = i.next();
						i.remove();
						
						if (key.isAcceptable()) {
							onConnection();
						} else if (key.isReadable()) {
							onMessage((SocketChannel) key.channel());
						}
					}
				}
			} catch (IOException e) {
				logger.error("客户端事件监听器已停止工作！原因：", e);
			}
		}
		
		public void stopListen() {
			stop = true;
		}
	}
}
