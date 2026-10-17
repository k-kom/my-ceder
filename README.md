# my-cedar

Cedar ポリシーによる認可判定を Clojure から利用する、小さなライブラリと実行サンプルです。
[cedar-java](https://github.com/cedar-policy/cedar-java) の Java binding を Clojure の Java interop から利用し、
同じ JVM プロセス内で JNI 経由で Cedar を呼び出します。AWS アカウントは不要です。

## 準備

- JDK 17 以上、Clojure CLI
- cedar-java 4.10.0（`deps.edn` の `com.cedarpolicy/cedar-java$uber` から自動取得）

`uber` classifier の JAR に同梱されたネイティブライブラリを自動ロードします。
同梱対象は macOS / Linux の aarch64・x86_64、Windows の x86_64 です。
macOS aarch64・JDK 25 で動作確認しています。
初回実行には Maven Central から依存を取得できるネットワーク環境が必要です。
`:run` と `:test` は JNI 用の `--enable-native-access=ALL-UNNAMED` を設定済みです。
組み込み先のアプリでも必要に応じて JVM オプションに指定してください。

```sh
clojure -M:run
# alice => allow
# bob => deny
clojure -M:test
```

## ライブラリとして使う

```clojure
(require '[my-cedar.core :as cedar])

(cedar/authorize
  "permit(principal == User::\"alice\", action == Action::\"read\", resource == Document::\"hello\");"
  []
  {:principal (cedar/entity "User" "alice")
   :action (cedar/entity "Action" "read")
   :resource (cedar/entity "Document" "hello")})
;; => {:decision :allow}
```

`allowed?` は同じ3引数で真偽値を返します。
エンティティと `:context` は Cedar の JSON 形式に対応する Clojure のマップで渡します。
エンティティ参照は `{:__entity {:type "User" :id "alice"}}` です。

## 構成と認可ルール

- `src/my_cedar/core.clj`: 認可 API。マップを Java の `AuthorizationRequest`・`Entities` に変換し、
  `PolicySet/parsePolicies` と `BasicAuthorizationEngine.isAuthorized` を呼びます。
- `resources/policies/documents.cedar`: 所有者だけが文書を読めるポリシー。ロック中は所有者も拒否します。
- `src/my_cedar/main.clj`: Alice と Bob の判定例。
- `test/`: `clojure.test` による実エンジンの統合テスト。

許可に一致しないリクエストは拒否されます。明示的な `forbid` は `permit` に優先します。
構文エラーと評価エラーは `ExceptionInfo` として呼び出し元に伝えます。
別のポリシーが許可していても、評価エラーがあれば例外になります。
JNI のロード失敗などの JVM エラーも呼び出し元に伝播します。
呼び出し側は例外時にアクセスを許可しないでください。

最小構成のため、ポリシーの永続化・管理 UI・スキーマ検証は含みません。
認可エンジンは遅延初期化して再利用し、ポリシーとエンティティは呼び出しごとにパースします。
認証済みユーザーと信頼できる保存先から principal・属性・ポリシーを組み立ててください。
