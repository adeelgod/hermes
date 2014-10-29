#!/bin/sh
WIKBOOK_FILE="docker_image_$1_dockerfile.asciidoc"

# ensure that we always have the current version from the POM file
sed -i -r 's/^FROM.*hlu_(.*):(.*)/FROM        hlu_\1:'$2'/g' Dockerfile

echo "[source]" > ../hlu-doc-developer/src/main/asciidoc/$WIKBOOK_FILE
echo ".Dockerfile: hlu_$1" >> ../hlu-doc-developer/src/main/asciidoc/$WIKBOOK_FILE
echo "----" >> ../hlu-doc-developer/src/main/asciidoc/$WIKBOOK_FILE
cat Dockerfile >> ../hlu-doc-developer/src/main/asciidoc/$WIKBOOK_FILE
echo "\n----" >> ../hlu-doc-developer/src/main/asciidoc/$WIKBOOK_FILE
