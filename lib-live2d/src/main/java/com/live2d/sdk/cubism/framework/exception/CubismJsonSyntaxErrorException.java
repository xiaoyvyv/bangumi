/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.exception;

/**
 * The exception class which reports that there is a syntax error in the parsed JSON string.
 */
public class CubismJsonSyntaxErrorException extends CubismJsonParseException {
    /**
     * Construct the exception which has the default message.
     */
    public CubismJsonSyntaxErrorException() {
        super(DEFAULT_MESSAGE);
    }
    
    /**
     * Construct the exception which has the specified message.
     */
    public CubismJsonSyntaxErrorException(String msg) {
        super(DEFAULT_MESSAGE + msg);
    }
    
    /**
     * Create exception with the specified message and the line number at syntax error.
     *
     * @param msg        error message describing what happened.
     * @param lineNumber line number at syntax error
     */
    public CubismJsonSyntaxErrorException(String msg, int lineNumber) {
        super(DEFAULT_MESSAGE + msg, lineNumber);
//        String errorPoint = "line " + lineNumber + "\n";
//        super(errorPoint + msg)
    }
    
    public CubismJsonSyntaxErrorException(String msg, Throwable cause) {
        super(DEFAULT_MESSAGE + msg, cause);
    }
    
    public CubismJsonSyntaxErrorException(Throwable cause) {
        super(cause);
    }
    
    private static final String DEFAULT_MESSAGE = "SyntaxError: ";
}
