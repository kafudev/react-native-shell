package com.rnshell.app;

import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Arguments;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 通用模块
 *
 * 常量导出，新页面打开，三方服务页面打开
 */
public class CommonModule extends ReactContextBaseJavaModule {
  private static ReactApplicationContext reactContext;

  private static final String DURATION_SHORT_KEY = "TOAST—SHORT";
  private static final String DURATION_LONG_KEY = "TOAST—LONG";

  public CommonModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
  }

  @Override
  public String getName() {
    return "Common";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
    constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
    return constants;
  }

  /**
   * 显示toast提示
   * @param message
   * @param duration
   */
  @ReactMethod
  public void toast(String message, int duration) {
    Toast.makeText(getReactApplicationContext(), message, duration).show();
  }

  /**
   * 跳转加载bundle打开页面
   *
   * @param bundleName
   * @param promise
   */
  @ReactMethod
  public void openPageActivity(String bundleName, String bundleUrl, final Promise promise) {
    PageActivity page = new PageActivity();
    page.open(bundleName, bundleUrl);
  }

}
