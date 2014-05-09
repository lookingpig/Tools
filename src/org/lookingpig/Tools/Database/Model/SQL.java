package org.lookingpig.Tools.Database.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL语句模型
 * @author Pig
 *
 */
public class SQL {
	private String key;
	private String statement;
	private List<Parameter> parameters;
	
	/**
	 * 获得索引
	 * @return 索引
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * 设置索引
	 * @param key 索引
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * 获得语句
	 * @return 语句
	 */
	public String getStatement() {
		return statement;
	}
	
	/**
	 * 设置语句
	 * @param statement 语句
	 */
	public void setStatement(String statement) {
		this.statement = statement;
	}
	
	/**
	 * 获得参数列表
	 * @return 参数列表
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}
	
	/**
	 * 设置参数列表
	 * @param parameters 参数列表
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * 获得指定位置参数
	 * @param index 位置索引
	 * @return 参数
	 */
	public Parameter getParameter(int index) {
		if (null != parameters) {
			return parameters.get(index);
		} else {
			return null;
		}
	}
	
	/**
	 * 追加一个参数
	 * @param parameter 参数
	 */
	public void addParameter(Parameter parameter) {
		if (null == parameters) {
			parameters = new ArrayList<Parameter>();
		}
		
		parameters.add(parameter);
	}
}
