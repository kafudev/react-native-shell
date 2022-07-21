package com.rnshell.app;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.view.WindowManager;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactRootView;
import com.facebook.react.ReactInstanceManager;

import expo.modules.ReactActivityDelegateWrapper;
import com.zoontek.rnbootsplash.RNBootSplash;
import com.zoontek.rnbars.RNBars; // <- add this necessary import

import com.qiyukf.unicorn.api.Unicorn;

public class MainActivity extends ReactActivity {

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "rnshell";
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    super.onCreate(savedInstanceState);
    // super.onCreate(null);
    // 启动页全屏，状态栏覆盖启动页
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    RNBootSplash.init(R.drawable.bootsplash, this);
    Unicorn.initSdk();
    Log.i("MainActivity", "onCreate executed!");
  }

  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    // return new MainActivityDelegate(this, getMainComponentName());
    return new ReactActivityDelegateWrapper(this, new MainActivityDelegate(this, getMainComponentName()));
  }

  public static class MainActivityDelegate extends ReactActivityDelegate {
    public MainActivityDelegate(ReactActivity activity, String mainComponentName) {
      super(activity, mainComponentName);
    }

    @Override
    protected void loadApp(String appKey) {
      // RNBars.init(getPlainActivity(), "dark-content"); // <- initialize with initial bars styles (could be light-content)
      super.loadApp(appKey);
    }

    @Override
    protected ReactRootView createRootView() {
      ReactRootView reactRootView = new ReactRootView(getContext());
      // If you opted-in for the New Architecture, we enable the Fabric Renderer.
      reactRootView.setIsFabric(BuildConfig.IS_NEW_ARCHITECTURE_ENABLED);
      return reactRootView;
    }
  }
}
