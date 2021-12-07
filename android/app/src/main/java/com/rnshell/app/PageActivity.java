package com.rnshell.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.common.JavascriptException;
import com.masteratul.exceptionhandler.NativeExceptionHandlerIfc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PageActivity extends ReactActivity {
  public PageActivityDelegate mDelegate = createReactActivityDelegate();
  public static String mMainComponentName;
  public static String mJSBundleFile;
  public static Boolean mIsReload;
  public static Integer mStyle;

  public PageStartView sView = null;
  public Boolean isLoadSuccess = false;  //是否加载成功

  // 启动
  public static void start(Activity activity, int style, Boolean isReload, String bundleUrl, String appModule,
                           String appName,
                           String appLogo,
                           String appVersion,
                           String appText,
                           Bundle extraData) {
    // 下载文件路径
    String filepath = activity.getApplication().getFilesDir().getAbsolutePath() + "/" + appModule;
    String bundleFile = filepath + "/" + appModule + "_" + appVersion + ".bundle";
    File destDir = new File(filepath);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }
    Log.w("PageActivity", "start " + appModule + " " + appVersion + " " + appName + " " + appLogo);

    // 设置默认变量
    mMainComponentName = appModule;
    mJSBundleFile = bundleFile;
    mIsReload = isReload;
    mStyle = style;

    // 启动参数
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
  public Boolean getIsReolad() {
    if (getIntent() == null) {
      if (mIsReload != null) {
        return mIsReload;
      }
      return false;
    }
    Boolean mName = getIntent().getBooleanExtra("isReload", false);
    return mName;
  }

  @Nullable
  public Bundle getExtraData() {
    Bundle mBundle = getIntent().getBundleExtra("extraData");
    return mBundle;
  }

  @Override
  protected PageActivityDelegate createReactActivityDelegate() {
    File file = new File(getJSBundleFile());
    if (!file.exists() || getIsReolad()) {
      // 主模块清空，重新下载并异步加载视图
      mMainComponentName = null;
      isLoadSuccess = false;
    } else {
      isLoadSuccess = true;
    }
    if (mDelegate == null) {
      mDelegate = new PageActivityDelegate(this, getMainComponentName(), getJSBundleFile());
    }
    return mDelegate;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    try {
      // 启动页全屏，状态栏覆盖启动页
      getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
      Log.w("PageActivity",
        "onCreate start mMainComponentName " + getMainComponentName() + " mBundleFile " + getJSBundleFile());

      // 获取数据
      Intent intent = getIntent();
      String bundleUrl = intent.getStringExtra("bundleUrl");
      String bundleFile = intent.getStringExtra("bundleFile");
      String appModule = intent.getStringExtra("appModule");
      String appName = intent.getStringExtra("appName");
      String appLogo = intent.getStringExtra("appLogo");
      String appVersion = intent.getStringExtra("appVersion");
      String appText = intent.getStringExtra("appText");
      Bundle extraData = getIntent().getBundleExtra("extraData");
      Integer style = intent.getIntExtra("style", 1);

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
        }
      };

      // 子线程进行加载文件
      new Thread() {
        public void run() {
          // 加载并下载文件
          loadFileOrDownFile(getApplicationContext(), intent, new OnDownloadListener() {
            @Override
            public void onLoadSuccess() {
              Log.w("loadFileOrDownFile", "download loadSuccess ");
              isLoadSuccess = true;
            }

            @Override
            public void onDownloadSuccess() {
              Log.w("loadFileOrDownFile", "download success ");
              isLoadSuccess = true;
              runOnUiThread(new Runnable() {
                public void run() {
                  try {
                    // 下载完成进行重新加载
                    Log.w("loadFileOrDownFile", "loadApp mMainComponentName " + getMainComponentName());
                    // 重新加载
                    // 禁止二次重新加载
                    mIsReload = false;
                    mMainComponentName = appModule;
                    Context cc = getApplicationContext();
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("isReload", mIsReload);
                    overridePendingTransition(0, 0);
                    finish();
                    overridePendingTransition(0, 0);
                    cc.startActivity(intent);
                  } catch (Exception e) {
                    Log.w("loadFileOrDownFile", "loadApp error " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "加载模块异常" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                  }
                }
              });
            }

            @Override
            public void onDownloading(int progress) {
              Log.w("loadFileOrDownFile", "download loading " + progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
              Log.w("loadFileOrDownFile", "download failed " + e.getMessage());
              if (Looper.myLooper() == null) {
                Looper.prepare();
              }
              Toast.makeText(getApplicationContext(), "模块加载失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
              Looper.loop();
            }
          });
        }
      }.start();

      // 进行页面加载方式判断
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

      // 设置默认背景视图
      super.onCreate(savedInstanceState);
      // 覆盖显示启动页
      addContentView(sView, sView.getLayoutParams());

      // 捕获异常并显示
      // 获取原有的异常处理器
      Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
      // 实例化异常处理器后，利用 setDefaultUncaughtExceptionHandler 重置异常处理器
      Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
        // 重写 uncaughtException 方法，当程序中有未捕获的异常时，会调用该方法
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
          String stackTraceString = Log.getStackTraceString(throwable);
          Log.w("PageActivity", "uncaughtException " + throwable.getMessage());
          // 允许执行且已存在异常处理函数时，执行原异常处理函数
          // if (originalHandler != null) {
          // originalHandler.uncaughtException(thread, throwable);
          // }
          // 提示错误信息
          Toast.makeText(getApplicationContext(), "模块运行异常" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
          finish();
          // 异常捕获重置
          Thread.setDefaultUncaughtExceptionHandler(originalHandler);
        }
      });

      // 新线程判断js加载情况-加载完成关闭启动视图
      new Thread() {
        public void run() {
          // 监听jsbundle加载完成移除启动视图
          ReactMarker.MarkerListener markerListener = new ReactMarker.MarkerListener() {
            @Override
            public void logMarker(ReactMarkerConstants name, @Nullable String tag, int instanceKey) {
              // 加载完成
              // 加载完成:CONTENT_APPEARED 加载完成之前:ATTACH_MEASURED_ROOT_VIEWS_END
              if (name == ReactMarkerConstants.CONTENT_APPEARED) {
                if (sView != null) {
                  sView.removeLayoutBox();
                }
              }
            }
          };
          ReactMarker.addListener(markerListener);
        }
      }.start();
      Log.w("PageActivity", "onCreate end.");
    } catch (Exception e) {
      Log.w("PageActivity", "onCreate error " + e.getMessage());
      Toast.makeText(getApplicationContext(), "模块加载异常" + e.getMessage(), Toast.LENGTH_SHORT).show();
      finish();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  // 加载或者下载文件
  public void loadFileOrDownFile(Context context, final Intent intent, final OnDownloadListener listener) {
    String bundleUrl = intent.getStringExtra("bundleUrl");
    String bundleFile = intent.getStringExtra("bundleFile");
    String appModule = intent.getStringExtra("appModule");
    String appName = intent.getStringExtra("appName");
    String appLogo = intent.getStringExtra("appLogo");
    String appVersion = intent.getStringExtra("appVersion");
    String appText = intent.getStringExtra("appText");
    Boolean isReload = intent.getBooleanExtra("isReload", false);

    // 下载文件
    Log.w("PageActivity", "loadFileOrDownFile " + appModule + " " + appVersion + " " + appName + " " + appText + " " + appLogo);
    File file = new File(bundleFile);
    if (!file.exists() || isReload) {
      downloadFile(context, bundleUrl, bundleFile, listener);
    } else {
      // 通知已下载成功
      listener.onLoadSuccess();
    }
  }

  // 下载文件
  public static void downloadFile(Context context, final String url, final String bundleFile,
                                  final OnDownloadListener listener) {
    final long startTime = System.currentTimeMillis();
    Log.i("DOWNLOAD", "startTime=" + startTime);
    OkHttpClient okHttpClient = new OkHttpClient();

    Request request = new Request.Builder().url(url).build();
    okHttpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        Log.i("DOWNLOAD", "download failed " + e.getMessage());
        // 下载失败
        e.printStackTrace();
        listener.onDownloadFailed(e);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        // 储存下载文件的目录
        String savePath = Environment.getDownloadCacheDirectory().getAbsolutePath();
        savePath = context.getApplicationContext().getFilesDir().getAbsolutePath();
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
//            listener.onDownloading(progress);
          }
          Log.i("DOWNLOAD", "download file " + file.getPath());
          Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));
          fos.flush();
          // 下载完成
          listener.onDownloadSuccess();
          Log.i("DOWNLOAD", "download success ");
        } catch (IOException e) {
          Log.i("DOWNLOAD", "download failed IOException " + e.getMessage());
          e.printStackTrace();
          listener.onDownloadFailed(e);
        } catch (Exception e) {
          Log.i("DOWNLOAD", "download failed Exception " + e.getMessage());
          e.printStackTrace();
          listener.onDownloadFailed(e);
        } finally {
          try {
            if (is != null)
              is.close();
          } catch (IOException e) {
            Log.i("DOWNLOAD", "is.close() failed " + e.getMessage());
          }
          try {
            if (fos != null)
              fos.close();
          } catch (IOException e) {
            Log.i("DOWNLOAD", "fos.close() failed " + e.getMessage());
          }
        }
      }
    });
  }

  public interface OnDownloadListener {
    /**
     * 加载成功
     */
    void onLoadSuccess();

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
