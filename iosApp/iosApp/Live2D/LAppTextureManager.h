/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#ifndef LAppTextureManager_h
#define LAppTextureManager_h

#import <MetalKit/MetalKit.h>
#import <Type/csmVector.hpp>
#import <string>

@interface LAppTextureManager : NSObject

/**
 * @brief 画像情報構造体
 */
typedef struct {
    id <MTLTexture> id;    ///< テクスチャID
    int width;            ///< 横幅
    int height;           ///< 高さ
    std::string fileName; ///< ファイル名
} TextureInfo;

/**
 * @brief 初期化
 */
- (id)init;

/**
 * @brief 解放処理
 *
 */
- (void)dealloc;

/**
 * @brief プリマルチプライ処理
 *
 * @param[in] red  画像のRed値
 * @param[in] green  画像のGreen値
 * @param[in] blue  画像のBlue値
 * @param[in] alpha  画像のAlpha値
 *
 * @return プリマルチプライ処理後のカラー値
 */
- (unsigned int)premultiply:(unsigned char)red
                      Green:(unsigned char)green
                       Blue:(unsigned char)blue
                      Alpha:(unsigned char)alpha;

/**
 * @brief 画像読み込み
 *
 * @param[in] fileName  読み込む画像ファイルパス名
 * @return 画像情報。読み込み失敗時はNULLを返す
 */
- (TextureInfo *)createTextureFromPngFile:(std::string)fileName;

/**
 * @brief 画像の解放
 *
 * 配列に存在する画像全てを解放する
 */
- (void)releaseTextures;

/**
 * @brief 画像の解放
 *
 * 指定したテクスチャIDの画像を解放する
 * @param[in] textureId  解放するテクスチャID
 **/
- (void)releaseTextureWithId:(id <MTLTexture>)textureId;

/**
 * @brief 画像の解放
 *
 * 指定した名前の画像を解放する
 * @param[in] fileName  解放する画像ファイルパス名
 **/
- (void)releaseTextureByName:(std::string)fileName;

@end

#endif /* LAppTextureManager_h */
