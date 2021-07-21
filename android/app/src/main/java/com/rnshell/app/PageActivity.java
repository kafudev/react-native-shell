package com.rnshell.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PageActivity extends ReactActivity {

  public static String bundleName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i("PageActivity", "onCreate executed!");
  }

  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new PageActivityDelegate(this, bundleName);
  }

}
