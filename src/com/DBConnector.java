package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnector {
	private static final String URL = "jdbc:mysql://10.0.0.85:3306/news_data";
	private static final String USR = "web_dev";
	private static final String PWD = "3ced9208632ab2a45ea9cc0040098269";

	Connection conn = null;
	Statement statement = null;
	ResultSet result = null;

	public String origName(String websource) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(URL, USR, PWD);

		statement = conn.createStatement();
		result = statement.executeQuery("SELECT  `role_name`,`weibo` FROM  "
				+ "`E_role`  WHERE   `roleID`=" + websource + " LIMIT 1");

		while (result.next())
			websource = result.getString("role_name");

		return websource;
	}
	
	public String repNum(String fileName) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(URL, USR, PWD);
		
		String repNum = null;
		statement = conn.createStatement();
		result = statement.executeQuery("SELECT count(*) as repnum FROM "
				+ "`E_info_reprint` WHERE `infoID`=" + fileName + " AND `reprintID` > 0");
		
		while (result.next())
			repNum = result.getString("repnum");
		
		return repNum;
	}
	
	public void close() throws SQLException {
		if (result != null) {
			result.close();
			result = null;
		}
		if (statement != null) {
			statement.close();
			statement = null;
		}
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}
}
