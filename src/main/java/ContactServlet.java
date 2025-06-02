import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.github.cdimascio.dotenv.Dotenv;

@WebServlet("/ContactServlet")
public class ContactServlet extends HttpServlet {

    private String smtpHost;
    private String smtpPort;
    private String username;
    private String password;

    public void init() {
        // `.env` から環境変数を読み込む
        Dotenv dotenv = Dotenv.configure()
                .directory("C:\\pleiades\\2024-12\\workspace\\MailSendServlet") // `.env`のあるフォルダ
                .load();

        smtpHost = dotenv.get("SMTP_HOST"); // SMTPサーバー（例: smtp.gmail.com）
        smtpPort = dotenv.get("SMTP_PORT"); // SMTPポート（例: 587）
        username = dotenv.get("MAIL_USERNAME"); // 送信元メールアドレス
        password = dotenv.get("MAIL_PASSWORD"); // Googleアプリパスワード

        // 環境変数の確認（デバッグ用）
        System.out.println("SMTP_HOST: " + smtpHost);
        System.out.println("SMTP_PORT: " + smtpPort);
        System.out.println("MAIL_USERNAME: " + username);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("confirm".equals(action)) {
            // 確認画面へ遷移
            request.getRequestDispatcher("./jsp/Confirm.jsp").forward(request, response);
        } else if ("send".equals(action)) {
            // フォームデータ取得
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String subject = request.getParameter("subject");
            String message = request.getParameter("message");

            // メール送信
            try {
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.port", "587");

                // セッション作成
                Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                    @Override
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }
                });


                // メール作成
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(username));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                msg.setSubject("問い合わせ: " + subject);
                msg.setText("名前: " + name + "\nメール: " + email + "\n\n" + message);

    	        InsertSQL.insertUser(name, email, subject, message);
                
                // メール送信
                Transport.send(msg);

                // 送信完了画面へ遷移
                response.sendRedirect("./jsp/Result.jsp");
            } catch (MessagingException e) {
                throw new ServletException("メール送信に失敗しました", e);
            }
        }
    }
}
