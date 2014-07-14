package org.lookingpig.Tools.Database.Model;

/**
 * SQL语句参数模型
 * 
 * @author Pig
 * 
 */
public class Parameter {
	private String type;
	private String value;
	private boolean option;
	private int optionIndex;

	public Parameter() {

	}

	public Parameter(String type, String value) {
		this.type = type;
		this.value = value;
		this.option = false;
	}

	public Parameter(String type, String value, boolean option) {
		this.type = type;
		this.value = value;
		this.option = option;
	}

	/**
	 * 获得参数类型
	 * 
	 * @return 参数类型
	 */
	public String getType() {
		return type;
	}

	/**
	 * 设置参数类型
	 * 
	 * @param type
	 *            参数类型
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获得参数值
	 * 
	 * @return 参数值
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 设置参数值
	 * 
	 * @param value
	 *            参数值
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 获得选项索引
	 * 
	 * @return 索引
	 */
	public int getOptionIndex() {
		return optionIndex;
	}

	/**
	 * 设置选项索引
	 * 
	 * @param optionIndex
	 *            索引
	 */
	public void setOptionIndex(int optionIndex) {
		this.optionIndex = optionIndex;
	}

	/**
	 * 是否是可选项
	 * 
	 * @return 可选项
	 */
	public boolean isOption() {
		return option;
	}

	/**
	 * 设置是否为可选项
	 * 
	 * @param option
	 *            可选项
	 */
	public void setOption(boolean option) {
		this.option = option;
	}

	@Override
	public String toString() {
		return "type: " + type + ", value: " + value;
	}
}
