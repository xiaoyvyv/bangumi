/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at
 * https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#import "LAppTextureManager.h"
#import <Foundation/Foundation.h>
#import <Metal/Metal.h>
#import <iostream>

#define STBI_NO_STDIO
#define STBI_ONLY_PNG
#define STB_IMAGE_IMPLEMENTATION
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wcomma"
#pragma clang diagnostic ignored "-Wunused-function"

#import "stb_image.h"

#pragma clang diagnostic pop

#import "LAppPal.h"
#import "Rendering/Metal/CubismRenderingInstanceSingleton_Metal.h"

@interface LAppTextureManager ()

@property(nonatomic) Csm::csmVector<TextureInfo *> textures;

@end

@implementation LAppTextureManager

- (id)init {
    self = [super init];
    return self;
}

- (void)dealloc {
    [self releaseTextures];
}

- (TextureInfo *)createTextureFromPngFile:(std::string)fileName {
    // 1. 检查是否已加载
    for (Csm::csmUint32 i = 0; i < _textures.GetSize(); i++) {
        if (_textures[i]->fileName == fileName) {
            return _textures[i];
        }
    }

    // 2. 读取 PNG 文件
    int width, height, channels;
    unsigned int size;
    unsigned char *png;
    unsigned char *address = LAppPal::LoadFileAsBytes(fileName, &size);
    png = stbi_load_from_memory(address, static_cast<int>(size), &width, &height, &channels, STBI_rgb_alpha);

    // 3. 创建纹理描述符
    MTLTextureDescriptor *textureDescriptor = [[MTLTextureDescriptor alloc] init];
    textureDescriptor.pixelFormat = MTLPixelFormatRGBA8Unorm;
    textureDescriptor.width = width;
    textureDescriptor.height = height;

    int widthLevels = ceil(log2(width));
    int heightLevels = ceil(log2(height));
    int mipCount = (heightLevels > widthLevels) ? heightLevels : widthLevels;
    textureDescriptor.mipmapLevelCount = mipCount;

    // 4. 获取 Metal 设备并创建独立 commandQueue
    CubismRenderingInstanceSingleton_Metal *single = [CubismRenderingInstanceSingleton_Metal sharedManager];
    id <MTLDevice> device = [single getMTLDevice];
    id <MTLCommandQueue> commandQueue = [device newCommandQueue];

    // 5. 创建纹理
    id <MTLTexture> texture = [device newTextureWithDescriptor:textureDescriptor];
    NSUInteger bytesPerRow = 4 * width;
    MTLRegion region = {{0, 0, 0}, {(NSUInteger) width, (NSUInteger) height, 1}};
    [texture replaceRegion:region mipmapLevel:0 withBytes:png bytesPerRow:bytesPerRow];

    // 6. 生成 mipmaps
    id <MTLCommandBuffer> commandBuffer = [commandQueue commandBuffer];
    id <MTLBlitCommandEncoder> blitEncoder = [commandBuffer blitCommandEncoder];
    [blitEncoder generateMipmapsForTexture:texture];
    [blitEncoder endEncoding];
    [commandBuffer commit];
    [commandBuffer waitUntilCompleted]; // 确保完成

    // 7. 清理
    stbi_image_free(png);
    LAppPal::ReleaseBytes(address);

    // 8. 保存纹理信息
    TextureInfo *textureInfo = new TextureInfo;
    textureInfo->fileName = fileName;
    textureInfo->width = width;
    textureInfo->height = height;
    textureInfo->id = texture;
    _textures.PushBack(textureInfo);

    return textureInfo;
}

- (id <MTLTexture>)loadTextureUsingMetalKit:(NSURL *)url device:(id <MTLDevice>)device {
    MTKTextureLoader *loader = [[MTKTextureLoader alloc] initWithDevice:device];

    NSError *error;
    id <MTLTexture> texture = [loader newTextureWithContentsOfURL:url options:nil error:&error];

    if (!texture) {
        NSLog(@"Failed to create the texture from %@", url.absoluteString);
        return nil;
    }
    return texture;
}

- (unsigned int)premultiply:(unsigned char)red
                      Green:(unsigned char)green
                       Blue:(unsigned char)blue
                      Alpha:(unsigned char)alpha {
    return static_cast<unsigned>((red * (alpha + 1) >> 8) | ((green * (alpha + 1) >> 8) << 8) |
            ((blue * (alpha + 1) >> 8) << 16) | (((alpha)) << 24));
}

- (void)releaseTextures {
    for (Csm::csmUint32 i = 0; i < _textures.GetSize(); i++) {
        delete _textures[i];
        _textures.Remove(i);
    }

    _textures.Clear();
}

- (void)releaseTextureWithId:(id <MTLTexture>)textureId {
    for (Csm::csmUint32 i = 0; i < _textures.GetSize(); i++) {
        if (_textures[i]->id != textureId) {
            continue;
        }
        delete _textures[i];
        _textures.Remove(i);
        break;
    }
}

- (void)releaseTextureByName:(std::string)fileName; {
    for (Csm::csmUint32 i = 0; i < _textures.GetSize(); i++) {
        if (_textures[i]->fileName == fileName) {
            delete _textures[i];
            _textures.Remove(i);
            break;
        }
    }
}

@end
