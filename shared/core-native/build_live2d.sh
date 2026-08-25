#!/usr/bin/env bash
set -e

# Bangumi Multiplatform - Live2D Core Native Cross-Platform Build Script
# This script compiles liblive2d_native for Android (arm64-v8a, x86_64, x86) and iOS (iphonesimulator, iphoneos).
# All CMake build output data is saved under shared/core-native/build/ with platform prefix naming.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_BASE_DIR="${SCRIPT_DIR}/build"
mkdir -p "${BUILD_BASE_DIR}"

# -----------------------------------------------------------------------------
# Configuration Variables (Defined here, can be overridden via environment)
# -----------------------------------------------------------------------------
NDK_VERSION="${NDK_VERSION:-26.3.11579264}"
ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-21}"
IOS_DEPLOYMENT_TARGET="${IOS_DEPLOYMENT_TARGET:-14.0}"
CUBISM_SDK_NAME="${CUBISM_SDK_NAME:-CubismSdkForNative-5-r.5}"
SHOW_WARNINGS="${SHOW_WARNINGS:-false}"

# -----------------------------------------------------------------------------
# Warning Suppression Flags
# -----------------------------------------------------------------------------
EXTRA_CMAKE_FLAGS=()
if [ "${SHOW_WARNINGS}" = "false" ]; then
  EXTRA_CMAKE_FLAGS+=(
    "-Wno-dev"
    "-DCMAKE_WARN_DEPRECATED=OFF"
    "-DCMAKE_C_FLAGS=-w"
    "-DCMAKE_CXX_FLAGS=-w"
  )
fi

# -----------------------------------------------------------------------------
# OS Detection & Default ANDROID_NDK_HOME Resolution
# -----------------------------------------------------------------------------
OS_NAME="$(uname -s 2>/dev/null || echo "Unknown")"

if [ -z "${ANDROID_NDK_HOME}" ]; then
  case "${OS_NAME}" in
    Darwin*)
      export ANDROID_NDK_HOME="$HOME/Library/Android/sdk/ndk/${NDK_VERSION}"
      ;;
    MINGW*|CYGWIN*|MSYS*|Windows_NT)
      USER_WIN="${USERNAME:-$USER}"
      export ANDROID_NDK_HOME="C:/Users/${USER_WIN}/AppData/Local/Android/Sdk/ndk/${NDK_VERSION}"
      ;;
    Linux*)
      export ANDROID_NDK_HOME="$HOME/Android/Sdk/ndk/${NDK_VERSION}"
      ;;
    *)
      export ANDROID_NDK_HOME="$HOME/Library/Android/sdk/ndk/${NDK_VERSION}"
      ;;
  esac
fi

echo "=========================================="
echo " OS Detected     : ${OS_NAME}"
echo " NDK Version     : ${NDK_VERSION}"
echo " NDK Location    : ${ANDROID_NDK_HOME}"
echo " Android Target  : ${ANDROID_PLATFORM}"
echo " iOS Target OS   : ${IOS_DEPLOYMENT_TARGET}"
echo " Cubism SDK      : ${CUBISM_SDK_NAME}"
echo " Show Warnings   : ${SHOW_WARNINGS}"
echo "=========================================="

echo "=========================================="
echo " 1. Generating Embedded GLSL Shaders Header "
echo "=========================================="
"${SCRIPT_DIR}/src/cpp/generate_shaders.sh"

echo "=========================================="
echo " 2. Building Android arm64-v8a (.so)       "
echo "=========================================="
rm -rf "${BUILD_BASE_DIR}/android-arm64-v8a"
cmake -S "${SCRIPT_DIR}/src/cpp" -B "${BUILD_BASE_DIR}/android-arm64-v8a" \
  "${EXTRA_CMAKE_FLAGS[@]}" \
  -DANDROID_ABI=arm64-v8a \
  -DANDROID_NDK="$ANDROID_NDK_HOME" \
  -DCMAKE_TOOLCHAIN_FILE="$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake" \
  -DANDROID_PLATFORM="$ANDROID_PLATFORM" \
  -DCMAKE_BUILD_TYPE=Release
cmake --build "${BUILD_BASE_DIR}/android-arm64-v8a" --config Release
mkdir -p "${SCRIPT_DIR}/src/androidMain/jniLibs/arm64-v8a"
cp "${BUILD_BASE_DIR}/android-arm64-v8a/liblive2d_native.so" "${SCRIPT_DIR}/src/androidMain/jniLibs/arm64-v8a/"

echo "=========================================="
echo " 3. Building Android x86_64 (.so)          "
echo "=========================================="
rm -rf "${BUILD_BASE_DIR}/android-x86_64"
cmake -S "${SCRIPT_DIR}/src/cpp" -B "${BUILD_BASE_DIR}/android-x86_64" \
  "${EXTRA_CMAKE_FLAGS[@]}" \
  -DANDROID_ABI=x86_64 \
  -DANDROID_NDK="$ANDROID_NDK_HOME" \
  -DCMAKE_TOOLCHAIN_FILE="$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake" \
  -DANDROID_PLATFORM="$ANDROID_PLATFORM" \
  -DCMAKE_BUILD_TYPE=Release
cmake --build "${BUILD_BASE_DIR}/android-x86_64" --config Release
mkdir -p "${SCRIPT_DIR}/src/androidMain/jniLibs/x86_64"
cp "${BUILD_BASE_DIR}/android-x86_64/liblive2d_native.so" "${SCRIPT_DIR}/src/androidMain/jniLibs/x86_64/"

echo "=========================================="
echo " 4. Building Android x86 (.so)             "
echo "=========================================="
rm -rf "${BUILD_BASE_DIR}/android-x86"
cmake -S "${SCRIPT_DIR}/src/cpp" -B "${BUILD_BASE_DIR}/android-x86" \
  "${EXTRA_CMAKE_FLAGS[@]}" \
  -DANDROID_ABI=x86 \
  -DANDROID_NDK="$ANDROID_NDK_HOME" \
  -DCMAKE_TOOLCHAIN_FILE="$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake" \
  -DANDROID_PLATFORM="$ANDROID_PLATFORM" \
  -DCMAKE_BUILD_TYPE=Release
cmake --build "${BUILD_BASE_DIR}/android-x86" --config Release
mkdir -p "${SCRIPT_DIR}/src/androidMain/jniLibs/x86"
cp "${BUILD_BASE_DIR}/android-x86/liblive2d_native.so" "${SCRIPT_DIR}/src/androidMain/jniLibs/x86/"

if [[ "${OS_NAME}" == Darwin* ]]; then
  echo "=========================================="
  echo " 5. Building iOS Simulator arm64 (.a)      "
  echo "=========================================="
  rm -rf "${BUILD_BASE_DIR}/ios-iphonesimulator"
  cmake -S "${SCRIPT_DIR}/src/cpp" -B "${BUILD_BASE_DIR}/ios-iphonesimulator" \
    "${EXTRA_CMAKE_FLAGS[@]}" \
    -DCMAKE_SYSTEM_NAME=iOS \
    -DCMAKE_OSX_SYSROOT=iphonesimulator \
    -DCMAKE_OSX_ARCHITECTURES=arm64 \
    -DCMAKE_OSX_DEPLOYMENT_TARGET="$IOS_DEPLOYMENT_TARGET" \
    -DIOS_BUILD=ON \
    -DIOS_PLATFORM=SIMULATORARM64 \
    -DCMAKE_BUILD_TYPE=Release
  cmake --build "${BUILD_BASE_DIR}/ios-iphonesimulator" --config Release
  mkdir -p "${SCRIPT_DIR}/native/ios/iphonesimulator"
  libtool -static -o "${SCRIPT_DIR}/native/ios/iphonesimulator/liblive2d_native.a" \
    "${BUILD_BASE_DIR}/ios-iphonesimulator/liblive2d_native.a" \
    "${SCRIPT_DIR}/native/${CUBISM_SDK_NAME}/Core/lib/ios/Release-iphonesimulator-arm64/libLive2DCubismCore.a"

  echo "=========================================="
  echo " 6. Building iOS Device arm64 (.a)         "
  echo "=========================================="
  rm -rf "${BUILD_BASE_DIR}/ios-iphoneos"
  cmake -S "${SCRIPT_DIR}/src/cpp" -B "${BUILD_BASE_DIR}/ios-iphoneos" \
    "${EXTRA_CMAKE_FLAGS[@]}" \
    -DCMAKE_SYSTEM_NAME=iOS \
    -DCMAKE_OSX_SYSROOT=iphoneos \
    -DCMAKE_OSX_ARCHITECTURES=arm64 \
    -DCMAKE_OSX_DEPLOYMENT_TARGET="$IOS_DEPLOYMENT_TARGET" \
    -DIOS_BUILD=ON \
    -DIOS_PLATFORM=OS \
    -DCMAKE_BUILD_TYPE=Release
  cmake --build "${BUILD_BASE_DIR}/ios-iphoneos" --config Release
  mkdir -p "${SCRIPT_DIR}/native/ios/iphoneos"
  libtool -static -o "${SCRIPT_DIR}/native/ios/iphoneos/liblive2d_native.a" \
    "${BUILD_BASE_DIR}/ios-iphoneos/liblive2d_native.a" \
    "${SCRIPT_DIR}/native/${CUBISM_SDK_NAME}/Core/lib/ios/Release-iphoneos/libLive2DCubismCore.a"
else
  echo "=========================================="
  echo " Skipping iOS builds on non-macOS system (${OS_NAME}) "
  echo "=========================================="
fi

echo "=========================================================="
echo " All Live2D Native Libraries Built Successfully!          "
echo " Build Data -> shared/core-native/build/                 "
echo " Android .so -> src/androidMain/jniLibs/                 "
if [[ "${OS_NAME}" == Darwin* ]]; then
  echo " iOS .a     -> native/ios/                               "
fi
echo "=========================================================="
