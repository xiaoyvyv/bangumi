//
//  SafeMetalContainerView.h
//  iosApp
//
//  Created by 王怀玉 on 2025/8/21.
//  Copyright © 2025 orgName. All rights reserved.
//

#import "Live2DView.h"
#import "CubismFramework.hpp"
#import "LAppDefine.h"
#import "LAppLive2DManager.h"
#import "LAppModel.h"
#import "LAppPal.h"
#import "LCubismSdk.h"
#import "Rendering/Metal/CubismRenderingInstanceSingleton_Metal.h"
#import "TouchManager.h"
#import <Math/CubismMatrix44.hpp>
#import <Math/CubismViewMatrix.hpp>
#import <Metal/Metal.h>
#import <QuartzCore/CAMetalLayer.h>
#import <QuartzCore/QuartzCore.h>
#import <UIKit/UIKit.h>
#import <math.h>
#import <string>

#define BUFFER_OFFSET(bytes) ((GLubyte *)NULL + (bytes))

using namespace std;
using namespace LAppDefine;

@interface Live2DView ()

@property(nonatomic) LAppLive2DManager *manager;
@property(nonatomic) TouchManager *touchManager;          ///< タッチマネージャー
@property(nonatomic) Csm::CubismMatrix44 *deviceToScreen; ///< デバイスからスクリーンへの行列
@property(nonatomic) Csm::CubismViewMatrix *viewMatrix;

@end

@implementation Live2DView

#pragma mark - 初始化

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.opaque = NO;

        _metalView = [[MTKView alloc] initWithFrame:self.bounds];
        _metalView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;

        [self addSubview:_metalView];
        [self setupMetalView];
    }
    return self;
}

#pragma mark - MetalView 设置

- (void)setupMetalView {
    id <MTLDevice> device = MTLCreateSystemDefaultDevice();

    _metalView.clearColor = MTLClearColorMake(0.0, 0.0, 0.0, 0.0);
    _metalView.device = device;
    _metalView.delegate = self;
    _metalView.opaque = NO;
    _metalView.backgroundColor = [UIColor clearColor];
    _metalView.colorPixelFormat = MTLPixelFormatBGRA8Unorm;

    // Fremework層でもMTLDeviceを参照するためシングルトンオブジェクトに登録
    CubismRenderingInstanceSingleton_Metal *single = [CubismRenderingInstanceSingleton_Metal sharedManager];
    [single setMTLDevice:device];
    [single setMetalLayer:(CAMetalLayer *) _metalView.layer];

    _commandQueue = [device newCommandQueue];

    // タッチ関係のイベント管理
    _touchManager = [[TouchManager alloc] init];

    _manager = [[LAppLive2DManager alloc] init];

    // デバイス座標からスクリーン座標に変換するための
    _deviceToScreen = new CubismMatrix44();

    // 画面の表示の拡大縮小や移動の変換を行う行列
    _viewMatrix = new CubismViewMatrix();
}

#pragma mark - 加载模型

- (void)loadModel:(NSString *)modelName modelDir:(NSString *)modelDir {
    if (self.window) {
        [_manager loadModel:modelName modelDir:modelDir];
    }
}

- (void)resizeScreen {
    int width = _metalView.frame.size.width;
    int height = _metalView.frame.size.height;

    // 縦サイズを基準とする
    float ratio = static_cast<float>(width) / static_cast<float>(height);
    float left = -ratio;
    float right = ratio;
    float bottom = ViewLogicalLeft;
    float top = ViewLogicalRight;

    // デバイスに対応する画面の範囲。 Xの左端, Xの右端, Yの下端, Yの上端
    _viewMatrix->SetScreenRect(left, right, bottom, top);
    _viewMatrix->Scale(ViewScale, ViewScale);

    _deviceToScreen->LoadIdentity(); // サイズが変わった際などリセット必須
    if (width > height) {
        float screenW = fabsf(right - left);
        _deviceToScreen->ScaleRelative(screenW / width, -screenW / width);
    } else {
        float screenH = fabsf(top - bottom);
        _deviceToScreen->ScaleRelative(screenH / height, -screenH / height);
    }
    _deviceToScreen->TranslateRelative(-width * 0.5f, -height * 0.5f);

    // 表示範囲の設定
    _viewMatrix->SetMaxScale(ViewMaxScale); // 限界拡大率
    _viewMatrix->SetMinScale(ViewMinScale); // 限界縮小率

    // 表示できる最大範囲
    _viewMatrix->SetMaxScreenRect(ViewLogicalMaxLeft, ViewLogicalMaxRight, ViewLogicalMaxBottom, ViewLogicalMaxTop);
}

- (void)mtkView:(nonnull MTKView *)view drawableSizeWillChange:(CGSize)size {
    MTLTextureDescriptor *depthTextureDescriptor =
            [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatDepth32Float
                                                               width:size.width
                                                              height:size.height
                                                           mipmapped:false];
    depthTextureDescriptor.usage = MTLTextureUsageRenderTarget | MTLTextureUsageShaderRead;
    depthTextureDescriptor.storageMode = MTLStorageModePrivate;

    _depthTexture = [view.device newTextureWithDescriptor:depthTextureDescriptor];

    [self resizeScreen];
}

- (void)drawInMTKView:(nonnull MTKView *)view {
    LAppPal::UpdateTime();
    CAMetalLayer *metalLayer = (CAMetalLayer *) _metalView.layer;

    id <MTLCommandBuffer> commandBuffer = [_commandQueue commandBuffer];
    id <CAMetalDrawable> currentDrawable = [metalLayer nextDrawable];

    MTLRenderPassDescriptor *renderPassDescriptor = [[MTLRenderPassDescriptor alloc] init];
    renderPassDescriptor.colorAttachments[0].texture = currentDrawable.texture;
    renderPassDescriptor.colorAttachments[0].loadAction = MTLLoadActionClear;
    renderPassDescriptor.colorAttachments[0].storeAction = MTLStoreActionStore;
    renderPassDescriptor.colorAttachments[0].clearColor = MTLClearColorMake(0, 0, 0, 0);

    id <MTLRenderCommandEncoder> renderEncoder = [commandBuffer renderCommandEncoderWithDescriptor:renderPassDescriptor];

    [renderEncoder endEncoding];

    [_manager SetViewMatrix:_viewMatrix];
    [_manager onUpdate:commandBuffer currentDrawable:currentDrawable depthTexture:_depthTexture frame:_metalView.frame];

    [commandBuffer presentDrawable:currentDrawable];
    [commandBuffer commit];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:_metalView];

    [_touchManager touchesBegan:point.x DeciveY:point.y];
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:_metalView];

    float viewX = [self transformViewX:[_touchManager getX]];
    float viewY = [self transformViewY:[_touchManager getY]];

    [_touchManager touchesMoved:point.x DeviceY:point.y];
    [_manager onDrag:viewX floatY:viewY];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:_metalView];

    // タッチ終了
    [_manager onDrag:0.0f floatY:0.0f];
    {
        // シングルタップ
        float getX = [_touchManager getX]; // 論理座標変換した座標を取得。
        float getY = [_touchManager getY]; // 論理座標変換した座標を取得。
        float x = _deviceToScreen->TransformX(getX);
        float y = _deviceToScreen->TransformY(getY);

        if (DebugTouchLogEnable) {
            LAppPal::PrintLogLn("[APP]touchesEnded x:%.2f y:%.2f", x, y);
        }

        LAppModel *model = [_manager currentModel];
        if (model) {
            NSString *modelName = [NSString stringWithUTF8String:model->GetModelName()];
            if (_onModelTapEvent != nil) {
                _onModelTapEvent(modelName, x, y);
            }
        }
    }
}

- (bool)isTapArea:(NSString *)hitAreaName floatX:(float)x floatY:(float)y {
    return [_manager isTapArea:hitAreaName floatX:x floatY:y];
}

- (void)setExpression:(NSString *)expressionID {
    [_manager setExpression:expressionID];
}

- (void)setRandomExpression {
    [_manager setRandomExpression];
}

- (void)setMotion:(NSString *)group no:(int)no {
    [_manager setMotion:group no:no];
}

- (void)setRandomMotion:(NSString *)group {
    [_manager setRandomMotion:group];
}

- (NSArray

<NSString *> *)getAllExpressionIds {
    Csm::csmVector <Csm::csmString> idsVector = [_manager getAllExpressionIds];
    NSMutableArray < NSString * > *ocArray = [NSMutableArray array];

    for (int i = 0; i < idsVector.GetSize(); i++) {
        const Csm::csmString &csmStr = idsVector[i];
        NSString *ocString = [NSString stringWithUTF8String:csmStr.GetRawString()];
        [ocArray addObject:ocString];
    }

    return [ocArray copy];
}

- (NSArray

<NSString *> *)getAllMotionIds {
    Csm::csmVector <Csm::csmString> idsVector = [_manager getAllMotionIds];
    NSMutableArray < NSString * > *ocArray = [NSMutableArray array];

    for (int i = 0; i < idsVector.GetSize(); i++) {
        const Csm::csmString &csmStr = idsVector[i];
        NSString *ocString = [NSString stringWithUTF8String:csmStr.GetRawString()];
        [ocArray addObject:ocString];
    }

    return [ocArray copy];
}

- (float)transformViewX:(float)deviceX {
    float screenX = _deviceToScreen->TransformX(deviceX); // 論理座標変換した座標を取得。
    return _viewMatrix->InvertTransformX(screenX);        // 拡大、縮小、移動後の値。
}

- (float)transformViewY:(float)deviceY {
    float screenY = _deviceToScreen->TransformY(deviceY); // 論理座標変換した座標を取得。
    return _viewMatrix->InvertTransformY(screenY);        // 拡大、縮小、移動後の値。
}

- (float)transformScreenX:(float)deviceX {
    return _deviceToScreen->TransformX(deviceX);
}

- (float)transformScreenY:(float)deviceY {
    return _deviceToScreen->TransformY(deviceY);
}

- (float)transformTapY:(float)deviceY {
    float height = _metalView.frame.size.height;
    return deviceY * -1 + height;
}

- (void)willMoveToSuperview:(UIView *)newSuperview {
    if (newSuperview == nil) {
        [self releaseView];
    }
}

#pragma mark - 释放资源

- (void)releaseView {
    _metalView.delegate = nil;
    _metalView = nil;

    _manager = nil;

    if (_viewMatrix) {
        delete _viewMatrix;
        _viewMatrix = nil;
    }
    if (_deviceToScreen) {
        delete _deviceToScreen;
        _deviceToScreen = nil;
    }

    _onModelTapEvent = nil;
    _touchManager = nil;
}

- (void)dealloc {
    NSLog(@"Live2DView -> dealloc");
}

@end
