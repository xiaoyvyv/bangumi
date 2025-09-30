/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at
 * https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#import <MetalKit/MetalKit.h>
#import <UIKit/UIKit.h>

@interface Live2DView : UIView <MTKViewDelegate>
@property(nonatomic, strong) MTKView *metalView;
@property(nonatomic) id <MTLCommandQueue> commandQueue;
@property(nonatomic) id <MTLTexture> depthTexture;

/*
 * 点击事件回调
 */
@property(nonatomic, copy) void (^onModelTapEvent)(NSString *modelName, float floatX, float floatY);

/**
 * @brief 解放処理
 */
- (void)dealloc;

/**
 * 加载模型
 */
- (void)loadModel:(NSString *)modelName modelDir:(NSString *)modelDir;

/**
 * 是否点击了某个区域
 */
- (bool)isTapArea:(NSString *)hitAreaName floatX:(float)x floatY:(float)y;

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
 * @brief   设置动作
 *
 * @param   group    动作モーションのGroup
 */
- (void)setMotion:(NSString *)group no:(int)no;

/**
 * @brief   ランダムに選ばれた动作モーションをセットする
 */
- (void)setRandomMotion:(NSString *)group;

/**
 * 获取全部表情ID
 */
- (NSArray

<NSString *> *)
getAllExpressionIds;

/**
 * 获取全部动作ID
 */
- (NSArray

<NSString *> *)
getAllMotionIds;

/**
 * @brief 解放する。
 */
- (void)releaseView;

/**
 * @brief 画面リサイズ処理
 */
- (void)resizeScreen;

/**
 * @brief X座標をView座標に変換する。
 *
 * @param[in]       deviceX            デバイスX座標
 */
- (float)transformViewX:(float)deviceX;

/**
 * @brief Y座標をView座標に変換する。
 *
 * @param[in]       deviceY            デバイスY座標
 */
- (float)transformViewY:(float)deviceY;

/**
 * @brief X座標をScreen座標に変換する。
 *
 * @param[in]       deviceX            デバイスX座標
 */
- (float)transformScreenX:(float)deviceX;

/**
 * @brief Y座標をScreen座標に変換する。
 *
 * @param[in]       deviceY            デバイスY座標
 */
- (float)transformScreenY:(float)deviceY;

@end
