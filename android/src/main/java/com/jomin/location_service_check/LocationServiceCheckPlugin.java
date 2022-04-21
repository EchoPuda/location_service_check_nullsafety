package com.jomin.location_service_check;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** LocationServiceCheckPlugin */
public class LocationServiceCheckPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context aContext;
  private LocationManager locationManager;
  private ContentResolver contentResolver;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "location_service_check");
    channel.setMethodCallHandler(this);
    aContext = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    init();
    if ("checkLocationIsOpen".equals(call.method)) {
      checkLocationIsOpen(result);
    } else if ("openSetting".equals(call.method)) {
      openSetting();
    } else if ("getLocation".equals(call.method)) {
      getLocation(result);
    } else {
      result.notImplemented();
    }
  }

  /**
   * 初始化
   */
  private void init() {
    if (locationManager == null) {
      locationManager = (LocationManager) aContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }
    if (contentResolver == null) {
      contentResolver = aContext.getApplicationContext().getContentResolver();
    }
  }

  /**
   * 检查定位服务是否开启
   */
  private void checkLocationIsOpen(Result result) {
    Map<String, Object> map = new HashMap<>();
    if (isLocationEnabled()) {
      map.put("success", true);
    } else {
      map.put("success", false);
    }
    result.success(map);
  }

  /**
   * 返回定位服务开启状态
   */
  private boolean isLocationEnabled() {
    int locationMode;
    String locationProviders;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      try {
        locationMode = Settings.Secure.getInt(contentResolver,Settings.Secure.LOCATION_MODE);
      } catch (Settings.SettingNotFoundException e) {
        e.printStackTrace();
        return false;
      }
      return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    } else {
      locationProviders = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
      return !TextUtils.isEmpty(locationProviders);
    }
  }

  /**
   * 打开定位服务设置页
   */
  private void openSetting() {
    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
    aContext.startActivity(intent);
  }

  /**
   * 获取定位信息
   */
  private void getLocation(Result result) {
    Location location = getMyLocation();
    HashMap<String, Object> map = new HashMap<>();
    if (location != null) {
      map.put("latitude", location.getLatitude());
      map.put("longitude", location.getLongitude());
    } else {
      map.put("error", "location data is null");
    }
    result.success(map);
  }

  private Location getMyLocation() {
    if (ActivityCompat.checkSelfPermission(aContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(aContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return null;
    }
    List<String> providers = locationManager.getAllProviders();
    Location bestLocation = null;
    for (String provider : providers) {

      Location l = locationManager.getLastKnownLocation(provider);
      if (l == null) {
        continue;
      }
      if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
        // Found best last known location: %s", l);
        bestLocation = l;
      }
    }
    return bestLocation;
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
