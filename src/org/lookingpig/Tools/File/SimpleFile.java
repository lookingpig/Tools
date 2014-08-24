package org.lookingpig.Tools.File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 简单文件工具
 * @author Pig
 *
 */
public class SimpleFile {
	private static final Logger logger = LogManager.getLogger(SimpleFile.class);
	
	
	/**
	 * 读取文件
	 * @param path 路径
	 * @return 文件内容
	 */
	public static String readFile(String path) {
		return readFile(new File(path));
	}
	
	/**
	 * 读取文件
	 * @param file 文件
	 * @return 文件内容
	 */
	public static String readFile(File file) {
		StringBuffer text = new StringBuffer();
		BufferedReader read = null;
		
		try {
			String line;
			read = new BufferedReader(new FileReader(file));
			
			while (null != (line = read.readLine())) {
				text.append(line);
			}
		} catch (Exception e) {
			logger.error("读取文件失败！位置：" + file.getPath() + "，原因：", e);
		} finally {
			if (null != read) {
				try {
					read.close();
					read= null;
				} catch (IOException e) {
					logger.error("关闭文件读取管道时发生异常！原因：", e);
				}
			}
		}
		
		return text.toString();
	}
}
