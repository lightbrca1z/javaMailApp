

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * データベース接続テスト用クラス
 * 
 * ※注意: このクラスはテスト目的のみで使用してください
 * 実際のアプリケーションではDatabaseUtilクラスを使用してください
 */
public class jdbcSample {
	// ローカルデータベース設定（MAMP使用時）
	private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:8889/mailsendservlet?useSSL=false&serverTimezone=UTC";
    private static final String JDBC_USER = "root"; // あなたのMySQLユーザー名
    private static final String JDBC_PASSWORD = "root"; // MAMPのデフォルトパスワード
	
	// リモートデータベース設定（セキュリティのためコメントアウト）
	// ※本番環境の認証情報はソースコードに含めないでください
//	private static final String JDBC_URL = "jdbc:mysql://160.251.184.93:8006/mailsendservlet?useSSL=false&serverTimezone=UTC";
//	private static final String JDBC_USER = "root";
//	private static final String JDBC_PASSWORD = "Keeper2020";


    public static void main(String[] args) {
        try {
            // JDBCドライバーのロード（必要に応じて）
            Class.forName("com.mysql.cj.jdbc.Driver");

            // データベース接続
            Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            System.out.println("データベース接続成功！");

            // 接続を閉じる
            connection.close();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBCドライバーが見つかりません。");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("データベース接続に失敗しました。");
            e.printStackTrace();
        }
    }
}
