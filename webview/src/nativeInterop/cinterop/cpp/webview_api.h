#ifndef WEBVIEW_API_H
#define WEBVIEW_API_H

#include <stdint.h>
#include <stdbool.h>

// WebView 实例句柄
typedef struct OH_WebView OH_WebView;

// 创建 WebView 实例
OH_WebView *create_webview(void *context);

// 销毁 WebView 实例
void destroy_webview(OH_WebView *webview);

// 加载 URL
void load_url(OH_WebView *webview, const char *url);

// 执行 JavaScript
void evaluate_javascript(OH_WebView *webview, const char *script,
                         void (*callback)(const char *result, void *user_data),
                         void *user_data);

// 判断是否可后退
bool can_go_back(OH_WebView *webview);

// 判断是否可前进
bool can_go_forward(OH_WebView *webview);

// 后退导航
void go_back(OH_WebView *webview);

// 前进导航
void go_forward(OH_WebView *webview);

// 重新加载页面
void reload(OH_WebView *webview);

// 停止加载
void stop_loading(OH_WebView *webview);

// 加载HTML数据
void load_data(OH_WebView *webview, const char *data, const char *mime_type,
               const char *encoding, const char *base_url);

// 注册页面开始加载回调
void set_page_started_callback(OH_WebView *webview,
                               void (*callback)(const char *url, void *user_data),
                               void *user_data);

// 注册页面加载完成回调
void set_page_finished_callback(OH_WebView *webview,
                                void (*callback)(const char *url, void *user_data),
                                void *user_data);

// 注册加载错误回调
void set_error_callback(OH_WebView *webview,
                        void (*callback)(int32_t error_code, const char *description,
                                         const char *failing_url, void *user_data),
                        void *user_data);

// 设置 AbilityContext
void OH_SetAbilityContext(void *context);

// 打开系统浏览器
void OH_OpenBrowser(const char *url);

#endif
