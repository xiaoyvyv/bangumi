/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at
 * https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#import "LAppLive2DManager.h"
#import "LAppDefine.h"
#import "LAppModel.h"
#import "LAppPal.h"
#import "Rendering/Metal/CubismRenderingInstanceSingleton_Metal.h"
#import <Foundation/Foundation.h>
#import <Rendering/Metal/CubismRenderer_Metal.hpp>
#import <stdlib.h>
#import <string.h>

@interface LAppLive2DManager ()

@property(nonatomic, readwrite) LAppTextureManager *textureManager;

- (id)init;

- (void)dealloc;
@end

@implementation LAppLive2DManager

void BeganMotion(Csm::ACubismMotion *self) {
    LAppPal::PrintLogLn("Motion began: %x", self);
}

void FinishedMotion(Csm::ACubismMotion *self) {
    LAppPal::PrintLogLn("Motion Finished: %x", self);
}

Csm::csmString GetPath(CFURLRef url) {
    CFStringRef cfstr = CFURLCopyFileSystemPath(url, CFURLPathStyle::kCFURLPOSIXPathStyle);
    CFIndex size = CFStringGetLength(cfstr) * 4 + 1; // Length * UTF-16 Max Character size + null-terminated-byte
    char *buf = new char[size];
    CFStringGetCString(cfstr, buf, size, CFStringBuiltInEncodings::kCFStringEncodingUTF8);
    Csm::csmString result(buf);
    delete[] buf;
    return result;
}

- (id)init {
    self = [super init];
    if (self) {
        _renderBuffer = nil;
        _viewMatrix = nil;

        _viewMatrix = new Csm::CubismMatrix44();

        _renderPassDescriptor = [[MTLRenderPassDescriptor alloc] init];
        _renderPassDescriptor.colorAttachments[0].storeAction = MTLStoreActionStore;
        _renderPassDescriptor.colorAttachments[0].clearColor = MTLClearColorMake(0.f, 0.f, 0.f, 0.f);
        _renderPassDescriptor.depthAttachment.loadAction = MTLLoadActionClear;
        _renderPassDescriptor.depthAttachment.storeAction = MTLStoreActionDontCare;
        _renderPassDescriptor.depthAttachment.clearDepth = 1.0;

        _textureManager = [[LAppTextureManager alloc] init];
    }
    return self;
}

- (void)dealloc {
    if (_renderBuffer) {
        _renderBuffer->DestroyOffscreenSurface();
        delete _renderBuffer;
        _renderBuffer = NULL;
    }

    if (_renderPassDescriptor != nil) {
        _renderPassDescriptor = nil;
    }

    if (_textureManager != nil) {
        _textureManager = nil;
    }

    delete _viewMatrix;
    _viewMatrix = nil;

    [self releaseModel];
}

- (void)releaseModel {
    delete _currentModel;
    _currentModel = nil;
}

- (LAppModel *)getModel {
    return _currentModel;
}

- (bool)isTapArea:(NSString *)hitAreaName floatX:(Csm::csmFloat32)x floatY:(Csm::csmFloat32)y {
    if (_currentModel != nil) {
        const char *area = [hitAreaName UTF8String];

        if (_currentModel->HitTest(area, x, y)) {
            if (LAppDefine::DebugLogEnable) {
                LAppPal::PrintLogLn("[APP]hit area: [%s]", area);
            }
            return true;
        }
    }
    return false;
}

- (void)setExpression:(NSString *)expressionID {
    if (_currentModel != nil) {
        _currentModel->SetExpression([expressionID UTF8String]);
    }
}

- (void)setRandomExpression {
    if (_currentModel != nil) {
        _currentModel->SetRandomExpression();
    }
}

- (void)setMotion:(NSString *)group no:(int)no {
    if (_currentModel != nil) {
        _currentModel->StartMotion([group UTF8String], no, LAppDefine::PriorityNormal, FinishedMotion, BeganMotion);
    }
}

- (void)setRandomMotion:(NSString *)group {
    if (_currentModel != nil) {
        _currentModel->StartRandomMotion([group UTF8String], LAppDefine::PriorityNormal, FinishedMotion, BeganMotion);
    }
}

- (Csm::csmVector <Csm::csmString>)getAllExpressionIds {
    if (_currentModel != nil) {
        return _currentModel->GetAllExpressionIds();
    }
    return Csm::csmVector<Csm::csmString>();
}

- (Csm::csmVector <Csm::csmString>)getAllMotionIds {
    if (_currentModel != nil) {
        return _currentModel->GetAllMotionIds();
    }
    return Csm::csmVector<Csm::csmString>();
}

- (void)onDrag:(Csm::csmFloat32)x floatY:(Csm::csmFloat32)y {
    if (_currentModel != nil) {
        _currentModel->SetDragging(x, y);
    }
}

- (void)onUpdate:(id <MTLCommandBuffer>)commandBuffer
 currentDrawable:(id <CAMetalDrawable>)drawable
    depthTexture:(id <MTLTexture>)depthTarget
           frame:(CGRect)frame {
    const CGFloat retinaScale = [[UIScreen mainScreen] scale];

    // Retinaディスプレイサイズにするため倍率をかける
    const float width = frame.size.width * retinaScale;
    const float height = frame.size.height * retinaScale;

    Csm::CubismMatrix44 projection;

    CubismRenderingInstanceSingleton_Metal *single = [CubismRenderingInstanceSingleton_Metal sharedManager];
    id <MTLDevice> device = [single getMTLDevice];

    _renderPassDescriptor.colorAttachments[0].texture = drawable.texture;
    _renderPassDescriptor.colorAttachments[0].loadAction = MTLLoadActionLoad;
    _renderPassDescriptor.depthAttachment.texture = depthTarget;

    Csm::Rendering::CubismRenderer_Metal::StartFrame(device, commandBuffer, _renderPassDescriptor);

    LAppModel *model = _currentModel;

    if (model == nil) {
        return;
    }

    if (model->GetModel() == NULL) {
        LAppPal::PrintLogLn("Failed to model->GetModel().");
        return;
    }

    if (model->GetModel()->GetCanvasWidth() > 1.0f && width < height) {
        // 横に長いモデルを縦長ウィンドウに表示する際モデルの横サイズでscaleを算出する
        model->GetModelMatrix()->SetWidth(2.0f);
        projection.Scale(1.0f, static_cast<float>(width) / static_cast<float>(height));
    } else {
        projection.Scale(static_cast<float>(height) / static_cast<float>(width), 1.0f);
    }

    // 必要があればここで乗算
    if (_viewMatrix != NULL) {
        projection.MultiplyByMatrix(_viewMatrix);
    }

    model->Update();
    model->Draw(projection); ///< 参照渡しなのでprojectionは変質する
}

- (void)loadModel:(NSString *)modelName modelDir:(NSString *)modelDir {
    // 判断目录末尾是否有 '/'
    NSString *fixedDir = modelDir;
    if (![modelDir hasSuffix:@"/"]) {
        fixedDir = [modelDir stringByAppendingString:@"/"];
    }

    Csm::csmString model([modelName UTF8String]);
    Csm::csmString dir([fixedDir UTF8String]);
    Csm::csmString modelJsonName(model);
    modelJsonName += ".model3.json";

    [self releaseModel];
    _currentModel = new LAppModel(_textureManager);
    _currentModel->LoadAssets(dir.GetRawString(), modelJsonName.GetRawString());
}


- (void)SetViewMatrix:(Csm::CubismMatrix44 *)m {
    for (int i = 0; i < 16; i++) {
        _viewMatrix->GetArray()[i] = m->GetArray()[i];
    }
}
@end
