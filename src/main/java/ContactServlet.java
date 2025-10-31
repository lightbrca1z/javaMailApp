import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.mailsendservlet.InsertSQL;

public class ContactServlet extends HttpServlet {

    private String smtpHost;
    private String smtpPort;
    private String username;
    private String password;

    @Override
    public void init() {
        try {
            // config.propertiesファイルから設定を読み込む
            Properties config = new Properties();
            InputStream configStream = getServletContext().getResourceAsStream("/WEB-INF/config.properties");
            if (configStream != null) {
                config.load(configStream);
                configStream.close();
                
                smtpHost = config.getProperty("SMTP_HOST", "smtp.gmail.com");
                smtpPort = config.getProperty("SMTP_PORT", "587");
                username = config.getProperty("MAIL_USERNAME", "");
                password = config.getProperty("MAIL_PASSWORD", "");
            } else {
                // デフォルト値を設定
                smtpHost = "smtp.gmail.com";
                smtpPort = "587";
                username = "";
                password = "";
                System.out.println("警告: config.propertiesファイルが見つかりません。デフォルト値を使用します。");
            }

            // 設定の確認（デバッグ用、パスワードは表示しない）
            System.out.println("SMTP_HOST: " + smtpHost);
            System.out.println("SMTP_PORT: " + smtpPort);
            System.out.println("MAIL_USERNAME: " + username);
        } catch (IOException e) {
            System.err.println("設定ファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            String action = request.getParameter("action");
            
            System.out.println("📝 ContactServlet: doPost呼び出し - action=" + action);

            if ("confirm".equals(action)) {
                // 確認画面へ遷移
                System.out.println("📝 確認画面への遷移を開始します");
                
                // パラメータの取得とログ出力
                String name = request.getParameter("name");
                String email = request.getParameter("email");
                String subject = request.getParameter("subject");
                String message = request.getParameter("message");
                
                System.out.println("名前: " + (name != null ? name : "null"));
                System.out.println("メールアドレス: " + (email != null ? email : "null"));
                System.out.println("件名: " + (subject != null ? subject : "null"));
                System.out.println("メッセージ: " + (message != null ? message.substring(0, Math.min(50, message.length())) : "null"));
                
                try {
                    request.getRequestDispatcher("/jsp/Confirm.jsp").forward(request, response);
                    System.out.println("✅ 確認画面への遷移が完了しました");
                } catch (Exception e) {
                    System.err.println("❌ 確認画面への遷移でエラーが発生: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
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
                properties.put("mail.smtp.host", smtpHost);
                properties.put("mail.smtp.port", smtpPort);

                // セッション作成
                Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                    @Override
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }
                });

                // メール作成
                javax.mail.Message msg = new MimeMessage(session);
                
                // メール設定の検証とアドレス設定
                try {
                    if (username == null || username.trim().isEmpty() || username.equals("your-email@gmail.com")) {
                        System.out.println("⚠️ 送信者メールアドレスが未設定です。デフォルトアドレスを使用します。");
                        msg.setFrom(new InternetAddress("noreply@example.com"));
                    } else {
                        msg.setFrom(new InternetAddress(username));
                        System.out.println("✅ 送信者アドレス設定: " + username);
                    }
                    
                    msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email));
                    msg.setSubject("問い合わせ: " + subject);
                    msg.setText("名前: " + name + "\nメール: " + email + "\n\n" + message);
                    
                    System.out.println("✅ メールオブジェクトの作成が成功しました");
                    
                } catch (AddressException ae) {
                    System.err.println("❌ メールアドレスエラー: " + ae.getMessage());
                    System.err.println("問題のアドレス - 送信者: '" + username + "', 受信者: '" + email + "'");
                    throw new ServletException("メールアドレスの設定に問題があります: " + ae.getMessage(), ae);
                }

                // データベースへのインサート
                boolean dbInsertSuccess = false;
                try {
                    dbInsertSuccess = InsertSQL.insertUser(name, email, subject, message);
                    if (dbInsertSuccess) {
                        System.out.println("📊 データベースへの保存が成功しました");
                    } else {
                        System.err.println("⚠️ データベースへの保存に失敗しましたが、メール送信を続行します");
                    }
                } catch (IllegalArgumentException e) {
                    // パラメータエラーの場合は処理を中断
                    System.err.println("❌ パラメータエラー: " + e.getMessage());
                    throw new ServletException("入力データが無効です: " + e.getMessage(), e);
                } catch (RuntimeException e) {
                    // データベースエラーの場合でもメール送信は続行
                    System.err.println("⚠️ データベースへの保存に失敗しましたが、メール送信を続行します: " + e.getMessage());
                }
                
                // メール送信
                try {
                    if (username == null || username.trim().isEmpty() || username.equals("your-email@gmail.com")) {
                        System.out.println("⚠️ メール設定が未設定のため、メール送信をスキップします");
                        System.out.println("📊 データベースへの保存のみ完了しました");
                    } else {
                        System.out.println("📧 メール送信を開始します...");
                        Transport.send(msg);
                        System.out.println("✅ メール送信が成功しました");
                    }
                } catch (MessagingException mailEx) {
                    System.err.println("⚠️ メール送信に失敗しましたが、処理を続行します: " + mailEx.getMessage());
                    mailEx.printStackTrace();
                } catch (Exception mailEx) {
                    System.err.println("⚠️ 予期しないメール送信エラー: " + mailEx.getMessage());
                    mailEx.printStackTrace();
                }

                // 送信完了画面へ遷移
                response.sendRedirect(request.getContextPath() + "/jsp/Result.jsp");
            } catch (IOException e) {
                System.err.println("❌ 入出力エラーが発生しました: " + e.getMessage());
                e.printStackTrace();
                throw new ServletException("入出力エラーが発生しました", e);
            } catch (Exception e) {
                System.err.println("❌ 予期しないエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
                throw new ServletException("予期しないエラーが発生しました", e);
            }
            } // else if ("send".equals(action)) ブロックを閉じる
        } catch (ServletException e) {
            System.err.println("❌ ServletException: " + e.getMessage());
            e.printStackTrace();
            
            // エラーページへ遷移
            request.setAttribute("errorMessage", "システムエラーが発生しました: " + e.getMessage());
            try {
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            } catch (Exception forwardEx) {
                // forward失敗時は例外を再スロー
                throw e;
            }
        } catch (IOException e) {
            System.err.println("❌ IOException: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.err.println("❌ 予期しないエラー: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            
            // エラーページへ遷移
            request.setAttribute("errorMessage", "予期しないエラーが発生しました");
            try {
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            } catch (Exception forwardEx) {
                // forward失敗時は新しいServletExceptionをスロー
                throw new ServletException("エラー処理中に問題が発生しました", e);
            }
        }
    }
}