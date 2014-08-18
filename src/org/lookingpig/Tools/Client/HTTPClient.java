package org.lookingpig.Tools.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTTP客户端
 * @author Pig
 *
 */
public class HTTPClient {
	private static final Logger logger = LogManager.getLogger(HTTPClient.class);
	
	/**
	 * 发送GET请求
	 * @param url 服务器地址
	 * @param params 参数
	 * @return 返回信息
	 */
	public static String sendGet(String url, Map<String, String> params) {
		StringBuffer paramsStr = new StringBuffer();
		
		for (String key : params.keySet()) {
			paramsStr.append(key).append("=").append(params.get(key)).append("&");
			
		}
		
		return sendGet(url, paramsStr.toString());
	}
	
	/**
	 * 发送GET请求
	 * @param url 服务器地址
	 * @param params 参数
	 * @return 返回信息
	 */
	public static String sendGet(String url, String params) {
		StringBuffer response = new StringBuffer();
		BufferedReader read = null;
		
		try {
			URL realUrl = new URL(url + "?" + params);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.connect();
			
			String line = null;
			read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while (null != (line = read.readLine())) {
				response.append(line);
			}
		} catch (Exception e) {
			logger.error("在向：" + url + " 服务器发送GET请求时发生了异常！params: " + params + "，原因：", e);
		} finally {
			try {
				if (null != read) {
					read.close();
					read = null;
				}
			} catch (IOException e) {
				logger.error("在断开HTTP读/写是发生了异常！原因：", e);
			}
		}
		
		return response.toString();
	}
	
	/**
	 * 发送POST请求
	 * @param url 服务器地址
	 * @param message 消息
	 * @return 返回信息
	 */
	public static String sendPost(String url, String message) {
		StringBuffer response = new StringBuffer();
		PrintWriter write = null;
		BufferedReader read = null;
		
		try {
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
			
            write = new PrintWriter(conn.getOutputStream());
            write.print(message);
            write.flush();
            
            String line = null;
            read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while (null != (line = read.readLine())) {
            	response.append(line);
            }	
		} catch (Exception e) {
			logger.error("在向：" + url + " 服务器发送POST请求时发生了异常！message: " + message + "，原因：", e);
		} finally {
			try {
				if (null != write) {
					write.close();
					write = null;
				}
				if (null != read) {
					read.close();
					read = null;
				}
			} catch (IOException e) {
				logger.error("在断开HTTP读/写是发生了异常！原因：", e);
			}
		}
		
		return response.toString();
	}
}
