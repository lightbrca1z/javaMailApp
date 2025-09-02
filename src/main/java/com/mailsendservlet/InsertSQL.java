
package com.mailsendservlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class InsertSQL {
    
    // データベースのvarchar(500)制限に対応
    private static final int MAX_LENGTH = 500;
    
    // 基本的なメールアドレスパターン
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    /**
     * ユーザー情報をデータベースに挿入
     * @param name 名前
     * @param email メールアドレス
     * @param subject 件名
     * @param message メッセージ
     * @return boolean 挿入成功時true、失敗時false
     * @throws IllegalArgumentException パラメータが無効な場合
     */
    public static boolean insertUser(String name, String email, String subject, String message) {
        try {
            // 入力パラメータの詳細検証
            validateParameters(name, email, subject, message);
            
            String sql = "INSERT INTO mailsend (name, email, subject, message) VALUES (?, ?, ?, ?)";

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                // パラメータ設定（長さ制限を適用）
                stmt.setString(1, truncateString(name, MAX_LENGTH));
                stmt.setString(2, truncateString(email, MAX_LENGTH));
                stmt.setString(3, truncateString(subject, MAX_LENGTH));
                stmt.setString(4, truncateString(message, MAX_LENGTH));

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("✅ データが正常に挿入されました！ (挿入行数: " + rowsInserted + ")");
                    return true;
                } else {
                    System.err.println("⚠️ 警告: データの挿入で影響を受けた行が0件です");
                    return false;
                }

            } catch (SQLException e) {
                System.err.println("❌ SQLエラー: データベースへの挿入に失敗しました");
                System.err.println("エラー詳細: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("ErrorCode: " + e.getErrorCode());
                e.printStackTrace();
                
                // 呼び出し元でエラーハンドリングできるよう例外を再スロー
                throw new RuntimeException("データベースへの挿入処理でエラーが発生しました: " + e.getMessage(), e);
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ パラメータエラー: " + e.getMessage());
            throw e; // パラメータエラーはそのまま再スロー
        } catch (Exception e) {
            System.err.println("❌ 予期しないエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("予期しないエラーが発生しました", e);
        }
    }
    
    /**
     * 入力パラメータの詳細検証
     */
    private static void validateParameters(String name, String email, String subject, String message) {
        // null チェック
        if (name == null || email == null || subject == null || message == null) {
            throw new IllegalArgumentException("すべてのパラメータは必須です（nullは許可されません）");
        }
        
        // 空文字列チェック
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です（空文字は許可されません）");
        }
        if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("メールアドレスは必須です（空文字は許可されません）");
        }
        if (subject.trim().isEmpty()) {
            throw new IllegalArgumentException("件名は必須です（空文字は許可されません）");
        }
        if (message.trim().isEmpty()) {
            throw new IllegalArgumentException("メッセージは必須です（空文字は許可されません）");
        }
        
        // 長さチェック
        if (name.length() > MAX_LENGTH) {
            System.out.println("⚠️ 警告: 名前が" + MAX_LENGTH + "文字を超えているため切り詰めます");
        }
        if (email.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("メールアドレスが" + MAX_LENGTH + "文字を超えています");
        }
        if (subject.length() > MAX_LENGTH) {
            System.out.println("⚠️ 警告: 件名が" + MAX_LENGTH + "文字を超えているため切り詰めます");
        }
        if (message.length() > MAX_LENGTH) {
            System.out.println("⚠️ 警告: メッセージが" + MAX_LENGTH + "文字を超えているため切り詰めます");
        }
        
        // メールアドレス形式の基本チェック
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("メールアドレスの形式が正しくありません: " + email);
        }
    }
    
    /**
     * 文字列を指定された長さで切り詰める
     */
    private static String truncateString(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength);
    }
}