/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework;

import com.live2d.sdk.cubism.core.CubismCoreVersion;
import com.live2d.sdk.cubism.core.ICubismLogger;
import com.live2d.sdk.cubism.core.Live2DCubismCore;
import com.live2d.sdk.cubism.framework.CubismFrameworkConfig.LogLevel;
import com.live2d.sdk.cubism.framework.id.CubismIdManager;
import com.live2d.sdk.cubism.framework.rendering.android.CubismRendererAndroid;

import java.util.Locale;

import static com.live2d.sdk.cubism.framework.utils.CubismDebug.cubismLogInfo;
import static com.live2d.sdk.cubism.framework.utils.CubismDebug.cubismLogWarning;

/**
 * Entrypoint of Live2D Cubism Original Workflow SDK.
 * <p>
 * Beginning to use this Framework, call CubismFramework.initialize() method. Terminating the application, call CubismFramework.dispose() method.
 */
public class CubismFramework {
    /**
     * Inner class that define optional elements to be set in CubismFramework.
     */
    public static class Option {
        /**
         * Set the log output function.
         *
         * @param logger log output function
         */
        public void setLogFunction(ICubismLogger logger) {
            if (logger == null) {
                throw new IllegalArgumentException("logger is null.");
            }
            logFunction = logger;
        }

        /**
         * Functional interface of logging.
         */
        public ICubismLogger logFunction;
        /**
         * Log output level.
         * (Default value is OFF(Log outputting is not executed.))
         */
        public LogLevel loggingLevel = LogLevel.OFF;
    }

    /**
     * Offset value for mesh vertices
     */
    public static final int VERTEX_OFFSET = 0;
    /**
     * Step value for mesh vertices
     */
    public static final int VERTEX_STEP = 2;


    /**
     * Enable Cubism Framework API.
     * Required to run this method before using API.
     * Once prepared, if you run this again, the inner processes are skipped.
     *
     * @param option Option Class's instance
     * @return if preparing process has finished, return true
     */
    public static boolean startUp(final Option option) {
        if (s_isStarted) {
            cubismLogInfo("CubismFramework.startUp() is already done.");

            return s_isStarted;
        }

        s_option = option;

        if (s_option != null) {
            Live2DCubismCore.setLogger(option.logFunction);
        }

        s_isStarted = true;

        // Display the version information of Live2D Cubism Core.
        final CubismCoreVersion version = Live2DCubismCore.getVersion();

        cubismLogInfo(String.format(Locale.US, "Live2D Cubism Core version: %02d.%02d.%04d (%d)", version.getMajor(), version.getMinor(), version.getPatch(), version.getVersionNumber()));

        cubismLogInfo("CubismFramework.startUp() is complete.");

        return s_isStarted;
    }

    /**
     * Clear each parameter in CubismFramework initialized by startUp() method.
     * Use this method at reusing CubismFramework done dispose() method.
     */
    public static void cleanUp() {
        s_isStarted = false;
        s_isInitialized = false;
        s_option = null;
        s_cubismIdManager = null;
    }

    /**
     * Whether Cubism Framework API has been prepared already.
     *
     * @return if API has been already prepared, return true
     */
    public static boolean isStarted() {
        return s_isStarted;
    }

    /**
     * Initializing resources in Cubism Framework, the model is enabled to display.
     * If you would like to use initialize() method again, first you need to run dispose() method.
     */
    public static void initialize() {
        assert (s_isStarted);

        if (!s_isStarted) {
            cubismLogWarning("CubismFramework is not started.");
            return;
        }

        // Disturb consecutive securing resources.
        if (s_isInitialized) {
            cubismLogWarning("CubismFramework.initialize() skipped, already initialized.");
            return;
        }

        // ----- Static Release -----
        s_cubismIdManager = new CubismIdManager();
        s_isInitialized = true;

        cubismLogInfo("CubismFramework::Initialize() is complete.");
    }

    /**
     * Releases all resources in the Cubism Framework.
     */
    public static void dispose() {
        assert (s_isStarted);

        if (!s_isStarted) {
            cubismLogWarning("CubismFramework is not started.");
            return;
        }

        // If you use dispose() method, it is required to run initialize() method firstly.
        if (!s_isInitialized) {
            cubismLogWarning("CubismFramework.dispose() skipped, not initialized.");
            return;
        }

        //---- static release ----
        s_cubismIdManager = null;
        // Release static resources of renderer(cf. Shader programs)
        CubismRendererAndroid.staticRelease();

        s_isInitialized = false;

        cubismLogInfo("CubismFramework.dispose() is complete.");
    }

    /**
     * Whether having already initialized CubismFramework's resources.
     *
     * @return If securing resources have already done, return true
     */
    public static boolean isInitialized() {
        return s_isInitialized;
    }

    /**
     * Execute log function bound Core API
     *
     * @param message log message
     */
    public static void coreLogFunction(final String message) {
        if (Live2DCubismCore.getLogger() != null) {
            Live2DCubismCore.getLogger().print(message);
        }
    }

    /**
     * Return the current value of log output level setting.
     *
     * @return the current value of log output level setting
     */
    public static LogLevel getLoggingLevel() {
        if (s_option != null) {
            return s_option.loggingLevel;
        }
        return LogLevel.OFF;
    }

    /**
     * Get the instance of ID manager.
     *
     * @return CubismIdManager class's instance
     */
    public static CubismIdManager getIdManager() {
        return s_cubismIdManager;
    }

    /**
     * private constructor
     */
    private CubismFramework() {}

    /**
     * Flag whether the framework has been started or not.
     */
    private static boolean s_isStarted;
    /**
     * Flag whether the framework has been initialized or not.
     */
    private static boolean s_isInitialized;
    /**
     * Option object
     */
    private static Option s_option;
    /**
     * CubismIDManager object
     */
    private static CubismIdManager s_cubismIdManager;
}
