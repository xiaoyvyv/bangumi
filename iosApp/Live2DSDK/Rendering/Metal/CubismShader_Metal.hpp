/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#pragma once

#include <MetalKit/MetalKit.h>
#include "CubismFramework.hpp"
#include "CubismRenderer_Metal.hpp"
#include "CubismCommandBuffer_Metal.hpp"
#include "Type/csmVector.hpp"
#include "MetalShaderTypes.h"

//------------ LIVE2D NAMESPACE ------------
namespace Live2D {
    namespace Cubism {
        namespace Framework {
            namespace Rendering {

// 前方宣言
                class CubismRenderer_Metal;

/**
 * @brief   Metal用のシェーダプログラムを生成・破棄するクラス<br>
 *           シングルトンなクラスであり、CubismShader_Metal::GetInstance()からアクセスする。
 *
 */
                class CubismShader_Metal {
                public:
                    /**
                     * @brief   インスタンスを取得する（シングルトン）。
                     *
                     * @return  インスタンスのポインタ
                     */
                    static CubismShader_Metal *GetInstance();

                    /**
                     * @brief   インスタンスを解放する（シングルトン）。
                     */
                    static void DeleteInstance();

                    /**
                     * @brief   描画用のシェーダプログラムの一連のセットアップを実行する
                     *
                     * @param[in]   drawCommandBuffer     ->  コマンドバッファ
                     * @param[in]   renderEncoder         ->  MTLRenderCommandEncoder
                     * @param[in]   renderer              ->  レンダラのインスタンス
                     * @param[in]   model                 ->  描画対象のモデル
                     * @param[in]   index                 ->  描画オブジェクトのインデックス
                     */
                    void SetupShaderProgramForDraw(CubismCommandBuffer_Metal::DrawCommandBuffer *drawCommandBuffer, id <MTLRenderCommandEncoder> renderEncoder, CubismRenderer_Metal *renderer, const CubismModel &model, const csmInt32 index);

                    /**
                     * @brief   マスク用のシェーダプログラムの一連のセットアップを実行する
                     *
                     * @param[in]   drawCommandBuffer     ->  コマンドバッファ
                     * @param[in]   renderEncoder         ->  MTLRenderCommandEncoder
                     * @param[in]   renderer              ->  レンダラのインスタンス
                     * @param[in]   model                 ->  描画対象のモデル
                     * @param[in]   index                 ->  描画オブジェクトのインデックス
                     */
                    void SetupShaderProgramForMask(CubismCommandBuffer_Metal::DrawCommandBuffer *drawCommandBuffer, id <MTLRenderCommandEncoder> renderEncoder, CubismRenderer_Metal *renderer, const CubismModel &model, const csmInt32 index);

                private:
                    struct ShaderProgram {
                        id <MTLFunction> vertexFunction;
                        id <MTLFunction> fragmentFunction;
                    };

                    /**
                    * @bref    シェーダープログラムとシェーダ変数のアドレスを保持する構造体
                    *
                    */
                    struct CubismShaderSet {
                        ShaderProgram *ShaderProgram; ///< シェーダプログラムのアドレス
                        id <MTLRenderPipelineState> RenderPipelineState;
                        id <MTLDepthStencilState> DepthStencilState;
                        id <MTLSamplerState> SamplerState;
                    };

                    /**
                     * @brief   privateなコンストラクタ
                     */
                    CubismShader_Metal();

                    /**
                     * @brief   privateなデストラクタ
                     */
                    virtual ~CubismShader_Metal();

                    /**
                     * @brief   シェーダプログラムを初期化する
                     */
                    void GenerateShaders(CubismRenderer_Metal *renderer);

                    /**
                     * @brief   CubismMatrix44をsimd::float4x4の形式に変換する
                     */
                    simd::float4x4 ConvertCubismMatrix44IntoSimdFloat4x4(CubismMatrix44 &matrix);

                    /**
                     * @brief   使用するカラーチャンネルを設定
                     *
                     * @param[in]   shaderUniforms     ->  シェーダー用ユニフォームバッファ
                     * @param[in]   contextBuffer      ->  クリッピングマスクのコンテキスト
                     */
                    void SetColorChannel(CubismShaderUniforms &shaderUniforms, CubismClippingContext_Metal *contextBuffer);

                    /**
                     * @brief   モデルにバインドされているテクスチャを、フラグメントシェーダーのテクスチャに設定する
                     *
                     * @param[in]   drawCommandBuffer     ->  コマンドバッファ
                     * @param[in]   renderEncoder         ->  MTLRenderCommandEncoder
                     * @param[in]   renderer              ->  レンダラのインスタンス
                     * @param[in]   model                 ->  描画対象のモデル
                     * @param[in]   index                 ->  描画オブジェクトのインデックス
                     */
                    void SetFragmentModelTexture(CubismCommandBuffer_Metal::DrawCommandBuffer *drawCommandBuffer, id <MTLRenderCommandEncoder> renderEncoder, CubismRenderer_Metal *renderer, const CubismModel &model, const csmInt32 index);

                    /**
                     * @brief   頂点シェーダーに頂点インデックスと UV 座標を設定する
                     *
                     * @param[in]   drawCommandBuffer     ->  コマンドバッファ
                     */
                    void SetVertexBufferForVerticesAndUvs(CubismCommandBuffer_Metal::DrawCommandBuffer *drawCommandBuffer, id <MTLRenderCommandEncoder> renderEncoder);

                    /**
                     * @brief   シェーダプログラムをロードしてアドレス返す。
                     *
                     * @param[in]   vertShaderSrc   ->  頂点シェーダのソース
                     * @param[in]   fragShaderSrc   ->  フラグメントシェーダのソース
                     *
                     * @return  シェーダプログラムのアドレス
                     */
                    ShaderProgram *LoadShaderProgram(const csmChar *vertShaderSrc, const csmChar *fragShaderSrc);

                    id <MTLRenderPipelineState> MakeRenderPipelineState(id <MTLDevice> device, ShaderProgram *shaderProgram, int blendMode);

                    id <MTLDepthStencilState> MakeDepthStencilState(id <MTLDevice> device);

                    id <MTLSamplerState> MakeSamplerState(id <MTLDevice> device, CubismRenderer_Metal *renderer);

                    id <MTLLibrary> _shaderLib;

                    csmVector<CubismShaderSet *> _shaderSets;   ///< ロードしたシェーダプログラムを保持する変数

                };

            }
        }
    }
}
//------------ LIVE2D NAMESPACE ------------
