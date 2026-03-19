# Change Log

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