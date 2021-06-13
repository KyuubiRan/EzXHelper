package com.github.kyuubiran.ezxhelper.utils.parasitics

import android.content.Context

class FixedClassLoader(
    private val mModuleClassLoader: ClassLoader,
    private val mHostClassLoader: ClassLoader
) : ClassLoader(mBootstrap) {
    companion object {
        private val mBootstrap: ClassLoader = Context::class.java.classLoader!!
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        try {
            return mBootstrap.loadClass(name)
        } catch (ignored: ClassNotFoundException) {
        }
        try {
            if ("androidx.lifecycle.ReportFragment" == name) {
                return mHostClassLoader.loadClass(name)
            }
        } catch (ignored: ClassNotFoundException) {
        }
        return try {
            mModuleClassLoader.loadClass(name)
        } catch (e: Exception) {
            mHostClassLoader.loadClass(name)
        }
    }
}