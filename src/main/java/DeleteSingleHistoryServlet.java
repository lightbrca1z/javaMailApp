import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.mailsendservlet.DatabaseUtil;

// web.xmlで設定されているため@WebServletアノテーションは不要
public class DeleteSingleHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // パラメータからIDを取得
        String id = request.getParameter("id");
        
        if (id != null && !id.isEmpty()) {
            // try-with-resources構文を使用してリソースの自動解放を行う
            String sql = "DELETE FROM mailsend WHERE id = ?";
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                // DELETE クエリ実行
                stmt.setInt(1, Integer.parseInt(id));
                stmt.executeUpdate();
                
            } catch (SQLException e) {
                // SQLExceptionを適切にログ出力し、ユーザーにエラーメッセージを表示
                e.printStackTrace();
                try {
                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().println("<h3>データベースエラーが発生しました。</h3>");
                    response.getWriter().println("<p>ID: " + id + " の削除に失敗しました。</p>");
                    response.getWriter().println("<a href='" + request.getContextPath() + "/jsp/Form.jsp'>フォームに戻る</a>");
                } catch (IOException ioException) {
                    throw new ServletException("エラーメッセージの出力に失敗しました", ioException);
                }
                return; // エラー時はリダイレクトしない
            } catch (NumberFormatException e) {
                // IDの数値変換エラーを適切に処理
                e.printStackTrace();
                try {
                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().println("<h3>無効なIDが指定されました。</h3>");
                    response.getWriter().println("<p>ID: " + id + " は無効な形式です。</p>");
                    response.getWriter().println("<a href='" + request.getContextPath() + "/jsp/Form.jsp'>フォームに戻る</a>");
                } catch (IOException ioException) {
                    throw new ServletException("エラーメッセージの出力に失敗しました", ioException);
                }
                return; // エラー時はリダイレクトしない
            }
        }
        
        // 削除後（または空のIDの場合）に問い合わせフォームのページへリダイレクト
        try {
            response.sendRedirect(request.getContextPath() + "/jsp/Form.jsp");
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServletException("リダイレクトに失敗しました", e);
        }
    }
}
