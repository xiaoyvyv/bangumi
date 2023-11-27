#!/bin/bash

# 设置文件路径
FILE_PATH="url.txt"

# 检查文件是否存在
if [ ! -f "$FILE_PATH" ]; then
  echo "File $FILE_PATH not found."
  exit 1
fi

# 逐行读取文件，下载并保存图片
while IFS= read -r URL; do
  # 提取文件名和目录路径
  FILENAME=$(basename "$URL")
  DIR_PATH=$(echo "$URL" | sed 's~^[[:alnum:]]*://~~; s~/[^/]*$~~')

  # 创建目录结构
  mkdir -p "$DIR_PATH"

  # 使用 curl 下载并保存图片
  curl -o "$DIR_PATH/$FILENAME" "$URL"
done < "$FILE_PATH"

echo "Download completed."
