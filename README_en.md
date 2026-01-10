# EzXHelper

[中文](https://github.com/KyuubiRan/EzXHelper/blob/3.x/README.md)

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.kyuubiran.ezxhelper/core)

A library to make Xposed modules development easier.     
Version 3.x has split the reflection part into a separate library, which can support running on JVM.

[![Telegram](https://img.shields.io/badge/Join-Telegram-blue)](https://t.me/EzXHelper) group to get
helps.

### Quick start

`build.gradle`

```groovy
dependencies {
    def ezxhelperVersion = '<version>'
    implementation "io.github.kyuubiran.ezxhelper:core:$ezxhelperVersion"
    // Xposed api 82
    implementation "io.github.kyuubiran.ezxhelper:xposed-api-82:$ezxhelperVersion"
    // Xposed api 100
    // implementation "io.github.kyuubiran.ezxhelper:xposed-api-100:$ezxhelperVersion"
    // If you need to use Android related utility extensions, you can include it
    implementation "io.github.kyuubiran.ezxhelper:android-utils:$ezxhelperVersion"
}
```

`build.gradle.kts`

```kotlin
dependencies {
    val ezxhelperVersion = "<version>"
    implementation("io.github.kyuubiran.ezxhelper:core:$ezxhelperVersion")
    // Xposed api 82
    implementation("io.github.kyuubiran.ezxhelper:xposed-api-82:$ezxhelperVersion")
    // Xposed api 100
    // implementation("io.github.kyuubiran.ezxhelper:xposed-api-100:$ezxhelperVersion")
    // If you need to use Android related utility extensions, you can include it
    implementation("io.github.kyuubiran.ezxhelper:android-utils:$ezxhelperVersion")
}
```

`xposed-api-82`

```kotlin
override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    // ...
    EzXposed.initHandleLoadPackage(lpparam)
}

// Optional
override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
    EzXposed.initZygote(startupParam)
}
```

`xposed-api-100`

```kotlin

override fun onPackageLoaded(param: PackageLoadedParam) {
    // ...
    EzXposed.initOnPackageLoaded(param)
}

```

`reflection-only`

```kotlin
// Optional
// Invoke this before use reflection utils
// or it will use ClassLoader.getSystemClassLoader() by default.
EzXReflection.init(yourClassLoader)
```

### Projects that use this library

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

### Star History

![Star History Chart](https://api.star-history.com/svg?repos=kyuubiran/ezxhelper&type=Date)

### Friend Links

Don't like EzXHelper? Try [KavaRef](https://github.com/HighCapable/KavaRef)!    
[DexKit](https://github.com/LuckyPray/DexKit) A high-performance runtime parsing library for dex
implemented in C++, used for lookup of obfuscated classes, methods, or properties.
