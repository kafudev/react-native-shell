package com.rnshell.app

import android.app.Activity
import android.app.ActivityManager.TaskDescription
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.facebook.react.ReactActivity
import com.facebook.react.ReactRootView
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile


class ContainerActivity : ReactActivity(), CoroutineScope by MainScope(),
  DefaultHardwareBackBtnHandler {
  companion object {
    private const val appName = "applicationName"
    private const val mName = "methodName"
    fun start(activity: Activity, applicationName: String, methodName: String) {
      val intent = Intent(activity, ContainerActivity::class.java)
      intent.putExtra(appName, applicationName)
      intent.putExtra(mName, methodName)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
      intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
      activity.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_content_rn)
    val applicaiton1 = intent.getStringExtra(appName)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      setTaskDescription(TaskDescription(applicaiton1))
    }
    val methodName = intent.getStringExtra(mName)
    applicaiton1?.let {
      val suffix = ".zip"
      runBlocking {
        withContext(Dispatchers.IO) {
          copyToDisk(applicaiton1, suffix)
          unZipApplication(applicaiton1, suffix)
        }
        val jsBundle = ".bundle"
        val bundlePath =
          filesDir.absolutePath + File.separator + applicaiton1 + File.separator + applicaiton1 + jsBundle
        val manager = ReactInstanceManagerHelper.getManager(this@ContainerActivity) {
          it.setJSBundleFile(bundlePath)
          it.setDefaultHardwareBackBtnHandler(this@ContainerActivity)
//                    it.setUseDeveloperSupport(BuildConfig.DEBUG)
        }
        val reactRootView = findViewById<ReactRootView>(R.id.containerRn)
        reactRootView?.startReactApplication(
          manager,
          methodName,
        )
      }
    }
  }


  private fun unZipApplication(
    applicationName: String,
    suffix: String,
  ) {
    val absolutePath = filesDir.absolutePath
    val fileApp1 = File(absolutePath, applicationName + suffix)
    val applicationFile1 = File(absolutePath, applicationName)
    if (!applicationFile1.exists() || !applicationFile1.isDirectory) {
      //unzip
      val zipFile = ZipFile(fileApp1)
      val entries = zipFile.entries()
      applicationFile1.mkdirs()
      val buffer = ByteArray(1024 * 1024 * 2)
      while (entries.hasMoreElements()) {
        entries.nextElement()?.apply {
          val inputStream = zipFile.getInputStream(this)
          val file = File(applicationFile1, name)
          if (isDirectory) {
            file.mkdirs()
          } else {
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            var len: Int
            while (inputStream.read(buffer).also { len = it } > 0) {
              outputStream.write(buffer, 0, len)
            }
            inputStream.close()
            outputStream.close()
          }
        }
      }
    }
  }

  private fun copyToDisk(
    applicationName: String,
    suffix: String,
  ) {
    val absolutePath = filesDir.absolutePath
    val fileApp1 = File(absolutePath, applicationName + suffix)
    if (!fileApp1.exists()) {
      val inputStream = assets.open(applicationName + suffix)
      File(
        filesDir.absolutePath,
        applicationName + suffix
      ).writeBytes(inputStream.readBytes())
    }
  }

  //    fun
  override fun onDestroy() {
    super.onDestroy()
    cancel()
    ReactInstanceManagerHelper.getManager(this@ContainerActivity)?.onHostDestroy(this)
    ReactInstanceManagerHelper.clear(this)
  }

  override fun invokeDefaultOnBackPressed() {
    super.onBackPressed()
  }

  override fun onPause() {
    super.onPause()
    ReactInstanceManagerHelper.getManager(this@ContainerActivity)?.onHostPause(this)
  }

  override fun onResume() {
    super.onResume()
    ReactInstanceManagerHelper.getManager(this@ContainerActivity)?.onHostResume(this, this)
  }


  override fun onBackPressed() {
    ReactInstanceManagerHelper.getManager(this@ContainerActivity)?.onBackPressed()
  }
}
