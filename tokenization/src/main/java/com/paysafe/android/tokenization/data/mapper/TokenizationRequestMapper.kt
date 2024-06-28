/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.mapper

import com.paysafe.android.tokenization.data.entity.paymentHandle.AuthenticationPurposeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.BillingCycleSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.ElectronicDeliverySerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.MerchantDescriptorSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.MessageCategorySerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentTypeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.PurchasedGiftCardDetailsSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.RequestorChallengePreferenceSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.ReturnLinkRelationSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.ReturnLinkSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.ThreeDSProfileSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.ThreeDSSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.TransactionIntentSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.TransactionTypeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.UserAccountDetailsSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.AuthenticationMethodSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.ChangedRangeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.CreatedRangeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.InitialUsageRangeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.PasswordChangeRangeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.PaymentAccountDetailsSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.PriorThreeDSAuthenticationSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.ShippingDetailsUsageSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.ThreeDSAuthenticationSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.TravelDetailsSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.UserLoginSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay.BillingAddressRequest
import com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay.GooglePayPaymentTokenRequest
import com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay.GooglePayRequest
import com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay.InfoRequest
import com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay.PaymentMethodDataRequest
import com.paysafe.android.tokenization.data.entity.paymentHandle.googlepay.TokenizationDataRequest
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.DateOfBirthSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.GenderSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.IdentityDocumentSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.ProfileLocaleSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.ProfileSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.BillingDetailsRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.CardExpiryRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.CardRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.OrderItemDetailsSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.ShippingDetailsSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.ShippingMethodSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.venmo.VenmoRequestSerializable
import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.CardExpiryRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.CardRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleReturnLink
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentType
import com.paysafe.android.tokenization.domain.model.paymentHandle.ReturnLinkRelation
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingMethod
import com.paysafe.android.tokenization.domain.model.paymentHandle.ThreeDSProfile
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.UserAccountDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.AuthenticationMethod
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.ChangedRange
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.CreatedRange
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.InitialUsageRange
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.PasswordChangeRange
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.PaymentAccountDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.PriorThreeDSAuthentication
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.ShippingDetailsUsage
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.ThreeDSAuthentication
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.TravelDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.UserLogin
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GoogleBillingAddress
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GoogleCardInfo
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GooglePayPaymentToken
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GooglePaymentMethodData
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GoogleTokenizationData
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.DateOfBirth
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Gender
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.IdentityDocument
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.ProfileLocale
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.AuthenticationPurpose
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.BillingCycle
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.ElectronicDelivery
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.MessageCategory
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.OrderItemDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.PurchasedGiftCardDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.RequestorChallengePreference
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.ThreeDS
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.TransactionIntent
import com.paysafe.android.tokenization.domain.model.paymentHandle.venmo.VenmoRequest

fun TransactionType.toData() = when (this) {
    TransactionType.PAYMENT -> TransactionTypeSerializable.PAYMENT
    TransactionType.STANDALONE_CREDIT -> TransactionTypeSerializable.STANDALONE_CREDIT
    TransactionType.ORIGINAL_CREDIT -> TransactionTypeSerializable.ORIGINAL_CREDIT
    TransactionType.VERIFICATION -> TransactionTypeSerializable.VERIFICATION
}

fun ReturnLinkRelation.toData() = when (this) {
    ReturnLinkRelation.DEFAULT -> ReturnLinkRelationSerializable.DEFAULT
    ReturnLinkRelation.ON_COMPLETED -> ReturnLinkRelationSerializable.ON_COMPLETED
    ReturnLinkRelation.ON_FAILED -> ReturnLinkRelationSerializable.ON_FAILED
    ReturnLinkRelation.ON_CANCELLED -> ReturnLinkRelationSerializable.ON_CANCELLED
}

fun PaymentHandleReturnLink.toData() = ReturnLinkSerializable(
    relation = relation.toData(),
    href = href,
    method = method
)

fun ProfileLocale.toData() = when (this) {
    ProfileLocale.CA_EN -> ProfileLocaleSerializable.CA_EN
    ProfileLocale.EN_US -> ProfileLocaleSerializable.EN_US
    ProfileLocale.FR_CA -> ProfileLocaleSerializable.FR_CA
    ProfileLocale.EN_GB -> ProfileLocaleSerializable.EN_GB
}

fun Gender.toData() = when (this) {
    Gender.MALE -> GenderSerializable.MALE
    Gender.FEMALE -> GenderSerializable.FEMALE
}

fun DateOfBirth.toData() = DateOfBirthSerializable(
    day = day,
    month = month,
    year = year
)

fun IdentityDocument.toData() = IdentityDocumentSerializable(
    type = "SOCIAL_SECURITY",
    documentNumber = documentNumber
)

fun Profile.toData() = ProfileSerializable(
    firstName = firstName,
    lastName = lastName,
    locale = locale?.toData(),
    merchantCustomerId = merchantCustomerId,
    dateOfBirth = dateOfBirth?.toData(),
    email = email,
    phone = phone,
    mobile = mobile,
    gender = gender?.toData(),
    nationality = nationality,
    identityDocuments = identityDocuments?.map { it.toData() }
)

fun BillingDetails.toData() = BillingDetailsRequestSerializable(
    city = city,
    country = country,
    nickName = nickName,
    state = state,
    street = street,
    street1 = street1,
    street2 = street2,
    phone = phone,
    zip = zip
)


internal fun CardExpiryRequest.toData() = CardExpiryRequestSerializable(
    month = month.toIntOrNull(),
    year = year.toIntOrNull()
)

internal fun CardRequest.toData() = CardRequestSerializable(
    cardNum = cardNum,
    cardExpiry = cardExpiry?.toData(),
    cvv = cvv,
    holderName = holderName,
)

internal fun PaymentType.toData() = when (this) {
    PaymentType.CARD -> PaymentTypeSerializable.CARD
    PaymentType.VENMO -> PaymentTypeSerializable.VENMO
}

internal fun MerchantDescriptor.toData() = MerchantDescriptorSerializable(
    dynamicDescriptor = dynamicDescriptor,
    phone = phone
)

internal fun ShippingMethod.toData() = when (this) {
    ShippingMethod.NEXT_DAY_OR_OVERNIGHT -> ShippingMethodSerializable.NEXT_DAY_OR_OVERNIGHT
    ShippingMethod.TWO_DAY_SERVICE -> ShippingMethodSerializable.TWO_DAY_SERVICE
    ShippingMethod.LOWEST_COST -> ShippingMethodSerializable.LOWEST_COST
    ShippingMethod.OTHER -> ShippingMethodSerializable.OTHER
}

internal fun ShippingDetails.toData() = ShippingDetailsSerializable(
    shipMethod = shipMethod?.toData(),
    street = street,
    street2 = street2,
    city = city,
    state = state,
    countryCode = countryCode,
    zip = zip
)

internal fun MessageCategory.toData() = when (this) {
    MessageCategory.PAYMENT -> MessageCategorySerializable.PAYMENT
    MessageCategory.NON_PAYMENT -> MessageCategorySerializable.NON_PAYMENT
}

internal fun TransactionIntent.toData() = when (this) {
    TransactionIntent.GOODS_OR_SERVICE_PURCHASE -> TransactionIntentSerializable.GOODS_OR_SERVICE_PURCHASE
    TransactionIntent.CHECK_ACCEPTANCE -> TransactionIntentSerializable.CHECK_ACCEPTANCE
    TransactionIntent.ACCOUNT_FUNDING -> TransactionIntentSerializable.ACCOUNT_FUNDING
    TransactionIntent.QUASI_CASH_TRANSACTION -> TransactionIntentSerializable.QUASI_CASH_TRANSACTION
    TransactionIntent.PREPAID_ACTIVATION -> TransactionIntentSerializable.PREPAID_ACTIVATION
}

internal fun AuthenticationPurpose.toData() = when (this) {
    AuthenticationPurpose.PAYMENT_TRANSACTION -> AuthenticationPurposeSerializable.PAYMENT_TRANSACTION
    AuthenticationPurpose.RECURRING_TRANSACTION -> AuthenticationPurposeSerializable.RECURRING_TRANSACTION
    AuthenticationPurpose.INSTALMENT_TRANSACTION -> AuthenticationPurposeSerializable.INSTALMENT_TRANSACTION
    AuthenticationPurpose.ADD_CARD -> AuthenticationPurposeSerializable.ADD_CARD
    AuthenticationPurpose.MAINTAIN_CARD -> AuthenticationPurposeSerializable.MAINTAIN_CARD
    AuthenticationPurpose.EMV_TOKEN_VERIFICATION -> AuthenticationPurposeSerializable.EMV_TOKEN_VERIFICATION
}

internal fun BillingCycle.toData() = BillingCycleSerializable(
    endDate = endDate,
    frequency = frequency
)

internal fun RequestorChallengePreference.toData() = when (this) {
    RequestorChallengePreference.CHALLENGE_MANDATED -> RequestorChallengePreferenceSerializable.CHALLENGE_MANDATED
    RequestorChallengePreference.CHALLENGE_REQUESTED -> RequestorChallengePreferenceSerializable.CHALLENGE_REQUESTED
    RequestorChallengePreference.NO_PREFERENCE -> RequestorChallengePreferenceSerializable.NO_PREFERENCE
}

internal fun ElectronicDelivery.toData() = ElectronicDeliverySerializable(
    isElectronicDelivery = isElectronicDelivery,
    email = email
)

internal fun ThreeDSProfile.toData() = ThreeDSProfileSerializable(
    email = email,
    phone = phone,
    cellPhone = cellPhone
)

internal fun OrderItemDetails.toData() = OrderItemDetailsSerializable(
    preOrderItemAvailabilityDate = preOrderItemAvailabilityDate,
    preOrderPurchaseIndicator = preOrderPurchaseIndicator,
    reorderItemsIndicator = reorderItemsIndicator,
    shippingIndicator = shippingIndicator
)

internal fun PurchasedGiftCardDetails.toData() = PurchasedGiftCardDetailsSerializable(
    amount = amount,
    count = count,
    currency = currency
)

internal fun CreatedRange.toData() = when (this) {
    CreatedRange.DURING_TRANSACTION -> CreatedRangeSerializable.DURING_TRANSACTION
    CreatedRange.NO_ACCOUNT -> CreatedRangeSerializable.NO_ACCOUNT
    CreatedRange.LESS_THAN_THIRTY_DAYS -> CreatedRangeSerializable.LESS_THAN_THIRTY_DAYS
    CreatedRange.THIRTY_TO_SIXTY_DAYS -> CreatedRangeSerializable.THIRTY_TO_SIXTY_DAYS
    CreatedRange.MORE_THAN_SIXTY_DAYS -> CreatedRangeSerializable.MORE_THAN_SIXTY_DAYS
}

internal fun ChangedRange.toData() = when (this) {
    ChangedRange.DURING_TRANSACTION -> ChangedRangeSerializable.DURING_TRANSACTION
    ChangedRange.LESS_THAN_THIRTY_DAYS -> ChangedRangeSerializable.LESS_THAN_THIRTY_DAYS
    ChangedRange.THIRTY_TO_SIXTY_DAYS -> ChangedRangeSerializable.THIRTY_TO_SIXTY_DAYS
    ChangedRange.MORE_THAN_SIXTY_DAYS -> ChangedRangeSerializable.MORE_THAN_SIXTY_DAYS
}

internal fun PasswordChangeRange.toData() = when (this) {
    PasswordChangeRange.MORE_THAN_SIXTY_DAYS -> PasswordChangeRangeSerializable.MORE_THAN_SIXTY_DAYS
    PasswordChangeRange.NO_CHANGE -> PasswordChangeRangeSerializable.NO_CHANGE
    PasswordChangeRange.DURING_TRANSACTION -> PasswordChangeRangeSerializable.DURING_TRANSACTION
    PasswordChangeRange.LESS_THAN_THIRTY_DAYS -> PasswordChangeRangeSerializable.LESS_THAN_THIRTY_DAYS
    PasswordChangeRange.THIRTY_TO_SIXTY_DAYS -> PasswordChangeRangeSerializable.THIRTY_TO_SIXTY_DAYS
}

internal fun InitialUsageRange.toData() = when (this) {
    InitialUsageRange.CURRENT_TRANSACTION -> InitialUsageRangeSerializable.CURRENT_TRANSACTION
    InitialUsageRange.LESS_THAN_THIRTY_DAYS -> InitialUsageRangeSerializable.LESS_THAN_THIRTY_DAYS
    InitialUsageRange.THIRTY_TO_SIXTY_DAYS -> InitialUsageRangeSerializable.THIRTY_TO_SIXTY_DAYS
    InitialUsageRange.MORE_THAN_SIXTY_DAYS -> InitialUsageRangeSerializable.MORE_THAN_SIXTY_DAYS
}

internal fun ShippingDetailsUsage.toData() = ShippingDetailsUsageSerializable(
    cardHolderNameMatch = cardHolderNameMatch,
    initialUsageDate = initialUsageDate,
    initialUsageRange = initialUsageRange?.toData()
)

internal fun PaymentAccountDetails.toData() = PaymentAccountDetailsSerializable(
    createdDate = createdDate,
    createdRange = createdRange?.toData()
)

internal fun AuthenticationMethod.toData() = when (this) {
    AuthenticationMethod.THIRD_PARTY_AUTHENTICATION -> AuthenticationMethodSerializable.THIRD_PARTY_AUTHENTICATION
    AuthenticationMethod.NO_LOGIN -> AuthenticationMethodSerializable.NO_LOGIN
    AuthenticationMethod.INTERNAL_CREDENTIALS -> AuthenticationMethodSerializable.INTERNAL_CREDENTIALS
    AuthenticationMethod.FEDERATED_ID -> AuthenticationMethodSerializable.FEDERATED_ID
    AuthenticationMethod.ISSUER_CREDENTIALS -> AuthenticationMethodSerializable.ISSUER_CREDENTIALS
    AuthenticationMethod.FIDO_AUTHENTICATOR -> AuthenticationMethodSerializable.FIDO_AUTHENTICATOR
}

internal fun UserLogin.toData() = UserLoginSerializable(
    data = data,
    authenticationMethod = authenticationMethod?.toData(),
    time = time
)

internal fun ThreeDSAuthentication.toData() = when (this) {
    ThreeDSAuthentication.FRICTIONLESS_AUTHENTICATION -> ThreeDSAuthenticationSerializable.FRICTIONLESS_AUTHENTICATION
    ThreeDSAuthentication.ACS_CHALLENGE -> ThreeDSAuthenticationSerializable.ACS_CHALLENGE
    ThreeDSAuthentication.AVS_VERIFIED -> ThreeDSAuthenticationSerializable.AVS_VERIFIED
    ThreeDSAuthentication.OTHER_ISSUER_METHOD -> ThreeDSAuthenticationSerializable.OTHER_ISSUER_METHOD
}

internal fun PriorThreeDSAuthentication.toData() = PriorThreeDSAuthenticationSerializable(
    data = data,
    method = method?.toData(),
    id = id,
    time = time
)

internal fun TravelDetails.toData() = TravelDetailsSerializable(
    isAirTravel = isAirTravel,
    airlineCarrier = airlineCarrier,
    departureDate = departureDate,
    destination = destination,
    origin = origin,
    passengerFirstName = passengerFirstName,
    passengerLastName = passengerLastName
)

internal fun UserAccountDetails.toData() = UserAccountDetailsSerializable(
    createdDate = createdDate,
    createdRange = createdRange?.toData(),
    changedDate = changedDate,
    changedRange = changedRange?.toData(),
    passwordChangedDate = passwordChangedDate,
    passwordChangedRange = passwordChangedRange?.toData(),
    totalPurchasesSixMonthCount = totalPurchasesSixMonthCount,
    transactionCountForPreviousDay = transactionCountForPreviousDay,
    transactionCountForPreviousYear = transactionCountForPreviousYear,
    suspiciousAccountActivity = suspiciousAccountActivity,
    shippingDetailsUsage = shippingDetailsUsage?.toData(),
    paymentAccountDetails = paymentAccountDetails?.toData(),
    userLogin = userLogin?.toData(),
    priorThreeDSAuthentication = priorThreeDSAuthentication?.toData(),
    travelDetails = travelDetails?.toData()
)

internal fun ThreeDS.toData(merchantRefNum: String) = ThreeDSSerializable(
    merchantRefNum = merchantRefNum,
    merchantUrl = merchantUrl,
    deviceChannel = "SDK",
    messageCategory = messageCategory.toData(),
    transactionIntent = transactionIntent.toData(),
    authenticationPurpose = authenticationPurpose.toData(),
    billingCycle = billingCycle?.toData(),
    requestorChallengePreference = requestorChallengePreference?.toData(),
    userLogin = userLogin?.toData(),
    orderItemDetails = orderItemDetails?.toData(),
    purchasedGiftCardDetails = purchasedGiftCardDetails?.toData(),
    userAccountDetails = userAccountDetails?.toData(),
    priorThreeDSAuthentication = priorThreeDSAuthentication?.toData(),
    shippingDetailsUsage = shippingDetailsUsage?.toData(),
    suspiciousAccountActivity = suspiciousAccountActivity,
    totalPurchasesSixMonthCount = totalPurchasesSixMonthCount,
    transactionCountForPreviousDay = transactionCountForPreviousDay,
    transactionCountForPreviousYear = transactionCountForPreviousYear,
    travelDetails = travelDetails?.toData(),
    maxAuthorizationsForInstalmentPayment = maxAuthorizationsForInstalmentPayment,
    electronicDelivery = electronicDelivery?.toData(),
    initialPurchaseTime = initialPurchaseTime,
    useThreeDSecureVersion2 = useThreeDSecureVersion2,
    threeDSProfile = threeDSProfile?.toData()
)

internal fun GoogleBillingAddress.toData() = BillingAddressRequest(
    name = name,
    postalCode = postalCode,
    countryCode = countryCode,
    phoneNumber = phoneNumber,
    address1 = address1,
    address2 = address2,
    address3 = address3,
    locality = locality,
    administrativeArea = administrativeArea,
    sortingCode = sortingCode
)

internal fun GoogleCardInfo.toData() = InfoRequest(
    billingAddress = billingAddress?.toData(),
    cardDetails = cardDetails,
    cardNetwork = cardNetwork
)

internal fun GoogleTokenizationData.toData() = TokenizationDataRequest(
    token = token,
    type = type
)

internal fun GooglePaymentMethodData.toData() = PaymentMethodDataRequest(
    description = description,
    info = cardInfo?.toData(),
    tokenizationData = tokenizationData?.toData(),
    type = type
)

internal fun GooglePayPaymentToken.toData() = GooglePayPaymentTokenRequest(
    apiVersion = apiVersion,
    apiVersionMinor = apiVersionMinor,
    paymentMethodData = googlePaymentMethodData?.toData()
)

internal fun VenmoRequest.toData() = VenmoRequestSerializable(
    consumerId = consumerId,
)

internal fun PaymentHandleRequest.toData(cardRequest: CardRequest? = null) =
    PaymentHandleRequestSerializable(
        merchantRefNum = merchantRefNum,
        transactionType = transactionType.toData(),
        card = cardRequest?.toData(),
        accountId = accountId,
        paymentType = paymentType?.toData(),
        amount = amount,
        currencyCode = currencyCode,
        returnLinks = returnLinks?.map { it.toData() },
        profile = profile?.toData(),
        threeDS = threeDS?.toData(merchantRefNum),
        billingDetails = billingDetails?.toData(),
        merchantDescriptor = merchantDescriptor?.toData(),
        shippingDetails = shippingDetails?.toData(),
        singleUseCustomerToken = singleUseCustomerToken,
        paymentHandleTokenFrom = paymentHandleTokenFrom,
        googlePay = googlePayPaymentToken?.let { GooglePayRequest(it.toData()) },
        venmo = venmoRequest?.toData()
    )
