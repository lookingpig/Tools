package org.lookingpig.Tools.Database;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.lookingpig.Tools.Database.Model.Parameter;
import org.lookingpig.Tools.Database.Model.SQL;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库服务
 * 
 * @author Pig
 * 
 */
public class DatabaseService {
	private static final String ELEMENT_PATH_SQL = "//sql";
	private static final String ELEMENT_NAME_KEY = "key";
	private static final String ELEMENT_NAME_STATEMENT = "statement";
	private static final String ELEMENT_NAME_PARAMETER = "parameter";
	private static final String ATTRIBUTE_NAME_TYPE = "type";
	private static final String ATTRIBUTE_TYPE_DEFAULT_VALUE = "string";

	private static ComboPooledDataSource ds;
	private static Logger logger;
	private static DatabaseService service;
	private Map<String, SQL> sqls;

	static {
		// 初始化数据库连接池
		ds = new ComboPooledDataSource();
		logger = LogManager.getLogger(DatabaseService.class);
	}

	private DatabaseService() {
		sqls = new HashMap<String, SQL>();
	}

	/**
	 * 获得数据服务
	 * 
	 * @return 数据服务
	 */
	public static DatabaseService getService() {
		if (null == service) {
			service = new DatabaseService();
		}

		return service;
	}

	/**
	 * 校验参数
	 * 
	 * @param key
	 *            服务索引
	 * @param parameters
	 *            参数列表
	 * @return 是否可以提供服务
	 */
	private boolean checkParameters(String key, Map<String, String> parameters) {
		SQL sql = sqls.get(key);

		if (null == sql) {
			logger.warn("服务索引未找到！key: " + key);
			return false;
		}

		int paramSize = null == parameters ? 0 : parameters.size();
		int requiredSize = null == sql.getParameters() ? 0 : sql
				.getParameters().size();

		if (requiredSize > paramSize) {
			logger.warn("提供参数与服务所需参数不符！kye: " + key);
			return false;
		}

		if (0 != requiredSize) {
			for (Parameter p : sql.getParameters()) {
				if (!parameters.keySet().contains(p.getValue())) {
					logger.warn("数据服务无法获取足够的所需参数！key: " + key);
					return false;
				}
			}
		}
		
		return true;
	}

	/**
	 * 添加参数
	 * 
	 * @param ps
	 *            要添加的语句
	 * @param requiredParams
	 *            所需参数
	 * @param params
	 *            服务参数
	 * @return 是否添加成功
	 */
	private boolean addParameter(PreparedStatement ps,
			List<Parameter> requiredParams, Map<String, String> params) {

		if (null == requiredParams || 0 == requiredParams.size()) {
			return true;
		}

		int index = 1;
		boolean success = true;
		String paramValue = null;

		try {
			for (Parameter p : requiredParams) {
				paramValue = params.get(p.getValue());

				switch (p.getType()) {
				case "string":
					ps.setString(index, paramValue);
					break;
				case "int":
					ps.setInt(index, Integer.parseInt(paramValue));
					break;
				case "bool":
					ps.setBoolean(index, Boolean.parseBoolean(paramValue));
					break;
				case "long":
					ps.setLong(index, Long.parseLong(paramValue));
					break;
				case "bigdecimal":
					ps.setBigDecimal(index, new BigDecimal(paramValue));
					break;
				case "byte":
					ps.setByte(index, Byte.parseByte(paramValue));
					break;
				case "bytes":
					ps.setBytes(index, paramValue.getBytes());
					break;
				case "double":
					ps.setDouble(index, Double.parseDouble(paramValue));
					break;
				case "float":
					ps.setFloat(index, Float.parseFloat(paramValue));
					break;
				case "short":
					ps.setShort(index, Short.parseShort(paramValue));
					break;
				}

				index++;
			}
		} catch (SQLException e) {
			success = false;
			logger.warn("添加参数失败！index: " + index);
		}

		return success;
	}

	/**
	 * 查询
	 * 
	 * @param key
	 *            服务索引
	 * @param parameters
	 *            服务参数
	 * @return 查询结果集
	 */
	public List<List<String>> query(String key, Map<String, String> parameters) {
		List<List<String>> result = new ArrayList<List<String>>();

		if (!checkParameters(key, parameters)) {
			return result;
		}

		SQL sql = sqls.get(key);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> column = null;

		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql.getStatement());

			if (!addParameter(ps, sql.getParameters(), parameters)) {
				return result;
			}

			rs = ps.executeQuery();
			rs.last();

			// 加入表头
			if (0 < rs.getRow()) {
				rs.beforeFirst();
				ResultSetMetaData rmd = rs.getMetaData();
				column = new ArrayList<String>();
	
				for (int i = 1; i <= rmd.getColumnCount(); i++) {
					column.add(rmd.getColumnLabel(i));
				}
	
				result.add(column);
			
				// 加入数据
				while (rs.next()) {
					column = new ArrayList<String>();
					
					for (String title : result.get(0)) {
						column.add(rs.getString(title));
					}
					
					result.add(column);
				}
			}
		} catch (SQLException e) {
			logger.error("数据服务查询失败！key: " + key, e);
		} catch (Exception e) {
			logger.error("数据服务查询失败！key: " + key, e);
		} finally {
			// 释放资源
			try {
				if (null != rs) {
					rs.close();
					rs = null;
				}

				if (null != ps) {
					ps.close();
					ps = null;
				}

				if (null != conn) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				logger.error("数据服务释放资源失败！", e);
			}
		}

		return result;
	}

	/**
	 * 执行
	 * 
	 * @param key
	 *            服务索引
	 * @param parameters
	 *            服务参数
	 * @param conn
	 *            数据库连接
	 * @return 是否执行成功
	 * @throws SQLException
	 *            执行异常
	 */
	private boolean execute(String key, Map<String, String> parameters,
			Connection conn) throws SQLException {
		boolean success = false;

		if (!checkParameters(key, parameters)) {
			return success;
		}

		SQL sql = sqls.get(key);

		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql.getStatement());
			
			if (!addParameter(ps, sql.getParameters(), parameters)) {
				return success;
			}

			success = ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("数据服务执行失败！key: " + key, e);
			throw e;
		} finally {
			// 释放资源
			try {
				if (null != ps) {
					ps.close();
					ps = null;
				}
			} catch (SQLException e) {
				logger.error("数据服务释放资源失败！key: " + key, e);
			}
		}

		return success;
	}

	public boolean execute(String key, Map<String, String> parameters) {
		boolean success = false;
		Connection conn = null;

		try {
			conn = getConnection();
			success = execute(key, parameters, conn);
		} catch (SQLException e) {
			logger.error("数据服务执行失败！key: " + key, e);
		} finally {
			// 释放资源
			try {
				if (null != conn) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				logger.error("数据服务释放资源失败！key: " + key, e);
			}
		}

		return success;
	}

	/**
	 * 事务执行
	 * 
	 * @param keys
	 *            服务索引列表
	 * @param parameters
	 *            服务参数列表
	 * @return 是否全部成功执行
	 */
	public boolean execute(List<String> keys,
			List<Map<String, String>> parameters) {
		boolean success = false;
		Connection conn = null;

		try {
			conn = getConnection();
			conn.setAutoCommit(false);

			for (int i = 0; i < keys.size(); i++) {
				if (!(success = execute(keys.get(i), parameters.get(i), conn))) {
					conn.rollback();
					break;
				}
			}

			if (success) {
				conn.commit();
			}
		} catch (SQLException e) {
			success = false;
			logger.error("数据服务执行失败！keys: " + keys, e);
		} finally {
			// 释放资源
			try {
				if (null != conn) {
					if (!success) {
						conn.rollback();
					}

					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				logger.error("数据服务释放资源失败！keys: " + keys, e);
			}
		}

		return success;
	}

	/**
	 * 加载数据服务配置文件
	 * 
	 * @param path
	 *            配置文件
	 */
	public void loadService(String path) {
		loadService(new File(path));
	}

	/**
	 * 加载数据服务配置文件
	 * 
	 * @param config
	 *            配置文件
	 */
	public void loadService(File config) {
		if (null == config) {
			logger.error("加载数据服务配置文件失败！没有传入配置文件。");
			return;
		}

		if (!config.exists()) {
			logger.error("加载数据服务配置文件失败！配置文件不存在。");
			return;
		}

		SAXReader reader = new SAXReader();
		Document doc = null;

		try {
			doc = reader.read(config);
		} catch (DocumentException e) {
			logger.error("无法解析数据服务配置文件！path: " + config.getPath(), e);
			return;
		}

		sqls.clear();
		// 遍历每一个服务配置项
		List<?> list = doc.selectNodes(ELEMENT_PATH_SQL);
		List<?> parameters;
		Element sqlElement;
		Element e;
		String key, statement, type;
		SQL sql;
		int index = 0;

		for (Object o : list) {
			sqlElement = (Element) o;
			e = sqlElement.element(ELEMENT_NAME_KEY);

			if (null == e) {
				logger.warn("数据服务配置缺少key！index: " + index);
				return;
			}

			key = e.getText();
			e = sqlElement.element(ELEMENT_NAME_STATEMENT);

			if (null == e) {
				logger.warn("数据服务配置缺少statement！index: " + index);
			}

			statement = e.getText();
			sql = new SQL();
			sql.setKey(key);
			sql.setStatement(statement);

			// 遍历参数列表
			parameters = sqlElement.elements(ELEMENT_NAME_PARAMETER);

			for (Object parameter : parameters) {
				e = (Element) parameter;
				type = e.attributeValue(ATTRIBUTE_NAME_TYPE);

				// type默认为string型
				if (null == type || "".equals(type)) {
					type = ATTRIBUTE_TYPE_DEFAULT_VALUE;
				} else {
					type = type.toLowerCase();
				}

				sql.addParameter(new Parameter(type, e.getText()));
			}

			sqls.put(key, sql);
			index++;
		}
	}

	/**
	 * 获得数据库连接
	 * 
	 * @return 数据库连接
	 * @throws SQLException
	 *             从数据库连接池获取连接失败
	 */
	public Connection getConnection() throws SQLException {
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			logger.error("从数据库连接池获得连接失败！", e);
			throw e;
		}
	}

	/**
	 * 获得指定SQL语句
	 * 
	 * @param kye
	 *            索引
	 * @return SQL语句
	 */
	public SQL getSQL(String kye) {
		return sqls.get(kye);
	}

	/**
	 * 获得全部SQL语句
	 * 
	 * @return SQL语句
	 */
	public Map<String, SQL> getSQLs() {
		return sqls;
	}
}
