# Change Log

## [2.2.0] - 2026-04-01

### Fixed

- **ABI crash**: Replaced experimental `LocalSoftwareKeyboardController` with stable `LocalFocusManager` in all field composables to prevent runtime crashes across Compose versions (#1)
- **Placeholder never shown**: Pass `null` for `label` lambda when `animateTopLabelText = false` so `OutlinedTextField` displays placeholder text correctly (#2)
- **Empty CVV hint**: Set `card_cvv_hint` to `"CVV"` and aligned `PSCvvView.provideLabelText()` fallback to use the string resource instead of a hardcoded string (#4)
- **Event handler inconsistency**: Standardized `PSExpiryDateText` to use `PSCardFieldEventHandler` instead of a raw lambda, matching all other field composables (#5)

### Added

- **`BLUR` event**: Added `PSCardFieldInputEvent.BLUR` emitted when a field loses focus, enabling blur-based validation UX (#6)
- **Error UX controls**: Added `clearsErrorOnInput` and `validatesEmptyFieldOnBlur` properties to opt into clearing errors on typing and skipping validation for empty fields (#7)
- **Hide card brand icon**: Added `showBrandIcon` flag on `PSCardNumberView` to suppress the built-in trailing card brand icon (#8)
- **`resetOnTokenize` opt-out**: Added `resetOnTokenize` property on `PSCardFormController` (default `true`) to preserve field values after tokenization for retry UX, matching iOS SDK (#9)
- **Focused border width control**: Added `uniformBorderWidth` option to render a consistent 1dp border in all states instead of Material 3's default 2dp focused border (#10)
- **Custom field height**: Added `compactFieldHeight` property to override the 56dp minimum height for compact layouts (#11)
- **`clearsFocusOnReset` opt-out**: Added `clearsFocusOnReset` property (default `true`) on all field views to prevent `reset()` from dismissing the keyboard and clearing focus (#12)

## [2.1.1] - 2026-03-24

### Updated

#### ⚠️ CRITICAL: Braintree SDK Upgrade to 4.45.0 (SSL Certificate Update Required)

**Action Required:** Upgraded the Braintree SDK dependency from version 4.39.0 to 4.45.0 to address expiring SSL certificates that will cause processing interruptions after March 30, 2026.

**Why This Update Is Critical:**
- Braintree's SSL certificates expire on **March 30, 2026**
- Without this update, Venmo payment processing will fail after the expiration date
- This update includes the renewed SSL certificates to ensure uninterrupted service

**Recommendation:** Update to SDK version 2.1.1 immediately to avoid payment processing interruptions.

##### Braintree SDK Changes (from Braintree release notes)

**Version 4.45.0:**
- Updated expiring pinned vendor SSL certificates
- Added Google Play Store rejection fixes with `hasUserLocationConsent` property support
- Bumped PayPalNativeCheckout to version 1.3.2
- Bumped Magnes version to 5.5.1

**Version 4.44.0:**
- Fixes for Google Play Store rejection
- Added `hasUserLocationConsent` property to LocalPayment, PayPal, and DataCollector APIs
- Bumped Magnes SDK to version 5.5.0
- Added new `GooglePayClient#isReadyToPay()` method

**Version 4.43.0:**
- Fixed metadata sending bug in Venmo SDK
- Resolved Cardinal.getInstance memory leak in ThreeDSecure

**Version 4.42.0:**
- Added `setIsFinalAmount()` and `setFallbackToWeb()` to VenmoRequest
- Web-based Venmo fallback support using App Links
- Upgraded data-collector SDK to version 3.21.0 for Google Play policy compliance

**Version 4.41.0:**
- Added new PayPalLineItem properties: `imageUrl`, `upcCode`, `upcType`
- Bumped native-checkout to version 1.2.1

**Version 4.40.0 & 4.40.1:**
- Fixed NPE when VenmoListener is null
- Fixed inaccurate PayPal error messages
- Bumped play-services-wallet to 19.2.1
- Added `totalPriceLabel` to GooglePayRequest
- Added `setUserAuthenticationEmail()` to PayPalNativeRequest

##### Impact

This is a dependency upgrade with no changes to the Paysafe SDK API. The update is backward-compatible with no breaking changes. The Venmo integration benefits from improved stability, bug fixes, and enhanced Google Play Store compliance provided by the Braintree SDK.

## [2.1.0] - 2026-03-17

### Added

#### Card Field Label Text Customization

Added the ability to customize label text for all card input fields, providing greater flexibility for UI customization and localization.

##### New Features

**Label Text Customization Support:**
- Added `label_text` XML attribute for all card input fields
- Added programmatic `labelText` property (getter/setter) for runtime customization
- Reactive UI updates when label text is changed programmatically
- Backward compatible with sensible defaults

**Supported Fields:**
1. **PSCardNumberView** - Default: "Card number"
2. **PSCardholderNameView** - Default: "Name on card"
3. **PSExpiryDatePickerView** - Default: "Expiry date"
4. **PSExpiryDateTextView** - Default: "Expiry date"
5. **PSCvvView** - Default: "CVV"

##### Usage Examples

**XML Configuration:**
```xml
<com.paysafe.android.hostedfields.cardnumber.PSCardNumberView
    android:id="@+id/creditCardNumberField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:label_text="Card Number" />

<com.paysafe.android.hostedfields.holdername.PSCardholderNameView
    android:id="@+id/creditCardHolderNameField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:label_text="Cardholder Name" />

<com.paysafe.android.hostedfields.expirydate.PSExpiryDatePickerView
    android:id="@+id/creditCardExpiryDateField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:label_text="Expiration Date" />

<com.paysafe.android.hostedfields.cvv.PSCvvView
    android:id="@+id/creditCardCvvField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:label_text="Security Code" />
```

**Programmatic Customization:**
```kotlin
// Set label text programmatically
binding.creditCardNumberField.labelText = "Card Number"
binding.creditCardHolderNameField.labelText = "Name on Card"
binding.creditCardExpiryDateField.labelText = "Expiry"
binding.creditCardCvvField.labelText = "CVV Code"

// Dynamic updates based on card type
cardController?.cardTypeLiveData?.observe(viewLifecycleOwner) { cardType ->
    binding.creditCardCvvField.labelText = when (cardType) {
        PSCreditCardType.AMEX -> "CID (4 digits)"
        else -> "CVV (3 digits)"
    }
}

// Localization support
when (Locale.getDefault().language) {
    "es" -> {
        binding.creditCardNumberField.labelText = "Número de tarjeta"
        binding.creditCardExpiryDateField.labelText = "Fecha de vencimiento"
    }
    "fr" -> {
        binding.creditCardNumberField.labelText = "Numéro de carte"
        binding.creditCardExpiryDateField.labelText = "Date d'expiration"
    }
}
```

##### Benefits

- **Flexibility**: Customize labels via XML or programmatically at runtime
- **Localization**: Easy support for multiple languages
- **Dynamic Updates**: UI automatically reflects label text changes
- **Backward Compatible**: Existing implementations continue to work with default labels
- **Consistent API**: Same pattern across all card input fields

##### Technical Details

- All fields use `mutableStateOf` for reactive state management
- Label text is read from XML attributes during view initialization
- Programmatic updates trigger immediate UI recomposition
- Default values sourced from string resources for easy localization

### Payment Methods Coverage

- [Google Pay](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/google-pay-integration/google-pay-overview/)
- [Cards](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/card-payments/overview/)
- [Venmo](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/venmo-integration/)

## [2.0.0]

### Updated

#### Cardinal SDK Upgrade to 2.2.7-8

We have upgraded the Cardinal Mobile SDK from version 2.2.7-5 to 2.2.7-8, which includes several important updates and requires action from integrating clients.

##### What's New in Cardinal SDK 2.2.7-8

**Version 2.2.7-8 (Bug Fix Release):**
- Fixed issue where ipAddress was not present in DeviceData

**Version 2.2.7-7 (13 Mar 2025):**
- Added support for Android 15
- Added Edge-to-Edge UI support for challenge screens (HTML and Native Views)
- Added new configuration to allow traffic routing to either Cardinal Data Center or Visa Data Center

**Version 2.2.7-6 (05 Dec 2024):**
- Added new configuration for data center routing (Cardinal DC or Visa DC)
- Bug fix: resolved issue where ipAddress was not present in DeviceData

##### Breaking Changes

**⚠️ CRITICAL: Cardinal Observer Initialization Required in Activity**

The Cardinal SDK upgrade requires a Cardinal observer to be initialized before the Activity/Fragment reaches the RESUMED state. We have updated our SDK to handle this internally, but **you must add one line to your Activity**.

**What You Must Add to Your Activity if you use only card-payments or the whole mobile SDK:**

In your Activity's `onCreate()` method, you must call `PSCardFormController.addCardinalObserver(this)` before calling `setContentView()`.

**Requirements:**
1. **Activity Base Class**: Your Activity must extend `FragmentActivity` (or `AppCompatActivity` which extends `FragmentActivity`)
2. **Initialization Timing**: ⚠️ Call `addCardinalObserver(this)` in `onCreate()` **BEFORE the Activity reaches the RESUMED state**

**Implementation Example (As Applied in Our Sample Activity):**

```kotlin
class YourActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // REQUIRED: Add this line to initialize Cardinal observer
        PSCardFormController.addCardinalObserver(this)
        
        setContentView(R.layout.your_activity_layout)
    }
}
```

##### Client Responsibilities

To integrate this upgrade, clients must:

1. **Update Activity Base Class**: Ensure your Activity extends `FragmentActivity` or `AppCompatActivity` (which already extends `FragmentActivity`)
2. **Add Cardinal Observer**: Add the single line `PSCardFormController.addCardinalObserver(this)` in your Activity's `onCreate()` method, before `setContentView()`
3. **Initialization Timing**: ⚠️ **CRITICAL** - Call `addCardinalObserver(this)` before the Activity reaches the RESUMED state (i.e., in `onCreate()`)
4. **Test on Android 15**: Verify your 3DS challenge flows work correctly on Android 15 devices
5. **Test Edge-to-Edge UI**: Validate that challenge screens display correctly with the new Edge-to-Edge UI support

##### Technical Notes

- The SDK internally handles the Cardinal observer setup and lifecycle management
- The default CardinalDataCenter setting is now Visa Data Center
- Cross-DC authentications (between Cardinal and Visa data centers) are supported

### Payment Methods Coverage

- [Google Pay](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/google-pay-integration/google-pay-overview/)
- [Cards](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/card-payments/overview/)
- [Venmo](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/venmo-integration/)

## [1.0.1] - 2026-01-21

### Patch release

## Add CHANGELOG content into release notes

### Payment Methods Coverage

- [Google Pay](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/google-pay-integration/google-pay-overview/)
- [Cards](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/card-payments/overview/)
- [Venmo](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/venmo-integration/)

## [1.0.0] - 2025-12-02

### Major release

### Payment Methods Coverage

- [Google Pay](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/google-pay-integration/google-pay-overview/)
- [Cards](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/card-payments/overview/)
- [Venmo](https://developer.paysafe.com/en/api-docs/mobile-sdks-payments-api/paysafe-android-sdk/venmo-integration/)