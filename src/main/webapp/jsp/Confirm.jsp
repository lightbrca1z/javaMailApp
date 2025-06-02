<%@ page contentType="text/html; charset=UTF-8" %>
<%
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String subject = request.getParameter("subject");
    String message = request.getParameter("message");
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
    <p>名前: <%= name %></p>
    <p>メール: <%= email %></p>
    <p>件名: <%= subject %></p>
    <p>メッセージ: <%= message %></p>

    <form action="./ContactServlet" method="post">
        <input type="hidden" name="name" value="<%= name %>">
        <input type="hidden" name="email" value="<%= email %>">
        <input type="hidden" name="subject" value="<%= subject %>">
        <input type="hidden" name="message" value="<%= message %>">
        <input type="hidden" name="action" value="send">
        <input type="submit" value="送信する">
    </form>

    <form action="Form.jsp" method="get">
        <input type="submit" value="修正する">
    </form>
    </div>
    </div>
</body>
</html>
