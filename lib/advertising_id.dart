import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class AdvertisingId {
  static const MethodChannel _channel = const MethodChannel('advertising_id');

  /// Retrieves a unique identifier based on the device's advertising ID.
  ///
  /// This method interacts with the platform (Android/iOS) to fetch the advertising ID.
  /// The method's behavior varies depending on the user's ad tracking preferences and the platform version.
  ///
  /// On Android:
  /// - If ad tracking is enabled, returns the Google Advertising ID.
  /// - For devices with Android 12 and above, if ad tracking is disabled, it fetches the AppSet ID as a fallback.
  /// - For devices below Android 12, if ad tracking is disabled, it returns the Android ID.
  /// - In case of any exceptions, an error is returned with the exception details.
  ///
  /// On iOS:
  /// - For iOS 14 and above, it checks the tracking authorization status.
  ///   - If authorized, returns the IDFA (Identifier for Advertisers).
  ///   - If not determined, it requests tracking authorization and returns the IDFA if subsequently authorized.
  ///   - In other cases (denied/restricted), it returns the IDFV (Identifier for Vendor).
  /// - For earlier versions of iOS, if ad tracking is enabled, it returns the IDFA; otherwise, it returns the IDFV.
  ///
  /// Returns:
  /// A [Future<String?>] representing the device's unique identifier or throw `PlatformException` if it cannot be retrieved.
  static Future<String?> id() async {
    final String? id = await _channel.invokeMethod(
      'getId',
    );
    return id;
  }

  /// Retrieves the advertising identifier (GAID) of the device.
  ///
  /// On iOS, this method can request tracking authorization from the user
  /// if [requestTrackingAuthorization] is set to `true`. This parameter
  /// is ignored on Android.
  ///
  /// Returns a `Future<String?>` that completes with the advertising identifier
  /// as a string, or `null` if it's not available or the user has opted out of
  /// ad tracking.
  static Future<String?> getAdvertisingId([
    bool requestTrackingAuthorization = false,
  ]) async {
    final String? id = await _channel.invokeMethod(
      'getAdvertisingId',
      requestTrackingAuthorization,
    );
    return id;
  }

  /// Checks if the user has limited ad tracking.
  ///
  /// This method returns a `Future<bool?>` which completes with `true` if ad
  /// tracking is limited, `false` otherwise. If the information is not available
  /// or there's an error, it completes with `null`.
  static Future<bool?> get isLimitAdTrackingEnabled async {
    return await _channel.invokeMethod('isLimitAdTrackingEnabled');
  }

  /// Retrieves the Android ID of the device.
  ///
  /// This method is specific to Android devices and will throw a `PlatformException`
  /// with the code 'platform_not_supported' if it's invoked on a non-Android platform.
  ///
  /// Returns a `Future<String?>` that completes with the Android ID as a string,
  /// or `null` if it's not available.
  static Future<String?> getAndroidId() async {
    if (!Platform.isAndroid) {
      throw PlatformException(
        code: 'PlatformNotSupported',
        message: 'Only use this API in android',
      );
    }

    final String? androidId = await _channel.invokeMethod('getAndroidId');
    return androidId;
  }

  /// Retrieves the App Set ID (ASID) for Android devices running Android 12 and above.
  ///
  /// The App Set ID is a privacy-friendly identifier shared by all apps on a device
  /// published by the same developer. This method is specific to Android and will
  /// throw a `PlatformException` if it's invoked on a non-Android platform.
  ///
  /// This method is only applicable for devices with Android 12 (API level 31) and above.
  /// For devices running lower versions of Android, this method will result in an error.
  ///
  /// Returns a `Future<String?>` that completes with the App Set ID as a string, or `null`
  /// if it's not available or the device is running an unsupported Android version.
  static Future<String?> getAppSetId() async {
    if (!Platform.isAndroid) {
      throw PlatformException(
        code: 'PlatformNotSupported',
        message: 'Only use this API in android',
      );
    }
    final String? appSetId = await _channel.invokeMethod('getAppSetId');
    return appSetId;
  }

  /// Retrieves the Identifier for Vendor (IDFV) specific to iOS devices.
  ///
  /// This method is designed specifically for iOS platforms. It fetches the IDFV, which is a unique
  /// identifier for the vendor of the app on a device.
  /// The IDFV is consistent across apps from the same vendor on the same device but varies between different devices and different vendors.
  ///
  /// It is important to note that this method should only be called when the app is running on an iOS device.
  /// If called on a non-iOS platform, it throws a [PlatformException] to indicate the method is not supported on that platform.
  static Future<String?> getIdFv() async {
    if (!Platform.isIOS) {
      throw PlatformException(
        code: 'PlatformNotSupported',
        message: 'Only use this API in IOS',
      );
    }
    final String? appSetId = await _channel.invokeMethod('getIdfv');
    return appSetId;
  }
}
