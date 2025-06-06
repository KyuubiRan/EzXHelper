# EzXHelper

[English](https://github.com/KyuubiRan/EzXHelper/blob/3.x/README_en.md)  

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.kyuubiran.ezxhelper/core)
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.kyuubiran.ezxhelper/xposed-api-82)

一个使Xposed模块开发变的更轻松的工具库。  
3.x版本已经将反射部分拆分为单独的库，可以支持在JVM上运行。

[![Telegram](https://img.shields.io/badge/Join-Telegram-blue)](https://t.me/EzXHelper) 群组来获取帮助

### 快速开始

`build.gradle`
```groovy
dependencies {
    def ezxhelperVersion = '<version>'
    implementation "io.github.kyuubiran.ezxhelper:core:$ezxhelperVersion"
    implementation "io.github.kyuubiran.ezxhelper:xposed-api-82:$ezxhelperVersion"
}
```

`build.gradle.kts`
```kotlin
dependencies {
    val ezxhelperVersion = "<version>"
    implementation("io.github.kyuubiran.ezxhelper:core:$ezxhelperVersion")
    implementation("io.github.kyuubiran.ezxhelper:xposed-api-82:$ezxhelperVersion")
}
```

`xposed-api-82`
```kotlin
override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    // ...
    EzXposed.initHandleLoadPackage(lpparam)
}
// 可选
override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
    EzXposed.initZygote(startupParam)
}
```

`reflection-only`
```kotlin
// 可选
// 在使用本库之前，调用此函数设置默认的 ClassLoader，否则它会默认使用 ClassLoader.getSystemClassLoader() 来作为反射的 ClassLoader。
EzXReflection.init(yourClassLoader)
```

### 使用本库的项目

|                                   项目名称                                    | 项目介绍                                                                                              |
|:-------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------|
|        [Template](https://github.com/KyuubiRan/ezxhepler-template)        | EzXHelper模板                                                                                       |
|          [QAssistant](https://github.com/KitsunePie/QAssistant)           | 兼具实用与美观于一身的 QQ 小帮手                                                                                |
|            [QQCleaner](https://github.com/KyuubiRan/QQCleaner)            | 瘦身模块                                                                                              |
|        [HideMyApplist](https://github.com/Dr-TSNG/Hide-My-Applist)        | 隐藏应用列表                                                                                            |
|          [OneText](https://github.com/lz233/OneText_For_Android)          | A neat little application that can display some custom sentences through widgets on the launcher. |
|           [XAutoDaily](https://github.com/LuckyPray/XAutoDaily)           | XAutoDaily 是一个兼容QQ大部分版本的开源签到 Xposed 模块                                                            |
|             [QAuxiliary](https://github.com/cinit/QAuxiliary)             | QAuxiliary 是一个基于 QNotified 的开源 Xposed 模块                                                          |
|                   [TMoe](https://github.com/cinit/TMoe)                   | TMoe 是一个兼容若干第三方开源 Telegram 客户端的开源 Xposed 模块                                                       |
| [FuckCoolapk R](https://github.com/Xposed-Modules-Repo/org.hello.coolapk) | Fuck Coolapk Again                                                                                |
|    [WooBox For MIUI](https://github.com/Simplicity-Team/WooBoxForMIUI)    | 一个基于 MIUI13(Android 12) 适配的自定义工具                                                                  |
| [WooBox For ColorOS](https://github.com/Simplicity-Team/WooBoxForColorOS) | 一个基于 ColorOS12(Android 12) 适配的自定义工具                                                               |
|              [Miui XXL](https://github.com/YuKongA/Miui_XXL)              | Miui XXL 是一个基于 Miui14 的大杂烩模块                                                                      |
|             [TwiFucker](https://github.com/Dr-TSNG/TwiFucker)             | Yet Another Adkiller for Twitter                                                                  |
|               [PureNGA](https://github.com/chr233/PureNGA)                | PureNGA 是一个去除NGA论坛APP广告的开源模块                                                                      |

### 星路历程

![Star History Chart](https://api.star-history.com/svg?repos=kyuubiran/ezxhelper&type=Date)


### 友情链接

不喜欢EzXHelper的写法？来试试 [KavaRef](https://github.com/HighCapable/KavaRef) 吧！  
[DexKit](https://github.com/LuckyPray/DexKit) 一个使用 C++ 实现的 dex 高性能运行时解析库，用于查找被混淆的类、方法或者属性。  
