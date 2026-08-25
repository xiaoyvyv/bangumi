#!/usr/bin/env bash
set -e

# Bangumi Multiplatform - Build Live2D Native Library for Windows x64 (.dll)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_BASE_DIR="${SCRIPT_DIR}/build/live2d"
mkdir -p "${BUILD_BASE_DIR}"

if [[ -z "${JAVA_HOME}" ]] && command -v /usr/libexec/java_home &>/dev/null; then
  export JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
fi

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
echo " Building Windows x64 (live2d_native.dll) "
echo "=========================================="
BUILD_DIR="${BUILD_BASE_DIR}/windows-x64"
rm -rf "${BUILD_DIR}"

cmake -S "${SCRIPT_DIR}/src/cpp" -B "${BUILD_DIR}" \
  "${EXTRA_CMAKE_FLAGS[@]}" \
  -DCMAKE_BUILD_TYPE=Release
cmake --build "${BUILD_DIR}" --config Release

OUT_NATIVE="${SCRIPT_DIR}/native/windows"
OUT_JVM_RES="${SCRIPT_DIR}/src/jvmMain/resources/native/windows"
mkdir -p "${OUT_NATIVE}" "${OUT_JVM_RES}"

DLL_FILE=""
if [ -f "${BUILD_DIR}/Release/live2d_native.dll" ]; then
  DLL_FILE="${BUILD_DIR}/Release/live2d_native.dll"
elif [ -f "${BUILD_DIR}/live2d_native.dll" ]; then
  DLL_FILE="${BUILD_DIR}/live2d_native.dll"
fi

if [ -n "${DLL_FILE}" ]; then
  cp "${DLL_FILE}" "${OUT_NATIVE}/live2d_native.dll"
  cp "${DLL_FILE}" "${OUT_JVM_RES}/live2d_native.dll"
  echo "[Windows] Successfully built live2d_native.dll!"
else
  echo "[Windows Error] live2d_native.dll not found in ${BUILD_DIR}"
  exit 1
fi
