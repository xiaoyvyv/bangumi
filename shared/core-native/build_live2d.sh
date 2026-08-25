#!/usr/bin/env bash
set -e

# CubismSdkForNative SDK
# https://cubism.live2d.com/sdk-native/bin/CubismSdkForNative-5-r.5.zip

# Bangumi Multiplatform - Live2D Core Native Master Build Script
# Usage:
#   ./build_live2d.sh            # Builds all target platforms supported by current OS
#   ./build_live2d.sh android    # Builds Android arm64-v8a, x86_64, x86 (.so)
#   ./build_live2d.sh macos      # Builds macOS Desktop (.dylib)
#   ./build_live2d.sh ios        # Builds iOS (.a)
#   ./build_live2d.sh windows    # Builds Windows x64 (.dll)
#   ./build_live2d.sh linux      # Builds Linux x64 (.so)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET="${1:-all}"

if [[ -z "${JAVA_HOME}" ]] && command -v /usr/libexec/java_home &>/dev/null; then
  export JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
fi

chmod +x "${SCRIPT_DIR}"/scripts/*.sh "${SCRIPT_DIR}/scripts/generate_shaders.sh"

case "${TARGET}" in
  android)
    "${SCRIPT_DIR}/scripts/build_android.sh"
    ;;
  macos)
    "${SCRIPT_DIR}/scripts/build_macos.sh"
    ;;
  ios)
    "${SCRIPT_DIR}/scripts/build_ios.sh"
    ;;
  windows)
    "${SCRIPT_DIR}/scripts/build_windows.sh"
    ;;
  linux)
    "${SCRIPT_DIR}/scripts/build_linux.sh"
    ;;
  all)
    OS_NAME="$(uname -s 2>/dev/null || echo "Unknown")"
    echo "Building for target host OS: ${OS_NAME}..."

    "${SCRIPT_DIR}/scripts/build_android.sh"

    case "${OS_NAME}" in
      Darwin*)
        "${SCRIPT_DIR}/scripts/build_macos.sh"
        "${SCRIPT_DIR}/scripts/build_ios.sh"
        ;;
      MINGW*|CYGWIN*|MSYS*|Windows_NT)
        "${SCRIPT_DIR}/scripts/build_windows.sh"
        ;;
      Linux*)
        "${SCRIPT_DIR}/scripts/build_linux.sh"
        ;;
    esac
    ;;
  *)
    echo "Unknown build target: ${TARGET}"
    echo "Valid targets: android, macos, ios, windows, linux, all"
    exit 1
    ;;
esac

echo "=========================================================="
echo " Live2D Native Library Build Completed Successfully!      "
echo "=========================================================="
