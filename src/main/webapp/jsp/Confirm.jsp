<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    try {
        // パラメータの取得
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        
        // デバッグログ
        System.out.println("📝 Confirm.jsp: パラメータ取得");
        System.out.println("名前: " + name);
        System.out.println("メール: " + email);
        System.out.println("件名: " + subject);
        System.out.println("メッセージ: " + (message != null ? message.substring(0, Math.min(50, message.length())) : "null"));
        
        // nullチェックとデフォルト値設定
        if (name == null) name = "";
        if (email == null) email = "";
        if (subject == null) subject = "";
        if (message == null) message = "";
        
        // HTMLエスケープ（XSS対策）
        String escapedName = name.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
        String escapedEmail = email.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
        String escapedSubject = subject.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
        String escapedMessage = message.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
%>
<!DOCTYPE html>
<html>
<head>
    <title>確認画面</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/jsp/css/style2.css">
</head>
<body>
<div class="container">
  <div class="form-container">
    <h2>確認画面</h2>
    <p>以下の内容で送信します。よろしいですか？</p>
    <p>名前: <%= escapedName %></p>
    <p>メール: <%= escapedEmail %></p>
    <p>件名: <%= escapedSubject %></p>
    <p>メッセージ: <%= escapedMessage %></p>

    <form action="${pageContext.request.contextPath}/ContactServlet" method="post">
        <input type="hidden" name="name" value="<%= escapedName %>">
        <input type="hidden" name="email" value="<%= escapedEmail %>">
        <input type="hidden" name="subject" value="<%= escapedSubject %>">
        <input type="hidden" name="message" value="<%= escapedMessage %>">
        <input type="hidden" name="action" value="send">
        <input type="submit" value="送信する">
    </form>

    <form action="${pageContext.request.contextPath}/jsp/Form.jsp" method="get">
        <input type="submit" value="修正する">
    </form>
    </div>
    </div>
<%
    } catch (Exception e) {
        System.err.println("❌ Confirm.jspでエラーが発生: " + e.getMessage());
        e.printStackTrace();
%>
    <div class="error-message">
        <h2>エラーが発生しました</h2>
        <p>確認画面の表示中にエラーが発生しました。</p>
        <p><a href="${pageContext.request.contextPath}/jsp/Form.jsp">フォームに戻る</a></p>
    </div>
<%
    }
%>
</body>
</html>
