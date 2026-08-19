#!/usr/bin/env bash

# 切换到脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MANIFEST_FILE="${SCRIPT_DIR}/manifest.local.json"
ASSETS_DIR="${SCRIPT_DIR}/assets"
BASE_URL="https://bgm.tv/js/lib/bmo"

mkdir -p "$ASSETS_DIR"

if [ ! -f "$MANIFEST_FILE" ]; then
    echo "错误: 未找到配置文件 $MANIFEST_FILE"
    exit 1
fi

echo "正在解析 $MANIFEST_FILE ..."

# 使用 python3 解析 JSON 提取所有 src 路径
SRCS=$(python3 -c "
import json, sys

with open('$MANIFEST_FILE', 'r', encoding='utf-8') as f:
    data = json.load(f)

srcs = set()
def extract_srcs(obj):
    if isinstance(obj, dict):
        for k, v in obj.items():
            if k == 'src' and isinstance(v, str):
                srcs.add(v)
            else:
                extract_srcs(v)
    elif isinstance(obj, list):
        for item in obj:
            extract_srcs(item)

extract_srcs(data)
for s in sorted(srcs):
    print(s)
")

TOTAL=$(echo "$SRCS" | grep -v '^$' | wc -l | tr -d ' ')
echo "共计找到 $TOTAL 个图片素材资源，开始下载..."

COUNT=0
SUCCESS_COUNT=0

for SRC in $SRCS; do
    [ -z "$SRC" ] && continue
    CLEAN_SRC=$(echo "$SRC" | sed 's|^\./||; s|^/||')
    FILENAME=$(basename "$CLEAN_SRC")
    TARGET_PATH="${ASSETS_DIR}/${FILENAME}"
    DOWNLOAD_URL="${BASE_URL}/${CLEAN_SRC}"

    COUNT=$((COUNT + 1))

    # 如果文件已存在且大小大于 0，跳过
    if [ -s "$TARGET_PATH" ]; then
        echo "[$COUNT/$TOTAL] 已存在 (跳过): $FILENAME"
        SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
        continue
    fi

    echo "[$COUNT/$TOTAL] 下载中: $FILENAME -> $TARGET_PATH"
    
    # 最多重试 3 次
    MAX_RETRIES=3
    RETRY=0
    DOWNLOAD_OK=0

    while [ $RETRY -lt $MAX_RETRIES ]; do
        curl -sSL --connect-timeout 10 --retry 2 -o "$TARGET_PATH" "$DOWNLOAD_URL"
        if [ -s "$TARGET_PATH" ]; then
            DOWNLOAD_OK=1
            break
        fi
        RETRY=$((RETRY + 1))
        echo "⚠️ 下载失败/为空文件，重试 ($RETRY/$MAX_RETRIES)..."
        sleep 1
    done

    if [ $DOWNLOAD_OK -eq 1 ]; then
        SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
    else
        echo "❌ 错误: 无法下载 $FILENAME"
    fi
done

echo "=========================================="
echo "✅ 资产下载完成: $SUCCESS_COUNT/$TOTAL 已就绪"
echo "📁 保存目录: $ASSETS_DIR"
echo "=========================================="
