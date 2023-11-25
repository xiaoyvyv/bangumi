/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.utils.jsonparser;

/**
 * This class expresses JSON Tokens.
 */
class CubismJsonToken {
    /**
     * Token types
     */
    public enum TokenType {
        NUMBER, // ex) 0, 1.0, -1.2e+3
        STRING, // ex) "test"
        BOOLEAN, // 'true' or 'false'
        LBRACE,  // '{'
        RBRACE,  // '}'
        LSQUARE_BRACKET,  // '['
        RSQUARE_BRACKET,  // ']'
        COMMA,
        COLON,
        NULL, // JSON null value
    }

    /**
     * Construct a CubismJsonToken by specifying only the token type.
     *
     * @param type token type
     */
    public CubismJsonToken(TokenType type) {
        tokenType = type;
    }

    /**
     * Construct a string token.
     *
     * @param value string value
     */
    public CubismJsonToken(String value) {
        tokenType = TokenType.STRING;
        stringValue = value;
    }

    /**
     * Construct a number token.
     *
     * @param value number value
     */
    public CubismJsonToken(double value) {
        tokenType = TokenType.NUMBER;
        numberValue = value;
    }


    /**
     * Construct a boolean token.
     *
     * @param value boolean value
     */
    public CubismJsonToken(boolean value) {
        tokenType = TokenType.BOOLEAN;
        booleanValue = value;
    }

    /**
     * Construct a null token.
     */
    public CubismJsonToken() {
        tokenType = TokenType.NULL;
    }

    /**
     * Get the type of token.
     *
     * @return token type
     */
    public TokenType getTokenType() {
        return tokenType;
    }

    /**
     * Get string value.
     *
     * @return string value
     */
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * Get number value.
     *
     * @return number value
     */
    public double getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(double numberValue) {
        this.numberValue = numberValue;
    }

    /**
     * Get boolean value.
     *
     * @return boolean value
     */
    public boolean getBooleanValue() {
        return booleanValue;
    }

    /**
     * Token type
     */
    private final TokenType tokenType;
    /**
     * String value
     */
    private String stringValue;
    /**
     * Number value
     */
    private double numberValue;
    /**
     * Boolean value
     */
    private boolean booleanValue;
}
