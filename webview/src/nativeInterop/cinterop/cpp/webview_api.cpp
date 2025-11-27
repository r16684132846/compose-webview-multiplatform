#include "webview_api.h"
#include "webview.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

// 鸿蒙 Ability SDK 头文件
#include <ability_runtime/ability_context.h>
#include <want/want.h>
#include "ohos_ability.h"

// --- 全局变量：保存 AbilityContext ---
static void *g_ability_context = nullptr;

// C 回调函数类型定义
typedef struct {
    void (*callback)(const char *result, void *user_data);

    void *user_data;
} JsCallbackData;

// JavaScript 执行回调
void js_execute_callback(const char *result, void *user_data) {
    JsCallbackData *data = (JsCallbackData *) user_data;
    if (data->callback) {
        data->callback(result, data->user_data);
    }
    free(data);
}

// 创建 WebView 实例
OH_WebView *create_webview(void *context) {
    OH_WebViewManager *manager = OH_WebViewManager_GetInstance();
    OH_WebViewManager_Init(manager);
    return OH_WebView_Create((OH_Context *) context, manager);
}

// 销毁 WebView 实例
void destroy_webview(OH_WebView *webview) {
    if (webview) {
        OH_WebView_Destroy(webview);
    }
}

// 加载 URL
void load_url(OH_WebView *webview, const char *url) {
    if (webview && url) {
        OH_WebView_LoadUrl(webview, url);
    }
}

// 执行 JavaScript
void evaluate_javascript(OH_WebView *webview, const char *script,
                         void (*callback)(const char *result, void *user_data),
                         void *user_data) {
    if (webview && script) {
        JsCallbackData *cb_data = (JsCallbackData *) malloc(sizeof(JsCallbackData));
        cb_data->callback = callback;
        cb_data->user_data = user_data;
        OH_WebView_EvaluateJavascript(webview, script, js_execute_callback, cb_data);
    }
}

// 判断是否可后退
bool can_go_back(OH_WebView *webview) {
    if (webview) {
        return OH_WebView_CanGoBack(webview);
    }
    return false;
}

// 判断是否可前进
bool can_go_forward(OH_WebView *webview) {
    if (webview) {
        return OH_WebView_CanGoForward(webview);
    }
    return false;
}

// 后退导航
void go_back(OH_WebView *webview) {
    if (webview) {
        OH_WebView_GoBack(webview);
    }
}

// 前进导航
void go_forward(OH_WebView *webview) {
    if (webview) {
        OH_WebView_GoForward(webview);
    }
}

// 重新加载页面
void reload(OH_WebView *webview) {
    if (webview) {
        OH_WebView_Reload(webview);
    }
}

// 停止加载
void stop_loading(OH_WebView *webview) {
    if (webview) {
        OH_WebView_StopLoading(webview);
    }
}

// 加载HTML数据
void load_data(OH_WebView *webview, const char *data, const char *mime_type,
               const char *encoding, const char *base_url) {
    if (webview && data) {
        OH_WebView_LoadData(webview, data, mime_type, encoding, base_url);
    }
}

// 注册页面开始加载回调
void set_page_started_callback(OH_WebView *webview,
                               void (*callback)(const char *url, void *user_data),
                               void *user_data) {
    if (webview) {
        OH_WebView_SetPageStartedCallback(webview, callback, user_data);
    }
}

// 注册页面加载完成回调
void set_page_finished_callback(OH_WebView *webview,
                                void (*callback)(const char *url, void *user_data),
                                void *user_data) {
    if (webview) {
        OH_WebView_SetPageFinishedCallback(webview, callback, user_data);
    }
}

// 注册加载错误回调
void set_error_callback(OH_WebView *webview,
                        void (*callback)(int32_t error_code, const char *description,
                                         const char *failing_url, void *user_data),
                        void *user_data) {
    if (webview) {
        OH_WebView_SetErrorCallback(webview, callback, user_data);
    }
}

// --- 新增功能：设置 AbilityContext ---
void OH_SetAbilityContext(void *context) {
    g_ability_context = context;
    if (context) {
        printf("OH_SetAbilityContext: AbilityContext set successfully.\n");
    } else {
        printf("OH_SetAbilityContext: Context is null.\n");
    }
}

// --- 新增功能：打开系统浏览器 ---
void OH_OpenBrowser(const char *url) {
    if (!url || strlen(url) == 0) {
        printf("OH_OpenBrowser: URL is null or empty\n");
        return;
    }

    // 检查 AbilityContext 是否已设置
    if (!g_ability_context) {
        printf("OH_OpenBrowser: AbilityContext is not initialized. Call OH_SetAbilityContext first.\n");
        return;
    }

    // 创建 Want 对象
    OH_Want *want = OH_Want_Create();
    if (!want) {
        printf("OH_OpenBrowser: Failed to create Want\n");
        return;
    }

    // 设置 Action: ohos.want.action.viewData
    const char *action = "ohos.want.action.viewData";
    int32_t result = OH_Want_SetAction(want, action);
    if (result != 0) {
        printf("OH_OpenBrowser: Failed to set action, error code: %d\n", result);
        OH_Want_Destroy(want);
        return;
    }

    // 设置 URI
    result = OH_Want_SetUri(want, url);
    if (result != 0) {
        printf("OH_OpenBrowser: Failed to set URI, error code: %d\n", result);
        OH_Want_Destroy(want);
        return;
    }

    // 添加 Entity: entity.system.browsable
    const char *entity = "entity.system.browsable";
    result = OH_Want_AddEntity(want, entity);
    if (result != 0) {
        printf("OH_OpenBrowser: Failed to add entity, error code: %d\n", result);
        OH_Want_Destroy(want);
        return;
    }

    // 启动 Ability（打开浏览器）
    result = OH_Ability_StartAbility((OH_AbilityContext *) g_ability_context, want);
    if (result != 0) {
        printf("OH_OpenBrowser: Failed to start ability, error code: %d\n", result);
    } else {
        printf("OH_OpenBrowser: Successfully opened browser with URL: %s\n", url);
    }

    // 销毁 Want
    OH_Want_Destroy(want);
}