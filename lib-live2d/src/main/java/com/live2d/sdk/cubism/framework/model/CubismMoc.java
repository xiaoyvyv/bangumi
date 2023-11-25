/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.model;

import com.live2d.sdk.cubism.core.Live2DCubismCore;
import com.live2d.sdk.cubism.framework.utils.CubismDebug;

import java.text.ParseException;


/**
 * Moc data manager class
 */
public class CubismMoc {
    /**
     * バッファからMocファイルを読み取り、Mocデータを作成する。
     * NOTE: デフォルトではMOC3の整合性をチェックしない。
     *
     * @param mocBytes MOC3ファイルのバイト配列バッファ
     * @return MOC3ファイルのインスタンス
     */
    public static CubismMoc create(byte[] mocBytes) {
        return create(mocBytes, false);
    }

    /**
     * バッファからMocファイルを読み取り、Mocデータを作成する。
     *
     * @param mocBytes            MOC3ファイルのバイト配列バッファ
     * @param shouldCheckMocConsistency MOC3の整合性をチェックするか。trueならチェックする。
     * @return MOC3ファイルのインスタンス
     */
    public static CubismMoc create(byte[] mocBytes, boolean shouldCheckMocConsistency) {
        com.live2d.sdk.cubism.core.CubismMoc moc;

        if (mocBytes == null) {
            return null;
        }

        if (shouldCheckMocConsistency) {
            // .moc3の整合性を確認する。
            boolean consistency = hasMocConsistency(mocBytes);

            if (!consistency) {
                CubismDebug.cubismLogError("Inconsistent MOC3.");
                return null;
            }
        }

        try {
            moc = com.live2d.sdk.cubism.core.CubismMoc.instantiate(mocBytes);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        CubismMoc cubismMoc = new CubismMoc(moc);
        cubismMoc.mocVersion = Live2DCubismCore.getMocVersion(mocBytes);

        return cubismMoc;
    }

    /**
     * Return the latest .moc3 Version.
     *
     * @return the latest .moc3 Version
     */
    public static int getLatestMocVersion() {
        return Live2DCubismCore.getLatestMocVersion();
    }

    /**
     * .moc3ファイルがロードされたメモリを参照し、フォーマットが正しいかチェックする。（不正なファイルかどうかのチェック）
     * Native CoreのcsmHasMocConsistencyに対応する。
     *
     * @param mocBytes .moc3が読まれたデータ配列
     *
     * @return .moc3が有効なデータであるかどうか。有効なデータならtrue
     */
    public static boolean hasMocConsistency(byte[] mocBytes) {
        return Live2DCubismCore.hasMocConsistency(mocBytes);
    }

    /**
     * Create a model.
     *
     * @return the model created from Moc data
     */
    public CubismModel createModel() {
        com.live2d.sdk.cubism.core.CubismModel model;

        try {
            model = moc.instantiateModel();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        }

        CubismModel cubismModel = new CubismModel(model);
        cubismModel.initialize();
        modelCount++;

        return cubismModel;
    }

    /**
     * Close the Moc instance.
     */
    public void delete() {
        assert (modelCount == 0);
        if (moc != null) {
            moc.close();
        }
        moc = null;
    }

    /**
     * Delete the model given in the argument.
     *
     * @param model model instance
     */
    public void deleteModel(CubismModel model) {
        model.close();
        modelCount--;
    }

    /**
     * Return the .moc3 Version of the loaded model.
     *
     * @return the .moc3 Version of the loaded model
     */
    public int getMocVersion() {
        return mocVersion;
    }

    /**
     * private constructor
     */
    private CubismMoc(com.live2d.sdk.cubism.core.CubismMoc moc) {
        this.moc = moc;
    }

    /**
     * Moc data
     */
    private com.live2d.sdk.cubism.core.CubismMoc moc;
    /**
     * Number of models created by the Moc data
     */
    private int modelCount;
    /**
     * .moc3 version of the loaded model
     */
    private int mocVersion;
}
