/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.xiaoyv.common.widget.musume;

import android.util.Log;

import com.live2d.sdk.cubism.core.ICubismLogger;

import java.io.IOException;
import java.io.InputStream;

public class LAppPal {

    private static double s_currentFrame;
    private static double _lastNanoTime;
    private static double _deltaNanoTime;

    private static final String TAG = "[Robot]";

    private LAppPal() {
    }

    /**
     * 用于在CubismFramework日志功能中注册的日志函数类。
     */
    public static class PrintLogFunction implements ICubismLogger {
        @Override
        public void print(String message) {
            Log.d(TAG, message);
        }
    }

    // 将应用程序移至后台。执行此操作将触发onPause()事件。
    public static void moveTaskToBack() {
        // LAppDelegate.getInstance().getActivity().moveTaskToBack(true);
    }

    // 更新增量时间
    public static void updateTime() {
        s_currentFrame = getSystemNanoTime();
        _deltaNanoTime = s_currentFrame - _lastNanoTime;
        _lastNanoTime = s_currentFrame;
    }

    // 作为字节序列读取文件
    public static byte[] loadFileAsBytes(final String path) {
        InputStream fileData = null;
        try {
            fileData = LAppDelegate.getInstance().getActivity().getAssets().open(path);

            int fileSize = fileData.available();
            byte[] fileBuffer = new byte[fileSize];
            fileData.read(fileBuffer, 0, fileSize);

            return fileBuffer;
        } catch (IOException e) {
            e.printStackTrace();

            if (LAppDefine.DEBUG_LOG_ENABLE) {
                printLog("文件打开错误。");
            }

            return new byte[0];
        } finally {
            try {
                if (fileData != null) {
                    fileData.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

                if (LAppDefine.DEBUG_LOG_ENABLE) {
                    printLog("文件打开错误。");
                }
            }
        }
    }

    // 获取增量时间（与上一帧的差异）
    public static float getDeltaTime() {
        // 将纳秒转换为秒
        return (float) (_deltaNanoTime / 1000000000.0f);
    }

    /**
     * 日志函数
     *
     * @param message 日志消息
     */
    public static void printLog(String message) {
        Log.d(TAG, message);
    }

    private static long getSystemNanoTime() {
        return System.nanoTime();
    }
}

