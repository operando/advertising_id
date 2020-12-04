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
                ATTrackingManager.requestTrackingAuthorization { status in
                    var idfaString = ""
                    switch status {
                        case .authorized:
                            idfaString = manager.advertisingIdentifier.uuidString
                            break
                        @unknown default:
                            break
                    }
                    result(idfaString) 
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
            result(ASIdentifierManager.shared().isAdvertisingTrackingEnabled)
        default:
            result(nil)
        }
    }
}
