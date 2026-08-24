#!/usr/bin/env bash

# download-live2d-assets.sh
# 官网的 Live2D 看板娘素材拉取脚本，保存到当前 ./black 和 ./musume 目录
# 素材的 baseUrl:
# black:  https://bgm.tv/img/musume_2d_black_2026/
# musume: https://bgm.tv/img/musume_2d_2026/

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

USER_AGENT="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

# 待拉取的 Live2D 模型配置 (名称 | BaseURL | JSON文件名)
MODELS=(
    "black|https://bgm.tv/img/musume_2d_black_2026|bangumi_black_musume_2026_parts.model3.json"
    "musume|https://bgm.tv/img/musume_2d_2026|bangumi_musume_2026_parts_grouped.model3.json"
)

echo "=========================================="
echo "🚀 开始拉取 Live2D 看板娘素材..."
echo "=========================================="

for item in "${MODELS[@]}"; do
    IFS="|" read -r NAME BASE_URL JSON_FILE <<< "$item"

    TARGET_DIR="${SCRIPT_DIR}/${NAME}"
    JSON_PATH="${TARGET_DIR}/${JSON_FILE}"
    JSON_URL="${BASE_URL}/${JSON_FILE}?v1"

    echo ""
    echo "------------------------------------------"
    echo "📦 正在处理模型 [$NAME]"
    echo "📁 目标目录: $TARGET_DIR"
    echo "🌐 主 JSON 地址: $JSON_URL"
    echo "------------------------------------------"

    mkdir -p "$TARGET_DIR"

    # 1. 先拉取 model3.json
    echo "正在下载主配置文件 $JSON_FILE ..."
    curl -sSL -H "User-Agent: $USER_AGENT" --connect-timeout 10 --retry 2 -o "$JSON_PATH" "$JSON_URL"

    if [ ! -s "$JSON_PATH" ]; then
        echo "❌ 错误: 无法下载或主配置文件为空: $JSON_PATH"
        continue
    fi
    echo "✅ 主配置文件已成功下载: $JSON_PATH"

    # 2. 使用 Python3 解析 JSON 提取 FileReferences 中包含的所有文件相对路径
    ASSET_FILES=$(python3 -c "
import json, sys, os

json_path = '$JSON_PATH'
if not os.path.exists(json_path):
    sys.exit(1)

with open(json_path, 'r', encoding='utf-8') as f:
    data = json.load(f)

file_refs = data.get('FileReferences', {})

files = set()
def extract_files(obj):
    if isinstance(obj, str):
        files.add(obj)
    elif isinstance(obj, list):
        for item in obj:
            extract_files(item)
    elif isinstance(obj, dict):
        if 'File' in obj and isinstance(obj['File'], str):
            files.add(obj['File'])
        else:
            for k, v in obj.items():
                extract_files(v)

extract_files(file_refs)
for f in sorted(files):
    if f.strip():
        print(f.strip())
")

    TOTAL=$(echo "$ASSET_FILES" | grep -v '^$' | wc -l | tr -d ' ')
    echo "🔍 共计提取到 $TOTAL 个素材文件，开始下载关联资源..."

    COUNT=0
    SUCCESS_COUNT=0

    for REL_PATH in $ASSET_FILES; do
        [ -z "$REL_PATH" ] && continue

        # 清理路径前导的 ./ 或 /
        CLEAN_REL_PATH=$(echo "$REL_PATH" | sed 's|^\./||; s|^/||')
        TARGET_PATH="${TARGET_DIR}/${CLEAN_REL_PATH}"
        DOWNLOAD_URL="${BASE_URL}/${CLEAN_REL_PATH}"

        COUNT=$((COUNT + 1))

        # 确保子目录存在
        mkdir -p "$(dirname "$TARGET_PATH")"

        # 如果文件已存在且大小大于 0，跳过
        if [ -s "$TARGET_PATH" ]; then
            echo "  [$COUNT/$TOTAL] 已存在 (跳过): $CLEAN_REL_PATH"
            SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
            continue
        fi

        echo "  [$COUNT/$TOTAL] 下载中: $CLEAN_REL_PATH -> $TARGET_PATH"

        # 最多重试 3 次
        MAX_RETRIES=3
        RETRY=0
        DOWNLOAD_OK=0

        while [ $RETRY -lt $MAX_RETRIES ]; do
            curl -sSL -H "User-Agent: $USER_AGENT" --connect-timeout 10 --retry 2 -o "$TARGET_PATH" "$DOWNLOAD_URL"
            if [ -s "$TARGET_PATH" ]; then
                DOWNLOAD_OK=1
                break
            fi
            RETRY=$((RETRY + 1))
            echo "  ⚠️ 下载失败/为空文件，重试 ($RETRY/$MAX_RETRIES)..."
            sleep 1
        done

        if [ $DOWNLOAD_OK -eq 1 ]; then
            SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
        else
            echo "  ❌ 错误: 无法下载 $CLEAN_REL_PATH"
        fi
    done

    echo "✅ [$NAME] 资产下载完成: $SUCCESS_COUNT/$TOTAL 已就绪"
done

echo ""
echo "=========================================="
echo "🎉 所有 Live2D 看板娘素材下载流程结束！"
echo "=========================================="