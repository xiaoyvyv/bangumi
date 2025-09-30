/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#import <UIKit/UIKit.h>

@class LAppView;

@interface LCubismSdk : NSObject

/**
 * @brief 获取类的实例。
 *        如果实例尚未生成，则内部创建一个新实例。
 */
+ (LCubismSdk *)getInstance;

/**
 * @brief   Cubism SDK の初期化
 */
- (void)initializeCubism;

/**
 * @brief   アプリケーションを終了する。
 */
- (void)releaseCubism;

@end

