#import "LocationServiceCheckPlugin.h"
#import <CoreLocation/CoreLocation.h>

@interface LocationServiceCheckPlugin()<CLLocationManagerDelegate>{
    CLLocationManager *locationmanager;
}
@end

@implementation LocationServiceCheckPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"location_service_check"
            binaryMessenger:[registrar messenger]];
  LocationServiceCheckPlugin* instance = [[LocationServiceCheckPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"checkLocationIsOpen" isEqualToString:call.method]) {
				[self checkLocationIsOpen:result];
    } else if ([@"openSetting" isEqualToString:call.method]) {
				[self openSetting];
		} else if ([@"getLocation" isEqualToString:call.method]) {
				[self getLocation:result];
		} else {
				result(FlutterMethodNotImplemented);
		}
}

- (void)checkLocationIsOpen:(FlutterResult)result {
		NSMutableDictionary *dic = [NSMutableDictionary dictionary];
		if ([CLLocationManager locationServicesEnabled] == YES) {
				dic[@"success"] = @YES;
		} else {
				dic[@"success"] = @NO;
		}
		result(dic);
}

- (void)openSetting {
		NSURL *url;
		if (@available(iOS 10.0, *)) {
			 url = [[NSURL alloc] initWithString:UIApplicationOpenSettingsURLString];
		} else {
			 url = [NSURL URLWithString:@"prefs:root=LOCATION_SERVICES"];
		}
		if ([[UIApplication sharedApplication] canOpenURL:url]) {
				[[UIApplication sharedApplication] openURL:url];
		}

}

- (void)returnMyLocation:(NSMutableDictionary*)dic {
//		[locationChannel invokeMethod:@"receiveLocation" arguments:dic];
}

- (void)getLocation:(FlutterResult)result {
		if ([CLLocationManager locationServicesEnabled]) {
				locationmanager = [[CLLocationManager alloc] init];
				locationmanager.delegate = self;
				
				locationmanager.desiredAccuracy = kCLLocationAccuracyBest;
				locationmanager.distanceFilter = 5.0;
				CLLocation *lastLocation = [locationmanager location];
				NSNumber *latitude = [NSNumber numberWithDouble:lastLocation.coordinate.latitude];
				NSNumber *longitude = [NSNumber numberWithDouble:lastLocation.coordinate.longitude];
				NSMutableDictionary *map = [NSMutableDictionary dictionaryWithObjectsAndKeys:@"latitude", latitude, @"longitude", longitude, nil];
				result(map);
		}
}

#pragma mark CoreLocation delegate
- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
		NSLog(@"定位失败");
}

// 用didUpdateLocations会更准确
- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray<CLLocation *> *)locations{
		[locationmanager stopUpdatingHeading];
		//地址
		CLLocation *currentLocation = [locations lastObject];
		NSMutableDictionary *dic = [NSMutableDictionary init];
		NSNumber *latitude = [NSNumber numberWithDouble:currentLocation.coordinate.latitude];
		NSNumber *longitude = [NSNumber numberWithDouble:currentLocation.coordinate.latitude];
		dic[@"latitude"] = latitude;
		dic[@"longitude"] = longitude;
		
		[self returnMyLocation:dic];
		
}

@end
