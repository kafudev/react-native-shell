package com.rnshell.app.generated;

import java.util.Arrays;
import java.util.List;
import org.unimodules.core.interfaces.Package;

public class BasePackageList {
  public List<Package> getPackageList() {
    return Arrays.<Package>asList(
        new expo.modules.application.ApplicationPackage(),
        new expo.modules.av.AVPackage(),
        new expo.modules.barcodescanner.BarCodeScannerPackage(),
        new expo.modules.battery.BatteryPackage(),
        new expo.modules.brightness.BrightnessPackage(),
        new expo.modules.camera.CameraPackage(),
        new expo.modules.constants.ConstantsPackage(),
        new expo.modules.crypto.CryptoPackage(),
        new expo.modules.device.DevicePackage(),
        new expo.modules.errorrecovery.ErrorRecoveryPackage(),
        new expo.modules.filesystem.FileSystemPackage(),
        new expo.modules.font.FontLoaderPackage(),
        new expo.modules.imageloader.ImageLoaderPackage(),
        new expo.modules.imagepicker.ImagePickerPackage(),
        new expo.modules.keepawake.KeepAwakePackage(),
        new expo.modules.permissions.PermissionsPackage(),
        new expo.modules.sensors.SensorsPackage(),
        new expo.modules.splashscreen.SplashScreenPackage(),
        new expo.modules.webbrowser.WebBrowserPackage()
    );
  }
}
