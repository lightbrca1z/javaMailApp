
	import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class InsertSQL {
    public static void insertUser(String name, String email, String subject, String message) {
        String sql = "INSERT INTO mailsend (name, email, subject, message) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, subject);
            stmt.setString(4, message);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("データが正常に挿入されました！");
            } else {
                System.out.println("データの挿入に失敗しました。");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}