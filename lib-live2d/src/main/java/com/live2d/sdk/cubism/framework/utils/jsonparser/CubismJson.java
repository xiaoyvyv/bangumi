/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.utils.jsonparser;

import com.live2d.sdk.cubism.framework.exception.CubismJsonParseException;
import com.live2d.sdk.cubism.framework.exception.CubismJsonSyntaxErrorException;

import java.io.IOException;

/**
 * This class has some functions related to JSON.
 */
public class CubismJson {
    /**
     * Creates the JSON object.
     *
     * @param buffer byte data of the JSON
     * @return JSON object
     *
     * @throws IllegalArgumentException If the argument is null
     */
    public static CubismJson create(byte[] buffer) {
        if (buffer == null || buffer.length == 0) {
            throw new IllegalArgumentException("Parsed JSON data is empty.");
        }

        CubismJson json = new CubismJson();
        json.parse(buffer);

        return json;
    }

    /**
     * Get a root of a parsed JSON.
     *
     * @return JSON root
     */
    public ACubismJsonValue getRoot() {
        return root;
    }

    /**
     * Private constructor
     */
    private CubismJson() {}

    /**
     * Parse JSON string.
     *
     * @param buffer JSON byte data
     */
    private void parse(byte[] buffer) {
        try {
            String json = new String(buffer, "UTF-8");
            lexer = new CubismJsonLexer(json);

            token = lexer.getNextToken();
            root = createValue();
        } catch (IOException e) {
            throw new CubismJsonParseException("It seems that an error has occured in the input/output processing", e);
        }
    }

    /**
     * Construct a JSON value.
     *
     * @return JSON Value
     */
    private ACubismJsonValue createValue() throws CubismJsonParseException, IOException {
        // JSON Object
        if (token.getTokenType() == CubismJsonToken.TokenType.LBRACE) {
            objectNestingLevel++;
            CubismJsonObject object = createObject();

            // If parsing is midway, the next token is read.
            if (objectNestingLevel != 0 || arrayNestingLevel != 0) {
                token = lexer.getNextToken();
            }
            return object;
        }
        // JSON Array
        else if (token.getTokenType() == CubismJsonToken.TokenType.LSQUARE_BRACKET) {
            arrayNestingLevel++;
            CubismJsonArray array = createArray();

            // If parsing is midway, the next token is read.
            if (objectNestingLevel != 0 || arrayNestingLevel != 0) {
                token = lexer.getNextToken();
            }
            return array;
        }
        // JSON Number
        else if (token.getTokenType() == CubismJsonToken.TokenType.NUMBER) {
            CubismJsonNumber number = CubismJsonNumber.valueOf(token.getNumberValue());

            if (objectNestingLevel != 0 || arrayNestingLevel != 0) {
                token = lexer.getNextToken();
            }
            return number;
        }
        // JSON String
        else if (token.getTokenType() == CubismJsonToken.TokenType.STRING) {
            CubismJsonString string = CubismJsonString.valueOf(token.getStringValue());

            if (objectNestingLevel != 0 || arrayNestingLevel != 0) {
                token = lexer.getNextToken();
            }
            return string;
        }
        // JSON Boolean(true or false)
        else if (token.getTokenType() == CubismJsonToken.TokenType.BOOLEAN) {
            CubismJsonBoolean bool = CubismJsonBoolean.valueOf(token.getBooleanValue());

            if (objectNestingLevel != 0 || arrayNestingLevel != 0) {
                token = lexer.getNextToken();
            }
            return bool;
        }
        // JSON null value
        else if (token.getTokenType() == CubismJsonToken.TokenType.NULL) {
            CubismJsonNullValue nullValue = new CubismJsonNullValue();

            if (objectNestingLevel != 0 || arrayNestingLevel != 0) {
                token = lexer.getNextToken();
            }
            return nullValue;
        } else {
            throw new CubismJsonSyntaxErrorException("Incorrect JSON format.", lexer.getCurrentLineNumber() - 1);
        }
    }

    /**
     * Construct a JSON object
     *
     * @return JSON Object
     */
    private CubismJsonObject createObject() throws CubismJsonParseException, IOException {
        CubismJsonObject object = new CubismJsonObject();

        token = lexer.getNextToken();

        // If the next token is braces, this object is regarded as empty object
        if (token.getTokenType() == CubismJsonToken.TokenType.RBRACE) {
            objectNestingLevel--;
            return object;
        } else {
            // Continue reading until closed by '}'
            // If the format is not "string : value (, string : value, ...)", an exception is thrown.
            while (true) {
                CubismJsonString string;
                ACubismJsonValue value;

                // Construct a string value
                if (token.getTokenType() == CubismJsonToken.TokenType.STRING) {
                    string = CubismJsonString.valueOf(token.getStringValue());
                } else {
                    throw new CubismJsonSyntaxErrorException("JSON Object's format is incorrect.", lexer.getCurrentLineNumber());
                }

                token = lexer.getNextToken();

                // If it is not divided by colon, an exception is thrown.
                if (token.getTokenType() != CubismJsonToken.TokenType.COLON) {
                    throw new CubismJsonSyntaxErrorException("JSON Object's format is incorrect.", lexer.getCurrentLineNumber());
                }

                token = lexer.getNextToken();
                value = createValue();

                // Put a pair of string and value into object
                object.putPair(string, value);

                // If the next token is comma, reading is continued. If the next token is '}', it is done to "break".
                if (token.getTokenType() == CubismJsonToken.TokenType.RBRACE) {
                    objectNestingLevel--;
                    break;
                } else if (token.getTokenType() == CubismJsonToken.TokenType.COMMA) {
                    token = lexer.getNextToken();
                } else {
                    throw new CubismJsonSyntaxErrorException("JSON Object's format is incorrect.", lexer.getCurrentLineNumber() - 1);
                }
            }
        }
        return object;
    }

    /**
     * Construct a JSON array.
     *
     * @return JSON Array
     *
     * @throws CubismJsonParseException an exception related to parsing
     */
    private CubismJsonArray createArray() throws CubismJsonParseException, IOException {
        CubismJsonArray array = new CubismJsonArray();

        token = lexer.getNextToken();

        // If the next token is square brackets, this array is regarded as empty array.
        if (token.getTokenType() == CubismJsonToken.TokenType.RSQUARE_BRACKET) {
            arrayNestingLevel--;
            return array;
        } else {
            // Continue reading until closed by ']'
            // If the format is not "value (, value, ...)", an exception is thrown.
            while (true) {
                ACubismJsonValue value;

                // Construct a value
                value = createValue();
                // Put the value into array
                array.putValue(value);

                // If the next token is comma, reading is continued. If the next token is ']', it is done to "break".
                if (token.getTokenType() == CubismJsonToken.TokenType.RSQUARE_BRACKET) {
                    arrayNestingLevel--;
                    break;
                } else if (token.getTokenType() == CubismJsonToken.TokenType.COMMA) {
                    token = lexer.getNextToken();
                } else {
                    throw new CubismJsonSyntaxErrorException("JSON Array's format is incorrect.", lexer.getCurrentLineNumber() - 1);
                }
            }
        }
        return array;
    }

    /**
     * JSON root
     */
    private ACubismJsonValue root;
    /**
     * JSON lexer
     */
    private CubismJsonLexer lexer;
    /**
     * JSON token
     */
    private CubismJsonToken token;
    /**
     * A nest level of JSON object. If left brace is appeared, nest level increases by 1, and if right brace is done, it decreases by 1.
     */
    private int objectNestingLevel;

    private int arrayNestingLevel;
}
