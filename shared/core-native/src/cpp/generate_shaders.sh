#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SHADERS_DIR="${SCRIPT_DIR}/../../native/CubismSdkForNative-5-r.5/Framework/src/Rendering/OpenGL/Shaders/StandardES"
OUTPUT_HEADER="${SCRIPT_DIR}/live2d_shaders.h"

# If shaders dir doesn't exist, try running setup_sdk.sh
if [ ! -d "${SHADERS_DIR}" ]; then
  SETUP_SDK_SCRIPT="${SCRIPT_DIR}/../../scripts/setup_sdk.sh"
  if [ -f "${SETUP_SDK_SCRIPT}" ]; then
    echo "Live2D SDK shaders not found, running setup_sdk.sh..."
    bash "${SETUP_SDK_SCRIPT}"
  fi
fi

if [ ! -d "${SHADERS_DIR}" ]; then
  if [ -f "${OUTPUT_HEADER}" ]; then
    echo "Warning: Shader directory not found at ${SHADERS_DIR}, but pre-generated ${OUTPUT_HEADER} exists. Skipping shader regeneration."
    exit 0
  else
    echo "Error: Shader directory not found at ${SHADERS_DIR}"
    exit 1
  fi
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

echo "Successfully generated ${OUTPUT_HEADER}"
