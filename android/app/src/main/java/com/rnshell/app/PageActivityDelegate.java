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
import com.swmansion.reanimated.ReanimatedJSIModulePackage;
import com.rnshell.app.jsi.CommonJSIModulePackage;
import com.rnshell.app.MainApplication;

/**
 * PageActivityDelegate
 */
public class PageActivityDelegate extends ReactActivityDelegate {

  private final @Nullable Activity mActivity;
  private final @Nullable String mMainComponentName;
  private final @Nullable String mBundleFile;

  private @Nullable
  PermissionListener mPermissionListener;
  private @Nullable
  Callback mPermissionsCallback;
  private ReactDelegate mReactDelegate;
  private ReactNativeHost  mReactNativeHost;

  public PageActivityDelegate(ReactActivity activity, @Nullable String mainComponentName, @Nullable String bundleFile) {
    super(activity, mainComponentName);
    mActivity = activity;
    mMainComponentName = mainComponentName;
    mBundleFile = bundleFile;
  }

  public String getMainComponentName() {
    if(mActivity instanceof PageActivity) {
      String mName = ((PageActivity) mActivity).getMainComponentName();
      if(mName != null && !mName.isEmpty()){
        return mName;
      }
    }
    return mMainComponentName;
  }

  public String getJSBundleFile() {
    if(mActivity instanceof PageActivity) {
      String mName = ((PageActivity) mActivity).getJSBundleFile();
      if(mName != null && !mName.isEmpty()){
        return mName;
      }
    }
    return mBundleFile;
  }

  protected void onCreate(Bundle savedInstanceState) {
    String mainComponentName = getMainComponentName();
    String bundleFile = getJSBundleFile();
    Log.w("PageActivityDelegate", "mMainComponentName " + mainComponentName + " mBundleFile " + bundleFile);
    mReactDelegate =
        new ReactDelegate(
            getPlainActivity(), getReactNativeHost(), mainComponentName, getLaunchOptions()) {
          @Override
          protected ReactRootView createRootView() {
            return PageActivityDelegate.this.createRootView();
          }

          private ReactNativeHost getReactNativeHost() {
            return getReactNativeHost();
          }

          @Override
          public ReactInstanceManager getReactInstanceManager() {
            return getReactNativeHost().getReactInstanceManager();
          }
        };
    if (mainComponentName != null) {
      loadApp(mainComponentName);
    }
  }

  protected void loadApp(String appKey) {
    mReactDelegate.loadApp(appKey);
    getPlainActivity().setContentView(mReactDelegate.getReactRootView());
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

  protected ReactNativeHost getReactNativeHost() {
    if(mReactNativeHost != null) {
      return mReactNativeHost;
    }
    mReactNativeHost = new ReactNativeHost(getPlainActivity().getApplication()) {
      @Override
      public boolean getUseDeveloperSupport() {
        return BuildConfig.DEBUG;
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
      protected @Nullable JSIModulePackage getJSIModulePackage() {
        return new CommonJSIModulePackage(); // <- add
      }

      @Override
      protected @Nullable String getJSBundleFile() {
        String bundleFile = ((PageActivity)getPlainActivity()).getJSBundleFile();
        Log.w("PageActivityDelegate", "getReactNativeHost getJSBundleFile " + bundleFile);
        return bundleFile;
      }

    };
    return mReactNativeHost;
  }
}
