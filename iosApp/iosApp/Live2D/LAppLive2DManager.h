/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#ifndef LAppLive2DManager_h
#define LAppLive2DManager_h

#import "LAppModel.h"
#import "LAppTextureManager.h"
#import <CubismFramework.hpp>
#import <Math/CubismMatrix44.hpp>
#import <Type/csmString.hpp>
#import <Type/csmVector.hpp>

@interface LAppLive2DManager : NSObject

@property(nonatomic) Csm::CubismMatrix44 *viewMatrix; // モデル描画に用いるView行列
@property(nonatomic) LAppModel *currentModel;         // モデルインスタンスのコンテナ
@property(nonatomic) Csm::Rendering::CubismOffscreenSurface_Metal *renderBuffer;
@property(nonatomic) MTLRenderPassDescriptor *renderPassDescriptor;
@property(nonatomic, readonly, getter=getTextureManager) LAppTextureManager *textureManager;

@property(nonatomic) Csm::csmVector <Csm::csmString> modelDir; ///< モデルディレクトリ名のコンテナ

/**
 * @brief   シーンを切り替える
 *           サンプルアプリケーションではモデルセットの切り替えを行う。
 */
- (void)loadModel:(NSString *)modelName modelDir:(NSString *)modelDir;

/**
 * @brief 現在のシーンで保持しているモデルを返す。
 *
 * @return モデルのインスタンスを返す。インデックス値が範囲外の場合はNULLを返す。
 */
- (LAppModel *)getModel;

/**
 * @brief 現在のシーンで保持している全てのモデルを解放する
 */
- (void)releaseModel;

/**
 * @brief   画面をタップしたときの処理
 *
 * @param[in]   x   画面のX座標
 * @param[in]   y   画面のY座標
 */
- (bool)isTapArea:(NSString *)hitAreaName floatX:(Csm::csmFloat32)x floatY:(Csm::csmFloat32)y;

/**
 * @brief   引数で指定した表情モーションをセットする
 *
 * @param   expressionID    表情モーションのID
 */
- (void)setExpression:(NSString *)expressionID;

/**
 * @brief   ランダムに選ばれた表情モーションをセットする
 */
- (void)setRandomExpression;

/**
 * @brief   引数で指定した动作モーションをセットする
 *
 * @param   group    动作モーションのGroup
 */
- (void)setMotion:(NSString *)group no:(int)no;

/**
 * @brief   ランダムに選ばれた动作モーションをセットする
 */
- (void)setRandomMotion:(NSString *)group;

/**
 * 获取全部表情、动作ID
 */
- (Csm::csmVector <Csm::csmString>)getAllExpressionIds;

/**
 * 获取全部动作ID
 */
- (Csm::csmVector <Csm::csmString>)getAllMotionIds;

/**
 * @brief   画面をドラッグしたときの処理
 *
 * @param[in]   x   画面のX座標
 * @param[in]   y   画面のY座標
 */
- (void)onDrag:(Csm::csmFloat32)x floatY:(Csm::csmFloat32)y;

/**
 * @brief   画面を更新するときの処理
 *          モデルの更新処理および描画処理を行う
 */
- (void)onUpdate:(id <MTLCommandBuffer>)commandBuffer
 currentDrawable:(id <CAMetalDrawable>)drawable
    depthTexture:(id <MTLTexture>)depthTarget
           frame:(CGRect)frame;

/**
 * @brief   viewMatrixをセットする
 */
- (void)SetViewMatrix:(Csm::CubismMatrix44 *)m;
@end

#endif /* LAppLive2DManager_h */
