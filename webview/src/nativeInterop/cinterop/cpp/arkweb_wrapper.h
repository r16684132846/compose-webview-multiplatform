#ifndef ARKWEB_WRAPPER_H
#define ARKWEB_WRAPPER_H

#include <stdint.h>
#include <stdlib.h>

// 回调函数类型定义（供 Kotlin/Native 传递 Lambda）
// 页面加载完成回调：url = 加载完成的URL
typedef void (*OnPageFinishedCallback)(const char *url);

// 页面加载错误回调：code = 错误码，msg = 错误信息
typedef void (*OnErrorCallback)(int32_t code, const char *msg);

// Component API 回调函数类型定义
typedef void (*OnComponentCallback)(const char *webTag, void *userData);

// 滚动回调函数类型定义
typedef void (*OnScrollCallback)(const char *webTag, int32_t x, int32_t y, void *userData);

// JavaScript执行回调
typedef void (*OnJavaScriptResultCallback)(const char *result);

// Cookie保存回调
typedef void (*OnCookieSaveCallback)(int32_t errorCode);

// HTTP头结构体
typedef struct {
    const char *name;
    const char *value;
} HttpHeader;

// 1. 创建 WebView 实例（返回实例ID，供后续操作）
// 参数：jsEnabled = 是否启用JavaScript，fileAccess = 是否允许访问本地文件
int32_t arkweb_wrapper_create(bool jsEnabled, bool fileAccess);

// 2. 加载 URL（不带HTTP头）
// 参数：instanceId = 实例ID，url = 要加载的URL（UTF-8字符串）
int32_t arkweb_wrapper_load_url(int32_t instanceId, const char *url);

// 3. 加载 URL（带HTTP头）
// 参数：instanceId = 实例ID，url = 要加载的URL（UTF-8字符串）
//      headers = HTTP头数组，headerCount = HTTP头数量
int32_t
arkweb_wrapper_load_url_with_headers(int32_t instanceId, const char *url, const HttpHeader *headers,
                                     int32_t headerCount);

// 4. 设置回调（页面完成/错误）
// 参数：instanceId = 实例ID，finishedCb = 完成回调，errorCb = 错误回调
void arkweb_wrapper_set_callbacks(int32_t instanceId,
                                  OnPageFinishedCallback finishedCb,
                                  OnErrorCallback errorCb);

// 5. 销毁 WebView 实例（释放内存）
void arkweb_wrapper_destroy(int32_t instanceId);

// 6. Controller API 相关函数
// 执行JavaScript
int32_t arkweb_wrapper_run_javascript(int32_t instanceId, const char *script,
                                      OnJavaScriptResultCallback callback);

// 刷新页面
int32_t arkweb_wrapper_refresh(int32_t instanceId);

// 注册JavaScript代理对象
int32_t arkweb_wrapper_register_js_proxy(int32_t instanceId, const char *objName);

// 删除JavaScript代理对象
int32_t arkweb_wrapper_delete_js_proxy(int32_t instanceId, const char *objName);

// 7. Component API 相关函数
// 设置组件回调
int32_t arkweb_wrapper_set_component_callbacks(
        int32_t instanceId,
        OnComponentCallback controllerAttachedCb,
        OnComponentCallback pageBeginCb,
        OnComponentCallback pageEndCb,
        OnComponentCallback destroyCb);

// 8. 滚动回调相关函数
// 设置滚动回调
int32_t arkweb_wrapper_set_scroll_callback(int32_t instanceId, OnScrollCallback scrollCb);

// 9. 导航控制相关函数
// 后退
int32_t arkweb_wrapper_go_back(int32_t instanceId);

// 前进
int32_t arkweb_wrapper_go_forward(int32_t instanceId);

// 停止加载
void arkweb_wrapper_stop_loading(int32_t instanceId);

// 检查是否可以后退
bool arkweb_wrapper_can_go_back(int32_t instanceId);

// 检查是否可以前进
bool arkweb_wrapper_can_go_forward(int32_t instanceId);

// 10. Cookie管理相关函数
int32_t arkweb_wrapper_save_cookie_sync();

void arkweb_wrapper_save_cookie_async(OnCookieSaveCallback callback);

// 11. 数据加载函数
int32_t arkweb_wrapper_load_data(int32_t instanceId, const char *data, const char *mimeType,
                                 const char *encoding, const char *baseUrl, const char *historyUrl);

// 12. JavaScript桥接相关函数
typedef char *(*JavaScriptProxyCallback)(const char **argv, int32_t argc);

// 13. POST请求函数
int32_t arkweb_wrapper_post_url(int32_t instanceId, const char *url, const uint8_t *postData,
                                size_t dataSize);

// 14. 加载HTML文件函数
int32_t arkweb_wrapper_load_html_file(int32_t instanceId, const char *fileName);

int32_t arkweb_wrapper_register_javascript_proxy_with_callback(
        int32_t instanceId,
        const char *objName,
        const char **methodList,
        JavaScriptProxyCallback *callbacks,
        int32_t size);

#endif // ARKWEB_WRAPPER_H
