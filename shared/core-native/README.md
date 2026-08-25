# Bangumi Multiplatform — Core Native 模块 (Live2D 渲染预制件)

`shared/core-native` 是 Bangumi Multiplatform 项目的核心 Native C++/JNI/cinterop 跨平台模块，主要负责 Live2D Cubism SDK v5 渲染引擎在 **Android**、**iOS**、**macOS**、**Windows** 与 **Linux** 平台上的高性能透明渲染、模型解压与 Kotlin Multiplatform (Compose UI / Desktop) 的无缝桥接。

---

## 目录
- [1. 模块架构与文件结构](#1-模块架构与文件结构)
- [2. Live2D SDK 放置路径说明](#2-live2d-sdk-放置路径说明)
- [3. GLSL 着色器嵌入头文件生成机制](#3-glsl-着色器嵌入头文件生成机制)
- [4. 开发与构建环境准备](#4-开发与构建环境准备)
- [5. Native 库构建指南](#5-native-库构建指南)
  - [5.1 主控制脚本 (`build_live2d.sh`)](#51-主控制脚本-build_live2dsh)
  - [5.2 模块化子构建脚本 (`scripts/`)](#52-模块化子构建脚本-scripts)
  - [5.3 CI / CD 自动化构建工作流 (`.github/workflows/build_live2d.yml`)](#53-ci--cd-自动化构建工作流-githubworkflowsbuild_live2dyml)
- [6. Compose Multiplatform 桥接设计](#6-compose-multiplatform-桥接设计)
- [7. 关键技术细节与坑点总结](#7-关键技术细节与坑点总结)

---

## 1. 模块架构与文件结构

```text
shared/core-native/
├── build_live2d.sh                   # [主入口脚本] Live2D 跨平台 Native 库自动化构建主控制脚本
├── scripts/                          # [模块化脚本目录]
│   ├── build_android.sh              # 构建 Android (.so) 动态库 (arm64-v8a, x86_64, x86)
│   ├── build_macos.sh                # 构建 macOS (.dylib) 动态库
│   ├── build_ios.sh                  # 构建 iOS (.a) 静态库 (iphonesimulator, iphoneos)
│   ├── build_windows.sh              # 构建 Windows (.dll) 动态库
│   └── build_linux.sh                # 构建 Linux (.so) 动态库
├── build.gradle.kts                   # KMP Gradle 构建配置 (JNI & cinterop 绑定)
├── core_native.podspec                # iOS CocoaPods 配置
├── native/
│   ├── CubismSdkForNative-5-r.5/      # Live2D 官方 Native SDK v5 核心文件目录
│   │   ├── Core/                      # 官方 C Core 头文件及各平台预编译静态库
│   │   └── Framework/                 # 官方 C++ Framework 渲染与动作逻辑源码
│   ├── ios/                           # iOS 生成的合并静态库目录
│   │   ├── iphoneos/liblive2d_native.a
│   │   └── iphonesimulator/liblive2d_native.a
│   ├── macos/liblive2d_native.dylib
│   ├── windows/live2d_native.dll
│   └── linux/liblive2d_native.so
└── src/
    ├── androidMain/
    │   ├── jniLibs/                   # Android 生成的 NDK 动态库
    │   │   ├── arm64-v8a/liblive2d_native.so
    │   │   ├── x86_64/liblive2d_native.so
    │   │   └── x86/liblive2d_native.so
    │   └── kotlin/.../Live2D.android.kt
    ├── jvmMain/
    │   ├── resources/native/          # Desktop (JVM) 打包内嵌 Native 动态库资源
    │   │   ├── macos/liblive2d_native.dylib
    │   │   ├── windows/live2d_native.dll
    │   │   └── linux/liblive2d_native.so
    │   └── kotlin/.../Live2D.jvm.kt   # Compose Desktop 离屏 Framebuffer 渲染 Loop
    ├── cpp/                           # 跨平台 C++ 核心渲染引擎源码
    │   ├── CMakeLists.txt             # 跨平台 CMake 构建规则
    │   ├── generate_shaders.sh        # GLSL 着色器自动生成 Header 脚本
    │   ├── live2d_shaders.h           # 内嵌生成的 36 个 GLSL 着色器内存头文件
    │   ├── live2d_renderer.cpp        # 跨平台离屏/窗口 GLES/GL 渲染引擎核心
    │   ├── live2d_renderer.h          # 跨平台导出 C API 接口
    │   ├── live2d_jni.cpp             # Android / Desktop JNI Bridge 方法实现
    │   └── miniz.c / miniz.h          # 高性能无依赖 ZIP 原生解压库
    └── iosMain/
        ├── cinterop/live2d.def        # Kotlin Native cinterop 定义
        └── kotlin/.../Live2D.ios.kt   # UIKitView + EAGLContext 渲染组件
```

---

## 2. Live2D SDK 放置路径说明

本预制件直接集成了 **Live2D Cubism SDK for Native (v5-r.5)**，存放于以下相对路径：

`shared/core-native/native/CubismSdkForNative-5-r.5/`

### 目录结构：
1. **`Core/`**：包含 Live2D 官方核心解析库与 C 头文件：
   - `Core/include/Live2DCubismCore.h`：核心 C 头文件。
   - `Core/lib/android/`：Android 平台各 ABI 的 `libLive2DCubismCore.a`。
   - `Core/lib/ios/`：iOS 平台 (`Release-iphoneos`, `Release-iphonesimulator-arm64`) 的 `libLive2DCubismCore.a`。
2. **`Framework/`**：开源的 C++ 框架层，负责模型的绘制算子、Blend Mode、Motion/Expression 管理器及纹理绑定。

---

## 3. GLSL 着色器嵌入头文件生成机制

着色器源码文件直接托管在项目源码路径 [`shared/core-native/src/cpp/shaders/`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/shared/core-native/src/cpp/shaders/) 中。

渲染器在编译前通过 [`generate_shaders.sh`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/shared/core-native/src/cpp/generate_shaders.sh) 前置脚本，将这 **36 个 GLSL 着色器源码**直接打入 C++ 头文件 `live2d_shaders.h` 内存字典中。这完全消除了对解压后的官方 SDK 内部深层着色器路径的依赖，并同时支持 Mobile GLES 2.0 与 Desktop OpenGL 语法自动兼容补丁。

### 运行前置脚本生成着色器头文件：
```bash
./shared/core-native/src/cpp/generate_shaders.sh
```

---

## 4. 开发与构建环境准备

根据所需构建的目标平台，确保环境满足以下需求：

| 构建目标 | 推荐操作系统 | 核心依赖与工具链 |
| :--- | :--- | :--- |
| **Android (`.so`)** | macOS / Linux / Windows | JDK 17+、CMake 3.22+、Android NDK (`26.3.11579264`+) |
| **macOS (`.dylib`)** | macOS | JDK 17+、CMake 3.22+、Xcode Command Line Tools |
| **iOS (`.a`)** | macOS | CMake 3.22+、Xcode 15+、`libtool` |
| **Windows (`.dll`)** | Windows | CMake 3.22+、MSVC (Visual Studio 2022) 或 MinGW-w64 |
| **Linux (`.so`)** | Linux (Ubuntu 22.04+) | CMake 3.22+、`build-essential`、`libgl1-mesa-dev`、`libx11-dev` |

---

## 5. Native 库构建指南

### 5.1 主控制脚本 (`build_live2d.sh`)

项目提供了统一的跨平台构建命令入口 [`build_live2d.sh`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/shared/core-native/build_live2d.sh)。

```bash
# 默认构建当前宿主 OS 兼容的所有平台产物
./shared/core-native/build_live2d.sh

# 指定单个目标平台独立构建
./shared/core-native/build_live2d.sh android    # 构建 Android (arm64-v8a, x86_64, x86) .so
./shared/core-native/build_live2d.sh macos      # 构建 macOS Desktop .dylib
./shared/core-native/build_live2d.sh ios        # 构建 iOS .a 静态库 (包含 Core 合并)
./shared/core-native/build_live2d.sh windows    # 构建 Windows Desktop .dll
./shared/core-native/build_live2d.sh linux      # 构建 Linux Desktop .so
```

---

### 5.2 模块化子构建脚本 (`scripts/`)

主脚本按平台分发调用 [`scripts/`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/shared/core-native/scripts/) 目录下的专属构建脚本。脚本会自动将编译产物输出并复制到项目所需的资源目录中：

| 平台 | 构建脚本 | 输出与分发路径 |
| :--- | :--- | :--- |
| **Android** | [`scripts/build_android.sh`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/shared/core-native/scripts/build_android.sh) | `src/androidMain/jniLibs/{abi}/liblive2d_native.so` |
| **macOS** | [`scripts/build_macos.sh`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/shared/core-native/scripts/build_macos.sh) | `native/macos/` & `src/jvmMain/resources/native/macos/` |
| **iOS** | [`scripts/build_ios.sh`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/shared/core-native/scripts/build_ios.sh) | `native/ios/iphoneos/` & `native/ios/iphonesimulator/` |
| **Windows** | [`scripts/build_windows.sh`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/shared/core-native/scripts/build_windows.sh) | `native/windows/` & `src/jvmMain/resources/native/windows/` |
| **Linux** | [`scripts/build_linux.sh`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/shared/core-native/scripts/build_linux.sh) | `native/linux/` & `src/jvmMain/resources/native/linux/` |

---

### 5.3 CI / CD 自动化构建工作流 (`.github/workflows/build_live2d.yml`)

项目集成了 GitHub Actions 自动化 CI 流程 [`.github/workflows/build_live2d.yml`](file:///Users/why/AndroidStudioProjects/bangumi-multiplatform/.github/workflows/build_live2d.yml)：

- **触发时机**：代码推送到 `main` 分支或提交针对 `shared/core-native/` 的 Pull Request 时触发。
- **构建矩阵**：
  - `macos-latest`：自动化生成 macOS `.dylib`、iOS `.a` 静态库以及 Android `.so`；
  - `windows-latest`：自动化生成 Windows `live2d_native.dll`；
  - `ubuntu-latest`：自动化生成 Linux `liblive2d_native.so`。
- **Artifacts 托管**：编译成功的全部平台 Native 二进制文件将自动存为 GitHub Actions 构件，供团队下载或 Release 打包。

---

## 6. Compose Multiplatform 桥接设计

### 6.1 Android 侧 (`Live2D.android.kt`)
- 使用 Compose `AndroidView` 嵌入自定义 `Live2DTextureView` (`TextureView`)；
- **透明通道支持**：设置 `isOpaque = false` 允许 Live2D 模型与其下方的 Compose 背景完全混合；
- **独立渲染线程**：由 `Live2DRenderThread` 维护独立 EGL14 上下文与 60 FPS 渲染循环。

### 6.2 Desktop JVM 侧 (`Live2D.jvm.kt`)
- 使用纯 Compose 离屏渲染 Loop (`BoxWithConstraints` + `ImageBitmap`)；
- 通过 C++ `RenderToPixels` 原生离屏 FBO 渲染直接生成 RGBA/ARGB 像素流；
- **跨平台离屏上下文**：macOS 自动采用 `CGL`，Windows 自动采用 Win32 + `WGL`，Linux 自动采用 X11 + `GLX`。

### 6.3 iOS 侧 (`Live2D.ios.kt`)
- 使用 Compose `UIKitView` 结合 `GLKView` / `EAGLContext`；
- 通过 Kotlin Native `cinterop` 自动绑定 `live2d_renderer.h` C API。

---

## 7. 关键技术细节与坑点总结

### 1. 着色器重编译导致加载冻结 (2.4s $\to$ 5ms)
Live2D Cubism SDK 包含 420 个 Blend/Mask 着色器变体。旧逻辑在每次重新加载模型时调用 `DeleteInstance()` 强制删除了静态 Shader 实例，导致 OpenGL 线程陷入长达 2.4 秒的 `glCompileShader` + `glLinkProgram` 卡顿。优化后在相同 OpenGL / EGL Context 下直接复用已编译着色器，加载性能提升 500 倍。

### 2. 桌面端 (OpenGL Standard) 像素通道偏色修复
`glReadPixels` 在 Little-Endian 架构下读取的像素为 RGBA 格式，转换为 Java `BufferedImage` (TYPE_INT_ARGB_PRE) 时，使用位运算 `(top & 0xFF00FF00) | ((top & 0x00FF0000) >> 16) | ((top & 0x000000FF) << 16)` 交换 Red 与 Blue 通道，确保画面无偏色。

### 3. GLSL 着色器桌面端 Desktop GL 兼容补丁
- 自动屏蔽桌面端 OpenGL 视作保留字 (Reserved Word) 的 `precision ...` 语句；
- 剥离子着色器片段中的 `#version` 标头，解决拼接着色器时出现的 `#version must occur before any other statement` 错误。

### 4. iOS 链接未定义符号 (`_csmGetDrawableBlendModes`)
使用 `scripts/build_ios.sh` 构建 iOS 静态库时，通过 macOS `libtool -static` 命令显式将 `libLive2DCubismCore.a` 打包合并入 `liblive2d_native.a`，避免 Kotlin cinterop 缺失底层 Core C 函数符号。
