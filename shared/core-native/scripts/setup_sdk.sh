#!/usr/bin/env bash
set -e

# Bangumi Multiplatform - Auto Download & Setup Live2D Cubism SDK for Native
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
NATIVE_DIR="${SCRIPT_DIR}/native"
SDK_NAME="CubismSdkForNative-5-r.5"
SDK_DIR="${NATIVE_DIR}/${SDK_NAME}"
SDK_ZIP_URL="https://cubism.live2d.com/sdk-native/bin/${SDK_NAME}.zip"
ZIP_FILE="${NATIVE_DIR}/${SDK_NAME}.zip"

mkdir -p "${NATIVE_DIR}"

if [ ! -d "${SDK_DIR}" ] || [ ! -f "${SDK_DIR}/Core/include/Live2DCubismCore.h" ]; then
  echo "=========================================="
  echo " Live2D SDK not found in ${SDK_DIR}"
  echo " Downloading ${SDK_ZIP_URL}..."
  echo "=========================================="

  if command -v curl &>/dev/null; then
    curl -sSL -o "${ZIP_FILE}" "${SDK_ZIP_URL}"
  elif command -v wget &>/dev/null; then
    wget -q -O "${ZIP_FILE}" "${SDK_ZIP_URL}"
  else
    echo "Error: Neither curl nor wget is available to download SDK."
    exit 1
  fi

  echo "Extracting ${SDK_NAME}.zip..."
  if command -v unzip &>/dev/null; then
    unzip -q "${ZIP_FILE}" -d "${NATIVE_DIR}"
  elif command -v 7z &>/dev/null; then
    7z x "${ZIP_FILE}" "-o${NATIVE_DIR}" -y &>/dev/null
  elif command -v powershell &>/dev/null; then
    powershell -Command "Expand-Archive -Path '${ZIP_FILE}' -DestinationPath '${NATIVE_DIR}' -Force"
  elif command -v tar &>/dev/null; then
    tar -xf "${ZIP_FILE}" -C "${NATIVE_DIR}"
  else
    echo "Error: No unzip utility available to extract ${ZIP_FILE}"
    exit 1
  fi

  rm -f "${ZIP_FILE}"
  echo "[SDK Setup] Live2D Cubism SDK setup completed successfully at ${SDK_DIR}!"
fi
