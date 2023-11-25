package com.live2d.sdk.cubism.framework.rendering.android;

import com.live2d.sdk.cubism.framework.model.CubismModel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Drawableの情報を格納するバッファをキャッシュし保持するクラス。
 */
class CubismDrawableInfoCachesHolder {
    public CubismDrawableInfoCachesHolder(CubismModel model) {
        final int drawableCount = model.getDrawableCount();
        final int[] renderOrder = model.getDrawableRenderOrders();
        final int[] sortedDrawableIndexList = new int[drawableCount];

        // Sort the index by drawing order
        for (int i = 0; i < drawableCount; i++) {
            final int order = renderOrder[i];
            sortedDrawableIndexList[order] = i;
        }
        vertexArrayCaches = new FloatBuffer[drawableCount];
        uvArrayCaches = new FloatBuffer[drawableCount];
        indexArrayCaches = new ShortBuffer[drawableCount];

        for (int i = 0; i < drawableCount; i++) {
            final int drawableIndex = sortedDrawableIndexList[i];

            // Vertex Array
            {
                float[] vertexArray = model.getDrawableVertices(drawableIndex);

                ByteBuffer bb = ByteBuffer.allocateDirect(vertexArray.length * 4);
                bb.order(ByteOrder.nativeOrder());
                FloatBuffer buffer = bb.asFloatBuffer();
                vertexArrayCaches[drawableIndex] = buffer;
            }

            // UV Array
            {
                float[] uvArray = model.getDrawableVertexUvs(drawableIndex);

                ByteBuffer bb = ByteBuffer.allocateDirect(uvArray.length * 4);
                bb.order(ByteOrder.nativeOrder());
                FloatBuffer buffer = bb.asFloatBuffer();
                uvArrayCaches[drawableIndex] = buffer;
            }

            // Index Array
            {
                short[] indexArray = model.getDrawableVertexIndices(drawableIndex);

                ByteBuffer bb = ByteBuffer.allocateDirect(indexArray.length * 4);
                bb.order(ByteOrder.nativeOrder());
                ShortBuffer buffer = bb.asShortBuffer();
                indexArrayCaches[drawableIndex] = buffer;
            }
        }
    }

    /**
     * Drawableの頂点をキャッシュされたバッファに入れて返す。
     *
     * @param drawableIndex 取得したいDrawableのインデックス
     * @param drawableVertices Drawableの頂点が格納された配列
     * @return 頂点バッファ
     */
    public FloatBuffer setUpVertexArray(int drawableIndex, float[] drawableVertices) {
        FloatBuffer vertexArray = vertexArrayCaches[drawableIndex];
        vertexArray.clear();
        vertexArray.put(drawableVertices);
        vertexArray.position(0);

        return vertexArray;
    }

    /**
     * DrawableのUV情報をキャッシュされたバッファに入れて返す。
     *
     * @param drawableIndex 取得したいDrawableのインデックス
     * @param drawableVertexUvs DrawableのUV情報が入った配列
     * @return UV情報バッファ
     */
    public FloatBuffer setUpUvArray(int drawableIndex, float[] drawableVertexUvs) {
        FloatBuffer uvArray = uvArrayCaches[drawableIndex];
        uvArray.clear();
        uvArray.put(drawableVertexUvs);
        uvArray.position(0);

        return uvArray;
    }

    /**
     * Drawableの頂点に対するポリゴンの対応番号をキャッシュされたバッファに入れて返す。
     *
     * @param drawableIndex 取得したいDrawableのインデックス
     * @param drawableIndices Drawableの頂点に対するポリゴンの対応番号の配列
     * @return 頂点に対するポリゴンの対応番号のバッファ
     */
    public ShortBuffer setUpIndexArray(int drawableIndex, short[] drawableIndices) {
        ShortBuffer indexArray = indexArrayCaches[drawableIndex];
        indexArray.clear();
        indexArray.put(drawableIndices);
        indexArray.position(0);

        return indexArray;
    }

    /**
     * Drawableの頂点のキャッシュ配列
     */
    private final FloatBuffer[] vertexArrayCaches;
    /**
     * DrawableのUV情報のキャッシュ配列
     */
    private final FloatBuffer[] uvArrayCaches;
    /**
     * Drawableの頂点に対するポリゴンの対応番号のキャッシュ配列
     */
    private final ShortBuffer[] indexArrayCaches;
}
