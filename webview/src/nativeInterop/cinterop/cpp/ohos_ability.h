#ifndef OHOS_ABILITY_H
#define OHOS_ABILITY_H

#ifdef __cplusplus
extern "C" {
#endif

// Want 相关函数声明
typedef struct OH_Want OH_Want;

OH_Want *OH_Want_Create(void);

int32_t OH_Want_SetAction(OH_Want *want, const char *action);

int32_t OH_Want_SetUri(OH_Want *want, const char *uri);

int32_t OH_Want_AddEntity(OH_Want *want, const char *entity);

void OH_Want_Destroy(OH_Want *want);

// Ability 相关函数声明
typedef struct OH_AbilityContext OH_AbilityContext;

int32_t OH_Ability_StartAbility(OH_AbilityContext *context, const OH_Want *want);

#ifdef __cplusplus
}
#endif

#endif // OHOS_ABILITY_H
