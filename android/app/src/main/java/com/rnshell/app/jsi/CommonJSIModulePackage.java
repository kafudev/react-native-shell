package com.rnshell.app.jsi;

import com.facebook.react.bridge.JSIModuleSpec;
import com.facebook.react.bridge.JSIModulePackage;
import com.facebook.react.bridge.JavaScriptContextHolder;
import com.facebook.react.bridge.ReactApplicationContext;
import com.swmansion.reanimated.ReanimatedJSIModulePackage;
// import com.reactnativemmkv.MmkvModule; // react-native-mmkv
import com.ammarahmed.mmkv.RNMMKVModule; // react-native-mmkv-storage
import com.nozbe.watermelondb.jsi.WatermelonDBJSIPackage; // ⬅️ This!

import java.util.Collections;
import java.util.List;

// TODO: Remove all of this when MMKV and Reanimated can be autoinstalled (maybe RN 0.65)
public class CommonJSIModulePackage extends  ReanimatedJSIModulePackage {
    @Override
    public List<JSIModuleSpec> getJSIModules(ReactApplicationContext reactApplicationContext, JavaScriptContextHolder jsContext) {
        super.getJSIModules(reactApplicationContext, jsContext);
        List<JSIModuleSpec> modules = Arrays.asList();
        // MmkvModule.install(jsContext, reactApplicationContext.getFilesDir().getAbsolutePath() + "/mmkv");
        reactApplicationContext.getNativeModule(RNMMKVModule.class).installLib(jsContext, reactApplicationContext.getFilesDir().getAbsolutePath() + "/mmkv");
        modules.addAll(new WatermelonDBJSIPackage().getJSIModules(reactApplicationContext, jsContext)); // ⬅️ This!
        return modules;
    }
}

