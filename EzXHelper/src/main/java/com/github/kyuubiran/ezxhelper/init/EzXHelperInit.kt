package com.github.kyuubiran.ezxhelper.init

import android.content.Context
import android.content.res.Resources
import android.content.res.XModuleResources
import com.github.kyuubiran.ezxhelper.init.InitFields.LOG_TAG
import com.github.kyuubiran.ezxhelper.init.InitFields.TOAST_TAG
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.init.InitFields.ezXClassLoader
import com.github.kyuubiran.ezxhelper.init.InitFields.hostPackageName
import com.github.kyuubiran.ezxhelper.init.InitFields.modulePath
import com.github.kyuubiran.ezxhelper.init.InitFields.moduleRes
import com.github.kyuubiran.ezxhelper.utils.Log
import com.github.kyuubiran.ezxhelper.utils.invokeMethod
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

object EzXHelperInit {
    /**
     * 使用本库必须执行的初始化
     * 应在handleLoadPackage方法内第一个调用
     * @see IXposedHookLoadPackage.handleLoadPackage
     * @see XC_LoadPackage.LoadPackageParam
     */
    fun initHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        setEzClassLoader(lpparam.classLoader)
        setHostPackageName(lpparam.packageName)
    }

    /**
     * 初始化Zygote 以便使用模块路径 和 模块资源
     * @see IXposedHookZygoteInit.initZygote
     */
    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
        moduleRes = XModuleResources.createInstance(modulePath, null)
    }

    /**
     * 设置本库使用的类加载器
     *
     * 注意：通常情况下 建议使用框架提供的类加载器进行操作
     *
     * 但某些app会修改自身的类加载器 遇到这种情况请自行设置运行时的类加载器
     * @param classLoader 类加载器
     */
    fun setEzClassLoader(classLoader: ClassLoader) {
        ezXClassLoader = classLoader
    }

    /**
     * 设置宿主包名
     */
    fun setHostPackageName(packageName: String) {
        hostPackageName = packageName
    }

    /**
     * 初始化全局ApplicationContext
     * @param context ctx
     * @param addPath 是否往ctx中添加模块资源路径
     */
    fun initAppContext(context: Context, addPath: Boolean = false) {
        appContext = context
        if (addPath) addModuleAssetPath(appContext)
    }

    /**
     * 设置打印日志的标签
     */
    fun setLogTag(tag: String) {
        LOG_TAG = tag
    }

    /**
     * 设置Log.toast的Tag
     * 如果不设置会使用日志TAG
     * @see Log.toast
     */
    fun setToastTag(tag: String) {
        TOAST_TAG = tag
    }

    /**
     * 将模块的资源路径添加到Context.resources内 允许直接以R.xx.xxx获取资源
     *
     * 要求:
     *
     * 1.在项目的build.gradle中修改资源id(不与宿主冲突即可) 如下:
     *
     * aaptOptions.additionalParameters '--allow-reserved-package-id', '--package-id', '0x64'
     *
     * 2.执行过EzXHelperInit.initZygote
     *
     * 3.在使用资源前调用
     *
     * eg:在Activity中:
     * init { addModuleAssetPath(this) }
     *
     * @see initZygote
     *
     */
    fun addModuleAssetPath(context: Context) {
        addModuleAssetPath(context.resources)
    }

    fun addModuleAssetPath(resources: Resources) {
        resources.assets.invokeMethod(
            "addAssetPath",
            arrayOf(modulePath),
            arrayOf(String::class.java)
        )
    }
}