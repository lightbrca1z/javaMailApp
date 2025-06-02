import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/deleteSingleHistory")
public class DeleteSingleHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // データベース接続情報
//    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/mailsendservlet?useSSL=false&serverTimezone=UTC";
//    private static final String DB_USER = "root";
//    private static final String DB_PASSWORD = "";
    
	private static final String JDBC_URL = "jdbc:mysql://160.251.184.93:3306/mailsendservlet?useSSL=false&serverTimezone=UTC";
	private static final String DB_USER = "root"; // または作成したMySQLユーザー
	private static final String DB_PASSWORD = ""; // 実際のパスワード
    

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // パラメータからIDを取得
            String id = request.getParameter("id");

            if (id != null && !id.isEmpty()) {
                // JDBCドライバの読み込み
                Class.forName("com.mysql.cj.jdbc.Driver");
                // データベース接続
                conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                // DELETE クエリ実行
                String sql = "DELETE FROM mailsend WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(id));
                stmt.executeUpdate();
            }

            // 削除後に問い合わせフォームのページへリダイレクト
            response.sendRedirect("./jsp/Form.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("エラーが発生しました。");
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
