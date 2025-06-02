<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>問い合わせフォーム</title>
    <link rel="stylesheet" href="./css/style.css">
</head>
<body>
    <div class="container">
        <div class="form-container">
            <h2>問い合わせフォーム</h2>
            <form action="../ContactServlet" method="post">
                名前: <input type="text" name="name" required><br>
                メール: <input type="email" name="email" required><br>
                件名: <input type="text" name="subject" required><br>
                メッセージ:<br>
                <textarea name="message" rows="5" cols="40" required></textarea><br>
                <input type="hidden" name="action" value="confirm">
                <input type="submit" value="確認画面へ">
            </form>
        </div>

        <div class="history-container">
            <h2>お問い合わせ履歴</h2>
            <table>
                <tr>
                    <th>名前</th>
                    <th>メール</th>
                    <th>件名</th>
                    <th>メッセージ</th>
                    <th>削除</th>
                </tr>
                
<!--                    String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/mailsendservlet?useSSL=false&serverTimezone=UTC";-->
<!--                    String dbUser = "root";-->
<!--                    String dbPassword = "";-->
                <%
                    // データベース接続情報
						String jdbcUrl = "jdbc:mysql://160.251.206.96:3306/mailsendservlet?useSSL=false&serverTimezone=UTC";
						String dbUser = "root"; // または作成したMySQLユーザー
						String dbPassword = ""; // 実際のパスワード

                    Connection conn = null;
                    PreparedStatement stmt = null;
                    ResultSet rs = null;

                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
                        String sql = "SELECT id, name, email, subject, message FROM mailsend ORDER BY id DESC";
                        stmt = conn.prepareStatement(sql);
                        rs = stmt.executeQuery();

                        while (rs.next()) {
                %>
                <tr>
                    <td><%= rs.getString("name") %></td>
                    <td><%= rs.getString("email") %></td>
                    <td><%= rs.getString("subject") %></td>
                    <td><%= rs.getString("message") %></td>
                    <td>
                        <form action="../deleteSingleHistory" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="<%= rs.getInt("id") %>">
                            <input type="submit" value="削除" onclick="return confirm('この履歴を削除しますか？');">
                        </form>
                    </td>
                </tr>
                <%
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                %>
                <tr>
                    <td colspan="4">エラーが発生しました。</td>
                </tr>
                <%
                    } finally {
                        try {
                            if (rs != null) rs.close();
                            if (stmt != null) stmt.close();
                            if (conn != null) conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                %>
            </table>
            <a href="../deleteHistory" onclick="return confirm('本当に履歴を削除しますか？');">履歴を消す</a>
        </div>
    </div>
</body>
</html>