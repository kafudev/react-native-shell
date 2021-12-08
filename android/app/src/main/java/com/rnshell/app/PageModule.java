package com.rnshell.app;

import android.os.Bundle;
import android.widget.Toast;

import com.facebook.react.ReactApplication;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面模块
 * <p>
 * 新页面打开，三方服务页面打开
 */
public class PageModule extends ReactContextBaseJavaModule {
  String TAG = "CommonModule";

  private static ReactApplicationContext reactContext;

  private static final String DURATION_SHORT_KEY = "TOAST—SHORT";
  private static final String DURATION_LONG_KEY = "TOAST—LONG";

  public PageModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
  }

  @Override
  public String getName() {
    return "Page";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
    constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
    return constants;
  }

  /**
   * 打开跳转到RN的展示页面
   *
   * @param readableMap
   */
  @ReactMethod
  public void openPage(ReadableMap readableMap, final Promise promise) {
    try {
      if (readableMap == null) {
        Toast.makeText(getCurrentActivity(), "模块参数错误", Toast.LENGTH_SHORT).show();
        promise.reject("RN_OPEN_PAGE_ACTIVITY_ERROR", "readableMap isEmpty");
        return;
      }
      // 获取参数
      Integer style = readableMap.getInt("style");
      Boolean isReload = readableMap.getBoolean("isReload");
      String bundleUrl = readableMap.getString("bundleUrl");
      String appModule = readableMap.getString("appModule");
      String appName = readableMap.getString("appName");
      String appLogo = readableMap.getString("appLogo");
      String appVersion = readableMap.getString("appVersion");
      String appText = readableMap.getString("appText");
      ReadableMap extraData = readableMap.getMap("extraData");
      // 参数默认值
      style = style == null ? 1 : style;
      isReload = isReload == null ? false : isReload;
      bundleUrl = bundleUrl == null || bundleUrl.isEmpty() ? "" : bundleUrl;
      appModule = appModule == null || appModule.isEmpty() ? "" : appModule;
      appName = appName == null || appName.isEmpty() ? "" : appName;
      appLogo = appLogo == null || appLogo.isEmpty() ? "" : appLogo;
      appVersion = appVersion == null || appVersion.isEmpty() ? "" : appVersion;
      appText = appText == null || appText.isEmpty() ? "" : appText;
      extraData = extraData == null ? null : extraData;
      if (appModule == null || appModule.isEmpty()) {
        Toast.makeText(getReactApplicationContext(), "模块名称不能为空", Toast.LENGTH_SHORT).show();
        promise.reject("RN_OPEN_PAGE_ACTIVITY_ERROR", "moduleName isEmpty");
        return;
      }
      if (bundleUrl == null || bundleUrl.isEmpty()) {
        Toast.makeText(getReactApplicationContext(), "模块加载地址不能为空", Toast.LENGTH_SHORT).show();
        promise.reject("RN_OPEN_PAGE_ACTIVITY_ERROR", "bundleUrl isEmpty");
        return;
      }

      // 传递额外参数序列化
      Bundle extraBundle = new Bundle();
      if (extraData != null) {
        extraBundle = Arguments.toBundle(extraData);
      }
      // 启动加载页面
      PageActivity.start(getCurrentActivity(), style, isReload, bundleUrl, appModule, appName, appLogo, appVersion,
        appText, extraBundle);
      promise.resolve(true);
      return;
    } catch (Exception e) {
      Toast.makeText(getReactApplicationContext(), "加载模块异常" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * 重新加载页面
   */
  @ReactMethod
  public void restartPage() {
    PageActivity pageActivity = (PageActivity) getCurrentActivity();
    pageActivity.restartActivity();
  }

  /**
   * 关闭当前页面
   */
  @ReactMethod
  public void finishPage() {
    PageActivity pageActivity = (PageActivity) getCurrentActivity();
    pageActivity.finishActivity();
  }

}
