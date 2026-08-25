cmake_minimum_required(VERSION 3.22.1)
project("live2d_native")

set(CUBISM_SDK_NAME "CubismSdkForNative-5-r.5" CACHE STRING "Cubism SDK Directory Name")
set(SDK_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../native/${CUBISM_SDK_NAME}")

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
    add_compile_definitions(CSM_TARGET_IPHONE_ES2 IOS_BUILD)

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
elseif(MACOS_DESKTOP_BUILD)
    set(CMAKE_OSX_DEPLOYMENT_TARGET "11.0" CACHE STRING "Minimum OS version" FORCE)
    add_compile_definitions(CSM_TARGET_MAC_GL CSM_TARGET_COCOS MACOS_DESKTOP_BUILD)

    if(DEFINED ENV{JAVA_HOME})
        include_directories(
            "$ENV{JAVA_HOME}/include"
            "$ENV{JAVA_HOME}/include/darwin"
        )
    else()
        find_package(JNI REQUIRED)
        include_directories(${JNI_INCLUDE_DIRS})
    endif()

    find_package(OpenGL REQUIRED)
    include_directories(${OPENGL_INCLUDE_DIR})

    if(CMAKE_OSX_ARCHITECTURES STREQUAL "x86_64")
        set(MAC_ARCH x86_64)
    else()
        set(MAC_ARCH arm64)
    endif()

    add_library(Live2DCubismCore STATIC IMPORTED)
    set_target_properties(Live2DCubismCore PROPERTIES IMPORTED_LOCATION
        "${SDK_ROOT}/Core/lib/macos/${MAC_ARCH}/libLive2DCubismCore.a"
    )

    add_library(live2d_native SHARED
        live2d_renderer.cpp
        live2d_jni.cpp
        miniz.c
        ${FRAMEWORK_SOURCES}
    )

    target_link_libraries(live2d_native
        Live2DCubismCore
        ${OPENGL_LIBRARIES}
    )
elseif(WINDOWS_DESKTOP_BUILD OR (WIN32 AND NOT ANDROID))
    add_compile_definitions(CSM_TARGET_WIN_GL WINDOWS_DESKTOP_BUILD)

    find_package(OpenGL REQUIRED)
    include_directories(${OPENGL_INCLUDE_DIR})

    if(DEFINED ENV{JAVA_HOME})
        include_directories(
            "$ENV{JAVA_HOME}/include"
            "$ENV{JAVA_HOME}/include/win32"
        )
    else()
        find_package(JNI QUIET)
        if(JNI_FOUND)
            include_directories(${JNI_INCLUDE_DIRS})
        endif()
    endif()

    find_package(GLEW QUIET)

    add_library(Live2DCubismCore STATIC IMPORTED)
    set_target_properties(Live2DCubismCore PROPERTIES IMPORTED_LOCATION
        "${SDK_ROOT}/Core/lib/windows/x86_64/143/Live2DCubismCore_MD.lib"
    )

    add_library(live2d_native SHARED
        live2d_renderer.cpp
        live2d_jni.cpp
        miniz.c
        ${FRAMEWORK_SOURCES}
    )

    if(GLEW_FOUND)
        include_directories(${GLEW_INCLUDE_DIRS})
        target_link_libraries(live2d_native
            Live2DCubismCore
            ${OPENGL_LIBRARIES}
            ${GLEW_LIBRARIES}
            opengl32
        )
    else()
        target_link_libraries(live2d_native
            Live2DCubismCore
            ${OPENGL_LIBRARIES}
            opengl32
        )
    endif()

elseif(LINUX_DESKTOP_BUILD OR (UNIX AND NOT APPLE AND NOT ANDROID))
    add_compile_definitions(CSM_TARGET_LINUX_GL LINUX_DESKTOP_BUILD)

    find_package(OpenGL REQUIRED)
    find_package(X11 REQUIRED)
    include_directories(${OPENGL_INCLUDE_DIR} ${X11_INCLUDE_DIR})

    if(DEFINED ENV{JAVA_HOME})
        include_directories(
            "$ENV{JAVA_HOME}/include"
            "$ENV{JAVA_HOME}/include/linux"
        )
    else()
        find_package(JNI QUIET)
        if(JNI_FOUND)
            include_directories(${JNI_INCLUDE_DIRS})
        endif()
    endif()

    add_library(Live2DCubismCore STATIC IMPORTED)
    set_target_properties(Live2DCubismCore PROPERTIES IMPORTED_LOCATION
        "${SDK_ROOT}/Core/lib/linux/x86_64/libLive2DCubismCore.a"
    )

    add_library(live2d_native SHARED
        live2d_renderer.cpp
        live2d_jni.cpp
        miniz.c
        ${FRAMEWORK_SOURCES}
    )

    target_link_libraries(live2d_native
        Live2DCubismCore
        ${OPENGL_LIBRARIES}
        ${X11_LIBRARIES}
        GL
    )

else()
    # Android
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
