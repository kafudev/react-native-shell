package com.rnshell.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;


import com.facebook.react.ReactActivity;

import com.facebook.react.PackageList;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;

import java.util.Arrays;
import java.util.List;

import com.facebook.react.bridge.JSIModulePackage;
import com.swmansion.gesturehandler.react.RNGestureHandlerEnabledRootView;
import com.swmansion.reanimated.ReanimatedJSIModulePackage;
import com.rnshell.app.jsi.CommonJSIModulePackage;
import com.rnshell.app.MainApplication;

import expo.modules.ReactNativeHostWrapper;

/**
 * PageActivityDelegate
 */
public class PageActivityDelegate extends ReactActivityDelegate {

  public final @Nullable
  Activity mActivity;
  public String mMainComponentName;
  public String mJSBundleFile;
  public Bundle mSavedInstanceState = new Bundle();

  private ReactNativeHost mReactNativeHost;

  public PageActivityDelegate(ReactActivity activity, @Nullable String mainComponentName, @Nullable String JSBundleFile) {
    super(activity, mainComponentName);
    mActivity = activity;
    mMainComponentName = mainComponentName;
    mJSBundleFile = JSBundleFile;
  }

  @Override
  public String getMainComponentName() {
    if (mActivity instanceof PageActivity) {
      String mName = ((PageActivity) mActivity).getMainComponentName();
      if (mName != null && !mName.isEmpty()) {
        mMainComponentName = mName;
        return mName;
      }
    }
    return mMainComponentName;
  }

  public String getJSBundleFile() {
    if (mActivity instanceof PageActivity) {
      String mName = ((PageActivity) mActivity).getJSBundleFile();
      if (mName != null && !mName.isEmpty()) {
        mJSBundleFile = mName;
        return mName;
      }
    }
    return mJSBundleFile;
  }

  public Integer getStyle() {
    if (mActivity instanceof PageActivity) {
      int mName = ((PageActivity) mActivity).getStyle();
      return mName;
    }
    return 1;
  }

  @Override
  protected Bundle getLaunchOptions() {
    if (mActivity instanceof PageActivity) {
      Bundle mBundle = ((PageActivity) mActivity).getExtraData();
      return mBundle;
    }
    return null;
  }

  @Override
  protected ReactRootView createRootView() {
    Log.w("PageActivityDelegate", "createRootView");
    return new RNGestureHandlerEnabledRootView(mActivity);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    mSavedInstanceState = savedInstanceState;
    Log.i("PageActivityDelegate", "mMainComponentName " + getMainComponentName() + " mBundleFile " + getJSBundleFile() + " mStyle " + getStyle());
    super.onCreate(savedInstanceState);
    Log.w("PageActivityDelegate", "onCreate");
  }

//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    String mainComponentName = getMainComponentName();
//    String bundleFile = getJSBundleFile();
//    Log.i("PageActivityDelegate", "mMainComponentName " + mainComponentName + " mBundleFile " + bundleFile);
//    this.mReactDelegate = new ReactDelegate(
//      getPlainActivity(), getReactNativeHost(), mainComponentName, getLaunchOptions());
//    if (mainComponentName != null) {
//      loadApp(mainComponentName);
//    }
//  }

  @Override
  protected void loadApp(String appKey) {
    super.loadApp(appKey);
  }

  @Override
  public ReactNativeHost getReactNativeHost() {
    Log.w("PageActivityDelegate", "ReactNativeHost getReactNativeHost");
    if (mReactNativeHost != null) {
      return mReactNativeHost;
    }

    // 判断加载方式
    if (getStyle() == 2) {
      // 注册方式加载
      mReactNativeHost = super.getReactNativeHost();
      return mReactNativeHost;
    }

    mReactNativeHost = new ReactNativeHost(getPlainActivity().getApplication()) {

      @Override
      public boolean getUseDeveloperSupport() {
        return false;
      }

      @Override
      protected List<ReactPackage> getPackages() {
        List<ReactPackage> packages = new PackageList(this).getPackages();
        packages.add(new CommonPackage()); // 加载通用模块
        return packages;
      }

      @Override
      protected String getJSMainModuleName() {
        return "index";
      }

      @Override
      protected @Nullable
      JSIModulePackage getJSIModulePackage() {
        return new CommonJSIModulePackage();
      }

      @Override
      protected @Nullable
      String getJSBundleFile() {
        String bundleFile = ((PageActivity) getPlainActivity()).getJSBundleFile();
        Log.w("ReactNativeHost", "getReactNativeHost getJSBundleFile " + bundleFile);
        return bundleFile;
      }
    };
    mReactNativeHost = new ReactNativeHostWrapper(getPlainActivity().getApplication(), mReactNativeHost);
    return mReactNativeHost;
  }


  protected void onDestroy() {
    super.onDestroy();
    mReactNativeHost.clear();
    mReactNativeHost = null;
  }

}
