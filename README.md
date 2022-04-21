# location_service_check

一个检查定位权限是否开启的服务，支持Android和iOS。

功能比较简单，所以之前一直没有管了。这次更新下nullsafety方便直接使用。

## Getting Started
```yaml
dependencies:
  location_service_check: ^1.0.0
```

## How to Use
```dart
import 'package:location_service_check/location_service_check.dart';

void check() {
    bool isOpen = await LocationServiceCheck.checkLocationIsOpen;
}

void _openSetting() async {
  LocationServiceCheck.openLocationSetting();
}

```

