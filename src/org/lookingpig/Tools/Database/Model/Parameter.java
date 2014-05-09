package org.lookingpig.Tools.Database.Model;

/**
 * SQL语句参数模型
 * @author Pig
 *
 */
public class Parameter {
	private String type;
	private String value;
	
	public Parameter() {
		
	}
	
	public Parameter(String type, String value) {
		this.type = type;
		this.value = value;
	}
	
	/**
	 * 获得参数类型
	 * @return 参数类型
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * 设置参数类型
	 * @param type 参数类型
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * 获得参数值
	 * @return 参数值
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * 设置参数值
	 * @param value 参数值
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "type: " + type + ", value: " + value;
	}
}
