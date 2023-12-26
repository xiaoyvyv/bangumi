#!/bin/bash

INPUT_DIR="./images"
OUTPUT_DIR="./output"

mkdir -p "$OUTPUT_DIR"

for FILE in "$INPUT_DIR"/*.png; do
    BASENAME=$(basename "$FILE")
    FILENAME="${BASENAME%.*}"

    # Convert PNG to PBM
    convert "$FILE" -resize 24x24 -monochrome pbm:- | potrace -o "$OUTPUT_DIR/$FILENAME.svg" -
done