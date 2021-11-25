package com.rnshell.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.react.ReactApplication;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Arguments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 通用模块
 *
 * 常量导出，新页面打开，三方服务页面打开
 */
public class CommonModule extends ReactContextBaseJavaModule {
  String TAG = "CommonModule";

  private static ReactApplicationContext reactContext;

  private static final String DURATION_SHORT_KEY = "TOAST—SHORT";
  private static final String DURATION_LONG_KEY = "TOAST—LONG";

  public CommonModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
  }

  @Override
  public String getName() {
    return "Common";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
    constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
    return constants;
  }

  /**
   * 显示toast提示
   *
   * @param message
   * @param duration
   */
  @ReactMethod
  public void toast(String message, int duration) {
    Toast.makeText(getReactApplicationContext(), message, duration).show();
  }

  /**
   * 显示开发工具目录
   *
   */
  @ReactMethod
  public void showDevMenu() {
    ReactApplication reactApplication = (ReactApplication) getCurrentActivity().getApplication();
    if (reactApplication.getReactNativeHost().hasInstance() && reactApplication.getReactNativeHost().getUseDeveloperSupport()) {
      reactApplication.getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
    }
  }

  /**
   * 重新加载js
   *
   */
  @ReactMethod
  public void reloadJs() {
    ReactApplication reactApplication = (ReactApplication) getCurrentActivity().getApplication();
    if (reactApplication.getReactNativeHost().hasInstance() && reactApplication.getReactNativeHost().getUseDeveloperSupport()) {
      reactApplication.getReactNativeHost().getReactInstanceManager().getDevSupportManager().handleReloadJS();
    }
  }

  /**
   * 显示FPS
   *
   */
  @ReactMethod
  public void showFps(Boolean isShow) {
    if(isShow == null) {
      isShow = true;
    }
    ReactApplication reactApplication = (ReactApplication) getCurrentActivity().getApplication();
    if (reactApplication.getReactNativeHost().hasInstance() && reactApplication.getReactNativeHost().getUseDeveloperSupport()) {
      reactApplication.getReactNativeHost().getReactInstanceManager().getDevSupportManager().setFpsDebugEnabled(isShow);
    }
  }

  // /**
  //  * 跳转加载bundle打开页面
  //  *
  //  * 判断是否已下载，未下载则先下载再加载
  //  *
  //  * @param bundleName
  //  * @param promise
  //  */
  // @ReactMethod
  // public void openPageActivity(String bundleName, String bundleUrl, final Promise promise) {
  //   // 检查是否下载过，如果已经下载过则直接打开，暂不考虑各种版本问题
  //   String f = getReactApplicationContext().getFilesDir().getAbsolutePath() + "/" + bundleName + "/" + bundleName
  //       + ".bundle";
  //   File file = new File((f));
  //   if (file.exists()) {
  //     openActivity(bundleName);
  //   } else {
  //     downloadBundle(bundleName, bundleUrl);
  //   }
  // }

  // /**
  //  * 打开跳转到RN的展示页面
  //  *
  //  * @param bundleName
  //  */
  // private void openActivity(String bundleName) {
  //   try {
  //     PageActivity.start(getCurrentActivity(), bundleName);
  //     // PageActivity.bundleName = bundleName;
  //     // Activity activity = getCurrentActivity();
  //     // Intent intent = new Intent(activity, PageActivity.class);
  //     // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  //     // activity.startActivity(intent);
  //   } catch (Exception e) {
  //     Toast.makeText(getReactApplicationContext(), "无法加载模块页面" + e.getMessage(), Toast.LENGTH_SHORT).show();
  //   }
  // }

  // /**
  //  * 下载对应的bundle
  //  *
  //  * @param bundleName
  //  */
  // private void downloadBundle(final String bundleName, final String bundleUrl) {
  //   Log.v("PageActivity", bundleName + " " + bundleUrl);
  //   String filepath = getReactApplicationContext().getFilesDir().getAbsolutePath() + "/" + bundleName;
  //   String filename = filepath + "/" + bundleName + ".bundle";
  //   File destDir = new File(filepath);
  //   if (!destDir.exists()) {
  //     destDir.mkdirs();
  //   }
  //   long mTaskId = Aria.download(this).load(bundleUrl).setFilePath(filename).ignoreFilePathOccupy()
  //       .ignoreCheckPermissions().setExtendField(bundleName).resetState().create();
  // }

  // @Download.onWait
  // void onWait(DownloadTask task) {
  //   Log.d(TAG + " file DownloadTask", "wait ==> " + task.getDownloadEntity().getFileName());
  // }

  // @Download.onPre
  // protected void onPre(DownloadTask task) {
  //   Log.d(TAG + " file DownloadTask", "onPre");
  // }

  // @Download.onTaskStart
  // void taskStart(DownloadTask task) {
  //   Log.d(TAG + " file DownloadTask", "onStart");
  //   Toast.makeText(getReactApplicationContext(), "模块加载开始", Toast.LENGTH_SHORT).show();
  // }

  // @Download.onTaskRunning
  // protected void running(DownloadTask task) {
  //   Log.d(TAG + " file DownloadTask", "running");
  //   // if (task.getKey().eques(url)) {
  //   // // 可以通过url判断是否是指定任务的回调
  //   // }
  //   int p = task.getPercent(); // 任务进度百分比
  //   String speed = task.getConvertSpeed(); // 转换单位后的下载速度，单位转换需要在配置文件中打开
  //   long speed1 = task.getSpeed(); // 原始byte长度速度
  //   Toast.makeText(getReactApplicationContext(), "模块加载进度" + p + "%", Toast.LENGTH_SHORT).show();
  // }

  // @Download.onTaskResume
  // void taskResume(DownloadTask task) {
  //   Log.d(TAG + " file DownloadTask", "resume");
  // }

  // @Download.onTaskStop
  // void taskStop(DownloadTask task) {
  //   Log.d(TAG + " file DownloadTask", "stop");
  // }

  // @Download.onTaskCancel
  // void taskCancel(DownloadTask task) {
  //   Log.d(TAG + " file DownloadTask", "cancel");
  // }

  // @Download.onTaskFail
  // void taskFail(DownloadTask task) {
  //   Log.d(TAG + " file DownloadTask", "fail");
  // }

  // @Download.onTaskComplete
  // void taskComplete(DownloadTask task) {
  //   String bundleName = task.getExtendField();
  //   Log.d(TAG + " file DownloadTask", "ExtendField ==> " + task.getExtendField());
  //   Log.d(TAG + " file DownloadTask", "path ==> " + task.getFilePath());
  //   Log.d(TAG + " file DownloadTask", "md5Code ==> " + CommonUtil.getFileMD5(new File(task.getFilePath())));
  //   Toast.makeText(getReactApplicationContext(), "模块加载完成", Toast.LENGTH_SHORT).show();
  //   openActivity(bundleName);
  // }
}
