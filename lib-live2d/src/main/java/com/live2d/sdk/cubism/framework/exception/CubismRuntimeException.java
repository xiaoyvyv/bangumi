/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.exception;

/**
 * Cubism SDK専用の実行時例外
 */
public class CubismRuntimeException extends RuntimeException {
    /**
     * エラーメッセージを持つ実行時例外を構築する
     *
     * @param msg エラーメッセージ
     */
    public CubismRuntimeException(String msg) {
        super(msg);
    }

    /**
     * Creates exception with the specified message and cause.
     *
     * @param msg error message describeing what happened.
     * @param cause cause root exception that caused this exception to be thrown.
     */
    public CubismRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Create exception with the specified cause. Consider using {@link #CubismRuntimeException(String, Throwable)} instead if you can describe what happened.
     *
     * @param cause cause root exception that caused this exception to be thrown.
     */
    public CubismRuntimeException(Throwable cause) {
        super(cause);
    }
}
