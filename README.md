# Weather App - Ứng Dụng Thời Tiết

## Mô Tả

Ứng dụng thời tiết Android được xây dựng bằng Kotlin, Jetpack Compose và Clean Architecture.

## Tính Năng

- Xem thời tiết hiện tại theo thành phố
- Dự báo thời tiết 5 ngày
- Dự báo theo giờ
- Tìm kiếm thành phố
- Pull-to-refresh để cập nhật dữ liệu
- Giao diện Material Design 3
- Hỗ trợ Dark/Light mode

## Công Nghệ Sử Dụng

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** Clean Architecture (MVVM)
- **DI:** Hilt
- **Networking:** Retrofit + OkHttp
- **Async:** Kotlin Coroutines + Flow
- **Image Loading:** Coil
- **API:** WeatherAPI.com

## Cài Đặt

1. Clone repository
2. Mở project bằng Android Studio
3. Sync Gradle
4. Chạy ứng dụng

## API Key

Để sử dụng, bạn cần thay API key trong file `WeatherApi.kt`:
```kotlin
const val API_KEY = "YOUR_API_KEY"
```

Lấy API key miễn phí tại: https://www.weatherapi.com/

## Cấu Trúc Project

```
app/src/main/java/com/example/weatherapp/
├── data/
│   ├── model/          # Data models
│   ├── remote/        # API interfaces
│   └── repository/    # Repository implementations
├── di/                # Hilt modules
├── domain/
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Business logic
└── ui/
    ├── components/     # Reusable UI components
    ├── screens/       # Screen composables
    └── theme/         # App theme
```
