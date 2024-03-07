/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.data.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CardTypeConfigSerializable(
    @SerialName("AM")
    val am: CardTypeCategorySerializable? = null,

    @SerialName("MC")
    val mc: CardTypeCategorySerializable? = null,

    @SerialName("VI")
    val vi: CardTypeCategorySerializable? = null,

    @SerialName("DI")
    val di: CardTypeCategorySerializable? = null,

    @SerialName("JC")
    val jc: CardTypeCategorySerializable? = null,

    @SerialName("MD")
    val md: CardTypeCategorySerializable? = null,

    @SerialName("SO")
    val so: CardTypeCategorySerializable? = null,

    @SerialName("VD")
    val vd: CardTypeCategorySerializable? = null,

    @SerialName("VE")
    val ve: CardTypeCategorySerializable? = null,
)