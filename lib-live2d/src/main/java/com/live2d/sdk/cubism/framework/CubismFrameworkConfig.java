package com.live2d.sdk.cubism.framework;

/**
 * CubismFrameworkで使用される定数の定義クラス。<br>
 * デバッグやログに関わる設定をデフォルトから変更したい場合は、このクラスの定数の値を書き換えること。
 */
public class CubismFrameworkConfig {
    /**
     * ログ出力レベルを定義する列挙体。
     */
    public enum LogLevel {
        /**
         * 詳細ログ出力設定
         */
        VERBOSE(0),
        /**
         * デバッグログ出力設定
         */
        DEBUG(1),
        /**
         * Infoログ出力設定
         */
        INFO(2),
        /**
         * 警告ログ出力設定
         */
        WARNING(3),
        /**
         * エラーログ出力設定
         */
        ERROR(4),
        /**
         * ログ出力オフ設定
         */
        OFF(5);

        private final int id;

        LogLevel(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    /**
     * Cubism SDKにおけるデバッグ機能の有効状態。trueなら有効。
     */
    public static final boolean CSM_DEBUG = true;

    /**
     * ログ出力設定。<br>
     * 強制的にログ出力レベルを変える時に定義を有効にする。
     *
     * @note LogLevel.VERBOSE ～ LogLevel.OFF のいずれかを指定する。
     */
    public static final LogLevel CSM_LOG_LEVEL = LogLevel.VERBOSE;

    /**
     * privateコンストラクタ。
     *
     * @note 定数クラスのためインスタンス化させない
     */
    private CubismFrameworkConfig() {}
}
