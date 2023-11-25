/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.utils;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.framework.CubismFrameworkConfig.LogLevel;

import static com.live2d.sdk.cubism.framework.CubismFrameworkConfig.CSM_LOG_LEVEL;

/**
 * A utility class for debugging.
 * <p>
 * Log output, dump byte, and so on.
 */
public class CubismDebug {
    /**
     * Output log. Set log level to 1st argument.
     * At using {@link CubismFramework#initialize()} function, if the log level is lower than set log output level, log output is not executed.
     *
     * @param logLevel log level setting
     * @param message format string
     * @param args variadic arguments
     */
    public static void print(final LogLevel logLevel, final String message, Object... args) {
        // If the log level is lower than set log output level in Option class, log outputting is not executed.
        if (logLevel.getId() < CubismFramework.getLoggingLevel().getId()) {
            return;
        }

        String format = String.format(message, args);
        CubismFramework.coreLogFunction(format);
    }

    /**
     * Dump out a specified length of data.
     * <p>
     * If the log output level is below the level set in the option at {@link CubismFramework#initialize()}, it will not be logged.
     *
     * @param logLevel setting of log level
     * @param data data to dump
     * @param length length of dumping
     */
    public static void dumpBytes(final LogLevel logLevel, final byte[] data, int length) {
        for (int i = 0; i < length; i++) {
            if (i % 16 == 0 && i > 0) {
                print(logLevel, "\n");
            } else if (i % 8 == 0 && i > 0) {
                print(logLevel, " ");
            }
            print(logLevel, "%02X", (data[i] & 0xFF));
        }
    }

    /**
     * Display the normal message.
     *
     * @param message message
     */
    public static void cubismLogPrint(LogLevel logLevel, String message, Object... args) {
        print(logLevel, "[CSM]" + message, args);
    }

    /**
     * Display a newline message.
     *
     * @param message message
     */
    public static void cubismLogPrintln(LogLevel logLevel, String message, Object... args) {
        cubismLogPrint(logLevel, message + "\n", args);
    }

    /**
     * Show detailed message.
     *
     * @param message message
     */
    public static void cubismLogVerbose(String message, Object... args) {
        if (CSM_LOG_LEVEL.getId() <= LogLevel.VERBOSE.getId()) {
            cubismLogPrintln(LogLevel.VERBOSE, "[V]" + message, args);
        }
    }

    /**
     * Display the debug message.
     *
     * @param message message
     */
    public static void cubismLogDebug(String message, Object... args) {
        if (CSM_LOG_LEVEL.getId() <= LogLevel.DEBUG.getId()) {
            cubismLogPrintln(LogLevel.DEBUG, "[D]" + message, args);
        }
    }

    /**
     * Display informational messages.
     *
     * @param message message
     */
    public static void cubismLogInfo(String message, Object... args) {
        if (CSM_LOG_LEVEL.getId() <= LogLevel.INFO.getId()) {
            cubismLogPrintln(LogLevel.INFO, "[I]" + message, args);
        }
    }

    /**
     * Display a warning message.
     *
     * @param message message
     */
    public static void cubismLogWarning(String message, Object... args) {
        if (CSM_LOG_LEVEL.getId() <= LogLevel.WARNING.getId()) {
            cubismLogPrintln(LogLevel.WARNING, "[W]" + message, args);
        }
    }

    /**
     * Display a error message.
     *
     * @param message message.
     */
    public static void cubismLogError(String message, Object... args) {
        if (CSM_LOG_LEVEL.getId() <= LogLevel.ERROR.getId()) {
            cubismLogPrintln(LogLevel.ERROR, "[E]" + message, args);
        }
    }

    /**
     * Private constructor
     */
    private CubismDebug() {}
}
