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
        case "getId":
            handlegetId(call: call, result: result)
        case "getAdvertisingId":
            handleGetAdvertisingId(call: call, result: result)
        case "isLimitAdTrackingEnabled":
            handleIsLimitAdTrackingEnabled(result: result)
        case "getIdfv":
            handleGetIdfv(result: result)
        default:
            result(FlutterError(code: "UNAVAILABLE",
                                message: "Method not available",
                                details: nil))
        }
    }


    private func handlegetId(call: FlutterMethodCall, result: @escaping FlutterResult) {
        let manager = ASIdentifierManager.shared()
        if #available(iOS 14.0, *) {
            // For iOS 14 and above, check the tracking authorization status
            let status = ATTrackingManager.trackingAuthorizationStatus
            switch status {
            case .authorized:
                // If authorized, return the IDFA
                result(manager.advertisingIdentifier.uuidString)
            case .notDetermined:
                // If not determined, request authorization
                ATTrackingManager.requestTrackingAuthorization { newStatus in
                    if newStatus == .authorized {
                        // If authorized after request, return the IDFA
                        result(manager.advertisingIdentifier.uuidString)
                    } else {
                        // If still not authorized, return the IDFV
                        result(UIDevice.current.identifierForVendor?.uuidString ?? "")
                    }
                }
            default:
                // If denied or restricted, return the IDFV
                result(UIDevice.current.identifierForVendor?.uuidString ?? "")
            }
        } else {
            // For earlier iOS versions
            if manager.isAdvertisingTrackingEnabled {
                // If tracking is enabled, return the IDFA
                result(manager.advertisingIdentifier.uuidString)
            } else {
                // If tracking is not enabled, return the IDFV
                result(UIDevice.current.identifierForVendor?.uuidString ?? "")
            }
        }
    }



    private func handleGetAdvertisingId(call: FlutterMethodCall, result: @escaping FlutterResult) {
        let manager = ASIdentifierManager.shared()
        if #available(iOS 14.0, *) {
            handleiOS14AdvertisingId(call: call, manager: manager, result: result)
        } else {
            result(manager.isAdvertisingTrackingEnabled ? manager.advertisingIdentifier.uuidString : "")
        }
    }

    @available(iOS 14.0, *)
    private func handleiOS14AdvertisingId(call: FlutterMethodCall, manager: ASIdentifierManager, result: @escaping FlutterResult) {
        let status = ATTrackingManager.trackingAuthorizationStatus
        if status == .authorized {
            result(manager.advertisingIdentifier.uuidString)
        } else if call.arguments as? Bool ?? false {
            ATTrackingManager.requestTrackingAuthorization { status in
                result(status == .authorized ? manager.advertisingIdentifier.uuidString : "")
            }
        } else {
            result("")
        }
    }

    private func handleIsLimitAdTrackingEnabled(result: @escaping FlutterResult) {
        if #available(iOS 14.0, *) {
            result(ATTrackingManager.trackingAuthorizationStatus != .authorized)
        } else {
            result(!ASIdentifierManager.shared().isAdvertisingTrackingEnabled)
        }
    }

    private func handleGetIdfv(result: @escaping FlutterResult) {
        result(UIDevice.current.identifierForVendor?.uuidString ?? "")
    }

}
