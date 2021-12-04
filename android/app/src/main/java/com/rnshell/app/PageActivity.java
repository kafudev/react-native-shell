package com.rnshell.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
    Activity _activity = this;
    // 启动页全屏，状态栏覆盖启动页
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    Log.w("PageActivity",
      "onCreate mMainComponentName " + getMainComponentName() + " mBundleFile " + getJSBundleFile());

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
    // 设置启动页为默认视图
    setContentView(sView);

    // 子线程进行加载文件
    new Thread() {
      public void run() {
        // 加载并下载文件
        loadFileOrDownFile(_activity, intent, new OnDownloadListener() {
          @Override
          public void onDownloadSuccess() {
            Log.w("loadFileOrDownFile", "download success " + bundleFile);
            isLoadSuccess = true;
          }

          @Override
          public void onDownloading(int progress) {
            Log.w("loadFileOrDownFile", "download progress " + progress);
          }

          @Override
          public void onDownloadFailed(Exception e) {
            Log.w("loadFileOrDownFile", "download failed " + bundleFile);
            // 文件下载失败
            Toast.makeText(getApplicationContext(), "模块加载失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    super.onCreate(savedInstanceState);
    // 继续覆盖显示启动页
    addContentView(sView, sView.getLayoutParams());

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

    Log.w("PageActivity", "onCreate");
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  // 加载或者下载文件
  public void loadFileOrDownFile(Activity activity, final Intent intent, final OnDownloadListener listener) {
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
      downloadFile(activity, bundleUrl, bundleFile, new OnDownloadListener() {
        @Override
        public void onDownloadSuccess() {
          Log.w("OnDownloadListener", "download success " + bundleFile);
          listener.onDownloadSuccess();
        }

        @Override
        public void onDownloading(int progress) {
          Log.w("OnDownloadListener", "download loading " + progress);
          listener.onDownloading(progress);
        }

        @Override
        public void onDownloadFailed(Exception e) {
          Log.w("OnDownloadListener", "download failed " + e.getMessage());
          listener.onDownloadFailed(e);
        }
      });
    } else {
      // 通知已下载成功
      listener.onDownloadSuccess();
    }
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
