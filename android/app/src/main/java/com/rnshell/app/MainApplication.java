package com.rnshell.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.arialyy.aria.core.Aria;
import com.rnshell.app.generated.BasePackageList;

import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.soloader.SoLoader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Arrays;

import com.facebook.react.bridge.JSIModulePackage;
import com.swmansion.reanimated.ReanimatedJSIModulePackage;
import com.rnshell.app.jsi.CommonJSIModulePackage;

import org.unimodules.adapters.react.ModuleRegistryAdapter;
import org.unimodules.adapters.react.ReactModuleRegistryProvider;
import org.unimodules.core.interfaces.SingletonModule;

import com.didichuxing.doraemonkit.DoKit;
import com.tencent.bugly.crashreport.CrashReport;
import com.microsoft.codepush.react.CodePush;
import cn.jiguang.plugins.push.JPushModule;
// import com.tencent.rtmp.TXLiveBase;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFOptions;

public class MainApplication extends Application implements ReactApplication {

  private final ReactModuleRegistryProvider mModuleRegistryProvider = new ReactModuleRegistryProvider(new BasePackageList().getPackageList(), null);

  private final ReactNativeHost mReactNativeHost =
      new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
          return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
          @SuppressWarnings("UnnecessaryLocalVariable")
          List<ReactPackage> packages = new PackageList(this).getPackages();
          // Packages that cannot be autolinked yet can be added manually here, for example:
          // packages.add(new MyReactNativePackage());
          packages.add(new CommonPackage());  // 加载通用模块
          // Add unimodules
          List<ReactPackage> unimodules = Arrays.<ReactPackage>asList(
            new ModuleRegistryAdapter(mModuleRegistryProvider)
          );
          packages.addAll(unimodules);
          return packages;
        }

        @Override
        protected String getJSMainModuleName() {
          return "index";
        }

        @Override
        protected JSIModulePackage getJSIModulePackage() {
          // return new ReanimatedJSIModulePackage(); // <- add
          return new CommonJSIModulePackage(); // <- add
        }

        // 2. Override the getJSBundleFile method in order to let
        // the CodePush runtime determine where to get the JS
        // bundle location from on each app start
        @Override
        protected String getJSBundleFile() {
            return CodePush.getJSBundleFile();
        }
      };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.v("MainApplication", "app onCreate");
    SoLoader.init(this, /* native exopackage */ false);
    initializeFlipper(this, getReactNativeHost().getReactInstanceManager());

    // 获取配置
    String jpush_appkey = this.getMetaDataValue("JPUSH_APPKEY", "");
    String dokit_appid = this.getMetaDataValue("DOKIT_APPID", "");
    String bugly_appid = this.getMetaDataValue("BUGLY_APPID", "");
    String qiyu_appkey = this.getMetaDataValue("QIYU_APPKEY", "");
    String txlive_licence_key = this.getMetaDataValue("TXLIVE_LICENCE_KEY", "");
    String txlive_licence_url = this.getMetaDataValue("TXLIVE_LICENCE_URL", "");

    // dokit开发工具
    if(BuildConfig.DEBUG){
      new DoKit.Builder(this).productId(dokit_appid).build();
    }

    // bugly异常上报
    CrashReport.initCrashReport(this, bugly_appid, BuildConfig.DEBUG);

    // 极光推送
    if(!jpush_appkey.isEmpty()){
      // 极光推送，注册
      JPushModule.registerActivityLifecycle(this);
    }

    // 七鱼客服
    if(!qiyu_appkey.isEmpty()){
      // appKey 可以在七鱼管理系统->设置->App 接入 页面找到
      Unicorn.init(this, qiyu_appkey, new YSFOptions(), null);
    }

    // 腾讯直播
    if(!txlive_licence_key.isEmpty()){
      // TXLiveBase.setConsoleEnabled(true);
      // TXLiveBase.getInstance().setLicence(this, txlive_licence_url, txlive_licence_key);
    }

    // 下载器初始化
    Aria.init(this);
  }

  /**
   * Loads Flipper in React Native templates. Call this in the onCreate method with something like
   * initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
   *
   * @param context
   * @param reactInstanceManager
   */
  private static void initializeFlipper(
      Context context, ReactInstanceManager reactInstanceManager) {
    if (BuildConfig.DEBUG) {
      try {
        /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
        Class<?> aClass = Class.forName("com.rnshell.ReactNativeFlipper");
        aClass
            .getMethod("initializeFlipper", Context.class, ReactInstanceManager.class)
            .invoke(null, context, reactInstanceManager);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 获取MetaData信息
   *
   * @param name
   * @param def
   * @return
   */
  public String getMetaDataValue(String name, String def) {
    try {
      ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
      String value = info.metaData.get(name)+"";  // 必须字符串，防止数字转义
      Log.v("MainApplication", name + ":" + value);
      return (value == null) ? def : value;
    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException("Could not read the name in the manifest file.", e);
    }
  }
}
