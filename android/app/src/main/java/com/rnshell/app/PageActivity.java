package com.rnshell.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.react.PackageList;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactDelegate;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.JSIModulePackage;
import com.rnshell.app.jsi.CommonJSIModulePackage;
import com.swmansion.gesturehandler.react.RNGestureHandlerEnabledRootView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import expo.modules.ReactActivityDelegateWrapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PageActivity extends ReactActivity {
  private final PageActivityDelegate mDelegate;
  private ReactRootView mReactRootView = null;
  private ReactDelegate mReactDelegate = null;
  private ReactInstanceManager mReactInstanceManager = null;
  public static String mBundleFile = "";
  public static String mMainComponentName = "";


  // 启动
  public static void start(Activity activity, String url, String moduleName, String version, String appName) {
    // 下载文件
    String filepath = activity.getApplication().getFilesDir().getAbsolutePath() + "/" + moduleName;
    String bundleFile = filepath + "/" + moduleName + "_" + version + ".bundle";
    File destDir = new File(filepath);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }
    Log.w("PageActivity", "start " + moduleName + " " + version + " " + appName);
    File file = new File(bundleFile);
    if (!file.exists()) {
      downloadFile(activity, url, bundleFile);
    } else {
      startActivity(activity, bundleFile, moduleName, version, appName);
    }
  }

  // 启动activity
  public static void startActivity(Activity activity, String bundleFile, String moduleName, String version,
                                   String appName) {
    mMainComponentName = moduleName;
    mBundleFile = bundleFile;
    Intent intent = new Intent(activity, PageActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra("bundleFile", bundleFile);
    intent.putExtra("moduleName", moduleName);
    intent.putExtra("version", moduleName);
    intent.putExtra("appName", appName);
    activity.startActivity(intent);
  }

  @Nullable
  public String getMainComponentName() {
    if(!mMainComponentName.isEmpty()){
      return mMainComponentName;
    }
    if(getIntent()==null){
      return null;
    }
    String mName = getIntent().getStringExtra("moduleName");
    if(mName == null || mName.isEmpty()){
      finish();
      return "";
    }
    return mName;
  }

  @Nullable
  public String getJSBundleFile() {
    if(!mBundleFile.isEmpty()){
      return mBundleFile;
    }
    if(getIntent()==null){
      return null;
    }
    String mName = getIntent().getStringExtra("bundleFile");
    if(mName == null || mName.isEmpty()){
      finish();
      return "";
    }
    return mName;
  }

  public PageActivity() {
    mDelegate = (PageActivityDelegate) createReactActivityDelegate();
  }

  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new PageActivityDelegate(this, getMainComponentName(), getJSBundleFile()) {
      @Override
      protected ReactRootView createRootView() {
        return new RNGestureHandlerEnabledRootView(PageActivity.this);
      }
    };
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.w("PageActivity", "onCreate mMainComponentName " + getMainComponentName() + " mBundleFile " + getJSBundleFile());
    super.onCreate(savedInstanceState);
    mDelegate.onCreate(savedInstanceState);
  }


//  @Override
//  protected ReactActivityDelegate createReactActivityDelegate() {
//    Log.w("PageActivity", "getReactNativeHost " + mBundleFile + " getMainComponentName " + getMainComponentName());
//    return
//      new ReactActivityDelegate(this, getMainComponentName()) {
//        @Override
//        protected ReactRootView createRootView() {
//          return new RNGestureHandlerEnabledRootView(PageActivity.this);
//        }
//
//        @Override
//        protected ReactNativeHost getReactNativeHost() {
//          return new ReactNativeHost((MainApplication) getApplication()) {
//            @Override
//            public boolean getUseDeveloperSupport() {
//              return BuildConfig.DEBUG;
//            }
//
//            @Override
//            protected List<ReactPackage> getPackages() {
//              @SuppressWarnings("UnnecessaryLocalVariable")
//              List<ReactPackage> packages = new PackageList(this).getPackages();
//              packages.add(new CommonPackage()); // 加载通用模块
////              packages = ((ReactApplication)getApplication()).getReactNativeHost().getReactInstanceManager().getPackages();
//              return packages;
//            }
//
//            @Override
//            protected String getJSMainModuleName() {
//              return "index";
//            }
//
//            @Override
//            protected JSIModulePackage getJSIModulePackage() {
//              return new CommonJSIModulePackage(); // <- add
//            }
//
//            @Override
//            protected String getJSBundleFile() {
//              Log.w("PageActivity", "getJSBundleFile " + mBundleFile);
//              return mBundleFile;
//            }
//          };
//        }
//      };
//  }

  protected void onCreate1(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 启动页全屏，状态栏覆盖启动页
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    Intent intent = getIntent();
    String bundleFile = intent.getStringExtra("bundleFile");
    String moduleName = intent.getStringExtra("moduleName");
    String appName = intent.getStringExtra("appName");

    mReactRootView = new ReactRootView(this);
    String mainComponentName = moduleName;
    mReactDelegate = new ReactDelegate(this, new ReactNativeHost((MainApplication) getApplication()) {
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
      protected JSIModulePackage getJSIModulePackage() {
        return new CommonJSIModulePackage(); // <- add
      }

      @Override
      protected String getJSBundleFile() {
        Log.w("PageActivity", "getJSBundleFile " + bundleFile);
        return bundleFile;
      }
    }, mainComponentName, null) {
      protected ReactRootView createRootView() {
        return mReactRootView;
      }
    };
    if (mainComponentName != null) {
      // 启动页面
      mReactDelegate.loadApp(mainComponentName);
    }

    // List<ReactPackage> packages = new
    // PackageList(getApplication()).getPackages();
    // mReactInstanceManager = ReactInstanceManager.builder()
    // .setApplication(getApplication())
    // .setCurrentActivity(this)
    // // .setBundleAssetName("index.android.bundle")
    // // .setJSMainModulePath("index")
    // .setJSBundleFile(bundleFile)
    // .addPackages(packages)
    // .setUseDeveloperSupport(BuildConfig.DEBUG)
    // .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
    // .setDefaultHardwareBackBtnHandler(this)
    // .build();
    // mReactRootView.startReactApplication(mReactInstanceManager, moduleName,
    // // null);
    setContentView(mReactRootView);
    Log.i("PageActivity", "onCreate executed!");
    Toast.makeText(this, "模块页面创建完成", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mDelegate.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mDelegate.onResume();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mDelegate.onDestroy();
  }

  @Override
  public void onBackPressed() {
    Toast.makeText(this, "onBackPressed", Toast.LENGTH_SHORT).show();
    if (!mDelegate.onBackPressed()) {
      super.onBackPressed();
    }
  }

  @Override
  public void invokeDefaultOnBackPressed() {
    Toast.makeText(this, "invokeDefaultOnBackPressed", Toast.LENGTH_SHORT).show();
    super.onBackPressed();
  }

  // 下载文件
  public static void downloadFile(Activity activity, final String url, final String bundleFile) {
    // final String url = "http://c.qijingonline.com/test.mkv";
    final long startTime = System.currentTimeMillis();
    Log.i("DOWNLOAD", "startTime=" + startTime);
    OkHttpClient okHttpClient = new OkHttpClient();

    Request request = new Request.Builder().url(url).build();
    okHttpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        // 下载失败
        e.printStackTrace();
        Log.i("DOWNLOAD", "download failed");
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        // 储存下载文件的目录
        String savePath = Environment.getDownloadCacheDirectory().getAbsolutePath();
        savePath = activity.getApplication().getFilesDir().getAbsolutePath();
        try {
          is = response.body().byteStream();
          long total = response.body().contentLength();
          File file = new File(bundleFile);
          Log.i("DOWNLOAD", "download file " + file.getPath());
          fos = new FileOutputStream(file);
          long sum = 0;
          while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);
            sum += len;
            int progress = (int) (sum * 1.0f / total * 100);
            Log.i("DOWNLOAD", "download progress " + progress);
            // 下载中
            // listener.onDownloading(progress);
          }
          fos.flush();
          // 下载完成
          // listener.onDownloadSuccess();
          Log.i("DOWNLOAD", "download file " + file.getPath());
          Log.i("DOWNLOAD", "download success");
          Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
          e.printStackTrace();
          // listener.onDownloadFailed();
          Log.i("DOWNLOAD", "download failed");
        } finally {
          try {
            if (is != null)
              is.close();
          } catch (IOException e) {
          }
          try {
            if (fos != null)
              fos.close();
          } catch (IOException e) {
          }
        }
      }
    });
  }
}
