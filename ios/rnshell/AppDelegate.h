#import <React/RCTBridgeDelegate.h>
#import <UIKit/UIKit.h>
#import "rnshell-Bridging-Header.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate, RCTBridgeDelegate>

@property (nonatomic, strong) UIWindow *window;

@end
