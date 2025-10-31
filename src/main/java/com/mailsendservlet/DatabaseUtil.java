
package com.mailsendservlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * データベース接続とメール送信履歴の管理を行うユーティリティクラス
 * 
 * 【データベース設定】
 * このアプリケーションを実行する前に、以下の設定を確認してください：
 * 
 * 1. MySQLサーバーが起動していることを確認
 * 2. データベース「mailsendservlet」が作成されていることを確認
 *    (プロジェクトルートのmailsendservlet.sqlを実行してください)
 * 3. 必要に応じて、以下の定数を環境に合わせて変更してください：
 *    - URL: データベースのURL（デフォルト: localhost:3306/mailsendservlet）
 *    - USER: データベースユーザー名（デフォルト: root）
 *    - PASSWORD: データベースパスワード（デフォルト: 空文字）
 */
public class DatabaseUtil {
    // データベース接続設定（一元管理）
    // ※環境に合わせて以下の値を変更してください
    // MAMP使用時はポート8889、通常のMySQLは3306
    private static final String URL = "jdbc:mysql://localhost:8889/mailsendservlet?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // MAMPのデフォルトパスワード

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // JDBCドライバのロード
            System.out.println("✅ JDBC Driver loaded successfully in static block");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBC Driver not found in static block: " + e.getMessage());
            // 例外をスローしないで、後でgetConnection()時に再試行
            // throw new RuntimeException("JDBC Driver not found", e);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error in static block: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * データベース接続を取得
     * @return Connection データベース接続
     * @throws SQLException SQL例外
     */
    public static Connection getConnection() throws SQLException {
        // JDBCドライバーの再確認と再読み込み
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ JDBC Driver verified in getConnection()");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBC Driver not found in getConnection(): " + e.getMessage());
            throw new SQLException("JDBC Driver not available: " + e.getMessage(), e);
        }
        
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            // 接続テスト
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ データベース接続成功: " + URL);
                return conn;
            } else {
                throw new SQLException("接続は確立されましたが、無効な状態です");
            }
        } catch (SQLException e) {
            System.err.println("❌ データベース接続失敗:");
            System.err.println("URL: " + URL);
            System.err.println("User: " + USER);
            System.err.println("エラー詳細: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
            
            // より詳細なエラー情報を提供
            if (e.getMessage().contains("Communications link failure")) {
                System.err.println("💡 解決のヒント: MySQLサーバーが起動していない、またはネットワーク接続に問題があります");
            } else if (e.getMessage().contains("Access denied")) {
                System.err.println("💡 解決のヒント: ユーザー名またはパスワードが間違っています");
            } else if (e.getMessage().contains("Unknown database")) {
                System.err.println("💡 解決のヒント: データベース 'mailsendservlet' が存在しません");
            }
            
            throw e; // 元の例外を再スロー
        }
    }

    /**
     * メール送信履歴を取得（新しい順）
     * @return List<Message> メッセージのリスト（エラー時は空のリスト、例外は発生しない）
     */
    public static List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT id, name, email, subject, message FROM mailsend ORDER BY id DESC";
        
        // 事前チェック：JDBCドライバーの確認
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBCドライバーエラー: " + e.getMessage());
            return new ArrayList<>();
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // 接続取得
            conn = getConnection();
            if (conn == null || conn.isClosed()) {
                System.err.println("❌ データベース接続が無効です");
                return new ArrayList<>();
            }
            
            // SQL準備
            stmt = conn.prepareStatement(sql);
            if (stmt == null) {
                System.err.println("❌ PreparedStatementの作成に失敗しました");
                return new ArrayList<>();
            }
            
            // クエリ実行
            rs = stmt.executeQuery();
            if (rs == null) {
                System.err.println("❌ ResultSetがnullです");
                return new ArrayList<>();
            }
            
            // 結果処理
            int recordCount = 0;
            while (rs.next()) {
                try {
                    Message msg = new Message();
                    
                    // IDの取得（必須フィールド）
                    int id = rs.getInt("id");
                    if (rs.wasNull()) {
                        System.err.println("⚠️ IDがnullのレコードをスキップします");
                        continue;
                    }
                    msg.setId(id);
                    
                    // null値の安全な処理
                    String name = rs.getString("name");
                    msg.setName(name != null ? name : "");
                    
                    String email = rs.getString("email");
                    msg.setEmail(email != null ? email : "");
                    
                    String subject = rs.getString("subject");
                    msg.setSubject(subject != null ? subject : "");
                    
                    String message = rs.getString("message");
                    msg.setMessage(message != null ? message : "");
                    
                    messages.add(msg);
                    recordCount++;
                    
                } catch (SQLException sqlEx) {
                    System.err.println("⚠️ レコード読み取りエラー: " + sqlEx.getMessage());
                    continue; // このレコードはスキップして次へ
                } catch (Exception ex) {
                    System.err.println("⚠️ レコード処理エラー: " + ex.getMessage());
                    continue; // このレコードはスキップして次へ
                }
            }
            
            System.out.println("✅ メッセージ取得完了: " + recordCount + "件のレコードを処理しました");
            
        } catch (SQLException e) {
            System.err.println("❌ SQLエラー: メッセージ取得に失敗しました");
            System.err.println("エラー詳細: " + e.getMessage());
            System.err.println("SQLState: " + (e.getSQLState() != null ? e.getSQLState() : "不明"));
            System.err.println("ErrorCode: " + e.getErrorCode());
            
            // 具体的なエラーメッセージ
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                if (errorMsg.contains("Communications link failure")) {
                    System.err.println("💡 解決ヒント: データベースサーバーに接続できません");
                } else if (errorMsg.contains("Table") && errorMsg.contains("doesn't exist")) {
                    System.err.println("💡 解決ヒント: テーブル 'mailsend' が存在しません");
                } else if (errorMsg.contains("Access denied")) {
                    System.err.println("💡 解決ヒント: データベースアクセス権限を確認してください");
                }
            }
            
            e.printStackTrace();
            
        } catch (Exception e) {
            System.err.println("❌ 予期しないエラー: " + e.getMessage());
            System.err.println("エラータイプ: " + e.getClass().getSimpleName());
            e.printStackTrace();
            
        } finally {
            // リソースの確実な解放
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                System.err.println("⚠️ ResultSet クローズエラー: " + e.getMessage());
            }
            
            try {
                if (stmt != null && !stmt.isClosed()) {
                    stmt.close();
                }
            } catch (SQLException e) {
                System.err.println("⚠️ PreparedStatement クローズエラー: " + e.getMessage());
            }
            
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("⚠️ Connection クローズエラー: " + e.getMessage());
            }
        }
        
        return messages; // 例外が発生した場合でも空のリストを返す
    }

    /**
     * データベース接続をテスト
     * @return boolean 接続成功ならtrue
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("データベース接続テスト失敗: " + e.getMessage());
            return false;
        }
    }
}
