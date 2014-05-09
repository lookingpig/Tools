package org.lookingpig.Tools.Database;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lookingpig.Tools.Database.Model.SQL;

public class DatabaseServiceTest {

	@BeforeClass
	public static void testLoadServiceString() {
		String path = DatabaseServiceTest.class.getResource("/").getPath() + "databaseservice_config.xml";
		DatabaseService.getService().loadService(path);
		Map<String, SQL> sqls = DatabaseService.getService().getSQLs();
		assertTrue(sqls.size() > 0);
		
		for (String key : sqls.keySet()) {
			System.out.println("key: " + key);
			System.out.println("statement: " + sqls.get(key).getStatement());
			System.out.println("parameters: " + sqls.get(key).getParameters());
		}
		
		System.out.println();
	}

	@Test
	public void testGetConnection() {
		try {
			Connection coon = DatabaseService.getService().getConnection();
			assertNotNull(coon);
			
			Statement stat = coon.createStatement();
			assertNotNull(stat);
			
			ResultSet rs = stat.executeQuery("select * from test");
			assertNotNull(rs);
			
			while (rs.next()) {
				System.out.println(rs.getString(1) + ", " + rs.getString(2));
			}
			
			rs.close();
			stat.close();
			coon.close();
		} catch (SQLException e) {
			e.printStackTrace();
			fail("获取数据库连接失败！");
		}
		
		System.out.println();
	}
	
	@Test
	public void testQuery() {
		List<List<String>> result = DatabaseService.getService().query("testquery", null);
		assertTrue(0 < result.size());
		
		for (List<String> row : result) {
			System.out.println(row);
		}
		
		System.out.println();
	}
	
	@Test
	public void testExecute() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", "gggg");
		boolean success = DatabaseService.getService().execute("testexecute", parameters);
		assertTrue(success);
	}
	
	@Test
	public void testExecuteList() {
		List<String> keys = new ArrayList<String>();
		List<Map<String, String>> parameters = new ArrayList<Map<String, String>>();
		
		for (int i=0; i<5; i++) {
			keys.add("testexecute");
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", "test" + i);
			parameters.add(map);
		}
		
		boolean success = DatabaseService.getService().execute(keys, parameters);
		assertTrue(success);
	}
}
