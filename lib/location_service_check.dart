
import 'dart:async';

import 'package:flutter/services.dart';

class LocationServiceCheck {
  static const MethodChannel _channel =
  MethodChannel('location_service_check');

  /// 获取是否开启了定位服务
  static Future<bool> get checkLocationIsOpen async {
    Map map = await _channel.invokeMethod("checkLocationIsOpen");
    return map["success"];
  }

  /// 打开定位服务设置
  static Future openLocationSetting() async {
    await _channel.invokeMethod("openSetting");
  }

  /// 获取我的定位（经纬度）
  static Future<LocationData> getLocation() async {
    Map res = await _channel.invokeMethod("getLocation");
    double lat = res["latitude"];
    double log = res["longitude"];

    LocationData locationData = LocationData(
        latitude: lat, longitude: log);
    return locationData;
  }
}

/// [latitude] 纬度，[longitude] 经度
class LocationData {
  double? latitude;
  double? longitude;

  LocationData({this.latitude, this.longitude});

  @override
  String toString() => "latitude: $latitude, longitude: $longitude";
}
