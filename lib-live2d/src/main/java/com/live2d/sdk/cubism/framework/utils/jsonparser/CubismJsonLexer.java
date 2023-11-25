/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */


package com.live2d.sdk.cubism.framework.utils.jsonparser;

import com.live2d.sdk.cubism.framework.exception.CubismJsonParseException;

import java.io.IOException;
import java.util.Arrays;

/**
 * This class offers a function of JSON lexer.
 */
class CubismJsonLexer {
    /**
     * Package-private constructor
     *
     * @param json string of JSON
     */
    public CubismJsonLexer(String json) {
        // 上位層で、nullだったら例外を出しているため、
        // 引数がnullであることは考えられない
        assert json != null;

        // char配列に変換する
        jsonChars = json.toCharArray();
        jsonCharsLength = jsonChars.length;

        // トークン解析用のバッファを初期化
        // 初期容量は128。128文字を超えるトークンが出現するならばその都度拡張する。
        parsedTokonBuffer = new char[MINIMUM_CAPACITY];
    }

    /**
     * Get a next token.
     */
    public CubismJsonToken getNextToken() throws CubismJsonParseException, IOException {
        // Skip blank characters
        while (isWhiteSpaceChar(nextChar)) {
            updateNextChar();
        }

        // null文字で埋める
        Arrays.fill(parsedTokonBuffer, 0, bufferIndex, '\0');
        bufferIndex = 0;

        // A Number token
        // A process when beginning at minus sign
        if (nextChar == '-') {
            append('-');
            updateNextChar();

            if (Character.isDigit(nextChar)) {
                buildNumber();
                String numberStr = String.copyValueOf(parsedTokonBuffer, 0, bufferIndex);
                NUMBER.setNumberValue(Double.parseDouble(numberStr));

                return NUMBER;
            } else {
                throw new CubismJsonParseException("Number's format is incorrect.", lineNumber);
            }
        }
        // A process when beginning at a number except 0.
        else if (Character.isDigit(nextChar)) {
            buildNumber();
            String numberStr = String.copyValueOf(parsedTokonBuffer, 0, bufferIndex);
            NUMBER.setNumberValue(Double.parseDouble(numberStr));

            return NUMBER;
        }
        // true
        else if (nextChar == 't') {
            append(nextChar);
            updateNextChar();

            for (int i = 0; i < 3; i++) {
                append(nextChar);
                updateNextChar();
            }

            // If "value" does not create true value, send an exception.
            String trueString = String.copyValueOf(parsedTokonBuffer, 0, bufferIndex);
            if (!trueString.equals("true")) {
                throw new CubismJsonParseException("Boolean's format or spell is incorrect.", lineNumber);
            }
            return TRUE;
        }
        // false
        else if (nextChar == 'f') {
            append(nextChar);
            updateNextChar();

            for (int i = 0; i < 4; i++) {
                append(nextChar);
                updateNextChar();
            }

            // If the value does not equals to "false" value, send the exception.
            String falseString = String.copyValueOf(parsedTokonBuffer, 0, bufferIndex);
            if (!falseString.equals("false")) {
                throw new CubismJsonParseException("Boolean's format or spell is incorrect.", lineNumber);
            }
            return FALSE;
        }
        // null
        else if (nextChar == 'n') {
            append(nextChar);
            updateNextChar();

            for (int i = 0; i < 3; i++) {
                append(nextChar);
                updateNextChar();
            }

            // If the JSON value does not equal to the "null" value, send an exception.
            String nullString = String.copyValueOf(parsedTokonBuffer, 0, bufferIndex);
            if (!nullString.equals("null")) {
                throw new CubismJsonParseException("JSON Null's format or spell is incorrect.", lineNumber);
            }
            return NULL;
        } else if (nextChar == '{') {
            updateNextChar();
            return LBRACE;
        } else if (nextChar == '}') {
            updateNextChar();
            return RBRACE;
        } else if (nextChar == '[') {
            updateNextChar();
            return LSQUARE_BRACKET;
        } else if (nextChar == ']') {
            updateNextChar();
            return RSQUARE_BRACKET;
        }
        // If next character is double quote, string token is created.
        else if (nextChar == '"') {
            updateNextChar();

            // Until closing by double quote("), it is continued to read.
            while (nextChar != '"') {
                // Consider a escape sequence.
                if (nextChar == '\\') {
                    updateNextChar();
                    buildEscapedString();
                } else {
                    append(nextChar);
                }
                updateNextChar();
            }
            updateNextChar();
            STRING.setStringValue(String.valueOf(parsedTokonBuffer, 0, bufferIndex));

            return STRING;
        }
        // Colon(:)
        else if (nextChar == ':') {
            updateNextChar();
            return COLON;
        }
        // Comma(,)
        else if (nextChar == ',') {
            updateNextChar();
            return COMMA;
        }
        throw new CubismJsonParseException("The JSON is not closed properly, or there is some other malformed form.", lineNumber);
    }

    /**
     * Return current line number.
     *
     * @return current line number
     */
    public int getCurrentLineNumber() {
        return lineNumber;
    }

    /**
     * Build number string.
     *
     * @throws CubismJsonParseException the exception at failing to parse
     */
    private void buildNumber() throws CubismJsonParseException {
        if (nextChar == '0') {
            append(nextChar);
            updateNextChar();
            buildDoubleOrExpNumber();
        } else {
            append(nextChar);
            updateNextChar();

            // Repeat processes until appearing a character except dot, exponential expression or number.
            while (Character.isDigit(nextChar)) {
                append(nextChar);
                updateNextChar();
            }
            buildDoubleOrExpNumber();
        }
    }

    /**
     * Build double or exponential number.
     *
     * @throws CubismJsonParseException the exception at failing to parse
     */
    private void buildDoubleOrExpNumber() throws CubismJsonParseException {
        // If the next character is dot, floating point number is created.
        if (nextChar == '.') {
            buildDoubleNumber();
        }
        // If there is an e or E, it is considered an exponential expression.
        if (nextChar == 'e' || nextChar == 'E') {
            buildExponents();
        }
    }

    /**
     * Return floating point number as strings(StringBuilder).
     *
     * @throws CubismJsonParseException the exception at failing to parse
     */
    private void buildDoubleNumber() throws CubismJsonParseException {
        append('.');
        updateNextChar();

        // If the character following dot sign is not a number, an exception is thrown.
        if (!Character.isDigit(nextChar)) {
            throw new CubismJsonParseException("Number's format is incorrect.", lineNumber);
        }
        do {
            append(nextChar);
            updateNextChar();
        } while (Character.isDigit(nextChar));
    }

    /**
     * Build a number string used an exponential expression.
     *
     * @throws CubismJsonParseException the exception at failing to parse
     */
    private void buildExponents() throws CubismJsonParseException {
        append(nextChar);
        updateNextChar();

        // Handle cases where a number is preceded by a sign.
        if (nextChar == '+') {
            append(nextChar);
            updateNextChar();
        } else if (nextChar == '-') {
            append(nextChar);
            updateNextChar();
        }
        // If the character is not a number or a sign, an exception is thrown.
        if (!Character.isDigit(nextChar)) {
            throw new CubismJsonParseException(String.copyValueOf(parsedTokonBuffer, 0, bufferIndex) + "\n: " + "Exponent value's format is incorrect.", lineNumber);
        }

        do {
            append(nextChar);
            updateNextChar();
        } while (Character.isDigit(nextChar));
    }

    /**
     * Build a string used an escape sequence.
     *
     * @throws CubismJsonParseException the exception at failing to parse
     */
    private void buildEscapedString() throws CubismJsonParseException {
        switch (nextChar) {
            case '"':
            case '\\':
            case '/':
                append(nextChar);
                break;
            case 'b':
                append('\b');
                break;
            case 'f':
                append('\f');
                break;
            case 'n':
                append('\n');
                break;
            case 'r':
                append('\r');
                break;
            case 't':
                append('\t');
                break;
            case 'u': {
                // バッファをクリアする
                bufferForHexadecimalString.delete(0, 16);

                bufferForHexadecimalString.append('\\');
                bufferForHexadecimalString.append('u');
                for (int i = 0; i < 4; i++) {
                    updateNextChar();
                    bufferForHexadecimalString.append(nextChar);
                }
                // Check whether it is hex number. If there is a problem, an exception is thrown.
                String tmp = bufferForHexadecimalString.toString();
                if (!tmp.matches("\\\\u[a-fA-F0-9]{4}")) {
                    throw new CubismJsonParseException(bufferForHexadecimalString + "\n: " + "The unicode notation is incorrect.", lineNumber);
                }

                for (int i = 0; i < tmp.length(); i++) {
                    append(tmp.charAt(i));
                }
                break;
            }
        }
    }

    /**
     * {@code buildEscapedString}の16進数の文字コードをパースする箇所で使用されるバッファ。
     */
    private static final StringBuffer bufferForHexadecimalString = new StringBuffer();

    /**
     * Whether a character is white space character.
     *
     * @param c checked character
     * @return If the character is white space character, return true
     */
    private boolean isWhiteSpaceChar(char c) {
        return (c == ' ' || c == '\r' || c == '\n' || c == '\t');
    }

    /**
     * Read a next character
     */
    private void updateNextChar() {
        // 文字を全部読んだら、次の文字をnull文字にセットしてreturnする
        if (charIndex >= jsonCharsLength) {
            nextChar = '\0';
            return;
        }

        nextChar = jsonChars[charIndex];
        charIndex++;

        // 改行コードがあれば行数をインクリメントする
        if (nextChar == '\n') {
            lineNumber++;
        }
    }

    /**
     * Tokonのパース用の文字列バッファに、引数で指定された文字リテラルを追加する。
     *
     * @param c 追加する文字リテラル
     */
    private void append(char c) {
        // Tokenをパースするためのバッファがいっぱいになったら、バッファサイズを2倍にする
        if (bufferLength == bufferIndex) {
            bufferLength *= 2;
            char[] tmp = new char[bufferLength];
            System.arraycopy(parsedTokonBuffer, 0, tmp, 0, bufferIndex);

            parsedTokonBuffer = tmp;
        }
        parsedTokonBuffer[bufferIndex] = c;
        bufferIndex++;
    }

    // Tokenを都度生成せずに定数として保持する
    /**
     * 左波カッコ'{'のトークン
     */
    private static final CubismJsonToken LBRACE = new CubismJsonToken(CubismJsonToken.TokenType.LBRACE);
    /**
     * 右波カッコ'}'のトークン
     */
    private static final CubismJsonToken RBRACE = new CubismJsonToken(CubismJsonToken.TokenType.RBRACE);
    /**
     * 左角カッコ'['のトークン
     */
    private static final CubismJsonToken LSQUARE_BRACKET = new CubismJsonToken(CubismJsonToken.TokenType.LSQUARE_BRACKET);
    /**
     * 左角カッコ'['のトークン
     */
    private static final CubismJsonToken RSQUARE_BRACKET = new CubismJsonToken(CubismJsonToken.TokenType.RSQUARE_BRACKET);
    /**
     * コロン':'のトークン
     */
    private static final CubismJsonToken COLON = new CubismJsonToken(CubismJsonToken.TokenType.COLON);
    /**
     * カンマ','のトークン
     */
    private static final CubismJsonToken COMMA = new CubismJsonToken(CubismJsonToken.TokenType.COMMA);
    /**
     * 真偽値'true'のトークン
     */
    private static final CubismJsonToken TRUE = new CubismJsonToken(true);
    /**
     * 真偽値'false'のトークン
     */
    private static final CubismJsonToken FALSE = new CubismJsonToken(false);
    /**
     * 'null'のトークン
     */
    private static final CubismJsonToken NULL = new CubismJsonToken();

    // 中の値を書き換えて使用する
    /**
     * 文字列のトークン
     */
    private static final CubismJsonToken STRING = new CubismJsonToken("");
    /**
     * 数値のトークン
     */
    private static final CubismJsonToken NUMBER = new CubismJsonToken(0.0);

    /**
     * jsonChars配列の初期サイズ。
     * これを超えるトークンが出現した場合はサイズを2倍に拡張する。
     */
    private static final int MINIMUM_CAPACITY = 128;

    /**
     * パースするJSON文字列
     */
    private final char[] jsonChars;
    /**
     * 現在読んでいる文字のインデックス
     */
    private int charIndex;
    /**
     * パースするJSON文字列の文字数
     */
    private final int jsonCharsLength;
    /**
     * 行数。改行文字が出てくるたびにインクリメントされる。
     */
    private int lineNumber = 1;

    /**
     * the next character
     */
    private char nextChar = ' ';

    /**
     * トークンのパース時に使用されるバッファ
     */
    private char[] parsedTokonBuffer;
    /**
     * {@code parsedTokonBuffer}の最後尾のインデックス
     */
    private int bufferIndex;
    /**
     * {@code parsedTokonBuffer}の容量
     */
    private int bufferLength = MINIMUM_CAPACITY;
}
