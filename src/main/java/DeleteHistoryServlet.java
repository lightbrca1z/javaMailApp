import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mailsendservlet.DatabaseUtil;

// web.xmlで設定されているため@WebServletアノテーションは不要
public class DeleteHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // try-with-resources構文を使用してリソースの自動解放を行う
        String sql = "DELETE FROM mailsend";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // DELETE クエリ実行
            int rowsAffected = stmt.executeUpdate();
            
            // 削除結果のログ出力
            System.out.println("削除された行数: " + rowsAffected);
            
            // 削除後に問い合わせフォームのページへリダイレクト
            response.sendRedirect("./jsp/Form.jsp");
            
        } catch (SQLException e) {
            // SQLExceptionを適切にログ出力し、エラーページに遷移
            System.err.println("データベースエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            
            // エラーメッセージを設定してエラーページに遷移
            request.setAttribute("errorMessage", "データベースエラーが発生しました。履歴の削除に失敗しました。");
            try {
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            } catch (ServletException | IOException forwardException) {
                // forward失敗時は直接レスポンスに書き込み
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println("<h3>データベースエラーが発生しました。</h3>");
                response.getWriter().println("<p>履歴の削除に失敗しました。管理者にお問い合わせください。</p>");
                response.getWriter().println("<a href='./jsp/Form.jsp'>フォームに戻る</a>");
            }
        } catch (Exception e) {
            // その他の予期しないエラー
            System.err.println("予期しないエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            
            // エラーメッセージを設定してエラーページに遷移
            request.setAttribute("errorMessage", "システムエラーが発生しました。");
            try {
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            } catch (ServletException | IOException forwardException) {
                // forward失敗時は直接レスポンスに書き込み
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println("<h3>システムエラーが発生しました。</h3>");
                response.getWriter().println("<p>管理者にお問い合わせください。</p>");
                response.getWriter().println("<a href='./jsp/Form.jsp'>フォームに戻る</a>");
            }
        }
    }
}
