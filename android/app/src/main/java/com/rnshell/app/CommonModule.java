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
 * <p>
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
   */
  @ReactMethod
  public void showDevMenu() {
    ReactApplication reactApplication = (ReactApplication) getCurrentActivity().getApplication();
    if (reactApplication.getReactNativeHost().hasInstance()
      && reactApplication.getReactNativeHost().getUseDeveloperSupport()) {
      reactApplication.getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
    }
  }

  /**
   * 重新加载js
   */
  @ReactMethod
  public void reloadJs() {
    ReactApplication reactApplication = (ReactApplication) getCurrentActivity().getApplication();
    if (reactApplication.getReactNativeHost().hasInstance()
      && reactApplication.getReactNativeHost().getUseDeveloperSupport()) {
      reactApplication.getReactNativeHost().getReactInstanceManager().getDevSupportManager().handleReloadJS();
    }
  }

  /**
   * 显示FPS
   */
  @ReactMethod
  public void showFps(Boolean isShow) {
    if (isShow == null) {
      isShow = true;
    }
    ReactApplication reactApplication = (ReactApplication) getCurrentActivity().getApplication();
    if (reactApplication.getReactNativeHost().hasInstance()) {
      reactApplication.getReactNativeHost().getReactInstanceManager().getDevSupportManager().setFpsDebugEnabled(isShow);
    }
  }

}
