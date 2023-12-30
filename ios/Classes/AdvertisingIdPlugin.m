#import "AdvertisingIdPlugin.h"

#if __has_include(<advertising_id/advertising_id-Swift.h>)
#import <advertising_id/advertising_id-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "advertising_id-Swift.h"
#endif

@implementation AdvertisingIdPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAdvertisingIdPlugin registerWithRegistrar:registrar];
}
@end
