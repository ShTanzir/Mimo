#!/bin/sh
# Generates every mipmap density (square + round) from assets/Mimo.png.
# Run from the repo root. Requires ImageMagick ("convert").
#
#   sh scripts/generate_icons.sh
#
# Safe to skip: if assets/Mimo.png doesn't exist yet, this script exits
# quietly and the previous icons (if any) are left untouched.

set -e

SRC="assets/Mimo.png"
RES_DIR="app/src/main/res"

if [ ! -f "$SRC" ]; then
  echo "No $SRC found — skipping custom icon generation (using existing icons)."
  exit 0
fi

if ! command -v convert >/dev/null 2>&1; then
  echo "ImageMagick 'convert' not found — skipping icon generation."
  exit 0
fi

# density name -> pixel size
set -- mdpi 48 hdpi 72 xhdpi 96 xxhdpi 144 xxxhdpi 192

while [ "$#" -ge 2 ]; do
  density="$1"; size="$2"; shift 2
  out_dir="$RES_DIR/mipmap-$density"
  mkdir -p "$out_dir"

  # Square icon
  convert "$SRC" -resize "${size}x${size}^" -gravity center -extent "${size}x${size}" \
    "$out_dir/ic_launcher.png"

  # Round icon: same square render, masked with a circle
  convert "$SRC" -resize "${size}x${size}^" -gravity center -extent "${size}x${size}" \
    \( +clone -alpha extract -fill black -colorize 100 \
       -fill white -draw "circle $((size/2)),$((size/2)) $((size/2)),0" \) \
    -alpha off -compose CopyOpacity -composite \
    "$out_dir/ic_launcher_round.png"

  echo "Generated $out_dir/ic_launcher.png + ic_launcher_round.png (${size}x${size})"
done

echo "Icon generation complete."
