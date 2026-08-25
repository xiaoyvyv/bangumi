#!/usr/bin/env bash
set -e

# Bangumi Multiplatform - Build Live2D Native Library for Linux x64 (.so)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_BASE_DIR="${SCRIPT_DIR}/build/live2d"
mkdir -p "${BUILD_BASE_DIR}"

"${SCRIPT_DIR}/scripts/setup_sdk.sh"
"${SCRIPT_DIR}/scripts/generate_shaders.sh"

if [[ -z "${JAVA_HOME}" ]] && command -v /usr/libexec/java_home &>/dev/null; then
  export JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
fi

SHOW_WARNINGS="${SHOW_WARNINGS:-false}"
EXTRA_CMAKE_FLAGS=()
if [ "${SHOW_WARNINGS}" = "false" ]; then
  EXTRA_CMAKE_FLAGS+=("-Wno-dev" "-DCMAKE_WARN_DEPRECATED=OFF" "-DCMAKE_C_FLAGS=-w" "-DCMAKE_CXX_FLAGS=-w")
fi

echo "=========================================="
echo " Building Linux x64 (liblive2d_native.so) "
echo "=========================================="
BUILD_DIR="${BUILD_BASE_DIR}/linux-x64"
rm -rf "${BUILD_DIR}"

cmake -S "${SCRIPT_DIR}/src/cpp" -B "${BUILD_DIR}" \
  "${EXTRA_CMAKE_FLAGS[@]}" \
  -DCMAKE_BUILD_TYPE=Release
cmake --build "${BUILD_DIR}" --config Release

OUT_NATIVE="${SCRIPT_DIR}/native/linux"
OUT_JVM_RES="${SCRIPT_DIR}/src/jvmMain/resources/native/linux"
mkdir -p "${OUT_NATIVE}" "${OUT_JVM_RES}"

cp "${BUILD_DIR}/liblive2d_native.so" "${OUT_NATIVE}/"
cp "${BUILD_DIR}/liblive2d_native.so" "${OUT_JVM_RES}/"

echo "[Linux] Successfully built liblive2d_native.so!"
