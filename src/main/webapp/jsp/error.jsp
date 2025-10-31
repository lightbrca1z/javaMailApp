<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>エラー - MailSendServlet</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/style.css">
    <style>
        .error-container {
            text-align: center;
            padding: 50px;
            background-color: #f8f9fa;
            border-radius: 10px;
            margin: 50px auto;
            max-width: 600px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .error-icon {
            font-size: 48px;
            color: #dc3545;
            margin-bottom: 20px;
        }
        .error-title {
            color: #dc3545;
            margin-bottom: 20px;
        }
        .error-message {
            color: #6c757d;
            margin-bottom: 30px;
        }
        .back-button {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 5px;
            display: inline-block;
        }
        .back-button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="error-container">
            <div class="error-icon">⚠️</div>
            <h2 class="error-title">申し訳ございません</h2>
            <p class="error-message">
                システムエラーが発生しました。<br>
                しばらく時間をおいてから再度お試しください。
            </p>
            <%
                // カスタムエラーメッセージの表示
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null && !errorMessage.isEmpty()) {
            %>
                <p class="error-details" style="color: #6c757d; font-size: 14px; margin-top: 10px;">
                    <%= errorMessage.replace("<", "&lt;").replace(">", "&gt;") %>
                </p>
            <% } %>
            
            <% if (request.getAttribute("javax.servlet.error.status_code") != null) { %>
                <p class="error-details">
                    エラーコード: <%= request.getAttribute("javax.servlet.error.status_code") %>
                </p>
            <% } %>
            
            <% 
                // デバッグ情報（開発環境用）
                Exception errorException = (Exception) request.getAttribute("javax.servlet.error.exception");
                if (errorException != null) {
                    System.err.println("エラーページで例外を検出: " + errorException.getClass().getName());
                    System.err.println("メッセージ: " + errorException.getMessage());
                    errorException.printStackTrace();
                }
            %>
            
            <a href="${pageContext.request.contextPath}/jsp/Form.jsp" class="back-button">問い合わせフォームに戻る</a>
        </div>
    </div>
</body>
</html>
