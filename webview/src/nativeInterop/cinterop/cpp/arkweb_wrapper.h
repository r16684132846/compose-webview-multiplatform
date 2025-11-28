#ifndef ARKWEB_WRAPPER_H
#define ARKWEB_WRAPPER_H

#include <stdint.h>
#include <stdlib.h>

// 回调函数类型定义（供 Kotlin/Native 传递 Lambda）
// 页面加载完成回调：url = 加载完成的URL
typedef void (*OnPageFinishedCallback)(const char *url);

// 页面加载错误回调：code = 错误码，msg = 错误信息
typedef void (*OnErrorCallback)(int32_t code, const char *msg);

// 1. 创建 WebView 实例（返回实例ID，供后续操作）
// 参数：jsEnabled = 是否启用JavaScript，fileAccess = 是否允许访问本地文件
int32_t arkweb_wrapper_create(bool jsEnabled, bool fileAccess);

// 2. 加载 URL
// 参数：instanceId = 实例ID，url = 要加载的URL（UTF-8字符串）
int32_t arkweb_wrapper_load_url(int32_t instanceId, const char *url);

// 3. 设置回调（页面完成/错误）
// 参数：instanceId = 实例ID，finishedCb = 完成回调，errorCb = 错误回调
void arkweb_wrapper_set_callbacks(int32_t instanceId,
                                  OnPageFinishedCallback finishedCb,
                                  OnErrorCallback errorCb);

// 4. 销毁 WebView 实例（释放内存）
void arkweb_wrapper_destroy(int32_t instanceId);

#endif // ARKWEB_WRAPPER_H