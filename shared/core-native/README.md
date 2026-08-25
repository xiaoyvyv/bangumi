# Bangumi Multiplatform — Core Native 模块 (Live2D 渲染预制件)

`shared/core-native` 是 Bangumi Multiplatform 项目的核心 Native C++/JNI/cinterop 跨平台预制件模块，主要负责 Live2D Cubism SDK v5 渲染引擎在 Android 与 iOS 平台上的高性能透明渲染、模型解压与 Kotlin Multiplatform (Compose UI) 的无缝桥接。

---

## 目录
- [1. 模块架构与文件结构](#1-模块架构与文件结构)
- [2. Live2D SDK 放置路径说明](#2-live2d-sdk-放置路径说明)
- [3. GLSL 着色器嵌入头文件生成机制](#3-glsl-着色器嵌入头文件生成机制)
- [4. 开发与构建环境准备](#4-开发与构建环境准备)
- [5. 各平台 Native 库完整构建流程](#5-各平台-native-库完整构建流程)
  - [5.1 构建 Android 动态库 (.so)](#51-构建-android-动态库-so)
  - [5.2 构建 iOS 静态库 (.a)](#52-构建-ios-静态库-a)
  - [5.3 一键完整编译构建脚本 (`build_live2d.sh`)](#53-一键完整编译构建脚本-build_live2dsh)
- [6. Compose Multiplatform 桥接设计](#6-compose-multiplatform-桥接设计)
- [7. 关键技术细节与坑点总结](#7-关键技术细节与坑点总结)

---

## 1. 模块架构与文件结构

```text
shared/core-native/
├── build_live2d.sh                   # [一键构建] Live2D 跨平台 Native 库自动化编译脚本
├── build.gradle.kts                   # KMP Gradle 构建配置 (JNI & cinterop 绑定)
├── core_native.podspec                # iOS CocoaPods 配置
├── native/
│   ├── CubismSdkForNative-5-r.5/      # Live2D 官方 Native SDK v5 核心文件目录
│   │   ├── Core/                      # 官方 C Core 头文件及各平台预编译静态库
│   │   └── Framework/                 # 官方 C++ Framework 渲染与动作逻辑源码
│   └── ios/                           # iOS 生成的合并静态库目录
│       ├── iphoneos/liblive2d_native.a
│       └── iphonesimulator/liblive2d_native.a
└── src/
    ├── androidMain/
    │   ├── jniLibs/                   # Android 生成的 NDK 动态库
    │   │   ├── arm64-v8a/liblive2d_native.so
    │   │   └── x86_64/liblive2d_native.so
    │   └── kotlin/.../Live2D.android.kt # TextureView + EGL14 渲染线程封装
    ├── cpp/                           # 跨平台 C++ 核心引擎源码
    │   ├── CMakeLists.txt             # 跨平台 CMake 构建规则
    │   ├── generate_shaders.sh        # GLSL 着色器自动生成 Header 脚本
    │   ├── live2d_shaders.h           # 内嵌生成的 36 个 GLSL 着色器内存头文件
    │   ├── live2d_renderer.cpp        # 模型/MOC3/纹理/动作管理及 GLES 渲染循环
    │   ├── live2d_renderer.h          # 跨平台导出 C API 接口
    │   ├── live2d_jni.cpp             # Android JNI Bridge 方法实现
    │   └── miniz.c / miniz.h          # 高性能无依赖 ZIP 原生解压库
    └── iosMain/
        ├── cinterop/live2d.def        # Kotlin Native cinterop 定义
        └── kotlin/.../Live2D.ios.kt   # UIKitView + EAGLContext 渲染组件
```

---

## 2. Live2D SDK 放置路径说明

本预制件直接集成了 **Live2D Cubism SDK for Native (v5-r.5)**，存放于以下相对路径：

`shared/core-native/native/CubismSdkForNative-5-r.5/`

### 目录说明：
1. **`Core/`**：包含 Live2D 官方闭源的核心解析库与 C 头文件：
   - `Core/include/Live2DCubismCore.h`：核心 C 头文件。
   - `Core/lib/android/`：Android 平台各 ABI (`arm64-v8a`, `x86_64`) 的 `libLive2DCubismCore.a`。
   - `Core/lib/ios/`：iOS 平台 (`Release-iphoneos`, `Release-iphonesimulator-arm64`) 的 `libLive2DCubismCore.a`。
2. **`Framework/`**：开源的 C++ 框架层，负责模型的绘制算子、Blend Mode、Motion/Expression 管理器及纹理绑定。
   - `Framework/src/`：框架源码，由 `src/cpp/CMakeLists.txt` 自动扫描并参与编译。

---

## 3. GLSL 着色器嵌入头文件生成机制

为了避免在 Android 和 iOS 平台上因读取外部磁盘文件或 Asset 资源文件带来的打包复杂性及 I/O 延迟，渲染器将 Live2D Cubism 框架需要的 **36 个 OpenGL ES 2.0 着色器文件**（`.vert` 与 `.frag`）直接打入 C++ 内存中。

### 自动生成脚本路径：
`shared/core-native/src/cpp/generate_shaders.sh`

### 工作原理：
1. 扫描 `native/CubismSdkForNative-5-r.5/Framework/src/Rendering/OpenGL/Shaders/StandardES/` 目录下的所有 GLSL 文件；
2. 将着色器源码提取为 C++11 Raw String Literal (`R"(...)"`) 格式；
3. 输出到 `shared/core-native/src/cpp/live2d_shaders.h` 中的全局字符串映射 `g_embeddedShaders`；
4. 渲染器从内存字典中毫秒级读取着色器代码，完全免去文件路径查找。

#### 手动执行生成：
```bash
cd shared/core-native/src/cpp
./generate_shaders.sh
```

---

## 4. 开发与构建环境准备

在开始编译前，请确保开发环境满足以下条件：

1. **操作系统**：macOS (推荐 macOS 14+ / Apple Silicon 或 Intel)
2. **CMake**：3.22.1 及以上（可通过 `brew install cmake` 安装）
3. **Android NDK**：`26.3.11579264` 或更高版本，并在环境变量中配置 `ANDROID_NDK_HOME`：
   ```bash
   export ANDROID_NDK_HOME=$HOME/Library/Android/sdk/ndk/26.3.11579264
   ```
4. **Xcode & Xcode Command Line Tools**：Xcode 15+ / 16+（安装命令：`xcode-select --install`）
5. **JDK & Gradle**：JDK 17+

---

## 5. 各平台 Native 库完整构建流程

### 5.1 构建 Android 动态库 (.so)

Android 侧预制件支持主流的 Native ABI 架构（`arm64-v8a`、`x86_64`、`x86`）。

> **关于 `armeabi-v7a` 的说明**：
> Live2D 官方 SDK v5 明确废弃并移除了 32 位 `armeabi-v7a` 静态库（详见 SDK CHANGELOG：`Remove armeabi-v7a from architecture support`）。因此 Android 端提供 `arm64-v8a`（主流 64 位真机）、`x86_64`（64 位模拟器）与 `x86`（32 位模拟器）的 `.so` 库构建。

#### 16 KB 页对齐要求 (Android 15+)
从 Android 15 (API 35) 开始，Google Play 要求所有应用及 Native `.so` 库支持 16 KB 页面对齐。在 `CMakeLists.txt` 中已强制包含链接参数：
```cmake
target_link_options(live2d_native PRIVATE "-Wl,-z,max-page-size=16384")
```

#### 手动编译步骤：

**1. 构建 ARM64 (`arm64-v8a`)**
```bash
export ANDROID_NDK_HOME=$HOME/Library/Android/sdk/ndk/26.3.11579264

rm -rf shared/core-native/build/live2d/android-arm64-v8a
cmake -S shared/core-native/src/cpp -B shared/core-native/build/live2d/android-arm64-v8a \
  -DANDROID_ABI=arm64-v8a \
  -DANDROID_NDK=$ANDROID_NDK_HOME \
  -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake \
  -DANDROID_PLATFORM=android-21 \
  -DCMAKE_BUILD_TYPE=Release

cmake --build shared/core-native/build/live2d/android-arm64-v8a --config Release

# 复制产物到 jniLibs
mkdir -p shared/core-native/src/androidMain/jniLibs/arm64-v8a
cp shared/core-native/build/live2d/android-arm64-v8a/liblive2d_native.so shared/core-native/src/androidMain/jniLibs/arm64-v8a/
```

**2. 构建 x86_64 (`x86_64`)**
```bash
rm -rf shared/core-native/build/live2d/android-x86_64
cmake -S shared/core-native/src/cpp -B shared/core-native/build/live2d/android-x86_64 \
  -DANDROID_ABI=x86_64 \
  -DANDROID_NDK=$ANDROID_NDK_HOME \
  -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake \
  -DANDROID_PLATFORM=android-21 \
  -DCMAKE_BUILD_TYPE=Release

cmake --build shared/core-native/build/live2d/android-x86_64 --config Release

# 复制产物到 jniLibs
mkdir -p shared/core-native/src/androidMain/jniLibs/x86_64
cp shared/core-native/build/live2d/android-x86_64/liblive2d_native.so shared/core-native/src/androidMain/jniLibs/x86_64/
```

**3. 构建 x86 (`x86`)**
```bash
rm -rf shared/core-native/build/live2d/android-x86
cmake -S shared/core-native/src/cpp -B shared/core-native/build/live2d/android-x86 \
  -DANDROID_ABI=x86 \
  -DANDROID_NDK=$ANDROID_NDK_HOME \
  -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake \
  -DANDROID_PLATFORM=android-21 \
  -DCMAKE_BUILD_TYPE=Release

cmake --build shared/core-native/build/live2d/android-x86 --config Release

# 复制产物到 jniLibs
mkdir -p shared/core-native/src/androidMain/jniLibs/x86
cp shared/core-native/build/live2d/android-x86/liblive2d_native.so shared/core-native/src/androidMain/jniLibs/x86/
```

---

### 5.2 构建 iOS 静态库 (.a)

iOS 平台需要为 `iphonesimulator`（模拟器 arm64）与 `iphoneos`（真机 arm64）编译静态库。

#### 为什么需要使用 `libtool -static` 进行库合并？
Live2D SDK 核心 C 语言函数（如 `csmGetDrawableBlendModes`）位于官方预编译静态库 `libLive2DCubismCore.a` 中。由于 CMake `add_library(... STATIC)` 默认不会将依赖的第三方静态库打包拆解进自身的 `.a` 目标中，因此**必须**使用 macOS 原生 `libtool -static` 命令将 `liblive2d_native.a` 与 `libLive2DCubismCore.a` 合并为单体完整的静态库，否则 Kotlin Native 链接时会出现未定义符号报错。

#### 手动编译步骤：

**构建 iOS 模拟器 (`iphonesimulator` arm64)**
```bash
rm -rf shared/core-native/build/live2d/ios-iphonesimulator
cmake -S shared/core-native/src/cpp -B shared/core-native/build/live2d/ios-iphonesimulator \
  -DCMAKE_SYSTEM_NAME=iOS \
  -DCMAKE_OSX_SYSROOT=iphonesimulator \
  -DCMAKE_OSX_ARCHITECTURES=arm64 \
  -DCMAKE_OSX_DEPLOYMENT_TARGET=14.0 \
  -DIOS_BUILD=ON \
  -DIOS_PLATFORM=SIMULATORARM64 \
  -DCMAKE_BUILD_TYPE=Release

cmake --build shared/core-native/build/live2d/ios-iphonesimulator --config Release

# 使用 libtool 合并 Live2D Cubism Core
mkdir -p shared/core-native/native/ios/iphonesimulator
libtool -static -o shared/core-native/native/ios/iphonesimulator/liblive2d_native.a \
  shared/core-native/build/live2d/ios-iphonesimulator/liblive2d_native.a \
  shared/core-native/native/CubismSdkForNative-5-r.5/Core/lib/ios/Release-iphonesimulator-arm64/libLive2DCubismCore.a
```

**构建 iOS 真机 (`iphoneos` arm64)**
```bash
rm -rf shared/core-native/build/live2d/ios-iphoneos
cmake -S shared/core-native/src/cpp -B shared/core-native/build/live2d/ios-iphoneos \
  -DCMAKE_SYSTEM_NAME=iOS \
  -DCMAKE_OSX_SYSROOT=iphoneos \
  -DCMAKE_OSX_ARCHITECTURES=arm64 \
  -DCMAKE_OSX_DEPLOYMENT_TARGET=14.0 \
  -DIOS_BUILD=ON \
  -DIOS_PLATFORM=OS \
  -DCMAKE_BUILD_TYPE=Release

cmake --build shared/core-native/build/live2d/ios-iphoneos --config Release

# 使用 libtool 合并 Live2D Cubism Core
mkdir -p shared/core-native/native/ios/iphoneos
libtool -static -o shared/core-native/native/ios/iphoneos/liblive2d_native.a \
  shared/core-native/build/live2d/ios-iphoneos/liblive2d_native.a \
  shared/core-native/native/CubismSdkForNative-5-r.5/Core/lib/ios/Release-iphoneos/libLive2DCubismCore.a
```

---

### 5.3 构建 macOS Desktop 动态库 (.dylib)

Desktop JVM (Compose Desktop) 平台需要 macOS 原生动态库 `liblive2d_native.dylib`。

#### 手动编译步骤：
```bash
rm -rf shared/core-native/build/live2d/macos-arm64
cmake -S shared/core-native/src/cpp -B shared/core-native/build/live2d/macos-arm64 \
  -DMACOS_DESKTOP_BUILD=ON \
  -DCMAKE_OSX_ARCHITECTURES=arm64 \
  -DCMAKE_BUILD_TYPE=Release

cmake --build shared/core-native/build/live2d/macos-arm64 --config Release

# 复制产物到 native/macos
mkdir -p shared/core-native/native/macos
cp shared/core-native/build/live2d/macos-arm64/liblive2d_native.dylib shared/core-native/native/macos/
```

---

### 5.4 一键完整编译构建脚本 (`build_live2d.sh`)

预制件根目录下已提供预建的自动化编译脚本 [`build_live2d.sh`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/shared/core-native/build_live2d.sh)。

#### 执行命令：
```bash
./shared/core-native/build_live2d.sh
```

该脚本将按顺序自动完成：
1. 自动生成最新的 GLSL 着色器 C++ 头文件 `live2d_shaders.h`；
2. 编译 Android `arm64-v8a`、`x86_64` 与 `x86` 的 `.so` 库，并自动复制到 `src/androidMain/jniLibs/`；
3. 编译 iOS `iphonesimulator` 和 `iphoneos` 的 `.a` 库，通过 `libtool` 自动合并官方 Core 并复制到 `native/ios/`；
4. 编译 macOS Desktop `arm64` 的 `liblive2d_native.dylib` 动态库并自动复制到 `native/macos/`。

---

## 6. Compose Multiplatform 桥接设计

### 6.1 Android 侧 (`Live2D.android.kt`)
- 使用 Compose `AndroidView` 嵌入自定义 `Live2DTextureView` (`TextureView`)；
- **透明通道支持**：设置 `isOpaque = false` 允许 Live2D 模型与其下方的 Compose 背景完全混合；
- **独立渲染线程**：由 `Live2DRenderThread` 维护独立 EGL14 上下文与 60 FPS 渲染循环，避免阻塞主 UI 线程；
- **生命周期与状态保持**：`Live2DState` 自动记录当前加载的模型路径，页面跳转或视图重建时自动恢复渲染。

### 6.2 iOS 侧 (`Live2D.ios.kt`)
- 使用 Compose `UIKitView` 结合 `GLKView` / `EAGLContext`；
- 通过 Kotlin Native `cinterop` 自动绑定 `live2d_renderer.h` C API；
- 支持手点交互与触摸事件透传。

---

## 7. 关键技术细节与坑点总结

### 1. 着色器重编译导致加载冻结 (2.4s $\to$ 5ms)
Live2D Cubism SDK 包含 420 个 Blend/Mask 着色器变体。旧逻辑在每次重新加载模型时调用 `DeleteInstance()` 强制删除了静态 Shader 实例，导致 OpenGL 线程陷入长达 2.4 秒的 `glCompileShader` + `glLinkProgram` 卡顿。优化后在相同 EGL Context 下直接复用已编译着色器，加载性能提升 500 倍。

### 2. 优先度预约泄露导致偶现一帧卡死
在 C++ `StartMotion()` 方法中，若在确定动作存在前提前调用 `_motionManager->ReserveMotion(priority)`，遇到未找到对应名称的动作时，`_reservePriority` 将残留无法被清除。这会导致随后所有帧的动作预约被拒（$1 \le 1$），模型停留在第 1 帧姿势。现已修正为确认动作存在后再预约优先度，并增加了保底首个动作循环播放机制。

### 3. iOS 链接未定义符号 (`_csmGetDrawableBlendModes`)
使用 CMake 构建 iOS 静态库时，需通过 macOS `libtool -static` 命令显式将 `libLive2DCubismCore.a` 打包合并入 `liblive2d_native.a`，避免 Kotlin cinterop 缺失底层 Core C 函数符号。

### 4. iOS Deployment Target 版本不匹配警告
在 `CMakeLists.txt` 中显式指定 `set(CMAKE_OSX_DEPLOYMENT_TARGET "14.0" CACHE STRING "" FORCE)`，确保生成的 Native `.o` 目标文件与 Kotlin Native 的 `ios.deploymentTarget = "14"` 保持一致。
