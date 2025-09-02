<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.mailsendservlet.Message" %>
<%@ page import="com.mailsendservlet.DatabaseUtil" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>問い合わせフォーム - MailSendServlet</title>
    <link rel="stylesheet" href="./css/style.css">
    <style>
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            font-weight: bold;
            margin-bottom: 5px;
        }
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        .btn {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-danger {
            background-color: #dc3545;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
        .error-message {
            color: #dc3545;
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            padding: 10px;
            border-radius: 4px;
            margin: 10px 0;
        }
        .success-message {
            color: #155724;
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            padding: 10px;
            border-radius: 4px;
            margin: 10px 0;
        }
        .table-responsive {
            overflow-x: auto;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        .text-center {
            text-align: center;
        }
        .no-data {
            color: #6c757d;
            font-style: italic;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="form-container">
            <h2>問い合わせフォーム</h2>
            <form action="../ContactServlet" method="post">
                <div class="form-group">
                    <label for="name">名前 <span style="color: red;">*</span></label>
                    <input type="text" id="name" name="name" maxlength="500" required 
                           placeholder="お名前を入力してください">
                </div>
                
                <div class="form-group">
                    <label for="email">メールアドレス <span style="color: red;">*</span></label>
                    <input type="email" id="email" name="email" maxlength="500" required 
                           placeholder="example@domain.com">
                </div>
                
                <div class="form-group">
                    <label for="subject">件名 <span style="color: red;">*</span></label>
                    <input type="text" id="subject" name="subject" maxlength="500" required 
                           placeholder="件名を入力してください">
                </div>
                
                <div class="form-group">
                    <label for="message">メッセージ <span style="color: red;">*</span></label>
                    <textarea id="message" name="message" rows="5" maxlength="500" required 
                              placeholder="お問い合わせ内容を入力してください"></textarea>
                </div>
                
                <input type="hidden" name="action" value="confirm">
                <button type="submit" class="btn">確認画面へ</button>
            </form>
        </div>

        <div class="history-container">
            <h2>お問い合わせ履歴</h2>
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>名前</th>
                            <th>メールアドレス</th>
                            <th>件名</th>
                            <th>メッセージ</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            // DatabaseUtilを使用してメッセージ履歴を取得
                            List<Message> messages = null;
                            String errorMessage = null;
                            int messageCount = 0;
                            
                            try {
                                System.out.println("Form.jsp: DatabaseUtil.getAllMessages()呼び出し開始...");
                                
                                // データベース接続とクラス存在確認
                                try {
                                    Class.forName("com.mailsendservlet.DatabaseUtil");
                                    System.out.println("Form.jsp: DatabaseUtilクラスの読み込み成功");
                                } catch (ClassNotFoundException cnfe) {
                                    System.err.println("Form.jsp: DatabaseUtilクラスが見つかりません - " + cnfe.getMessage());
                                    throw new RuntimeException("DatabaseUtilクラスが見つかりません", cnfe);
                                }
                                
                                messages = DatabaseUtil.getAllMessages();
                                System.out.println("Form.jsp: DatabaseUtil.getAllMessages()呼び出し完了");
                                
                                if (messages != null) {
                                    messageCount = messages.size();
                                    System.out.println("Form.jsp: " + messageCount + "件のメッセージを取得しました");
                                } else {
                                    System.err.println("Form.jsp: getAllMessages()がnullを返しました");
                                    messages = new ArrayList<Message>();
                                    messageCount = 0;
                                    errorMessage = "データベースから予期しない応答がありました。";
                                }
                                
                            } catch (NoClassDefFoundError ncdfe) {
                                System.err.println("Form.jsp: クラス定義エラー - " + ncdfe.getMessage());
                                ncdfe.printStackTrace();
                                messages = new ArrayList<Message>();
                                messageCount = 0;
                                errorMessage = "システムクラスの読み込みに失敗しました: " + ncdfe.getMessage();
                                
                            } catch (RuntimeException re) {
                                System.err.println("Form.jsp: 実行時エラー - " + re.getMessage());
                                re.printStackTrace();
                                messages = new ArrayList<Message>();
                                messageCount = 0;
                                errorMessage = "システム実行エラーが発生しました: " + re.getMessage();
                                
                            } catch (Exception e) {
                                System.err.println("Form.jsp: 予期しないエラー - " + e.getClass().getName() + ": " + e.getMessage());
                                e.printStackTrace();
                                messages = new ArrayList<Message>();
                                messageCount = 0;
                                errorMessage = "システムエラーが発生しました: " + e.getClass().getSimpleName() + " - " + e.getMessage();
                                
                            } catch (Throwable t) {
                                System.err.println("Form.jsp: 深刻なエラー - " + t.getClass().getName() + ": " + t.getMessage());
                                t.printStackTrace();
                                messages = new ArrayList<Message>();
                                messageCount = 0;
                                errorMessage = "深刻なシステムエラーが発生しました: " + t.getClass().getSimpleName();
                            }
                            
                            if (errorMessage != null) {
                        %>
                        <tr>
                            <td colspan="6" class="text-center">
                                <div class="error-message">
                                    <strong>🚨 データベースエラー</strong><br>
                                    <%= errorMessage %>
                                    <br><small>管理者にお問い合わせください。再読み込みで解決する場合があります。</small>
                                </div>
                            </td>
                        </tr>
                        <%
                            } else if (messages != null && !messages.isEmpty()) {
                                for (Message msg : messages) {
                                    String safeName = (msg.getName() != null) ? msg.getName() : "";
                                    String safeEmail = (msg.getEmail() != null) ? msg.getEmail() : "";
                                    String safeSubject = (msg.getSubject() != null) ? msg.getSubject() : "";
                                    String safeMessage = (msg.getMessage() != null) ? msg.getMessage() : "";
                                    
                                    if (safeMessage.length() > 50) {
                                        safeMessage = safeMessage.substring(0, 50) + "...";
                                    }
                        %>
                        <tr>
                            <td><%= msg.getId() %></td>
                            <td><%= safeName %></td>
                            <td><%= safeEmail %></td>
                            <td><%= safeSubject %></td>
                            <td><%= safeMessage %></td>
                            <td class="text-center">
                                <form action="../deleteSingleHistory" method="post" style="display:inline;">
                                    <input type="hidden" name="id" value="<%= msg.getId() %>">
                                    <button type="submit" class="btn btn-danger" 
                                            onclick="return confirm('ID: <%= msg.getId() %> の履歴を削除しますか？');">
                                        削除
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="6" class="text-center no-data">
                                📝 まだお問い合わせ履歴がありません。<br>
                                <small>上記のフォームからお問い合わせを送信すると、こちらに履歴が表示されます。</small>
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>
            
            <div style="margin-top: 15px;">
                <% if (messageCount > 0) { %>
                <p><strong>合計:</strong> <%= messageCount %> 件の履歴があります。</p>
                <button type="button" class="btn btn-danger" 
                        onclick="if(confirm('本当にすべての履歴（<%= messageCount %>件）を削除しますか？\n\nこの操作は取り消せません。')) { window.location.href='../deleteHistory'; }"
                        title="すべての履歴を削除">
                    🗑️ すべての履歴を削除
                </button>
                <% } else if (errorMessage == null) { %>
                <p class="no-data">履歴がありません。</p>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html>