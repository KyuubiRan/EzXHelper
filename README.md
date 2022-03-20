# EzXHelper

一个使Xposed模块开发变的更轻松的kotlin库    
当前最新版本:0.7.5

本库依旧处于开发过程中，你可能会遇到包括但不限于以下问题：

- 出现八哥
- 函数名称、参数变动

注意：

- 0.5.0版本更改了部分函数命名，比如findXxxByCondition一律改成了findXxx，并且参数也进行了修改，更新到此版本时请注意修改。
- 0.7.1版本更改了方法、构造的调用参数类型，请将原来的`arrayOf()`函数替换成`args()`以及`argTypes()`。
- 0.7.5将`minSdkVersion`从 21 提升至 **24** 。

## 使用方式

在build.gradle的dependencies下添加语句 `implementation 'com.github.kyuubiran:EzXHelper:0.7.5'`   
调用`EzXHelperInit.initHandleLoadPackage(lpparam)`初始化 就可以使用大部分工具类啦

### 例子

![image](docs/example.png)

### 功能

- 方法查找(通过条件、Descriptor等查找)
- 属性查找(通过条件、Descriptor等查找)
- 各种扩展属性(如Menber 可以直接判断是否公开、私有、静态等)
- 各种扩展函数(如可以直接用Class.newInstance调用有参构造实例化对象 obj.invokeMethod调用成员方法 obj.getObject获取对象等)
- 资源注入(使用宿主的context时 也能使用模块的资源)
- 在宿主内以宿主的身份启动模块(未注册)的Activity

### 使用本库的项目

|                            项目名称                             | 项目介绍                                                                                              |
|:-----------------------------------------------------------:|:--------------------------------------------------------------------------------------------------|
| [Template](https://github.com/KyuubiRan/ezxhepler-template) | EzXHelper模板                                                                                       |
|   [QAssistant](https://github.com/KitsunePie/QAssistant)    | 兼具实用与美观于一身的 QQ 小帮手                                                                                |
|     [QQCleaner](https://github.com/KyuubiRan/QQCleaner)     | 瘦身模块                                                                                              |
| [HideMyApplist](https://github.com/Dr-TSNG/Hide-My-Applist) | 隐藏应用列表                                                                                            |
|   [OneText](https://github.com/lz233/OneText_For_Android)   | A neat little application that can display some custom sentences through widgets on the launcher. |
|    [XAutoDaily](https://github.com/LuckyPray/XAutoDaily)    | XAutoDaily 是一个兼容QQ大部分版本的开源签到 Xposed 模块                                                            |
|      [QAuxiliary](https://github.com/cinit/QAuxiliary)      | QAuxiliary 是一个基于 QNotified 的开源 Xposed 模块                                                          |
|            [TMoe](https://github.com/cinit/TMoe)            | TMoe 是一个兼容若干第三方开源 Telegram 客户端的开源 Xposed 模块                                                       |
|            [FuckCoolapk R](https://github.com/Xposed-Modules-Repo/org.hello.coolapk)            | Fuck Coolapk Again                                                        |
