//#include "webview_api.h"
//#include <stdlib.h>
//#include <string.h>
//#include <stdio.h>
//
//// 鸿蒙官方 ArkWeb 头文件（必须包含，对接底层实现）
//#include <ohos_arkweb.h>
//// 鸿蒙 Ability 上下文头文件（用于获取 AbilityContext）
//#include <ability_runtime/ability_context.h>
//// 鸿蒙 Want 相关头文件（仅保留必要依赖）
//#include <want/want.h>
//
//// --- 全局变量：保存 AbilityContext（保持你的原有设计）---
//static OH_AbilityContext *g_ability_context = nullptr;
//
//// C 回调函数类型定义（保持你的原有设计）
//typedef struct {
//    void (*callback)(const char *result, void *user_data);
//    void *user_data;
//} JsCallbackData;
//
//// JavaScript 执行回调（适配官方 ArkWeb 回调规范）
//void js_execute_callback(const char *result, int32_t result_len, void *user_data) {
//    JsCallbackData *data = (JsCallbackData *) user_data;
//    if (data != nullptr && data->callback != nullptr) {
//        // 官方回调返回 result_len，确保字符串安全（避免越界）
//        char *safe_result = (char *)malloc(result_len + 1);
//        if (safe_result != nullptr) {
//            memcpy(safe_result, result, result_len);
//            safe_result[result_len] = '\0';
//            data->callback(safe_result, data->user_data);
//            free(safe_result);
//        }
//    }
//    free(data); // 释放回调数据
//}
//
//// -------------------------- 核心接口实现（对接官方 ArkWeb）--------------------------
//// 创建 WebView 实例（关键修正：用官方 ArkWeb 配置+创建接口）
//OH_WebView *create_webview(void *context) {
//    if (context == nullptr) {
//        printf("create_webview: context is null\n");
//        return nullptr;
//    }
//
//    // 1. 转换上下文为官方类型（OH_AbilityContext*）
//    OH_AbilityContext *ability_ctx = static_cast<OH_AbilityContext*>(context);
//
//    // 2. 创建官方 WebView 配置（必须步骤，官方强制要求）
//    OH_ArkWebConfig *config = OH_ArkWebConfig_Create();
//    if (config == nullptr) {
//        printf("create_webview: failed to create OH_ArkWebConfig\n");
//        return nullptr;
//    }
//
//    // 3. 配置核心参数（启用 JavaScript，默认关闭）
//    OH_ArkWebConfig_SetJavaScriptEnabled(config, true);
//    // 可选：配置缓存模式（按需调整）
//    OH_ArkWebConfig_SetCacheMode(config, OH_ARKWEB_CACHE_MODE_DEFAULT);
//
//    // 4. 调用官方接口创建 WebView 实例
//    OH_ArkWeb *webview = OH_ArkWeb_Create(ability_ctx, config);
//    if (webview == nullptr) {
//        printf("create_webview: failed to create OH_ArkWeb\n");
//        OH_ArkWebConfig_Destroy(config); // 创建失败释放配置
//        return nullptr;
//    }
//
//    // 5. 配置创建后可销毁（WebView 会内部持有配置，无需外部保留）
//    OH_ArkWebConfig_Destroy(config);
//    printf("create_webview: success\n");
//    return reinterpret_cast<OH_WebView*>(webview); // 句柄转换（兼容你的原有定义）
//}
//
//// 销毁 WebView 实例（对接官方销毁接口）
//void destroy_webview(OH_WebView *webview) {
//    if (webview == nullptr) return;
//
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWeb_Destroy(ark_web);
//    printf("destroy_webview: success\n");
//}
//
//// 加载 URL（对接官方 OH_ArkWeb_LoadUrl）
//void load_url(OH_WebView *webview, const char *url) {
//    if (webview == nullptr || url == nullptr || strlen(url) == 0) {
//        printf("load_url: invalid webview or url\n");
//        return;
//    }
//
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWeb_LoadUrl(ark_web, url);
//    printf("load_url: start loading %s\n", url);
//}
//
//// 执行 JavaScript（对接官方 OH_ArkWeb_EvaluateJavaScript）
//void evaluate_javascript(OH_WebView *webview, const char *script,
//                         void (*callback)(const char *result, void *user_data),
//                         void *user_data) {
//    if (webview == nullptr || script == nullptr || strlen(script) == 0) {
//        printf("evaluate_javascript: invalid param\n");
//        return;
//    }
//
//    // 封装回调数据
//    JsCallbackData *cb_data = (JsCallbackData *)malloc(sizeof(JsCallbackData));
//    if (cb_data == nullptr) return;
//    cb_data->callback = callback;
//    cb_data->user_data = user_data;
//
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWeb_EvaluateJavaScript(ark_web, script, js_execute_callback, cb_data);
//}
//
//// 判断是否可后退（对接官方 OH_ArkWeb_CanGoBack）
//void can_go_back() {
//    // 假设g_GlobalUIEnv是全局的napi_env环境变量
//    if (g_GlobalUIEnv == nullptr) {
//        return;
//    }
//
//    napi_status status;
//    napi_value webviewModule, webviewControllerConstructor, webviewControllerInstance;
//    napi_value backwardFn;
//
//    // 加载webview模块
//    status = napi_load_module(g_GlobalUIEnv, "@ohos.web.webview", &webviewModule);
//    if (status != napi_ok) {
//        return;
//    }
//
//    // 获取WebviewController构造函数
//    status = napi_get_named_property(g_GlobalUIEnv, webviewModule, "WebviewController", &webviewControllerConstructor);
//    if (status != napi_ok) {
//        return;
//    }
//
//    // 创建WebviewController实例
//    status = napi_new_instance(g_GlobalUIEnv, webviewControllerConstructor, 0, nullptr, &webviewControllerInstance);
//    if (status != napi_ok) {
//        return;
//    }
//
//    // 获取backward方法
//    status = napi_get_named_property(g_GlobalUIEnv, webviewControllerInstance, "backward", &backwardFn);
//    if (status != napi_ok) {
//        return;
//    }
//
//    // 调用backward方法，无返回值
//    napi_call_function(g_GlobalUIEnv, webviewControllerInstance, backwardFn, 0, nullptr, nullptr);
//}
//
//// 判断是否可前进（对接官方 OH_ArkWeb_CanGoForward）
//bool can_go_forward(OH_WebView *webview) {
//    if (webview == nullptr) return false;
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    return OH_ArkWeb_CanGoForward(ark_web);
//}
//
//// 后退导航（对接官方 OH_ArkWeb_GoBack）
//void go_back(OH_WebView *webview) {
//    if (webview == nullptr || !can_go_back(webview)) return;
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWeb_GoBack(ark_web);
//}
//
//// 前进导航（对接官方 OH_ArkWeb_GoForward）
//void go_forward(OH_WebView *webview) {
//    if (webview == nullptr || !can_go_forward(webview)) return;
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWeb_GoForward(ark_web);
//}
//
//// 重新加载页面（对接官方 OH_ArkWeb_Reload）
//void reload(OH_WebView *webview) {
//    if (webview == nullptr) return;
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWeb_Reload(ark_web);
//}
//
//// 停止加载（对接官方 OH_ArkWeb_StopLoading）
//void stop_loading(OH_WebView *webview) {
//    if (webview == nullptr) return;
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWeb_StopLoading(ark_web);
//}
//
//// 加载 HTML 数据（对接官方 OH_ArkWeb_LoadData）
//void load_data(OH_WebView *webview, const char *data, const char *mime_type,
//               const char *encoding, const char *base_url) {
//    if (webview == nullptr || data == nullptr || strlen(data) == 0) {
//        printf("load_data: invalid param\n");
//        return;
//    }
//
//    // 补全默认参数（避免传空导致失败）
//    const char *default_mime = (mime_type == nullptr) ? "text/html" : mime_type;
//    const char *default_encoding = (encoding == nullptr) ? "UTF-8" : encoding;
//    const char *default_base = (base_url == nullptr) ? "" : base_url;
//
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWeb_LoadData(ark_web, data, default_mime, default_encoding, default_base);
//    printf("load_data: start loading HTML\n");
//}
//
//// -------------------------- 回调注册（适配官方统一回调结构体）--------------------------
//// 页面开始加载回调（内部映射到官方回调）
//void set_page_started_callback(OH_WebView *webview,
//                               void (*callback)(const char *url, void *user_data),
//                               void *user_data) {
//    if (webview == nullptr || callback == nullptr) return;
//
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWebCallback ark_callback = {0};
//
//    // 官方回调 -> 你的自定义回调（类型映射）
//    ark_callback.onPageStarted = [](void *webview_ptr, const char *url, void *cb_data) {
//        if (webview_ptr == nullptr || url == nullptr) return;
//        void (*user_cb)(const char*, void*) = reinterpret_cast<decltype(user_cb)>(cb_data);
//        user_cb(url, cb_data);
//    };
//
//    OH_ArkWeb_SetCallback(ark_web, &ark_callback, user_data);
//}
//
//// 页面加载完成回调（同理映射）
//void set_page_finished_callback(OH_WebView *webview,
//                                void (*callback)(const char *url, void *user_data),
//                                void *user_data) {
//    if (webview == nullptr || callback == nullptr) return;
//
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWebCallback ark_callback = {0};
//
//    ark_callback.onPageFinished = [](void *webview_ptr, const char *url, void *cb_data) {
//        if (webview_ptr == nullptr || url == nullptr) return;
//        void (*user_cb)(const char*, void*) = reinterpret_cast<decltype(user_cb)>(cb_data);
//        user_cb(url, cb_data);
//    };
//
//    OH_ArkWeb_SetCallback(ark_web, &ark_callback, user_data);
//}
//
//// 加载错误回调（映射官方错误信息）
//void set_error_callback(OH_WebView *webview,
//                        void (*callback)(int32_t error_code, const char *description,
//                                         const char *failing_url, void *user_data),
//                        void *user_data) {
//    if (webview == nullptr || callback == nullptr) return;
//
//    OH_ArkWeb *ark_web = reinterpret_cast<OH_ArkWeb*>(webview);
//    OH_ArkWebCallback ark_callback = {0};
//
//    ark_callback.onError = [](void *webview_ptr, const OH_ArkWebError *error, void *cb_data) {
//        if (webview_ptr == nullptr || error == nullptr) return;
//        void (*user_cb)(int32_t, const char*, const char*, void*) = reinterpret_cast<decltype(user_cb)>(cb_data);
//        // 官方错误结构体 -> 你的回调参数
//        user_cb(error->errorCode, error->description, error->failingUrl, cb_data);
//    };
//
//    OH_ArkWeb_SetCallback(ark_web, &ark_callback, user_data);
//}
//
//// --- 设置 AbilityContext（保持你的原有设计，修正类型安全）---
//void OH_SetAbilityContext(void *context) {
//    if (context == nullptr) {
//        printf("OH_SetAbilityContext: context is null\n");
//        g_ability_context = nullptr;
//        return;
//    }
//
//    // 强制转换为官方类型（避免无效上下文）
//    g_ability_context = static_cast<OH_AbilityContext*>(context);
//    printf("OH_SetAbilityContext: success\n");
//}
//
//// --- 打开系统浏览器（关键修正：用官方 ArkWeb 接口，无需手动创建 Want）---
//void OH_OpenBrowser(const char *url) {
//    if (url == nullptr || strlen(url) == 0) {
//        printf("OH_OpenBrowser: url is empty\n");
//        return;
//    }
//
//    if (g_ability_context == nullptr) {
//        printf("OH_OpenBrowser: call OH_SetAbilityContext first\n");
//        return;
//    }
//
//    // 调用官方接口直接打开浏览器（自动处理 Want/Entity，无需手动封装）
//    int32_t result = OH_ArkWeb_OpenBrowser(g_ability_context, url);
//    if (result == 0) {
//        printf("OH_OpenBrowser: success, url=%s\n", url);
//    } else {
//        printf("OH_OpenBrowser: failed, error code=%d\n", result);
//    }
//}
