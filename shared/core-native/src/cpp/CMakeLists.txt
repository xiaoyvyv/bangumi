cmake_minimum_required(VERSION 3.22.1)
project("live2d_native")

set(SDK_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../native/CubismSdkForNative-5-r.5")

include_directories(
    "${CMAKE_CURRENT_SOURCE_DIR}"
    "${SDK_ROOT}/Core/include"
    "${SDK_ROOT}/Framework/src"
    "${SDK_ROOT}/Samples/OpenGL/thirdParty"
)

# Collect all Cubism Framework C++ source files
file(GLOB_RECURSE FRAMEWORK_SOURCES
    "${SDK_ROOT}/Framework/src/*.cpp"
)

# Filter out non-OpenGLES Framework renderers (D3D, Vulkan, Metal)
list(FILTER FRAMEWORK_SOURCES EXCLUDE REGEX ".*/Rendering/D3D.*")
list(FILTER FRAMEWORK_SOURCES EXCLUDE REGEX ".*/Rendering/Vulkan.*")
list(FILTER FRAMEWORK_SOURCES EXCLUDE REGEX ".*/Rendering/Metal.*")

if(IOS_BUILD)
    set(CMAKE_OSX_DEPLOYMENT_TARGET "14.0" CACHE STRING "Minimum OS version" FORCE)
    add_compile_definitions(CSM_TARGET_IPHONE_ES2)

    if(IOS_PLATFORM STREQUAL "SIMULATORARM64")
        set(PLATFORM_NAME Release-iphonesimulator-arm64)
    elseif(IOS_PLATFORM STREQUAL "SIMULATOR64")
        set(PLATFORM_NAME Release-iphonesimulator-x86_64)
    else()
        set(PLATFORM_NAME Release-iphoneos)
    endif()

    add_library(Live2DCubismCore STATIC IMPORTED)
    set_target_properties(Live2DCubismCore PROPERTIES IMPORTED_LOCATION
        "${SDK_ROOT}/Core/lib/ios/${PLATFORM_NAME}/libLive2DCubismCore.a"
    )

    add_library(live2d_native STATIC
        live2d_renderer.cpp
        miniz.c
        ${FRAMEWORK_SOURCES}
    )
else()
    add_compile_definitions(CSM_TARGET_ANDROID_ES2)

    add_library(Live2DCubismCore STATIC IMPORTED)
    set_target_properties(Live2DCubismCore PROPERTIES IMPORTED_LOCATION
        "${SDK_ROOT}/Core/lib/android/${ANDROID_ABI}/libLive2DCubismCore.a"
    )

    add_library(live2d_native SHARED
        live2d_renderer.cpp
        live2d_jni.cpp
        miniz.c
        ${FRAMEWORK_SOURCES}
    )

    target_link_libraries(live2d_native
        Live2DCubismCore
        GLESv2
        EGL
        android
        log
    )

    target_link_options(live2d_native PRIVATE "-Wl,-z,max-page-size=16384")
endif()
