import 'dart:async';

import 'package:flutter/services.dart';

class AdvertisingId {
  static const MethodChannel _channel =
      const MethodChannel('advertising_id');

  static Future<String> get id async {
    final String id = await _channel.invokeMethod('getAdvertisingId');
    return id;
  }

  static Future<bool> get isLimitAdTrackingEnabled async {
    return await _channel.invokeMethod('isLimitAdTrackingEnabled');
  }
}
