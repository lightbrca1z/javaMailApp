

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
//	private static final String URL = "jdbc:mysql://127.0.0.1:3306/mailsendservlet?useSSL=false&serverTimezone=UTC";
//    private static final String USER = "root"; // あなたのMySQLユーザー名
//    private static final String PASSWORD = ""; // あなたのMySQLパスワード
    
	private static final String URL = "jdbc:mysql://160.251.206.96:3306/mailsendservlet?useSSL=false&serverTimezone=UTC";
	private static final String USER = "root"; // または作成したMySQLユーザー
	private static final String PASSWORD = ""; // 実際のパスワード

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // JDBCドライバのロード
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
