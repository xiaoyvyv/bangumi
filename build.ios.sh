#!/bin/sh

set -eu

PROJECT_ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
OUTPUT_DIR="${PROJECT_ROOT}/build/ios/release"
ARCHIVE_PATH="${OUTPUT_DIR}/iosApp.xcarchive"
DERIVED_DATA_PATH="${OUTPUT_DIR}/DerivedData"
VERSION_CODE="${APP_VERSION_CODE:-$(awk -F= '/^appVersionCode=/{print $2; exit}' "${PROJECT_ROOT}/gradle.properties")}"
VERSION_NAME="${APP_VERSION_NAME:-$(awk -F= '/^appVersionName=/{print $2; exit}' "${PROJECT_ROOT}/gradle.properties")}"

cd "${PROJECT_ROOT}"
. "${PROJECT_ROOT}/iosApp/gradle-ios.sh"

mkdir -p "${OUTPUT_DIR}"

echo "Build iOS Release IPA: ${VERSION_NAME} (${VERSION_CODE})"
./gradlew :shared:core-native:generateDummyFramework
pod install --project-directory=iosApp

xcodebuild \
    -workspace iosApp/iosApp.xcworkspace \
    -scheme iosApp \
    -configuration Release \
    -sdk iphoneos \
    -jobs 1 \
    -archivePath "${ARCHIVE_PATH}" \
    -derivedDataPath "${DERIVED_DATA_PATH}" \
    archive \
    CODE_SIGNING_ALLOWED=NO \
    CODE_SIGNING_REQUIRED=NO \
    CODE_SIGN_IDENTITY="" \
    DEVELOPMENT_TEAM="" \
    APP_VERSION_CODE="${VERSION_CODE}" \
    APP_VERSION_NAME="${VERSION_NAME}"

APP_PATH="$(find "${ARCHIVE_PATH}/Products/Applications" -maxdepth 1 -type d -name '*.app' -print -quit)"
test -n "${APP_PATH}"

PACKAGE_DIR="$(mktemp -d "${OUTPUT_DIR}/ipa.XXXXXX")"
trap 'rm -rf "${PACKAGE_DIR}"' EXIT
mkdir -p "${PACKAGE_DIR}/Payload"
cp -R "${APP_PATH}" "${PACKAGE_DIR}/Payload/"
ditto -c -k --sequesterRsrc --keepParent "${PACKAGE_DIR}/Payload" "${PACKAGE_DIR}/bangumi-ios.ipa"
mv -f "${PACKAGE_DIR}/bangumi-ios.ipa" "${OUTPUT_DIR}/bangumi-ios.ipa"

echo "IPA: ${OUTPUT_DIR}/bangumi-ios.ipa"
