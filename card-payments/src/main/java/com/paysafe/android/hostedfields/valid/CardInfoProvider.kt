package com.paysafe.android.hostedfields.valid

import com.paysafe.android.hostedfields.cardnumber.CardInfo
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import java.util.regex.Pattern

private val allCardsInfo: List<CardInfo> = listOf(
    CardInfo(
        type = PSCreditCardType.VISA,
        pattern = "^4\\d*$",
        maxLength = 16
    ),
    CardInfo(
        type = PSCreditCardType.MASTERCARD,
        pattern = "^(5[1-5]|222[1-8]|2229[0-9]|22[3-9]|2[3-6]|27[01]|2720[0-8]|27209)\\d*$",
        maxLength = 16
    ),
    CardInfo(
        type = PSCreditCardType.AMEX,
        pattern = "^3[47]\\d*$",
        maxLength = 15
    ),
    CardInfo(
        type = PSCreditCardType.DISCOVER,
        pattern = "^6(011(0[0-3]|0[5-9]|2|3|4|7[47]|8[6-9]|9)|4[4-9]|5[0-9])\\d*$",
        maxLength = 16
    )
)

internal fun String.getCardInfo(): CardInfo? = allCardsInfo.firstOrNull { cardInfo ->
    Pattern.compile(cardInfo.pattern).matcher(this).lookingAt()
}

internal fun PSCreditCardType.getCardInfo(): CardInfo? =
    allCardsInfo.firstOrNull { cardInfo -> cardInfo.type == this }

