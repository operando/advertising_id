#import "AdvertisingIdPlugin.h"
#import <advertising_id/advertising_id-Swift.h>

@implementation AdvertisingIdPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAdvertisingIdPlugin registerWithRegistrar:registrar];
}
@end
