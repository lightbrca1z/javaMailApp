# MailSendServlet - メール送信Webアプリケーション

## 概要
問い合わせフォームからメールを送信し、データベースに履歴を保存するJava Webアプリケーションです。

## 必要な環境
- **Java**: JDK 11以上
- **Webサーバー**: Apache Tomcat 9.0以上
- **データベース**: MySQL 5.7以上
- **IDE**: Eclipse (Tomcatプラグイン付き)

## セットアップ手順

### 1. データベースの準備
```bash
# MySQLを起動
# プロジェクトルートのmailsendservlet.sqlを実行
mysql -u root -p < mailsendservlet.sql
```

### 2. データベース接続設定
`src/main/java/com/mailsendservlet/DatabaseUtil.java`を開き、必要に応じて以下を変更：
```java
private static final String URL = "jdbc:mysql://localhost:3306/mailsendservlet?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "";
```

### 3. メール送信設定
`src/main/webapp/WEB-INF/config.properties`を編集：
```properties
# Gmailを使用する場合
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# テストモード（メール送信スキップ）の場合
MAIL_USERNAME=
MAIL_PASSWORD=
```

**注意**: Gmailを使用する場合は、アプリパスワードを取得してください
→ https://myaccount.google.com/apppasswords

### 4. Eclipseでのデプロイ
1. プロジェクトをEclipseにインポート
2. プロジェクトを右クリック → `Clean...` → OK
3. プロジェクトを右クリック → `Run As` → `Run on Server`
4. Apache Tomcat v9.0を選択
5. ブラウザで `http://localhost:8080/MailSendServlet/` にアクセス

## トラブルシューティング

### エラー: 確認画面への遷移時にシステムエラー

#### 解決手順:

1. **Eclipseでプロジェクトをクリーン&ビルド**
   ```
   プロジェクトを右クリック → Clean...
   プロジェクトを右クリック → Build Project
   ```

2. **Tomcatサーバーをクリーンして再起動**
   - Servers ビューでTomcatを右クリック → `Clean...`
   - Tomcatを右クリック → `Clean Tomcat Work Directory...`
   - Tomcatを停止
   - Tomcatを再起動

3. **ブラウザのキャッシュをクリア**
   - Chrome: `Cmd + Shift + Delete`
   - ページをリロード: `Cmd + Shift + R`

4. **ログの確認**
   - Eclipseの `Console` ビューを確認
   - エラーメッセージを確認し、以下のキーワードを探す：
     - `❌` マークがあるログ
     - `ContactServlet: doPost呼び出し`
     - `Confirm.jsp: パラメータ取得`

5. **詳細なエラーログの確認**
   ```
   📝 ContactServlet: doPost呼び出し - action=confirm
   📝 確認画面への遷移を開始します
   名前: XXX
   メールアドレス: XXX
   件名: XXX
   メッセージ: XXX
   ```
   これらのログが出力されているか確認してください。

### エラー: データベース接続エラー

**症状**: `Communications link failure` または `Access denied`

**解決方法**:
1. MySQLサーバーが起動しているか確認
   ```bash
   # macOSの場合
   mysql.server status
   # または
   sudo /Applications/MAMP/bin/mysql/bin/mysql.server status
   ```

2. データベース接続情報を確認
   - `DatabaseUtil.java`の設定を確認
   - ユーザー名、パスワード、ポート番号を確認

3. データベースが存在するか確認
   ```bash
   mysql -u root -p
   SHOW DATABASES;
   USE mailsendservlet;
   SHOW TABLES;
   ```

### エラー: 404 Not Found - ContactServlet

**解決方法**:
1. web.xmlの設定を確認
2. プロジェクトのコンテキストパスを確認
3. URLが正しいか確認: `http://localhost:8080/MailSendServlet/ContactServlet`

## プロジェクト構造
```
javaMailApp-javaMailApp/
├── src/main/
│   ├── java/
│   │   ├── ContactServlet.java              # メイン処理サーブレット
│   │   ├── DeleteHistoryServlet.java        # 履歴全削除サーブレット
│   │   ├── DeleteSingleHistoryServlet.java  # 履歴単一削除サーブレット
│   │   ├── jdbcSample.java                  # DB接続テスト用
│   │   └── com/mailsendservlet/
│   │       ├── DatabaseUtil.java            # DB接続ユーティリティ
│   │       ├── InsertSQL.java               # DB挿入処理
│   │       └── Message.java                 # メッセージエンティティ
│   └── webapp/
│       ├── jsp/
│       │   ├── Form.jsp                     # 入力フォーム画面
│       │   ├── Confirm.jsp                  # 確認画面
│       │   ├── Result.jsp                   # 完了画面
│       │   ├── error.jsp                    # エラー画面
│       │   └── css/
│       │       ├── style.css
│       │       └── style2.css
│       ├── WEB-INF/
│       │   ├── web.xml                      # サーブレット設定
│       │   ├── config.properties            # メール設定
│       │   └── lib/                         # ライブラリJARファイル
│       └── META-INF/
│           └── MANIFEST.MF
├── build/classes/                           # コンパイル済みクラス
├── mailsendservlet.sql                      # DB初期化スクリプト
└── README.md                                # このファイル
```

## 主な機能
1. **問い合わせフォーム**: 名前、メールアドレス、件名、メッセージを入力
2. **確認画面**: 入力内容を確認
3. **メール送信**: Gmailを使用してメール送信（設定している場合）
4. **データベース保存**: 問い合わせ履歴をMySQLに保存
5. **履歴管理**: 問い合わせ履歴の表示と削除

## セキュリティ機能
- XSS対策: ユーザー入力のHTMLエスケープ
- SQLインジェクション対策: PreparedStatementの使用
- エラーハンドリング: 詳細なエラーログと適切なエラーページ表示

## 開発者向け情報

### デバッグモード
サーブレットとJSPには詳細なログ出力機能が実装されています。
Eclipseの `Console` ビューでログを確認できます。

### ログの見方
- ✅: 成功
- ⚠️: 警告（処理は継続）
- ❌: エラー
- 📝: 情報
- 📊: データベース関連
- 📧: メール関連

## ライセンス
教育目的で作成されたサンプルプロジェクトです。

## 作成日
2025年10月31日

