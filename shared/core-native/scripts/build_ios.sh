#!/usr/bin/env bash
set -e

# Bangumi Multiplatform - Build Live2D Native Library for iOS (.a)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_BASE_DIR="${SCRIPT_DIR}/build/live2d"
mkdir -p "${BUILD_BASE_DIR}"

"${SCRIPT_DIR}/scripts/setup_sdk.sh"

IOS_DEPLOYMENT_TARGET="${IOS_DEPLOYMENT_TARGET:-14.0}"
CUBISM_SDK_NAME="${CUBISM_SDK_NAME:-CubismSdkForNative-5-r.5}"
SHOW_WARNINGS="${SHOW_WARNINGS:-false}"

EXTRA_CMAKE_FLAGS=()
if [ "${SHOW_WARNINGS}" = "false" ]; then
  EXTRA_CMAKE_FLAGS+=("-Wno-dev" "-DCMAKE_WARN_DEPRECATED=OFF" "-DCMAKE_C_FLAGS=-w" "-DCMAKE_CXX_FLAGS=-w")
fi

echo "=========================================="
echo " Generating Embedded GLSL Shaders Header "
echo "=========================================="
"${SCRIPT_DIR}/src/cpp/generate_shaders.sh"

echo "=========================================="
echo " Building iOS Simulator arm64 (.a)        "
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
echo " Building iOS Device arm64 (.a)           "
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

echo "[iOS] Successfully built iOS static libraries!"
