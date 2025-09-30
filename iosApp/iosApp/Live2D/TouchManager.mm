/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#import <Foundation/Foundation.h>
#import "TouchManager.h"

@interface TouchManager ()

@property(nonatomic, readwrite) float startX;
@property(nonatomic, readwrite) float startY;
@property(nonatomic, readwrite) float lastX;
@property(nonatomic, readwrite) float lastY;
@property(nonatomic, readwrite) float lastX1;
@property(nonatomic, readwrite) float lastY1;
@property(nonatomic, readwrite) float lastX2;
@property(nonatomic, readwrite) float lastY2;
@property(nonatomic, readwrite) float lastTouchDistance;
@property(nonatomic, readwrite) float deltaX;
@property(nonatomic, readwrite) float deltaY;
@property(nonatomic, readwrite) float scale;
@property(nonatomic, readwrite) float touchSingle;
@property(nonatomic, readwrite) float flipAvailable;

@end

@implementation TouchManager

- (id)init {
    self = [super init];
    return self;
}

- (void)touchesBegan:(float)deviceX DeciveY:(float)deviceY {
    _lastX = deviceX;
    _lastY = deviceY;
    _startX = deviceX;
    _startY = deviceY;
    _lastTouchDistance = -1.0f;
    _flipAvailable = true;
    _touchSingle = true;
}

- (void)touchesMoved:(float)deviceX DeviceY:(float)deviceY {
    _lastX = deviceX;
    _lastY = deviceY;
    _lastTouchDistance = -1.0f;
    _touchSingle = true;
}

- (void)touchesMoved:(float)deviceX1 DeviceY1:(float)deviceY1 DeviceX2:(float)deviceX2 DeviceY2:(float)deviceY2 {
    float distance = [self calculateDistance:deviceX1 TouchY1:deviceY1 TouchX2:deviceX2 TouchY2:deviceY2];
    float centerX = (deviceX1 + deviceX2) * 0.5f;
    float centerY = (deviceY1 + deviceY2) * 0.5f;

    if (_lastTouchDistance > 0.0f) {
        _scale = powf(distance / _lastTouchDistance, 0.75f);
        _deltaX = [self calculateMovingAmount:deviceX1 - _lastX1 Vector2:deviceX2 - _lastX2];
        _deltaY = [self calculateMovingAmount:deviceY1 - _lastY1 Vector2:deviceY2 - _lastY2];
    } else {
        _scale = 1.0f;
        _deltaX = 0.0f;
        _deltaY = 0.0f;
    }

    _lastX = centerX;
    _lastY = centerY;
    _lastX1 = deviceX1;
    _lastY1 = deviceY1;
    _lastX2 = deviceX2;
    _lastY2 = deviceY2;
    _lastTouchDistance = distance;
    _touchSingle = false;
}

- (float)getFlickDistance {
    return [self calculateDistance:_startX TouchY1:_startY TouchX2:_lastX TouchY2:_lastY];
}


- (float)calculateDistance:(float)x1 TouchY1:(float)y1 TouchX2:(float)x2 TouchY2:(float)y2 {
    return sqrtf((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
}

- (float)calculateMovingAmount:(float)v1 Vector2:(float)v2 {
    if ((v1 > 0.0f) != (v2 > 0.0f)) {
        return 0.0f;
    }

    float sign = v1 > 0.0f ? 1.0f : -1.0f;
    float absoluteValue1 = fabsf(v1);
    float absoluteValue2 = fabsf(v2);
    return sign * ((absoluteValue1 < absoluteValue2) ? absoluteValue1 : absoluteValue2);
}

- (void)dealloc {
}

@end

