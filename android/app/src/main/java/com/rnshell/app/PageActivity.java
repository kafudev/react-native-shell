package com.rnshell.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PageActivity extends ReactActivity implements DefaultHardwareBackBtnHandler {

  public static String bundleName = "index";
  public static void start(Activity activity, String moduleName){
    PageActivity.bundleName = moduleName;
    Intent intent = new Intent(activity, PageActivity.class);
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    activity.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i("PageActivity", "onCreate executed!");
    Toast.makeText(this, "模块页面创建完成", Toast.LENGTH_SHORT).show();
  }

  @Override
  protected String getMainComponentName() {
    return bundleName;
  }

  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    Log.i("PageActivity", "createReactActivityDelegate "+bundleName);
    return new PageActivityDelegate(this, getMainComponentName());
  }

  @Override
  public void invokeDefaultOnBackPressed() {
    super.onBackPressed();
  }

}
