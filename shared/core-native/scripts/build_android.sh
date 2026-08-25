#!/usr/bin/env bash
set -e

# Bangumi Multiplatform - Build Live2D Native Library for Android
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_BASE_DIR="${SCRIPT_DIR}/build/live2d"
mkdir -p "${BUILD_BASE_DIR}"

"${SCRIPT_DIR}/scripts/setup_sdk.sh"
"${SCRIPT_DIR}/scripts/generate_shaders.sh"

NDK_VERSION="${NDK_VERSION:-26.3.11579264}"
ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-21}"
SHOW_WARNINGS="${SHOW_WARNINGS:-false}"

OS_NAME="$(uname -s 2>/dev/null || echo "Unknown")"
if [ -z "${ANDROID_NDK_HOME}" ]; then
  if [ -n "${ANDROID_NDK_ROOT}" ]; then
    export ANDROID_NDK_HOME="${ANDROID_NDK_ROOT}"
  elif [ -n "${ANDROID_NDK}" ]; then
    export ANDROID_NDK_HOME="${ANDROID_NDK}"
  else
    case "${OS_NAME}" in
      Darwin*) export ANDROID_NDK_HOME="$HOME/Library/Android/sdk/ndk/${NDK_VERSION}" ;;
      MINGW*|CYGWIN*|MSYS*|Windows_NT) export ANDROID_NDK_HOME="C:/Users/${USERNAME:-$USER}/AppData/Local/Android/Sdk/ndk/${NDK_VERSION}" ;;
      Linux*) export ANDROID_NDK_HOME="$HOME/Android/Sdk/ndk/${NDK_VERSION}" ;;
      *) export ANDROID_NDK_HOME="$HOME/Library/Android/sdk/ndk/${NDK_VERSION}" ;;
    esac
  fi
fi

EXTRA_CMAKE_FLAGS=()
if [ "${SHOW_WARNINGS}" = "false" ]; then
  EXTRA_CMAKE_FLAGS+=("-Wno-dev" "-DCMAKE_WARN_DEPRECATED=OFF" "-DCMAKE_C_FLAGS=-w" "-DCMAKE_CXX_FLAGS=-w")
fi

ABIS=("arm64-v8a" "x86_64" "x86")
for ABI in "${ABIS[@]}"; do
  echo "=========================================="
  echo " Building Android ${ABI} (.so)            "
  echo "=========================================="
  BUILD_DIR="${BUILD_BASE_DIR}/android-${ABI}"
  rm -rf "${BUILD_DIR}"
  cmake -S "${SCRIPT_DIR}/src/cpp" -B "${BUILD_DIR}" \
    "${EXTRA_CMAKE_FLAGS[@]}" \
    -DANDROID_ABI="${ABI}" \
    -DANDROID_NDK="$ANDROID_NDK_HOME" \
    -DCMAKE_TOOLCHAIN_FILE="$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake" \
    -DANDROID_PLATFORM="$ANDROID_PLATFORM" \
    -DCMAKE_BUILD_TYPE=Release
  cmake --build "${BUILD_DIR}" --config Release

  OUT_JNI="${SCRIPT_DIR}/src/androidMain/jniLibs/${ABI}"
  mkdir -p "${OUT_JNI}"
  cp "${BUILD_DIR}/liblive2d_native.so" "${OUT_JNI}/"
done

echo "[Android] Successfully built all Android ABI shared libraries!"
