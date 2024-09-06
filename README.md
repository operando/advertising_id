[![pub package](https://img.shields.io/pub/v/advertising_id.svg)](https://pub.dartlang.org/packages/advertising_id)

# advertising_id

A Flutter plugin to access advertising ID.

Wraps [ASIdentifierManager.advertisingIdentifier](https://developer.apple.com/documentation/adsupport/asidentifiermanager/1614151-advertisingidentifier) (on iOS) and [advertising ID](https://developers.google.com/android/reference/com/google/android/gms/ads/identifier/AdvertisingIdClient) (on Android).

## Getting Started

Run this command
```
 flutter pub add advertising_id
```
This will add a line like this to your package's pubspec.yaml

```yaml
dependencies:
  ....
  advertising_id: ^2.7.1
```

## Usage

### `AdvertisingId.id`

Get advertising ID.

```dart
String? advertisingId;
// Platform messages may fail, so we use a try/catch PlatformException.
try {
  advertisingId = await AdvertisingId.id(true);
} on PlatformException {
  advertisingId = null;
}
```

### `AdvertisingId.isLimitAdTrackingEnabled`

Retrieves whether the user has limit ad tracking enabled or not.

```dart
bool? isLimitAdTrackingEnabled;
// Platform messages may fail, so we use a try/catch PlatformException.
try {
  isLimitAdTrackingEnabled = await AdvertisingId.isLimitAdTrackingEnabled;
} on PlatformException {
  isLimitAdTrackingEnabled = false;
}
```
