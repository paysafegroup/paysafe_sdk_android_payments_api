/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.threeds

/**
 * These are the details of a previously made purchase or preorder.
 */
data class OrderItemDetails(
    /** Pre order item availability date. */
    val preOrderItemAvailabilityDate: String? = null,

    /** Pre order purchase indicator. */
    val preOrderPurchaseIndicator: String? = null,

    /** Reorder items indicator. */
    val reorderItemsIndicator: String? = null,

    /** Shipping indicator. */
    val shippingIndicator: String? = null
)