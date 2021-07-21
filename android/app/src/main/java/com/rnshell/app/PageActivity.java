package com.rnshell.app;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.DoKit;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;

import com.google.gson.Gson;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.OkDownload;

import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.rnshell.app.delegate.DispatchDelegate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PageActivity extends ReactActivity {

  public static String bundleName;

  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    DispatchDelegate delegate = new DispatchDelegate(this, bundleName);
    return delegate;
  }

  /**
   * 打开跳转到RN的展示页面
   *
   * 判断是否已下载，未下载则先下载再加载
   *
   * @param bundleName
   * @param bundleUrl
   */
  public void open(String bundleName, String bundleUrl) {
    // 检查是否下载过，如果已经下载过则直接打开，暂不考虑各种版本问题
    String f = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + bundleName + "/" + bundleName + ".bundle";
    File file = new File((f));
    if (file.exists()) {
      openActivity(bundleName);
    } else {
      downloadBundle(bundleName, bundleUrl);
    }
  }

  /**
   * 打开跳转到RN的展示页面
   *
   * @param bundleName
   */
  public void openActivity(String bundleName) {
    Intent starter = new Intent(getApplicationContext(), PageActivity.class);
    PageActivity.bundleName = bundleName;
    getApplicationContext().startActivity(starter);
  }

  /**
   * 下载对应的bundle
   *
   * @param bundleName
   */
  private void downloadBundle(final String bundleName, String bundleUrl) {
    Log.v("PageActivity", bundleName +" "+ bundleUrl);
    DownloadTask task = new DownloadTask.Builder(getApplicationContext().getFilesDir().getAbsolutePath(), Uri.parse(bundleUrl))
      // the minimal interval millisecond for callback progress
      .setMinIntervalMillisCallbackProcess(30)
      // do re-download even if the task has already been completed in the past.
      .setPassIfAlreadyCompleted(false)
      .build();
    task.enqueue(new DownloadListener() {
      @Override
      public void taskStart(@NonNull DownloadTask task) {

      }

      @Override
      public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {

      }

      @Override
      public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

      }

      @Override
      public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {

      }

      @Override
      public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {

      }

      @Override
      public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

      }

      @Override
      public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

      }

      @Override
      public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {

      }

      @Override
      public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {

      }

      @Override
      public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {

      }

      @Override
      public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
        try {
          Toast.makeText(getApplicationContext(), "下载完成", Toast.LENGTH_SHORT).show();
          // // 下载之后解压，然后打开
          // ZipUtils.unzip(getApplicationContext().getFilesDir().getAbsolutePath() + "/" +
          // bundleName + ".zip",
          // MainActivity.this.getFilesDir().getAbsolutePath());
          openActivity(bundleName);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}
