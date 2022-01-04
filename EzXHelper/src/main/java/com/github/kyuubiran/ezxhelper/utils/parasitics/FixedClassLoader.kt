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
        runCatching {
            return mBootstrap.loadClass(name)
        }

        runCatching {
            if ("androidx.lifecycle.ReportFragment" == name) {
                return mHostClassLoader.loadClass(name)
            }
        }

        return try {
            mModuleClassLoader.loadClass(name)
        } catch (e: Exception) {
            mHostClassLoader.loadClass(name)
        }
    }
}