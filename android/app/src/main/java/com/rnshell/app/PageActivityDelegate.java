package com.rnshell.app;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.Nullable;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactApplication;
import com.rnshell.app.generated.BasePackageList;

import com.facebook.react.PackageList;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;

import java.util.Arrays;
import java.util.List;

import com.facebook.react.bridge.JSIModulePackage;
import com.swmansion.reanimated.ReanimatedJSIModulePackage;
import com.rnshell.app.jsi.CommonJSIModulePackage;

import org.unimodules.adapters.react.ModuleRegistryAdapter;
import org.unimodules.adapters.react.ReactModuleRegistryProvider;
import org.unimodules.core.interfaces.SingletonModule;

/**
 * PageActivityDelegate
 */
public class PageActivityDelegate extends ReactActivityDelegate {

  private final @Nullable Activity mActivity;
  private final @Nullable String mMainComponentName;

//  private final ReactModuleRegistryProvider mModuleRegistryProvider = new ReactModuleRegistryProvider(new BasePackageList().getPackageList(), null);

  private ReactActivity activity;
  private String bundleName;

  public PageActivityDelegate(Activity activity, @Nullable String bundleName) {
    super(activity, bundleName);
    activity = activity;
    bundleName = bundleName;
    mActivity = activity;
    mMainComponentName = bundleName;
    Log.i("PageActivityDelegate", "bundleName: " + bundleName);
  }

  @Override
  protected ReactNativeHost getReactNativeHost() {

    ReactNativeHost mReactNativeHost = new ReactNativeHost(getPlainActivity().getApplication()) {
      @Override
      public boolean getUseDeveloperSupport() {
        return BuildConfig.DEBUG;
      }

      // 注册原生模块
      @Override
      protected List<ReactPackage> getPackages() {
        // @SuppressWarnings("UnnecessaryLocalVariable")
        List<ReactPackage> packages = new PackageList(this).getPackages();
        // Packages that cannot be autolinked yet can be added manually here, for
        // example:
        packages.add(new MainReactPackage());
        // packages.add(new CommonPackage()); // 加载通用模块
        // // Add unimodules
        // List<ReactPackage> unimodules = Arrays.<ReactPackage>asList(new ModuleRegistryAdapter(mModuleRegistryProvider));
        // packages.addAll(unimodules);
        return packages;
      }

      // @Override
      // protected JSIModulePackage getJSIModulePackage() {
      //   // return new ReanimatedJSIModulePackage(); // <- add
      //   return new CommonJSIModulePackage(); // <- add
      // }

      @Nullable
      @Override
      protected String getJSBundleFile() {
        // 读取已经解压的bundle文件
        String file = getPlainActivity().getApplicationContext().getFilesDir().getAbsolutePath() + "/" + bundleName + "/" + bundleName + ".bundle";
        Log.i("PageActivityDelegate", "getJSBundleFile:"+file);
        return file;
      }

      @Nullable
      @Override
      protected String getBundleAssetName() {
        return bundleName + ".bundle";
      }

      @Override
      protected String getJSMainModuleName() {
        return "index.android";
      }
    };
    return mReactNativeHost;
  }
}
