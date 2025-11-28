cmake_minimum_required(VERSION 3.5.0)
project(webview_utils)

set(NATIVERENDER_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR})

if (DEFINED PACKAGE_FIND_FILE)
    include(${PACKAGE_FIND_FILE})
endif ()

# 添加 ArkWeb 头文件路径
include_directories(
        ${NATIVERENDER_ROOT_PATH}
        ${OHOS_SDK_NATIVE_INCLUDE}/arkweb
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# 添加webview_api.cpp到编译中
add_library(webview_utils STATIC
        ohos_webview_utils.cpp
        webview_api.cpp)

# 链接 ArkWeb 官方库和基础鸿蒙库
target_link_libraries(webview_utils PUBLIC
        libhilog_ndk.z.so
        libace_napi.z.so
        libohresmgr.so
        librawfile.z.so
        arkweb)

# 设置 C++17 标准
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 适配鸿蒙支持的架构
if (NOT DEFINED CMAKE_SYSTEM_PROCESSOR)
    set(CMAKE_SYSTEM_PROCESSOR aarch64)
endif ()
