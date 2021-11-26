package com.rnshell.app;

import android.app.Activity
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactInstanceManagerBuilder
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage

object ReactInstanceManagerHelper {
  private val mbMaps = mutableMapOf<Int, ReactInstanceManager>()


  fun getManager(
    activity: ContainerActivity,
    cb: (builder: ReactInstanceManagerBuilder) -> Unit = {}
  ): ReactInstanceManager? {

    val keyCode = activity.hashCode()
    var reactInstanceManager = mbMaps[keyCode]
    if (reactInstanceManager == null) {
      val nativeModuleCallExceptionHandler = ReactInstanceManager.builder()
        .addPackage(MainReactPackage())
        .setApplication(activity.application)
        .setCurrentActivity(activity)
        .setInitialLifecycleState(LifecycleState.RESUMED)
        .setNativeModuleCallExceptionHandler { e: Exception ->
          e.printStackTrace()
        }
      cb(nativeModuleCallExceptionHandler)
      val ct = nativeModuleCallExceptionHandler.build()
      reactInstanceManager = ct
      mbMaps[keyCode] = ct
    }
    return reactInstanceManager
  }

  fun clear(activity: Activity) {
    mbMaps.remove(activity.hashCode())
  }
}
