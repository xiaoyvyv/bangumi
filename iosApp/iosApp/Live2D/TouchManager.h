/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#ifndef TouchManager_h
#define TouchManager_h

@interface TouchManager : NSObject

@property(nonatomic, readonly) float startX; // タッチを開始した時のxの値
@property(nonatomic, readonly) float startY; // タッチを開始した時のxの値
@property(nonatomic, readonly, getter=getX) float lastX; // シングルタッチ時のxの値
@property(nonatomic, readonly, getter=getY) float lastY; // タッチを開始した時のxの値
@property(nonatomic, readonly, getter=getX1) float lastX1; // シングルタッチ時のyの値
@property(nonatomic, readonly, getter=getY1) float lastY1; // ダブルタッチ時の一つ目のyの値
@property(nonatomic, readonly, getter=getX2) float lastX2; // ダブルタッチ時の二つ目のxの値
@property(nonatomic, readonly, getter=getY2) float lastY2; // ダブルタッチ時の一つ目のxの値
@property(nonatomic, readonly) float lastTouchDistance; // 2本以上でタッチしたときの指の距離
@property(nonatomic, readonly) float deltaX; // 前回の値から今回の値へのxの移動距離。
@property(nonatomic, readonly) float deltaY; // 前回の値から今回の値へのyの移動距離。
@property(nonatomic, readonly) float scale; // このフレームで掛け合わせる拡大率。拡大操作中以外は1。
@property(nonatomic, readonly) float touchSingle; // シングルタッチ時はtrue
@property(nonatomic, readonly) float flipAvailable; // フリップが有効かどうか

/**
 * @brief 初期化
 */
- (id)init;

/**
 * @brief 解放処理
 */
- (void)dealloc;

/*
 * @brief タッチ開始時イベント
 *
 * @param[in] deviceY    タッチした画面のyの値
 * @param[in] deviceX    タッチした画面のxの値
 */
- (void)touchesBegan:(float)deviceX DeciveY:(float)deviceY;

/*
 * @brief ドラッグ時のイベント
 *
 * @param[in] deviceX    タッチした画面のyの値
 * @param[in] deviceY    タッチした画面のxの値
 */
- (void)touchesMoved:(float)deviceX DeviceY:(float)deviceY;

/*
 * @brief ドラッグ時のイベント
 *
 * @param[in] deviceX1   1つめのタッチした画面のxの値
 * @param[in] deviceY1   1つめのタッチした画面のyの値
 * @param[in] deviceX2   2つめのタッチした画面のxの値
 * @param[in] deviceY2   2つめのタッチした画面のyの値
 */
- (void)touchesMoved:(float)deviceX1 DeviceY1:(float)deviceY1 DeviceX2:(float)deviceX2 DeviceY2:(float)deviceY2;

/*
 * @brief フリックの距離測定
 *
 * @return フリック距離
 */
- (float)getFlickDistance;

/*
 * @brief 点1から点2への距離を求める
 *
 * @param[in] x1 1つめのタッチした画面のxの値
 * @param[in] y1 1つめのタッチした画面のyの値
 * @param[in] x2 2つめのタッチした画面のxの値
 * @param[in] y2 2つめのタッチした画面のyの値
 * @return   2点の距離
 */
- (float)calculateDistance:(float)x1 TouchY1:(float)y1 TouchX2:(float)x2 TouchY2:(float)y2;

/*
 * 二つの値から、移動量を求める。
 * 違う方向の場合は移動量０。同じ方向の場合は、絶対値が小さい方の値を参照する
 *
 * @param[in] v1    1つめの移動量
 * @param[in] v2    2つめの移動量
 *
 * @return   小さい方の移動量
 */
- (float)calculateMovingAmount:(float)v1 Vector2:(float)v2;

@end

#endif /* TouchManager_h */
