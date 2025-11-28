#ifndef OHOS_WEBVIEW_MANAGER_H
#define OHOS_WEBVIEW_MANAGER_H

#include <stdint.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

// WebView管理器句柄类型
typedef struct OH_WebViewManager OH_WebViewManager;


// ------------------------------
// 管理器生命周期与单例
// ------------------------------
/**
 * 获取WebView管理器单例
 * @return 管理器句柄，全局唯一
 */
OH_WebViewManager *OH_WebViewManager_GetInstance();

/**
 * 初始化WebView管理器
 * @param manager 管理器句柄（不可为NULL）
 */
void OH_WebViewManager_Init(OH_WebViewManager *manager);

/**
 * 销毁WebView管理器
 * @param manager 管理器句柄（不可为NULL）
 */
void OH_WebViewManager_Destroy(OH_WebViewManager *manager);


// ------------------------------
// 全局配置项
// ------------------------------
/**
 * 设置全局用户代理（UA）
 * @param manager 管理器句柄
 * @param userAgent 用户代理字符串（NULL表示使用默认）
 */
void OH_WebViewManager_SetUserAgent(OH_WebViewManager *manager, const char *userAgent);

/**
 * 全局启用/禁用JavaScript
 * @param manager 管理器句柄
 * @param enabled true=启用，false=禁用
 */
void OH_WebViewManager_SetJavaScriptEnabled(OH_WebViewManager *manager, bool enabled);

/**
 * 全局启用/禁用DOM存储
 * @param manager 管理器句柄
 * @param enabled true=启用，false=禁用
 */
void OH_WebViewManager_SetDomStorageEnabled(OH_WebViewManager *manager, bool enabled);

/**
 * 设置全局缓存模式
 * @param manager 管理器句柄
 * @param mode 缓存模式（0=默认，1=无缓存，2=仅缓存）
 */
void OH_WebViewManager_SetCacheMode(OH_WebViewManager *manager, int32_t mode);


#ifdef __cplusplus
}
#endif

#endif