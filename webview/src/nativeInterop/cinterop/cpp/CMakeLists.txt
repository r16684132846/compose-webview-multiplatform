cmake_minimum_required(VERSION 3.5.0)
project(webview_utils)

set(NATIVERENDER_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR})

if (DEFINED PACKAGE_FIND_FILE)
    include(${PACKAGE_FIND_FILE})
endif ()

include_directories(${NATIVERENDER_ROOT_PATH}
        ${NATIVERENDER_ROOT_PATH})

add_library(webview_utils STATIC
        arkweb_wrapper.cpp)

target_link_libraries(webview_utils PUBLIC libace_napi.z.so libohresmgr.so librawfile.z.so libhilog_ndk.z.so)

# 链接鸿蒙 ArkWeb 库
find_library(ARKWEB_LIBRARIES NAMES libarkweb.so arkweb PATHS ${OHOS_SDK_HOME}/native/system/lib)
if (ARKWEB_LIBRARIES)
    target_link_libraries(webview_utils PUBLIC ${ARKWEB_LIBRARIES})
    message(STATUS "找到 ArkWeb 库: ${ARKWEB_LIBRARIES}")
else ()
    message(WARNING "未能找到 ArkWeb 库 (libarkweb.so)，某些功能可能无法正常工作")
endif ()

# 链接其它可能需要的库
find_library(WEBVIEW_LIBRARIES libwebview.so)
if (WEBVIEW_LIBRARIES)
    target_link_libraries(webview_utils PUBLIC ${WEBVIEW_LIBRARIES})
endif ()
