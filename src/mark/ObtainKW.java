package mark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ObtainKW {
	private static final String URL = "jdbc:mysql://10.0.0.85:3306/news_data";
	private static final String USR = "web_dev";
	private static final String PWD = "3ced9208632ab2a45ea9cc0040098269";

	Connection conn = null;
	Statement statement = null;
	ResultSet result = null;

	public String getKey(int roleID) {
		String resultKey = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USR, PWD);

			statement = conn.createStatement();
			result = statement.executeQuery("SELECT * FROM `E_role_key` WHERE roleID=" + roleID);

			while (result.next())
				resultKey = result.getString("keyword");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return resultKey;
	}
}
