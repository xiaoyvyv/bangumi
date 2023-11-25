/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.exception;

/**
 * CubismJsonに関連する実行時例外
 */
public class CubismJsonRuntimeException extends CubismRuntimeException {
    /**
     * エラーメッセージを持たない実行時例外を構築する
     */
    public CubismJsonRuntimeException() {
        super("");
    }
    
    /**
     * 指定されたエラーメッセージを持つ例外を構築する
     *
     * @param msg エラーメッセージ
     */
    public CubismJsonRuntimeException(String msg) {
        super(msg);
    }
}
