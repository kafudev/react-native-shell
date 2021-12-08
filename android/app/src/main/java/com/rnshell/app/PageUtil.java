package com.rnshell.app;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PageUtil {

  // 下载文件
  public static void downloadFile(Context context, final String url, final String savePath,
                                  final PageUtil.OnDownloadListener listener) {
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
        File destDir = new File(savePath);
        if (!destDir.exists()) {
          destDir.mkdirs();
        }
        Log.i("DOWNLOAD", "download savePath " + savePath);
        try {
          is = response.body().byteStream();
          long total = response.body().contentLength();
          // 保存路径
          String filePath = savePath + getFileName(response);
          Log.i("DOWNLOAD", "download filePath " + filePath);
          File file = new File(filePath);
          //当文件不存在，创建出来
          if (!file.exists()) {
            file.createNewFile();
          } else {
            file.delete();
            file.createNewFile();
          }
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
          listener.onDownloadSuccess(file.getPath());
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

  // 获取请求文件名
  public static String getFileName(Response response) {
    String fileName = "";
    if (response != null) {
      try {
//        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
//        Request request = new Request.Builder()
//          .url(url)//请求接口。如果需要传参拼接到接口后面。
//          .build();//创建Request 对象
//        Response response = client.newCall(request).execute();//得到Response 对象
        HttpUrl realUrl = response.request().url();
        String path = realUrl.encodedPath();
        Log.e("PageUtil", "getFileName real:" + realUrl.toString());
        if (path != null) {
          String temp = path.toString();
          fileName = temp.substring(temp.lastIndexOf("/") + 1);
        }
      } catch (Exception e) {
        // e.printStackTrace();
        Log.w("PageUtil", "getFileName Get File Name:error" + e.getMessage());
      }
    }
    Log.w("PageUtil", "getFileName fileName--->" + fileName);
    return fileName;
  }

//  /**
//   * 解析文件头
//   * Content-Disposition:attachment;filename=FileName.txt
//   * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
//   */
//  public static String getHeaderFileName(Response response) {
//    String dispositionHeader = response.header("Content-Disposition");
//    if (!TextUtils.isEmpty(dispositionHeader)) {
//      dispositionHeader.replace("attachment;filename=", "");
//      dispositionHeader.replace("filename*=utf-8", "");
//      String[] strings = dispositionHeader.split("; ");
//      if (strings.length > 1) {
//        dispositionHeader = strings[1].replace("filename=", "");
//        dispositionHeader = dispositionHeader.replace("\"", "");
//        return dispositionHeader;
//      }
//      return "";
//    }
//    return "";
//  }

  /**
   * 进行文件解压的方法
   *
   * @param file 下载回来的压缩文件
   **/
  public static List<File> unZipFile(File file, String outPath) {

    //创建集合，保存解压出来的文件
    List<File> mFileList = new ArrayList<>();
    //定义解压出来的文件对象
    File outFile = null;
    try {
      //创建ZipFile对象
      ZipFile mZipFile = new ZipFile(file);
      //创建ZipInputStream对象
      ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
      //创建ZipEntry对象
      ZipEntry zipEntry = null;
      //定义InputStream对象
      InputStream is = null;
      //定义OutputStream对象
      OutputStream os = null;
      while ((zipEntry = zis.getNextEntry()) != null) {
        //拼凑路径，创建解压出来的文件对象
        outFile = new File(outPath
          + "/" + File.separator + zipEntry.getName());
        //判断父级目录是否存在
        if (!outFile.getParentFile().exists()) {
          //创建父级目录
          outFile.getParentFile().mkdir();
        }
        //判断文件是否存在
        if (!outFile.exists()) {
          //创建文件
          outFile.createNewFile();
        }
        //获取is的实例
        is = mZipFile.getInputStream(zipEntry);
        os = new FileOutputStream(outFile);
        //创建缓冲区
        byte[] buffer = new byte[8 * 1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
          os.write(buffer, 0, length);
        }
        //这里加多一次判断是为了保险起见，防止出现空指针
        if (outFile.exists()) {
          //将文件保存到集合中
          mFileList.add(outFile);
        }
        is.close();
        os.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mFileList;
  }

  public static interface OnDownloadListener {
    /**
     * 加载成功
     */
    void onLoadSuccess();

    /**
     * 下载成功
     */
    void onDownloadSuccess(String filePath);

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
