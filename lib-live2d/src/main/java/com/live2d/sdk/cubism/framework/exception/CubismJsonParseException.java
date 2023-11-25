/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.exception;

/**
 * CubismJsonに関連する例外
 */
public class CubismJsonParseException extends CubismRuntimeException {
    /**
     * エラーメッセージを持たない例外を構築する
     */
    public CubismJsonParseException() {
        super(DEFAULT_MESSAGE);
    }
    
    /**
     * 指定されたエラーメッセージを持つ例外を構築する
     *
     * @param msg エラーメッセージ
     */
    public CubismJsonParseException(String msg) {
        super(DEFAULT_MESSAGE + msg);
    }
    
    public CubismJsonParseException(String msg, int lineNumber) {
        super("line " + lineNumber + "\n" + DEFAULT_MESSAGE + msg);
    }
    
    
    public CubismJsonParseException(String msg, Throwable cause) {
        super(DEFAULT_MESSAGE + msg, cause);
    }
    
    public CubismJsonParseException(Throwable cause) {
        super(cause);
    }
    
    private static final String DEFAULT_MESSAGE = "Failed to parse the JSON data. ";
}
