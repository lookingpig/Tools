package org.lookingpig.Tools.Database.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lookingpig.Tools.Database.DatabaseService;

/**
 * SQL语句模型
 * 
 * @author Pig
 * 
 */
public class SQL {
	private String key;
	private String statement;
	private List<Parameter> parameters;
	private List<String> options;

	public SQL() {
		options = null;
	}

	/**
	 * 获得索引
	 * 
	 * @return 索引
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 设置索引
	 * 
	 * @param key
	 *            索引
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获得语句
	 * 
	 * @return 语句
	 */
	public String getStatement() {
		return statement;
	}

	/**
	 * 根据传入参数获得语句
	 * 
	 * @param parameters
	 *            参数
	 * @return 语句
	 */
	public String getStatement(Map<String, String> parameters) {
		StringBuffer sql = new StringBuffer(statement);
		StringBuffer key = new StringBuffer();
		int place = -1;

		if (null != parameters && null != getParameters()) {
			
			//替换可选项
			for (Parameter p : getParameters()) {
				if (p.isOption()) {
					if (parameters.containsKey(p.getValue())) {
						key.delete(0, key.length());
						key.append(DatabaseService.SQL_PARAMETER_OPTION_START_FLAG);
						key.append(p.getOptionIndex());
						key.append(DatabaseService.SQL_PARAMETER_OPTION_END_FLAG);
						
						place = sql.indexOf(key.toString());
						sql.replace(place, place + key.length(), options.get(p.getOptionIndex()));
					}
				}
			}
		}
		
		if (null != options) {
			place = -1;
			
			//删除多余的可选项标注
			for (int i=0; i<options.size(); i++) {
				key.delete(0, key.length());
				key.append(DatabaseService.SQL_PARAMETER_OPTION_START_FLAG);
				key.append(i);
				key.append(DatabaseService.SQL_PARAMETER_OPTION_END_FLAG);
				
				if (-1 != (place = sql.indexOf(key.toString()))) {
					sql.delete(place, place + key.length());
				}
			}
		}

		return sql.toString();
	}

	/**
	 * 设置语句
	 * 
	 * @param statement
	 *            语句
	 */
	public void setStatement(String statement) {
		this.statement = statement;
	}

	/**
	 * 添加选项
	 * 
	 * @param option
	 *            选项
	 */
	public void addOption(String option) {
		if (null == options) {
			options = new ArrayList<String>();
		}

		options.add(option);
	}

	/**
	 * 获得参数列表
	 * 
	 * @return 参数列表
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}
	
	/**
	 * 获得必须参数数量
	 * @return 参数数量
	 */
	public int getMustParameterSize() {
		int size = 0;
		
		if (null != parameters) {
			for (Parameter p : parameters) {
				if (!p.isOption()) {
					size++;
				}
			}
		}
		
		return size;
	}

	/**
	 * 设置参数列表
	 * 
	 * @param parameters
	 *            参数列表
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * 获得指定位置参数
	 * 
	 * @param index
	 *            位置索引
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
	 * 
	 * @param parameter
	 *            参数
	 */
	public void addParameter(Parameter parameter) {
		if (null == parameters) {
			parameters = new ArrayList<Parameter>();
		}

		parameters.add(parameter);
	}

}
