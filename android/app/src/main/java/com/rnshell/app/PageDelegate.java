package com.rnshell.app;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.facebook.react.ReactDelegate;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;

/**
 * PageDelegate
 */
public class PageDelegate extends ReactDelegate {

  private final Activity mActivity;

  private ReactNativeHost mReactNativeHost;

  public PageDelegate(Activity activity, ReactNativeHost reactNativeHost, @Nullable String appKey, @Nullable Bundle launchOptions) {
    super(activity, reactNativeHost, appKey, launchOptions);
    this.mActivity = activity;
    this.mReactNativeHost = reactNativeHost;
  }

  private ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public ReactInstanceManager getReactInstanceManager() {
    return getReactNativeHost().getReactInstanceManager();
  }


}
