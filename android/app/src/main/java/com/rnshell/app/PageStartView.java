package com.rnshell.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import java.net.URL;

/**
 * TODO: document your custom view class.
 */
public class PageStartView extends RelativeLayout {

  public static int BaseWidth = 375;

  public Context context;
  public Bundle bundle;
  RelativeLayout layoutBox;
  RelativeLayout layoutBtnBox;

  public ImageButton leftBtn;
  public ImageButton rightBtn;

  public Integer screenWidth = 0;
  public Integer screenHeight = 0;
  public Integer boxTop = 0;

  public PageStartView(Context mContext, Bundle mBundle) {
    super(mContext);
    context = mContext;
    bundle = mBundle;
    DisplayMetrics dm = getResources().getDisplayMetrics();
    screenWidth = dm.widthPixels;
    screenHeight = dm.heightPixels;
    boxTop = screenHeight / 4;

    initLayoutBox();
    initBtnBox();
  }

  public FrameLayout.LayoutParams getLayoutParams() {
    return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
  }

  // 初始化应用加载信息框
  public void initLayoutBox() {
    // 防止重复加载
    if (layoutBox != null) {
      return;
    }

    // 初始化加载页面
    String appName = "";
    String appLogo = "";
    String appText = "";
    String appVersion = "";
    if (bundle != null) {
      appName = bundle.getString("appName", "");
      appLogo = bundle.getString("appLogo", "");
      appText = bundle.getString("appText", "");
      appVersion = bundle.getString("appVersion", "");
    }
    RelativeLayout.LayoutParams boxParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    layoutBox = new RelativeLayout(context);
    layoutBox.setBackgroundColor(Color.WHITE);

    // 初始化应用图标
    layoutBox.addView(this.renderIconView(context, appLogo));
    // 初始化应用名称
    layoutBox.addView(this.renderTitleView(context, appName));
    // 初始化加载load
    layoutBox.addView(this.renderLoadView(context, "···"));
    // 初始化页脚文本
    layoutBox.addView(this.renderFooterView(context, appText));

    this.addView(layoutBox, boxParams);

    removeLayoutBtnBox();
    initBtnBox();
    return;
  }

  // 移除应用加载信息框
  public void removeLayoutBox() {
    if (layoutBox != null) {
      this.removeView(layoutBox);
    }
    layoutBox = null;
  }

  // 初始化应用右上角按钮框
  public void initBtnBox() {
    // 防止重复加载
    if (layoutBtnBox != null) {
      return;
    }

    RelativeLayout.LayoutParams boxParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    layoutBtnBox = new RelativeLayout(context);
    // 初始化按钮组合
    layoutBtnBox.addView(this.renderBtnBoxView(context));
    layoutBtnBox.setGravity(Gravity.RIGHT);
    this.addView(layoutBtnBox, boxParams);
    return;
  }

  // 移除应用右上角按钮框
  public void removeLayoutBtnBox() {
    if (layoutBtnBox != null) {
      this.removeView(layoutBtnBox);
    }
    layoutBtnBox = null;
  }


  // 渲染按钮组合
  private LinearLayout renderBtnBoxView(Context context) {
    int bgColor = Color.parseColor("#80ffffff");
    int btnBorderColor = Color.parseColor("#dddddd");
    int btnLineColor = Color.parseColor("#999999");
    int btnWidth = pxToDip(context, 44);
    int btnHeight = pxToDip(context, 32);
    int btnTextSize = 12;
    int btnTextColor = Color.BLACK;
    int boxTopMargin = pxToDip(context, 4);
    int boxRightMargin = pxToDip(context, 6);
    int btnNum = 2;

    //判断是否是全屏
    int svint = ((Activity)context).getWindow().getDecorView().getSystemUiVisibility();
    if(svint == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
      boxTopMargin += getStatusHeight(context);
    }

    // 图标集合
    String base64String_left = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADwAAAA8CAYAAAA6/NlyAAAAAXNSR0IArs4c6QAAAZZJREFUaEPt171KHUEUB/DfLUyTV/AFtAlELMRahXTpNNhZWYgWxkJbP4oEtDRgJ0TS2gjaKwiKjeAD5BFsjBBl4QrXj9FdES7OnG33zH/O/2PPzLYU9rQK4ysI5+54OBwOZ6ZARDozQx/RCYfD4cwUiEhnZmgMrYh0RDozBSLSmRkaUzoiXXqkRzGAT/iPM1xg95XCdOJd4wTnOHgl3td2b/24qoOXinQPlrGQaGQPXxo2uYiVxJodfGuIt4HZxJpfmEEl6r3nKcIfcITPNRqoOwNuamBVJW+Jd4oh/Ovc+6kN5vGjZoNLWH2h9jcmuoT3HT+fI/yx/Y321mywKhvDfqJ+5Jl3qS3eEu8v+nB5t9lDhwdx3IBsVVp9m2uJNXNY7zLeMA5ThKew1bDBPxhPrNnGZJfxprEZhNsKFBfp4oZWZXRRx1JFuLiLR0W6qKtl5ylSzM9Dw6Pz/ZTXvay/H0YvdBqEs7EyQSQcDoczUyAinZmhj+iEw+FwZgpEpDMzNIZWRDoinZkCEenMDI0pHZHOPdK375hiPUaIiJYAAAAASUVORK5CYII=";
    //将Base64编码字符串解码成Bitmap
    byte[] decodedString_left = Base64.decode(base64String_left.split(",")[1], Base64.DEFAULT);
    Bitmap leftBitmap = BitmapFactory.decodeByteArray(decodedString_left, 0, decodedString_left.length);

    String base64String_right = "data:image/png;base64,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";
    //将Base64编码字符串解码成Bitmap
    byte[] decodedString_right = Base64.decode(base64String_right.split(",")[1], Base64.DEFAULT);
    Bitmap rightBitmap = BitmapFactory.decodeByteArray(decodedString_right, 0, decodedString_right.length);

    // 左边按钮
    leftBtn = new ImageButton(context);
    leftBtn.setBackgroundColor(Color.alpha(0));
    leftBtn.setLayoutParams(new ViewGroup.LayoutParams(btnWidth, btnHeight));
    leftBtn.setImageBitmap(leftBitmap);
    leftBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onLeftClick();
      }
    });
    GradientDrawable bg_left = createRectangleDrawable(0, btnBorderColor, 0, new float[]{btnHeight / 2, 0, 0, btnHeight / 2});
    leftBtn.setBackground(bg_left);

    // 右边按钮
    rightBtn = new ImageButton(context);
    rightBtn.setBackgroundColor(Color.alpha(0));
    rightBtn.setLayoutParams(new ViewGroup.LayoutParams(btnWidth, btnHeight));
    rightBtn.setImageBitmap(rightBitmap);
    rightBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onRightClick();
      }
    });
    GradientDrawable bg_right = createRectangleDrawable(0, btnBorderColor, 0, new float[]{0, btnHeight / 2, btnHeight / 2, 0});
    rightBtn.setBackground(bg_right);

    // 中间短线
    View centerLine = new View(context);
    centerLine.setBackgroundColor(btnLineColor);
    LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(1, btnHeight / 2);
    lineParams.topMargin = btnHeight / 2 / 2;
    centerLine.setLayoutParams(lineParams);

    LinearLayout layoutBtn = new LinearLayout(context);
    LinearLayout.LayoutParams layoutBtnParams = new LinearLayout.LayoutParams(btnWidth * btnNum, btnHeight);
    layoutBtnParams.topMargin = boxTopMargin;
    layoutBtnParams.rightMargin = boxRightMargin;
    layoutBtn.setLayoutParams(layoutBtnParams);
    layoutBtn.setGravity(Gravity.LEFT);
//        layoutBtn.setBackgroundColor(Color.BLACK);
    GradientDrawable bg_box = createRectangleDrawable(0, btnBorderColor, 1, new float[]{btnHeight / 2, btnHeight / 2, btnHeight / 2, btnHeight / 2});
    layoutBtn.setBackground(bg_box);
    layoutBtn.addView(leftBtn);
    layoutBtn.addView(centerLine);
    layoutBtn.addView(rightBtn);
    return layoutBtn;
  }

  // 左边点击事件
  public void onLeftClick() {
    initLayoutBox();
  }

  // 右边点击事件
  public void onRightClick() {
    removeLayoutBox();
    removeLayoutBtnBox();
  }

  // 重启activity
  public void restartActivity() {
    if (context != null) {
      Activity aa = ((Activity) context);
      Context cc = context.getApplicationContext();
      Intent intent = aa.getIntent();
      aa.overridePendingTransition(0, 0);
      ((Activity) context).finish();
      aa.overridePendingTransition(0, 0);
      cc.startActivity(intent);
    }

  }

  // 结束activity
  public void finishActivity() {
    if (context != null) {
      ((Activity) context).finish();
    }
  }

  /**
   * 创建背景颜色
   *
   * @param color       填充色
   * @param strokeColor 线条颜色
   * @param strokeWidth 线条宽度  单位px
   * @param radius      角度  px,长度为4,分别表示左上,右上,右下,左下的角度
   */
  private static GradientDrawable createRectangleDrawable(@ColorInt int color, @ColorInt int strokeColor, int strokeWidth, float radius[]) {
    try {
      GradientDrawable radiusBg = new GradientDrawable();
      //设置Shape类型
      radiusBg.setShape(GradientDrawable.RECTANGLE);
      //设置填充颜色
      radiusBg.setColor(color);
      //设置线条粗心和颜色,px
      radiusBg.setStroke(strokeWidth, strokeColor);
      //每连续的两个数值表示是一个角度,四组:左上,右上,右下,左下
      if (radius != null && radius.length == 4) {
        radiusBg.setCornerRadii(new float[]{radius[0], radius[0], radius[1], radius[1], radius[2], radius[2], radius[3], radius[3]});
      }
      return radiusBg;
    } catch (Exception e) {
      return new GradientDrawable();
    }
  }

  // 渲染图标
  private LinearLayout renderIconView(Context context, String url) {
    ImageView appImg = new ImageView(context);
    Glide.with(context)
      .load(url)
      .fitCenter()
      .apply(RequestOptions.bitmapTransform(new CircleCrop()))
      .into(appImg);

    LinearLayout layoutImg = new LinearLayout(context);
    LinearLayout.LayoutParams layoutImgParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxToDip(context, 60));
    layoutImgParams.topMargin = boxTop;
    layoutImg.setLayoutParams(layoutImgParams);
    layoutImg.setGravity(Gravity.CENTER);
//        layoutImg.setBackgroundColor(Color.BLUE);
    layoutImg.addView(appImg);
    return layoutImg;
  }

  // 渲染标题文字
  private LinearLayout renderTitleView(Context context, String title) {
    TextView tv = new TextView(context);
    tv.setText(title);
    tv.setTextSize(15);
    tv.setGravity(Gravity.CENTER);
    tv.setTextColor(Color.rgb(51, 51, 51));
//        tv.setBackgroundColor(Color.WHITE);
    tv.setWidth(screenWidth);
    LinearLayout layoutText = new LinearLayout(context);
    LinearLayout.LayoutParams layoutTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxToDip(context, 40));
    layoutTextParams.topMargin = boxTop + pxToDip(context, 70);
    layoutText.setLayoutParams(layoutTextParams);
    layoutText.setGravity(Gravity.CENTER);
//        layoutText.setBackgroundColor(Color.GREEN);
    layoutText.addView(tv);
    return layoutText;
  }

  // 渲染load加载器
  private LinearLayout renderLoadView(Context context, String title) {
    TextView tv = new TextView(context);
    tv.setText(title);
    tv.setTextSize(26);
    tv.setGravity(Gravity.CENTER);
//        tv.setTextColor(Color.WHITE);
//        tv.setBackgroundColor(Color.WHITE);
    tv.setWidth(screenWidth);
    LinearLayout layoutText = new LinearLayout(context);
    LinearLayout.LayoutParams layoutTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxToDip(context, 40));
    layoutTextParams.topMargin = boxTop + pxToDip(context, 100);
    layoutText.setLayoutParams(layoutTextParams);
    layoutText.setGravity(Gravity.CENTER);
//        layoutText.setBackgroundColor(Color.GREEN);
    layoutText.addView(tv);
    return layoutText;
  }

  // 渲染页脚文本
  private LinearLayout renderFooterView(Context context, String title) {
    TextView tv = new TextView(context);
    tv.setText(title);
    tv.setTextSize(12);
    tv.setGravity(Gravity.CENTER);
    tv.setTextColor(Color.rgb(136, 136, 136));
//        tv.setTextColor(Color.WHITE);
//        tv.setBackgroundColor(Color.WHITE);
    tv.setWidth(screenWidth);
    LinearLayout layoutText = new LinearLayout(context);
    LinearLayout.LayoutParams layoutTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxToDip(context, 40));
    layoutTextParams.topMargin = screenHeight - pxToDip(context, 40) - pxToDip(context, 40);
    layoutText.setLayoutParams(layoutTextParams);
    layoutText.setGravity(Gravity.CENTER);
//        layoutText.setBackgroundColor(Color.GREEN);
    layoutText.addView(tv);
    return layoutText;
  }

  /**
   * dip转px
   */
  public static int dipToPx(Context context, float dip) {
    return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
  }

  /**
   * px转dip
   */
  public static int pxToDip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    pxValue = pxValue * getScreenBaseScale(context);
    return (int) (pxValue / scale + 0.5f);
  }

  /**
   * 将sp值转换为px值
   */
  public static int sp2px(Context context, float spValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (spValue * fontScale + 0.5f);
  }

  /**
   * 将sp值转换为px值
   */
  public static int px2sp(Context context, float pxValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (pxValue / fontScale + 0.5f);
  }


  /**
   * dp转换成px
   *
   * @param dp
   * @param context
   * @return
   */
  protected int dp2px(int dp, Context context) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
  }

  /**
   * sp转换成px
   *
   * @param sp
   * @param context
   * @return
   */
  protected int sp2px(int sp, Context context) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
  }

  /**
   * 获取屏幕分辨率：宽
   */
  public static int getScreenPixWidth(Context context) {
    return context.getResources().getDisplayMetrics().widthPixels;
  }

  /**
   * 获取屏幕分辨率：高
   */
  public static int getScreenPixHeight(Context context) {
    return context.getResources().getDisplayMetrics().heightPixels;
  }

  /**
   * 获取设计稿跟屏幕比例
   */
  public static float getScreenBaseScale(Context context) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return context.getResources().getDisplayMetrics().widthPixels * scale / BaseWidth;
  }

  /**
   * 获取状态栏的高度
   */
  public static int getStatusHeight(Context context) {
    int result = 0;
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");//包名:com.example.application
    if (resourceId > 0) {
      result = context.getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }
}


