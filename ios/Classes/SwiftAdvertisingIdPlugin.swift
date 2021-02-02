import Flutter
import UIKit
import AdSupport
import AppTrackingTransparency

public class SwiftAdvertisingIdPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "advertising_id", binaryMessenger: registrar.messenger())
        let instance = SwiftAdvertisingIdPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "getAdvertisingId":
            let manager = ASIdentifierManager.shared()
            if #available(iOS 14.0, *) {
                if (ATTrackingManager.trackingAuthorizationStatus == ATTrackingManager.AuthorizationStatus.authorized) {
                    result(manager.advertisingIdentifier.uuidString)
                } else if (call.arguments as? Bool ?? false){
                    ATTrackingManager.requestTrackingAuthorization { status in
                        if (status == ATTrackingManager.AuthorizationStatus.authorized) {
                            result(manager.advertisingIdentifier.uuidString)
                        } else {
                            result("")
                        }
                    }
                } else {
                    result("")
                }
            } else {
                var idfaString: String!
                if manager.isAdvertisingTrackingEnabled {
                    idfaString = manager.advertisingIdentifier.uuidString
                } else {
                    idfaString = ""
                }
                result(idfaString)
            }            
        case "isLimitAdTrackingEnabled":
            if #available(iOS 14.0, *) {
                result(ATTrackingManager.trackingAuthorizationStatus == ATTrackingManager.AuthorizationStatus.authorized)
            } else {
                result(ASIdentifierManager.shared().isAdvertisingTrackingEnabled)
            }
        default:
            result(nil)
        }
    }
}
