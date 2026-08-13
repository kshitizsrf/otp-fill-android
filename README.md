# OTP Fill Android

A reusable, auto-filling OTP input for Jetpack Compose. Drop it into any screen with a single function call — each digit animates into its own box, and it auto-fills from incoming SMS codes with zero extra setup.

![Kotlin](https://img.shields.io/badge/Kotlin-2.4-purple)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.08-blue)
![License](https://img.shields.io/badge/license-MIT-green)

## Features

- 🔢 Configurable number of digits (default: 4)
- ✨ Smooth small-to-big spring animation per digit
- 📩 Native SMS OTP auto-fill — no extra library or permissions needed
- 🎨 Fully customizable: box color, text color, corner radius, border colors, size, spacing
- 📦 Single reusable composable — no boilerplate

## Preview

| Empty | Typing | Auto-filled |
|-------|--------|-------------|
| `[ ][ ][ ][ ]` | `[1][ ][ ][ ]` → animates | `[1][2][3][4]` |

## Installation

Add JitPack to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.kshitizsrf:otp-fill-android:1.0.0")
}
```

## Usage

```kotlin
OtpView(
    otpLength = 4,
    cornerRadius = 12.dp,
    boxColor = Color.White,
    textColor = Color(0xFFE91E63),
    onOtpComplete = { otp ->
        // verify otp with your backend
    }
)
```

That's it — no state management, no manual focus handling, no autofill wiring required.

## Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `otpLength` | `Int` | `4` | Number of digits |
| `boxSize` | `Dp` | `48.dp` | Width/height of each box |
| `boxSpacing` | `Dp` | `10.dp` | Gap between boxes |
| `cornerRadius` | `Dp` | `12.dp` | Corner radius of each box |
| `boxColor` | `Color` | `Color.White` | Background color of each box |
| `textColor` | `Color` | Pink `#E91E63` | Color of the digit text |
| `borderColor` | `Color` | Light gray | Border color of empty/inactive boxes |
| `focusedBorderColor` | `Color` | `textColor` | Border color of the currently active box |
| `fontSize` | `TextUnit` | `22.sp` | Size of the digit text |
| `autoFocus` | `Boolean` | `true` | Requests focus and opens the keyboard on appear |
| `onOtpComplete` | `(String) -> Unit` | `{}` | Called once, with the full code, when the last digit is entered |

## How auto-fill works

`OtpView` registers an `AutofillNode` with `AutofillType.SmsOtpCode`, which is the standard Compose Autofill API. When your device's autofill service (e.g. "Autofill with Google") detects an OTP in an incoming SMS, it offers to fill the field automatically — no `SmsRetriever`, broadcast receiver, or SMS permission required.

> Auto-fill depends on the device having a compatible autofill service enabled (Settings → System → Languages & input → Advanced → Autofill service). It generally doesn't work on emulators without Google Play Services configured.

## Example

See [`OtpUsageExample.kt`](./otpview/src/main/java/com/example/otpview/OtpUsageExample.kt) for a full screen example with a 6-digit variant.

## License

MIT — free to use in personal and commercial projects.
