#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SHADERS_DIR="${SCRIPT_DIR}/shaders"
OUTPUT_HEADER="${SCRIPT_DIR}/live2d_shaders.h"

if [ ! -d "${SHADERS_DIR}" ]; then
  echo "Error: Shader directory not found at ${SHADERS_DIR}"
  exit 1
fi

cat << 'EOF' > "${OUTPUT_HEADER}"
#ifndef LIVE2D_SHADERS_H
#define LIVE2D_SHADERS_H

#include <map>
#include <string>

// Auto-generated 36 GLSL StandardES Framework Shaders from src/cpp/shaders/
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

echo "Successfully generated ${OUTPUT_HEADER} from ${SHADERS_DIR}"
