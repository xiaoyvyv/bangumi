#!/bin/bash

# 查找当前目录下的所有文件（不包括文件夹）
files=$(find . -maxdepth 1 -type f)

# 添加前缀
for file in $files; do
  # 获取文件名
  filename=$(basename "$file")

  # 添加前缀并重命名文件
  mv "$filename" "normal_$filename"
done

echo "Prefix added to files."
