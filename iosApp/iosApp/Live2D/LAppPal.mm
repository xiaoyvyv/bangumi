/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at
 * https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#import "LAppPal.h"
#import "LAppDefine.h"
#import <Foundation/Foundation.h>
#import <fstream>
#import <iostream>
#import <stdarg.h>
#import <stdio.h>
#import <stdlib.h>
#import <sys/stat.h>

using std::endl;
using namespace Csm;
using namespace std;
using namespace LAppDefine;

double LAppPal::s_currentFrame = 0.0;
double LAppPal::s_lastFrame = 0.0;
double LAppPal::s_deltaTime = 0.0;


csmByte *LAppPal::LoadFileAsBytes(const std::string filePath, csmSizeInt *outSize) {
    const std::string prefix = "file://";

    if (filePath.rfind(prefix, 0) == 0) {
        // file:// 协议路径，走本地文件
        return LoadFileAsBytesFromFileURL(filePath, outSize);
    } else {
        // 否则走 Bundle 资源
        return LoadFileAsBytesFromBundle(filePath, outSize);
    }
}

csmByte *LAppPal::LoadFileAsBytesFromBundle(const string filePath, csmSizeInt *outSize) {
    int path_i = static_cast<int>(filePath.find_last_of("/") + 1);
    int ext_i = static_cast<int>(filePath.find_last_of("."));
    std::string pathname = filePath.substr(0, path_i);
    std::string extname = filePath.substr(ext_i, filePath.size() - ext_i);
    std::string filename = filePath.substr(path_i, ext_i - path_i);
    NSString *castFilePath = [[NSBundle mainBundle] pathForResource:[NSString stringWithUTF8String:filename.c_str()]
                                                             ofType:[NSString stringWithUTF8String:extname.c_str()]
                                                        inDirectory:[NSString stringWithUTF8String:pathname.c_str()]];

    NSData *data = [NSData dataWithContentsOfFile:castFilePath];

    if (data == nil) {
        PrintLogLn("File load failed : %s", filePath.c_str());
        return NULL;
    } else if (data.length == 0) {
        PrintLogLn("File is loaded but file size is zero : %s", filePath.c_str());
        return NULL;
    }

    NSUInteger len = [data length];
    Byte *byteData = (Byte *) malloc(len);
    memcpy(byteData, [data bytes], len);

    *outSize = static_cast<Csm::csmSizeInt>(len);
    return static_cast<Csm::csmByte *>(byteData);
}

csmByte *LAppPal::LoadFileAsBytesFromFileURL(const std::string filePath, csmSizeInt *outSize) {
    std::string cleanPath = filePath;
    const std::string prefix = "file://";
    if (cleanPath.rfind(prefix, 0) == 0) {
        cleanPath = cleanPath.substr(prefix.size());
    }

    NSString *nsPath = [NSString stringWithUTF8String:cleanPath.c_str()];

    NSData *data = [NSData dataWithContentsOfFile:nsPath];

    if (data == nil) {
        PrintLogLn("File load failed : %s", filePath.c_str());
        return NULL;
    } else if (data.length == 0) {
        PrintLogLn("File is loaded but file size is zero : %s", filePath.c_str());
        return NULL;
    }

    NSUInteger len = [data length];
    Byte *byteData = (Byte *) malloc(len);
    memcpy(byteData, [data bytes], len);

    *outSize = static_cast<Csm::csmSizeInt>(len);
    return static_cast<Csm::csmByte *>(byteData);
}

void LAppPal::ReleaseBytes(csmByte *byteData) {
    free(byteData);
}

void LAppPal::UpdateTime() {
    NSDate *now = [NSDate date];
    double unixtime = [now timeIntervalSince1970];
    s_currentFrame = unixtime;
    s_deltaTime = s_currentFrame - s_lastFrame;
    s_lastFrame = s_currentFrame;
}

void LAppPal::PrintLogLn(const csmChar *format, ...) {
    va_list args;
    Csm::csmChar buf[256];
    va_start(args, format);
    vsnprintf(buf, sizeof(buf), format, args); // 標準出力でレンダリング;
    NSLog(@"%@", [NSString stringWithCString:buf encoding:NSUTF8StringEncoding]);
    va_end(args);
}

void LAppPal::PrintMessageLn(const csmChar *message) {
    PrintLogLn("%s", message);
}
