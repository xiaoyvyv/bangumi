#!/usr/bin/env bash
set -e

# Bangumi Multiplatform - Generate Live2D GLSL Embedded Shaders Header
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SDK_NAME="${CUBISM_SDK_NAME:-CubismSdkForNative-5-r.5}"
SHADERS_DIR="${SCRIPT_DIR}/native/${SDK_NAME}/Framework/src/Rendering/OpenGL/Shaders/StandardES"
OUTPUT_HEADER="${SCRIPT_DIR}/src/cpp/live2d_shaders.h"

if [ ! -d "${SHADERS_DIR}" ]; then
  "${SCRIPT_DIR}/scripts/setup_sdk.sh"
fi

if [ ! -d "${SHADERS_DIR}" ]; then
  echo "Error: Shader directory not found at ${SHADERS_DIR}"
  exit 1
fi

cat << 'EOF' > "${OUTPUT_HEADER}"
#ifndef LIVE2D_SHADERS_H
#define LIVE2D_SHADERS_H

#include <map>
#include <string>

// Auto-generated 36 GLSL StandardES Framework Shaders from Cubism SDK
static const std::map<std::string, std::string> g_embeddedShaders = {
EOF

FIRST=true
for shader_file in $(ls "${SHADERS_DIR}"/*.vert "${SHADERS_DIR}"/*.frag 2>/dev/null | sort); do
    filename=$(basename "${shader_file}")
    if [ "$FIRST" = true ]; then
        FIRST=false
    else
        echo "," >> "${OUTPUT_HEADER}"
    fi
    echo "    {\"${filename}\", R\"(" >> "${OUTPUT_HEADER}"
    cat "${shader_file}" >> "${OUTPUT_HEADER}"
    printf ')"}' >> "${OUTPUT_HEADER}"
done

cat << 'EOF' >> "${OUTPUT_HEADER}"

};

#endif // LIVE2D_SHADERS_H
EOF

echo "[Shaders] Successfully generated ${OUTPUT_HEADER}"
