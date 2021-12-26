#import "AppDelegate.h"

#import <React/RCTLinkingManager.h>
#import <React/RCTBridge.h>
#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>

#ifdef NSFoundationVersionNumber_iOS_9_x_Max
#import <UserNotifications/UserNotifications.h>
#endif

#import <AppCenterReactNative.h>
#import <AppCenterReactNativeAnalytics.h>
#import <AppCenterReactNativeCrashes.h>
#import <CodePush/CodePush.h>

#import <Bugly/Bugly.h>
#import <QYSDK/QYSDK.h>
//#import <TXLiteAVSDK_Professional/TXLiteAVSDK.h>
//#import <TXLiteAVSDK_Professional/TXLiveBase.h>

#import "ReactNativeConfig.h"
#import "RNBootSplash.h"
#import <RCTJPushModule.h>
#import <WXApi.h>
#import <AlibcTradeSDK/AlibcTradeSDK.h>
#import "Orientation.h"

#ifdef DEBUG
#import <DoraemonKit/DoraemonManager.h>
#endif

#ifdef FB_SONARKIT_ENABLED
#import <FlipperKit/FlipperClient.h>
#import <FlipperKitLayoutPlugin/FlipperKitLayoutPlugin.h>
#import <FlipperKitUserDefaultsPlugin/FKUserDefaultsPlugin.h>
#import <FlipperKitNetworkPlugin/FlipperKitNetworkPlugin.h>
#import <SKIOSNetworkPlugin/SKIOSNetworkAdapter.h>
#import <FlipperKitReactPlugin/FlipperKitReactPlugin.h>

static void InitializeFlipper(UIApplication *application) {
  FlipperClient *client = [FlipperClient sharedClient];
  SKDescriptorMapper *layoutDescriptorMapper = [[SKDescriptorMapper alloc] initWithDefaults];
  [client addPlugin:[[FlipperKitLayoutPlugin alloc] initWithRootNode:application withDescriptorMapper:layoutDescriptorMapper]];
  [client addPlugin:[[FKUserDefaultsPlugin alloc] initWithSuiteName:nil]];
  [client addPlugin:[FlipperKitReactPlugin new]];
  [client addPlugin:[[FlipperKitNetworkPlugin alloc] initWithNetworkAdapter:[SKIOSNetworkAdapter new]]];
  [client start];
}
#endif

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{

  // bugly异常上报
  NSString *BUGLY_APPID = [ReactNativeConfig envFor:@"BUGLY_APPID"];
  [Bugly startWithAppId: BUGLY_APPID];

  // appcenter
  [AppCenterReactNative register];  // Initialize AppCenter
  [AppCenterReactNativeAnalytics registerWithInitiallyEnabled:true];  // Initialize AppCenter analytics
  [AppCenterReactNativeCrashes registerWithAutomaticProcessing];  // Initialize AppCenter crashes


  #ifdef FB_SONARKIT_ENABLED
    InitializeFlipper(application);
  #endif

  #ifdef DEBUG
    // then read individual keys like:
    NSString *DOKIT_APPID = [ReactNativeConfig envFor:@"DOKIT_APPID"];
    [[DoraemonManager shareInstance] installWithPid: DOKIT_APPID];//productId为在“平台端操作指南”中申请的产品id
  #endif

  //七鱼推荐在程序启动的时候初始化 SDK
  NSString *QIYU_APPKEY = [ReactNativeConfig envFor:@"QIYU_APPKEY"];
  if(QIYU_APPKEY != nil) {
    QYSDKOption *option = [QYSDKOption optionWithAppKey:QIYU_APPKEY];
    option.appName = @"rnshell";
    [[QYSDK sharedSDK] registerWithOption:option];
  }

//  // TXLive初始化 SDK
//  NSString *TXLIVE_LICENCE_KEY = [ReactNativeConfig envFor:@"TXLIVE_LICENCE_KEY"];
//  NSString *TXLIVE_LICENCE_URL = [ReactNativeConfig envFor:@"TXLIVE_LICENCE_URL"];
//  if(TXLIVE_LICENCE_KEY != nil && TXLIVE_LICENCE_URL != nil) {
//    [TXLiveBase setLicenceURL:TXLIVE_LICENCE_URL key:TXLIVE_LICENCE_KEY];
//  }


  // JPush初始化配置
  NSString *JPUSH_APPKEY = [ReactNativeConfig envFor:@"JPUSH_APPKEY"];
  [JPUSHService setupWithOption:launchOptions appKey: JPUSH_APPKEY
                        channel:@"appstore" apsForProduction:YES];
  // APNS
  JPUSHRegisterEntity * entity = [[JPUSHRegisterEntity alloc] init];
  if (@available(iOS 12.0, *)) {
    entity.types = JPAuthorizationOptionAlert|JPAuthorizationOptionBadge|JPAuthorizationOptionSound|JPAuthorizationOptionProvidesAppNotificationSettings;
  }
  [JPUSHService registerForRemoteNotificationConfig:entity delegate:self];
  [launchOptions objectForKey: UIApplicationLaunchOptionsRemoteNotificationKey];
  // 自定义消息
  NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
  [defaultCenter addObserver:self selector:@selector(networkDidReceiveMessage:) name:kJPFNetworkDidReceiveMessageNotification object:nil];
  // 地理围栏
  [JPUSHService registerLbsGeofenceDelegate:self withLaunchOptions:launchOptions];

  RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:launchOptions];
  RCTRootView *rootView = [[RCTRootView alloc] initWithBridge:bridge
                                                   moduleName:@"rnshell"
                                            initialProperties:nil];

  if (@available(iOS 13.0, *)) {
      rootView.backgroundColor = [UIColor systemBackgroundColor];
  } else {
      rootView.backgroundColor = [UIColor whiteColor];
  }

  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  [RNBootSplash initWithStoryboard:@"BootSplash" rootView:rootView];
  return YES;
}

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
#if DEBUG
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index" fallbackResource:nil];
#else
  // return [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];
  return [CodePush bundleURL];
#endif
}

- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
  while ([[UIDevice currentDevice] isGeneratingDeviceOrientationNotifications]) {
      [[UIDevice currentDevice] endGeneratingDeviceOrientationNotifications];
  }
  return [Orientation getOrientation];
}


//************************************************JPush start************************************************

//注册 APNS 成功并上报 DeviceToken
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
  [JPUSHService registerDeviceToken:deviceToken];
  [[QYSDK sharedSDK] updateApnsToken:deviceToken];
  // NSLog(@"iOS deviceToken: %@", deviceToken);
}

//iOS 7 APNS
- (void)application:(UIApplication *)application didReceiveRemoteNotification:  (NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
  // iOS 10 以下 Required
  NSLog(@"iOS 7 APNS");
  [JPUSHService handleRemoteNotification:userInfo];
  [[NSNotificationCenter defaultCenter] postNotificationName:J_APNS_NOTIFICATION_ARRIVED_EVENT object:userInfo];
  completionHandler(UIBackgroundFetchResultNewData);
}

//ios 4 本地通知 todo
- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification{
  NSDictionary *userInfo =  notification.userInfo;
  NSLog(@"iOS 4 本地通知");
  [[NSNotificationCenter defaultCenter] postNotificationName:J_LOCAL_NOTIFICATION_OPENED_EVENT object:userInfo];
}

//iOS 10 前台收到消息
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center  willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(NSInteger))completionHandler {
  NSDictionary * userInfo = notification.request.content.userInfo;
  if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
    // Apns
    NSLog(@"iOS 10 APNS 前台收到消息");
    [JPUSHService handleRemoteNotification:userInfo];
    [[NSNotificationCenter defaultCenter] postNotificationName:J_APNS_NOTIFICATION_ARRIVED_EVENT object:userInfo];
  }
  else {
    // 本地通知 todo
    NSLog(@"iOS 10 本地通知 前台收到消息");
    [[NSNotificationCenter defaultCenter] postNotificationName:J_LOCAL_NOTIFICATION_ARRIVED_EVENT object:userInfo];
  }
  //需要执行这个方法，选择是否提醒用户，有 Badge、Sound、Alert 三种类型可以选择设置
  completionHandler(UNNotificationPresentationOptionAlert);
}

//iOS 10 消息事件回调
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler: (void (^)())completionHandler {
  NSDictionary * userInfo = response.notification.request.content.userInfo;
  if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
    NSLog(@"iOS 10 APNS 消息事件回调");
    // Apns
    [JPUSHService handleRemoteNotification:userInfo];
    // 保障应用被杀死状态下，用户点击推送消息，打开app后可以收到点击通知事件
    [[RCTJPushEventQueue sharedInstance]._notificationQueue insertObject:userInfo atIndex:0];
    [[NSNotificationCenter defaultCenter] postNotificationName:J_APNS_NOTIFICATION_OPENED_EVENT object:userInfo];
  }
  else {
    // 本地通知 todo
    NSLog(@"iOS 10 本地通知 消息事件回调");
    [[NSNotificationCenter defaultCenter] postNotificationName:J_LOCAL_NOTIFICATION_OPENED_EVENT object:userInfo];
  }
  // 系统要求执行这个方法
  completionHandler();
}

//自定义消息
- (void)networkDidReceiveMessage:(NSNotification *)notification {
  NSDictionary * userInfo = [notification userInfo];
  [[NSNotificationCenter defaultCenter] postNotificationName:J_CUSTOM_NOTIFICATION_EVENT object:userInfo];
}

//************************************************JPush end************************************************



//************************************************openURL start************************************************

- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url {
  return  [WXApi handleOpenURL:url delegate:self];
}

- (BOOL)application:(UIApplication *)application
continueUserActivity:(NSUserActivity *)userActivity
 restorationHandler:(void(^)(NSArray<id<UIUserActivityRestoring>> * __nullable
                             restorableObjects))restorationHandler {
  return [WXApi handleOpenUniversalLink:userActivity
                               delegate:self];
}
// Universal Links 配置文件, 没使用的话可以忽略。

// ios 8.x or older
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
  [RCTLinkingManager application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
  // 如果百川处理过会返回YES
  if (![[AlibcTradeSDK sharedInstance] application:application openURL:url sourceApplication:sourceApplication annotation:annotation]) {
      // 处理其他app跳转到自己的app
    [WXApi handleOpenURL:url delegate:self];
  }
  return YES;
}

// ios 9.0+
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url
            options:(NSDictionary<NSString*, id> *)options
{
  // Triggers a callback event.
  // 触发回调事件
  [RCTLinkingManager application:application openURL:url options:options];
  __unused BOOL isHandledByALBBSDK=[[AlibcTradeSDK sharedInstance] application:application openURL:url options:options];
  if(!isHandledByALBBSDK){
    [WXApi handleOpenURL:url delegate:self];
  }
  return YES;
}

@end
