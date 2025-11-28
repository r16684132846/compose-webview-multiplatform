#ifndef OHOS_WEBVIEW_H
#define OHOS_WEBVIEW_H

#include <stdint.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

// 前置声明管理器类型
typedef struct OH_WebViewManager OH_WebViewManager;
// 上下文句柄（鸿蒙API必备，用于能力上下文）
typedef struct OH_Context OH_Context;
// WebView实例句柄
typedef struct OH_WebView OH_WebView;


// ------------------------------
// 加载回调类型（实例级事件）
// ------------------------------
typedef void (*OH_WebPageStartedCallback)(const char *url, void *userData);

typedef void (*OH_WebPageFinishedCallback)(const char *url, void *userData);

typedef void (*OH_WebErrorCallback)(int32_t errorCode, const char *description,
                                    const char *failingUrl, void *userData);


// ------------------------------
// WebView实例生命周期与操作
// ------------------------------
/**
 * 创建WebView实例（依赖全局管理器的配置）
 * @param context 能力上下文句柄（不可为NULL）
 * @param manager 全局WebView管理器句柄（不可为NULL）
 * @return WebView实例句柄，失败返回NULL
 */
OH_ArkWeb *OH_ArkWeb_Create(OH_Context *context, OH_WebViewManager *manager);

/**
 * 销毁WebView实例
 * @param webView WebView实例句柄（不可为NULL）
 */
void OH_WebView_Destroy(OH_WebView *webView);


// ------------------------------
// 资源加载操作
// ------------------------------
/**
 * 加载URL
 * @param webView WebView实例句柄
 * @param url 目标URL（不可为NULL）
 */
void OH_WebView_LoadUrl(OH_WebView *webView, const char *url);

/**
 * 加载HTML数据
 * @param webView WebView实例句柄
 * @param data HTML数据（不可为NULL）
 * @param mimeType 数据类型（如"text/html"，可为NULL）
 * @param encoding 编码（如"UTF-8"，可为NULL）
 * @param baseUrl 基础URL（可为NULL，用于相对路径解析）
 */
void OH_WebView_LoadData(OH_WebView *webView, const char *data, const char *mimeType,
                         const char *encoding, const char *baseUrl);

/**
 * 执行JavaScript脚本
 * @param webView WebView实例句柄
 * @param script 脚本字符串（不可为NULL）
 * @param callback 执行结果回调（可为NULL）
 * @param userData 回调透传的用户数据（可为NULL）
 */
void OH_WebView_EvaluateJavascript(OH_WebView *webView, const char *script,
                                   void (*callback)(const char *result, void *userData),
                                   void *userData);


// ------------------------------
// 加载回调注册
// ------------------------------
/**
 * 注册页面开始加载回调
 * @param webView WebView实例句柄
 * @param callback 回调函数（可为NULL，取消注册）
 * @param userData 回调透传的用户数据（可为NULL）
 */
void OH_WebView_SetPageStartedCallback(OH_WebView *webView, OH_WebPageStartedCallback callback,
                                       void *userData);

/**
 * 注册页面加载完成回调
 * @param webView WebView实例句柄
 * @param callback 回调函数（可为NULL，取消注册）
 * @param userData 回调透传的用户数据（可为NULL）
 */
void OH_WebView_SetPageFinishedCallback(OH_WebView *webView, OH_WebPageFinishedCallback callback,
                                        void *userData);

/**
 * 注册加载错误回调
 * @param webView WebView实例句柄
 * @param callback 回调函数（可为NULL，取消注册）
 * @param userData 回调透传的用户数据（可为NULL）
 */
void OH_WebView_SetErrorCallback(OH_WebView *webView, OH_WebErrorCallback callback, void *userData);


// ------------------------------
// 导航控制
// ------------------------------
/**
 * 判断是否可后退
 * @param webView WebView实例句柄
 * @return true=可后退，false=不可
 */
bool OH_WebView_CanGoBack(OH_WebView *webView);

/**
 * 判断是否可前进
 * @param webView WebView实例句柄
 * @return true=可前进，false=不可
 */
bool OH_WebView_CanGoForward(OH_WebView *webView);

/**
 * 后退导航
 * @param webView WebView实例句柄
 */
void OH_WebView_GoBack(OH_WebView *webView);

/**
 * 前进导航
 * @param webView WebView实例句柄
 */
void OH_WebView_GoForward(OH_WebView *webView);

/**
 * 重新加载页面
 * @param webView WebView实例句柄
 */
void OH_WebView_Reload(OH_WebView *webView);

/**
 * 停止加载
 * @param webView WebView实例句柄
 */
void OH_WebView_StopLoading(OH_WebView *webView);


// ------------------------------
// JavaScript交互
// ------------------------------
/**
 * 注入JavaScript接口（供前端调用Native方法）
 * @param webView WebView实例句柄
 * @param name 前端调用的对象名（如"nativeBridge"）
 * @param object 接口实现（自定义结构体，包含方法指针）
 */
void OH_WebView_AddJavascriptInterface(OH_WebView *webView, const char *name, void *object);


#ifdef __cplusplus
}
#endif

#endif // OHOS_WEBVIEW_H