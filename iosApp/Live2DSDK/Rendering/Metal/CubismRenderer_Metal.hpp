/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#pragma once

#include <MetalKit/MetalKit.h>
#include "../CubismRenderer.hpp"
#include "../CubismClippingManager.hpp"
#include "CubismFramework.hpp"
#include "CubismOffscreenSurface_Metal.hpp"
#include "CubismCommandBuffer_Metal.hpp"
#include "Type/csmVector.hpp"
#include "Type/csmRectF.hpp"
#include "Type/csmMap.hpp"
#include "Math/CubismVector2.hpp"

//------------ LIVE2D NAMESPACE ------------
namespace Live2D {
    namespace Cubism {
        namespace Framework {
            namespace Rendering {

//  前方宣言
                class CubismRenderer_Metal;

                class CubismClippingContext_Metal;

                class CubismShader_Metal;

/**
 * @brief  クリッピングマスクの処理を実行するクラス
 *
 */
                class CubismClippingManager_Metal
                        : public CubismClippingManager<CubismClippingContext_Metal, CubismOffscreenSurface_Metal> {
                public:

                    /**
                     * @brief   クリッピングコンテキストを作成する。モデル描画時に実行する。
                     *
                     * @param[in]   model        ->  モデルのインスタンス
                     * @param[in]   renderer     ->  レンダラのインスタンス
                     * @param[in]   lastFBO      ->  フレームバッファ
                     * @param[in]   lastViewport ->  ビューポート
                     */
                    void SetupClippingContext(CubismModel &model, CubismRenderer_Metal *renderer, CubismOffscreenSurface_Metal *lastColorBuffer, csmRectF lastViewport);
                };

/**
 * @brief   クリッピングマスクのコンテキスト
 */
                class CubismClippingContext_Metal : public CubismClippingContext {
                    friend class CubismClippingManager_Metal;

                    friend class CubismShader_Metal;

                    friend class CubismRenderer_Metal;

                public:
                    /**
                     * @brief   引数付きコンストラクタ
                     *
                     */
                    CubismClippingContext_Metal(CubismClippingManager <CubismClippingContext_Metal, CubismOffscreenSurface_Metal> *manager, CubismModel &model, const csmInt32 *clippingDrawableIndices, csmInt32 clipCount);

                    /**
                     * @brief   デストラクタ
                     */
                    virtual ~CubismClippingContext_Metal();

                    /**
                     * @brief   このマスクを管理するマネージャのインスタンスを取得する。
                     *
                     * @return  クリッピングマネージャのインスタンス
                     */
                    CubismClippingManager <CubismClippingContext_Metal, CubismOffscreenSurface_Metal> *GetClippingManager();

                private:
                    csmVector<CubismCommandBuffer_Metal::DrawCommandBuffer *> *_clippingCommandBufferList;
                    CubismClippingManager <CubismClippingContext_Metal, CubismOffscreenSurface_Metal> *_owner;        ///< このマスクを管理しているマネージャのインスタンス
                };

/**
 * @brief   Cubismモデルを描画する直前のMetalのステートを保持・復帰させるクラス
 *
 */
                class CubismRendererProfile_Metal {
                    friend class CubismRenderer_Metal;

                private:
                    /**
                     * @brief   privateなコンストラクタ
                     */
                    CubismRendererProfile_Metal() {
                    };

                    /**
                     * @brief   privateなデストラクタ
                     */
                    virtual ~CubismRendererProfile_Metal() {
                    };

                    csmBool _lastScissorTest;             ///< モデル描画直前のGL_VERTEX_ATTRIB_ARRAY_ENABLEDパラメータ
                    csmBool _lastBlend;                   ///< モデル描画直前のGL_SCISSOR_TESTパラメータ
                    csmBool _lastStencilTest;             ///< モデル描画直前のGL_STENCIL_TESTパラメータ
                    csmBool _lastDepthTest;               ///< モデル描画直前のGL_DEPTH_TESTパラメータ
                    CubismOffscreenSurface_Metal *_lastColorBuffer;                         ///< モデル描画直前のフレームバッファ
                    id <MTLTexture> _lastDepthBuffer;
                    id <MTLTexture> _lastStencilBuffer;
                    csmRectF _lastViewport;                 ///< モデル描画直前のビューポート
                };

/**
 * @brief   Metal用の描画命令を実装したクラス
 *
 */
                class CubismRenderer_Metal : public CubismRenderer {
                    friend class CubismRenderer;

                    friend class CubismClippingManager_Metal;

                    friend class CubismShader_Metal;

                public:

                    static void StartFrame(id <MTLDevice> device, id <MTLCommandBuffer> commandBuffer, MTLRenderPassDescriptor *renderPassDescriptor);

                    /**
                     * @brief    レンダラの初期化処理を実行する<br>
                     *           引数に渡したモデルからレンダラの初期化処理に必要な情報を取り出すことができる
                     *
                     * @param[in]  model -> モデルのインスタンス
                     */
                    void Initialize(Framework::CubismModel *model) override;

                    void Initialize(Framework::CubismModel *model, csmInt32 maskBufferCount) override;

                    /**
                     * @brief   テクスチャのバインド処理<br>
                     *           CubismRendererにテクスチャを設定し、CubismRenderer中でその画像を参照するためのIndex値を戻り値とする
                     *
                     * @param[in]   modelTextureIndex  ->  セットするモデルテクスチャの番号
                     * @param[in]   texture     ->  バックエンドテクスチャ
                     *
                     */
                    void BindTexture(csmUint32 modelTextureIndex, id <MTLTexture> texture);

                    /**
                     * @brief   バインドされたテクスチャのリストを取得する
                     *
                     * @return  テクスチャのアドレスのリスト
                     */
                    const csmMap <csmInt32, id<MTLTexture>> &GetBindedTextures() const;

                    /**
                     * @brief   指定したIDにバインドされたテクスチャを取得する
                     *
                     * @return  テクスチャ
                     */
                    id <MTLTexture> GetBindedTextureId(csmInt32 textureId);

                    /**
                     * @brief  クリッピングマスクバッファのサイズを設定する<br>
                     *         マスク用のOffscreenSurfaceを破棄・再作成するため処理コストは高い。
                     *
                     * @param[in]  size -> クリッピングマスクバッファのサイズ
                     *
                     */
                    void SetClippingMaskBufferSize(csmFloat32 width, csmFloat32 height);

                    /**
                     * @brief  レンダーテクスチャの枚数を取得する。
                     *
                     * @return  レンダーテクスチャの枚数
                     *
                     */
                    csmInt32 GetRenderTextureCount() const;

                    /**
                     * @brief  クリッピングマスクバッファのサイズを取得する
                     *
                     * @return クリッピングマスクバッファのサイズ
                     *
                     */
                    CubismVector2 GetClippingMaskBufferSize() const;

                    /**
                     * @brief  オフスクリーンサーフェイスバッファを取得する
                     *
                     * @return オフスクリーンサーフェイスバッファへの参照
                     *
                     */
                    CubismOffscreenSurface_Metal *GetOffscreenSurface(csmInt32 index);

                    CubismCommandBuffer_Metal *GetCommandBuffer() {
                        return &_commandBuffer;
                    }

                protected:
                    /**
                     * @brief   コンストラクタ
                     */
                    CubismRenderer_Metal();

                    /**
                     * @brief   デストラクタ
                     */
                    virtual ~CubismRenderer_Metal();

                    /**
                     * @brief   モデルを描画する実際の処理
                     *
                     */
                    void DoDrawModel() override;

                    /**
                     * @brief    描画オブジェクト（アートメッシュ）を描画する。<br>
                     *           ポリゴンメッシュとテクスチャ番号をセットで渡す。
                     *
                     * @param[in]   textureIndex         ->  描画するテクスチャ番号
                     * @param[in]   renderEncoder     ->  MTLRenderCommandEncoder
                     * @param[in]   model             ->  描画対象モデル
                     * @param[in]   index             ->  描画オブジェクトのインデックス
                     */
                    void DrawMeshMetal(CubismCommandBuffer_Metal::DrawCommandBuffer *drawCommandBuffer, id <MTLRenderCommandEncoder> renderEncoder, const CubismModel &model, const csmInt32 index);

                    CubismCommandBuffer_Metal::DrawCommandBuffer *GetDrawCommandBufferData(csmInt32 drawableIndex);

                private:

                    // Prevention of copy Constructor
                    CubismRenderer_Metal(const CubismRenderer_Metal &);

                    CubismRenderer_Metal &operator=(const CubismRenderer_Metal &);

                    static id <MTLCommandBuffer> s_commandBuffer;
                    static id <MTLDevice> s_device;
                    static MTLRenderPassDescriptor *s_renderPassDescriptor;

                    /**
                     * @brief   レンダラが保持する静的なリソースを解放する<br>
                     *           Metalの静的なシェーダプログラムを解放する
                     */
                    static void DoStaticRelease();

                    /**
                     * @brief   描画開始時の追加処理。<br>
                     *           モデルを描画する前にクリッピングマスクに必要な処理を実装している。
                     * @return  描画に使うMTLRenderCommandEncoder
                     */
                    id <MTLRenderCommandEncoder> PreDraw(id <MTLCommandBuffer> commandBuffer, MTLRenderPassDescriptor *drawableRenderDescriptor);

                    /**
                     * @brief   描画完了後の追加処理。
                     *
                     */
                    void PostDraw(id <MTLRenderCommandEncoder> renderEncoder);

                    /**
                     * @brief   SuperClass対応
                     */
                    void SaveProfile() override;

                    /**
                     * @brief   SuperClass対応
                     */
                    void RestoreProfile() override;

                    /**
                     * @brief   マスクテクスチャに描画するクリッピングコンテキストをセットする。
                     */
                    void SetClippingContextBufferForMask(CubismClippingContext_Metal *clip);

                    /**
                     * @brief   マスクテクスチャに描画するクリッピングコンテキストを取得する。
                     *
                     * @return  マスクテクスチャに描画するクリッピングコンテキスト
                     */
                    CubismClippingContext_Metal *GetClippingContextBufferForMask() const;

                    /**
                     * @brief   画面上に描画するクリッピングコンテキストをセットする。
                     */
                    void SetClippingContextBufferForDraw(CubismClippingContext_Metal *clip);

                    /**
                     * @brief   画面上に描画するクリッピングコンテキストを取得する。
                     *
                     * @return  画面上に描画するクリッピングコンテキスト
                     */
                    CubismClippingContext_Metal *GetClippingContextBufferForDraw() const;

                    /**
                     * @brief  マスク生成時かを判定する
                     *
                     * @return  判定値
                     */
                    const inline csmBool IsGeneratingMask() const;

                    csmMap <csmInt32, id<MTLTexture>> _textures;                      ///< モデルが参照するテクスチャとレンダラでバインドしているテクスチャとのマップ
                    csmVector <csmInt32> _sortedDrawableIndexList;       ///< 描画オブジェクトのインデックスを描画順に並べたリスト
                    CubismRendererProfile_Metal _rendererProfile;               ///< Metalのステートを保持するオブジェクト
                    CubismClippingManager_Metal *_clippingManager;               ///< クリッピングマスク管理オブジェクト
                    CubismClippingContext_Metal *_clippingContextBufferForMask;  ///< マスクテクスチャに描画するためのクリッピングコンテキスト
                    CubismClippingContext_Metal *_clippingContextBufferForDraw;  ///< 画面上描画するためのクリッピングコンテキスト

                    csmVector <CubismOffscreenSurface_Metal> _offscreenSurfaces;         ///< マスク描画用のフレームバッファ
                    CubismCommandBuffer_Metal _commandBuffer;
                    csmVector<CubismCommandBuffer_Metal::DrawCommandBuffer *> _drawableDrawCommandBuffer;
                };

            }
        }
    }
}
//------------ LIVE2D NAMESPACE ------------
