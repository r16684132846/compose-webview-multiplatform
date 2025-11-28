//#ifndef OH_ARKWEB_API_H
//#define OH_ARKWEB_API_H
//
//#include <stdint.h>
//#include <stdbool.h>
//#include <ohos_arkweb.h>  // 官方ArkWeb头文件（需配置Native SDK依赖）
//
//// -------------------------- 官方标准类型别名（简化使用）--------------------------
//// 官方核心句柄（对应你的OH_WebView）
//typedef OH_ArkWeb;
//// 官方配置结构体（必须先创建配置）
//typedef OH_ArkWebConfig;
//// 官方上下文类型（替代void*，明确类型）
//typedef OH_AbilityContext;
//// 官方错误类型
//typedef OH_ArkWebError;
//
//// -------------------------- 回调结构体（遵循官方统一回调规范）--------------------------
//// 页面加载开始回调
//typedef void (*OH_WebViewPageStartedCallback)(OH_WebView *webview, const char *url,
//                                              void *user_data);
//
//// 页面加载完成回调
//typedef void (*OH_WebViewPageFinishedCallback)(OH_WebView *webview, const char *url,
//                                               void *user_data);
//
//// 加载错误回调
//typedef void (*OH_WebViewErrorCallback)(OH_WebView *webview, const OH_WebViewError *error,
//                                        void *user_data);
//
//// JavaScript执行回调（官方标准参数）
//typedef void (*OH_WebViewJsCallback)(const char *result, int32_t result_len, void *user_data);
//
//// -------------------------- 核心接口（对齐官方函数签名，简化调用）--------------------------
///**
// * 创建WebView配置（必须先创建，再传入WebView）
// */
//static inline OH_WebViewConfig *oh_webview_config_create() {
//    return OH_ArkWebConfig_Create();  // 调用官方配置创建接口
//}
//
///**
// * 销毁WebView配置
// */
//static inline void oh_webview_config_destroy(OH_WebViewConfig *config) {
//    if (config != NULL) {
//        OH_ArkWebConfig_Destroy(config);  // 调用官方配置销毁接口
//    }
//}
//
///**
// * 配置是否启用JavaScript（官方核心配置项）
// */
//static inline void oh_webview_config_set_js_enabled(OH_WebViewConfig *config, bool enabled) {
//    if (config != NULL) {
//        OH_ArkWebConfig_SetJavaScriptEnabled(config, enabled);
//    }
//}
//
///**
// * 创建WebView实例（官方标准接口，必须传入上下文和配置）
// */
//static inline OH_WebView *oh_webview_create(OH_WebViewContext *context, OH_WebViewConfig *config) {
//    if (context == NULL || config == NULL) {
//        return NULL;
//    }
//    return OH_ArkWeb_Create(context, config);  // 调用官方WebView创建接口
//}
//
///**
// * 销毁WebView实例
// */
//static inline void oh_webview_destroy(OH_WebView *webview) {
//    if (webview != NULL) {
//        OH_ArkWeb_Destroy(webview);  // 调用官方WebView销毁接口
//    }
//}
//
///**
// * 加载URL（官方标准接口）
// */
//static inline void oh_webview_load_url(OH_WebView *webview, const char *url) {
//    if (webview != NULL && url != NULL) {
//        OH_ArkWeb_LoadUrl(webview, url);
//    }
//}
//
///**
// * 加载HTML数据（官方标准接口）
// */
//static inline void
//oh_webview_load_data(OH_WebView *webview, const char *data, const char *mime_type,
//                     const char *encoding, const char *base_url) {
//    if (webview != NULL && data != NULL) {
//        OH_ArkWeb_LoadData(webview, data, mime_type, encoding, base_url);
//    }
//}
//
///**
// * 执行JavaScript（对接官方接口）
// */
//static inline void oh_webview_evaluate_js(OH_WebView *webview, const char *script,
//                                          OH_WebViewJsCallback callback, void *user_data) {
//    if (webview != NULL && script != NULL) {
//        OH_ArkWeb_EvaluateJavaScript(webview, script, callback, user_data);
//    }
//}
//
///**
// * 导航相关接口（对齐官方）
// */
//static inline bool oh_webview_can_go_back(OH_WebView *webview) {
//    return (webview != NULL) ? OH_ArkWeb_CanGoBack(webview) : false;
//}
//
//static inline bool oh_webview_can_go_forward(OH_WebView *webview) {
//    return (webview != NULL) ? OH_ArkWeb_CanGoForward(webview) : false;
//}
//
//static inline void oh_webview_go_back(OH_WebView *webview) {
//    if (webview != NULL && oh_webview_can_go_back(webview)) {
//        OH_ArkWeb_GoBack(webview);
//    }
//}
//
//static inline void oh_webview_go_forward(OH_WebView *webview) {
//    if (webview != NULL && oh_webview_can_go_forward(webview)) {
//        OH_ArkWeb_GoForward(webview);
//    }
//}
//
//static inline void oh_webview_reload(OH_WebView *webview) {
//    if (webview != NULL) {
//        OH_ArkWeb_Reload(webview);
//    }
//}
//
//static inline void oh_webview_stop_loading(OH_WebView *webview) {
//    if (webview != NULL) {
//        OH_ArkWeb_StopLoading(webview);
//    }
//}
//
///**
// * 注册事件回调（基于官方统一回调结构体封装）
// */
//static inline void oh_webview_set_callbacks(OH_WebView *webview,
//                                            OH_WebViewPageStartedCallback started_cb,
//                                            OH_WebViewPageFinishedCallback finished_cb,
//                                            OH_WebViewErrorCallback error_cb,
//                                            void *user_data) {
//    if (webview == NULL) {
//        return;
//    }
//    // 官方统一回调结构体
//    OH_ArkWebCallback callback = {0};
//    // 绑定自定义回调到官方结构体（需根据官方文档补充回调映射）
//    callback.onPageStarted = [](void *webview, const char *url, void *user_data) {
//        OH_WebViewPageStartedCallback cb = (OH_WebViewPageStartedCallback) user_data;
//        if (cb != NULL) {
//            cb((OH_WebView *) webview, url, user_data);
//        }
//    };
//    callback.onPageFinished = [](void *webview, const char *url, void *user_data) {
//        OH_WebViewPageFinishedCallback cb = (OH_WebViewPageFinishedCallback) user_data;
//        if (cb != NULL) {
//            cb((OH_WebView *) webview, url, user_data);
//        }
//    };
//    callback.onError = [](void *webview, const OH_ArkWebError *error, void *user_data) {
//        OH_WebViewErrorCallback cb = (OH_WebViewErrorCallback) user_data;
//        if (cb != NULL) {
//            cb((OH_WebView *) webview, error, user_data);
//        }
//    };
//    // 注册到官方接口
//    OH_ArkWeb_SetCallback(webview, &callback, user_data);
//}
//
///**
// * 打开系统浏览器（官方标准接口）
// */
//static inline void oh_webview_open_browser(OH_WebViewContext *context, const char *url) {
//    if (context != NULL && url != NULL) {
//        OH_ArkWeb_OpenBrowser(context, url);  // 官方打开浏览器接口
//    }
//}
//
//#endif // OH_ARKWEB_API_H