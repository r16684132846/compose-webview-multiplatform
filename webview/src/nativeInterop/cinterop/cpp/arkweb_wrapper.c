#include "arkweb_wrapper.h"
#include <arkweb/arkweb.h>  // 鸿蒙 ArkWeb C API 头文件
#include <stdio.h>
#include <string.h>

// 存储 WebView 实例和回调（简化实现：用数组存储，生产环境可改用哈希表）
#define MAX_WEBVIEW_INSTANCES 10
typedef struct {
    OH_ArkWeb *webview;                // ArkWeb 原生实例
    OnPageFinishedCallback finishedCb; // 页面完成回调
    OnErrorCallback errorCb;           // 错误回调
} WebViewWrapper;

static WebViewWrapper g_instances[MAX_WEBVIEW_INSTANCES] = {0};
static int32_t g_next_instance_id = 1; // 实例ID自增（从1开始，0表示无效）

// -------------- ArkWeb 原生回调实现（转发给 K/N）--------------
// 页面加载完成回调（ArkWeb C API 要求的回调格式）
static void OnArkWebPageFinished(void *context, const char *url) {
    if (context == NULL || url == NULL) return;
    int32_t instanceId = *(int32_t *) context;
    // 查找实例并调用 K/N 传递的回调
    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL &&
            g_instances[i].finishedCb != NULL) {
            g_instances[i].finishedCb(url);
            break;
        }
    }
    free(context); // 释放上下文内存
}

// 页面加载错误回调（ArkWeb C API 要求的回调格式）
static void OnArkWebError(void *context, const OH_ArkWeb_Error *error) {
    if (context == NULL || error == NULL) return;
    int32_t instanceId = *(int32_t *) context;
    // 查找实例并调用 K/N 传递的回调
    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL &&
            g_instances[i].errorCb != NULL) {
            g_instances[i].errorCb(error->errorCode, error->errorMsg);
            break;
        }
    }
    free(context); // 释放上下文内存
}

// -------------- C 中间层接口实现（暴露给 K/N）--------------
int32_t arkweb_wrapper_create(bool jsEnabled, bool fileAccess) {
    if (g_next_instance_id >= MAX_WEBVIEW_INSTANCES) {
        printf("ArkWeb: 超过最大实例数（%d）\n", MAX_WEBVIEW_INSTANCES);
        return -1;
    }

    // 1. 初始化 ArkWeb 配置
    OH_ArkWeb_Config config = {0};
    config.javaScriptEnabled = jsEnabled ? 1 : 0; // 1 = 启用JS
    config.allowFileAccess = fileAccess ? 1 : 0;  // 1 = 允许访问本地文件
    config.cacheMode = OH_ARKWEB_CACHE_MODE_DEFAULT; // 默认缓存模式

    // 2. 创建 ArkWeb 原生实例（核心 API：OH_ArkWeb_Create）
    OH_ArkWeb *webview = OH_ArkWeb_Create(&config);
    if (webview == NULL) {
        printf("ArkWeb: 创建实例失败\n");
        return -1;
    }

    // 3. 存储实例到数组
    int32_t instanceId = g_next_instance_id++;
    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview == NULL) {
            g_instances[i].webview = webview;
            g_instances[i].finishedCb = NULL;
            g_instances[i].errorCb = NULL;
            break;
        }
    }

    printf("ArkWeb: 创建实例成功，ID = %d\n", instanceId);
    return instanceId;
}

int32_t arkweb_wrapper_load_url(int32_t instanceId, const char *url) {
    if (url == NULL || strlen(url) == 0) {
        printf("ArkWeb: URL 为空\n");
        return -1;
    }

    // 查找实例
    OH_ArkWeb *webview = NULL;
    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL) {
            webview = g_instances[i].webview;
            break;
        }
    }

    if (webview == NULL) {
        printf("ArkWeb: 实例 ID %d 不存在\n", instanceId);
        return -1;
    }

    // 调用 ArkWeb C API 加载 URL
    OH_ArkWeb_LoadUrl(webview, url);
    printf("ArkWeb: 开始加载 URL = %s\n", url);
    return 0;
}

void arkweb_wrapper_set_callbacks(int32_t instanceId,
                                  OnPageFinishedCallback finishedCb,
                                  OnErrorCallback errorCb) {
    // 查找实例并设置回调
    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL) {
            g_instances[i].finishedCb = finishedCb;
            g_instances[i].errorCb = errorCb;

            // 配置 ArkWeb 原生回调（传递实例ID作为上下文）
            OH_ArkWeb_Callback arkwebCb = {0};
            int32_t *context = (int32_t *) malloc(sizeof(int32_t));
            *context = instanceId;
            arkwebCb.onPageFinished = OnArkWebPageFinished;
            arkwebCb.onReceivedError = OnArkWebError;
            OH_ArkWeb_SetCallback(g_instances[i].webview, &arkwebCb, context);
            break;
        }
    }
}

void arkweb_wrapper_destroy(int32_t instanceId) {
    // 查找实例并销毁
    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL) {
            OH_ArkWeb_Destroy(g_instances[i].webview); // 销毁 ArkWeb 实例
            g_instances[i].webview = NULL;
            g_instances[i].finishedCb = NULL;
            g_instances[i].errorCb = NULL;
            printf("ArkWeb: 销毁实例 ID = %d\n", instanceId);
            break;
        }
    }
}