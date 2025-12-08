# Paysafe Payments API Android SDK

## Table of contents

- [Overview](#overview)
- [Setup](#setup)
- [Error Object](#error-object)
- [Card Payments](#card-payments)
- [Google Pay](#google-pay)
- [Venmo](#venmo)
- [Examples](#examples)

## Overview

The Paysafe Android SDK is a flexible and fully customizable mobile SDK that seamlessly
integrates into your mobile app. It handles data security and PCI compliance while giving you the
freedom to customize the entire customer experience to match your mobile app's design. The mobile
SDK takes care of sensitive payment fields (card number, CVV, and expiry date, year, and month),
while keeping data security and PCI compliance in mind.

Paysafe handles the customer input and data storage. The Paysafe Android SDK uses the [Payments
API](https://developer.paysafe.com/en/payments-api/) for processing payments. The SDK is designed with a clear and modular structure to ensure a
seamless implementation experience.

### Advantages

- Paysafe native SDK may provide better PCI scope because of how Paysafe collects the card
  data right from within the mobile app. Please consult with your Qualified Security Assessor to
  determine your PCI level.
- Extensive customization options enable you to customize payment forms that match your
  mobile app design. Create as many translated or localized versions of your payment form as required.
- Embeds naturally and remains invisible.
- Supports processing payments with 3D Secure-enabled cards.
- No redirection to an external webpage for 3DS is required.
- Supports native Android Platform SCA authentication.

### Payment Methods

- Card payments
- Google Pay
- Venmo

## Before you begin

Contact your business relationship manager or email Integrations Support for your Business Portal credentials.
To obtain the Secret API key from the Business Portal:

1. Log in to the Merchant Portal.
2. Go to Developer > API Keys.
3. For the Secret Key, you are required to authenticate once more.
4. When the key is revealed, click the Copy icon to copy the API key.
5. Your API key will have the format `username:password`, for example:

```
MerchantXYZ:B-tst1-0-51ed39e4-312d02345d3f123120881dff9bb4020a89e8ac44cdfdcecd702151182fdc952272661d290ab2e5849e31bb03deede9
```

**Note:**
- Use the same API key for all payment methods.
- The API key is case-sensitive and sent using HTTP Basic Authentication.

For more information, see [Authentication](https://developer.paysafe.com/en/support/reference-information/authentication/).

## Integrating the Android SDK

The Paysafe Android SDK is open source and includes a demo for testing the various functionalities.
It is compatible with apps supporting Android XX or above.

To integrate the Paysafe Payments Android SDK:

**STEP 1:** Add the JitPack repository to your build file. Add it in your root `build.gradle` (or `settings.gradle.kts`) at the end of repositories:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**STEP 2:** In your gradle file, add the dependency that includes all SDK functionalities and integrations:

```kotlin
dependencies {
    implementation("com.github.paysafegroup:paysafe_sdk_android_payments_api:x.y.z")
}
```

If you want to include only one module or specific modules, specify them as shown in the following example:

```kotlin
dependencies {
    implementation("com.github.paysafegroup.paysafe_sdk_android_payments_api:venmo:x.y.z")
    implementation("com.github.paysafegroup.paysafe_sdk_android_payments_api:google-pay:x.y.z")
    implementation("com.github.paysafegroup.paysafe_sdk_android_payments_api:card-payments:x.y.z")
    implementation("com.github.paysafegroup.paysafe_sdk_android_payments_api:threedsecure:x.y.z")
}
```

### Important

For 3DS operations, Paysafe SDK uses Cardinal SDK. It is not a transitive dependency but a direct
dependency (an AAR file) served through the `paysafe-cardinal` module.

If your project already has Cardinal SDK integrated, it will clash with our `paysafe-cardinal` module.
To solve this, you must exclude one of the Cardinal dependencies from your project.

### SDK requirements

- Android 6.0 (API level 23) and above
- Android Gradle Plugin 3.5.1
- Gradle 5.4.1 +
- AndroidX (as of v11.0.0)

## Setup

After the Paysafe Android package is integrated into your project, set up the PaysafeSDK on application start.

```kotlin
import com.paysafe.android.PaysafeSDK

try {
    PaysafeSDK.setup("<api-key>", PSEnvironment.TEST)
} catch (e: SomeException) {
    // Handle Exception
}
```

The setup function creates and initializes the Paysafe Android SDK. Pass the following parameters
during its initialization from the application:

### API Key

The Base64-encoded version of the single-use token API key is used to authenticate with the Payments API.
For more information about obtaining your API Keys, see [Before you begin](#before-you-begin).

Note that this key can only be used to generate single-use tokens and has no other API access rights
(such as for taking payments). Consequently, it can be exposed publicly in the customer's browser.

### Environment

The environment string is used to select the environment for tokenization.
The accepted environments are `PROD` (Paysafe Production environment) and `TEST` (Paysafe Merchant Test or Sandbox environment).

> **Warning:** Do not use real card numbers or other payment instrument details in the Merchant Test environment. Test/ Sandbox is not compliant with Payment Card Industry Data Security Standards (PCI-DSS) and does not protect cardholder/ payee information.

## Error Object

### Callback Error Object Signature

| Parameter | Required | Type | Description |
|-----------|----------|------|-------------|
| `code` | true | String | Error Code |
| `displayMessage` | true | String | Error message for display to customers. |
| `detailedMessage` | true | String | Detailed description of the error (this information should not be displayed to customers). |
| `correlationId` | true | String | Unique error ID to be provided to Paysafe Support during investigation |

## Card Payments

Collecting card payments in your Android app involves creating an object to collect card information,
tokenizing card and payment details, and submitting the payment to Paysafe for processing.

More details related to card payments integration can be referenced at our [Developer Guide](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/card-payments/overview/).

## Google Pay

Paysafe Android SDK allows you to take payments via your Android apps using mobile-based payment methods, such as Google Pay,
that rely on card payments made through the Paysafe Payments API.

More details related to Google Pay payments integration can be referenced at our [Developer Guide](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/google-pay-integration/google-pay-overview/).

## Venmo

Paysafe Android SDK allows you to authenticate payments through Venmo Android App and submit the payment to Paysafe for processing.

More details related to Venmo payments integration can be referenced at our [Developer Guide](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/venmo-integration/).

## Examples

Paysafe Android SDK includes a standalone Examples module for testing all supported Payment Methods (Cards, Google Pay & Venmo).

