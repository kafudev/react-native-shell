package com.rnshell.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.react.PackageList;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactDelegate;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.JSIModulePackage;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
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
  public PageActivityDelegate mDelegate = createReactActivityDelegate();
  public static String mMainComponentName;
  public static String mJSBundleFile;
  public static Integer mStyle;

  public PageStartView sView = null;

  // 启动
  public static void start(Activity activity, int style, Boolean isReload, String bundleUrl, String appModule,
                           String appName,
                           String appLogo,
                           String appVersion,
                           String appText,
                           Bundle extraData) {
    // 下载文件
    String filepath = activity.getApplication().getFilesDir().getAbsolutePath() + "/" + appModule;
    String bundleFile = filepath + "/" + appModule + "_" + appVersion + ".bundle";
    File destDir = new File(filepath);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }
    Log.w("PageActivity", "start " + appModule + " " + appVersion + " " + appName + " " + appLogo);
    File file = new File(bundleFile);
    if (!file.exists() || isReload) {
      downloadFile(activity, bundleUrl, bundleFile, new OnDownloadListener() {
        @Override
        public void onDownloadSuccess() {
          Log.w("OnDownloadListener", "download success " + bundleFile);
          if (Looper.myLooper() == null) {
            Looper.prepare();
          }
//          Toast.makeText(activity.getApplicationContext(), (!appName.isEmpty() ? appName : appModule) + "模块加载成功", Toast.LENGTH_SHORT).show();
          startActivity(activity, style, isReload, bundleUrl, bundleFile, appModule, appName, appLogo, appVersion,
            appText,
            extraData);
          Looper.loop();
        }

        @Override
        public void onDownloading(int progress) {
          Log.w("OnDownloadListener", "download loading " + progress);
        }

        @Override
        public void onDownloadFailed(Exception e) {
          Log.w("OnDownloadListener", "download failed " + e.getMessage());
          if (Looper.myLooper() == null) {
            Looper.prepare();
          }
          Toast.makeText(activity.getApplicationContext(),
            (!appName.isEmpty() ? appName : appModule) + "模块加载失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
          Looper.loop();
        }
      });
    } else {
      startActivity(activity, style, isReload, bundleUrl, bundleFile, appModule, appName, appLogo, appVersion,
        appText,
        extraData);
    }
  }

  // 启动activity
  public static void startActivity(Activity activity, int style, Boolean isReload, String bundleUrl, String bundleFile,
                                   String appModule,
                                   String appName, String appLogo,
                                   String appVersion, String appText, Bundle extraData) {
    mMainComponentName = appModule;
    mJSBundleFile = bundleFile;
    mStyle = style;

    Intent intent = new Intent(activity, PageActivity.class);
    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra("style", style);
    intent.putExtra("isReload", isReload);
    intent.putExtra("bundleUrl", bundleUrl);
    intent.putExtra("bundleFile", bundleFile);
    intent.putExtra("appModule", appModule);
    intent.putExtra("appName", appName);
    intent.putExtra("appLogo", appLogo);
    intent.putExtra("appVersion", appVersion);
    intent.putExtra("appText", appText);
    intent.putExtra("extraData", extraData);
    activity.startActivity(intent);
  }

  @Nullable
  public String getMainComponentName() {
    if (getIntent() == null) {
      if (mMainComponentName != null) {
        return mMainComponentName;
      }
      return null;
    }
    String mName = getIntent().getStringExtra("appModule");
    if (mName == null || mName.isEmpty()) {
      finish();
      return "";
    }
    return mName;
  }

  @Nullable
  public String getJSBundleFile() {
    if (getIntent() == null) {
      if (mJSBundleFile != null) {
        return mJSBundleFile;
      }
      return null;
    }
    String mName = getIntent().getStringExtra("bundleFile");
    if (mName == null || mName.isEmpty()) {
      finish();
      return "";
    }
    return mName;
  }

  @Nullable
  public Integer getStyle() {
    if (getIntent() == null) {
      if (mStyle != null) {
        return mStyle;
      }
      return 1;
    }
    Integer mName = getIntent().getIntExtra("style", 1);
    return mName;
  }

  @Nullable
  public Bundle getExtraData() {
    Bundle mBundle = getIntent().getBundleExtra("extraData");
    return mBundle;
  }

  @Override
  protected PageActivityDelegate createReactActivityDelegate() {
    if (mDelegate == null) {
      mDelegate = new PageActivityDelegate(this, getMainComponentName(), getJSBundleFile());
    }
    return mDelegate;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.w("PageActivity",
      "onCreate mMainComponentName " + getMainComponentName() + " mBundleFile " + getJSBundleFile());
    // todo拦截下载加载

    // 启动页全屏，状态栏覆盖启动页
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    // 获取数据
    Intent intent = getIntent();
    String bundleUrl = intent.getStringExtra("bundleUrl");
    String bundleFile = intent.getStringExtra("bundleFile");
    String appModule = intent.getStringExtra("appModule");
    String appName = intent.getStringExtra("appName");
    String appLogo = intent.getStringExtra("appLogo");
    String appVersion = intent.getStringExtra("appVersion");
    String appText = intent.getStringExtra("appText");
    Integer style = intent.getIntExtra("style", 1);

    // 初始化加载 判断加载方式
    if (style == 2) {
      // todo !采用注册模块加载-有问题待处理
      // ReactNativeHost aa = ((ReactApplication)
      // getApplication()).getReactNativeHost();
      // ReactInstanceManager bb = aa.getReactInstanceManager();
      // ReactContext cc = bb.getCurrentReactContext();
      // CatalystInstance dd = cc.getCatalystInstance();
      // dd.loadScriptFromFile(bundleFile, "", false);
      // bb.recreateReactContextInBackground();
    }
    super.onCreate(savedInstanceState);

    // 初始化加载页面
    Bundle bundle = new Bundle();
    bundle.putString("appName", appName);
    bundle.putString("appLogo", appLogo);
    bundle.putString("appVersion", appVersion);
    bundle.putString("appText", appText);
    sView = new PageStartView(this, bundle) {
      @Override
      public void onLeftClick() {
         this.restartActivity();
//        this.removeLayoutBox();
      }

      @Override
      public void onRightClick() {
        this.finishActivity();
      }
    };
    addContentView(sView, sView.getLayoutParams());


    // 新线程判断js加载情况
    new Thread() {
      public void run() {
        int i = 1;
        while (i >= 10) {
          // 判断jsbundle是否加载完成
          ReactNativeHost aa = getReactNativeHost();
          if (aa != null) {
            ReactInstanceManager bb = aa.getReactInstanceManager();
            if (bb != null) {
              ReactContext cc = bb.getCurrentReactContext();
              if (cc != null) {
                CatalystInstance dd = cc.getCatalystInstance();
                if (dd != null) {
                  while (dd.hasRunJSBundle()) {
                    // 移除初始化加载页面
                    if (sView != null) {
                      try {
                        Thread.sleep(1000);
                        sView.removeLayoutBox();
                        return;
                      } catch (InterruptedException e) {
                        e.printStackTrace();
                      }
                    }
                  }
                }
              }
            }
          }
          try {
            Thread.sleep(300);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          i++;
        }

      }
    }.start();

    Log.w("PageActivity", "onCreate");
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  // 下载文件
  public static void downloadFile(Activity activity, final String url, final String bundleFile,
                                  final OnDownloadListener listener) {
    final long startTime = System.currentTimeMillis();
    Log.i("DOWNLOAD", "startTime=" + startTime);
    OkHttpClient okHttpClient = new OkHttpClient();

    Request request = new Request.Builder().url(url).build();
    okHttpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        // 下载失败
        e.printStackTrace();
        listener.onDownloadFailed(e);
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
          Log.i("DOWNLOAD", "download file " + file.getPath() + " total:" + total);
          fos = new FileOutputStream(file);
          long sum = 0;
          while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);
            sum += len;
            int progress = (int) (sum * 1.0f / total * 100);
            // 下载中
            listener.onDownloading(progress);
          }
          fos.flush();
          Log.i("DOWNLOAD", "download file " + file.getPath());
          Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));
          // 下载完成
          listener.onDownloadSuccess();
          Log.i("DOWNLOAD", "download success");
        } catch (Exception e) {
          e.printStackTrace();
          listener.onDownloadFailed(e);
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

  public interface OnDownloadListener {
    /**
     * 下载成功
     */
    void onDownloadSuccess();

    /**
     * @param progress 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载失败
     */
    void onDownloadFailed(Exception e);
  }
}
