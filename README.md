# EzXHelper
一个使Xposed模块开发变的更轻松的kotlin库    
当前最新版本:0.3.4
## 使用方式
在build.gradle的dependencies下添加语句implementation 'com.github.kyuubiran:EzXHelper:0.3.4'
调用EzXHelperInit.initHandleLoadPackage(lpparam)初始化 就可以使用大部分工具类啦
### 例子
![image](docs/example.png)

### 功能
- 方法查找(通过条件、signature等查找)
- 属性查找(通过条件、signature等查找)
- 各种扩展属性(如Menber 可以直接判断是否公开、私有、静态等)
- 各种扩展函数(如可以直接用Class.newInstance调用有参构造实例化对象 obj.invokeMethod调用成员方法 obj.getObject获取对象等)
- 资源注入(使用宿主的context时 也能使用模块的资源)
- 在宿主内以宿主的身份启动模块(未注册)的Activity

### 使用本库的项目
[QAssistant](https://github.com/KitsunePie/QAssistant)
[QQCleaner](https://github.com/KyuubiRan/QQCleaner)
