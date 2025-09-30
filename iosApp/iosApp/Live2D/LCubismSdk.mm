/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at
 * https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#import "LCubismSdk.h"
#import "LAppAllocator.h"
#import "LAppDefine.h"
#import "LAppPal.h"
#import <iostream>

@interface LCubismSdk ()

@property(nonatomic) LAppAllocator cubismAllocator;             // Cubism SDK Allocator
@property(nonatomic) Csm::CubismFramework::Option cubismOption; // Cubism SDK Option

@end

@implementation LCubismSdk

static LCubismSdk *s_instance = nil;

// 获取单例实例
+ (LCubismSdk *)getInstance {
    @synchronized (self) {
        if (s_instance == nil) {
            s_instance = [[LCubismSdk alloc] init];
        }
    }
    return s_instance;
}

- (void)initializeCubism {
    _cubismOption.LogFunction = LAppPal::PrintMessageLn;
    _cubismOption.LoggingLevel = LAppDefine::CubismLoggingLevel;
    _cubismOption.LoadFileFunction = LAppPal::LoadFileAsBytes;
    _cubismOption.ReleaseBytesFunction = LAppPal::ReleaseBytes;

    Csm::CubismFramework::CleanUp();
    Csm::CubismFramework::StartUp(&_cubismAllocator, &_cubismOption);
    Csm::CubismFramework::Initialize();

    LAppPal::UpdateTime();
}

- (void)releaseCubism {
    Csm::CubismFramework::Dispose();
    Csm::CubismFramework::CleanUp();
}

@end
