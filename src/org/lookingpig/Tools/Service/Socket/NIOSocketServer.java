package org.lookingpig.Tools.Service.Socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 无阻塞Socket服务器
 * @author Pig
 *
 */
public class NIOSocketServer {
	private static final Logger logger = LogManager.getLogger(NIOSocketServer.class);
	private ServerSocketChannel server;
	private Selector selector;
	private List<SocketChannel> clients;
	private ByteBuffer buffer;
	private Charset charset;
	private SelectorListener listener;
	private Thread lisnThr;
	
	public NIOSocketServer(String charset, int bufSize) {
		clients = new ArrayList<SocketChannel>();
		buffer = ByteBuffer.allocate(bufSize);
		this.charset = Charset.forName(charset);
		listener = new SelectorListener();
	}
	
	/**
	 * 启动服务
	 * @param port 端口
	 * @return 启动是否成功
	 */
	public boolean start(int port) {
		shutdown();
		boolean start = true;
		
		try {
			selector = Selector.open();
			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.socket().bind(new InetSocketAddress(port));
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
			try {
				for (SocketChannel client : clients) {
						client.close();
				}
			} catch (IOException e) {
				logger.error("断开客户端连接时发生异常！原因：", e);
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
		ByteBuffer bb = ByteBuffer.wrap(message.getBytes());
		
		try {
			for (SocketChannel client: clients) {
				client.write(bb);
			}
		} catch (IOException e) {
			logger.error("向所有客户端发送消息时发生异常！原因：", e);
		}
	}
	
	/**
	 * 当有客户端请求连接时调用
	 */
	private void onConnection() {
		try {
			SocketChannel client = server.accept();
			logger.info("一个客户端连接已经建立，地址：" + client.socket());
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
			clients.add(client);
		} catch (Exception e) {
			logger.error("与客户端建立连接失败！原因：", e);
		}
	}
	
	/**
	 * 当接收到来自客户端消息时调用
	 */
	private void onMessage(SocketChannel client) {
		try {
			CharBuffer cb;
			StringBuffer sb = new StringBuffer();
			
			if (0 < client.read(buffer)) {
				cb = charset.decode((ByteBuffer) buffer.flip());
				sb.append(cb.toString());
				cb.clear();
				buffer.clear();
				onMessageComplete(sb.toString());
			} else {
				logger.info("与客户端连接已断开。客户端：" + client.socket());
				clients.remove(client);
				client.close();
			}
		} catch (IOException e) {
			logger.error("接收客户端消息失败！原因：", e);
		}
	}
	
	/**
	 * 接收到一条完整的消息
	 * @param message 消息
	 */
	private void onMessageComplete(String message) {
		logger.info("接收到一条来自客户端的消息，内容：" + message);
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
						
						if (!key.isValid()) {
							System.out.println("关闭");
						}else if (key.isAcceptable()) {
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
