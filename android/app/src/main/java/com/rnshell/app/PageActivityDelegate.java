package com.rnshell.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import android.widget.Toast;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactApplication;

import com.facebook.react.PackageList;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactDelegate;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.shell.MainReactPackage;

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

  private final @Nullable
  Activity mActivity;
  private final @Nullable
  String mMainComponentName;
  private final @Nullable
  String mBundleFile;

  private @Nullable
  Callback mPermissionsCallback;
  private ReactDelegate mReactDelegate;
  private ReactNativeHost mReactNativeHost;

  public PageActivityDelegate(ReactActivity activity, @Nullable String mainComponentName, @Nullable String bundleFile) {
    super(activity, mainComponentName);
    mActivity = activity;
    mMainComponentName = mainComponentName;
    mBundleFile = bundleFile;
  }

  public String getMainComponentName() {
    if (mActivity instanceof PageActivity) {
      String mName = ((PageActivity) mActivity).getMainComponentName();
      if (mName != null && !mName.isEmpty()) {
        return mName;
      }
    }
    return mMainComponentName;
  }

  public String getJSBundleFile() {
    if (mActivity instanceof PageActivity) {
      String mName = ((PageActivity) mActivity).getJSBundleFile();
      if (mName != null && !mName.isEmpty()) {
        return mName;
      }
    }
    return mBundleFile;
  }

  @Override
  protected ReactRootView createRootView() {
    return new RNGestureHandlerEnabledRootView(mActivity);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    String mainComponentName = getMainComponentName();
    String bundleFile = getJSBundleFile();
    Log.i("PageActivityDelegate", "mMainComponentName " + mainComponentName + " mBundleFile " + bundleFile);
    mReactDelegate = new PageDelegate(
      getPlainActivity(), getReactNativeHost(), mainComponentName, getLaunchOptions());
    if (mainComponentName != null) {
      loadApp(mainComponentName);
    }
  }

  protected void loadApp(String appKey) {
    mReactDelegate.loadApp(appKey);
    getPlainActivity().setContentView(mReactDelegate.getReactRootView());
  }

  public ReactNativeHost getReactNativeHost() {
    if (mReactNativeHost != null) {
      return mReactNativeHost;
    }
    mReactNativeHost = new ReactNativeHost(getPlainActivity().getApplication()) {
      private @Nullable
      ReactInstanceManager mReactInstanceManager;

      @Override
      public ReactInstanceManager getReactInstanceManager() {
        if (mReactInstanceManager == null) {
          mReactInstanceManager = createReactInstanceManager();
        }
        return mReactInstanceManager;
      }

      @Override
      public boolean getUseDeveloperSupport() {
        return false;
      }

      @Override
      protected List<ReactPackage> getPackages() {
        @SuppressWarnings("UnnecessaryLocalVariable")
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
        return new CommonJSIModulePackage(); // <- add
      }

      @Override
      protected @Nullable
      String getJSBundleFile() {
        String bundleFile = ((PageActivity) getPlainActivity()).getJSBundleFile();
        Log.w("PageActivityDelegate", "getReactNativeHost getJSBundleFile " + bundleFile);
        return bundleFile;
      }
    };
    mReactNativeHost = new ReactNativeHostWrapper(getPlainActivity().getApplication(), mReactNativeHost);
    return mReactNativeHost;
  }

  protected void onPause() {
    mReactDelegate.onHostPause();
  }

  protected void onResume() {
    mReactDelegate.onHostResume();

    if (mPermissionsCallback != null) {
      mPermissionsCallback.invoke();
      mPermissionsCallback = null;
    }
  }

  protected void onDestroy() {
    mReactDelegate.onHostDestroy();
  }


}
