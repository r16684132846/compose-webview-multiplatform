cmake_minimum_required(VERSION 3.5.0)
project(webview_utils)

set(NATIVERENDER_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR})

if (DEFINED PACKAGE_FIND_FILE)
    include(${PACKAGE_FIND_FILE})
endif ()

# 添加系统头文件路径
include_directories(${NATIVERENDER_ROOT_PATH}
        ${NATIVERENDER_ROOT_PATH})

# 添加我们的源文件到库中
add_library(webview_utils STATIC
        arkweb_wrapper.cpp)

# 链接必要的系统库
target_link_libraries(webview_utils PUBLIC
        hilog_ndk.z
        ace_napi.z
        ohresmgr
        rawfile.z
        arkweb)
