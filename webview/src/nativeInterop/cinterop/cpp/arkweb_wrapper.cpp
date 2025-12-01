#include "arkweb_wrapper.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

// 定义ArkWeb Native API类型枚举
typedef enum {
    ARKWEB_NATIVE_COMPONENT = 0,
    ARKWEB_NATIVE_CONTROLLER,
    ARKWEB_NATIVE_WEB_MESSAGE_PORT,
    ARKWEB_NATIVE_WEB_MESSAGE,
    ARKWEB_NATIVE_COOKIE_MANAGER,
    ARKWEB_NATIVE_JAVASCRIPT_VALUE
} ArkWeb_NativeAPIVariantKind;

// 定义基础Native API类型
typedef struct {
    size_t size;
} ArkWeb_AnyNativeAPI;

// 定义Controller API结构体
typedef struct {
    size_t size;

    void (*runJavaScript)(const char *webTag, const void *javascriptObject);

    void (*registerJavaScriptProxy)(const char *webTag, const void *proxyObject);

    void (*deleteJavaScriptRegister)(const char *webTag, const char *objName);

    void (*refresh)(const char *webTag);

    void (*registerAsyncJavaScriptProxy)(const char *webTag, const void *proxyObject);

    void *(*createWebMessagePorts)(const char *webTag, size_t *size);

    void (*destroyWebMessagePorts)(void ***ports, size_t size);

    int (*postWebMessage)(const char *webTag, const char *name, void **webMessagePorts, size_t size,
                          const char *url);

    const char *(*getLastJavascriptProxyCallingFrameUrl)();
} ArkWeb_ControllerAPI;


// 定义Component API结构体
typedef struct {
    size_t size;

    void (*onControllerAttached)(const char *webTag, void *callback, void *userData);

    void (*onPageBegin)(const char *webTag, void *callback, void *userData);

    void (*onPageEnd)(const char *webTag, void *callback, void *userData);

    void (*onDestroy)(const char *webTag, void *callback, void *userData);
} ArkWeb_ComponentAPI;


// 声明获取Native API的函数
ArkWeb_AnyNativeAPI *OH_ArkWeb_GetNativeAPI(ArkWeb_NativeAPIVariantKind type);

// 定义滚动回调函数类型
typedef void (*ArkWeb_OnScrollCallback)(const char *webTag, int32_t x, int32_t y, void *userData);

bool OH_ArkWeb_RegisterScrollCallback(const char *webTag, ArkWeb_OnScrollCallback callback,
                                      void *userData);

// 存储 WebView 实例和回调（简化实现：用数组存储，生产环境可改用哈希表）
#define MAX_WEBVIEW_INSTANCES 10
typedef struct {
    void *webview;                     // ArkWeb 原生实例
    OnPageFinishedCallback finishedCb; // 页面完成回调
    OnErrorCallback errorCb;           // 错误回调
} WebViewWrapper;

static WebViewWrapper g_instances[MAX_WEBVIEW_INSTANCES] = {0};
static int32_t g_next_instance_id = 1; // 实例ID自增（从1开始，0表示无效）

// Component API 回调函数类型定义
typedef void (*OnComponentCallback)(const char *webTag, void *userData);

// 滚动回调函数类型定义
typedef void (*OnScrollCallback)(const char *webTag, int32_t x, int32_t y, void *userData);

// 存储 Component API 回调
typedef struct {
    OnComponentCallback controllerAttachedCb;
    OnComponentCallback pageBeginCb;
    OnComponentCallback pageEndCb;
    OnComponentCallback destroyCb;
    OnScrollCallback scrollCb;
} ComponentCallbacks;

static ComponentCallbacks g_component_callbacks[MAX_WEBVIEW_INSTANCES] = {0};

// Component API 回调实现
static void OnControllerAttachedCallback(const char *webTag, void *userData) {
    if (userData == NULL) return;
    int32_t instanceId = *(int32_t *) userData;

    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL &&
            g_component_callbacks[i].controllerAttachedCb != NULL) {
            g_component_callbacks[i].controllerAttachedCb(webTag, userData);
            break;
        }
    }
}

static void OnPageBeginCallback(const char *webTag, void *userData) {
    if (userData == NULL) return;
    int32_t instanceId = *(int32_t *) userData;

    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL && g_component_callbacks[i].pageBeginCb != NULL) {
            g_component_callbacks[i].pageBeginCb(webTag, userData);
            break;
        }
    }
}

static void OnPageEndCallback(const char *webTag, void *userData) {
    if (userData == NULL) return;
    int32_t instanceId = *(int32_t *) userData;

    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL && g_component_callbacks[i].pageEndCb != NULL) {
            g_component_callbacks[i].pageEndCb(webTag, userData);
            break;
        }
    }
}

static void OnDestroyCallback(const char *webTag, void *userData) {
    if (userData == NULL) return;
    int32_t instanceId = *(int32_t *) userData;

    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL && g_component_callbacks[i].destroyCb != NULL) {
            g_component_callbacks[i].destroyCb(webTag, userData);
            break;
        }
    }
}

static void OnScrollCallbackImpl(const char *webTag, int32_t x, int32_t y, void *userData) {
    if (userData == NULL) return;
    int32_t instanceId = *(int32_t *) userData;

    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL && g_component_callbacks[i].scrollCb != NULL) {
            g_component_callbacks[i].scrollCb(webTag, x, y, userData);
            break;
        }
    }
}

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
static void OnArkWebError(void *context, const void *error) {
    if (context == NULL || error == NULL) return;
    int32_t instanceId = *(int32_t *) context;
    // 查找实例并调用 K/N 传递的回调
    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL &&
            g_instances[i].errorCb != NULL) {
            // 由于我们不知道错误结构的确切定义，我们简单地传递错误码和消息
            g_instances[i].errorCb(-1, "Unknown error");
            break;
        }
    }
    free(context); // 释放上下文内存
}

// JavaScript执行回调
static void OnArkWebJsResult(void *context, const char *result, int32_t result_len) {
    // 处理JavaScript执行结果
}

// 添加Controller API相关函数
int32_t arkweb_wrapper_run_javascript(int32_t instanceId, const char *script) {
    // 查找实例
    void *webview = NULL;
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

    // 获取Controller API
    ArkWeb_ControllerAPI *controller = (ArkWeb_ControllerAPI *) OH_ArkWeb_GetNativeAPI(
            ARKWEB_NATIVE_CONTROLLER);
    if (controller == NULL) {
        printf("ArkWeb: Controller API 不可用\n");
        return -1;
    }

    // 检查函数指针是否存在
    if (controller->runJavaScript == NULL) {
        printf("ArkWeb: runJavaScript 函数不可用\n");
        return -1;
    }

    // 注意：这里需要构造正确的JavaScript对象
    controller->runJavaScript("webview", NULL);
    return 0;
}

int32_t arkweb_wrapper_refresh(int32_t instanceId) {
    // 查找实例
    void *webview = NULL;
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

    // 获取Controller API
    ArkWeb_ControllerAPI *controller = (ArkWeb_ControllerAPI *) OH_ArkWeb_GetNativeAPI(
            ARKWEB_NATIVE_CONTROLLER);
    if (controller == NULL) {
        printf("ArkWeb: Controller API 不可用\n");
        return -1;
    }

    // 检查函数指针是否存在
    if (controller->refresh == NULL) {
        printf("ArkWeb: refresh 函数不可用\n");
        return -1;
    }

    // 刷新页面
    controller->refresh("webview");
    return 0;
}

int32_t arkweb_wrapper_register_js_proxy(int32_t instanceId, const char *objName) {
    // 查找实例
    void *webview = NULL;
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

    // 获取Controller API
    ArkWeb_ControllerAPI *controller = (ArkWeb_ControllerAPI *) OH_ArkWeb_GetNativeAPI(
            ARKWEB_NATIVE_CONTROLLER);
    if (controller == NULL) {
        printf("ArkWeb: Controller API 不可用\n");
        return -1;
    }

    // 检查函数指针是否存在
    if (controller->registerJavaScriptProxy == NULL) {
        printf("ArkWeb: registerJavaScriptProxy 函数不可用\n");
        return -1;
    }

    // 注意：这里需要构造正确的代理对象
    controller->registerJavaScriptProxy("webview", NULL);
    return 0;
}

int32_t arkweb_wrapper_delete_js_proxy(int32_t instanceId, const char *objName) {
    // 查找实例
    void *webview = NULL;
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

    // 获取Controller API
    ArkWeb_ControllerAPI *controller = (ArkWeb_ControllerAPI *) OH_ArkWeb_GetNativeAPI(
            ARKWEB_NATIVE_CONTROLLER);
    if (controller == NULL) {
        printf("ArkWeb: Controller API 不可用\n");
        return -1;
    }

    // 检查函数指针是否存在
    if (controller->deleteJavaScriptRegister == NULL) {
        printf("ArkWeb: deleteJavaScriptRegister 函数不可用\n");
        return -1;
    }

    // 删除JavaScript代理对象
    controller->deleteJavaScriptRegister("webview", objName);
    return 0;
}

// 添加Component API相关函数
int32_t arkweb_wrapper_set_component_callbacks(
        int32_t instanceId,
        OnComponentCallback controllerAttachedCb,
        OnComponentCallback pageBeginCb,
        OnComponentCallback pageEndCb,
        OnComponentCallback destroyCb) {

    // 查找实例
    void *webview = NULL;
    int instanceIndex = -1;
    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL) {
            webview = g_instances[i].webview;
            instanceIndex = i;
            break;
        }
    }

    if (webview == NULL || instanceIndex == -1) {
        printf("ArkWeb: 实例 ID %d 不存在\n", instanceId);
        return -1;
    }

    // 获取Component API
    ArkWeb_ComponentAPI *component = (ArkWeb_ComponentAPI *) OH_ArkWeb_GetNativeAPI(
            ARKWEB_NATIVE_COMPONENT);
    if (component == NULL) {
        printf("ArkWeb: Component API 不可用\n");
        return -1;
    }

    // 存储回调函数
    g_component_callbacks[instanceIndex].controllerAttachedCb = controllerAttachedCb;
    g_component_callbacks[instanceIndex].pageBeginCb = pageBeginCb;
    g_component_callbacks[instanceIndex].pageEndCb = pageEndCb;
    g_component_callbacks[instanceIndex].destroyCb = destroyCb;

    // 分配用户数据
    int32_t *userData = (int32_t *) malloc(sizeof(int32_t));
    *userData = instanceId;

    // 注册Component回调
    if (component->onControllerAttached != NULL) {
        component->onControllerAttached("webview", (void *) OnControllerAttachedCallback, userData);
    }

    if (component->onPageBegin != NULL) {
        component->onPageBegin("webview", (void *) OnPageBeginCallback, userData);
    }

    if (component->onPageEnd != NULL) {
        component->onPageEnd("webview", (void *) OnPageEndCallback, userData);
    }

    if (component->onDestroy != NULL) {
        component->onDestroy("webview", (void *) OnDestroyCallback, userData);
    }

    return 0;
}

// 设置滚动回调
int32_t arkweb_wrapper_set_scroll_callback(int32_t instanceId, OnScrollCallback scrollCb) {
    // 查找实例
    void *webview = NULL;
    int instanceIndex = -1;
    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL) {
            webview = g_instances[i].webview;
            instanceIndex = i;
            break;
        }
    }

    if (webview == NULL || instanceIndex == -1) {
        printf("ArkWeb: 实例 ID %d 不存在\n", instanceId);
        return -1;
    }

    // 存储滚动回调
    g_component_callbacks[instanceIndex].scrollCb = scrollCb;

    // 注册滚动回调
    bool result = OH_ArkWeb_RegisterScrollCallback("webview", OnScrollCallbackImpl, NULL);
    if (!result) {
        printf("ArkWeb: 滚动回调注册失败\n");
        return -1;
    }

    return 0;
}

// -------------- C 中间层接口实现（暴露给 K/N）--------------
int32_t arkweb_wrapper_create(bool jsEnabled, bool fileAccess) {
    if (g_next_instance_id >= MAX_WEBVIEW_INSTANCES) {
        printf("ArkWeb: 超过最大实例数（%d）\n", MAX_WEBVIEW_INSTANCES);
        return -1;
    }

    // 1. 初始化 ArkWeb 配置
    // 注意：由于我们缺少具体的结构体定义，这里使用void*
    void *config = NULL; // 这里应该创建配置对象

    // 2. 创建 ArkWeb 原生实例
    void *webview = NULL; // 这里应该调用创建函数
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

            // 初始化Component回调
            g_component_callbacks[i].controllerAttachedCb = NULL;
            g_component_callbacks[i].pageBeginCb = NULL;
            g_component_callbacks[i].pageEndCb = NULL;
            g_component_callbacks[i].destroyCb = NULL;
            g_component_callbacks[i].scrollCb = NULL;
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
    void *webview = NULL;
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

    // 注意：这里应该调用加载URL的函数
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

            // 注意：这里应该设置回调
            printf("ArkWeb: 设置回调功能暂未完全实现\n");
            break;
        }
    }
}

void arkweb_wrapper_destroy(int32_t instanceId) {
    // 查找实例并销毁
    for (int i = 0; i < MAX_WEBVIEW_INSTANCES; i++) {
        if (g_instances[i].webview != NULL) {
            // 这里应该调用销毁函数
            g_instances[i].webview = NULL;
            g_instances[i].finishedCb = NULL;
            g_instances[i].errorCb = NULL;

            // 清除Component回调
            g_component_callbacks[i].controllerAttachedCb = NULL;
            g_component_callbacks[i].pageBeginCb = NULL;
            g_component_callbacks[i].pageEndCb = NULL;
            g_component_callbacks[i].destroyCb = NULL;
            g_component_callbacks[i].scrollCb = NULL;

            printf("ArkWeb: 销毁实例 ID = %d\n", instanceId);
            break;
        }
    }
}
