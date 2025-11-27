cmake_minimum_required(VERSION 3.5.0)
project(webview_utils)

set(NATIVERENDER_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR})

if (DEFINED PACKAGE_FIND_FILE)
    include(${PACKAGE_FIND_FILE})
endif ()

include_directories(${NATIVERENDER_ROOT_PATH}
        ${NATIVERENDER_ROOT_PATH})

# 添加webview_api.cpp到编译中
add_library(webview_utils STATIC
        ohos_webview_utils.cpp
        webview_api.cpp)

target_link_libraries(webview_utils PUBLIC libhilog_ndk.z.so)
target_link_libraries(webview_utils PUBLIC
        libace_napi.z.so
        libohresmgr.so
        librawfile.z.so
        libhilog_ndk.z.so)
