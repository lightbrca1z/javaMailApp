# MySQL起動手順（macOS）

## エラー内容
```
Communications link failure
Connection refused
💡 解決のヒント: MySQLサーバーが起動していない、またはネットワーク接続に問題があります
```

このエラーは、MySQLサーバーが起動していないために発生しています。

## MySQLの起動方法

お使いのMySQLのインストール方法によって、起動方法が異なります。

### 方法1: MAMPを使用している場合（最も一般的）

1. **MAMPアプリケーションを起動**
   - アプリケーションフォルダから`MAMP`を起動

2. **"Start"ボタンをクリック**
   - Apache & MySQLの両方が起動します
   - ステータスランプが緑色になることを確認

3. **MySQLのポート番号を確認**
   - デフォルトは`8889`ですが、プロジェクトでは`3306`を使用
   - MAMPの設定で`3306`に変更するか、`DatabaseUtil.java`を修正

#### MAMPのポート設定変更（推奨）
```
MAMP → Preferences → Ports → MySQL Port: 3306
```

または、`DatabaseUtil.java`を修正：
```java
// 8889ポートを使用する場合
private static final String URL = "jdbc:mysql://localhost:8889/mailsendservlet?useSSL=false&serverTimezone=UTC";
```

### 方法2: Homebrewでインストールした場合

#### MySQLの起動
```bash
# MySQLサービスを起動
brew services start mysql

# または、一時的に起動
mysql.server start
```

#### MySQLの状態確認
```bash
# サービスの状態を確認
brew services list

# または
mysql.server status
```

#### MySQLへの接続テスト
```bash
# MySQLに接続
mysql -u root -p

# パスワードを入力（デフォルトは空の場合が多い）
```

### 方法3: 公式MySQLをインストールした場合

#### システム環境設定から起動
```
1. システム環境設定を開く
2. 一番下の「MySQL」をクリック
3. 「Start MySQL Server」ボタンをクリック
```

#### コマンドラインから起動
```bash
sudo /usr/local/mysql/support-files/mysql.server start
```

## データベースの初期化

MySQLが起動したら、データベースを作成します：

```bash
# MySQLに接続
mysql -u root -p

# データベースの確認
SHOW DATABASES;

# mailsendservletデータベースが無い場合
# プロジェクトルートのSQLファイルを実行
exit

# SQLファイルを実行
mysql -u root -p < /Applications/Eclipse_2025-09.app/Contents/workspace/javaMailApp-javaMailApp/mailsendservlet.sql
```

## トラブルシューティング

### エラー: Access Denied

パスワードが間違っている可能性があります。

**解決方法:**
1. `DatabaseUtil.java`のパスワードを確認
2. MySQLのパスワードをリセット（必要な場合）

### エラー: Unknown database 'mailsendservlet'

データベースが作成されていません。

**解決方法:**
```bash
mysql -u root -p < mailsendservlet.sql
```

### ポート番号が違う

**現在の設定:** `localhost:3306`

**MAMPのデフォルト:** `localhost:8889`

**解決方法1:** MAMPのポートを3306に変更

**解決方法2:** `DatabaseUtil.java`を修正
```java
private static final String URL = "jdbc:mysql://localhost:8889/mailsendservlet?useSSL=false&serverTimezone=UTC";
```

## 確認方法

1. **ターミナルでMySQLに接続できるか確認**
```bash
mysql -u root -p
```

2. **データベースが存在するか確認**
```sql
SHOW DATABASES;
USE mailsendservlet;
SHOW TABLES;
```

3. **Eclipseでプロジェクトを再起動**
   - サーバーを停止
   - プロジェクトをクリーン
   - サーバーを再起動

4. **ブラウザでアクセス**
```
http://localhost:8080/MailSendServlet/
```

## 成功のサイン

コンソールに以下のログが表示されれば成功：
```
✅ JDBC Driver loaded successfully in static block
✅ JDBC Driver verified in getConnection()
✅ データベース接続成功: jdbc:mysql://localhost:3306/mailsendservlet?useSSL=false&serverTimezone=UTC
```

## 参考情報

### MySQL起動状態の確認（各種方法）

```bash
# Homebrewの場合
brew services list | grep mysql

# mysql.serverの場合
mysql.server status

# プロセスの確認
ps aux | grep mysql

# ポート3306が使用されているか確認
lsof -i :3306

# ポート8889が使用されているか確認（MAMP）
lsof -i :8889
```

### よく使うMySQLコマンド

```bash
# 起動
brew services start mysql
# または
mysql.server start
# または (MAMP)
/Applications/MAMP/Library/bin/mysql.server start

# 停止
brew services stop mysql
# または
mysql.server stop

# 再起動
brew services restart mysql
# または
mysql.server restart
```

---

**注意:** MAMPを使用している場合は、MAMPアプリから起動するのが最も簡単で確実です。

